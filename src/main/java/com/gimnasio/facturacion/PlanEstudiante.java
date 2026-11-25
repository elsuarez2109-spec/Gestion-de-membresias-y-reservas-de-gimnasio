package facturacion;

public class PlanEstudiante implements Plan {
    private static final double COST_MENSUAL=60000;
    @Override 
    public String getNombre(){
        return "Plan Estudiante";
    }
    @Override 
    public String getDescripcion(){
        return "Paln con precio reducido pero necesita carnet vigente";
    }
    @Override 
    public double getCostoMensual(){
        return COSTO_MENSUAL;
    }
}
