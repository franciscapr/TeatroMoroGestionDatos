package teatromorogestiondatos.TeatroMoroGestionDatos.src.teatromorogestiondatos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Venta.java
 * Representa una boleta/venta realizada.
 */
public class Venta {
    private static int contador = 1;
    private int id;
    private Cliente cliente;
    private Asiento asiento;
    private double precioBase;
    private double descuentoPorcentaje; // ex: 25.0
    private double precioFinal;
    private LocalDateTime fecha;

    public Venta(Cliente cliente, Asiento asiento, double precioBase, double descuentoPorcentaje) {
        this.id = contador++;
        this.cliente = cliente;
        this.asiento = asiento;
        this.precioBase = precioBase;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.precioFinal = calcularPrecioFinal();
        this.fecha = LocalDateTime.now();
    }

    private double calcularPrecioFinal() {
        double descuentos = (descuentoPorcentaje / 100.0) * precioBase;
        double total = precioBase - descuentos;
        return Math.round(total * 100.0) / 100.0;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Asiento getAsiento() { return asiento; }
    public double getPrecioBase() { return precioBase; }
    public double getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public double getPrecioFinal() { return precioFinal; }
    public LocalDateTime getFecha() { return fecha; }

    public String imprimirBoleta() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("======= BOLETA TEATRO MORO =======\n");
        sb.append("Boleta N°: ").append(id).append("\n");
        sb.append("Fecha: ").append(fecha.format(fmt)).append("\n");
        sb.append("Cliente: ").append(cliente.getNombre()).append(" | Edad: ").append(cliente.getEdad())
                .append(" | Género: ").append(cliente.getGenero()).append("\n");
        sb.append("Asiento: ").append(asiento.getSeccion()).append(" - F").append(asiento.getFila())
                .append(" N").append(asiento.getNumero()).append("\n");
        sb.append(String.format("Precio base: $%.2f\n", precioBase));
        sb.append(String.format("Descuento aplicado: %.1f %%\n", descuentoPorcentaje));
        sb.append(String.format("Total a pagar: $%.2f\n", precioFinal));
        sb.append("==================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Venta#" + id + " | " + cliente.getNombre() + " | " + asiento.getId() + " | $" + precioFinal;
    }
}
