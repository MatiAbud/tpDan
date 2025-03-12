package isi.dan.ms_productos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import isi.dan.ms_productos.dto.DescuentoUpdateDTO;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.EchoClientFeign; // Agregado
import isi.dan.ms_productos.servicio.ProductoService;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private EchoClientFeign echoClientFeign; // Agregado

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto;

    @BeforeEach
    void setup() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setStockActual(10);
        producto.setPrecio(BigDecimal.valueOf(100));
        producto.setDescuentoPromocional(0); // Aseguramos que el descuento inicial es 0
    }

    @Test
    void testCreateProducto() throws Exception {
        producto.setStockActual(0);
        Mockito.when(productoService.saveProducto(Mockito.any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Test"))
                .andExpect(jsonPath("$.stockActual").value(0)) // Verifica que el stock sea 0 al crear
                .andExpect(jsonPath("$.descuentoPromocional").value(0)); // Verifica que el descuento sea 0
    }

    @Test
    void testGetAllProductos() throws Exception {
        Mockito.when(productoService.getAllProductos()).thenReturn(Collections.singletonList(producto));

        mockMvc.perform(get("/api/productos/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto Test"))
                .andExpect(jsonPath("$[0].precio").value(100));
    }

    @Test
    void testGetProductoById() throws Exception {
        Mockito.when(productoService.getProductoById(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/id")
                .param("id", "1")) // Usamos param para pasar el ID
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Test"))
                .andExpect(jsonPath("$.precio").value(100));
    }

    @Test
    void testDeleteProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productoService, Mockito.times(1)).deleteProducto(1L); // Verificamos la llamada al servicio
    }

    @Test
    void testActualizarStock_Provision() throws Exception {
        StockUpdateDTO stockUpdateDTO = new StockUpdateDTO(1L, 5, BigDecimal.valueOf(120));
        Mockito.when(productoService.reponerStock(Mockito.any(StockUpdateDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/productos/provision")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); // Verificamos que se devuelve true
    }

    @Test
    void testActualizarStock_Consumo() throws Exception {
        StockUpdateDTO stockUpdateDTO = new StockUpdateDTO(1L, 5, BigDecimal.valueOf(120));
        Mockito.when(productoService.consumirStock(Mockito.any(StockUpdateDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/productos/consumo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); // Verificamos que se devuelve true
    }

    @Test
    void testActualizarDescuentoPromocional() throws Exception {
        DescuentoUpdateDTO descuentoUpdateDTO = new DescuentoUpdateDTO(1L, 10);
        producto.setDescuentoPromocional(10);

        Mockito.when(productoService.getProductoById(1L)).thenReturn(producto);
        Mockito.when(productoService.saveProducto(Mockito.any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/descuento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(descuentoUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descuentoPromocional").value(10));
    }

    @Test
    void testEditarProducto() throws Exception {
        Mockito.when(productoService.saveProducto(Mockito.any(Producto.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Test"))
                .andExpect(jsonPath("$.precio").value(100));
    }

    @Test
    void testGetEcho() throws Exception {
        Mockito.when(echoClientFeign.echo()).thenReturn("Echo from Feign");

        mockMvc.perform(get("/api/productos/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Echo from Feign"));
    }
}