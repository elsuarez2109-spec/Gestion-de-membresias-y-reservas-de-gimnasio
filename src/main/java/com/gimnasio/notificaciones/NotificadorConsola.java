package com.gimnasio.notificaciones;
import com.gimnasio.facturacion.ResultadoCobro;
import com.gimnasio.reservas.EventoCupo;
import com.gimnasio.reservas.ObservadorCupo;
import java.io.PrintStream;
public class NotificadorConsola implements ObservadorCupo {
    private final PrintStream salida;
    public NotificadorConsola(){
        this(System.out);
    }
    public NotificadorConsola(PrintStream salida){
        this.salida=salida;
    }
    @Override
    public void alRecibirEvento(EventoCupo evento){
        notificar(String.valueOf(evento));
    }
    public void notificar(String mensaje){
        salida.println("[NOTIFICACION] "+mensaje);
    }
    public void notificarCobro(ResultadoCobro resultado){
        notificar("Factura  generada: "+resultado);
    }
}