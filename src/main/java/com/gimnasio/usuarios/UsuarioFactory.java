package com.gimnasio.usuarios;

import java.util.UUID;

public abstract class UsuarioFactory {
    protected abstract Usuario crearUsuario(String id, String nombre,String correo, String passwordHash);
    public Usuario registrar(String nombre, String correo, String password,PasswordHasher hasher) {
        if (nombre==null||nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (correo==null||!correo.contains("@"))
            throw new IllegalArgumentException("Correo inválido");
        if (password==null||password.length()< 6)
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        return crearUsuario(UUID.randomUUID().toString(), nombre, correo,hasher.hash(password));
    }
}