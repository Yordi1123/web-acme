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
