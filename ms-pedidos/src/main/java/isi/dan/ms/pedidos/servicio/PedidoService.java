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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import isi.dan.ms.pedidos.conf.RabbitMQConfig;
import isi.dan.ms.pedidos.dao.PedidoRepository;
import isi.dan.ms.pedidos.dto.StockUpdateDTO;
import isi.dan.ms.pedidos.feignClients.ProductoClient;
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
        ObjectMapper objectMapper = new ObjectMapper();
        for (OrdenCompraDetalle dp : pedido.getDetalle()) {
            try {
                StockUpdateDTO stock = new StockUpdateDTO(dp.getProducto().getId(), dp.getCantidad());
                String message = objectMapper.writeValueAsString(stock);
                log.info("Enviando {}", message);
                rabbitTemplate.convertAndSend(RabbitMQConfig.STOCK_UPDATE_QUEUE, message);
            }  catch (Exception e) {
                log.error("Error serializando mensaje para RabbitMQ", e);
            }

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

    public void deletePedidoNumero(Integer num) {
        pedidoRepository.deleteByNumeroPedido(num);
    }

    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido getPedidoPorNumero(Integer id) throws Exception {
        return pedidoRepository.findByNumeroPedido(id);
    }

    public List<Pedido> obtenerPedidosPorCliente(String clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Pedido actualizarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Boolean verificarYActualizarStock(List<OrdenCompraDetalle> detalles) {
        // URL del servicio de productos
        String url = "http://ms-gateway-svc:8080/productos/api/productos/consumo";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (OrdenCompraDetalle orden : detalles) {
            StockUpdateDTO stock =new StockUpdateDTO(orden.getProducto().getId(),orden.getCantidad());
            HttpEntity<StockUpdateDTO> requestEntity = new HttpEntity<>(stock, headers);
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Boolean.class);
            if (!Boolean.TRUE.equals(response.getBody())) {
                return false;
            }
        }
        return true;
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

    public Pedido entregarPedido(String id){
        Pedido pedido = pedidoRepository.findById(id).get();
        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);
        return pedido;
    }

    public Pedido enviarMensajeDevolverStock(String id) {
        Pedido pedido = pedidoRepository.findById(id).get();
        pedido.setEstado(EstadoPedido.CANCELADO);
        for (OrdenCompraDetalle detalle : pedido.getDetalle()) {
            StockUpdateDTO stockUpdateDTO = new StockUpdateDTO(detalle.getProducto().getId(),detalle.getCantidad());
            // Send the message to RabbitMQ
            rabbitTemplate.convertAndSend("devolverStockQueue", stockUpdateDTO);
            log.info("Mensaje enviado a la cola devolverStockQueue: {}", stockUpdateDTO);
        }
        pedidoRepository.save(pedido);
        return pedido;
    }
}
