package com.gimnasio;
import com.gimnasio.fachada.GimnasioFacade;
import com.gimnasio.facturacion.FacturacionService;
import com.gimnasio.facturacion.MembresiaService;
import com.gimnasio.reservas.ObservadorCupo;
import com.gimnasio.reservas.ReservaService;
import com.gimnasio.usuarios.AutenticacionService;
import com.gimnasio.usuarios.PasswordHasher;
import com.gimnasio.usuarios.Rol;
import com.gimnasio.usuarios.SesionManager;

import java.time.Clock;
import java.time.Duration;
public final class Aplicacion {

    private static final int HORAS_CANCELACION = 2; 
    private static final int LIMITE_INFRACCIONES = 3; 

    private Aplicacion() {}

    public static GimnasioFacade crearNucleo(ObservadorCupo notificador) {
        Clock reloj = Clock.systemDefaultZone();

        PasswordHasher hasher = new PasswordHasher();
        SesionManager sesiones = new SesionManager(reloj, Duration.ofHours(2));
        AutenticacionService autenticacion = new AutenticacionService(hasher, sesiones);

        MembresiaService membresias = new MembresiaService(new FacturacionService(), reloj);
        ReservaService reservas = new ReservaService(
                membresias::tieneMembresiaVigente, HORAS_CANCELACION, LIMITE_INFRACCIONES, reloj);
        reservas.registrarObservador(notificador);

        autenticacion.registrar(Rol.ADMINISTRADOR, "Administrador", "admin@gym.com", "admin123");

        return new GimnasioFacade(autenticacion, reservas, membresias);
    }
}
