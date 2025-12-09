package com.uns.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "orden_compra")
public class OrdenCompra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden", length = 20)
    private String numeroOrden;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Column(columnDefinition = "ENUM('Soles', 'Dolares')")
    private String moneda;

    @Column(name = "forma_pago", length = 50)
    private String formaPago;

    @Column(name = "sub_total", precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "id_usuario_compras", nullable = false)
    private Usuario usuarioCompras;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private com.uns.enums.EstadoOrden estado;

    @Column(name = "lugar_entrega", length = 200)
    private String lugarEntrega;

    @Column(name = "fecha_entrega_almacen")
    private LocalDate fechaEntregaAlmacen;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    public OrdenCompra() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public BigDecimal getIgv() { return igv; }
    public void setIgv(BigDecimal igv) { this.igv = igv; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public Usuario getUsuarioCompras() { return usuarioCompras; }
    public void setUsuarioCompras(Usuario usuarioCompras) { this.usuarioCompras = usuarioCompras; }

    public com.uns.enums.EstadoOrden getEstado() { return estado; }
    public void setEstado(com.uns.enums.EstadoOrden estado) { this.estado = estado; }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    public String getLugarEntrega() { return lugarEntrega; }
    public void setLugarEntrega(String lugarEntrega) { this.lugarEntrega = lugarEntrega; }

    public LocalDate getFechaEntregaAlmacen() { return fechaEntregaAlmacen; }
    public void setFechaEntregaAlmacen(LocalDate fechaEntregaAlmacen) { this.fechaEntregaAlmacen = fechaEntregaAlmacen; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final OrdenCompra other = (OrdenCompra) obj;
        return Objects.equals(this.id, other.id);
    }
}
