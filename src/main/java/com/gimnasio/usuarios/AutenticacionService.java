package com.gimnasio.usuarios;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AutenticacionService {
    private final Map<Rol, UsuarioFactory> fabricas = Map.of(
        Rol.ADMINISTRADOR, new AdministradorFactory(),
        Rol.INSTRUCTOR, new InstructorFactory(),
        Rol.CLIENTE, new ClienteFactory()
    );
    private final Map<String, Usuario> usuariosPorCorreo=new ConcurrentHashMap<>();
    private final PasswordHasher hasher;
    private final SesionManager sesiones;
    public AutenticacionService(PasswordHasher hasher, SesionManager sesiones) {
        this.hasher=hasher;
        this.sesiones=sesiones;
    }
    public Usuario registrar(Rol rol, String nombre, String correo, String password) {
        if (correo==null||correo.isBlank()) {
            throw new IllegalArgumentException("Correo inválido");
        }
        String clave = correo.toLowerCase();
        if (usuariosPorCorreo.containsKey(clave)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }
        UsuarioFactory fabrica = fabricas.get(rol);
        if (fabrica==null) {
            throw new IllegalArgumentException("Rol no soportado: " + rol);
        }
        Usuario usuario = fabrica.registrar(nombre, correo, password, hasher);
        usuariosPorCorreo.put(clave, usuario);
        return usuario;
    }
    public String iniciarSesion(String correo, String password) {
        Usuario usuario = usuariosPorCorreo.get(correo == null ? "" : correo.toLowerCase());
        if (usuario == null||!hasher.verificar(password, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return sesiones.abrir(usuario);
    }
    public Optional<Usuario> usuarioDeSesion(String token) {
        return sesiones.validar(token);
    }
    public void cerrarSesion(String token) {
        sesiones.cerrar(token);
    }
}
