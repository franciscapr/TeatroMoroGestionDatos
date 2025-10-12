package teatromorogestiondatos;

import java.util.*;

/**
 * TeatroMoroSistema.java
 * Núcleo del sistema: maneja asientos, ventas, descuentos, CRUD y utilidades.
 */
public class TeatroMoroSistema {

    // Map de secciones a lista de asientos
    private Map<String, List<Asiento>> mapaAsientos;
    // Lista de ventas (CRUD)
    private List<Venta> listaVentas;
    // Eventos (opcional, para agrupar ventas)
    private List<Evento> eventos;
    // Precios por sección
    private Map<String, Double> preciosBase;

    public TeatroMoroSistema() {
        mapaAsientos = new LinkedHashMap<>();
        listaVentas = new ArrayList<>();
        eventos = new ArrayList<>();
        preciosBase = new HashMap<>();
        inicializarPrecios();
        inicializarAsientos();
        inicializarEventos();
    }

    private void inicializarPrecios() {
        preciosBase.put("VIP", 50000.0);
        preciosBase.put("PALCO", 40000.0);
        preciosBase.put("PLATEA_BAJA", 30000.0);
        preciosBase.put("PLATEA_ALTA", 20000.0);
        preciosBase.put("GALERIA", 10000.0);
    }

    private void inicializarAsientos() {
        mapaAsientos.put("VIP", crearAsientos("VIP", 1, 10));
        mapaAsientos.put("PALCO", crearAsientos("PALCO", 2, 10));
        mapaAsientos.put("PLATEA_BAJA", crearAsientos("PLATEA_BAJA", 5, 10));
        mapaAsientos.put("PLATEA_ALTA", crearAsientos("PLATEA_ALTA", 4, 10));
        mapaAsientos.put("GALERIA", crearAsientos("GALERIA", 3, 15));
    }

    private void inicializarEventos() {
        eventos.add(new Evento(1, "Obra: Hamlet"));
        eventos.add(new Evento(2, "Show: Stand Up Comedy"));
    }

    private List<Asiento> crearAsientos(String seccion, int filas, int porFila) {
        List<Asiento> lista = new ArrayList<>();
        for (int f = 1; f <= filas; f++) {
            for (int n = 1; n <= porFila; n++) {
                lista.add(new Asiento(seccion, f, n));
            }
        }
        return lista;
    }

    // Mostrar disponibilidad por sección
    public void mostrarDisponibilidad() {
        System.out.println("---- Disponibilidad por sección ----");
        for (String s : mapaAsientos.keySet()) {
            long disponibles = mapaAsientos.get(s).stream().filter(a -> !a.isVendido()).count();
            double precio = preciosBase.getOrDefault(s, 0.0);
            System.out.println(String.format("%-12s -> %3d disponibles | Precio base: $%.0f", s, disponibles, precio));
        }
        System.out.println("------------------------------------");
    }

    // Obtener asientos disponibles de una sección
    public List<Asiento> obtenerAsientosDisponibles(String seccion) {
        List<Asiento> salida = new ArrayList<>();
        List<Asiento> lista = mapaAsientos.get(seccion);
        if (lista == null) return salida;
        for (Asiento a : lista) if (!a.isVendido()) salida.add(a);
        return salida;
    }

    // Buscar asiento por coordenadas fila+numero
    public Asiento buscarAsiento(String seccion, int fila, int numero) {
        List<Asiento> lista = mapaAsientos.get(seccion);
        if (lista == null) return null;
        for (Asiento a : lista) {
            if (a.getFila() == fila && a.getNumero() == numero) return a;
        }
        return null;
    }

    // Calcula descuento de acuerdo a la pauta (retorna porcentaje)
    // Reglas del enunciado:
    // Niño 5%, Mujer 7%, Estudiante 25%, Tercera edad 30%
    // Se aplicará el **mayor descuento** aplicable (no acumulativo).
    public double calcularDescuento(Cliente c) {
        double dNino = (c.getEdad() < 12) ? 5.0 : 0.0;
        double dMujer = (c.getGenero().equals("F")) ? 7.0 : 0.0;
        double dEst = (c.isEstudiante()) ? 25.0 : 0.0;
        double dTerc = (c.getEdad() >= 60) ? 30.0 : 0.0;
        return Math.max(Math.max(dNino, dMujer), Math.max(dEst, dTerc));
    }

