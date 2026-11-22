package usuarios;

public class ClienteFactory extends UsuarioFactory {
    @Override
    protected Usuario crearUsuario(String id, String nombre, String correo, String hash) {
        return new Cliente(id, nombre, correo, hash);
    }
}
