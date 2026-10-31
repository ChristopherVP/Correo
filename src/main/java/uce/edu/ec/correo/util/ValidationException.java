package uce.edu.ec.correo.util;

/**
 * Clase de excepción personalizada para manejar errores de validación
 * en el sistema de correo electrónico.
 *
 * Esta clase extiende Exception y permite almacenar información adicional
 * sobre el campo que causó el error de validación y el mensaje descriptivo.
 *
 * @author Sistema de Correo UCE
 * @version 1.0
 */
public class ValidationException extends Exception {

    // ==================== ATRIBUTOS ====================

    /**
     * Mensaje descriptivo del error de validación
     */
    private String mensaje;

    /**
     * Nombre del campo que causó el error de validación
     */
    private String campo;

    // ==================== CONSTRUCTORES ====================

    /**
     * Constructor con solo mensaje de error
     *
     * @param mensaje Descripción del error de validación
     */
    public ValidationException(String mensaje) {
        // Llama al constructor de la clase padre Exception con el mensaje
        super(mensaje);

        // Almacena el mensaje en el atributo de instancia
        this.mensaje = mensaje;

        // Inicializa el campo como vacío
        this.campo = "";
    }

    /**
     * Constructor con mensaje de error y campo específico
     *
     * @param mensaje Descripción del error de validación
     * @param campo Nombre del campo que causó el error
     */
    public ValidationException(String mensaje, String campo) {
        // Llama al constructor de la clase padre Exception con el mensaje
        super(mensaje);

        // Almacena el mensaje en el atributo de instancia
        this.mensaje = mensaje;

        // Almacena el nombre del campo que causó el error
        this.campo = campo;
    }

    // ==================== MÉTODOS GETTER ====================

    /**
     * Obtiene el mensaje de error de validación
     *
     * @return Mensaje descriptivo del error
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Obtiene el nombre del campo que causó el error
     *
     * @return Nombre del campo con error
     */
    public String getCampo() {
        return campo;
    }

    // ==================== MÉTODOS SOBRESCRITOS ====================

    /**
     * Representación en texto de la excepción
     *
     * Genera un mensaje formateado que incluye:
     * - Si hay campo: "Error en [campo]: [mensaje]"
     * - Si no hay campo: "Error de validación: [mensaje]"
     *
     * @return Cadena de texto formateada con la información del error
     */
    @Override
    public String toString() {
        // Verifica si existe un nombre de campo válido
        if (campo != null && !campo.isEmpty()) {
            // Retorna formato: "Error en [campo]: [mensaje]"
            return "Error en " + campo + ": " + mensaje;
        }

        // Retorna formato genérico: "Error de validación: [mensaje]"
        return "Error de validación: " + mensaje;
    }
}