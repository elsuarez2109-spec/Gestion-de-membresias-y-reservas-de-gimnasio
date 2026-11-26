package com.gimnasio.ui.consola;

import com.gimnasio.Aplicacion;
import com.gimnasio.fachada.GimnasioFacade;
import com.gimnasio.facturacion.Plan;
import com.gimnasio.facturacion.ResultadoCobro;
import com.gimnasio.notificaciones.NotificadorConsola;
import com.gimnasio.reservas.ClaseGrupal;
import com.gimnasio.reservas.Reserva;
import com.gimnasio.usuarios.Rol;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsolaApp {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final GimnasioFacade fachada;
    private final Scanner in;
    private final PrintStream out;
    private String token;

    public ConsolaApp(GimnasioFacade fachada, InputStream entrada, PrintStream salida) {
        this.fachada = fachada;
        this.in = new Scanner(entrada);
        this.out = salida;
    }

    public void iniciar() {
        out.println("Gimnasio: membresías y reservas");
        boolean seguir = true;
        while (seguir) {
            try {
                seguir = (token == null) ? menuInicial() : menuPorRol();
            } catch (SecurityException e) {
                out.println("Acceso denegado: " + e.getMessage());
                if (e.getMessage() != null && e.getMessage().startsWith("Sesión")) token = null;
            } catch (IllegalArgumentException | IllegalStateException | DateTimeParseException e) {
                out.println("Error: " + e.getMessage());
            } catch (EntradaTerminada e) {
                seguir = false;
            }
        }
        out.println("Hasta pronto.");
    }

    private boolean menuInicial() {
        out.println("\n1. Iniciar sesión\n2. Registrarse como cliente\n0. Salir");
        switch (leer("Opción: ")) {
            case "1" -> {
                token = fachada.iniciarSesion(leer("Correo: "), leer("Contraseña: "));
                out.println("Bienvenido, " + fachada.nombreDe(token) + ".");
            }
            case "2" -> {
                fachada.registrarCliente(leer("Nombre: "), leer("Correo: "), leer("Contraseña: "));
                out.println("Registro exitoso. Ahora inicia sesión.");
            }
            case "0" -> { return false; }
            default -> out.println("Opción inválida");
        }
        return true;
    }

    private boolean menuPorRol() {
        Rol rol = fachada.rolDe(token);
        return switch (rol) {
            case ADMINISTRADOR -> menuAdministrador();
            case INSTRUCTOR -> menuInstructor();
            case CLIENTE -> menuCliente();
        };
    }

    private boolean menuAdministrador() {
        out.println("\n[ADMINISTRADOR] 1. Crear instructor  2. Programar clase  3. Asignar plan a cliente"
                + "  4. Ver clases  9. Cerrar sesión");
        switch (leer("Opción: ")) {
            case "1" -> {
                fachada.registrarUsuario(token, Rol.INSTRUCTOR, leer("Nombre: "),
                        leer("Correo: "), leer("Contraseña: "));
                out.println("Instructor creado.");
            }
            case "2" -> {
                ClaseGrupal c = fachada.crearClase(token, leer("Nombre de la clase: "),
                        leer("Correo del instructor: "),
                        fecha("Inicio (yyyy-MM-dd HH:mm): "), fecha("Fin (yyyy-MM-dd HH:mm): "),
                        entero("Aforo máximo: "));
                out.println("Clase programada: " + c.getNombre());
            }
            case "3" -> asignarPlan();
            case "4" -> mostrarClases();
            case "9" -> cerrarSesion();
            default -> out.println("Opción inválida");
        }
        return true;
    }

    private void asignarPlan() {
        String correo = leer("Correo del cliente: ");
        String tipo = leer("Plan (BASICO, PRO, ESTUDIANTE, GRUPAL): ");
        int miembros = tipo.trim().equalsIgnoreCase("GRUPAL") ? entero("Número de miembros: ") : 0;
        out.println("Beneficios disponibles: " + fachada.beneficiosDisponibles());
        String linea = leer("Beneficios separados por coma (vacío = ninguno): ");
        List<String> beneficios = new ArrayList<>();
        if (!linea.isBlank()) {
            Arrays.stream(linea.split(",")).map(String::trim).forEach(beneficios::add);
        }
        ResultadoCobro factura = fachada.asignarPlan(token, correo, tipo, miembros, beneficios,
                entero("Meses a pagar: "));
        out.println(factura);
    }

    private boolean menuInstructor() {
        out.println("\n[INSTRUCTOR] 1. Ver clases  2. Registrar asistencia  9. Cerrar sesión");
        switch (leer("Opción: ")) {
            case "1" -> mostrarClases();
            case "2" -> registrarAsistencia();
            case "9" -> cerrarSesion();
            default -> out.println("Opción inválida");
        }
        return true;
    }

    private void registrarAsistencia() {
        ClaseGrupal clase = elegirClase();
        List<Reserva> reservas = fachada.reservasDeClase(token, clase.getId());
        if (reservas.isEmpty()) {
            out.println("La clase no tiene reservas.");
            return;
        }
        for (int i = 0; i < reservas.size(); i++) {
            out.println((i + 1) + ". cliente " + corto(reservas.get(i).getClienteId())
                    + " - " + reservas.get(i).getEstado());
        }
        Reserva r = reservas.get(entero("Número de reserva: ") - 1);
        if (leer("¿Asistió? (s/n): ").equalsIgnoreCase("s")) {
            fachada.registrarAsistencia(token, clase.getId(), r.getId());
            out.println("Asistencia registrada.");
        } else {
            fachada.registrarNoAsistencia(token, clase.getId(), r.getId());
            out.println("Inasistencia registrada (genera infracción).");
        }
    }
    private boolean menuCliente() {
        out.println("\n[CLIENTE] 1. Ver clases  2. Reservar  3. Lista de espera  4. Mis reservas"
                + "  5. Cancelar reserva  6. Mi plan e infracciones  9. Cerrar sesión");
        switch (leer("Opción: ")) {
            case "1" -> mostrarClases();
            case "2" -> {
                fachada.reservarClase(token, elegirClase().getId());
                out.println("Reserva confirmada.");
            }
            case "3" -> {
                boolean ok = fachada.entrarListaEspera(token, elegirClase().getId());
                out.println(ok ? "Quedaste en la lista de espera." : "Ya estabas en la lista de espera.");
            }
            case "4" -> mostrarMisReservas();
            case "5" -> cancelar();
            case "6" -> {
                Optional<Plan> plan = fachada.miPlan(token);
                out.println(plan.map(p -> "Plan: " + p.getDescripcion()
                        + String.format(" ($%,.0f/mes)", p.getCostoMensual()))
                        .orElse("Aún no tienes un plan activo. Pide al administrador que te asigne uno."));
                out.println("Infracciones activas: " + fachada.consultarInfracciones(token)
                        + (fachada.puedeReservar(token) ? "" : "  (no puedes reservar)"));
            }
            case "9" -> cerrarSesion();
            default -> out.println("Opción inválida");
        }
        return true;
    }

    private void cancelar() {
        List<Reserva> mias = mostrarMisReservas();
        if (mias.isEmpty()) return;
        Reserva r = mias.get(entero("Número de reserva a cancelar: ") - 1);
        fachada.cancelarReserva(token, r.getClaseId(), r.getId());
        out.println("Reserva cancelada.");
    }

    private List<Reserva> mostrarMisReservas() {
        List<Reserva> mias = fachada.misReservas(token);
        if (mias.isEmpty()) out.println("No tienes reservas.");
        for (int i = 0; i < mias.size(); i++) {
            Reserva r = mias.get(i);
            out.println((i + 1) + ". " + nombreClase(r.getClaseId()) + " - " + r.getEstado());
        }
        return mias;
    }

    private void mostrarClases() {
        List<ClaseGrupal> clases = fachada.listarClases(token);
        if (clases.isEmpty()) out.println("No hay clases programadas.");
        for (int i = 0; i < clases.size(); i++) {
            ClaseGrupal c = clases.get(i);
            out.println((i + 1) + ". " + c.getNombre() + " | " + c.getInstructor() + " | "
                    + c.getInicio().format(FORMATO) + " - " + c.getFin().toLocalTime()
                    + " | cupos: " + c.getCuposDisponibles() + "/" + c.getAforoMaximo()
                    + " | en espera: " + c.getListaEspera().cantidad());
        }
    }

    private ClaseGrupal elegirClase() {
        List<ClaseGrupal> clases = fachada.listarClases(token);
        if (clases.isEmpty()) throw new IllegalStateException("No hay clases programadas");
        mostrarClases();
        int n = entero("Número de clase: ");
        if (n < 1 || n > clases.size()) throw new IllegalArgumentException("Clase inexistente");
        return clases.get(n - 1);
    }

    private String nombreClase(String claseId) {
        return fachada.listarClases(token).stream()
                .filter(c -> c.getId().equals(claseId))
                .map(c -> c.getNombre() + " (" + c.getInicio().format(FORMATO) + ")")
                .findFirst().orElse("clase desconocida");
    }

    private void cerrarSesion() {
        fachada.cerrarSesion(token);
        token = null;
        out.println("Sesión cerrada.");
    }

    private LocalDateTime fecha(String mensaje) {
        return LocalDateTime.parse(leer(mensaje), FORMATO);
    }

    private int entero(String mensaje) {
        try {
            return Integer.parseInt(leer(mensaje));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debes escribir un número");
        }
    }

    private String corto(String id) {
        return id.length() > 8 ? id.substring(0, 8) : id;
    }

    private String leer(String mensaje) {
        out.print(mensaje);
        if (!in.hasNextLine()) throw new EntradaTerminada();
        return in.nextLine().trim();
    }

    private static class EntradaTerminada extends RuntimeException {}

    public static void main(String[] args) {
        new ConsolaApp(Aplicacion.crearNucleo(new NotificadorConsola()), System.in, System.out).iniciar();
    }
}
