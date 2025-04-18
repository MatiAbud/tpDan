package isi.dan.ms.pedidos.modelo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;

//import isi.dan.msclientes.model.Obra;
import lombok.Data;

@Document(collection = "pedidos")
@Data

public class Pedido {
    @Id
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
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
    public BigDecimal calcularTotal() {
        BigDecimal totalFinal=BigDecimal.valueOf(0);    
        for (OrdenCompraDetalle det: detalle){
            System.out.println("---------------------CALCULAR LINEA ------------------------------------");
            System.out.println(det.calcularTotalLinea());
            System.out.println("---------------------CALCULAR LINEA ------------------------------------");
      
            totalFinal = totalFinal.add(det.calcularTotalLinea());
            System.out.println("---------------------EL TOTAL PARCIAL ES ------------------------------------");
            System.out.println(totalFinal);
            System.out.println("---------------------EL TOTAL PARCIAL ES ------------------------------------");
      
        }
       System.out.println("---------------------EL TOTAL FINAL ES ------------------------------------");
       System.out.println(totalFinal);
       System.out.println("---------------------EL TOTAL FINAL ES ------------------------------------");
        return totalFinal;
    }

    public void addEstado(EstadoPedido estado, Instant fecha, String detalle, String user){
        HistorialEstado historial = new HistorialEstado(estado, fecha, detalle, user);
        historialEstado.add(historial);
    }
}
