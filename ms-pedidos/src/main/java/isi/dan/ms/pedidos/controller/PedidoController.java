package isi.dan.ms.pedidos.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import isi.dan.ms.pedidos.modelo.Cliente;
import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.OrdenCompraDetalle;
import isi.dan.ms.pedidos.modelo.Pedido;
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

    // @Autowired
    // private ClienteService clienteService;

    @PostMapping
    @LogExecutionTime
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        // Paso 1: Crear un nuevo pedido con los datos proporcionados
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(pedido.getCliente());
        // nuevoPedido.setObra(pedido.getObra());
        nuevoPedido.setObservaciones(pedido.getObservaciones());

        // Asignar número de pedido y fecha actual
        nuevoPedido.setNumeroPedido(pedidoCounter.incrementAndGet());
        nuevoPedido.setFecha(Instant.now());

        // Paso 2: Calcular el total del pedido y el total de cada línea
        BigDecimal totalPedido = BigDecimal.ZERO;
        for (OrdenCompraDetalle detalle : pedido.getDetalle()) {
            detalle.calcularTotalLinea();
            totalPedido = totalPedido.add(detalle.getPrecioFinal());
        }
        nuevoPedido.setDetalle(pedido.getDetalle());
        nuevoPedido.setTotal(totalPedido);

        // Paso b: Verificar saldo del cliente
        Cliente cliente = nuevoPedido.getCliente();
        // BigDecimal tieneSaldoSuficiente =
        // clienteService.verificarSaldo(cliente.getId());

        // Obtener los pedidos que no han sido entregados o rechazados
        List<Pedido> pedidosEnCurso = pedidoService.obtenerPedidosEnCurso(cliente.getId());

        // Sumar el monto de esos pedidos
        BigDecimal saldoActual = pedidosEnCurso.stream().map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular el saldo con el nuevo pedido
        BigDecimal saldoConNuevoPedido = saldoActual.add(nuevoPedido.getTotal());

        /*
         * if (saldoConNuevoPedido.compareTo(tieneSaldoSuficiente) > 0) {
         * // Si el cliente no tiene saldo suficiente, se rechaza el pedido
         * nuevoPedido.setEstado(EstadoPedido.RECHAZADO);
         * Pedido pedidoRechazado = pedidoService.savePedido(nuevoPedido);
         * log.info("Pedido rechazado por falta de saldo: {}", pedidoRechazado);
         * 
         * return ResponseEntity.ok(pedidoRechazado); // Retornar el pedido rechazado
         * }
         */
        // Paso c: Verificar y actualizar el stock
        boolean stockActualizado = pedidoService.verificarYActualizarStock(nuevoPedido.getDetalle());
        if (!stockActualizado) {
            // Si no se pudo actualizar el stock de todos los productos, el pedido queda en
            // estado "ACEPTADO"
            nuevoPedido.setEstado(EstadoPedido.ACEPTADO);
        } else {
            // Si el stock se actualizó correctamente para todos los productos, el pedido se
            // guarda en estado "EN_PREPARACION"
            nuevoPedido.setEstado(EstadoPedido.EN_PREPARACION);
        }

        // Guardar el pedido en la base de datos
        Pedido pedidoGuardado = pedidoService.savePedido(nuevoPedido);

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
    public ResponseEntity<Pedido> getPedidoById(@PathVariable String id) {
        Pedido pedido = pedidoService.getPedidoById(id);
        return pedido != null ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Void> deletePedido(@PathVariable String id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    @LogExecutionTime
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable String id, @RequestBody EstadoPedido estado) {
        Optional<Pedido> pedidoOptional = pedidoService.obtenerPedidoPorId(id);

        if (!pedidoOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = pedidoOptional.get();

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
