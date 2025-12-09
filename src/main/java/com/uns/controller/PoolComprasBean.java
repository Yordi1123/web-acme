package com.uns.controller;

import com.uns.dao.DetalleRequerimientoDAO;
import com.uns.dao.RequerimientoDAO;
import com.uns.entities.DetalleRequerimiento;
import com.uns.entities.Requerimiento;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class PoolComprasBean implements Serializable {

    private RequerimientoDAO requerimientoDAO;
    private DetalleRequerimientoDAO detalleDAO;
    
    // Vista por Requerimientos
    private List<Requerimiento> requerimientosAprobados;
    private Requerimiento requerimientoSeleccionado;
    
    // Vista por Items (pool tradicional)
    private List<DetalleRequerimiento> itemsPendientes;
    private List<DetalleRequerimiento> itemsSeleccionados;
    
    // Modo de vista activo
    private String vistaActiva = "requerimientos"; // "requerimientos" o "items"

    @PostConstruct
    public void init() {
        requerimientoDAO = new RequerimientoDAO();
        detalleDAO = new DetalleRequerimientoDAO();
        itemsSeleccionados = new ArrayList<>();
        cargarRequerimientosAprobados();
        cargarItemsPendientes();
    }

    /**
     * Carga todos los requerimientos aprobados para la bandeja de entrada.
     */
    public void cargarRequerimientosAprobados() {
        requerimientosAprobados = requerimientoDAO.findAprobadosParaCompras();
    }
    
    /**
     * Carga items pendientes (detalles con cantidad pendiente > 0).
     */
    public void cargarItemsPendientes() {
        itemsPendientes = detalleDAO.findPendingItems();
    }
    
    /**
     * Selecciona un requerimiento para ver su detalle.
     */
    public void seleccionarRequerimiento(Requerimiento req) {
        this.requerimientoSeleccionado = req;
    }
    
    /**
     * Cambiar a vista de requerimientos.
     */
    public void mostrarVistaRequerimientos() {
        this.vistaActiva = "requerimientos";
    }
    
    /**
     * Cambiar a vista de items.
     */
    public void mostrarVistaItems() {
        this.vistaActiva = "items";
    }

    /**
     * Genera una orden de compra con los items seleccionados.
     */
    public void generarOrden() {
        if (itemsSeleccionados == null || itemsSeleccionados.isEmpty()) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_WARN,
                    "Sin selección", "Debe seleccionar al menos un ítem para generar la orden"));
            return;
        }
        // Redirigir a generar_orden con los items seleccionados (usando flash scope)
        jakarta.faces.context.FacesContext.getCurrentInstance().getExternalContext()
            .getFlash().put("itemsParaOrden", itemsSeleccionados);
        try {
            jakarta.faces.context.FacesContext.getCurrentInstance().getExternalContext()
                .redirect("generar_orden.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Genera orden desde un requerimiento específico (todos sus items pendientes).
     */
    public void generarOrdenDesdeRequerimiento(Requerimiento req) {
        List<DetalleRequerimiento> itemsDelReq = new ArrayList<>();
        for (DetalleRequerimiento det : req.getDetalles()) {
            if (det.getCantidadPendiente() != null && det.getCantidadPendiente().doubleValue() > 0) {
                itemsDelReq.add(det);
            }
        }
        
        if (itemsDelReq.isEmpty()) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                    "Sin pendientes", "Este requerimiento no tiene items pendientes de compra"));
            return;
        }
        
        jakarta.faces.context.FacesContext.getCurrentInstance().getExternalContext()
            .getFlash().put("itemsParaOrden", itemsDelReq);
        try {
            jakarta.faces.context.FacesContext.getCurrentInstance().getExternalContext()
                .redirect("generar_orden.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Cuenta items pendientes de un requerimiento.
     */
    public int contarItemsPendientes(Requerimiento req) {
        int count = 0;
        if (req.getDetalles() != null) {
            for (DetalleRequerimiento det : req.getDetalles()) {
                if (det.getCantidadPendiente() != null && det.getCantidadPendiente().doubleValue() > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    // --- Getters y Setters ---
    public List<Requerimiento> getRequerimientosAprobados() { return requerimientosAprobados; }
    public void setRequerimientosAprobados(List<Requerimiento> r) { this.requerimientosAprobados = r; }
    
    public Requerimiento getRequerimientoSeleccionado() { return requerimientoSeleccionado; }
    public void setRequerimientoSeleccionado(Requerimiento r) { this.requerimientoSeleccionado = r; }
    
    public List<DetalleRequerimiento> getItemsPendientes() { return itemsPendientes; }
    public void setItemsPendientes(List<DetalleRequerimiento> i) { this.itemsPendientes = i; }

    public List<DetalleRequerimiento> getItemsSeleccionados() { return itemsSeleccionados; }
    public void setItemsSeleccionados(List<DetalleRequerimiento> i) { this.itemsSeleccionados = i; }
    
    public String getVistaActiva() { return vistaActiva; }
    public void setVistaActiva(String v) { this.vistaActiva = v; }
}
