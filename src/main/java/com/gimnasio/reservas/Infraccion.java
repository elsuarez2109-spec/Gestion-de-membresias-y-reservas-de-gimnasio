package reservas;
import java.time.LocalDateTime;
import java.util.UUID;

public class Infraccion {

    public enum Motivo {
        CANCELACION_TARDIA,
        NO_ASISTENCIA
    }

    private final String id;
    private final String clienteId;
    private final String reservaId;
    private final Motivo motivo;
    private final LocalDateTime fecha;
    private boolean activa;

    public Infraccion(String clienteId,
                      String reservaId,
                      Motivo motivo) {

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                "El cliente es obligatorio"
            );
        }

        if (reservaId == null || reservaId.isBlank()) {
            throw new IllegalArgumentException(
                "La reserva es obligatoria"
            );
        }

        if (motivo == null) {
            throw new IllegalArgumentException(
                "El motivo es obligatorio"
            );
        }

        this.id = UUID.randomUUID().toString();
        this.clienteId = clienteId;
        this.reservaId = reservaId;
        this.motivo = motivo;
        this.fecha = LocalDateTime.now();
        this.activa = true;
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getReservaId() {
        return reservaId;
    }

    public Motivo getMotivo() {
        return motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isActiva() {
        return activa;
    }

    public void desactivar() {
        this.activa = false;
    }
}
