
package reservas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

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

    // PRUEBA 5: No reservar cuando la clase esta llena
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
    }

    // Metodo auxiliar para crear clases
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

