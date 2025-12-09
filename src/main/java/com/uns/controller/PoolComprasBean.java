package com.uns.controller;

import com.uns.dao.DetalleRequerimientoDAO;
import com.uns.entities.DetalleRequerimiento;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class PoolComprasBean implements Serializable {

    private DetalleRequerimientoDAO detalleDAO;
    private List<DetalleRequerimiento> itemsPendientes;
    private List<DetalleRequerimiento> itemsSeleccionados;

    @PostConstruct
    public void init() {
        detalleDAO = new DetalleRequerimientoDAO();
        itemsSeleccionados = new ArrayList<>();
        cargarItemsPendientes();
    }

    public void cargarItemsPendientes() {
        itemsPendientes = detalleDAO.findPendingItems();
    }

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

    // --- Getters y Setters ---
    public List<DetalleRequerimiento> getItemsPendientes() { return itemsPendientes; }
    public void setItemsPendientes(List<DetalleRequerimiento> i) { this.itemsPendientes = i; }

    public List<DetalleRequerimiento> getItemsSeleccionados() { return itemsSeleccionados; }
    public void setItemsSeleccionados(List<DetalleRequerimiento> i) { this.itemsSeleccionados = i; }
}
