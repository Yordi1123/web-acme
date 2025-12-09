package com.uns.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "area_negocio")
public class AreaNegocio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5, nullable = false)
    private String prefijo;

    @Column(length = 100, nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_jefe")
    private Usuario jefe;

    @Column(columnDefinition = "ENUM('Activo','Inactivo') DEFAULT 'Activo'")
    private String estado = "Activo";

    public AreaNegocio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Usuario getJefe() { return jefe; }
    public void setJefe(Usuario jefe) { this.jefe = jefe; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Objects.equals(this.id, ((AreaNegocio) obj).id);
    }

    @Override
    public String toString() {
        return prefijo + " - " + nombre;
    }
}
