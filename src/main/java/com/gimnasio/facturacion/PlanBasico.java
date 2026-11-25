package facturacion;

public class PlanBasico implements Plan {
    private static final double COSTO_MENSUAL=80000;
    @Override 
    public String getNombre(){
        return "Plan Basico";
    }
    @Override 
    public String getDescripcion(){
        return "Plan basico con acceso limitado a maquinas";
    }
    @Override
    public double getCostoMensual(){
        return COSTO_MENSUAL;
    }    
}
