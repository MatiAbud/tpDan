package isi.dan.ms_productos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import isi.dan.ms_productos.aop.LogExecutionTime;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.EchoClientFeign;
import isi.dan.ms_productos.servicio.ProductoService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    EchoClientFeign echoSvc;

    @PostMapping
    @LogExecutionTime
    public ResponseEntity<Producto> createProducto(@RequestBody @Validated Producto producto) {
        // Validamos atributos
        if (producto.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser vacío");
        }
        if (producto.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("El producto debe tener una descripción");
        }
        if (producto.getCategoria().isValid()) {
            throw new IllegalArgumentException("Se debe seleccionar una categoria para el producto");
        }
        if (producto.getStockMinimo() == 0 || producto.getStockMinimo() <= 0) {
            producto.setStockMinimo(10);
        }
        if (producto.getPrecio() == null) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor que cero");
        }

        // Seteamos el descuento
        producto.setDescuentoPromocional(0);
        // Seteamos el valor incial de stock en 0
        producto.setStockActual(0);

        // guardamos el producto
        Producto savedProducto = productoService.saveProducto(producto);
        return ResponseEntity.ok(savedProducto);
    }

    @GetMapping("/test")
    @LogExecutionTime
    public String getEcho() {
        String resultado = echoSvc.echo();
        log.info("Log en test 1!!!! {}", resultado);
        return resultado;
    }

    @GetMapping("/test2")
    @LogExecutionTime
    public String getEcho2() {
        RestTemplate restTemplate = new RestTemplate();
        String gatewayURL = "http://ms-gateway-svc:8080";
        String resultado = restTemplate.getForObject(gatewayURL + "/clientes/api/clientes/echo", String.class);
        log.info("Log en test 2 {}", resultado);
        return resultado;
    }

    @GetMapping
    @LogExecutionTime
    public List<Producto> getAllProductos() {
        return productoService.getAllProductos();
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) throws ProductoNotFoundException {
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}