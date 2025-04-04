package isi.dan.msclientes.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import isi.dan.msclientes.aop.LogExecutionTime;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.model.UsuarioHabilitado;
import isi.dan.msclientes.servicios.UsuarioHabilitadoService;
import jakarta.validation.Valid;


@RestController
@CrossOrigin
@RequestMapping("/api/usuarios")
public class UsuarioHabilitadoController {
    @Autowired
    private UsuarioHabilitadoService usuarioService;

    @GetMapping("/todos")
    @LogExecutionTime
    public List<UsuarioHabilitado> getAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/clientes/{id}")
    @LogExecutionTime
    public List<UsuarioHabilitado> getByCliente(@PathVariable Integer id) {
        return usuarioService.findByClienteId(id);
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<UsuarioHabilitado> getById(@PathVariable Integer id) {
        Optional<UsuarioHabilitado> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idUsuario}")
    @LogExecutionTime
    public ResponseEntity<Cliente> getCliente(@PathVariable Integer idUsuario) {
        Optional<Cliente> cliente = usuarioService.findClienteById(idUsuario);
        return cliente.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UsuarioHabilitado create(@Valid @RequestBody UsuarioHabilitado usuario) {
        if (usuario.getCliente() == null) {
            throw new IllegalArgumentException("El cliente_id no puede ser nulo");
        }
        return usuarioService.save(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioHabilitado> update(@PathVariable Integer id, @RequestBody UsuarioHabilitado usuario) {
        if (!usuarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        usuario.setId(id);
        return ResponseEntity.ok(usuarioService.update(usuario));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!usuarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
}