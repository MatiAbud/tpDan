package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Obra {

    private Integer id;
    private String direccion;
    private Integer idCliente;
    private Integer lat;
    private Integer lng;
    private Boolean esRemodelacion;
    private BigDecimal presupuesto;
    
}
