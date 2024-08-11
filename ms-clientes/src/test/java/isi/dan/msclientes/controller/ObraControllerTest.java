package isi.dan.msclientes.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import isi.dan.msclientes.model.EstadoObra;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.servicios.ClienteService;
import isi.dan.msclientes.servicios.ObraService;

@WebMvcTest(ObraController.class)
public class ObraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObraService obraService;

    @MockBean
    private ClienteService clienteService;

    private Obra obra;

    private Obra obra2;
    
    @BeforeEach
    void setUp() {
        obra = new Obra();
        obra.setEsRemodelacion(true);
        obra.setId(1);
        obra.setDireccion("Direccion Test Obra");
        obra.setPresupuesto(BigDecimal.valueOf(100));
        obra.setEstado(EstadoObra.HABILITADA);
        obra.setLat(45);
        obra.setLng(43);

        obra2 = new Obra();
        obra2.setEsRemodelacion(true);
        obra2.setId(2);
        obra2.setDireccion("Direccion Test Obra pendiente");
        obra2.setPresupuesto(BigDecimal.valueOf(100));
        obra2.setEstado(EstadoObra.PENDIENTE);
        obra2.setLat(45);
        obra2.setLng(43);
    }

    @Test
    void testGetAll() throws Exception {
        Mockito.when(obraService.findAll()).thenReturn(Collections.singletonList(obra));

        mockMvc.perform(get("/api/obras"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].direccion").value("Direccion Test Obra"));
    }

    @Test
    void testGetById() throws Exception {
        Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));

        mockMvc.perform(get("/api/obras/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.direccion").value("Direccion Test Obra"));
    }

    @Test
    void testCreate() throws Exception {
        Mockito.when(obraService.save(Mockito.any(Obra.class))).thenReturn(obra);

        mockMvc.perform(post("/api/obras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(obra)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Direccion Test Obra"));
    }

    @Test
    void testUpdate() throws Exception {
        Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
        Mockito.when(obraService.update(Mockito.any(Obra.class))).thenReturn(obra);

        mockMvc.perform(put("/api/obras/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(obra)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Direccion Test Obra"));
    }

    @Test
    void testDelete() throws Exception {
        Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
        Mockito.doNothing().when(obraService).deleteById(1);

        mockMvc.perform(delete("/api/obras/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testMarcarHabilitada()throws Exception{
        Mockito.when(obraService.findById(2)).thenReturn(Optional.of(obra2));
        Mockito.when(obraService.cambiarEstadoObra(2, EstadoObra.HABILITADA)).thenReturn(obra2);

        mockMvc.perform(put("/api/obras/2/habilitar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(obra2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Direccion Test Obra pendiente"));

    }

    @Test
    void testMarcarFinalizada()throws Exception{
        Mockito.when(obraService.findById(2)).thenReturn(Optional.of(obra2));
        Mockito.when(obraService.cambiarEstadoObra(2, EstadoObra.FINALIZADA)).thenReturn(obra2);

        mockMvc.perform(put("/api/obras/2/finalizar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(obra2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Direccion Test Obra pendiente"));

    }

    @Test
    void testMarcarPendiente()throws Exception{
        Mockito.when(obraService.findById(1)).thenReturn(Optional.of(obra));
        Mockito.when(obraService.cambiarEstadoObra(1, EstadoObra.PENDIENTE)).thenReturn(obra);

        mockMvc.perform(put("/api/obras/1/pendiente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(obra)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion").value("Direccion Test Obra"));

    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
