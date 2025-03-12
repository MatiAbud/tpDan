package isi.dan.msclientes.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.dao.UsuarioHabilitadoRepository;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.UsuarioHabilitado;

public class UsuarioHabilitadoServiceTest {

    @Mock
    private UsuarioHabilitadoRepository usuarioHabilitadoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private UsuarioHabilitadoService usuarioHabilitadoService;

    private UsuarioHabilitado usuario;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Test Cliente");
        cliente.setCorreoElectronico("test@cliente.com");
        cliente.setCuit("12345678901");
        cliente.setMaximoDescubierto(BigDecimal.valueOf(1000));
        cliente.setMaxObrasEnEjecucion(5);

        usuario = new UsuarioHabilitado();
        usuario.setId(1);
        usuario.setNombre("Test Usuario");
        usuario.setDni("12345678");
        usuario.setCliente(1); // Asumiendo que almacenas el ID del cliente

        System.out.println("SetUp executed: " + usuario);
    }

    @Test
    public void testFindAll() {
        when(usuarioHabilitadoRepository.findAll()).thenReturn(Arrays.asList(usuario));
        List<UsuarioHabilitado> usuarios = usuarioHabilitadoService.findAll();
        assertEquals(1, usuarios.size());
        assertEquals("Test Usuario", usuarios.get(0).getNombre());
    }

    @Test
    public void testFindById_UsuarioExists() {
        when(usuarioHabilitadoRepository.findById(1)).thenReturn(Optional.of(usuario));
        Optional<UsuarioHabilitado> found = usuarioHabilitadoService.findById(1);
        assertTrue(found.isPresent());
        assertEquals("Test Usuario", found.get().getNombre());
    }

    @Test
    public void testFindById_UsuarioNotFound() {
        when(usuarioHabilitadoRepository.findById(2)).thenReturn(Optional.empty());
        Optional<UsuarioHabilitado> found = usuarioHabilitadoService.findById(2);
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindByClienteId() {
        when(usuarioHabilitadoRepository.findByClienteId(1)).thenReturn(Arrays.asList(usuario));
        List<UsuarioHabilitado> usuarios = usuarioHabilitadoService.findByClienteId(1);
        assertEquals(1, usuarios.size());
        assertEquals("Test Usuario", usuarios.get(0).getNombre());
    }

    @Test
    public void testFindClienteById_ClienteExists() {
        when(usuarioHabilitadoRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        Optional<Cliente> found = usuarioHabilitadoService.findClienteById(1);
        assertTrue(found.isPresent());
        assertEquals("Test Cliente", found.get().getNombre());
    }

    @Test
    public void testFindClienteById_UsuarioNotFound() {
        when(usuarioHabilitadoRepository.findById(2)).thenReturn(Optional.empty());
        Optional<Cliente> cliente = usuarioHabilitadoService.findClienteById(2);
        assertFalse(cliente.isPresent()); // Verifica que el Optional esté vacío
    }

    @Test
    public void testSave() {
        when(usuarioHabilitadoRepository.save(any(UsuarioHabilitado.class))).thenReturn(usuario);
        UsuarioHabilitado saved = usuarioHabilitadoService.save(usuario);
        assertEquals("Test Usuario", saved.getNombre());
    }

    @Test
    public void testUpdate() {
        when(usuarioHabilitadoRepository.save(any(UsuarioHabilitado.class))).thenReturn(usuario);
        UsuarioHabilitado updated = usuarioHabilitadoService.update(usuario);
        assertEquals("Test Usuario", updated.getNombre());
    }

    @Test
    public void testDeleteById() {
        usuarioHabilitadoService.deleteById(1);
        verify(usuarioHabilitadoRepository, times(1)).deleteById(1);
    }
}