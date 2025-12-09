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

    // Para el diálogo de detalle
    private Requerimiento requerimientoSeleccionado;
    private List<com.uns.entities.DetalleRequerimiento> detallesSeleccionados;
    private com.uns.dao.DetalleRequerimientoDAO detalleDAO = new com.uns.dao.DetalleRequerimientoDAO();

    public void seleccionar(Requerimiento req) {
        System.out.println(">>> SeguimientoBean.seleccionar() - ID: " + req.getId());
        this.requerimientoSeleccionado = req;
        // Cargar detalles frescos desde la base de datos
        this.detallesSeleccionados = detalleDAO.findByRequerimientoConMaterial(req.getId());
        System.out.println(">>> Detalles cargados: " + (detallesSeleccionados != null ? detallesSeleccionados.size() : 0));
    }

    // Getters y Setters
    public List<Requerimiento> getRequerimientos() { return requerimientos; }
    public void setRequerimientos(List<Requerimiento> requerimientos) { this.requerimientos = requerimientos; }

    public String getFiltroEstado() { return filtroEstado; }
    public void setFiltroEstado(String filtroEstado) { this.filtroEstado = filtroEstado; }

    public Requerimiento getRequerimientoSeleccionado() { return requerimientoSeleccionado; }
    public void setRequerimientoSeleccionado(Requerimiento r) { this.requerimientoSeleccionado = r; }

    public List<com.uns.entities.DetalleRequerimiento> getDetallesSeleccionados() { return detallesSeleccionados; }
}
