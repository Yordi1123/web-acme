package com.uns.controller;

import com.uns.dao.UnidadDAO;
import com.uns.entities.Unidad;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UnidadBean implements Serializable {

    private Unidad unidad; // Objeto para el formulario
    private List<Unidad> unidades; // Lista para la tabla
    private UnidadDAO unidadDAO;

    @PostConstruct
    public void init() {
        unidadDAO = new UnidadDAO();
        unidad = new Unidad();
        // Cargar la lista al iniciar
        unidades = unidadDAO.findAll();
    }

    public void guardar() {
        try {
            unidad.setEstado("Activo"); // Por defecto
            unidadDAO.create(unidad);
            
            // Actualizar la tabla y limpiar el formulario
            unidades = unidadDAO.findAll();
            unidad = new Unidad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Getters y Setters ---
    public Unidad getUnidad() { return unidad; }
    public void setUnidad(Unidad unidad) { this.unidad = unidad; }

    public List<Unidad> getUnidades() { return unidades; }
    public void setUnidades(List<Unidad> unidades) { this.unidades = unidades; }
}