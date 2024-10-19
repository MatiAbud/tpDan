package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;

//import isi.dan.ms_productos.modelo.Categoria;
import lombok.Data;

@Data
public class Producto {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stockActual;
    private Integer stockMinimo;
    private Categoria categorio;

}
