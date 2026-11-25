package com.gimnasio.reservas;

import java.util.function.Predicate;

public class AsignadorListaEspera implements ObservadorCupo {

    private final Predicate<String> clienteHabilitado;

    public AsignadorListaEspera(
            Predicate<String> clienteHabilitado) {

        if (clienteHabilitado == null) {
            throw new IllegalArgumentException(
                    "La validacion es obligatoria");
        }

        this.clienteHabilitado = clienteHabilitado;
    }

    @Override
    public void alRecibirEvento(EventoCupo evento) {

        if (evento.getTipo()
                != EventoCupo.Tipo.CUPO_LIBERADO) {
            return;
        }

        ClaseGrupal clase = evento.getClaseGrupal();
        ListaEspera lista = clase.getListaEspera();

        while (clase.hayCupo() && !lista.estaVacia()) {

            String clienteId = lista.sacarPrimero();

            if (!clienteHabilitado.test(clienteId)) {
                continue;
            }

            try {
                clase.reservar(clienteId);

                clase.notificar(new EventoCupo(
                        EventoCupo.Tipo.CUPO_ASIGNADO,
                        clase,
                        clienteId));

            } catch (IllegalStateException e) {
                System.err.println(
                        "No se pudo asignar el cupo: "
                        + e.getMessage());
            }
        }
    }
}
