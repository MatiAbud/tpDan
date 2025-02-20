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

    private static final AtomicInteger pedidoCounter = new AtomicInteger(0);

    Logger log = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService pedidoService;

    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    public PedidoController(ClienteClient clienteClient, ProductoClient productoClient) {
        this.clienteClient = clienteClient;
        this.productoClient = productoClient;
    }

    @PostMapping
    @LogExecutionTime
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        // Paso 1: Crear un nuevo pedido con los datos proporcionados
        Cliente cliente = pedido.getCliente();
        if (cliente == null) {
            log.error("Cliente con ID {} no encontrado", pedido.getCliente().getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        pedido.setNumeroPedido(pedidoCounter.incrementAndGet());
        pedido.setFecha(Instant.now());

        if (!clienteClient.verificarSaldo(cliente.getId(), pedido.getTotal().abs()).getBody()) {
            System.out.println("ENTRE AL IF !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            // Si el cliente no tiene saldo suficiente, se rechaza el pedido
            pedido.setEstado(EstadoPedido.RECHAZADO);
            Pedido pedidoRechazado = pedidoService.savePedido(pedido);
            log.info("Pedido rechazado por falta de saldo: {}", pedidoRechazado);
            
            return ResponseEntity.ok(pedidoRechazado); // Retornar el pedido rechazado
        }

        System.out.println("SALDO VERIFICADO Y ACTUALIZADO");

        boolean stockActualizado = pedidoService.verificarYActualizarStock(pedido.getDetalle());
        if (!stockActualizado) {
            // Si no se pudo actualizar el stock de todos los productos, el pedido queda en
            // estado "ACEPTADO"
            pedido.setEstado(EstadoPedido.ACEPTADO);
        } else {
            // Si el stock se actualizó correctamente para todos los productos, el pedido se
            // guarda en estado "EN_PREPARACION"
            pedido.setEstado(EstadoPedido.EN_PREPARACION);
        }
        // Guardar el pedido en la base de datos
        Pedido pedidoGuardado = pedidoService.crearPedido(pedido);

        log.info("Nuevo pedido creado: {}", pedidoGuardado);

        return ResponseEntity.ok(pedidoGuardado);
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

    @PutMapping("/{id}/estado")
    @LogExecutionTime
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Integer id, @RequestBody EstadoPedido estado) throws Exception {
        Pedido pedido = pedidoService.getPedidoPorNumero(id);

        // If the state is being updated to "CANCELADO", send message to return stock
        if (estado == EstadoPedido.CANCELADO) {
            pedidoService.enviarMensajeDevolverStock(pedido);
        }

        // Update the order state and save it
        pedido.setEstado(estado);
        Pedido pedidoActualizado = pedidoService.actualizarPedido(pedido);

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
