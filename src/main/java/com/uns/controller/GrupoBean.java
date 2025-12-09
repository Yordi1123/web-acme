package com.uns.controller;

import com.uns.dao.GrupoDAO;
import com.uns.entities.Grupo;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class GrupoBean implements Serializable {

    private Grupo grupo;
    private List<Grupo> grupos;
    private GrupoDAO grupoDAO;

    @PostConstruct
    public void init() {
        grupoDAO = new GrupoDAO();
        grupo = new Grupo();
        grupos = grupoDAO.findAll();
    }

    public void guardar() {
        grupo.setEstado("Activo"); // Valor por defecto
        grupoDAO.create(grupo);
        grupos = grupoDAO.findAll(); // Actualizar tabla
        grupo = new Grupo(); // Limpiar formulario
    }

    // Getters y Setters
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public List<Grupo> getGrupos() { return grupos; }
}