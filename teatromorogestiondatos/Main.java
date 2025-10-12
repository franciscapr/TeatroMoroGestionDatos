package teatromorogestiondatos;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Main.java
 * Interfaz por consola para interactuar con TeatroMoroSistema.
 *
 * Instrucciones:
 * - Copia todos los archivos dentro del mismo paquete en NetBeans.
 * - Ejecuta Main.java.
 *
 * El menú cubre:
 *  1) Vender entrada (selección manual de asiento o asignación automática)
 *  2) Actualizar cliente de una venta (CRUD update)
 *  3) Eliminar venta (CRUD delete)
 *  4) Mostrar ventas (CRUD read)
 *  5) Mostrar eventos y ventas por evento
 *  6) Mostrar disponibilidad por sección
 *  7) Mostrar asientos de una sección
 *  0) Salir
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final TeatroMoroSistema sistema = new TeatroMoroSistema();

    public static void main(String[] args) {
        System.out.println("BIENVENIDO AL SISTEMA DE VENTAS - TEATRO MORO");
        int op;
        do {
            mostrarMenu();
            op = leerInt("Seleccione una opción: ");
            try {
                switch (op) {
                    case 1 -> flujoVenderEntrada();
                    case 2 -> flujoActualizarCliente();
                    case 3 -> flujoEliminarVenta();
                    case 4 -> flujoMostrarVentas();
                    case 5 -> flujoMostrarEventos();
                    case 6 -> sistema.mostrarDisponibilidad();
                    case 7 -> flujoMostrarAsientosSeccion();
                    case 0 -> System.out.println("Saliendo... ¡Gracias!");
                    default -> System.out.println("Opción inválida, intente nuevamente.");
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
            System.out.println();
        } while (op != 0);
    }

    private static void mostrarMenu() {
        System.out.println("==================================");
        System.out.println("1. Vender entrada");
        System.out.println("2. Actualizar cliente en venta");
        System.out.println("3. Eliminar venta");
        System.out.println("4. Mostrar ventas");
        System.out.println("5. Mostrar eventos y ventas");
        System.out.println("6. Mostrar disponibilidad por sección");
        System.out.println("7. Mostrar asientos de una sección");
        System.out.println("0. Salir");
        System.out.println("==================================");
    }

    // Flujo para vender entrada (manual o automática)
    private static void flujoVenderEntrada() throws Exception {
        System.out.println("--- VENDER ENTRADA ---");
        String nombre = leerTexto("Nombre del cliente: ");
        int edad = leerInt("Edad: ");
        String genero = leerTexto("Género (M/F/O): ").toUpperCase();
        boolean estudiante = leerTexto("¿Es estudiante? (s/n): ").equalsIgnoreCase("s");

        Cliente c = new Cliente(nombre, edad, genero, estudiante);

        System.out.println("Secciones disponibles:");
        List<String> secciones = sistema.getSecciones();
        for (int i = 0; i < secciones.size(); i++) {
            System.out.println((i+1) + ". " + secciones.get(i));
        }
        int sel = leerInt("Seleccione sección (número): ");
        if (sel < 1 || sel > secciones.size()) {
            System.out.println("Sección inválida.");
            return;
        }
        String seccion = secciones.get(sel-1);

        System.out.println("1) Seleccionar asiento específico");
        System.out.println("2) Asignar primeros asientos libres automáticamente");
        int modo = leerInt("Modo (1/2): ");
        int idEvento = leerInt("ID de evento (ej: 1 = Hamlet, 2 = Stand Up): ");

        if (modo == 1) {
            // mostrar asientos disponibles resumido
            List<Asiento> disponibles = sistema.obtenerAsientosDisponibles(seccion);
            if (disponibles.isEmpty()) {
                System.out.println("No hay asientos disponibles en la sección " + seccion);
                return;
            }
            System.out.println("Asientos disponibles (muestra):");
            for (int i = 0; i < Math.min(10, disponibles.size()); i++) {
                Asiento a = disponibles.get(i);
                System.out.println("Fila " + a.getFila() + " - N° " + a.getNumero());
            }

            int fila = leerInt("Ingrese fila: ");
            int numero = leerInt("Ingrese número: ");
            Venta v = sistema.venderEntrada(c, seccion, fila, numero, idEvento);
            System.out.println("Venta registrada con éxito:");
            System.out.println(v.imprimirBoleta());
        } else if (modo == 2) {
            int cantidad = leerInt("Ingrese cantidad de entradas a asignar automáticamente: ");
            List<Venta> ventas = sistema.venderEntradasAutomaticas(c, seccion, cantidad, idEvento);
            System.out.println("Ventas registradas:");
            for (Venta v : ventas) {
                System.out.println(v.imprimirBoleta());
            }
        } else {
            System.out.println("Modo inválido.");
        }
    }

    // Actualizar cliente en venta
    private static void flujoActualizarCliente() {
        System.out.println("--- ACTUALIZAR CLIENTE EN VENTA ---");
        int id = leerInt("Ingrese ID de venta a actualizar: ");
        Venta v = sistema.buscarVentaPorId(id);
        if (v == null) {
            System.out.println("Venta no encontrada.");
            return;
        }
        System.out.println("Venta encontrada: " + v.toString());
        String nuevoNombre = leerTexto("Nuevo nombre (enter para mantener): ");
        String edadTxt = leerTexto("Nueva edad (enter para mantener): ");
        Integer nuevaEdad = null;
        if (!edadTxt.isBlank()) {
            try {
                nuevaEdad = Integer.parseInt(edadTxt);
            } catch (NumberFormatException e) {
                System.out.println("Edad inválida. No se modificará.");
            }
        }
        String nuevoGenero = leerTexto("Nuevo género (M/F/O) (enter para mantener): ");
        String estTxt = leerTexto("¿Es estudiante? (s/n) (enter para mantener): ");
        Boolean nuevoEst = null;
        if (!estTxt.isBlank()) {
            nuevoEst = estTxt.equalsIgnoreCase("s");
        }
        boolean ok = sistema.actualizarClienteVenta(id, nuevoNombre.isBlank() ? null : nuevoNombre, nuevaEdad, nuevoGenero.isBlank() ? null : nuevoGenero, nuevoEst);
        if (ok) System.out.println("Actualización realizada.");
        else System.out.println("No se pudo actualizar.");
    }

    // Eliminar venta
    private static void flujoEliminarVenta() {
        System.out.println("--- ELIMINAR VENTA ---");
        int id = leerInt("Ingrese ID de venta a eliminar: ");
        boolean ok = sistema.eliminarVenta(id);
        if (ok) System.out.println("Venta eliminada y asiento liberado.");
        else System.out.println("Venta no encontrada.");
    }

    // Mostrar todas las ventas
    private static void flujoMostrarVentas() {
        System.out.println("--- LISTADO DE VENTAS ---");
        List<Venta> ventas = sistema.listarVentas();
        if (ventas.isEmpty()) {
            System.out.println("(sin ventas registradas)");
            return;
        }
        for (Venta v : ventas) {
            System.out.println(v.toString());
        }
    }

    // Mostrar eventos y sus ventas
    private static void flujoMostrarEventos() {
        System.out.println("--- EVENTOS ---");
        List<Evento> evs = sistema.listarEventos();
        for (Evento e : evs) {
            System.out.println(e.toString());
            if (e.ventas.isEmpty()) {
                System.out.println("  (sin ventas registradas)");
            } else {
                for (Venta v : e.ventas) {
                    System.out.println("  " + v.toString());
                }
            }
        }
    }

    // Mostrar asientos de una sección
    private static void flujoMostrarAsientosSeccion() {
        List<String> secciones = sistema.getSecciones();
        for (int i = 0; i < secciones.size(); i++) {
            System.out.println((i+1) + ". " + secciones.get(i));
        }
        int sel = leerInt("Seleccione sección (número): ");
        if (sel < 1 || sel > secciones.size()) {
            System.out.println("Sección inválida.");
            return;
        }
        String seccion = secciones.get(sel-1);
        sistema.mostrarAsientosSeccion(seccion);
    }

    // Helpers de lectura robusta
    private static int leerInt(String mensaje) {
        int val = -1;
        while (true) {
            try {
                System.out.print(mensaje);
                val = sc.nextInt();
                sc.nextLine();
                break;
            } catch (InputMismatchException ex) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
                sc.nextLine();
            }
        }
        return val;
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }
}
