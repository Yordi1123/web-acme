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
        try {
            jakarta.persistence.EntityManager em = com.uns.config.JPAFactory.getEntityManager();
            try {
                em.getTransaction().begin();

                if (ordenCompra.getId() == null) {
                    em.persist(ordenCompra);
                } else {
                    em.merge(ordenCompra);
                }
                
                // Detalles
                for (DetalleOrden det : detalles) {
                    det.setOrdenCompra(ordenCompra);
                    // Recalcular importe si no es automatico (pero es generated column segun entity, asi que no setImporte)
                    // Si el entity tiene 'insertable=false' JPA lo ignora.
                    em.persist(det);
                }
                
                // Actualizar estado requerimiento? (Opcional: ATENDIDO_TOTAL)
                if (requerimientoSeleccionado != null) {
                     requerimientoSeleccionado.setEstado(com.uns.enums.EstadoRequerimiento.ATENDIDO_TOTAL);
                     // EstadoRequerimiento: PENDIENTE, OBSERVADO, APROBADO, EN_ATENCION, ATENDIDO_TOTAL, RECHAZADO
                     // Check spelling in Enum definition
                     em.merge(requerimientoSeleccionado);
                }
                
                em.getTransaction().commit();
                
                listaOrdenes = ordenCompraDAO.findAll();
                cargarRequerimientosAprobados(); // Refresh list
                nuevaOrden();
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
