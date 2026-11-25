package com.gimnasio.usuarios;

public class AdministradorFactory extends UsuarioFactory {
    @Override
    protected Usuario crearUsuario(String id, String nombre, String correo, String hash) {
        return new Administrador(id, nombre, correo, hash);
    }
}
