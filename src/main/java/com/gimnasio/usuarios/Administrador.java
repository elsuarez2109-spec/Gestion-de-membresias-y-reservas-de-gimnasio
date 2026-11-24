package com.gimnasio.usuarios;

public class Administrador extends Usuario {
    public Administrador(String id, String nombre, String correo, String passwordHash) {
        super(id, nombre, correo, passwordHash);
    }
    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }
}
