package isi.dan.ms.pedidos.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class StockUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idProducto;
    private Integer cantidad;

    public StockUpdateDTO(Long id, Integer cantidad){
        this.idProducto=id;
        this.cantidad=cantidad;
    }
}
