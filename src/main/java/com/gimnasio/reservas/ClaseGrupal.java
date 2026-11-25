package com.gimnasio.reservas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClaseGrupal implements SujetoCupo {

    private final String id;
    private final String nombre;
    private final String instructor;
    private final LocalDateTime inicio;
    private final LocalDateTime fin;
    private final int aforoMaximo;

    private final List<Reserva> reservas = new ArrayList<>();
    private final ListaEspera listaEspera = new ListaEspera();

    private final List<ObservadorCupo> observadores =
            new CopyOnWriteArrayList<>();

    public ClaseGrupal(String id, String nombre,
            String instructor, LocalDateTime inicio,
            LocalDateTime fin, int aforoMaximo) {

        if (id == null || id.isBlank()
                || nombre == null || nombre.isBlank()
                || instructor == null || instructor.isBlank()
                || inicio == null || fin == null
                || !fin.isAfter(inicio)
                || aforoMaximo <= 0) {
            throw new IllegalArgumentException(
                    "Datos de clase invalidos");
        }

        this.id = id;
        this.nombre = nombre;
        this.instructor = instructor;
        this.inicio = inicio;
        this.fin = fin;
        this.aforoMaximo = aforoMaximo;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getInstructor() { return instructor; }
    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFin() { return fin; }
    public int getAforoMaximo() { return aforoMaximo; }
    public ListaEspera getListaEspera() { return listaEspera; }

    public synchronized int getCuposDisponibles() {
        int ocupados = 0;

        for (Reserva r : reservas) {
            if (r.getEstado() == Reserva.Estado.CONFIRMADA
                    || r.getEstado() == Reserva.Estado.ASISTIO) {
                ocupados++;
            }
        }
        return aforoMaximo - ocupados;
    }

    public synchronized boolean hayCupo() {
        return getCuposDisponibles() > 0;
    }

    public synchronized Reserva reservar(String clienteId) {
        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException("Cliente invalido");
        }

        if (!hayCupo()) {
            throw new IllegalStateException("No hay cupos");
        }

        for (Reserva r : reservas) {
            if (r.getClienteId().equals(clienteId)
                    && r.getEstado() == Reserva.Estado.CONFIRMADA) {
                throw new IllegalStateException(
                        "El cliente ya tiene una reserva");
            }
        }

        Reserva nueva = new Reserva(
                UUID.randomUUID().toString(), clienteId, id);

        reservas.add(nueva);
        return nueva;
    }

    public synchronized boolean unirseListaEspera(String clienteId) {
        if (hayCupo()) {
            throw new IllegalStateException(
                    "Todavia hay cupos disponibles");
        }

        for (Reserva r : reservas) {
            if (r.getClienteId().equals(clienteId)
                    && r.getEstado() == Reserva.Estado.CONFIRMADA) {
                throw new IllegalStateException(
                        "El cliente ya tiene una reserva");
            }
        }

        return listaEspera.agregar(clienteId);
    }

    public synchronized void cancelar(String reservaId) {
        Reserva reserva = buscarReserva(reservaId);
        reserva.cancelar();

        notificar(new EventoCupo(
                EventoCupo.Tipo.CUPO_LIBERADO,
                this, reserva.getClienteId()));
    }

    public synchronized Reserva buscarReserva(String reservaId) {
        for (Reserva r : reservas) {
            if (r.getId().equals(reservaId)) {
                return r;
            }
        }
        throw new IllegalArgumentException(
                "Reserva no encontrada");
    }

    public synchronized void registrarAsistencia(String reservaId) {
        buscarReserva(reservaId).registrarAsistencia();
    }

    public synchronized void registrarNoAsistencia(String reservaId) {
        buscarReserva(reservaId).registrarNoAsistencia();
    }

    @Override
    public void suscribir(ObservadorCupo observador) {
        if (observador != null) {
            observadores.add(observador);
        }
    }

    @Override
    public void desuscribir(ObservadorCupo observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificar(EventoCupo evento) {
        for (ObservadorCupo observador : observadores) {
            try {
                observador.alRecibirEvento(evento);
            } catch (RuntimeException e) {
                System.err.println(
                        "Error de observador: " + e.getMessage());
            }
        }
    }
}

