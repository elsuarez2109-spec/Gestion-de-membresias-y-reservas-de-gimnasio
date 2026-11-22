package usuarios;

public class Instructor extends Usuario {
    public Instructor(String id, String nombre, String correo, String passwordHash) {
        super(id, nombre, correo, passwordHash);
    }
    @Override
    public Rol getRol() {
        return Rol.INSTRUCTOR;
    }
}
