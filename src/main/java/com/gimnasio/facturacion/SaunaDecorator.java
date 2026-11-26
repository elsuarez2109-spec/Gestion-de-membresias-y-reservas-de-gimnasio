package com.gimnasio.facturacion;

public class SaunaDecorator extends BeneficioDecorator {
    private static final double COSTO=30000;
    public SaunaDecorator(Plan plan){
        super(plan);
    }
    @Override 
    protected String getNombreBeneficio(){
        return "Sauna";
    }
    @Override
    protected double getCostoBeneficio(){
        return COSTO;
    }
}