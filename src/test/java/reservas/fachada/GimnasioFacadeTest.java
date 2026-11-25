package com.gimnasio.fachada;

import com.gimnasio.reservas.ClaseGrupal;
import com.gimnasio.reservas.Reserva;
import com.gimnasio.reservas.ReservaService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;


public class GimnasioFacadeTest {

    @Test
    public void probarReservaDesdeFachada() {

        // Crear el servicio de reservas
        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3
        );

        // Crear la fachada
        GimnasioFacade gimnasio =
                new GimnasioFacade(servicio);

        // Crear una clase grupal
        LocalDateTime inicio =
                LocalDateTime.now().plusDays(1);

        ClaseGrupal yoga = new ClaseGrupal(
                "CLASE01",
                "Yoga",
                "Instructor Juan",
                inicio,
                inicio.plusHours(1),
                2
        );

        // Programar la clase usando la fachada
        gimnasio.programarClase(yoga);

        // Reservar usando la fachada
        Reserva reserva = gimnasio.reservarClase(
                "CLASE01", "CLIENTE_A"
        );

        // Comprobar que la reserva fue confirmada
        assertEquals(
                Reserva.Estado.CONFIRMADA,
                reserva.getEstado()
        );

        // Comprobar que queda un cupo
        assertEquals(1, yoga.getCuposDisponibles());
    }

    @Test
    public void probarCancelacionDesdeFachada() {

        ReservaService servicio = new ReservaService(
                clienteId -> true, 4, 3
        );

        GimnasioFacade gimnasio =
                new GimnasioFacade(servicio);

        LocalDateTime inicio =
                LocalDateTime.now().plusDays(1);

        ClaseGrupal yoga = new ClaseGrupal(
                "CLASE02",
                "Yoga",
                "Instructor Juan",
                inicio,
                inicio.plusHours(1),
                1
        );

        gimnasio.programarClase(yoga);

        Reserva reserva = gimnasio.reservarClase(
                "CLASE02", "CLIENTE_A"
        );

        gimnasio.cancelarReserva(
                "CLASE02", reserva.getId()
        );

        assertEquals(
                Reserva.Estado.CANCELADA,
                reserva.getEstado()
        );

        assertEquals(1, yoga.getCuposDisponibles());
    }
}
