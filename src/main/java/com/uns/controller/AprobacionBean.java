package com.uns.controller;

import com.uns.dao.DetalleRequerimientoDAO;
import com.uns.dao.RequerimientoDAO;
import com.uns.entities.DetalleRequerimiento;
import com.uns.entities.Requerimiento;
import com.uns.enums.EstadoRequerimiento;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AprobacionBean implements Serializable {

    @Inject
    private LoginBean loginBean;

    private RequerimientoDAO requerimientoDAO;
    private DetalleRequerimientoDAO detalleRequerimientoDAO;
    
    private List<Requerimiento> requerimientosPendientes;
    private Requerimiento requerimientoSeleccionado;
    private List<DetalleRequerimiento> detallesSeleccionados;
    private String observacionAprobador;

    @PostConstruct
    public void init() {
        requerimientoDAO = new RequerimientoDAO();
        detalleRequerimientoDAO = new DetalleRequerimientoDAO();
        cargarPendientes();
    }

    public void cargarPendientes() {
        if (loginBean != null && loginBean.isLoggedIn()) {
            // Filtrar por jefe logueado
            requerimientosPendientes = requerimientoDAO.findPendientesByJefe(
                loginBean.getUsuarioLogueado().getId());
        } else {
            requerimientosPendientes = new java.util.ArrayList<>();
        }
    }

    public void seleccionar(Requerimiento req) {
        this.requerimientoSeleccionado = req;
        this.observacionAprobador = null;
        // Cargar detalles frescos
        this.detallesSeleccionados = detalleRequerimientoDAO.findByRequerimientoConMaterial(req.getId());
    }

    public void aprobar(Requerimiento req) {
        req.setEstado(EstadoRequerimiento.APROBADO);
        requerimientoDAO.update(req);
        cargarPendientes();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Aprobado", 
                "Requerimiento #" + req.getId() + " aprobado exitosamente"));
    }

    public void observar(Requerimiento req) {
        if (observacionAprobador == null || observacionAprobador.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", 
                    "Debe ingresar una observación"));
            return;
        }
        req.setEstado(EstadoRequerimiento.OBSERVADO);
        String obsExistente = req.getObservacion() != null ? req.getObservacion() + "\n" : "";
        req.setObservacion(obsExistente + "[OBSERVADO] " + observacionAprobador);
        requerimientoDAO.update(req);
        observacionAprobador = null;
        requerimientoSeleccionado = null;
        cargarPendientes();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Observado", 
                "Requerimiento #" + req.getId() + " devuelto al solicitante"));
    }

    public void rechazar(Requerimiento req) {
        req.setEstado(EstadoRequerimiento.RECHAZADO);
        requerimientoDAO.update(req);
        cargarPendientes();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Rechazado", 
                "Requerimiento #" + req.getId() + " rechazado"));
    }
    
    public void rechazarConObservacion(Requerimiento req) {
        if (observacionAprobador == null || observacionAprobador.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", 
                    "Debe ingresar el motivo del rechazo"));
            return;
        }
        req.setEstado(EstadoRequerimiento.RECHAZADO);
        String obsExistente = req.getObservacion() != null ? req.getObservacion() + "\n" : "";
        req.setObservacion(obsExistente + "[RECHAZADO] " + observacionAprobador);
        requerimientoDAO.update(req);
        observacionAprobador = null;
        requerimientoSeleccionado = null;
        cargarPendientes();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Rechazado", 
                "Requerimiento #" + req.getId() + " rechazado con observación"));
    }

    // --- Getters y Setters ---
    public List<Requerimiento> getRequerimientosPendientes() { return requerimientosPendientes; }
    public void setRequerimientosPendientes(List<Requerimiento> r) { this.requerimientosPendientes = r; }

    public Requerimiento getRequerimientoSeleccionado() { return requerimientoSeleccionado; }
    public void setRequerimientoSeleccionado(Requerimiento r) { this.requerimientoSeleccionado = r; }

    public List<DetalleRequerimiento> getDetallesSeleccionados() { return detallesSeleccionados; }

    public String getObservacionAprobador() { return observacionAprobador; }
    public void setObservacionAprobador(String o) { this.observacionAprobador = o; }
}