    // Vender una entrada con asiento específico
    public Venta venderEntrada(Cliente cliente, String seccion, int fila, int numero, int idEvento) throws Exception {
        // Validaciones
        if (cliente.getEdad() < 0) throw new Exception("Edad inválida.");
        if (!mapaAsientos.containsKey(seccion)) throw new Exception("Sección inválida.");
        Asiento asiento = buscarAsiento(seccion, fila, numero);
        if (asiento == null) throw new Exception("Asiento no existe.");
        if (asiento.isVendido()) throw new Exception("Asiento ya vendido.");

        // Asignar asiento, calcular precio y crear venta
        asiento.vender();
        double precioBase = preciosBase.getOrDefault(seccion, 0.0);
        double descuento = calcularDescuento(cliente);
        Venta v = new Venta(cliente, asiento, precioBase, descuento);
        listaVentas.add(v);

        // Asociar a evento si corresponde
        Evento ev = buscarEventoPorId(idEvento);
        if (ev != null) ev.ventas.add(v);

        return v;
    }

    // Vender entrada automática: asigna primeros N asientos libres de la sección requerida
    public List<Venta> venderEntradasAutomaticas(Cliente cliente, String seccion, int cantidad, int idEvento) throws Exception {
        List<Asiento> disponibles = obtenerAsientosDisponibles(seccion);
        if (disponibles.size() < cantidad) throw new Exception("No hay suficientes asientos libres en " + seccion);
        List<Venta> ventasGeneradas = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Asiento a = disponibles.get(i);
            a.vender();
            double precioBase = preciosBase.getOrDefault(seccion, 0.0);
            double descuento = calcularDescuento(cliente);
            Venta v = new Venta(cliente, a, precioBase, descuento);
            listaVentas.add(v);
            ventasGeneradas.add(v);
            Evento ev = buscarEventoPorId(idEvento);
            if (ev != null) ev.ventas.add(v);
        }
        return ventasGeneradas;
    }

    // CRUD: listar ventas
    public List<Venta> listarVentas() {
        return Collections.unmodifiableList(listaVentas);
    }

    // Buscar venta por id
    public Venta buscarVentaPorId(int id) {
        for (Venta v : listaVentas) if (v.getId() == id) return v;
        return null;
    }

    // Actualizar cliente en una venta (por ejemplo cambiar nombre o marcar estudiante)
    public boolean actualizarClienteVenta(int idVenta, String nuevoNombre, Integer nuevaEdad, String nuevoGenero, Boolean nuevoEstudiante) {
        Venta v = buscarVentaPorId(idVenta);
        if (v == null) return false;
        Cliente c = v.getCliente();
        if (nuevoNombre != null && !nuevoNombre.isBlank()) c.setNombre(nuevoNombre);
        if (nuevaEdad != null) c.setEdad(nuevaEdad);
        if (nuevoGenero != null) c.setGenero(nuevoGenero);
        if (nuevoEstudiante != null) c.setEstudiante(nuevoEstudiante);

        // IMPORTANT: si cambia la edad/género/estudiante podría cambiar el descuento; en este ejercicio no recalculamos precio histórico,
        // pero podríamos: (opcional) recalcular precio y actualizar la venta.
        return true;
    }

    // Eliminar venta (devolver asiento)
    public boolean eliminarVenta(int idVenta) {
        Venta v = buscarVentaPorId(idVenta);
        if (v == null) return false;
        // Liberar asiento
        v.getAsiento().liberar();
        // Remover de lista y de evento si está
        listaVentas.remove(v);
        for (Evento e : eventos) e.ventas.remove(v);
        return true;
    }

    // Mostrar resumen de eventos
    public List<Evento> listarEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public Evento buscarEventoPorId(int id) {
        for (Evento e : eventos) if (e.idEvento == id) return e;
        return null;
    }

    // Obtener las secciones disponibles (ordenadas)
    public List<String> getSecciones() {
        return new ArrayList<>(mapaAsientos.keySet());
    }

    // Mostrar asientos de una sección
    public void mostrarAsientosSeccion(String seccion) {
        List<Asiento> lista = mapaAsientos.get(seccion);
        if (lista == null) {
            System.out.println("Sección inválida.");
            return;
        }
        System.out.println("Asientos - Sección: " + seccion);
        for (Asiento a : lista) {
            System.out.println(a.toString());
        }
    }
}
