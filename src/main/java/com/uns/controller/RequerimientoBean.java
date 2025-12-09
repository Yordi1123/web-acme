package com.uns.controller;

import com.uns.dao.RequerimientoDAO;
import com.uns.entities.DetalleRequerimiento;
import com.uns.entities.Requerimiento;
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
    private List<com.uns.entities.AreaNegocio> listaAreasNegocio;
    private List<com.uns.entities.Usuario> listaJefes;
    
    // DAOs auxiliares
    private com.uns.dao.ProyectoDAO proyectoDAO;
    private com.uns.dao.CentroCostoDAO centroCostoDAO;
    private com.uns.dao.MaterialDAO materialDAO;
    private com.uns.dao.AreaNegocioDAO areaNegocioDAO;
    private com.uns.dao.UsuarioDAO usuarioDAO;
    
    // IDs seleccionados para el detalle (binding simple)
    private Long idMaterialSeleccionado;

    @PostConstruct
    public void init() {
        requerimientoDAO = new RequerimientoDAO();
        proyectoDAO = new com.uns.dao.ProyectoDAO();
        centroCostoDAO = new com.uns.dao.CentroCostoDAO();
        materialDAO = new com.uns.dao.MaterialDAO();
        areaNegocioDAO = new com.uns.dao.AreaNegocioDAO();
        usuarioDAO = new com.uns.dao.UsuarioDAO();
        
        listaProyectos = proyectoDAO.findAll();
        listaCentrosCosto = centroCostoDAO.findAll();
        listaMateriales = materialDAO.findAll();
        listaAreasNegocio = areaNegocioDAO.findAll();
        listaJefes = usuarioDAO.findByRol(com.uns.enums.RolUsuario.JEFE_AREA);
        
        nuevoRequerimiento();
        cargarMisRequerimientos();
    }
    
    public void cargarMisRequerimientos() {
        if (loginBean != null && loginBean.isLoggedIn()) {
            requerimientos = requerimientoDAO.findByUsuarioConDetalles(loginBean.getUsuarioLogueado().getId());
        } else {
            requerimientos = new java.util.ArrayList<>();
        }
    }

    public void nuevoRequerimiento() {
        requerimiento = new Requerimiento();
        requerimiento.setFechaSolicitud(LocalDate.now());
        requerimiento.setEstado(EstadoRequerimiento.PENDIENTE);
        requerimiento.setEtapa(1); // Default etapa 1
        
        if (loginBean != null && loginBean.isLoggedIn()) {
            requerimiento.setUsuarioSolicitante(loginBean.getUsuarioLogueado());
            // Auto-asignar area del usuario si tiene una
            if (loginBean.getUsuarioLogueado().getAreaNegocio() != null) {
                requerimiento.setAreaNegocio(loginBean.getUsuarioLogueado().getAreaNegocio());
                // Auto-asignar jefe del area
                if (loginBean.getUsuarioLogueado().getAreaNegocio().getJefe() != null) {
                    requerimiento.setJefeAprobador(loginBean.getUsuarioLogueado().getAreaNegocio().getJefe());
                }
            }
        }
        
        detalle = new DetalleRequerimiento();
        detalles = new java.util.ArrayList<>();
    }
    
    public void onAreaChange() {
        // Cuando cambia el area, auto-seleccionar su jefe
        if (requerimiento.getAreaNegocio() != null && requerimiento.getAreaNegocio().getJefe() != null) {
            requerimiento.setJefeAprobador(requerimiento.getAreaNegocio().getJefe());
        }
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
        // Validación: mínimo 1 ítem
        if (detalles == null || detalles.isEmpty()) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "Debe agregar al menos un ítem al requerimiento"));
            return;
        }
        
        try {
            jakarta.persistence.EntityManager em = com.uns.config.JPAFactory.getEntityManager();
            try {
                em.getTransaction().begin();
                
                boolean esNuevo = (requerimiento.getId() == null);
                
                if (esNuevo) {
                    em.persist(requerimiento); // Guarda cabecera y genera ID
                    em.flush(); // Forzar generación del ID
                    
                    // Generar código: REQ-YYYYMM-XXXX (donde XXXX es el ID con padding)
                    String codigo = String.format("REQ-%tY%<tm-%04d", 
                        java.sql.Date.valueOf(requerimiento.getFechaSolicitud()), 
                        requerimiento.getId());
                    requerimiento.setCodigoReq(codigo);
                    em.merge(requerimiento);
                } else {
                    em.merge(requerimiento);
                }
                
                // Guardar detalles
                for (DetalleRequerimiento det : detalles) {
                    det.setRequerimiento(requerimiento); // Asegurar vínculo
                    if (det.getId() == null) {
                        em.persist(det);
                    } else {
                        em.merge(det);
                    }
                }
                
                em.getTransaction().commit();
                
                nuevoRequerimiento();
                cargarMisRequerimientos();
                
                jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                        "Éxito", "Requerimiento guardado correctamente"));
                
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
    
    /**
     * Verifica si el requerimiento puede ser editado.
     * Solo se permite editar en estados PENDIENTE u OBSERVADO.
     */
    public boolean puedeEditar(Requerimiento req) {
        if (req == null || req.getEstado() == null) return false;
        return req.getEstado() == EstadoRequerimiento.PENDIENTE 
            || req.getEstado() == EstadoRequerimiento.OBSERVADO;
    }
    
    /**
     * Verifica si es un requerimiento observado que requiere reenvío.
     */
    public boolean esObservado(Requerimiento req) {
        if (req == null || req.getEstado() == null) return false;
        return req.getEstado() == EstadoRequerimiento.OBSERVADO;
    }
    
    /**
     * Verifica si estamos editando un requerimiento existente.
     */
    public boolean isEditando() {
        return requerimiento != null && requerimiento.getId() != null;
    }
    
    /**
     * Getter para acceso desde EL.
     */
    public boolean getEditando() {
        return isEditando();
    }
    
    /**
     * Reenvía un requerimiento observado al jefe.
     * Cambia el estado de OBSERVADO a PENDIENTE para que el jefe pueda revisarlo de nuevo.
     */
    public void reenviarAlJefe() {
        if (requerimiento == null || requerimiento.getId() == null) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "Debe seleccionar un requerimiento para reenviar"));
            return;
        }
        
        if (requerimiento.getEstado() != EstadoRequerimiento.OBSERVADO) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_WARN,
                    "No aplicable", "Solo los requerimientos observados pueden ser reenviados"));
            return;
        }
        
        // Validar que tenga items
        if (detalles == null || detalles.isEmpty()) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Error", "Debe agregar al menos un ítem al requerimiento"));
            return;
        }
        
        try {
            jakarta.persistence.EntityManager em = com.uns.config.JPAFactory.getEntityManager();
            try {
                em.getTransaction().begin();
                
                // Agregar nota de reenvío
                String obsExistente = requerimiento.getObservacion() != null ? requerimiento.getObservacion() + "\n" : "";
                requerimiento.setObservacion(obsExistente + "[REENVIADO por " + 
                    loginBean.getUsuarioLogueado().getNombreCompleto() + " el " + 
                    java.time.LocalDate.now() + "]");
                
                // Cambiar estado a PENDIENTE
                requerimiento.setEstado(EstadoRequerimiento.PENDIENTE);
                
                em.merge(requerimiento);
                
                // Guardar detalles
                for (DetalleRequerimiento det : detalles) {
                    det.setRequerimiento(requerimiento);
                    if (det.getId() == null) {
                        em.persist(det);
                    } else {
                        em.merge(det);
                    }
                }
                
                em.getTransaction().commit();
                
                nuevoRequerimiento();
                cargarMisRequerimientos();
                
                jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                    new jakarta.faces.application.FacesMessage(
                        jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                        "Reenviado", "Requerimiento reenviado al jefe para su revisión"));
                        
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
    
    public void seleccionarParaEditar(Requerimiento req) {
        if (!puedeEditar(req)) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_WARN,
                    "No editable", "Este requerimiento ya no puede ser modificado"));
            return;
        }
        this.requerimiento = req;
        // Cargar detalles frescos desde la base de datos para evitar problemas de lazy loading
        com.uns.dao.DetalleRequerimientoDAO detalleDAO = new com.uns.dao.DetalleRequerimientoDAO();
        this.detalles = new java.util.ArrayList<>(detalleDAO.findByRequerimientoConMaterial(req.getId()));
        
        jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
            new jakarta.faces.application.FacesMessage(
                jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                "Editando", "Requerimiento " + req.getCodigoReq() + " cargado para edición"));
    }
    
    public void eliminar(Requerimiento req) {
        if (!puedeEditar(req)) {
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_WARN,
                    "No eliminable", "Este requerimiento ya no puede ser eliminado"));
            return;
        }
        requerimientoDAO.delete(req);
        cargarMisRequerimientos();
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

    public List<com.uns.entities.AreaNegocio> getListaAreasNegocio() { return listaAreasNegocio; }
    public List<com.uns.entities.Usuario> getListaJefes() { return listaJefes; }
}
