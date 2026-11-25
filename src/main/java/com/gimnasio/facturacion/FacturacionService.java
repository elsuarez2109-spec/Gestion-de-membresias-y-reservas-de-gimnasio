package com.gimnasio.facturacion;
import java.util.function.Consumer;
public class FacturacionService {
    private final GestorPoliticas gestorPoliticas;
    private Consumer<String> notificador;
    public FacturacionService(){
        this(new GestorPoliticas());
    }
    public FacturacionService(GestorPoliticas gestorPoliticas){
        this.gestorPoliticas=gestorPoliticas;
    }
    public GestorPoliticas getGestorPoliticas(){
        return gestorPoliticas;
    }
    public void setNotificador(Consumer<String>notificador){
        this.notificador=notificador;
    }
    public ResultadoCobro calcular (ContextoCobro contexto){
        Plan plan=contexto.getPlan();
        double subtotal=plan.getCostoMensual()*contexto.getMeses();
        ResultadoCobro resultado=new ResultadoCobro(plan.getDescripcion()+" + "+contexto.getMeses()+" mes/meses", subtotal);
        gestorPoliticas.aplicar(contexto, resultado);
        if(notificador!=null){
            notificador.accept("Factura generada: "+resultado);
        }
        return resultado;
    }
}
