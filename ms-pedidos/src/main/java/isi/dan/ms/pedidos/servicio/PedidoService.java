package isi.dan.ms.pedidos.servicio;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import isi.dan.ms.pedidos.conf.RabbitMQConfig;
import isi.dan.ms.pedidos.dao.PedidoRepository;
import isi.dan.ms.pedidos.dto.StockUpdateDTO;
import isi.dan.ms.pedidos.modelo.EstadoPedido;
import isi.dan.ms.pedidos.modelo.OrdenCompraDetalle;
import isi.dan.ms.pedidos.modelo.Pedido;
//import isi.dan.ms_productos.modelo.Producto;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
 
    @Autowired
    private RestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(PedidoService.class);

    public Pedido savePedido(Pedido pedido) {
        for (OrdenCompraDetalle dp : pedido.getDetalle()) {
            log.info("Enviando {}", dp.getProducto().getId() + ";" + dp.getCantidad());
            rabbitTemplate.convertAndSend(RabbitMQConfig.STOCK_UPDATE_QUEUE,
                    dp.getProducto().getId() + ";" + dp.getCantidad());
        }
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido getPedidoById(String id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public void deletePedido(String id) {
        pedidoRepository.deleteById(id);
    }

    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> obtenerPedidoPorId(String id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerPedidosPorCliente(String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Pedido actualizarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public boolean verificarYActualizarStock(List<OrdenCompraDetalle> detalles) {
        // Llamada al servicio de productos para verificar y actualizar el stock
        String url = "http://ms-productos/actualizar-stock";
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, detalles, Boolean.class);
        return response.getBody(); // Retorna true si el stock fue actualizado correctamente
    }

    // Método para verificar punto de pedido y generar un pedido
    /*
    public void verificarPuntoPedidoYGenerarPedido(@RequestBody Producto producto) {
        try {
            if (producto.getStockActual() <= producto.getStockMinimo()) {
                // Crear nuevo pedido (OrdenCompra)
                Pedido nuevoPedido = new Pedido();
                nuevoPedido.setFecha(Instant.now());
                nuevoPedido.setNumeroPedido(generarNumeroPedido()); // Método para generar un número de pedido

                // Crear el detalle del pedido
                OrdenCompraDetalle detalle = new OrdenCompraDetalle();
                detalle.setProducto(producto);
                detalle.setCantidad(producto.getStockMinimo() - producto.getStockActual()); // Cantidad para reponer
                detalle.setPrecioUnitario(producto.getPrecio()); // Usar el precio unitario del producto
                detalle.setDescuento(BigDecimal.ZERO); // Puedes ajustar el descuento si corresponde
                // Precio final basado en cantidad
                detalle.setPrecioFinal(producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));

                // Añadir el detalle al pedido
                nuevoPedido.getDetalle().add(detalle); // Asume que Pedido tiene una lista de detalles

                // Calcular el total del pedido
                nuevoPedido.calcularTotal();

                // Guardar el pedido en el repositorio de pedidos
                pedidoRepository.save(nuevoPedido);
                log.info("Pedido generado para el producto ID: {} con cantidad: {}", producto.getId(),
                        detalle.getCantidad());
            } else {
                log.info("El producto ID: {} no requiere generar un pedido.", producto.getId());
            }
        } catch (Exception e) {
            log.error("Error al verificar el stock mínimo o generar un pedido", e);
        }
    }
 */
    private int generarNumeroPedido() {
        // Lógica para generar un número de pedido único
        return (int) (Math.random() * 10000);
    }

    // Método para obtener los pedidos que no están rechazados ni entregados
    public List<Pedido> obtenerPedidosEnCurso(Integer clienteId) {
        return pedidoRepository.findByClienteIdAndEstadoIn(
                clienteId, Arrays.asList(EstadoPedido.ACEPTADO, EstadoPedido.EN_PREPARACION));
    }

    public void enviarMensajeDevolverStock(Pedido pedido) {
        for (OrdenCompraDetalle detalle : pedido.getDetalle()) {
            StockUpdateDTO stockUpdateDTO = new StockUpdateDTO();
            stockUpdateDTO.setIdProducto(detalle.getProducto().getId());
            stockUpdateDTO.setCantidad(detalle.getCantidad());

            // Send the message to RabbitMQ
            rabbitTemplate.convertAndSend("devolverStockQueue", stockUpdateDTO);
            log.info("Mensaje enviado a la cola devolverStockQueue: {}", stockUpdateDTO);
        }
    }
}
