package com.uns.controller;

import com.uns.dao.RequerimientoDAO;
import com.uns.entities.DetalleRequerimiento;
import com.uns.entities.Requerimiento;
import com.uns.entities.Usuario;
import com.uns.enums.EstadoRequerimiento;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named
@ViewScoped
public class RequerimientoBean implements Serializable {

    @Inject
    private LoginBean loginBean;

    private Requerimiento requerimiento;
    private List<Requerimiento> requerimientos;
    private RequerimientoDAO requerimientoDAO;

    private DetalleRequerimiento detalle;
    private List<DetalleRequerimiento> detalles;
    
    // Listas para combos
    private List<com.uns.entities.Proyecto> listaProyectos;
    private List<com.uns.entities.CentroCosto> listaCentrosCosto;
    private List<com.uns.entities.Material> listaMateriales;
    
    // DAOs auxiliares
    private com.uns.dao.ProyectoDAO proyectoDAO;
    private com.uns.dao.CentroCostoDAO centroCostoDAO;
    private com.uns.dao.MaterialDAO materialDAO;
    
    // IDs seleccionados para el detalle (binding simple)
    private Long idMaterialSeleccionado;

    @PostConstruct
    public void init() {
        requerimientoDAO = new RequerimientoDAO();
        proyectoDAO = new com.uns.dao.ProyectoDAO();
        centroCostoDAO = new com.uns.dao.CentroCostoDAO();
        materialDAO = new com.uns.dao.MaterialDAO();
        
        listaProyectos = proyectoDAO.findAll();
        listaCentrosCosto = centroCostoDAO.findAll();
        listaMateriales = materialDAO.findAll();
        
        nuevoRequerimiento();
        requerimientos = requerimientoDAO.findAll();
    }

    public void nuevoRequerimiento() {
        requerimiento = new Requerimiento();
        requerimiento.setFechaSolicitud(LocalDate.now());
        requerimiento.setEstado(EstadoRequerimiento.PENDIENTE);
        
        if (loginBean != null && loginBean.isLoggedIn()) {
            requerimiento.setUsuarioSolicitante(loginBean.getUsuarioLogueado());
        }
        
        detalle = new DetalleRequerimiento();
        detalles = new java.util.ArrayList<>();
    }

    public void agregarDetalle() {
        if (idMaterialSeleccionado != null && detalle.getCantidadSolicitada() != null) {
            // Buscar material
            com.uns.entities.Material m = listaMateriales.stream()
                    .filter(mat -> mat.getId().equals(idMaterialSeleccionado))
                    .findFirst().orElse(null);
            
            if (m != null) {
                detalle.setMaterial(m);
                detalle.setRequerimiento(requerimiento); // Vincular (aunque req aun no tiene ID)
                detalles.add(detalle);
                
                // Limpiar para siguiente item
                detalle = new DetalleRequerimiento();
                idMaterialSeleccionado = null;
            }
        }
    }
    
    public void eliminarDetalle(DetalleRequerimiento det) {
        detalles.remove(det);
    }

    public void guardar() {
        try {
            jakarta.persistence.EntityManager em = com.uns.config.JPAFactory.getEntityManager();
            try {
                em.getTransaction().begin();
                
                if (requerimiento.getId() == null) {
                    em.persist(requerimiento); // Guarda cabecera y genera ID
                } else {
                    em.merge(requerimiento);
                }
                
                // Guardar detalles
                for (DetalleRequerimiento det : detalles) {
                    det.setRequerimiento(requerimiento); // Asegurar vínculo
                    em.persist(det);
                }
                
                em.getTransaction().commit();
                
                nuevoRequerimiento();
                requerimientos = requerimientoDAO.findAll();
                
            } catch (Exception e) {
                em.getTransaction().rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Actions for flow ---
    public void aprobar(Requerimiento req) {
        req.setEstado(EstadoRequerimiento.APROBADO);
        requerimientoDAO.update(req);
        requerimientos = requerimientoDAO.findAll();
    }

    public void rechazar(Requerimiento req) {
        req.setEstado(EstadoRequerimiento.RECHAZADO);
        requerimientoDAO.update(req);
        requerimientos = requerimientoDAO.findAll();
    }

    // --- Getters y Setters ---
    public Requerimiento getRequerimiento() { return requerimiento; }
    public void setRequerimiento(Requerimiento requerimiento) { this.requerimiento = requerimiento; }

    public List<Requerimiento> getRequerimientos() { return requerimientos; }
    public void setRequerimientos(List<Requerimiento> requerimientos) { this.requerimientos = requerimientos; }
    
    public List<DetalleRequerimiento> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleRequerimiento> detalles) { this.detalles = detalles; }
    
    public DetalleRequerimiento getDetalle() { return detalle; }
    public void setDetalle(DetalleRequerimiento detalle) { this.detalle = detalle; }
    
    public List<com.uns.entities.Proyecto> getListaProyectos() { return listaProyectos; }
    public List<com.uns.entities.CentroCosto> getListaCentrosCosto() { return listaCentrosCosto; }
    public List<com.uns.entities.Material> getListaMateriales() { return listaMateriales; }
    
    public Long getIdMaterialSeleccionado() { return idMaterialSeleccionado; }
    public void setIdMaterialSeleccionado(Long idMaterialSeleccionado) { this.idMaterialSeleccionado = idMaterialSeleccionado; }
}
