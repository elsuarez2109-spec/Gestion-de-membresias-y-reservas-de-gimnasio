package com.gimnasio.facturacion;

public class SpaDecorator extends BeneficioDecorator {
    private static final double COSTO=40000;
    public SpaDecorator(Plan plan){
        super(plan);
    }
    @Override 
    protected String getNombreBeneficio(){
        return "Spa";
    }
    @Override
    protected double getCostoBeneficio(){
        return COSTO;
    }
}