package teatromorogestiondatos.TeatroMoroGestionDatos.src.teatromorogestiondatos;

import java.util.ArrayList;

/**
 * Evento.java
 * Modelo simple para almacenar ventas por evento (opcional en el flujo).
 */
public class Evento {
    public int idEvento;
    public String nombre;
    public ArrayList<Venta> ventas;

    public Evento(int idEvento, String nombre) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.ventas = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Evento " + idEvento + ": " + nombre + " (Ventas: " + ventas.size() + ")";
    }
}
