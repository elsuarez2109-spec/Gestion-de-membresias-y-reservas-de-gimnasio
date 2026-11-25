package com.gimnasio.reservas;

public class SujetoCupo {
public interface SujetoCupo {
    void suscribir(ObservadorCupo observador);
    void desuscribir(ObservadorCupo observador);
    void notificar(EventoCupo evento);
}
