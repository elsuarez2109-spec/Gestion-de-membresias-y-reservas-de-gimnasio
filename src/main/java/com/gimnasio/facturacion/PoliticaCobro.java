package com.gimnasio.facturacion;

public interface PoliticaCobro {
    String getNombre();
    boolean aplicable(ContextoCobro contexto);
    double cobro(ContextoCobro contexto, double subtotal);
}
