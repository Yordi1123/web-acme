package com.uns.controller;

import com.uns.dao.OrdenCompraDAO;
import com.uns.entities.DetalleOrden;
import com.uns.entities.DetalleRequerimiento;
import com.uns.entities.OrdenCompra;
import com.uns.enums.EstadoOrden;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Named
@ViewScoped
public class ComprasBean implements Serializable {

    private OrdenCompra ordenCompra;
    private List<OrdenCompra> listaOrdenes;
    private OrdenCompraDAO ordenCompraDAO;

    @Inject
    private LoginBean loginBean;

    private DetalleOrden detalle;
    private List<DetalleOrden> detalles;
    
    // Lista de Requerimientos Aprobados (para seleccionar manual)
    private List<com.uns.entities.Requerimiento> listaRequerimientosAprobados;
    private com.uns.entities.Requerimiento requerimientoSeleccionado;
    
    // Flag para indicar si llegamos desde el pool (multi-select)
    private boolean modoMultiRequerimiento = false;

    // DAOs
    private com.uns.dao.RequerimientoDAO requerimientoDAO;
    private com.uns.dao.DetalleRequerimientoDAO detalleRequerimientoDAO;
    
    @PostConstruct
    public void init() {
        ordenCompraDAO = new OrdenCompraDAO();
        requerimientoDAO = new com.uns.dao.RequerimientoDAO();
        detalleRequerimientoDAO = new com.uns.dao.DetalleRequerimientoDAO();
        
        cargarRequerimientosAprobados();
        listaOrdenes = ordenCompraDAO.findAll();
        nuevaOrden();
        
        // Verificar si hay items desde el pool de compras (flash scope)
        cargarItemsDesdeFlash();
    }
    
    /**
     * Carga items desde el flash scope (enviados desde pool_compras).
     * Esto permite agrupar items de múltiples requerimientos en una sola OC.
     */
    @SuppressWarnings("unchecked")
    private void cargarItemsDesdeFlash() {
        Object itemsFlash = FacesContext.getCurrentInstance()
            .getExternalContext().getFlash().get("itemsParaOrden");
        
        if (itemsFlash != null && itemsFlash instanceof List) {
            List<DetalleRequerimiento> itemsSeleccionados = (List<DetalleRequerimiento>) itemsFlash;
            
            if (!itemsSeleccionados.isEmpty()) {
                modoMultiRequerimiento = true;
                detalles = new ArrayList<>();
                
                for (DetalleRequerimiento reqItem : itemsSeleccionados) {
                    DetalleOrden det = new DetalleOrden();
                    det.setDetalleRequerimiento(reqItem);
                    det.setOrdenCompra(ordenCompra);
                    // Solo cargar cantidad pendiente, no la solicitada
                    det.setCantidad(reqItem.getCantidadPendiente());
                    det.setPrecioUnitario(BigDecimal.ZERO);
                    detalles.add(det);
                }
            }
        }
    }
    
    public void cargarRequerimientosAprobados() {
        List<com.uns.entities.Requerimiento> todos = requerimientoDAO.findAll();
        listaRequerimientosAprobados = new ArrayList<>();
        for (com.uns.entities.Requerimiento r : todos) {
            if (r.getEstado() == com.uns.enums.EstadoRequerimiento.APROBADO 
                || r.getEstado() == com.uns.enums.EstadoRequerimiento.EN_ATENCION) {
                listaRequerimientosAprobados.add(r);
            }
        }
    }
    
    /**
     * Lista de lugares de entrega predefinidos.
     */
    public List<String> getLugaresEntregaPredefinidos() {
        List<String> lugares = new ArrayList<>();
        lugares.add("Almacén Central - Av. Industrial 123, Lima");
        lugares.add("Almacén Norte - Panamericana Norte Km 25");
        lugares.add("Almacén Sur - Av. Separadora Industrial 456");
        lugares.add("En Obra (especificar en observaciones)");
        lugares.add("Otro (especificar en observaciones)");
        return lugares;
    }

    public void nuevaOrden() {
        ordenCompra = new OrdenCompra();
        ordenCompra.setFechaEmision(LocalDate.now());
        ordenCompra.setEstado(EstadoOrden.BORRADOR);
        ordenCompra.setMoneda("Soles");
        // numeroOrden se asigna al generar (no en borrador)
        
        if (loginBean != null && loginBean.isLoggedIn()) {
            ordenCompra.setUsuarioCompras(loginBean.getUsuarioLogueado());
        }
         
        detalles = new ArrayList<>();
        requerimientoSeleccionado = null;
        modoMultiRequerimiento = false;
    }
    
