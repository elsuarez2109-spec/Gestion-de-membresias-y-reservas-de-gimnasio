package facturacion;

public class InvitadoDecorator implements BeneficioDecorator {
    private static final double COSTO=20000;
    public InvitadoDecorator(Plan plan){
        super(plan);
    }
    @Override 
    protected String getNombreBeneficio(){
        return "Pase de inviado";
    }
    protected doubñe getCostoBeneficio(){
        return COSTO;
    }
}
