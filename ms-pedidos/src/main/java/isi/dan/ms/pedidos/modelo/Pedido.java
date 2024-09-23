package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import isi.dan.msclientes.model.Obra;
import lombok.Data;

@Document(collection = "pedidos")
@Data
public class Pedido {
    @Id
    private String id;
    private Instant fecha;
    private Integer numeroPedido;
    private String usuario;
    private String observaciones;

    private Cliente cliente;
    private Obra obra;
    private BigDecimal total;

    private EstadoPedido estado;

    @Field("detalle")
    private List<OrdenCompraDetalle> detalle;

    @Field("historialEstado")
    private List<HistorialEstado> historialEstado;

    // Método para calcular el total del pedido
    public void calcularTotal() {
        this.total = detalle.stream()
                .map(OrdenCompraDetalle::getPrecioFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
