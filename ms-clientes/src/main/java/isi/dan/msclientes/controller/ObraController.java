package isi.dan.msclientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import isi.dan.msclientes.aop.LogExecutionTime;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.servicios.ClienteService;
import isi.dan.msclientes.servicios.ObraService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;



@RestController
@CrossOrigin
@RequestMapping("/api/obras")
public class ObraController {

    @Autowired
    private ObraService obraService;
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @LogExecutionTime
    public List<Obra> getAll() {
        return obraService.findAll();
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Obra> getById(@PathVariable Integer id) {
        Optional<Obra> obra = obraService.findById(id);
        return obra.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Obra create(@Valid @RequestBody Obra obra) {
        return obraService.save(obra);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Obra> update(@PathVariable Integer id, @RequestBody Obra obra) {
        if (!obraService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        obra.setId(id);
        return ResponseEntity.ok(obraService.update(obra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!obraService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        obraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/habilitar")
    public ResponseEntity<Obra> marcarHabilitada(@PathVariable Integer id) {
        try {
            // Delegación al servicio para marcar la obra como pendiente
            Obra obraActualizada = obraService.cambiarEstadoObra(id,EstadoObra.HABILITADA);

            // Validación de la existencia de la obra
            if (obraActualizada == null) {
                return ResponseEntity.notFound().build();
            }
            // Retorno de la obra actualizada con código de estado OK (200)
            return ResponseEntity.ok(obraActualizada);
        } catch (Exception e) {
            // Manejo de excepciones generales
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Obra> marcarFinalizada(@PathVariable Integer id) {
        try {
            // Llamada al service para marcar la obra como finalizada
            Obra obraActualizada = obraService.cambiarEstadoObra(id,EstadoObra.FINALIZADA);

            // Validación de la existencia de la obra
            if (obraActualizada == null) {
                return ResponseEntity.notFound().build();
            }

            // Retorno de la obra actualizada con código de estado OK (200)
            return ResponseEntity.ok(obraActualizada);
        } catch (Exception e) {
            // Manejo de excepciones generales
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/pendiente") 
    public ResponseEntity<Obra> marcarPendiente(@PathVariable Integer id) {
        try {
            // Delegación al servicio para marcar la obra como pendiente
            Obra obraActualizada = obraService.cambiarEstadoObra(id,EstadoObra.PENDIENTE);

            // Validación de la existencia de la obra
            if (obraActualizada == null) {
                return ResponseEntity.notFound().build();
            }
            // Retorno de la obra actualizada con código de estado OK (200)
            return ResponseEntity.ok(obraActualizada);
        } catch (Exception e) {
            // Manejo de excepciones generales
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/clientes/{idCliente}")
    public ResponseEntity<Object> obtenerObrasDeCliente(@PathVariable Integer idCliente) {
        try {
            // Validación de entrada básica
            if (idCliente == null) {
                throw new IllegalArgumentException("El ID del cliente debe ser un Long válido");
            }

            // Delegación al servicio para obtener las obras del cliente
            List<Obra> obras = obraService.obtenerObrasDeCliente(idCliente);

            // Validación de la existencia del cliente
            if (obras.isEmpty()) {
                // Maneja la respuesta no encontrada con el cuerpo extraído si es necesario
                return ResponseEntity.notFound().build();
            }

            // Retorno de la lista de obras con código de estado OK
            return ResponseEntity.ok(obras);

        } catch (ClienteNotFoundException e) {
            // Manejo de la excepción de cliente no encontrado
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Manejo de excepciones generales
            return ResponseEntity.internalServerError().body("Error al obtener las obras del cliente");
        }
    }

    @PutMapping("/{id}/{idCliente}")
    public ResponseEntity<Obra> asignarCliente(@PathVariable Integer id, @PathVariable Integer idCliente) throws Exception {
        Optional<Obra> obra = obraService.findById(id);
        Optional<Cliente> cliente = clienteService.findById(idCliente);
        if(!cliente.isPresent()){
            throw new Exception();
        }
        List<Obra> obrasHabilitadasCliente = obraService.obtenerObrasDeCliente(cliente.get().getId());
            obrasHabilitadasCliente.stream()
                                    .filter(o -> o.getEstado().equals(EstadoObra.HABILITADA))
                                    .collect(Collectors.toList());
        if(obra.get().getEstado().equals(EstadoObra.HABILITADA)){
            if(obrasHabilitadasCliente.size()==cliente.get().getMaxObrasEnEjecucion()){
                throw new Exception("se alcanzo el max de obras ejecutadas");
            }
            obra.get().setIdCliente(cliente.get().getId());
            return ResponseEntity.ok(obraService.cambiarEstadoObra(id, EstadoObra.HABILITADA));
        }
        else if(obra.get().getEstado().equals(EstadoObra.PENDIENTE)){
            obra.get().setIdCliente(cliente.get().getId());
            return ResponseEntity.ok(obraService.cambiarEstadoObra(id, EstadoObra.PENDIENTE));
        }
        else{
            obra.get().setIdCliente(cliente.get().getId());
            return ResponseEntity.ok(obraService.cambiarEstadoObra(id, EstadoObra.FINALIZADA));
        }
        
    }
}
