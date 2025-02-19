package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Cliente {

    private Integer id;
    private String nombre;
    private String correoElectronico;
    private String cuit;
    public BigDecimal maximoDescubierto;

}
