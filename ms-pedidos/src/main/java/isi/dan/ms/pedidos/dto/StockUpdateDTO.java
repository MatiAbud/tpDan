package isi.dan.ms.pedidos.dto;

import lombok.Data;

@Data
public class StockUpdateDTO {
    private Long idProducto;
    private Integer cantidad;

    public StockUpdateDTO(Long id, Integer cantidad){
        this.idProducto=id;
        this.cantidad=cantidad;
    }
}
