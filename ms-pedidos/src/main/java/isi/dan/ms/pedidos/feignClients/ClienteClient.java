package isi.dan.ms.pedidos.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import isi.dan.ms.pedidos.modelo.Cliente;

@FeignClient(name = "clienteService", url = "http://localhost:8080")
public interface ClienteClient {
    @GetMapping("/clientes/api/clientes/{id}")
    Cliente obtenerCliente(@PathVariable("id") Integer id);
}
