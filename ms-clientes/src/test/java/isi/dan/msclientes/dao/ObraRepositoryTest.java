package isi.dan.msclientes.dao;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ObraRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(ObraRepositoryTest.class);

    @SuppressWarnings("resource")
    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ObraRepository obraRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        mysqlContainer.start();
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    private Obra obra;
    private Cliente cliente;

    @BeforeEach
    void iniciarDatos() {
        log.info("Iniciando datos de prueba...");

        cliente = new Cliente();
        cliente.setCuit("123");
        cliente.setCorreoElectronico("cliente@ejemplo.com");
        cliente.setMaxObrasEnEjecucion(3);
        cliente.setMaximoDescubierto(BigDecimal.valueOf(10000));
        cliente.setNombre("Cliente Prueba");
        cliente = clienteRepository.save(cliente); // Asegurar ID generado

        obra = new Obra();
        obra.setDireccion("Test Obra 999");
        obra.setPresupuesto(BigDecimal.valueOf(100));
        obra.setEsRemodelacion(true);
        obra.setEstado(EstadoObra.HABILITADA);
        obra.setLat(12);
        obra.setLng(432);
        obra.setIdCliente(cliente.getId()); // Relacionar con cliente
        obra = obraRepository.save(obra); // Guardar después de asignar cliente
    }

    @AfterEach
    void borrarDatos() {
        log.info("Borrando datos de prueba...");
        obraRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @AfterAll
    static void stopContainer() {
        log.info("Deteniendo contenedor de Testcontainers...");
        mysqlContainer.stop();
    }

    @Test
    void testSaveAndFindById() {
        Obra nuevaObra = new Obra();
        nuevaObra.setDireccion("Test Obra");
        nuevaObra.setPresupuesto(BigDecimal.valueOf(150));
        nuevaObra = obraRepository.save(nuevaObra); // Guardar antes de buscar

        Optional<Obra> foundObra = obraRepository.findById(nuevaObra.getId());
        log.info("Obra encontrada: {} ", foundObra);
        assertThat(foundObra).isPresent();
        assertThat(foundObra.get().getDireccion()).isEqualTo("Test Obra");
    }

    @Test
    void testFindByPresupuesto() {
        Obra obraExtra = new Obra();
        obraExtra.setDireccion("Test Obra 2");
        obraExtra.setPresupuesto(BigDecimal.valueOf(200));
        obraRepository.save(obraExtra);

        List<Obra> resultado = obraRepository.findByPresupuestoGreaterThanEqual(BigDecimal.valueOf(50));
        log.info("Obras encontradas: {} ", resultado);
        assertThat(resultado.size()).isGreaterThanOrEqualTo(1);
        assertThat(resultado.get(0).getPresupuesto()).isGreaterThan(BigDecimal.valueOf(50));
    }

    @Test
    void testFindByClienteId() {
        List<Obra> resultado = obraRepository.findByIdCliente(cliente.getId());
        log.info("Obras encontradas para cliente: {} ", resultado);
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getPresupuesto()).isEqualTo(obra.getPresupuesto());
    }
}
