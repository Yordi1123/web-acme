package com.uns.controller;

import com.uns.dao.ProveedorDAO;
import com.uns.entities.Proveedor;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProveedorBean implements Serializable {

    private Proveedor proveedor;
    private List<Proveedor> proveedores;
    private ProveedorDAO proveedorDAO;

    @PostConstruct
    public void init() {
        proveedorDAO = new ProveedorDAO();
        nuevo();
        listar();
    }

    public void nuevo() {
        proveedor = new Proveedor();
        proveedor.setEstado("Activo");
    }

    public void listar() {
        proveedores = proveedorDAO.findAll();
    }

    public void guardar() {
        try {
            if (proveedor.getId() == null) {
                proveedorDAO.create(proveedor);
                jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                        "Éxito", "Proveedor creado correctamente"));
            } else {
                proveedorDAO.update(proveedor);
                jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                        "Éxito", "Proveedor actualizado correctamente"));
            }
            nuevo();
            listar();
        } catch (Exception e) {
            e.printStackTrace();
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "No se pudo guardar: " + e.getMessage()));
        }
    }
    
    public void seleccionar(Proveedor pv) {
        this.proveedor = pv;
    }
    
    public void eliminar(Proveedor pv) {
        try {
            proveedorDAO.delete(pv);
            listar();
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                    "Eliminado", "Proveedor eliminado correctamente"));
        } catch (Exception e) {
            e.printStackTrace();
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "No se pudo eliminar: " + e.getMessage()));
        }
    }

    // Getters y Setters
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public List<Proveedor> getProveedores() { return proveedores; }
    public void setProveedores(List<Proveedor> proveedores) { this.proveedores = proveedores; }
}
