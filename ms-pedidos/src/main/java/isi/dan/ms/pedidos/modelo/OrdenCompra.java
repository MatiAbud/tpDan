package isi.dan.ms.pedidos.modelo;

import java.time.Instant;
import java.util.List;

//import isi.dan.msclientes.model.Obra;
import lombok.Data;

@Data
public class OrdenCompra {
    private String id;
    private Instant fecha;
    private Integer numeroPedido;
    private String user;
    private String observaciones;
    private Cliente cliente;
    private Obra obra;
    private List<HistorialEstado> estados;
    private EstadoPedido estado;
    private Double total;
}
