package com.gimnasio.facturacion;

public class PlanPro implements Plan {
    public static final double COSTO_MENSUAL=100000;
    @Override 
    public String getNombre(){
        return "Plan Pro";
    }
    @Override 
    public String getDescripcion(){
        return "Plan con acceso total al gimnansio y sus servicios";
    }
    @Override 
    public double getCostoMensual(){
        return COSTO_MENSUAL;
    }
}
