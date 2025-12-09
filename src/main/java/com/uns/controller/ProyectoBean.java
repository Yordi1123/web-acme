package com.uns.controller;

import com.uns.dao.ProyectoDAO;
import com.uns.entities.Proyecto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ProyectoBean implements Serializable {

    private Proyecto proyecto;
    private List<Proyecto> proyectos;
    private ProyectoDAO proyectoDAO;

    @PostConstruct
    public void init() {
        proyectoDAO = new ProyectoDAO();
        nuevo();
        listar();
    }

    public void nuevo() {
        proyecto = new Proyecto();
        proyecto.setEstado("Activo");
    }

    public void listar() {
        proyectos = proyectoDAO.findAll();
    }

    public void guardar() {
        try {
            if (proyecto.getId() == null) {
                proyectoDAO.create(proyecto);
            } else {
                proyectoDAO.update(proyecto);
            }
            nuevo();
            listar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters y Setters
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }

    public List<Proyecto> getProyectos() { return proyectos; }
    public void setProyectos(List<Proyecto> proyectos) { this.proyectos = proyectos; }
}
