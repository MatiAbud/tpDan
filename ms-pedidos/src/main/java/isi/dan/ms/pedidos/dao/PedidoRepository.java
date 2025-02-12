package isi.dan.ms.pedidos.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.Pedido;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByClienteId(String clienteId);

    List<Pedido> findByClienteIdAndEstadoIn(Integer clienteId, List<EstadoPedido> estados);

    Pedido findByNumeroPedido(Integer numeroPedido);
}
