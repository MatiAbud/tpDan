package isi.dan.msclientes.model;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoObra {
    HABILITADA,
    PENDIENTE,
    FINALIZADA;

    public static Set<EstadoObra> getValidEstados() {
        return EnumSet.allOf(EstadoObra.class);
    }
}