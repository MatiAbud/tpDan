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
import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.exception.MaxObrasExceededException;
import isi.dan.msclientes.exception.ObraFinalizadaException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;

public class ObraServiceTest {

    @Mock
    private ObraRepository obraRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ObraService obraService;

    private Obra obra;
    private Obra obra2;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        obra = new Obra();
        obra2 = new Obra();
        obra.setDireccion("pruebaHabilitada");
        obra.setEsRemodelacion(true);
        obra.setPresupuesto(BigDecimal.valueOf(150));
        obra.setId(1);
        obra.setLat(10);
        obra.setLng(10);

        obra2.setDireccion("pruebaPendiente");
        obra2.setEsRemodelacion(true);
        obra2.setPresupuesto(BigDecimal.valueOf(150));
        obra2.setId(2);
        obra2.setLat(10);
        obra2.setLng(10);

        cliente = new Cliente();
        cliente.setId(3);
        cliente.setNombre("pruebaMax1");
        cliente.setCorreoElectronico("Prueba@asdsa.com");
        cliente.setCuit("123");
        cliente.setMaxObrasEnEjecucion(1);
        cliente.setMaximoDescubierto(BigDecimal.valueOf(10000));

        obra.setIdCliente(cliente.getId());
        obra2.setIdCliente(cliente.getId());

        System.out.println("SetUp executed: " + obra);
    }

    @Test
    public void testFindAll() {
        when(obraRepository.findAll()).thenReturn(Arrays.asList(obra, obra2));
        List<Obra> obras = obraService.findAll();
        assertEquals(2, obras.size());
    }

    @Test
    public void testFindById_ObraExists() {
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        Optional<Obra> found = obraService.findById(1);
        assertTrue(found.isPresent());
        assertEquals("pruebaHabilitada", found.get().getDireccion());
    }

    @Test
    public void testFindById_ObraNotFound() {
        when(obraRepository.findById(3)).thenReturn(Optional.empty());
        Optional<Obra> found = obraService.findById(3);
        assertFalse(found.isPresent());
    }

    @Test
    public void testSave() {
        when(obraRepository.save(any(Obra.class))).thenReturn(obra);
        Obra saved = obraService.save(obra);
        assertEquals(EstadoObra.PENDIENTE, saved.getEstado());
    }

    @Test
    public void testUpdate() {
        when(obraRepository.save(any(Obra.class))).thenReturn(obra);
        Obra updated = obraService.update(obra);
        assertEquals("pruebaHabilitada", updated.getDireccion());
    }

    @Test
    public void testDeleteById() {
        obraService.deleteById(1);
        verify(obraRepository, times(1)).deleteById(1);
    }

    @Test
    public void testMarcarObraComoHabilitada() throws Exception {
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        obraService.marcarObraComoHabilitada(1);
        assertEquals(EstadoObra.HABILITADA, obra.getEstado());
    }

    @Test
    public void testMarcarObraComoFinalizada() throws Exception {
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        obraService.marcarObraComoFinalizada(1);
        assertEquals(EstadoObra.FINALIZADA, obra.getEstado());
    }

    @Test
    public void testMarcarObraComoPendiente() throws Exception {
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        obraService.marcarObraComoPendiente(1);
        assertEquals(EstadoObra.PENDIENTE, obra.getEstado());
    }

    @Test
    public void testObtenerObrasDeCliente() throws Exception {
        when(obraRepository.findByIdCliente(3)).thenReturn(Arrays.asList(obra, obra2));
        List<Obra> obras = obraService.obtenerObrasDeCliente(3);
        assertEquals(2, obras.size());
    }

    @Test
    public void testCambiarEstadoObra_Finalizada() {
        obra.setEstado(EstadoObra.FINALIZADA);
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        assertThrows(Exception.class, () -> obraService.cambiarEstadoObra(1, EstadoObra.HABILITADA));
    }

    @Test
    public void testCambiarEstadoObra_MaxObras() throws Exception {
        obra.setEstado(EstadoObra.PENDIENTE);
        obra2.setEstado(EstadoObra.HABILITADA);
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        when(clienteRepository.findById(3)).thenReturn(Optional.of(cliente));
        when(obraRepository.findByIdCliente(3)).thenReturn(Arrays.asList(obra, obra2));
        assertThrows(Exception.class, () -> obraService.cambiarEstadoObra(1, EstadoObra.HABILITADA));
    }

    @Test
    public void testCambiarEstadoObra_Success() throws Exception {
        obra.setEstado(EstadoObra.PENDIENTE);
        when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        when(clienteRepository.findById(3)).thenReturn(Optional.of(cliente));
        when(obraRepository.findByIdCliente(3)).thenReturn(Arrays.asList(obra));
        when(obraRepository.save(any(Obra.class))).thenReturn(obra);
        Obra changed = obraService.cambiarEstadoObra(1, EstadoObra.HABILITADA);
        assertEquals(EstadoObra.HABILITADA, changed.getEstado());
    }

    @Test
    public void testAsignarObraACliente_MaxObras() throws Exception {
        obra.setEstado(EstadoObra.HABILITADA);
        obra2.setEstado(EstadoObra.HABILITADA);
        cliente.setObrasClientes(Arrays.asList(obra, obra2)); // Agrega las obras a obrasClientes
        when(clienteRepository.findById(3)).thenReturn(Optional.of(cliente));
        assertThrows(MaxObrasExceededException.class, () -> obraService.asignarObraACliente(3, obra));
    }

    @Test
    public void testAsignarObraACliente_Finalizada() {
        obra.setEstado(EstadoObra.FINALIZADA);
        when(clienteRepository.findById(3)).thenReturn(Optional.of(cliente));
        assertThrows(ObraFinalizadaException.class, () -> obraService.asignarObraACliente(3, obra));
    }

    @Test
    public void testAsignarObraACliente_Success() throws Exception {
        obra.setEstado(EstadoObra.PENDIENTE);
        when(clienteRepository.findById(3)).thenReturn(Optional.of(cliente));
        when(obraRepository.save(any(Obra.class))).thenReturn(obra);
        Obra assigned = obraService.asignarObraACliente(3, obra);
        assertEquals(3, assigned.getIdCliente());
    }
}