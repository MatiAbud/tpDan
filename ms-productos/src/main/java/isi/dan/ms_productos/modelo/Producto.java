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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MS_PRD_PRODUCTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull
    private String nombre;
    private String descripcion;
    @Column(name = "STOCK_ACTUAL")
    private int stockActual;
    @Column(name = "STOCK_MINIMO")
    private int stockMinimo;
    private BigDecimal precio;
    private double descuentoPromocional;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

}
