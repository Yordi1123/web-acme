package com.uns.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "unidad")
public class Unidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String abreviatura;

    @Column(length = 50)
    private String descripcion;

    @Column(columnDefinition = "ENUM('Activo', 'Baja')")
    private String estado;

    // Constructor vacío (Obligatorio para JPA)
    public Unidad() {
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAbreviatura() { return abreviatura; }
    public void setAbreviatura(String abreviatura) { this.abreviatura = abreviatura; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // --- Equals y HashCode (Vital para que PrimeFaces seleccione items correctamente) ---
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Unidad other = (Unidad) obj;
        return Objects.equals(this.id, other.id);
    }
    
    @Override
    public String toString() {
        return descripcion; // Para que se vea bonito en los logs
    }
}