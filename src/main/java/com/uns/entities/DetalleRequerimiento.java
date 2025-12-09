package com.uns.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "detalle_requerimiento")
public class DetalleRequerimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_requerimiento", nullable = false)
    private Requerimiento requerimiento;

    @ManyToOne
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @Column(name = "cantidad_solicitada", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadSolicitada;

    @Column(name = "cantidad_atendida", precision = 10, scale = 2)
    private BigDecimal cantidadAtendida;

    public DetalleRequerimiento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Requerimiento getRequerimiento() { return requerimiento; }
    public void setRequerimiento(Requerimiento requerimiento) { this.requerimiento = requerimiento; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public BigDecimal getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(BigDecimal cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }

    public BigDecimal getCantidadAtendida() { return cantidadAtendida; }
    public void setCantidadAtendida(BigDecimal cantidadAtendida) { this.cantidadAtendida = cantidadAtendida; }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final DetalleRequerimiento other = (DetalleRequerimiento) obj;
        return Objects.equals(this.id, other.id);
    }
}
