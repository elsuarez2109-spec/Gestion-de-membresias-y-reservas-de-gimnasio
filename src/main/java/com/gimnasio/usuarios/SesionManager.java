package com.gimnasio.usuarios;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class SesionManager {
    private record Sesion(Usuario usuario, Instant expira) {}
    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final Clock reloj;
    private final Duration duracion;
    public SesionManager(Clock reloj, Duration duracion) {
        this.reloj = reloj;
        this.duracion = duracion;
    }
    public String abrir(Usuario usuario) {
        String token=UUID.randomUUID().toString();
        sesiones.put(token, new Sesion(usuario, reloj.instant().plus(duracion)));
        return token;
    }
    public Optional<Usuario> validar(String token) {
        if (token==null) return Optional.empty();
        Sesion s=sesiones.get(token);
        if (s==null) return Optional.empty();
        if (reloj.instant().isAfter(s.expira())) {
            sesiones.remove(token);
            return Optional.empty();
        }
        return Optional.of(s.usuario());
    }
    public void cerrar(String token) {
        sesiones.remove(token);
    }
}
