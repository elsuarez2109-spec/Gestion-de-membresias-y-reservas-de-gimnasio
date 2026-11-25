package facturacion;

public class SpaDecorator implements BeneficioDecorator {
    private static final double COSTO=40000;
    public SpaDecorator(Plan plan){
        super(plan);
    }
    @Override 
    protected String getNombreBeneficio(){
        return "Spa";
    }
    protected double getCostoBeneficio(){
        return COSTO;
    }
}
