package isi.dan.msclientes.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.config.ClienteConfig;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteConfig clienteConfig;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        System.out.println(clienteConfig.getMaximoDescubierto());
        cliente.setMaximoDescubierto(clienteConfig.getMaximoDescubierto());
        return clienteRepository.save(cliente);
    }

    public Cliente update(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }
}
