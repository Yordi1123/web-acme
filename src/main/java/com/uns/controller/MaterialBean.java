package com.uns.controller;

import com.uns.dao.GrupoDAO;
import com.uns.dao.MaterialDAO;
import com.uns.dao.UnidadDAO;
import com.uns.entities.Grupo;
import com.uns.entities.Material;
import com.uns.entities.Unidad;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class MaterialBean implements Serializable {

    private Material material;
    private List<Material> materiales;
    
    // Listas para los Combos (SelectOneMenu)
    private List<Grupo> listaGrupos;
    private List<Unidad> listaUnidades;
    
    // Objeto temporal para capturar el ID seleccionado en el combo
    private Long idGrupoSeleccionado;
    private Long idUnidadSeleccionada;

    private MaterialDAO materialDAO;
    private GrupoDAO grupoDAO;
    private UnidadDAO unidadDAO;

    @PostConstruct
    public void init() {
        materialDAO = new MaterialDAO();
        grupoDAO = new GrupoDAO();
        unidadDAO = new UnidadDAO();
        
        material = new Material();
        
        // Cargar todas las listas
        materiales = materialDAO.findAll();
        listaGrupos = grupoDAO.findAll();
        listaUnidades = unidadDAO.findAll();
    }

    public void guardar() {
        try {
            // Vincular los objetos seleccionados por ID
            Grupo g = new Grupo(); 
            g.setId(idGrupoSeleccionado);
            material.setGrupo(g);
            
            Unidad u = new Unidad();
            u.setId(idUnidadSeleccionada);
            material.setUnidad(u);
            
            material.setFechaCreacion(new Date());
            material.setEstado("Activo");
            
            materialDAO.create(material);
            
            // Refrescar
            materiales = materialDAO.findAll();
            material = new Material();
            idGrupoSeleccionado = null;
            idUnidadSeleccionada = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eliminar(Long id) {
        materialDAO.delete(id);
        materiales = materialDAO.findAll();
    }

    // --- GETTERS Y SETTERS ---
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public List<Material> getMateriales() { return materiales; }

    public List<Grupo> getListaGrupos() { return listaGrupos; }
    public List<Unidad> getListaUnidades() { return listaUnidades; }

    public Long getIdGrupoSeleccionado() { return idGrupoSeleccionado; }
    public void setIdGrupoSeleccionado(Long idGrupoSeleccionado) { this.idGrupoSeleccionado = idGrupoSeleccionado; }

    public Long getIdUnidadSeleccionada() { return idUnidadSeleccionada; }
    public void setIdUnidadSeleccionada(Long idUnidadSeleccionada) { this.idUnidadSeleccionada = idUnidadSeleccionada; }
}