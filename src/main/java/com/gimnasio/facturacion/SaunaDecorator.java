package facturacion;

public class SaunaDecorator implements BeneficioDecorator {
    private static final double COSTO=30000;
    public SaunaDecorator(Plan plan){
        super(plan);
    }
    @Override 
    protected String getNombreBeneficio(){
        return "Sauna";
    }
    protected double getCostoBeneficio(){
        return COSTO;
    }
}
