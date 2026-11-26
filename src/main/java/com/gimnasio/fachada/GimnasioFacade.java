package com.gimnasio.fachada;

import com.gimnasio.facturacion.MembresiaService;
import com.gimnasio.facturacion.Plan;
import com.gimnasio.facturacion.ResultadoCobro;
import com.gimnasio.reservas.ClaseGrupal;
import com.gimnasio.reservas.Reserva;
import com.gimnasio.reservas.ReservaService;
import com.gimnasio.usuarios.AutenticacionService;
import com.gimnasio.usuarios.Rol;
import com.gimnasio.usuarios.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GimnasioFacade {

    private final AutenticacionService autenticacion;
    private final ReservaService reservaService;
    private final MembresiaService membresias;

    public GimnasioFacade(AutenticacionService autenticacion,
                          ReservaService reservaService,
                          MembresiaService membresias) {
        if (autenticacion == null || reservaService == null || membresias == null) {
            throw new IllegalArgumentException("Los servicios son obligatorios");
        }
        this.autenticacion = autenticacion;
        this.reservaService = reservaService;
        this.membresias = membresias;
    }
    public Usuario registrarCliente(String nombre, String correo, String password) {
        return autenticacion.registrar(Rol.CLIENTE, nombre, correo, password);
    }

    public Usuario registrarUsuario(String token, Rol rol, String nombre,
                                    String correo, String password) {
        exigirRol(token, Rol.ADMINISTRADOR);
        return autenticacion.registrar(rol, nombre, correo, password);
    }

    public String iniciarSesion(String correo, String password) {
        return autenticacion.iniciarSesion(correo, password);
    }

    public void cerrarSesion(String token) {
        autenticacion.cerrarSesion(token);
    }

    public Rol rolDe(String token) {
        return usuarioActual(token).getRol();
    }

    public String nombreDe(String token) {
        return usuarioActual(token).getNombre();
    }

    public void programarClase(String token, ClaseGrupal clase) {
        exigirRol(token, Rol.ADMINISTRADOR);
        reservaService.programarClase(clase);
    }

    public ClaseGrupal crearClase(String token, String nombre, String correoInstructor,
                                  LocalDateTime inicio, LocalDateTime fin, int aforo) {
        exigirRol(token, Rol.ADMINISTRADOR);
        Usuario instructor = autenticacion.buscarPorCorreo(correoInstructor)
                .filter(u -> u.getRol() == Rol.INSTRUCTOR)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un instructor con ese correo"));
        ClaseGrupal clase = new ClaseGrupal(UUID.randomUUID().toString(), nombre,
                instructor.getNombre(), inicio, fin, aforo);
        reservaService.programarClase(clase);
        return clase;
    }

    public ResultadoCobro asignarPlan(String token, String correoCliente, String tipoPlan,
                                      int miembros, List<String> beneficios, int meses) {
        exigirRol(token, Rol.ADMINISTRADOR);
        Usuario cliente = autenticacion.buscarPorCorreo(correoCliente)
                .filter(u -> u.getRol() == Rol.CLIENTE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un cliente con ese correo"));
        return membresias.asignar(cliente.getId(), tipoPlan, miembros, beneficios, meses);
    }

    public List<String> beneficiosDisponibles() {
        return membresias.beneficiosDisponibles();
    }

    public List<ClaseGrupal> listarClases(String token) {
        usuarioActual(token);
        return reservaService.listarClases();
    }

    public Reserva reservarClase(String token, String claseId) {
        Usuario u = exigirRol(token, Rol.CLIENTE);
        return reservaService.reservar(claseId, u.getId());
    }

    public boolean entrarListaEspera(String token, String claseId) {
        Usuario u = exigirRol(token, Rol.CLIENTE);
        return reservaService.unirseListaEspera(claseId, u.getId());
    }

    public void cancelarReserva(String token, String claseId, String reservaId) {
        Usuario u = exigirRol(token, Rol.CLIENTE);
        Reserva r = reservaService.buscarClase(claseId).buscarReserva(reservaId);
        if (!r.getClienteId().equals(u.getId())) {
            throw new SecurityException("Esa reserva no es tuya");
        }
        reservaService.cancelarReserva(claseId, reservaId);
    }

    public List<Reserva> misReservas(String token) {
        Usuario u = exigirRol(token, Rol.CLIENTE);
        return reservaService.reservasDeCliente(u.getId());
    }

    public Optional<Plan> miPlan(String token) {
        Usuario u = exigirRol(token, Rol.CLIENTE);
        return membresias.planDe(u.getId());
    }

    public int consultarInfracciones(String token) {
        Usuario u = exigirRol(token, Rol.CLIENTE);
        return reservaService.contarInfracciones(u.getId());
    }

    public boolean puedeReservar(String token) {
        Usuario u = exigirRol(token, Rol.CLIENTE);
        return reservaService.puedeReservar(u.getId());
    }

    public List<Reserva> reservasDeClase(String token, String claseId) {
        exigirRol(token, Rol.INSTRUCTOR);
        return reservaService.reservasDeClase(claseId);
    }

    public void registrarAsistencia(String token, String claseId, String reservaId) {
        exigirRol(token, Rol.INSTRUCTOR);
        reservaService.registrarAsistencia(claseId, reservaId);
    }

    public void registrarNoAsistencia(String token, String claseId, String reservaId) {
        exigirRol(token, Rol.INSTRUCTOR);
        reservaService.registrarNoAsistencia(claseId, reservaId);
    }

    private Usuario usuarioActual(String token) {
        return autenticacion.usuarioDeSesion(token)
                .orElseThrow(() -> new SecurityException("Sesión inválida o expirada"));
    }

    private Usuario exigirRol(String token, Rol rolRequerido) {
        Usuario u = usuarioActual(token);
        if (u.getRol() != rolRequerido) {
            throw new SecurityException("No tienes permiso para esta acción");
        }
        return u;
    }
}