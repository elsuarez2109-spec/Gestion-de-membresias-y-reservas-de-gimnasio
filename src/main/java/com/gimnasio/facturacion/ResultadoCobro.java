package com.gimnasio.facturacion;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
public class ResultadoCobro {
    private final String descripcion;
    private final double subtotal;
    private final Map<String, Double>ajustes=new LinkedHashMap<>();
    public ResultadoCobro(String descripcion, double subtotal){
        this.descripcion=descripcion;
        this.subtotal=subtotal;
    }
    public void agregarAjuste(String nombre, double valor){
        ajustes.put(nombre, valor);
    }
    public String getDescripcion(){
        return descripcion;
    }
    public double getSubtotal(){
        return subtotal;
    }
    public Map<String,Double> getAjustes(){
        return Collections.unmodifiableMap(ajustes);
    }
    public double getTotal(){
        double total=subtotal;
        for(double ajuste:ajustes.values()){
            total+=ajuste;
        }
        return Math.max(total, 0);
    }
    @Override 
    public String toString(){
        StringBuilder sb=new StringBuilder();
    sb.append("FACTURA").append("\n");
        sb.append(descripcion).append("\n");
        sb.append(String.format("Subtotal: $%,.0f%n", subtotal));
        for(Map.Entry<String, Double>e:ajustes.entrySet()){
            sb.append(String.format(" %s: %s$%,.0f%n", e.getKey(), e.getValue()<0?"-":"+", Math.abs(e.getValue())));
        }
        sb.append(String.format("Total: $%,.0f",getTotal()));
        return sb.toString();
    }
}