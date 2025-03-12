package isi.dan.ms_productos.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // Importante: Usar WebMvcTest
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import isi.dan.ms_productos.modelo.Categoria;

@WebMvcTest(CategoriaController.class) // Especificar el controlador a probar
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllCategorias() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(Categoria.CEMENTOS.name()))
                .andExpect(jsonPath("$[1]").value(Categoria.PLACAS.name()))
                .andExpect(jsonPath("$[2]").value(Categoria.PERFILES.name()))
                .andExpect(jsonPath("$[3]").value(Categoria.MORTEROS.name()))
                .andExpect(jsonPath("$[4]").value(Categoria.YESERIA.name()));
    }
}