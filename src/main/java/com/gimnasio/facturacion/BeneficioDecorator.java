package com.gimnasio.facturacion;

public abstract class BeneficioDecorator implements Plan {
    protected final Plan plan;
    protected BeneficioDecorator(Plan plan){
        if (plan==null){
            throw new IllegalArgumentException("Debe tener un plan activo");
        }
        this.plan=plan;
    }
    protected abstract String getNombreBeneficio();
    protected abstract double getCostoBeneficio();
    @Override 
    public String getNombre(){
        return plan.getNombre();
    }
    @Override 
    public String getDescripcion(){
        return plan.getDescripcion()+" + "+getNombreBeneficio();
    }
    @Override 
    public double getCostoMensual(){
        return plan.getCostoMensual()+getCostoBeneficio();
    }
}