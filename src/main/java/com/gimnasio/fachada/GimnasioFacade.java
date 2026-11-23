package fachada;

import reservas.ClaseGrupal;
import reservas.Reserva;
import reservas.ReservaService;

public class GimnasioFacade {

    private final ReservaService reservaService;

    public GimnasioFacade(ReservaService reservaService) {

        if (reservaService == null) {
            throw new IllegalArgumentException(
                "El servicio de reservas es obligatorio"
            );
        }

        this.reservaService = reservaService;
    }

    public void programarClase(ClaseGrupal clase) {
        reservaService.programarClase(clase);
    }

    public Reserva reservarClase(
            String claseId, String clienteId) {

        return reservaService.reservar(
                claseId, clienteId);
    }

    public boolean entrarListaEspera(
            String claseId, String clienteId) {

        return reservaService.unirseListaEspera(
                claseId, clienteId);
    }

    public void cancelarReserva(
            String claseId, String reservaId) {

        reservaService.cancelarReserva(
                claseId, reservaId);
    }

    public void registrarAsistencia(
            String claseId, String reservaId) {

        reservaService.registrarAsistencia(
                claseId, reservaId);
    }

    public void registrarNoAsistencia(
            String claseId, String reservaId) {

        reservaService.registrarNoAsistencia(
                claseId, reservaId);
    }

    public int consultarInfracciones(String clienteId) {
        return reservaService.contarInfracciones(clienteId);
    }

    public boolean clientePuedeReservar(String clienteId) {
        return reservaService.puedeReservar(clienteId);
    }
}
