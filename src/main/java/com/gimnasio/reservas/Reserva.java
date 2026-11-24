package com.gimnasio.reservas;

public class Reserva {

    public enum Estado {
        CONFIRMADA,
        CANCELADA,
        ASISTIO,
        NO_ASISTIO
    }

    private String id;
    private String clienteId;
    private String claseId;
    private Estado estado;

    public Reserva(String id, String clienteId, String claseId) {
        this.id = id;
        this.clienteId = clienteId;
        this.claseId = claseId;
        this.estado = Estado.CONFIRMADA;
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getClaseId() {
        return claseId;
    }

    public Estado getEstado() {
        return estado;
    }

    public void cancelar() {
        if (estado != Estado.CONFIRMADA) {
            throw new IllegalStateException(
                "Solo se pueden cancelar reservas confirmadas"
            );
        }
        estado = Estado.CANCELADA;
    }

    public void registrarAsistencia() {
        if (estado != Estado.CONFIRMADA) {
            throw new IllegalStateException(
                "La reserva no esta confirmada"
            );
        }
        estado = Estado.ASISTIO;
    }

    public void registrarNoAsistencia() {
        if (estado != Estado.CONFIRMADA) {
            throw new IllegalStateException(
                "La reserva no esta confirmada"
            );
        }
        estado = Estado.NO_ASISTIO;
    }
}
