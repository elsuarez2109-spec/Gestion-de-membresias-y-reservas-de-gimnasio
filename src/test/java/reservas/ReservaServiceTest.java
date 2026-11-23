
package reservas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class ReservaServiceTest {

    @Test
    public void probarReservaYListaEspera() {

        // 1. Crear el servicio de reservas
        ReservaService servicio = new ReservaService(
                clienteId -> true,
                4,
                3
        );

        // 2. Crear una clase con un solo cupo
        ClaseGrupal yoga = new ClaseGrupal(
                "CLASE01",
                "Yoga",
                "Instructor Juan",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                1
        );

        servicio.programarClase(yoga);

        // 3. El cliente A reserva
        Reserva reservaA = servicio.reservar(
                "CLASE01", "CLIENTE_A"
        );

        assertEquals(
                Reserva.Estado.CONFIRMADA,
                reservaA.getEstado()
        );

        assertEquals(0, yoga.getCuposDisponibles());

        // 4. El cliente B entra a lista de espera
        assertTrue(servicio.unirseListaEspera(
                "CLASE01", "CLIENTE_B"
        ));

        // 5. El cliente A cancela
        servicio.cancelarReserva(
                "CLASE01", reservaA.getId()
        );

        assertEquals(
                Reserva.Estado.CANCELADA,
                reservaA.getEstado()
        );

        // 6. Verificar asignacion automatica
        assertTrue(yoga.getListaEspera().estaVacia());

        assertEquals(0, yoga.getCuposDisponibles());

        // B ya tiene una reserva confirmada
        assertThrows(
                IllegalStateException.class,
                () -> yoga.reservar("CLIENTE_B")
        );
    }
}
