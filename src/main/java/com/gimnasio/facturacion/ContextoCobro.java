package com.gimnasio.facturacion;

public class ContextoCobro {
    private final Plan plan;
    private final int meses;
    private final int mora;
    public ContextoCobro(Plan plan, int meses, int mora){
        if(plan==null){
            throw new IllegalArgumentException("El tipo de plan debe ser valido");
        }
        if(meses<1){
            throw new IllegalArgumentException("Debe tener minimo un mes");
        }
        if(mora<0){
            throw new IllegalArgumentException("Los dias de mora no pueden ser negativos");
        }
        this.plan=plan;
        this.meses=meses;
        this.mora=mora;
    }
    public ContextoCobro(Plan plan, int meses){
        this(plan, meses,0);
    }
    public Plan getPlan(){
        return plan;
    }
    public int getMeses(){
        return meses;
    }
    public int getMora(){
        return mora;
    }
}
