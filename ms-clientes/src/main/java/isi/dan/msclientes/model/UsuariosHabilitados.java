package isi.dan.msclientes.model;

import java.util.List;

public class UsuariosHabilitados {
    private List<UsuarioHabilitado> usuariosHabilitados;

    // Constructor, getters y setters

    public void agregarUsuarioHabilitado(UsuarioHabilitado usuario) {
        // Validaciones y lógica para agregar el usuario a la lista
        usuariosHabilitados.add(usuario);
    }

    public void removerUsuarioHabilitado(int usuarioId) {
        // Lógica para eliminar el usuario de la lista por su ID
        usuariosHabilitados.removeIf(usuario -> usuario.getId() == usuarioId);
    }

    public List<UsuarioHabilitado> obtenerUsuariosHabilitados() {
        return usuariosHabilitados;
    }

    public boolean esUsuarioHabilitado(int usuarioId) {
        // Lógica para verificar si el usuario está en la lista
        return usuariosHabilitados.stream()
                .anyMatch(usuario -> usuario.getId() == usuarioId);
    }
}
