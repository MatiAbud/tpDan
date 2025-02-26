package isi.dan.ms.pedidos.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.codecs.IntegerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import isi.dan.ms.pedidos.aop.LogExecutionTime;
import isi.dan.ms.pedidos.feignClients.ClienteClient;
import isi.dan.ms.pedidos.feignClients.ProductoClient;
import isi.dan.ms.pedidos.modelo.Cliente;
import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.OrdenCompraDetalle;
import isi.dan.ms.pedidos.modelo.Pedido;
import isi.dan.ms.pedidos.modelo.Producto;
import isi.dan.ms.pedidos.servicio.PedidoService;
//import isi.dan.ms_productos.modelo.Producto;
//import isi.dan.msclientes.servicios.ClienteService;

@CrossOrigin(origins = "http://localhost")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    Logger log = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @LogExecutionTime
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        return pedidoService.crearPedido(pedido);
    }

    @GetMapping("/todos")
    @LogExecutionTime
    public List<Pedido> getAllPedidos() {
        return pedidoService.getAllPedidos();
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Pedido> getPedidoPorNumero(@PathVariable Integer id) throws Exception {
        Pedido pedido = pedidoService.getPedidoPorNumero(id);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/cliente/{id}")
    @LogExecutionTime
    public List <Pedido> getPedidoPorCliente(@PathVariable Integer id) throws Exception {
        List<Pedido> pedidos = pedidoService.getPedidosCliente(id);
        return pedidos;
    }

    @GetMapping("/estado/{estado}")
    @LogExecutionTime
    public List<Pedido> getPedidosEstado(@PathVariable String estado) throws Exception {
        List<Pedido> pedidos = pedidoService.getPedidosEstado(estado);
        return pedidos;
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Void> deletePedido(@PathVariable String id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/numero/{num}")
    @LogExecutionTime
    public ResponseEntity<Void> deletePedidoNumero(@PathVariable Integer num) {
        pedidoService.deletePedidoNumero(num);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/entregar")
    @LogExecutionTime
    public ResponseEntity<Pedido> entregarPedido(@PathVariable String id) throws Exception {
        Pedido pedidoActualizado = pedidoService.entregarPedido(id);
        return ResponseEntity.ok(pedidoActualizado);
    }

    @PutMapping("/{id}/cancelar")
    @LogExecutionTime
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable String id) throws Exception {
        Pedido pedidoActualizado = pedidoService.enviarMensajeDevolverStock(id);
        return ResponseEntity.ok(pedidoActualizado);
    }
    /*
     * @GetMapping("/{id}")
     * public ResponseEntity<Pedido> consultarPedido(@PathVariable String id) {
     * Optional<Pedido> pedido = pedidoService.obtenerPedidoPorId(id);
     * return pedido.map(ResponseEntity::ok)
     * .orElseGet(() -> ResponseEntity.notFound().build());
     * }
     * 
     * @GetMapping("/cliente/{clienteId}")
     * public ResponseEntity<List<Pedido>> consultarPedidosPorCliente(@PathVariable
     * String clienteId) {
     * List<Pedido> pedidos = pedidoService.obtenerPedidosPorCliente(clienteId);
     * return ResponseEntity.ok(pedidos);
     * }
     */
    /*
     * @PostMapping("/verificarPuntoPedido")
     * public void verificarPuntoPedidoYGenerarPedido(@RequestBody Producto
     * producto) {
     * pedidoService.verificarPuntoPedidoYGenerarPedido(producto);
     * }
     */
}
