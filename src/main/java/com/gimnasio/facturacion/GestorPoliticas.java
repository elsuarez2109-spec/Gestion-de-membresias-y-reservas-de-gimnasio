package com.gimnasio.facturacion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
public class GestorPoliticas {
    private final List<PoliticaCobro> politicas=new ArrayList<>();
    public GestorPoliticas(){
        registrar(new PoliticaPorcentaje("Descuento pago anual (10%)",c->c.getMeses()>=12, -0.10));
        registrar(new PoliticaPorcentaje("Descuento pago semestral (5%)",c->c.getMeses()>=6 && c.getMeses()<12, -0.05));
        registrar(new PoliticaPorcentaje("Recargo por mora (5%)",c->c.getMora()>0, 0.05));
    }
    public void registrar(PoliticaCobro politica){
        if(politica==null){
            throw new IllegalArgumentException("La politica debe ser valida");
        }
        politicas.add(politica);
    }
    public List<PoliticaCobro> getPoliticas(){
        return Collections.unmodifiableList(politicas);
    }
    public void aplicar(ContextoCobro contexto, ResultadoCobro resultado){
        for(PoliticaCobro politica:politicas){
            if(politica.aplicable(contexto)){
                double ajuste=politica.calcularAjuste(contexto, resultado.getSubtotal());
                resultado.agregarAjuste(politica.getNombre(),ajuste);
            }
        }
    }
    public static class PoliticaPorcentaje implements PoliticaCobro{
        private final String nombre;
        private final Predicate<ContextoCobro> condicion;
        private final double porcentaje;
        PoliticaPorcentaje(String nombre, Predicate<ContextoCobro>condicion, double porcentaje){
            this.nombre=nombre;
            this.condicion=condicion;
            this.porcentaje=porcentaje;
        }
        @Override 
        public String getNombre(){
            return nombre;
        }
        @Override
        public boolean aplicable(ContextoCobro contexto){
            return condicion.test(contexto);
        }
        @Override 
        public double calcularAjuste(ContextoCobro contexto, double subtotal){
            return subtotal*porcentaje;
        }
    }
}
