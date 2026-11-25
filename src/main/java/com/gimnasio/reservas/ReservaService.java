package com.gimnasio.reservas;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ReservaService {

    private final Map<String, ClaseGrupal> clases =
            new HashMap<>();

    private final List<Infraccion> infracciones =
            new ArrayList<>();

    private final Predicate<String> membresiaValida;
    private final int horasCancelacion;
    private final int limiteInfracciones;
    private final Clock reloj;

    public ReservaService(
            Predicate<String> membresiaValida,
            int horasCancelacion,
            int limiteInfracciones) {

        this(membresiaValida, horasCancelacion,
                limiteInfracciones, Clock.systemDefaultZone());
    }

    public ReservaService(
            Predicate<String> membresiaValida,
            int horasCancelacion,
            int limiteInfracciones,
            Clock reloj) {

        if (membresiaValida == null || reloj == null
                || horasCancelacion < 0
                || limiteInfracciones <= 0) {
            throw new IllegalArgumentException(
                    "Configuracion invalida");
        }

        this.membresiaValida = membresiaValida;
        this.horasCancelacion = horasCancelacion;
        this.limiteInfracciones = limiteInfracciones;
        this.reloj = reloj;
    }

    
    public synchronized void programarClase(ClaseGrupal clase) {

    if (clase == null || clases.containsKey(clase.getId())) {
        throw new IllegalArgumentException(
                "Clase invalida o duplicada");
    }

    AsignadorListaEspera asignador =
            new AsignadorListaEspera(this::puedeReservar);

    clase.suscribir(asignador);

    clases.put(clase.getId(), clase);
}


    public synchronized ClaseGrupal buscarClase(String claseId) {
        ClaseGrupal clase = clases.get(claseId);

        if (clase == null) {
            throw new IllegalArgumentException(
                    "La clase no existe");
        }
        return clase;
    }

    public synchronized boolean estaBloqueado(String clienteId) {
        int cantidad = 0;

        for (Infraccion i : infracciones) {
            if (i.getClienteId().equals(clienteId)
                    && i.isActiva()) {
                cantidad++;
            }
        }

        return cantidad >= limiteInfracciones;
    }

    public synchronized boolean puedeReservar(String clienteId) {
        return clienteId != null
                && !clienteId.isBlank()
                && membresiaValida.test(clienteId)
                && !estaBloqueado(clienteId);
    }

    public synchronized Reserva reservar(
            String claseId, String clienteId) {

        if (!puedeReservar(clienteId)) {
            throw new IllegalStateException(
                    "Cliente sin permiso para reservar");
        }

        ClaseGrupal clase = buscarClase(claseId);

        if (!clase.getInicio().isAfter(
                LocalDateTime.now(reloj))) {
            throw new IllegalStateException(
                    "La clase ya comenzo");
        }

        return clase.reservar(clienteId);
    }

    public synchronized boolean unirseListaEspera(
            String claseId, String clienteId) {

        if (!puedeReservar(clienteId)) {
            throw new IllegalStateException(
                    "Cliente sin permiso para reservar");
        }

        ClaseGrupal clase = buscarClase(claseId);

        if (!clase.getInicio().isAfter(
                LocalDateTime.now(reloj))) {
            throw new IllegalStateException(
                    "La clase ya comenzo");
        }

        return clase.unirseListaEspera(clienteId);
    }

    public synchronized void cancelarReserva(
            String claseId, String reservaId) {

        ClaseGrupal clase = buscarClase(claseId);
        Reserva reserva = clase.buscarReserva(reservaId);

        if (reserva.getEstado() != Reserva.Estado.CONFIRMADA) {
            throw new IllegalStateException(
                    "La reserva no esta confirmada");
        }

        long minutos = Duration.between(
                LocalDateTime.now(reloj),
                clase.getInicio()).toMinutes();

        clase.cancelar(reservaId);

        if (minutos < horasCancelacion * 60L) {
            infracciones.add(new Infraccion(
                    reserva.getClienteId(),
                    reservaId,
                    Infraccion.Motivo.CANCELACION_TARDIA));
        }
    }

    public synchronized void registrarAsistencia(
            String claseId, String reservaId) {

        buscarClase(claseId).registrarAsistencia(reservaId);
    }

    public synchronized void registrarNoAsistencia(
            String claseId, String reservaId) {

        ClaseGrupal clase = buscarClase(claseId);
        Reserva reserva = clase.buscarReserva(reservaId);

        if (LocalDateTime.now(reloj).isBefore(clase.getFin())) {
            throw new IllegalStateException(
                    "La clase aun no ha terminado");
        }

        clase.registrarNoAsistencia(reservaId);

        infracciones.add(new Infraccion(
                reserva.getClienteId(),
                reservaId,
                Infraccion.Motivo.NO_ASISTENCIA));
    }

    public synchronized int contarInfracciones(String clienteId) {
        int cantidad = 0;

        for (Infraccion i : infracciones) {
            if (i.getClienteId().equals(clienteId)
                    && i.isActiva()) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public synchronized void registrarObservador(
            ObservadorCupo observador) {

        for (ClaseGrupal clase : clases.values()) {
            clase.suscribir(observador);
        }
    }
}
