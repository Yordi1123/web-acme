package com.uns.controller;

import com.uns.dao.RequerimientoDAO;
import com.uns.entities.Requerimiento;
import com.uns.enums.EstadoRequerimiento;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AprobacionBean implements Serializable {

    private RequerimientoDAO requerimientoDAO;
    private List<Requerimiento> requerimientosPendientes;
    private Requerimiento requerimientoSeleccionado;
    private String observacionAprobador;

    @PostConstruct
    public void init() {
        requerimientoDAO = new RequerimientoDAO();
        cargarPendientes();
    }

    public void cargarPendientes() {
        requerimientosPendientes = requerimientoDAO.findPendientes();
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
        // Append observation to existing one
        String obsExistente = req.getObservacion() != null ? req.getObservacion() + "\n" : "";
        req.setObservacion(obsExistente + "[OBSERVADO] " + observacionAprobador);
        requerimientoDAO.update(req);
        observacionAprobador = null;
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
        // Agregar observación del rechazo
        String obsExistente = req.getObservacion() != null ? req.getObservacion() + "\n" : "";
        req.setObservacion(obsExistente + "[RECHAZADO] " + observacionAprobador);
        requerimientoDAO.update(req);
        observacionAprobador = null;
        cargarPendientes();
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Rechazado", 
                "Requerimiento #" + req.getId() + " rechazado con observación"));
    }

    public void seleccionar(Requerimiento req) {
        this.requerimientoSeleccionado = req;
        this.observacionAprobador = null;
    }

    // --- Getters y Setters ---
    public List<Requerimiento> getRequerimientosPendientes() { return requerimientosPendientes; }
    public void setRequerimientosPendientes(List<Requerimiento> r) { this.requerimientosPendientes = r; }

    public Requerimiento getRequerimientoSeleccionado() { return requerimientoSeleccionado; }
    public void setRequerimientoSeleccionado(Requerimiento r) { this.requerimientoSeleccionado = r; }

    public String getObservacionAprobador() { return observacionAprobador; }
    public void setObservacionAprobador(String o) { this.observacionAprobador = o; }
}
