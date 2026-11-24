package com.gimnasio.usuarios;

public class Cliente extends Usuario {
    public Cliente(String id, String nombre, String correo, String passwordHash){
        super(id,nombre,correo,passwordHash);  
    }
    @Override 
    public Rol getRol(){
        return Rol.CLIENTE;
    }
}
