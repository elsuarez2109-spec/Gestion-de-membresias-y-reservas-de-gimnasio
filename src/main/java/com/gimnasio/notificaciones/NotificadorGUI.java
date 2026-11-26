package com.gimnasio.notificaciones;
import com.gimnasio.facturacion.ResultadoCobro;
import com.gimnasio.reservas.EventoCupo;
import com.gimnasio.reservas.ObservadorCupo;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

public class NotificadorGUI implements ObservadorCupo {
    @Override 
    public void alRecibirEvento(EventoCupo evento){
        notificar(String.valueOf(evento));
    }
    public void notificar(String mensaje){
        if(GraphicsEnvironment.isHeadless()){
            System.out.println("[NOTIFICACION] "+mensaje);
            return;
        }
        SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, mensaje, "Notificacion", JOptionPane.INFORMATION_MESSAGE));
    }
    public void notificarCobro(ResultadoCobro resultado){
        notificar("Factura generada: "+resultado);
    }
}