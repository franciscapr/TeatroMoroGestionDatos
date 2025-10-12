package teatromorogestiondatos.TeatroMoroGestionDatos.src.teatromorogestiondatos;

/**
 * Asiento.java
 * Representa un asiento dentro del teatro.
 */
public class Asiento {
    private String seccion; // VIP, PALCO, PLATEA_BAJA, PLATEA_ALTA, GALERIA
    private int fila;
    private int numero;
    private boolean vendido;

    public Asiento(String seccion, int fila, int numero) {
        this.seccion = seccion;
        this.fila = fila;
        this.numero = numero;
        this.vendido = false;
    }

    public String getSeccion() { return seccion; }
    public int getFila() { return fila; }
    public int getNumero() { return numero; }
    public boolean isVendido() { return vendido; }

    public void vender() { this.vendido = true; }
    public void liberar() { this.vendido = false; }

    // ID único legible
    public String getId() {
        return seccion + "-F" + fila + "N" + numero;
    }

    @Override
    public String toString() {
        return getId() + (vendido ? " (VENDIDO)" : " (DISPONIBLE)");
    }
}
