package com.gimnasio.reservas;
package reservas;
import java.util.ArrayDeque;
import java.util.Queue;

public class ListaEspera {

    private final Queue<String> clientes;

    public ListaEspera() {
        clientes = new ArrayDeque<>();
    }
    public boolean agregar(String clienteId) {

        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException(
                "El ID del cliente no puede estar vacio"
            );
        }
        if (clientes.contains(clienteId)) {
            return false;
        }
        return clientes.offer(clienteId);
    }
    public String consultarPrimero() {
        return clientes.peek();
    }
    public String sacarPrimero() {
        return clientes.poll();
    }
    public boolean eliminar(String clienteId) {
        return clientes.remove(clienteId);
    }
    public boolean estaVacia() {
        return clientes.isEmpty();
    }
    public int cantidad() {
        return clientes.size();
    }
}
