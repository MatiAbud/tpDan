package isi.dan.msclientes.servicios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.config.ClienteConfig;
import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.dao.UsuarioHabilitadoRepository;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.exception.InvalidEmailException;
import isi.dan.msclientes.exception.MaxObrasExceededException;
import isi.dan.msclientes.exception.ObraNotFoundException;
import isi.dan.msclientes.exception.UsuarioHabilitadoNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.model.UsuarioHabilitado;
import isi.dan.msclientes.model.UsuariosHabilitados;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private ClienteConfig clienteConfig;

    @Autowired
    private UsuarioHabilitadoRepository usuarioHabilitadoRepository;

    @Autowired
    private UsuariosHabilitados usuariosHabilitados;

    @Value("${cliente.maximo.descubierto.default:0}") // Valor por defecto 0 si no se encuentra en el properties
    private int maximoDescubiertoDefault;

    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public Cliente crearCliente(Cliente cliente) throws Exception {
        // 1. Validar correo electrónico
        if (!isValidEmail(cliente.getCorreoElectronico())) {
            throw new InvalidEmailException("El correo electrónico no es válido.");
        }

        // 2. Buscar UsuarioHabilitado (usando el ID que viene en el objeto Cliente)
        Integer idUsuarioHabilitado = cliente.getIdUsuarioHabilitado(); // Obtiene el ID del request
        if (idUsuarioHabilitado == null) {
            throw new IllegalArgumentException("Debe proporcionar el ID del usuario habilitado.");
        }

        UsuarioHabilitado usuario = usuarioHabilitadoRepository.findById(idUsuarioHabilitado)
                .orElseThrow(() -> new UsuarioHabilitadoNotFoundException("Usuario habilitado no encontrado"));

        // 3. Asignar UsuarioHabilitado al cliente
        cliente.setUsuarioHabilitado(usuario);

        // 4. Asignar valor por defecto a maximoDescubierto SI ES NULO ***
        if (cliente.getMaximoDescubierto() == null) {
            cliente.setMaximoDescubierto(maximoDescubiertoDefault);
        }

        // 5. Guardar cliente (SIN CAMBIOS)
        return clienteRepository.save(cliente);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        cliente.setMaximoDescubierto(clienteConfig.getMaximoDescubierto());
        return clienteRepository.save(cliente);
    }

    public Cliente update(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }

    public BigDecimal verificarSaldo(Integer clienteId) {
        // Obtener el cliente por ID
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        // Verificar si el saldo del cliente es suficiente para cubrir el pedido
        return cliente.getSaldo();
    }

    @Transactional // Add transactional annotation
    public Obra asignarObra(Integer clienteId, Obra obra) throws MaxObrasExceededException, ClienteNotFoundException {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado"));

        if (!cliente.puedeAgregarObra()) {
            throw new MaxObrasExceededException("El cliente ha alcanzado el máximo de obras en ejecución.");
        }

        obra.setCliente(cliente);
        obra.setEstado(EstadoObra.PENDIENTE); // Initial state
        return obraRepository.save(obra);
    }

    @Transactional
    public void cambiarEstadoObra(Integer obraId, EstadoObra nuevoEstado) throws ObraNotFoundException {
        Obra obra = obraRepository.findById(obraId)
                .orElseThrow(() -> new ObraNotFoundException("Obra no encontrada"));

        obra.setEstado(nuevoEstado);
        obraRepository.save(obra);
    }
}
