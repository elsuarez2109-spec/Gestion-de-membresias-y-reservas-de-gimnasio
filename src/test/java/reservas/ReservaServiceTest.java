
package reservas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class ReservaServiceTest {

    // PRUEBA 1: Reserva y lista de espera
    @Test
    public void probarReservaYListaEspera() {

        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3
        );

        ClaseGrupal yoga = crearClase("CLASE01", 1);
        servicio.programarClase(yoga);

        Reserva reservaA = servicio.reservar(
                "CLASE01", "CLIENTE_A"
        );

        assertEquals(
                Reserva.Estado.CONFIRMADA,
                reservaA.getEstado()
        );

        assertEquals(0, yoga.getCuposDisponibles());

        assertTrue(servicio.unirseListaEspera(
                "CLASE01", "CLIENTE_B"
        ));

        servicio.cancelarReserva(
                "CLASE01", reservaA.getId()
        );

        assertEquals(
                Reserva.Estado.CANCELADA,
                reservaA.getEstado()
        );

        assertTrue(yoga.getListaEspera().estaVacia());
        assertEquals(0, yoga.getCuposDisponibles());

        assertThrows(
                IllegalStateException.class,
                () -> yoga.reservar("CLIENTE_B")
        );
    }

    // PRUEBA 2: No permitir reservas duplicadas
    @Test
    public void noPermitirReservaDuplicada() {

        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3
        );

        ClaseGrupal yoga = crearClase("CLASE02", 3);
        servicio.programarClase(yoga);

        servicio.reservar("CLASE02", "CLIENTE_A");

        assertThrows(
                IllegalStateException.class,
                () -> servicio.reservar(
                        "CLASE02", "CLIENTE_A"
                )
        );

        assertEquals(2, yoga.getCuposDisponibles());
    }

    // PRUEBA 3: Membresia invalida
    @Test
    public void noPermitirMembresiaInvalida() {

        ReservaService servicio = new ReservaService(
                clienteId -> false, 4, 3
        );

        ClaseGrupal yoga = crearClase("CLASE03", 5);
        servicio.programarClase(yoga);

        assertThrows(
                IllegalStateException.class,
                () -> servicio.reservar(
                        "CLASE03", "CLIENTE_A"
                )
        );

        assertEquals(5, yoga.getCuposDisponibles());
    }

    // PRUEBA 4: Cancelacion de reserva
    @Test
    public void cancelarReservaCorrectamente() {

        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3
        );

        ClaseGrupal yoga = crearClase("CLASE04", 2);
        servicio.programarClase(yoga);

        Reserva reserva = servicio.reservar(
                "CLASE04", "CLIENTE_A"
        );

        servicio.cancelarReserva(
                "CLASE04", reserva.getId()
        );

        assertEquals(
                Reserva.Estado.CANCELADA,
                reserva.getEstado()
        );

        assertEquals(2, yoga.getCuposDisponibles());
    }

    // PRUEBA 5: No reservar cuando no hay cupos
    @Test
    public void noReservarSinCupos() {

        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3
        );

        ClaseGrupal yoga = crearClase("CLASE05", 1);
        servicio.programarClase(yoga);

        servicio.reservar("CLASE05", "CLIENTE_A");

        assertThrows(
                IllegalStateException.class,
                () -> servicio.reservar(
                        "CLASE05", "CLIENTE_B"
                )
        );

        assertEquals(0, yoga.getCuposDisponibles());
    }

    // PRUEBA 6: Infraccion por cancelacion tardia
    @Test
    public void registrarInfraccionPorCancelacionTardia() {

        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3
        );

        LocalDateTime inicio =
                LocalDateTime.now().plusHours(2);

        ClaseGrupal yoga = new ClaseGrupal(
                "CLASE06",
                "Yoga",
                "Instructor Juan",
                inicio,
                inicio.plusHours(1),
                2
        );

        servicio.programarClase(yoga);

        Reserva reserva = servicio.reservar(
                "CLASE06", "CLIENTE_A"
        );

        servicio.cancelarReserva(
                "CLASE06", reserva.getId()
        );

        assertEquals(
                Reserva.Estado.CANCELADA,
                reserva.getEstado()
        );

        assertEquals(
                1,
                servicio.contarInfracciones("CLIENTE_A")
        );
    }

    // PRUEBA 7: Infraccion por no asistencia
    @Test
    public void registrarInfraccionPorNoAsistencia() {

        // Simulamos una hora posterior a la clase
        Clock reloj = Clock.fixed(
                Instant.parse("2026-11-22T15:00:00Z"),
                ZoneOffset.UTC
        );

        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3, reloj
        );

        // La clase termina a las 13:00
        ClaseGrupal yoga = new ClaseGrupal(
                "CLASE07",
                "Yoga",
                "Instructor Juan",
                LocalDateTime.of(2026, 11, 22, 12, 0),
                LocalDateTime.of(2026, 11, 22, 13, 0),
                2
        );

        servicio.programarClase(yoga);

        // Preparamos una reserva confirmada
        Reserva reserva = yoga.reservar("CLIENTE_A");

        // Registramos que el cliente no asistio
        servicio.registrarNoAsistencia(
                "CLASE07", reserva.getId()
        );

        // Comprobamos el estado de la reserva
        assertEquals(
                Reserva.Estado.NO_ASISTIO,
                reserva.getEstado()
        );

        // Comprobamos que se genero una infraccion
        assertEquals(
                1,
                servicio.contarInfracciones("CLIENTE_A")
        );
    }

    // PRUEBA 8: Bloqueo por infracciones
    @Test
    public void bloquearClientePorInfracciones() {

        // El limite es de 2 infracciones
        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 2
        );

        for (int i = 1; i <= 2; i++) {

            LocalDateTime inicio =
                    LocalDateTime.now().plusHours(2);

            String claseId = "BLOQUEO" + i;

            ClaseGrupal clase = new ClaseGrupal(
                    claseId,
                    "Yoga",
                    "Instructor Juan",
                    inicio,
                    inicio.plusHours(1),
                    2
            );

            servicio.programarClase(clase);

            Reserva reserva = servicio.reservar(
                    claseId, "CLIENTE_A"
            );

            servicio.cancelarReserva(
                    claseId, reserva.getId()
            );
        }

        assertEquals(
                2,
                servicio.contarInfracciones("CLIENTE_A")
        );

        assertTrue(
                servicio.estaBloqueado("CLIENTE_A")
        );

        assertFalse(
                servicio.puedeReservar("CLIENTE_A")
        );

        // Comprobamos que no puede reservar otra clase
        ClaseGrupal otraClase = crearClase("CLASE09", 3);
        servicio.programarClase(otraClase);

        assertThrows(
                IllegalStateException.class,
                () -> servicio.reservar(
                        "CLASE09", "CLIENTE_A"
                )
        );
    }

    // METODO AUXILIAR: Crear clases grupales
    private ClaseGrupal crearClase(String id, int cupos) {

        LocalDateTime inicio =
                LocalDateTime.now().plusDays(1);

        return new ClaseGrupal(
                id,
                "Yoga",
                "Instructor Juan",
                inicio,
                inicio.plusHours(1),
                cupos
        );
    }
}

