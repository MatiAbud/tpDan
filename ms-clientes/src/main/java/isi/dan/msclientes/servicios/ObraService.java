package isi.dan.msclientes.servicios;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.exception.ObraFinalizadaException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;

@Service
public class ObraService {

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Obra> findAll() {
        return obraRepository.findAll();
    }

    public Optional<Obra> findById(Integer id) {
        return obraRepository.findById(id);
    }

    public Obra save(Obra obra) {
        obra.setEstado(EstadoObra.PENDIENTE);
        return obraRepository.save(obra);
    }

    public Obra update(Obra obra) {
        return obraRepository.save(obra);
    }

    public void deleteById(Integer id) {
        obraRepository.deleteById(id);
    }

    public Obra marcarObraComoHabilitada(Integer id) throws Exception {
        Obra obra = obraRepository.findById(id).orElseThrow();

        obra.setEstado(EstadoObra.HABILITADA);
        obraRepository.save(obra);

        return obra;
    }

    public Obra marcarObraComoFinalizada(Integer id) throws Exception {
        Obra obra = obraRepository.findById(id).orElseThrow();

        obra.setEstado(EstadoObra.FINALIZADA);
        obraRepository.save(obra);

        return obra;
    }

    public Obra marcarObraComoPendiente(Integer idObra) throws Exception {
        Obra obra = obraRepository.findById(idObra).orElseThrow();

        obra.setEstado(EstadoObra.PENDIENTE);
        return obraRepository.save(obra);
    }

    public List<Obra> obtenerObrasDeCliente(Integer idCliente) throws Exception {
        List<Obra> obras = obraRepository.findByClienteId(idCliente);

        /*if (obras.isEmpty()) {
            throw new Exception("No se encontraron obras para el cliente con ID: " + idCliente);
        }*/

        return obras;
    }

    // Otros métodos según necesidades
    public Obra cambiarEstadoObra(Integer idObra, EstadoObra nuevoEstado)
            throws Exception {

        Obra obra = obraRepository.findById(idObra)
                .orElseThrow();

        // Validamos nuevo estado
        if (!EstadoObra.getValidEstados().contains(nuevoEstado)) {
            try {
                throw new Exception("Estado no válido: " + nuevoEstado);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Chequeamos si el estado finalizado cambia a otro estado
        /*if (obra.getEstado().equals(EstadoObra.FINALIZADA) && !nuevoEstado.equals(EstadoObra.FINALIZADA)) {
            try {
                throw new Exception("No se puede cambiar el estado de una obra FINALIZADA");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        if (obra.getEstado().equals(EstadoObra.FINALIZADA)) {
            throw new Exception("No se puede cambiar el estado de una obra FINALIZADA");
        }

        if (nuevoEstado.equals(EstadoObra.FINALIZADA)) {
            // Verificar si hay obras pendientes para habilitar
            Obra obraPendiente = obraRepository.findFirstByEstado(EstadoObra.PENDIENTE);
            if (obraPendiente != null) {
                obraPendiente.setEstado(EstadoObra.HABILITADA);
                obraRepository.save(obraPendiente);
            }
        }
        // No debe supera el maximo
       /* if (nuevoEstado.equals(EstadoObra.HABILITADA)) {
            Cliente cliente = obra.getCliente();
            /*List<Obra> obrasHabilitadasCliente = this.obtenerObrasDeCliente(cliente.getId());
            obrasHabilitadasCliente.stream()
                                    .filter(o -> o.getEstado().equals(EstadoObra.HABILITADA))
                                    .collect(Collectors.toList());
            if (obrasHabilitadasCliente.size() == cliente.getMaxObrasEnEjecucion()) {
                try {
                    throw new Exception(
                            "No se pueden habilitar más obras para el cliente: " + cliente.getNombre()
                                    + ". Se alcanzó el máximo permitido de: " + cliente.getMaxObrasEnEjecucion());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/

        // Actualizamos estado
        obra.setEstado(nuevoEstado);

        // Guardamos la actualizacion del estado
        obra = obraRepository.save(obra);

        return obra;
    }

    public Obra asignarObraACliente(Integer idCliente, Obra obra) throws Exception {
        // recuperar estado habilitado del cliente
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ClassNotFoundException("Cliente no encontrado con ID: " + idCliente));

        // chequeamos si el cliente esta disponible
        // if (!cliente.isEnabled()) {
        // throw new Exception("El cliente no está habilitado para asignar obras: " +
        // idCliente);
        // }
        if(obra.getEstado()==EstadoObra.FINALIZADA){
            throw new ObraFinalizadaException("La obra se encuentra finalizada");
        }
        else{
            obra.setCliente(cliente);
            obra = obraRepository.save(obra);
            return obra;
        }

    }

}
