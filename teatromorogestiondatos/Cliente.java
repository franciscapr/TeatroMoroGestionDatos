package teatromorogestiondatos;

/**
 * Cliente.java
 * Representa a la persona que comprará una entrada.
 */
public class Cliente {
    private String nombre;
    private int edad;
    private String genero; // "M" = hombre, "F" = mujer, "O" = otro
    private boolean estudiante;

    public Cliente(String nombre, int edad, String genero, boolean estudiante) {
        this.nombre = nombre;
        this.edad = Math.max(0, edad); // evitar edades negativas
        this.genero = (genero == null) ? "O" : genero.toUpperCase();
        this.estudiante = estudiante;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = Math.max(0, edad); }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = (genero == null) ? "O" : genero.toUpperCase(); }

    public boolean isEstudiante() { return estudiante; }
    public void setEstudiante(boolean estudiante) { this.estudiante = estudiante; }

    @Override
    public String toString() {
        return nombre + " | Edad: " + edad + " | Género: " + genero + " | Estudiante: " + (estudiante ? "Sí" : "No");
    }
}
