package isi.dan.ms_productos.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import isi.dan.ms_productos.aop.LogExecutionTime;
import isi.dan.ms_productos.dto.DescuentoUpdateDTO;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.EchoClientFeign;
import isi.dan.ms_productos.servicio.ProductoService;

@CrossOrigin(origins = "http://localhost")
@RestController
@RequestMapping("/api/productos")

public class ProductoController {
    @Autowired
    private ProductoService productoService;

    Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Value("${dan.productos.instancia}")
    private String instancia;

    @Autowired
    EchoClientFeign echoSvc;

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
  
    }
    
    @PostMapping
    @LogExecutionTime
    public ResponseEntity<Producto> createProducto(@RequestBody @Validated Producto producto) {

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

    @GetMapping("/todos")
    @LogExecutionTime
    public List<Producto> getAllProductos() {
        return productoService.getAllProductos();
    }

    @GetMapping("/id")
    @LogExecutionTime
    public ResponseEntity<Producto> getProductoById(@RequestParam Long id) throws ProductoNotFoundException {
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    @GetMapping("/nombre")
    @LogExecutionTime
    public ResponseEntity<List<Producto>> getProductoByNombre(@RequestParam String nombre) throws ProductoNotFoundException {
        return ResponseEntity.ok(productoService.getProductoByNombre(nombre));
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/consumo")
    @LogExecutionTime
    public ResponseEntity<Boolean> actualizarStock(@RequestBody @Validated StockUpdateDTO stockUpdateDto) throws Exception{    
        if(productoService.consumirStock(stockUpdateDto)){  
            return ResponseEntity.ok(true);            
        }
        else{
            return ResponseEntity.ok(false);
        }
    }

    @PutMapping("/provision")
    @LogExecutionTime
    public ResponseEntity<Boolean> reponerStock(@RequestBody @Validated StockUpdateDTO stockUpdateDto) throws Exception{        
        return ResponseEntity.ok(productoService.reponerStock(stockUpdateDto));

    }

    /*
     * @PutMapping("/precio")
     * 
     * @LogExecutionTime
     * public ResponseEntity<Producto> actualizarPrecio(@RequestBody @Validated
     * StockUpdateDTO stockUpdateDTO)
     * throws ProductoNotFoundException {
     * Producto producto =
     * productoService.getProductoById(stockUpdateDTO.getIdProducto());
     * 
     * if (producto == null)
     * throw new ProductoNotFoundException(stockUpdateDTO.getIdProducto());
     * 
     * // Actualizamos el precio del producto
     * producto.setPrecioInicial(BigDecimal.valueOf(stockUpdateDTO.getCantidad()));
     * // Asumiendo que 'cantidad' es el
     * // precio
     * 
     * // Guardamos los cambios
     * Producto updatedProducto = productoService.saveProducto(producto);
     * 
     * return ResponseEntity.ok(updatedProducto);
     * }
     */

    @PutMapping("/descuento")
    @LogExecutionTime
    public ResponseEntity<Producto> actualizarDescuentoPromocional(
            @RequestBody @Validated DescuentoUpdateDTO descuentoUpdateDto) throws ProductoNotFoundException {
        Producto producto = productoService.getProductoById(descuentoUpdateDto.getIdProducto());
 
        if (producto == null)
            throw new ProductoNotFoundException(descuentoUpdateDto.getIdProducto());

        // Actualizamos el descuento promocional del producto
        producto.setDescuentoPromocional(descuentoUpdateDto.getDescuentoPromocional());

        // Guardamos los cambios
        Producto updatedProducto = productoService.saveProducto(producto);

        return ResponseEntity.ok(updatedProducto);

    }

    @PutMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Producto> editar(@PathVariable final Long id, @RequestBody Producto producto) throws ProductoNotFoundException{
        producto.setId(id);
        return ResponseEntity.ok(productoService.saveProducto(producto));
    }

}