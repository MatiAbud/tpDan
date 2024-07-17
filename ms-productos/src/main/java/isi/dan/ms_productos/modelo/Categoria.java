package isi.dan.ms_productos.modelo;

import java.util.Arrays;

public enum Categoria {
    CEMENTOS, PLACAS, PERFILES, MORTEROS, YESERIA;

    // implementa la logica para chequear si el valor del enum es valido
    public boolean isValid() {
        return this != null && Arrays.asList(values()).contains(this);
    }
}
