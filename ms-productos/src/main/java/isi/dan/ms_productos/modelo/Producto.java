package isi.dan.ms_productos.modelo;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "MS_PRD_PRODUCTO")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;
    private String descripcion;
    @Column(name = "STOCK_ACTUAL")
    private int stockActual;
    @Column(name = "STOCK_MINIMO")
    private int stockMinimo;
    private BigDecimal precio;
    private double descuentoPromocional = 0;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    public Producto(Long id, @NotNull String nombre, String descripcion, int stockActual, int stockMinimo,
            BigDecimal precio, Categoria categoria, double descuentoPromocional) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.precio = precio;
        this.categoria = categoria;
        this.descuentoPromocional = descuentoPromocional;
    }

    // Getters y setters

    public Long getIdProducto() {
        return id;
    }

    public void setIdProducto(Long id) {
        this.id = id;
    }

    public String getNombreP() {
        return nombre;
    }

    public void setNombreP(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getPrecioInicial() {
        return precio;
    }

    public void setPrecioInicial(BigDecimal precioInicial) {
        this.precio = precioInicial;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public double getDescuentoPromocional() {
        return descuentoPromocional;
    }

    public void setDescuentoPromocional(double descuentoPromocional) {
        this.descuentoPromocional = descuentoPromocional;
    }

}
