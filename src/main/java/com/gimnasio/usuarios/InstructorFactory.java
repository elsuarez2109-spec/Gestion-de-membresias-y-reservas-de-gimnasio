package com.gimnasio.usuarios;

public class InstructorFactory extends UsuarioFactory {
    @Override
    protected Usuario crearUsuario(String id, String nombre, String correo, String hash) {
        return new Instructor(id, nombre, correo, hash);
    }
}
