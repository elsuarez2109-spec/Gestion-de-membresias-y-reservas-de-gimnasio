package reservas;
import java.time.LocalDateTime;
public class EventoCupo {

    public enum Tipo {
        CUPO_LIBERADO,
        CUPO_ASIGNADO
    }
    private final Tipo tipo;
    private final ClaseGrupal clase;
    private final String clienteId;
    private final LocalDateTime momento;

    public EventoCupo(Tipo tipo, ClaseGrupal clase,
                      String clienteId) {

        this.tipo = tipo;
        this.clase = clase;
        this.clienteId = clienteId;
        this.momento = LocalDateTime.now();
    }

    public Tipo getTipo() {
        return tipo;
    }

    public ClaseGrupal getClaseGrupal() {
        return clase;
    }

    public String getClienteId() {
        return clienteId;
    }

    public LocalDateTime getMomento() {
        return momento;
    }

    public String mensaje() {

        switch (tipo) {

            case CUPO_LIBERADO:
                return "Se libero un cupo en la clase "
                        + clase.getNombre();

            case CUPO_ASIGNADO:
                return "Se asigno un cupo al cliente "
                        + clienteId
                        + " en la clase "
                        + clase.getNombre();

            default:
                return "Evento desconocido";
        }
    }
}
