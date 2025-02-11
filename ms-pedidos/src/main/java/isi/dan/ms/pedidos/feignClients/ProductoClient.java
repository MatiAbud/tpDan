package isi.dan.ms.pedidos.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import isi.dan.ms.pedidos.modelo.Producto;

@FeignClient(name = "productoService", url = "http://localhost:3080")
public interface ProductoClient {
    @GetMapping("/productos/api/productos/{id}")
    Producto obtenerProducto(@PathVariable("id") Long id);
}
