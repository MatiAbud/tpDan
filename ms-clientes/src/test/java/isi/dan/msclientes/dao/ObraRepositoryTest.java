package isi.dan.msclientes.dao;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Answers.valueOf;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.netflix.discovery.converters.Auto;

import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.servicios.ClienteService;

@DataJpaTest
@Testcontainers
@ActiveProfiles("db")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ObraRepositoryTest {

    Logger log = LoggerFactory.getLogger(ObraRepositoryTest.class);

    @SuppressWarnings("resource")
    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ObraRepository obraRepository;

    @Autowired 
    ClienteRepository clienteRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }
    private Obra obra;

    private Cliente cliente;

    @BeforeEach
    void iniciarDatos() {
        obra = new Obra();
        obra.setDireccion("Test Obra 999");
        obra.setPresupuesto(BigDecimal.valueOf(100));
        obra.setEsRemodelacion(true);
        obra.setEstado(EstadoObra.HABILITADA);
        obra.setLat(12);
        obra.setLng(432);
        obra.setId(1);
        obraRepository.save(obra);
         
        cliente= new Cliente();
        cliente.setId(1);
        cliente.setCuit("123");
        cliente.setCorreoElectronico("asa@asfs.com");
        cliente.setMaxObrasEnEjecucion(3);
        cliente.setMaximoDescubierto(10000);
        cliente.setNombre("prueba");
        clienteRepository.save(cliente);
       
        
        obra.setIdCliente(cliente.getId());
    }

    @BeforeEach
    void borrarDatos() {
        obraRepository.deleteAll();
    }

    @AfterAll
    static void stopContainer() {
        mysqlContainer.stop();
    }

    @Test
    void testSaveAndFindById() {
        Obra obra = new Obra();
        obra.setDireccion("Test Obra");
        obra.setPresupuesto(BigDecimal.valueOf(150));
        obraRepository.save(obra);

        Optional<Obra> foundObra = obraRepository.findById(obra.getId());
        log.info("ENCONTRE: {} ", foundObra);
        assertThat(foundObra).isPresent();
        assertThat(foundObra.get().getDireccion()).isEqualTo("Test Obra");
    }

    @Test
    void testFindByPresupuesto() {
        Obra obra = new Obra();
        obra.setDireccion("Test Obra");
        obra.setPresupuesto(BigDecimal.valueOf(200));
        obraRepository.save(obra);

        List<Obra> resultado = obraRepository.findByPresupuestoGreaterThanEqual(BigDecimal.valueOf(50));
        log.info("ENCONTRE: {} ", resultado);
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado.get(0).getPresupuesto()).isGreaterThan(BigDecimal.valueOf(50));
        assertThat(resultado.get(1).getPresupuesto()).isGreaterThan(BigDecimal.valueOf(50));
    }

   /* @Test
    void testFindByClienteId(){
        obraRepository.save(obra);

        List<Obra> resultado= obraRepository.findByClienteId(cliente.getId());
        log.info("Encontre: {} ", resultado);
        assertThat(resultado.size()).isEqualTo(1);
        assertThat(resultado.get(0).getPresupuesto()).isEqualTo(obra.getPresupuesto());
    }
 */
}
