package isi.dan.ms.pedidos.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.Pedido;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByClienteId(Integer clienteId);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByClienteIdAndEstadoIn(Integer clienteId, List<EstadoPedido> estados);

    Pedido findByNumeroPedido(Integer numeroPedido);

    Void deleteByNumeroPedido(Integer numeroPedido);

    Optional<Pedido> findTopByOrderByNumeroPedidoDesc();
}
