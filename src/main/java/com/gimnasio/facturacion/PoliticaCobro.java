package com.gimnasio.facturacion;

public interface PoliticaCobro {
    String getNombre();
    boolean aplicable(ContextoCobro contexto);
    double calcularAjuste(ContextoCobro contexto, double subtotal);
}
