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

    private List<Unidad> unidades;
    private UnidadDAO unidadDAO = new UnidadDAO();

    @PostConstruct
    public void init() {
        System.out.println("--- INICIANDO BEAN DE UNIDAD ---");
    try {
        unidades = unidadDAO.findAll();
        System.out.println("✅ Se encontraron " + (unidades != null ? unidades.size() : 0) + " unidades.");
    } catch (Exception e) {
        System.out.println("❌ ERROR GRAVE CONECTANDO A BD:");
        e.printStackTrace(); // Esto imprimirá el error real en la ventana Output
    }
    }

    public List<Unidad> getUnidades() {
        return unidades;
    }
}