    public void cargarDetallesDeRequerimiento() {
        if (requerimientoSeleccionado != null) {
            List<DetalleRequerimiento> itemsReq = 
                detalleRequerimientoDAO.findByRequerimientoConMaterial(requerimientoSeleccionado.getId());
            detalles = new ArrayList<>();
            
            for (DetalleRequerimiento reqItem : itemsReq) {
                // Solo agregar items con cantidad pendiente > 0
                if (reqItem.getCantidadPendiente() != null && 
                    reqItem.getCantidadPendiente().compareTo(BigDecimal.ZERO) > 0) {
                    DetalleOrden det = new DetalleOrden();
                    det.setDetalleRequerimiento(reqItem);
                    det.setOrdenCompra(ordenCompra);
                    det.setCantidad(reqItem.getCantidadPendiente());
                    det.setPrecioUnitario(BigDecimal.ZERO);
                    detalles.add(det);
                }
            }
            
            modoMultiRequerimiento = false;
        }
    }
    
    /**
     * Calcula totales dinámicamente para mostrar en la vista.
     */
    public BigDecimal getSubTotalCalculado() {
        BigDecimal subTotal = BigDecimal.ZERO;
        if (detalles != null) {
            for (DetalleOrden det : detalles) {
                if (det.getCantidad() != null && det.getPrecioUnitario() != null) {
                    subTotal = subTotal.add(det.getCantidad().multiply(det.getPrecioUnitario()));
                }
            }
        }
        return subTotal;
    }
    
    public BigDecimal getIgvCalculado() {
        return getSubTotalCalculado().multiply(new BigDecimal("0.18"));
    }
    
    public BigDecimal getTotalCalculado() {
        return getSubTotalCalculado().add(getIgvCalculado());
    }
    
    /**
     * Elimina un item del detalle de la orden.
     */
    public void eliminarItem(DetalleOrden item) {
        if (detalles != null) {
            detalles.remove(item);
        }
    }
    
