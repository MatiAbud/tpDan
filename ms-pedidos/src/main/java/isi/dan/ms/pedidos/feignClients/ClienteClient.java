package isi.dan.ms.pedidos.feignClients;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import isi.dan.ms.pedidos.modelo.Cliente;

@FeignClient(name = "clienteService", url = "http://ms-gateway-svc:8080/clientes/api/clientes")
public interface ClienteClient {
    @GetMapping("/{id}")
    Cliente obtenerCliente(@PathVariable Integer id);

    @GetMapping("/{id}/saldo")
    BigDecimal verificarSaldo(@PathVariable Integer id);

    @PutMapping("/{id}/saldo/{gasto}")
    ResponseEntity <Cliente> sumarSaldo(@PathVariable Integer id, @PathVariable BigDecimal gasto);

}