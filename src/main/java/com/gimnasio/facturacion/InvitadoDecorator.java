package com.gimnasio.facturacion;

public class InvitadoDecorator extends BeneficioDecorator {
    private static final double COSTO=20000;
    public InvitadoDecorator(Plan plan){
        super(plan);
    }
    @Override 
    protected String getNombreBeneficio(){
        return "Pase de invitado";
    }
    @Override
    protected double getCostoBeneficio(){
        return COSTO;
    }
}
