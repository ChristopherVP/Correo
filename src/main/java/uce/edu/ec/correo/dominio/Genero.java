package uce.edu.ec.correo.dominio;

/**
 * Enumeración que define las opciones de género para el registro de usuarios.
 * Permite clasificar a los usuarios según su identidad de género.
 */
public enum Genero {
    // Representa el género masculino
    MASCULINO("Masculino"),

    // Representa el género femenino
    FEMENINO("Femenino"),

    // Opción inclusiva para otras identidades de género
    OTRO("Otro");

    // Descripción legible del género para mostrar en la interfaz
    private final String descripcion;

    /**
     * Constructor privado de la enumeración.
     * Asigna una descripción legible a cada opción de género.
     *
     * @param descripcion Texto descriptivo del género
     */
    Genero(String descripcion) {
        // Asigna la descripción al atributo final
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción legible del género.
     *
     * @return Texto descriptivo del género (ej: "Masculino", "Femenino")
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Convierte el género a String usando su descripción.
     * Override del método toString() de Object.
     *
     * @return Descripción del género
     */
    @Override
    public String toString() {
        // Retorna la descripción legible en lugar del nombre del enum
        return descripcion;
    }
}