package com.uns.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "requerimiento")
public class Requerimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_req", length = 20)
    private String codigoReq;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    private Usuario usuarioSolicitante;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "id_centro_costo", nullable = false)
    private CentroCosto centroCosto;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private com.uns.enums.EstadoRequerimiento estado;

    @OneToMany(mappedBy = "requerimiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<DetalleRequerimiento> detalles = new java.util.ArrayList<>();

    @Column
    private Integer etapa;

    @ManyToOne
    @JoinColumn(name = "id_area_negocio")
    private AreaNegocio areaNegocio;

    @ManyToOne
    @JoinColumn(name = "id_jefe_aprobador")
    private Usuario jefeAprobador;

    public Requerimiento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoReq() { return codigoReq; }
    public void setCodigoReq(String codigoReq) { this.codigoReq = codigoReq; }

    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDate fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public Usuario getUsuarioSolicitante() { return usuarioSolicitante; }
    public void setUsuarioSolicitante(Usuario usuarioSolicitante) { this.usuarioSolicitante = usuarioSolicitante; }

    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }

    public CentroCosto getCentroCosto() { return centroCosto; }
    public void setCentroCosto(CentroCosto centroCosto) { this.centroCosto = centroCosto; }

    public com.uns.enums.EstadoRequerimiento getEstado() { return estado; }
    public void setEstado(com.uns.enums.EstadoRequerimiento estado) { this.estado = estado; }

    public java.util.List<DetalleRequerimiento> getDetalles() { return detalles; }
    public void setDetalles(java.util.List<DetalleRequerimiento> detalles) { this.detalles = detalles; }

    public Integer getEtapa() { return etapa; }
    public void setEtapa(Integer etapa) { this.etapa = etapa; }

    public AreaNegocio getAreaNegocio() { return areaNegocio; }
    public void setAreaNegocio(AreaNegocio areaNegocio) { this.areaNegocio = areaNegocio; }

    public Usuario getJefeAprobador() { return jefeAprobador; }
    public void setJefeAprobador(Usuario jefeAprobador) { this.jefeAprobador = jefeAprobador; }

    /**
     * Calcula el porcentaje de atención del requerimiento.
     * @return Porcentaje de 0 a 100
     */
    public double getPorcentajeAtencion() {
        if (detalles == null || detalles.isEmpty()) return 0.0;
        
        java.math.BigDecimal totalSolicitado = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalAtendido = java.math.BigDecimal.ZERO;
        
        for (DetalleRequerimiento det : detalles) {
            totalSolicitado = totalSolicitado.add(det.getCantidadSolicitada());
            totalAtendido = totalAtendido.add(det.getCantidadAtendida() != null ? det.getCantidadAtendida() : java.math.BigDecimal.ZERO);
        }
        
        if (totalSolicitado.compareTo(java.math.BigDecimal.ZERO) == 0) return 0.0;
        
        return totalAtendido.multiply(new java.math.BigDecimal("100"))
                .divide(totalSolicitado, 2, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }
    
    /**
     * Retorna el estado de atención en texto.
     * @return "Totalmente Atendido", "Parcialmente Atendido" o "Sin Atender"
     */
    public String getEstadoAtencion() {
        double porcentaje = getPorcentajeAtencion();
        if (porcentaje >= 100.0) return "Totalmente Atendido";
        if (porcentaje > 0.0) return "Parcialmente Atendido";
        return "Sin Atender";
    }
    
    /**
     * Retorna el severity para el badge de atención.
     * @return "success", "warning" o "secondary"
     */
    public String getSeverityAtencion() {
        double porcentaje = getPorcentajeAtencion();
        if (porcentaje >= 100.0) return "success";
        if (porcentaje > 0.0) return "warning";
        return "secondary";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Requerimiento other = (Requerimiento) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Requerimiento[ id=" + id + " ]";
    }
}
