package isi.dan.msclientes.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.dao.UsuarioHabilitadoRepository;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.UsuarioHabilitado;

@Service
public class UsuarioHabilitadoService {
    @Autowired
    private UsuarioHabilitadoRepository usuarioHabilitadoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    public List<UsuarioHabilitado> findAll() {
        return usuarioHabilitadoRepository.findAll();
    }

    public Optional<UsuarioHabilitado> findById(Integer id) {
        return usuarioHabilitadoRepository.findById(id);
    }

    public List<UsuarioHabilitado> findByClienteId(Integer id) {
        return usuarioHabilitadoRepository.findByClienteId(id);
    }

    public Optional<Cliente> findClienteById(Integer idUsuario) {
        Optional<UsuarioHabilitado> usuario = usuarioHabilitadoRepository.findById(idUsuario);
        if (usuario.isPresent()) {
            return clienteRepository.findById(usuario.get().getCliente());
        } else {
            return Optional.empty(); // O lanzar una excepción personalizada
        }
    }

    public UsuarioHabilitado save(UsuarioHabilitado usuarioHabilitado) {
        return usuarioHabilitadoRepository.save(usuarioHabilitado);
    }

    public UsuarioHabilitado update(UsuarioHabilitado usuarioHabilitado) {
        return usuarioHabilitadoRepository.save(usuarioHabilitado);
    }

    public void deleteById(Integer id) {
        usuarioHabilitadoRepository.deleteById(id);
    }

}