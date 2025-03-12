package isi.dan.msclientes.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.UsuarioHabilitado;
import isi.dan.msclientes.servicios.UsuarioHabilitadoService;

@WebMvcTest(UsuarioHabilitadoController.class)
public class UsuarioHabilitadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioHabilitadoService usuarioService;

    private UsuarioHabilitado usuario;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Test Cliente");

        usuario = new UsuarioHabilitado();
        usuario.setId(1);
        usuario.setNombre("Test Usuario");
        usuario.setDni("12345678");
        usuario.setCliente(1);
    }

    @Test
    void testGetAll() throws Exception {
        Mockito.when(usuarioService.findAll()).thenReturn(Collections.singletonList(usuario));

        mockMvc.perform(get("/api/usuarios/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Test Usuario"));
    }

    @Test
    void testGetByCliente() throws Exception {
        Mockito.when(usuarioService.findByClienteId(1)).thenReturn(Collections.singletonList(usuario));

        mockMvc.perform(get("/api/usuarios/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombre").value("Test Usuario"));
    }

    @Test
    void testGetById() throws Exception {
        Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Test Usuario"));
    }

    @Test
    void testGetCliente() throws Exception {
        Mockito.when(usuarioService.findClienteById(1)).thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/api/usuarios/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre").value("Test Cliente"));
    }

    @Test
    void testCreate() throws Exception {
        Mockito.when(usuarioService.save(Mockito.any(UsuarioHabilitado.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test Usuario"));
    }

    @Test
    void testCreate_ClienteNull() throws Exception {
        UsuarioHabilitado usuarioSinCliente = new UsuarioHabilitado();
        usuarioSinCliente.setNombre("Usuario Sin Cliente");
        usuarioSinCliente.setDni("98765432");

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuarioSinCliente)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate() throws Exception {
        Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioService.update(Mockito.any(UsuarioHabilitado.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test Usuario"));
    }

    @Test
    void testDelete() throws Exception {
        Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(usuario));
        Mockito.doNothing().when(usuarioService).deleteById(1);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}