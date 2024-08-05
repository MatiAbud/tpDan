package isi.dan.ms_productos.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class StockUpdateDTO {
    private Long idProducto;
    private Integer cantidad;
    private BigDecimal precio;

    public StockUpdateDTO(Long idProducto, Integer cantidad, BigDecimal precio) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precio=precio;
    }
}
