package facturacion;

public class EntrenadorPersonalDecorator implements BeneficioDecorator {
    private static final double COSTO=60000;
    public EntrenadorPersonalDecorator(Plan plan){
        super(plan);
    }
    @Override 
    protected String getNombreBeneficio(){
        return "Entrenador personal";
    }
    @Override
    protected double getCostoBeneficio(){
        return COSTO;
    }
}
