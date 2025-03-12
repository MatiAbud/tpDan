package isi.dan.msclientes.dao;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import isi.dan.msclientes.model.Cliente;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClienteRepositoryTest {

    Logger log = LoggerFactory.getLogger(ClienteRepositoryTest.class);

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    ClienteRepository clienteRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    private Cliente cliente;

    @BeforeEach
    void iniciarDatos() {
        cliente = new Cliente();
        cliente.setCuit("123");
        cliente.setCorreoElectronico("asa@asfs.com");
        cliente.setMaxObrasEnEjecucion(3);
        cliente.setMaximoDescubierto(BigDecimal.valueOf(10000));
        cliente.setNombre("prueba");

        clienteRepository.save(cliente);
    }

    @AfterEach
    void limpiarDatos() {
        clienteRepository.deleteAll();
    }

    @Test
    public void testFindById() {
        Optional<Cliente> resultado = clienteRepository.findById(cliente.getId());

        log.info("Encontré: {} ", resultado);

        assertThat(resultado)
                .isPresent()
                .get()
                .extracting(Cliente::getId)
                .isEqualTo(cliente.getId());
    }
}
