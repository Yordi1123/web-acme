package com.uns.controller;

import com.uns.dao.OrdenCompraDAO;
import com.uns.entities.DetalleOrden;
import com.uns.entities.OrdenCompra;
import com.uns.enums.EstadoOrden;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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
    
    // Lista de Requerimientos Aprobados (para seleccionar)
    private List<com.uns.entities.Requerimiento> listaRequerimientosAprobados;
    private com.uns.entities.Requerimiento requerimientoSeleccionado;

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
    }
    
    public void cargarRequerimientosAprobados() {
        // Filtramos en memoria o hacemos consulta especifica. Usaremos memoria por ahora.
        List<com.uns.entities.Requerimiento> todos = requerimientoDAO.findAll();
        listaRequerimientosAprobados = new java.util.ArrayList<>();
        for (com.uns.entities.Requerimiento r : todos) {
            if (r.getEstado() == com.uns.enums.EstadoRequerimiento.APROBADO) {
                listaRequerimientosAprobados.add(r);
            }
        }
    }

    public void nuevaOrden() {
        ordenCompra = new OrdenCompra();
        ordenCompra.setFechaEmision(LocalDate.now());
        ordenCompra.setEstado(EstadoOrden.GENERADA);
        ordenCompra.setMoneda("Soles");
        
         if (loginBean != null && loginBean.isLoggedIn()) {
            ordenCompra.setUsuarioCompras(loginBean.getUsuarioLogueado());
        }
         
        detalles = new java.util.ArrayList<>();
        requerimientoSeleccionado = null;
    }
    
    public void cargarDetallesDeRequerimiento() {
        if (requerimientoSeleccionado != null) {
            List<com.uns.entities.DetalleRequerimiento> itemsReq = detalleRequerimientoDAO.findByRequerimiento(requerimientoSeleccionado.getId());
            detalles = new java.util.ArrayList<>();
            
            for (com.uns.entities.DetalleRequerimiento reqItem : itemsReq) {
                DetalleOrden det = new DetalleOrden();
                det.setDetalleRequerimiento(reqItem); // Vinculo obligatorio
                det.setOrdenCompra(ordenCompra); // Vinculo temporal
                det.setCantidad(reqItem.getCantidadSolicitada());
                det.setPrecioUnitario(java.math.BigDecimal.ZERO); // A completar por el usuario
                detalles.add(det);
            }
            
            // Copiar datos del proveedor si existiera en req (no existe, se escoge aqui)
            // Copiar datos del proyecto al comentario u otro lado si se desea
            ordenCompra.setProveedor(new com.uns.entities.Proveedor()); // Inicializar para combo
        }
    }

    public void generarOrden() {
        if (detalles == null || detalles.isEmpty()) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "No hay items en la orden"));
            return;
        }
        
        try {
            jakarta.persistence.EntityManager em = com.uns.config.JPAFactory.getEntityManager();
            try {
                em.getTransaction().begin();

                // Calcular totales
                java.math.BigDecimal subTotal = java.math.BigDecimal.ZERO;
                for (DetalleOrden det : detalles) {
                    java.math.BigDecimal importe = det.getCantidad().multiply(det.getPrecioUnitario());
                    subTotal = subTotal.add(importe);
                }
                java.math.BigDecimal igvRate = new java.math.BigDecimal("0.18");
                java.math.BigDecimal igv = subTotal.multiply(igvRate);
                java.math.BigDecimal total = subTotal.add(igv);
                
                ordenCompra.setSubTotal(subTotal);
                ordenCompra.setIgv(igv);
                ordenCompra.setTotal(total);

                if (ordenCompra.getId() == null) {
                    em.persist(ordenCompra);
                } else {
                    em.merge(ordenCompra);
                }
                
                // Guardar detalles y ACTUALIZAR cantidadAtendida en requerimientos originales
                java.util.Set<Long> requerimientosAfectados = new java.util.HashSet<>();
                
                for (DetalleOrden det : detalles) {
                    det.setOrdenCompra(ordenCompra);
                    em.persist(det);
                    
                    // ALGORITMO CRÍTICO: Actualizar cantidadAtendida
                    com.uns.entities.DetalleRequerimiento reqItem = det.getDetalleRequerimiento();
                    if (reqItem != null) {
                        // Recargar para asegurar datos frescos
                        reqItem = em.find(com.uns.entities.DetalleRequerimiento.class, reqItem.getId());
                        
                        java.math.BigDecimal atendidoActual = reqItem.getCantidadAtendida();
                        if (atendidoActual == null) atendidoActual = java.math.BigDecimal.ZERO;
                        
                        java.math.BigDecimal nuevoAtendido = atendidoActual.add(det.getCantidad());
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
                
                jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                        "Éxito", "Orden de Compra generada correctamente"));
                
                listaOrdenes = ordenCompraDAO.findAll();
                cargarRequerimientosAprobados();
                nuevaOrden();
                
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
                jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
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
        java.util.List<com.uns.entities.DetalleRequerimiento> items = 
            em.createQuery("SELECT d FROM DetalleRequerimiento d WHERE d.requerimiento.id = :id", 
                com.uns.entities.DetalleRequerimiento.class)
            .setParameter("id", reqId)
            .getResultList();
        
        boolean todosCompletos = true;
        boolean algunoAtendido = false;
        
        for (com.uns.entities.DetalleRequerimiento item : items) {
            java.math.BigDecimal atendida = item.getCantidadAtendida();
            if (atendida == null) atendida = java.math.BigDecimal.ZERO;
            
            if (atendida.compareTo(item.getCantidadSolicitada()) >= 0) {
                algunoAtendido = true;
            } else if (atendida.compareTo(java.math.BigDecimal.ZERO) > 0) {
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
    
    // Proveedores para el combo de orden
    public List<com.uns.entities.Proveedor> getListaProveedores() {
        com.uns.dao.ProveedorDAO pDao = new com.uns.dao.ProveedorDAO();
        return pDao.findAll();
    }
}