    /**
     * Guarda la orden como BORRADOR sin actualizar cantidades atendidas.
     * Permite guardar y editar después.
     */
    public void guardarBorrador() {
        if (detalles == null || detalles.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "Debe agregar al menos un item"));
            return;
        }
        
        if (ordenCompra.getProveedor() == null || ordenCompra.getProveedor().getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "Debe seleccionar un proveedor"));
            return;
        }
        
        try {
            jakarta.persistence.EntityManager em = com.uns.config.JPAFactory.getEntityManager();
            try {
                em.getTransaction().begin();

                // Calcular totales
                BigDecimal subTotal = getSubTotalCalculado();
                BigDecimal igv = getIgvCalculado();
                BigDecimal total = getTotalCalculado();
                
                ordenCompra.setSubTotal(subTotal);
                ordenCompra.setIgv(igv);
                ordenCompra.setTotal(total);
                ordenCompra.setEstado(EstadoOrden.BORRADOR);

                if (ordenCompra.getId() == null) {
                    em.persist(ordenCompra);
                } else {
                    em.merge(ordenCompra);
                }
                
                // Guardar detalles SIN actualizar cantidadAtendida (es borrador)
                for (DetalleOrden det : detalles) {
                    det.setOrdenCompra(ordenCompra);
                    if (det.getId() == null) {
                        em.persist(det);
                    } else {
                        em.merge(det);
                    }
                }
                
                em.getTransaction().commit();
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                        "Guardado", "Orden guardada como borrador. Puede editarla después."));
                
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo guardar: " + e.getMessage()));
            } finally {
                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera la orden de compra con estado GENERADA.
     * ACTUALIZA las cantidadAtendida de los requerimientos originales.
     */
    public void generarOrden() {
        if (detalles == null || detalles.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "No hay items en la orden"));
            return;
        }
        
        if (ordenCompra.getProveedor() == null || ordenCompra.getProveedor().getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "Debe seleccionar un proveedor"));
            return;
        }
        
        // Validar que todos los items tengan precio > 0
        for (DetalleOrden det : detalles) {
            if (det.getPrecioUnitario() == null || det.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                        "Error", "Todos los items deben tener un precio unitario mayor a 0"));
                return;
            }
        }
        
        try {
            jakarta.persistence.EntityManager em = com.uns.config.JPAFactory.getEntityManager();
            try {
                em.getTransaction().begin();

                // Calcular totales
                BigDecimal subTotal = getSubTotalCalculado();
                BigDecimal igv = getIgvCalculado();
                BigDecimal total = getTotalCalculado();
                
                ordenCompra.setSubTotal(subTotal);
                ordenCompra.setIgv(igv);
                ordenCompra.setTotal(total);
                ordenCompra.setEstado(EstadoOrden.GENERADA);
                
                // Generar número de orden automáticamente
                if (ordenCompra.getNumeroOrden() == null || ordenCompra.getNumeroOrden().isEmpty()) {
                    ordenCompra.setNumeroOrden(ordenCompraDAO.getNextNumeroOrden());
                }

                if (ordenCompra.getId() == null) {
                    em.persist(ordenCompra);
                } else {
                    em.merge(ordenCompra);
                }
                
                // Guardar detalles y ACTUALIZAR cantidadAtendida en requerimientos originales
                Set<Long> requerimientosAfectados = new HashSet<>();
                
                for (DetalleOrden det : detalles) {
                    det.setOrdenCompra(ordenCompra);
                    if (det.getId() == null) {
                        em.persist(det);
                    } else {
                        em.merge(det);
                    }
                    
                    // ALGORITMO CRÍTICO: Actualizar cantidadAtendida
                    DetalleRequerimiento reqItem = det.getDetalleRequerimiento();
                    if (reqItem != null) {
                        // Recargar para asegurar datos frescos
                        reqItem = em.find(DetalleRequerimiento.class, reqItem.getId());
                        
                        BigDecimal atendidoActual = reqItem.getCantidadAtendida();
                        if (atendidoActual == null) atendidoActual = BigDecimal.ZERO;
                        
                        BigDecimal nuevoAtendido = atendidoActual.add(det.getCantidad());
                        reqItem.setCantidadAtendida(nuevoAtendido);
                        em.merge(reqItem);
                        
                        // Guardar ID del requerimiento para verificar completitud después
                        requerimientosAfectados.add(reqItem.getRequerimiento().getId());
                    }
                }
                
                // VERIFICAR COMPLETITUD de cada requerimiento afectado
                for (Long reqId : requerimientosAfectados) {
                    verificarYActualizarEstadoRequerimiento(em, reqId);
                }
                
                em.getTransaction().commit();
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                        "Éxito", "Orden de Compra #" + ordenCompra.getId() + " generada correctamente"));
                
                listaOrdenes = ordenCompraDAO.findAll();
                cargarRequerimientosAprobados();
                nuevaOrden();
                
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo generar la orden: " + e.getMessage()));
            } finally {
                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si todos los items de un requerimiento han sido atendidos.
     * Si es así, cambia el estado a ATENDIDO_TOTAL.
     * Si al menos uno fue atendido parcialmente, cambia a EN_ATENCION.
     */
    private void verificarYActualizarEstadoRequerimiento(jakarta.persistence.EntityManager em, Long reqId) {
        com.uns.entities.Requerimiento req = em.find(com.uns.entities.Requerimiento.class, reqId);
        if (req == null) return;
        
        // Cargar detalles frescos
        List<DetalleRequerimiento> items = 
            em.createQuery("SELECT d FROM DetalleRequerimiento d WHERE d.requerimiento.id = :id", 
                DetalleRequerimiento.class)
            .setParameter("id", reqId)
            .getResultList();
        
        boolean todosCompletos = true;
        boolean algunoAtendido = false;
        
        for (DetalleRequerimiento item : items) {
            BigDecimal atendida = item.getCantidadAtendida();
            if (atendida == null) atendida = BigDecimal.ZERO;
            
            if (atendida.compareTo(item.getCantidadSolicitada()) >= 0) {
                algunoAtendido = true;
            } else if (atendida.compareTo(BigDecimal.ZERO) > 0) {
                algunoAtendido = true;
                todosCompletos = false;
            } else {
                todosCompletos = false;
            }
        }
        
        // Actualizar estado según resultado
        if (todosCompletos && algunoAtendido) {
            req.setEstado(com.uns.enums.EstadoRequerimiento.ATENDIDO_TOTAL);
        } else if (algunoAtendido) {
            req.setEstado(com.uns.enums.EstadoRequerimiento.EN_ATENCION);
        }
        // Si no hay ninguno atendido, mantener APROBADO
        
        em.merge(req);
    }
    
    // ========== FLUJO DE APROBACIÓN DE OC ==========
    
    /**
     * Aprueba una orden de compra (Visto Bueno del Jefe).
     * Cambia estado de GENERADA a APROBADA.
     */
    public void aprobarOrden(OrdenCompra orden) {
        if (orden == null || orden.getEstado() != EstadoOrden.GENERADA) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_WARN,
                    "No aplicable", "Solo se pueden aprobar órdenes en estado Generada"));
            return;
        }
        
        orden.setEstado(EstadoOrden.APROBADA);
        ordenCompraDAO.update(orden);
        listaOrdenes = ordenCompraDAO.findAll();
        
        FacesContext.getCurrentInstance().addMessage(null,
            new jakarta.faces.application.FacesMessage(
                jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                "Aprobada", "Orden " + orden.getNumeroOrden() + " aprobada correctamente"));
    }
    
    /**
     * Envía la orden de compra al proveedor.
     * Cambia estado de APROBADA a ENVIADA.
     */
    public void enviarOrden(OrdenCompra orden) {
        if (orden == null || orden.getEstado() != EstadoOrden.APROBADA) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_WARN,
                    "No aplicable", "Solo se pueden enviar órdenes aprobadas"));
            return;
        }
        
        orden.setEstado(EstadoOrden.ENVIADA);
        ordenCompraDAO.update(orden);
        listaOrdenes = ordenCompraDAO.findAll();
        
        FacesContext.getCurrentInstance().addMessage(null,
            new jakarta.faces.application.FacesMessage(
                jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                "Enviada", "Orden " + orden.getNumeroOrden() + " enviada al proveedor"));
    }
    
    // Variable para justificación de anulación
    private String motivoAnulacion;
    private OrdenCompra ordenParaAnular;
    
    public void prepararAnulacion(OrdenCompra orden) {
        this.ordenParaAnular = orden;
        this.motivoAnulacion = "";
    }
    
    /**
     * Anula una orden de compra con justificación.
     * Requiere motivo obligatorio.
     */
    public void anularOrden() {
        if (ordenParaAnular == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "No hay orden seleccionada para anular"));
            return;
        }
        
        if (motivoAnulacion == null || motivoAnulacion.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "Debe indicar el motivo de anulación"));
            return;
        }
        
        // Solo se pueden anular órdenes que no estén ya anuladas
        if (ordenParaAnular.getEstado() == EstadoOrden.ANULADA) {
            FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_WARN,
                    "No aplicable", "Esta orden ya está anulada"));
            return;
        }
        
        // Guardar observaciones con motivo de anulación
        String obsActual = ordenParaAnular.getObservaciones() != null ? ordenParaAnular.getObservaciones() + "\n" : "";
        ordenParaAnular.setObservaciones(obsActual + "[ANULADA " + LocalDate.now() + " por " + 
            (loginBean != null ? loginBean.getUsuarioLogueado().getNombreCompleto() : "Sistema") + "]: " + motivoAnulacion);
        
        ordenParaAnular.setEstado(EstadoOrden.ANULADA);
        ordenCompraDAO.update(ordenParaAnular);
        listaOrdenes = ordenCompraDAO.findAll();
        
        FacesContext.getCurrentInstance().addMessage(null,
            new jakarta.faces.application.FacesMessage(
                jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                "Anulada", "Orden " + ordenParaAnular.getNumeroOrden() + " anulada correctamente"));
        
        ordenParaAnular = null;
        motivoAnulacion = "";
    }
    
    /**
     * Verifica si una orden puede ser editada.
     * Solo BORRADOR es editable.
     */
    public boolean puedeEditar(OrdenCompra orden) {
        return orden != null && orden.getEstado() == EstadoOrden.BORRADOR;
    }
    
    /**
     * Verifica si una orden puede ser aprobada.
     */
    public boolean puedeAprobar(OrdenCompra orden) {
        return orden != null && orden.getEstado() == EstadoOrden.GENERADA;
    }
    
    /**
     * Verifica si una orden puede ser enviada.
     */
    public boolean puedeEnviar(OrdenCompra orden) {
        return orden != null && orden.getEstado() == EstadoOrden.APROBADA;
    }
    
    /**
     * Verifica si una orden puede ser anulada.
     */
    public boolean puedeAnular(OrdenCompra orden) {
        return orden != null && orden.getEstado() != EstadoOrden.ANULADA;
    }
    
    // Getters y Setters adicionales para anulación
    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }
    
    public OrdenCompra getOrdenParaAnular() { return ordenParaAnular; }
    public void setOrdenParaAnular(OrdenCompra ordenParaAnular) { this.ordenParaAnular = ordenParaAnular; }
    
    // Getters y Setters
    public OrdenCompra getOrdenCompra() { return ordenCompra; }
    public void setOrdenCompra(OrdenCompra ordenCompra) { this.ordenCompra = ordenCompra; }

    public List<OrdenCompra> getListaOrdenes() { return listaOrdenes; }
    public void setListaOrdenes(List<OrdenCompra> listaOrdenes) { this.listaOrdenes = listaOrdenes; }
    
    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
    
    public com.uns.entities.Requerimiento getRequerimientoSeleccionado() { return requerimientoSeleccionado; }
    public void setRequerimientoSeleccionado(com.uns.entities.Requerimiento requerimientoSeleccionado) { this.requerimientoSeleccionado = requerimientoSeleccionado; }
    
    public List<com.uns.entities.Requerimiento> getListaRequerimientosAprobados() { return listaRequerimientosAprobados; }
    
    public boolean isModoMultiRequerimiento() { return modoMultiRequerimiento; }
    
    // Proveedores para el combo de orden
    public List<com.uns.entities.Proveedor> getListaProveedores() {
        com.uns.dao.ProveedorDAO pDao = new com.uns.dao.ProveedorDAO();
        return pDao.findAll();
    }
}
