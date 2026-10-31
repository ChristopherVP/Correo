package uce.edu.ec.correo.dominio;

import java.io.Serializable;

/**
 * Clase que representa un contacto en la libreta de direcciones de un usuario.
 * Almacena información básica de contacto (email y nombre).
 * Implementa Serializable para permitir guardar y cargar contactos desde archivos.
 */
public class Contacto implements Serializable {
    // Versión de serialización para mantener compatibilidad al guardar/cargar
    private static final long serialVersionUID = 1L;

    // Dirección de correo electrónico del contacto (identificador único)
    private String email;

    // Nombre o alias del contacto
    private String nombre;

    /**
     * Constructor que crea un nuevo contacto.
     *
     * @param email Dirección de correo electrónico del contacto
     * @param nombre Nombre o alias del contacto
     */
    public Contacto(String email, String nombre) {
        // Asigna el email al atributo de la clase
        this.email = email;

        // Asigna el nombre al atributo de la clase
        this.nombre = nombre;
    }

    /**
     * Obtiene el email del contacto.
     *
     * @return Dirección de correo electrónico
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece o modifica el email del contacto.
     *
     * @param email Nueva dirección de correo electrónico
     */
    public void setEmail(String email) {
        // Actualiza el email del contacto
        this.email = email;
    }

    /**
     * Obtiene el nombre del contacto.
     *
     * @return Nombre o alias del contacto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece o modifica el nombre del contacto.
     *
     * @param nombre Nuevo nombre o alias
     */
    public void setNombre(String nombre) {
        // Actualiza el nombre del contacto
        this.nombre = nombre;
    }
}