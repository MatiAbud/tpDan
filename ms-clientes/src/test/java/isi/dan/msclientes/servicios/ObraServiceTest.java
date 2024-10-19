package isi.dan.msclientes.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;

public class ObraServiceTest {

    @Mock
    private ObraRepository obraRepository;

    @InjectMocks
    private ObraService obraService;

    private Obra obra;
    private Obra obra2;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        obra = new Obra();
        obra2= new Obra();
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
        cliente.setMaximoDescubierto(10000);

        obra.setCliente(cliente);
        obra2.setCliente(cliente);
        
        System.out.println("SetUp executed: " + obra);
    }

    @Test
    public void testObraFinalizada() {
        obra.setEstado(EstadoObra.FINALIZADA);
        Mockito.when(obraRepository.findById(1)).thenReturn(Optional.of(obra));

        Exception exception = assertThrows(Exception.class, () -> {
            obraService.cambiarEstadoObra(1, EstadoObra.HABILITADA);
        });

        assertEquals("No se puede cambiar el estado de una obra FINALIZADA", exception.getMessage());
    }

    @Test
    public void testFinalizarObra() throws Exception{
        obra.setEstado(EstadoObra.HABILITADA);
        obra2.setEstado(EstadoObra.PENDIENTE);
        Mockito.when(obraRepository.findById(1)).thenReturn(Optional.of(obra));
        Mockito.when(obraRepository.findFirstByEstado(EstadoObra.PENDIENTE)).thenReturn(obra2);

        obraService.cambiarEstadoObra(1, EstadoObra.FINALIZADA);

        verify(obraRepository , times(1)).save(obra2);
        assertEquals(obra2.getEstado(), EstadoObra.HABILITADA);

    }

    /*  FALTA TEST DE MAXIMAS OBRAS EN EJECUCION, LA CONDICION SE VERIFICA
        EN CONTROLLER,HAY QUE CAMBIARLO
    @Test
    public void testMaxObrasEnEjecucion(){
        obra.setEstado(EstadoObra.PENDIENTE);
        obra2.setEstado(EstadoObra.HABILITADA);
        
        Mockito.when(obraRepository.f)
    }*/
}
