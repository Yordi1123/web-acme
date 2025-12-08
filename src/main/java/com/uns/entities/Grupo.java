package com.uns.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "grupo")
public class Grupo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codgrupo", length = 3, nullable = false)
    private String codGrupo;

    @Column(length = 85)
    private String descripcion;

    @Column(length = 85)
    private String observacion;

    @Column(columnDefinition = "ENUM('Activo', 'Baja')")
    private String estado;

    public Grupo() {
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodGrupo() { return codGrupo; }
    public void setCodGrupo(String codGrupo) { this.codGrupo = codGrupo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // --- Equals y HashCode ---
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Grupo other = (Grupo) obj;
        return Objects.equals(this.id, other.id);
    }
}