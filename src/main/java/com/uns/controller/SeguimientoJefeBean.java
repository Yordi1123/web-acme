package com.uns.controller;

import com.uns.dao.DetalleRequerimientoDAO;
import com.uns.dao.RequerimientoDAO;
import com.uns.entities.DetalleRequerimiento;
import com.uns.entities.Requerimiento;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class SeguimientoJefeBean implements Serializable {

    @Inject
    private LoginBean loginBean;

    private RequerimientoDAO requerimientoDAO;
    private DetalleRequerimientoDAO detalleRequerimientoDAO;
    
    private List<Requerimiento> requerimientos;
    private Requerimiento requerimientoSeleccionado;
    private List<DetalleRequerimiento> detallesSeleccionados;

    @PostConstruct
    public void init() {
        requerimientoDAO = new RequerimientoDAO();
        detalleRequerimientoDAO = new DetalleRequerimientoDAO();
        cargarRequerimientos();
    }

    public void cargarRequerimientos() {
        if (loginBean != null && loginBean.isLoggedIn()) {
            requerimientos = requerimientoDAO.findAprobadosByJefe(
                loginBean.getUsuarioLogueado().getId());
        } else {
            requerimientos = new java.util.ArrayList<>();
        }
    }

    public void seleccionar(Requerimiento req) {
        this.requerimientoSeleccionado = req;
        this.detallesSeleccionados = detalleRequerimientoDAO.findByRequerimientoConMaterial(req.getId());
    }

    // --- Getters y Setters ---
    public List<Requerimiento> getRequerimientos() { return requerimientos; }
    public void setRequerimientos(List<Requerimiento> r) { this.requerimientos = r; }

    public Requerimiento getRequerimientoSeleccionado() { return requerimientoSeleccionado; }
    public void setRequerimientoSeleccionado(Requerimiento r) { this.requerimientoSeleccionado = r; }

    public List<DetalleRequerimiento> getDetallesSeleccionados() { return detallesSeleccionados; }
}
