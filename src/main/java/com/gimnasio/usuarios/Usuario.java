package usuarios;

public abstract class Usuario {
    private final String id;
    private final String nombre;
    private final String correo;
    private final String passwordHash;
    protected Usuario(String id, String nombre, String correo, String passwordHash){
        this.id=id;
        this.nombre=nombre;
        this.correo=correo;
        this.passwordHash=passwordHash;
    }
    public abstract Rol getRol();
    public String getId(){
        return id;
    }
    public String getNombre(){
        return nombre;
    }
    public String getCorreo(){
        return correo;
    }
    public String getPasswordHash(){
        return passwordHash;
    }
}
