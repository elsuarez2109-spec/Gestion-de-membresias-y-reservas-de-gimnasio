package com.gimnasio.reservas;
public interface SujetoCupo {
    void suscribir(ObservadorCupo observador);
    void desuscribir(ObservadorCupo observador);
    void notificar(EventoCupo evento);
}
