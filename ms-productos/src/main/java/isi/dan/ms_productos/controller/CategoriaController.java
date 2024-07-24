package isi.dan.ms_productos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import isi.dan.ms_productos.modelo.Categoria;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @GetMapping
    public Categoria[] getAllCategorias() {
        return Categoria.values();
    }
}
