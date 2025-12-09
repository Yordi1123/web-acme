package com.uns.controller;

import com.uns.dao.RequerimientoDAO;
import com.uns.entities.Requerimiento;
import com.uns.enums.EstadoRequerimiento;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class SeguimientoBean implements Serializable {

    @Inject
    private LoginBean loginBean;

    private RequerimientoDAO requerimientoDAO;
    private List<Requerimiento> requerimientos;
    private List<Requerimiento> todosRequerimientos;
    private String filtroEstado;

    @PostConstruct
    public void init() {
        requerimientoDAO = new RequerimientoDAO();
        cargarRequerimientos();
    }

    public void cargarRequerimientos() {
        if (loginBean != null && loginBean.isLoggedIn()) {
            todosRequerimientos = requerimientoDAO.findByUsuarioConDetalles(loginBean.getUsuarioLogueado().getId());
            requerimientos = todosRequerimientos;
        } else {
            requerimientos = new java.util.ArrayList<>();
            todosRequerimientos = new java.util.ArrayList<>();
        }
    }

    public void filtrar() {
        if (filtroEstado == null || filtroEstado.isEmpty()) {
            requerimientos = todosRequerimientos;
        } else {
            EstadoRequerimiento estado = EstadoRequerimiento.valueOf(filtroEstado);
            requerimientos = todosRequerimientos.stream()
                .filter(r -> r.getEstado() == estado)
                .collect(Collectors.toList());
        }
    }

    public void limpiarFiltros() {
        filtroEstado = null;
        requerimientos = todosRequerimientos;
    }

    // Getters y Setters
    public List<Requerimiento> getRequerimientos() { return requerimientos; }
    public void setRequerimientos(List<Requerimiento> requerimientos) { this.requerimientos = requerimientos; }

    public String getFiltroEstado() { return filtroEstado; }
    public void setFiltroEstado(String filtroEstado) { this.filtroEstado = filtroEstado; }
}
