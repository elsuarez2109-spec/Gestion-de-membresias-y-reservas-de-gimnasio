package com.gimnasio.facturacion;

public class PlanGrupal implements Plan {
    private static final double COSTO_MENSUAL_MIEMBRO=70000;
    private static final int NUMERO_MIN_PERSONAS=2;
    private final int miembros;
    public PlanGrupal(int miembros){
        if(miembros<NUMERO_MIN_PERSONAS){
            throw new IllegalArgumentException("El plan grupal requiere minimo "+NUMERO_MIN_PERSONAS+" miembros");
        }
        this.miembros=miembros;
    }
    public int getMiembros(){
        return miembros;
    }
    @Override 
    public String getNombre(){
        return "Plan Grupal";
    }
    @Override 
    public String getDescripcion(){
        return "Plan grupal de "+miembros+" miembros";
    }
    @Override 
    public double getCostoMensual(){
        return COSTO_MENSUAL_MIEMBRO*miembros;
    }
}
