package uce.edu.ec.correo.dominio;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Clase que representa un mensaje enviado dentro de un grupo.
 * Permite la comunicación entre miembros de un grupo con soporte para archivos adjuntos.
 * También soporta mensajes del sistema para notificaciones automáticas.
 * Implementa Serializable para persistencia de datos.
 */
public class MensajeGrupo implements Serializable {
    // Versión de serialización para compatibilidad
    private static final long serialVersionUID = 1L;

    // Nombre del grupo al que pertenece este mensaje
    private String nombreGrupo;

    // Email del usuario que envió el mensaje
    private String remitente;

    // Nombre completo del remitente (para mostrar en la interfaz)
    private String remitenteNombre;

    // Contenido textual del mensaje
    private String contenido;

    // Fecha y hora en que se envió el mensaje
    private LocalDateTime fechaEnvio;

    // Lista de archivos adjuntos al mensaje
    private ArrayList<ArchivoAdjunto> archivosAdjuntos;

    // Indica si es un mensaje generado automáticamente por el sistema
    private boolean esMensajeSistema;

    /**
     * Constructor que crea un nuevo mensaje de grupo.
     *
     * @param nombreGrupo Nombre del grupo donde se envía el mensaje
     * @param remitente Email del usuario remitente
     * @param remitenteNombre Nombre completo del remitente
     * @param contenido Texto del mensaje
     */
    public MensajeGrupo(String nombreGrupo, String remitente, String remitenteNombre, String contenido) {
        // Asigna el nombre del grupo
        this.nombreGrupo = nombreGrupo;

        // Asigna el email del remitente
        this.remitente = remitente;

        // Asigna el nombre del remitente
        this.remitenteNombre = remitenteNombre;

        // Asigna el contenido del mensaje
        this.contenido = contenido;

        // Establece la fecha y hora actual como momento de envío
        this.fechaEnvio = LocalDateTime.now();

        // Inicializa la lista vacía de archivos adjuntos
        this.archivosAdjuntos = new ArrayList<>();

        // Por defecto, los mensajes creados normalmente no son del sistema
        this.esMensajeSistema = false;
    }

    /**
     * Obtiene el nombre del grupo al que pertenece el mensaje.
     *
     * @return Nombre del grupo
     */
    public String getNombreGrupo() {
        return nombreGrupo;
    }

    /**
     * Obtiene el email del remitente del mensaje.
     *
     * @return Email del remitente
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * Obtiene el nombre completo del remitente.
     *
     * @return Nombre del remitente
     */
    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    /**
     * Obtiene el contenido textual del mensaje.
     *
     * @return Texto del mensaje
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Obtiene la fecha y hora de envío del mensaje.
     *
     * @return Objeto LocalDateTime con la fecha de envío
     */
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    /**
     * Obtiene la lista de archivos adjuntos al mensaje.
     *
     * @return ArrayList con los archivos adjuntos
     */
    public ArrayList<ArchivoAdjunto> getArchivosAdjuntos() {
        return archivosAdjuntos;
    }

    /**
     * Verifica si el mensaje es un mensaje del sistema.
     *
     * @return true si es mensaje del sistema, false si es mensaje normal
     */
    public boolean isEsMensajeSistema() {
        return esMensajeSistema;
    }

    /**
     * Agrega un archivo adjunto al mensaje.
     *
     * @param archivo Objeto ArchivoAdjunto a agregar
     */
    public void agregarArchivoAdjunto(ArchivoAdjunto archivo) {
        // Verifica que el archivo no sea null antes de agregarlo
        if (archivo != null) {
            // Agrega el archivo a la lista de adjuntos
            archivosAdjuntos.add(archivo);
        }
    }

    /**
     * Verifica si el mensaje tiene archivos adjuntos.
     *
     * @return true si tiene adjuntos, false si no tiene
     */
    public boolean tieneAdjuntos() {
        // Verifica que la lista exista y que no esté vacía
        return archivosAdjuntos != null && !archivosAdjuntos.isEmpty();
    }

    /**
     * Obtiene la cantidad de archivos adjuntos en el mensaje.
     *
     * @return Número de archivos adjuntos
     */
    public int getCantidadAdjuntos() {
        // Si la lista existe retorna su tamaño, si no existe retorna 0
        return archivosAdjuntos != null ? archivosAdjuntos.size() : 0;
    }

    /**
     * Formatea la fecha de envío en un formato legible.
     *
     * @return String con la fecha formateada (dd/MM/yyyy HH:mm) o cadena vacía
     */
    public String getFechaEnvioFormateada() {
        // Verifica que la fecha no sea null
        if (fechaEnvio != null) {
            // Crea un formateador con el patrón día/mes/año hora:minuto
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Formatea la fecha según el patrón y la retorna
            return fechaEnvio.format(formatter);
        }
        // Si la fecha es null, retorna cadena vacía
        return "";
    }

    /**
     * Método estático que crea un mensaje del sistema (notificación automática).
     * Estos mensajes se generan para eventos como agregar miembros, cambios de admin, etc.
     *
     * @param nombreGrupo Nombre del grupo donde se genera el mensaje
     * @param nombreSistema Nombre a mostrar como remitente (ej: "Sistema")
     * @param contenido Texto del mensaje de notificación
     * @return Nuevo MensajeGrupo marcado como mensaje del sistema
     */
    public static MensajeGrupo crearMensajeSistema(String nombreGrupo, String nombreSistema, String contenido) {
        // Crea un nuevo mensaje con un email especial del sistema
        MensajeGrupo mensaje = new MensajeGrupo(
                nombreGrupo,                 // Nombre del grupo
                "sistema@grupo.ec",          // Email especial del sistema
                nombreSistema,               // Nombre a mostrar (ej: "Sistema")
                contenido                    // Contenido de la notificación
        );

        // Marca el mensaje como mensaje del sistema
        mensaje.esMensajeSistema = true;

        // Retorna el mensaje creado
        return mensaje;
    }

    /**
     * Convierte el mensaje a una representación en String.
     * Override del método toString() de Object.
     *
     * @return String con información del mensaje
     */
    @Override
    public String toString() {
        // Construye un string con todos los detalles del mensaje
        return "MensajeGrupo{" +
                "nombreGrupo='" + nombreGrupo + '\'' +          // Nombre del grupo
                ", remitente='" + remitente + '\'' +            // Email del remitente
                ", remitenteNombre='" + remitenteNombre + '\'' + // Nombre del remitente
                ", contenido='" + contenido + '\'' +            // Contenido del mensaje
                ", fechaEnvio=" + getFechaEnvioFormateada() +   // Fecha formateada
                ", cantidadAdjuntos=" + getCantidadAdjuntos() + // Número de adjuntos
                ", esMensajeSistema=" + esMensajeSistema +      // Si es del sistema
                '}';
    }
}