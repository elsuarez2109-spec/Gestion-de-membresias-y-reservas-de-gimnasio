package com.gimnasio.usuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.*;
class AutenticacionServiceTest {
    private AutenticacionService servicio;
    @BeforeEach
    void preparar() {
        PasswordHasher hasher = new PasswordHasher();
        SesionManager sesiones = new SesionManager(Clock.systemUTC(), Duration.ofHours(1));
        servicio = new AutenticacionService(hasher, sesiones);
    }
    @Test
    void registraYDevuelveElRolCorrecto() {
        Usuario u = servicio.registrar(Rol.CLIENTE, "Ana", "ana@mail.com", "secreto1");
        assertInstanceOf(Cliente.class, u);
        assertEquals(Rol.CLIENTE, u.getRol());
    }
    @Test
    void laContrasenaNoSeGuardaEnTextoPlano() {
        Usuario u = servicio.registrar(Rol.CLIENTE, "Ana", "ana@mail.com", "secreto1");
        assertNotEquals("secreto1", u.getPasswordHash());
        assertFalse(u.getPasswordHash().contains("secreto1"));
    }
    @Test
    void mismaContrasenaGeneraHashesDistintos() {
        PasswordHasher h = new PasswordHasher();
        assertNotEquals(h.hash("secreto1"), h.hash("secreto1"));
    }
    @Test
    void inicioDeSesionCorrectoDevuelveToken() {
        servicio.registrar(Rol.CLIENTE, "Ana", "ana@mail.com", "secreto1");
        String token = servicio.iniciarSesion("ana@mail.com", "secreto1");
        assertTrue(servicio.usuarioDeSesion(token).isPresent());
    }
    @Test
    void contrasenaIncorrectaFalla() {
        servicio.registrar(Rol.CLIENTE, "Ana", "ana@mail.com", "secreto1");
        assertThrows(IllegalArgumentException.class,
                () -> servicio.iniciarSesion("ana@mail.com", "otra"));
    }
    @Test
    void noPermiteCorreoDuplicado() {
        servicio.registrar(Rol.CLIENTE, "Ana", "ana@mail.com", "secreto1");
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar(Rol.CLIENTE, "Otra", "ANA@mail.com", "secreto2"));
    }
    @Test
    void tokenInvalidoNoDaAcceso() {
        assertTrue(servicio.usuarioDeSesion("token-falso").isEmpty());
    }
    @Test
    void tokenExpiraConElTiempo() {
        PasswordHasher hasher = new PasswordHasher();
        MutableClock reloj = new MutableClock(Instant.parse("2026-01-01T10:00:00Z"));
        SesionManager sesiones = new SesionManager(reloj, Duration.ofMinutes(30));
        AutenticacionService s = new AutenticacionService(hasher, sesiones);
        s.registrar(Rol.CLIENTE, "Ana", "ana@mail.com", "secreto1");
        String token = s.iniciarSesion("ana@mail.com", "secreto1");
        assertTrue(s.usuarioDeSesion(token).isPresent());
        reloj.avanzar(Duration.ofMinutes(31));
        assertTrue(s.usuarioDeSesion(token).isEmpty());
    }
    @Test
    void cadaFabricaCreaSuTipoDeUsuario() {
        PasswordHasher hasher = new PasswordHasher();
        assertInstanceOf(Cliente.class,
                new ClienteFactory().registrar("Ana", "ana@mail.com", "secreto1", hasher));
        assertInstanceOf(Instructor.class,
                new InstructorFactory().registrar("Luis", "luis@mail.com", "secreto1", hasher));
        assertInstanceOf(Administrador.class,
                new AdministradorFactory().registrar("Eva", "eva@mail.com", "secreto1", hasher));
    }
    static class MutableClock extends Clock {
        private Instant ahora;
        MutableClock(Instant inicio) { this.ahora = inicio; }
        void avanzar(Duration d) { ahora = ahora.plus(d); }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId z) { return this; }
        @Override public Instant instant() { return ahora; }
    }
}