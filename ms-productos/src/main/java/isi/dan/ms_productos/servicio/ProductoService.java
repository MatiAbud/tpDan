package isi.dan.ms_productos.servicio;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import isi.dan.ms.pedidos.modelo.Pedido;
import isi.dan.ms_productos.conf.RabbitMQConfig;
import isi.dan.ms_productos.dao.ProductoRepository;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    Logger log = LoggerFactory.getLogger(ProductoService.class);

    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE)
    public void handleStockUpdate(Message msg) {
        log.info("Recibido {}", msg);
        try {
            // Deserializar el mensaje JSON a un LinkedHashMap
            ObjectMapper mapper = new ObjectMapper();
            LinkedHashMap<String, Object> messageData = mapper.readValue(msg.getBody(), LinkedHashMap.class);

            // Obtener ID de producto y cantidad
            Long productId = (Long) messageData.get("idProducto");
            Integer cantidad = (Integer) messageData.get("cantidad");
            BigDecimal precio = (BigDecimal) messageData.get("precio");

            // Crear objeto DTO
            StockUpdateDTO stockUpdate = new StockUpdateDTO(productId, cantidad, precio);

            // Buscar el producto
            Producto producto = productoRepository.findById(stockUpdate.getIdProducto())
                    .orElseThrow(() -> new ProductoNotFoundException(stockUpdate.getIdProducto()));

            // Actualizar el stock
            producto.setStockActual(stockUpdate.getCantidad());
            productoRepository.save(producto);
            log.info("Stock actualizado para el producto ID: {} a {}", producto.getId(), producto.getStockActual());

            // verificar el punto de pedido y generar un pedido
            if (producto.getStockActual() <= producto.getStockMinimo()) {
                log.info("El stock está por debajo del mínimo. Generando nuevo pedido...");

                // Generar un nuevo pedido
                Pedido nuevoPedido = new Pedido();
                nuevoPedido.setProducto(producto);
                nuevoPedido.setCantidad(producto.getStockMinimo() - producto.getStockActual());
                nuevoPedido.setPrecio(producto.getPrecio());

                // Guardar el nuevo pedido (asumiendo que tienes un PedidoRepository o similar)
                // pedidoRepository.save(nuevoPedido);
                log.info("Nuevo pedido generado: {}", nuevoPedido);
            }

        } catch (Exception e) {
            log.error("Error al procesar la actualización de stock", e);
        }
    }

    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Producto getProductoById(Long id) throws ProductoNotFoundException {
        return productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
    }

    public void deleteProducto(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow();

        // eliminamos productos de la base de datos
        productoRepository.delete(producto);
    }
}
