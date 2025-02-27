package isi.dan.ms.pedidos.modelo;

import java.time.Instant;

import lombok.Data;

@Data
public class HistorialEstado {
    private EstadoPedido estado;
    private Instant fechaEstado;
    private String usuarioEstado;
    private String detalle;

    public HistorialEstado(EstadoPedido estado, Instant fecha, String detalle, String user){
        this.estado=estado;
        this.fechaEstado=fecha;
        this.detalle=detalle;
        this.usuarioEstado=user;
    }

    public HistorialEstado(){
        
    }
}
