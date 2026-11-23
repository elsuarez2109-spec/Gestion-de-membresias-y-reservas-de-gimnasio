package reservas;
import java.util.function.Predicate;
public class AsignadorListaEspera implements ObservadorCupo {

    private final Predicate<String> clienteHabilitado;

    public AsignadorListaEspera(
            Predicate<String> clienteHabilitado) {

        this.clienteHabilitado = clienteHabilitado;
    }
    @Override
    public void alRecibirEvento(EventoCupo evento) {

        if (evento.getTipo()
                != EventoCupo.Tipo.CUPO_LIBERADO) {
            return;
        }

        ClaseGrupal clase = evento.getClaseGrupal();


    }
}
