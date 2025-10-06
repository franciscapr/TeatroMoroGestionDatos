/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package teatromorogestiondatos;


import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author franc
 */
public class TeatroMoroGestionDatos {

    // Arreglos
    static int[] ventas = new int[100];          // IDs de ventas
    static boolean[] asientos = new boolean[50]; // Estado de los asientos (false = disponible)
    static String[] clientes = new String[100];  // Nombres de clientes

    // Listas dinámicas
    static ArrayList<Evento> eventos = new ArrayList<>();
    static ArrayList<String> descuentos = new ArrayList<>();

    // Precio base
    static double precioEntrada = 10000.0;

    public static void main(String[] args) {

        // Inicializamos la lista de descuentos
        descuentos.add("Estudiante 10%");
        descuentos.add("Tercera Edad 15%");

        // Eventos de ejemplo
        eventos.add(new Evento(1, "Obra: Hamlet"));
        eventos.add(new Evento(2, "Show: Stand Up Comedy"));

        // Interfaz básica en consola
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("SISTEMA TEATRO MORO");
            System.out.println("1. Vender entrada");
            System.out.println("2. Actualizar cliente");
            System.out.println("3. Eliminar venta");
            System.out.println("4. Mostrar ventas");
            System.out.println("5. Mostrar eventos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> venderEntrada(sc);
                case 2 -> actualizarCliente(sc);
                case 3 -> eliminarVenta(sc);
                case 4 -> mostrarVentas();
                case 5 -> mostrarEventos();
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opcion invalida. Intente nuevamente.");
            }

        } while (opcion != 0);
    }

    // Venta de entradas
    public static void venderEntrada(Scanner sc) {
        System.out.print("Ingrese ID de venta (0-99): ");
        int idVenta = sc.nextInt();
        sc.nextLine();

        if (idVenta < 0 || idVenta >= ventas.length) {
            System.out.println("IMPORTANTE: El ID de venta seleccionado esta fuera de rango.");
            return;
        }

        System.out.print("Ingrese nombre del cliente: ");
        String cliente = sc.nextLine();

        System.out.print("Tipo de cliente (normal / estudiante / tercera edad): ");
        String tipoCliente = sc.nextLine();

        System.out.print("Ingrese numero de asiento (0-49): ");
        int asiento = sc.nextInt();
        sc.nextLine();

        if (asiento < 0 || asiento >= asientos.length) {
            System.out.println("Numero de asiento invalido.");
            return;
        }

        if (asientos[asiento]) {
            System.out.println("Asiento no disponible.");
            return;
        }

        System.out.print("Seleccione ID de evento (1 = Cine o 2 = Stand up): ");
        int idEvento = sc.nextInt();
        sc.nextLine();

        // Registrar datos
        ventas[idVenta] = idVenta + 1;
        clientes[idVenta] = cliente;
        asientos[asiento] = true;

        double total = aplicarDescuento(tipoCliente);
        System.out.println("Venta registrada: Cliente " + cliente
                + ", Asiento " + asiento
                + ", Total: $" + total);

        // Guardar venta en lista del evento
        Venta nuevaVenta = new Venta(idVenta, cliente, total);
        for (Evento e : eventos) {
            if (e.idEvento == idEvento) {
                e.ventas.add(nuevaVenta);
                break;
            }
        }
    }

    // Aplicando el descuento
    public static double aplicarDescuento(String tipoCliente) {
        return switch (tipoCliente.toLowerCase()) {
            case "estudiante" -> precioEntrada * 0.9;
            case "tercera edad" -> precioEntrada * 0.85;
            default -> precioEntrada;
        };
    }

    // Actualizando al cliente
    public static void actualizarCliente(Scanner sc) {
        System.out.print("Ingrese ID de venta a actualizar: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (id < 0 || id >= clientes.length || clientes[id] == null) {
            System.out.println("Venta no encontrada.");
            return;
        }

        System.out.print("Ingrese nuevo nombre del cliente: ");
        String nuevo = sc.nextLine();
        clientes[id] = nuevo;
        System.out.println("Cliente actualizado correctamente.");
    }

    // Eliminación de una venta
    public static void eliminarVenta(Scanner sc) {
        System.out.print("Ingrese ID de venta a eliminar: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (id < 0 || id >= ventas.length || ventas[id] == 0) {
            System.out.println("Venta no encontrada.");
            return;
        }

        ventas[id] = 0;
        clientes[id] = null;
        System.out.println("Venta eliminada correctamente.");
    }

    // Mostramos todas las ventas realizadas
    public static void mostrarVentas() {
        System.out.println("LISTADO DE VENTAS");
        for (int i = 0; i < ventas.length; i++) {
            if (ventas[i] != 0 && clientes[i] != null) {
                System.out.println("ID Venta: " + ventas[i] + " | Cliente: " + clientes[i]);
            }
        }
    }

    // Mostramos los eventos
    public static void mostrarEventos() {
        System.out.println("EVENTOS Y VENTAS REGISTRADAS");
        for (Evento e : eventos) {
            System.out.println("Evento " + e.idEvento + ": " + e.nombre);
            if (e.ventas.isEmpty()) {
                System.out.println("  (sin ventas registradas)");
            } else {
                for (Venta v : e.ventas) {
                    System.out.println("  Venta ID " + v.idVenta + " - Cliente: " + v.nombreCliente + " - Total: $" + v.total);
                }
            }
        }
    }
}

// Clase que sirve de apoyo
class Evento {
    int idEvento;
    String nombre;
    ArrayList<Venta> ventas = new ArrayList<>();

    public Evento(int idEvento, String nombre) {
        this.idEvento = idEvento;
        this.nombre = nombre;
    }
}

class Venta {
    int idVenta;
    String nombreCliente;
    double total;

    public Venta(int idVenta, String nombreCliente, double total) {
        this.idVenta = idVenta;
        this.nombreCliente = nombreCliente;
        this.total = total;
    }
}