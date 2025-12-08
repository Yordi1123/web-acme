package com.uns.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "material")
public class Material implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codcorrelativo", length = 3)
    private String codCorrelativo;

    @Column(length = 75)
    private String nombre;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;

    @Column(columnDefinition = "ENUM('Activo', 'Baja')")
    private String estado;

    // --- RELACIONES (Foreign Keys) ---
    
    @ManyToOne // Muchos materiales pertenecen a un Grupo
    @JoinColumn(name = "idgrupo") // Nombre de la columna en BD
    private Grupo grupo;

    @ManyToOne // Muchos materiales tienen una Unidad
    @JoinColumn(name = "idunidad") // Nombre de la columna en BD
    private Unidad unidad;

    public Material() {
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodCorrelativo() { return codCorrelativo; }
    public void setCodCorrelativo(String codCorrelativo) { this.codCorrelativo = codCorrelativo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }

    public Unidad getUnidad() { return unidad; }
    public void setUnidad(Unidad unidad) { this.unidad = unidad; }

    // --- Equals y HashCode ---
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Material other = (Material) obj;
        return Objects.equals(this.id, other.id);
    }
}