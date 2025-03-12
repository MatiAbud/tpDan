package isi.dan.msclientes.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import isi.dan.msclientes.config.ClienteConfig;
import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.dao.UsuarioHabilitadoRepository;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.exception.InvalidEmailException;
import isi.dan.msclientes.exception.MaxObrasExceededException;
import isi.dan.msclientes.exception.ObraNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private ObraService obraService;

    @Mock
    private ClienteConfig clienteConfig;

    @Mock
    private UsuarioHabilitadoRepository usuarioHabilitadoRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private Obra obra;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Test Cliente");
        cliente.setCorreoElectronico("test@cliente.com");
        cliente.setCuit("12998887776");
        cliente.setMaxObrasEnEjecucion(10);
        cliente.setMaximoDescubierto(BigDecimal.valueOf(1000));
        cliente.setSaldo(BigDecimal.valueOf(500));

        obra = new Obra();
        obra.setId(1);
        obra.setDireccion("Direccion Test Obra");
        obra.setEstado(EstadoObra.PENDIENTE);
        obra.setIdCliente(1);
    }

    @Test
    void testCrearCliente_ValidEmail() throws Exception {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.crearCliente(cliente);

        assertEquals("Test Cliente", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testCrearCliente_InvalidEmail() {
        cliente.setCorreoElectronico("correo.invalido");

        assertThrows(InvalidEmailException.class, () -> clienteService.crearCliente(cliente));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void testFindAll() {
        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(cliente));

        List<Cliente> resultado = clienteService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("Test Cliente", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void testFindById_ClienteExists() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Test Cliente", resultado.get().getNombre());
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_ClienteNotFound() {
        when(clienteRepository.findById(2)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.findById(2);

        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findById(2);
    }

    @Test
    void testSave() {
        when(clienteConfig.getMaximoDescubierto()).thenReturn(BigDecimal.valueOf(1000));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.save(cliente);

        assertEquals("Test Cliente", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testUpdate() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        Cliente resultado = clienteService.update(cliente);

        assertEquals("Test Cliente", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testDeleteById() {
        clienteService.deleteById(1);

        verify(clienteRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetSaldo() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        BigDecimal saldo = clienteService.getSaldo(1);

        assertEquals(BigDecimal.valueOf(500), saldo);
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    void testAsignarObra_Success() throws MaxObrasExceededException, ClienteNotFoundException {
        cliente.setObrasClientes(new ArrayList<>()); // Inicializa la lista
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(obraRepository.save(any(Obra.class))).thenReturn(obra);
        Obra resultado = clienteService.asignarObra(1, obra);
        assertEquals(1, resultado.getIdCliente());
        assertEquals(EstadoObra.PENDIENTE, resultado.getEstado());
        verify(obraRepository, times(1)).save(any(Obra.class));
    }

    @Test
    void testAsignarObra_MaxObrasExceeded() {
        cliente.setObrasClientes(new ArrayList<>()); // Inicializa la lista
        cliente.setMaxObrasEnEjecucion(0);
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        assertThrows(MaxObrasExceededException.class, () -> clienteService.asignarObra(1, obra));
        verify(obraRepository, never()).save(any(Obra.class));
    }

    @Test
    void testAsignarObra_ClienteNotFound() {
        when(clienteRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> clienteService.asignarObra(2, obra));
        verify(obraRepository, never()).save(any(Obra.class));
    }

    @Test
    void testCambiarEstadoObra_Success() throws ObraNotFoundException {
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        when(obraRepository.save(any(Obra.class))).thenReturn(obra);

        clienteService.cambiarEstadoObra(1, EstadoObra.HABILITADA);

        assertEquals(EstadoObra.HABILITADA, obra.getEstado());
        verify(obraRepository, times(1)).save(any(Obra.class));
    }

    @Test
    void testCambiarEstadoObra_ObraNotFound() {
        when(obraRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ObraNotFoundException.class, () -> clienteService.cambiarEstadoObra(2, EstadoObra.HABILITADA));
        verify(obraRepository, never()).save(any(Obra.class));
    }

    @Test
    void testVerificarSaldo_SufficientFunds() {
        // Configuración del cliente (saldo inicial y máximo descubierto)
        Cliente cliente = new Cliente();
        cliente.setSaldo(BigDecimal.valueOf(500)); // Saldo inicial
        cliente.setMaximoDescubierto(BigDecimal.valueOf(1000)); // Máximo descubierto
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        boolean resultado = clienteService.verificarSaldo(1, BigDecimal.valueOf(200)); // Gasto positivo que no excede
                                                                                       // el máximo descubierto

        assertTrue(resultado); // Verifica que el saldo es suficiente
        assertEquals(BigDecimal.valueOf(700), cliente.getSaldo()); // Verifica que el saldo se actualizó correctamente
        verify(clienteRepository, times(1)).save(any(Cliente.class)); // Verifica que se llamó a save()
    }

    @Test
    void testVerificarSaldo_GastoNegativo() { // Cambiado el nombre del test
        boolean resultado = clienteService.verificarSaldo(1, BigDecimal.valueOf(-1500)); // Gasto negativo

        assertFalse(resultado); // Verifica que se devuelve false para gastos negativos
        // No verificamos el saldo porque no debería cambiar con un gasto negativo
        verify(clienteRepository, never()).save(any(Cliente.class)); // Verifica que no se llamó a save()
    }

    @Test
    void testVerificarSaldo_SaldoInsuficiente() {
        Cliente cliente = new Cliente();
        cliente.setSaldo(BigDecimal.valueOf(100)); // Saldo inicial bajo
        cliente.setMaximoDescubierto(BigDecimal.valueOf(500)); // Máximo descubierto
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        boolean resultado = clienteService.verificarSaldo(1, BigDecimal.valueOf(600)); // Gasto mayor que saldo y máximo
                                                                                       // descubierto

        assertFalse(resultado); // Verifica que se devuelve false por saldo insuficiente
        // No verificamos el saldo porque no debería cambiar
        verify(clienteRepository, never()).save(any(Cliente.class)); // Verifica que no se llamó a save()
    }
}