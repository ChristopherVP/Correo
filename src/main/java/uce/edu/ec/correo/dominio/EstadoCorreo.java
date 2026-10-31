package uce.edu.ec.correo.dominio;

/**
 * Enumeración que define los posibles estados de un correo electrónico.
 * Permite clasificar y filtrar correos según su estado en el sistema.
 */
public enum EstadoCorreo {
    // Representa todos los correos sin filtro específico
    TODOS("Todos"),

    // Correo guardado como borrador, aún no enviado
    BORRADOR("Borrador"),

    // Correo que ha sido enviado exitosamente
    ENVIADO("Enviado"),

    // Correo que ha sido recibido por el usuario
    RECIBIDO("Recibido"),

    // Correo que fue reenviado a otros destinatarios
    REENVIADO("Reenviado"),

    // Correo marcado como eliminado (papelera)
    ELIMINADO("Eliminado");

    // Descripción legible del estado para mostrar en la interfaz
    private final String descripcion;

    /**
     * Constructor privado de la enumeración.
     * Asigna una descripción legible a cada estado.
     *
     * @param descripcion Texto descriptivo del estado
     */
    EstadoCorreo(String descripcion) {
        // Asigna la descripción al atributo final
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción legible del estado.
     *
     * @return Texto descriptivo del estado (ej: "Enviado", "Borrador")
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Convierte el estado a String usando su descripción.
     * Override del método toString() de Object.
     *
     * @return Descripción del estado
     */
    @Override
    public String toString() {
        // Retorna la descripción legible en lugar del nombre del enum
        return descripcion;
    }
}