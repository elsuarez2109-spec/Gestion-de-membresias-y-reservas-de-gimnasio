package com.gimnasio.reservas;

public class SujetoCupo {
    
package reservas;
public interface SujetoCupo {
    void suscribir(ObservadorCupo observador);
    void desuscribir(ObservadorCupo observador);
    void notificar(EventoCupo evento);
}
