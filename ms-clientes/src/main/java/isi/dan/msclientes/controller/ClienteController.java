package isi.dan.msclientes.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import isi.dan.msclientes.aop.LogExecutionTime;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.exception.ErrorInfo;
import isi.dan.msclientes.exception.InvalidEmailException;
import isi.dan.msclientes.exception.UsuarioHabilitadoNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.servicios.ClienteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    Logger log = LoggerFactory.getLogger(ClienteController.class);

    @Value("${dan.clientes.instancia}")
    private String instancia;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/todos")
    @LogExecutionTime
    public List<Cliente> getAll() {
        return clienteService.findAll();
    }

    @GetMapping("/echo")
    @LogExecutionTime
    public String getEcho() {
        log.debug("Recibiendo un echo ----- {}", instancia);
        return Instant.now() + " - " + instancia;
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Cliente> getById(@PathVariable Integer id) throws ClienteNotFoundException {
        Optional<Cliente> cliente = clienteService.findById(id);
        return ResponseEntity
                .ok(cliente.orElseThrow(() -> new ClienteNotFoundException("Cliente " + id + " no encontrado")));
    }

    @GetMapping("/{id}/saldo")
    public BigDecimal getSaldo(@PathVariable Integer id) {
        return clienteService.verificarSaldo(id);
    }
    

    @PostMapping
    @LogExecutionTime
    public ResponseEntity<?> create(@RequestBody @Validated Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.crearCliente(cliente);
            return ResponseEntity.ok(nuevoCliente); // Devuelve el cliente creado
        } catch (UsuarioHabilitadoNotFoundException e) { // Captura la excepción específica primero
            Instant fecha = Instant.now();
            ErrorInfo error = new ErrorInfo(fecha, e.getMessage(), "Detalle del error", 400);
            return ResponseEntity.badRequest().body(error);
        } catch (InvalidEmailException e) { // Luego las otras excepciones específicas
            Instant fecha = Instant.now();
            ErrorInfo error = new ErrorInfo(fecha, e.getMessage(), "Detalle del error", 400);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) { // Finalmente, la excepción general
            Instant fecha = Instant.now();
            ErrorInfo error = new ErrorInfo(fecha, e.getMessage(), "Detalle del error", 500); // Otro código para
                                                                                              // errores generales
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Cliente> update(@PathVariable final Integer id, @RequestBody Cliente cliente)
            throws ClienteNotFoundException {
        if (!clienteService.findById(id).isPresent()) {
            throw new ClienteNotFoundException("Cliente " + id + " no encontrado");
        }
        cliente.setId(id);
        return ResponseEntity.ok(clienteService.update(cliente));
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Void> delete(@PathVariable Integer id) throws ClienteNotFoundException {
        if (!clienteService.findById(id).isPresent()) {
            throw new ClienteNotFoundException("Cliente " + id + " no encontrado para borrar");
        }
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/saldo/{gasto}")
    public ResponseEntity<Cliente> sumarSaldo(@PathVariable Integer id, @PathVariable BigDecimal gasto) {
        return ResponseEntity.ok(clienteService.sumarSaldo(id, gasto));
    }
}
