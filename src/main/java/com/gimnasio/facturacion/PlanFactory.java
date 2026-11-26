package com.gimnasio.facturacion;

public final class PlanFactory {
    public enum Tipo{
        BASICO, ESTUDIANTE, GRUPAL, PRO
    }
    private PlanFactory(){

    }
    public static Plan crear(Tipo tipo){
        return crear(tipo, 0);
    }
    /**
     * @param miembros Número de miembros para el plan grupal
     */
    public static Plan crear(Tipo tipo, int miembros){
        if(tipo==null){
            throw new IllegalArgumentException("El tipo de plan debe ser valido");
        }
        switch(tipo){
            case BASICO:
                return new PlanBasico();
            case ESTUDIANTE:
                return new PlanEstudiante();
            case GRUPAL:
                return new PlanGrupal(miembros);
            case PRO:
                return new PlanPro();
            default:
                throw new IllegalArgumentException("Tipo de plan invalido o inexistente");
        }
    }
    public static Plan crear(String tipo, int miembros){
        try{
            return crear(Tipo.valueOf(tipo.trim().toUpperCase()),miembros);
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Tipo de plan invalido");
        }
    }
}