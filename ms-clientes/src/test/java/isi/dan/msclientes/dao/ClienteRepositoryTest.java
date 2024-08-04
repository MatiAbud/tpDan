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
public class ClienteRepositoryTest {

    Logger log = LoggerFactory.getLogger(ClienteRepositoryTest.class);

    @SuppressWarnings("resource")
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
        cliente= new Cliente();
        cliente.setId(1);
        cliente.setCuit("123");
        cliente.setCorreoElectronico("asa@asfs.com");
        cliente.setMaxObrasEnEjecucion(3);
        cliente.setMaximoDescubierto(10000);
        cliente.setNombre("prueba");
        //clienteRepository.save(cliente);
    }

    @BeforeEach
    void borrarDatos() {
        clienteRepository.deleteAll();
    }

    @AfterAll
    static void stopContainer() {
        mysqlContainer.stop();
    }

    @Test
    public void testFindById(){
        clienteRepository.save(cliente);
        Optional<Cliente> resultado=clienteRepository.findById(cliente.getId());
        log.info("Encontre: {} ", resultado);
        assertThat(resultado.get().getId().equals(cliente.getId()));
    }
}