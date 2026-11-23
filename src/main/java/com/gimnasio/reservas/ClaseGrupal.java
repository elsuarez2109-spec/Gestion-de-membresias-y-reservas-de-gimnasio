package reservas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClaseGrupal {

    private String id;
    private String nombre;
    private String instructor;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private int aforoMaximo;

    private List<Reserva> reservas;

    public ClaseGrupal(String id, String nombre,
                       String instructor,
                       LocalDateTime inicio,
                       LocalDateTime fin,
                       int aforoMaximo) {

        if (aforoMaximo <= 0) {
            throw new IllegalArgumentException(
                "El aforo debe ser mayor que cero"
            );
        }

        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException(
                "La hora final debe ser posterior al inicio"
            );
        }

        this.id = id;
        this.nombre = nombre;
        this.instructor = instructor;
        this.inicio = inicio;
        this.fin = fin;
        this.aforoMaximo = aforoMaximo;

        this.reservas = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getInstructor() {
        return instructor;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public int getAforoMaximo() {
        return aforoMaximo;
    }

    public int getCuposDisponibles() {

        int ocupados = 0;

        for (Reserva reserva : reservas) {
            if (reserva.getEstado()
                    == Reserva.Estado.CONFIRMADA) {
                ocupados++;
            }
        }

        return aforoMaximo - ocupados;
    }

    public boolean hayCupo() {
        return getCuposDisponibles() > 0;
    }
}
