package uce.edu.ec.correo.dominio;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Clase que representa un grupo de usuarios en el sistema de correo.
 * Permite la comunicación grupal entre múltiples usuarios.
 * Los grupos tienen administradores que pueden gestionar miembros y configuración.
 * Implementa Serializable para persistencia de datos.
 */
public class Grupo implements Serializable {
    // Versión de serialización para compatibilidad
    private static final long serialVersionUID = 1L;

    // Nombre único identificador del grupo
    private String nombre;

    // Descripción o propósito del grupo
    private String descripcion;

    // Email del administrador principal del grupo
    private String emailAdministrador;

    // Lista de contactos que son miembros del grupo
    private ArrayList<Contacto> miembros;

    // Historial de mensajes enviados en el grupo
    private ArrayList<MensajeGrupo> mensajes;

    // Lista de emails de todos los administradores del grupo
    private ArrayList<String> administradores;

    /**
     * Constructor que crea un nuevo grupo.
     *
     * @param nombre Nombre único del grupo
     * @param descripcion Descripción del grupo
     */
    public Grupo(String nombre, String descripcion) {
        // Asigna el nombre del grupo
        this.nombre = nombre;

        // Asigna la descripción del grupo
        this.descripcion = descripcion;

        // Inicializa la lista vacía de miembros
        this.miembros = new ArrayList<>();

        // Inicializa la lista vacía de mensajes
        this.mensajes = new ArrayList<>();

        // Inicializa la lista vacía de administradores
        this.administradores = new ArrayList<>();
    }

    // ==================== GETTERS Y SETTERS BÁSICOS ====================

    /**
     * Obtiene el nombre del grupo.
     *
     * @return Nombre del grupo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece o modifica el nombre del grupo.
     *
     * @param nombre Nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del grupo.
     *
     * @return Descripción del grupo
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece o modifica la descripción del grupo.
     *
     * @param descripcion Nueva descripción
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el email del administrador principal.
     *
     * @return Email del administrador principal
     */
    public String getEmailAdministrador() {
        return emailAdministrador;
    }

    /**
     * Establece el administrador principal del grupo.
     * También lo agrega automáticamente a la lista de administradores.
     *
     * @param emailAdministrador Email del nuevo administrador principal
     */
    public void setEmailAdministrador(String emailAdministrador) {
        // Asigna el email del administrador principal
        this.emailAdministrador = emailAdministrador;

        // Verifica que el email no esté ya en la lista de administradores
        if (!this.administradores.contains(emailAdministrador.toLowerCase())) {
            // Agrega el email normalizado (minúsculas) a la lista
            this.administradores.add(emailAdministrador.toLowerCase());
        }
    }

    /**
     * Obtiene la lista de miembros del grupo.
     *
     * @return ArrayList de contactos miembros
     */
    public ArrayList<Contacto> getMiembros() {
        return miembros;
    }

    /**
     * Obtiene el historial de mensajes del grupo.
     *
     * @return ArrayList de mensajes del grupo
     */
    public ArrayList<MensajeGrupo> getMensajes() {
        return mensajes;
    }

    /**
     * Obtiene la lista de administradores del grupo.
     *
     * @return ArrayList con emails de administradores
     */
    public ArrayList<String> getAdministradores() {
        return administradores;
    }

    // ==================== MÉTODOS DE GESTIÓN DE MIEMBROS ====================

    /**
     * Agrega un nuevo miembro al grupo.
     * Previene duplicados verificando el email.
     *
     * @param contacto Contacto a agregar como miembro
     */
    public void agregarMiembro(Contacto contacto) {
        // Verifica que el contacto no sea null
        if (contacto != null) {
            // Variable para controlar si el miembro ya existe
            boolean yaExiste = false;

            // Recorre todos los miembros actuales
            for (Contacto c : miembros) {
                // Compara emails ignorando mayúsculas/minúsculas
                if (c.getEmail().equalsIgnoreCase(contacto.getEmail())) {
                    // Marca que el miembro ya existe
                    yaExiste = true;
                    break; // Sale del ciclo
                }
            }

            // Si el miembro no existe, lo agrega
            if (!yaExiste) {
                miembros.add(contacto);
            }
        }
    }

    /**
     * Elimina un miembro del grupo por su email.
     *
     * @param email Email del miembro a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminarMiembroPorEmail(String email) {
        // Usa removeIf para eliminar el miembro cuyo email coincida (ignorando mayúsculas)
        return miembros.removeIf(c -> c.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Busca un miembro específico por su email.
     *
     * @param email Email del miembro a buscar
     * @return Contacto encontrado o null si no existe
     */
    public Contacto buscarMiembroPorEmail(String email) {
        // Recorre todos los miembros
        for (Contacto c : miembros) {
            // Compara emails ignorando mayúsculas/minúsculas
            if (c.getEmail().equalsIgnoreCase(email)) {
                // Retorna el contacto encontrado
                return c;
            }
        }
        // Si no se encontró, retorna null
        return null;
    }

    /**
     * Alias de buscarMiembroPorEmail para compatibilidad.
     *
     * @param email Email del contacto a buscar
     * @return Contacto encontrado o null
     */
    public Contacto buscarContactoPorEmail(String email) {
        // Llama al método buscarMiembroPorEmail
        return buscarMiembroPorEmail(email);
    }

    /**
     * Verifica si un email pertenece a un miembro del grupo.
     *
     * @param email Email a verificar
     * @return true si es miembro, false si no lo es
     */
    public boolean esMiembro(String email) {
        // Recorre todos los miembros
        for (Contacto c : miembros) {
            // Compara emails ignorando mayúsculas/minúsculas
            if (c.getEmail().equalsIgnoreCase(email)) {
                return true; // Es miembro
            }
        }
        return false; // No es miembro
    }

    /**
     * Obtiene la cantidad total de miembros en el grupo.
     *
     * @return Número de miembros
     */
    public int getCantidadMiembros() {
        // Retorna el tamaño de la lista de miembros
        return miembros.size();
    }

    /**
     * Obtiene una lista con todos los emails de los miembros.
     *
     * @return ArrayList de strings con los emails
     */
    public ArrayList<String> obtenerEmailsMiembros() {
        // Crea una nueva lista para almacenar los emails
        ArrayList<String> emails = new ArrayList<>();

        // Recorre todos los miembros
        for (Contacto miembro : miembros) {
            // Agrega el email de cada miembro a la lista
            emails.add(miembro.getEmail());
        }

        // Retorna la lista de emails
        return emails;
    }

    // ==================== MÉTODOS DE MENSAJES ====================

    /**
     * Agrega un mensaje al historial del grupo.
     *
     * @param mensaje Mensaje a agregar
     */
    public void agregarMensaje(MensajeGrupo mensaje) {
        // Verifica que el mensaje no sea null antes de agregarlo
        if (mensaje != null) {
            // Agrega el mensaje a la lista
            mensajes.add(mensaje);
        }
    }

    // ==================== MÉTODOS DE ADMINISTRACIÓN ====================

    /**
     * Agrega un nuevo administrador al grupo.
     * Previene duplicados.
     *
     * @param email Email del nuevo administrador
     */
    public void agregarAdministrador(String email) {
        // Verifica que el email no sea null o vacío
        if (email != null && !email.trim().isEmpty()) {
            // Normaliza el email a minúsculas
            String emailNormalizado = email.toLowerCase();

            // Verifica que no esté ya en la lista
            if (!administradores.contains(emailNormalizado)) {
                // Agrega el email a la lista de administradores
                administradores.add(emailNormalizado);
            }
        }
    }

    /**
     * Quita los privilegios de administrador a un usuario.
     * No permite quitar al administrador principal.
     *
     * @param email Email del administrador a quitar
     * @return true si se quitó, false si no se pudo
     */
    public boolean quitarAdministrador(String email) {
        // Verifica que el email no sea null y que no sea el administrador principal
        if (email != null && !email.equalsIgnoreCase(emailAdministrador)) {
            // Usa removeIf para eliminar el administrador (ignorando mayúsculas)
            return administradores.removeIf(a -> a.equalsIgnoreCase(email));
        }
        // No se puede quitar al administrador principal o email null
        return false;
    }

    /**
     * Verifica si un email tiene privilegios de administrador.
     *
     * @param email Email a verificar
     * @return true si es administrador, false si no lo es
     */
    public boolean esAdministrador(String email) {
        // Verifica que el email no sea null
        if (email == null) {
            return false;
        }

        // Verifica si es el administrador principal
        if (email.equalsIgnoreCase(emailAdministrador)) {
            return true;
        }

        // Busca en la lista de administradores
        for (String admin : administradores) {
            // Compara emails ignorando mayúsculas
            if (admin.equalsIgnoreCase(email)) {
                return true; // Es administrador
            }
        }

        // No es administrador
        return false;
    }

    /**
     * Obtiene la cantidad total de administradores.
     *
     * @return Número de administradores
     */
    public int getCantidadAdministradores() {
        // Retorna el tamaño de la lista de administradores
        return administradores.size();
    }

    // ==================== MÉTODOS DE MENSAJES DEL SISTEMA ====================

    /**
     * Genera y agrega un mensaje del sistema cuando se agrega un miembro.
     *
     * @param emailAdmin Email del administrador que agregó
     * @param nombreAdmin Nombre del administrador
     * @param emailNuevoMiembro Email del miembro agregado
     * @param nombreNuevoMiembro Nombre del miembro agregado
     */
    public void agregarMensajeSistemaMiembroAgregado(String emailAdmin, String nombreAdmin,
                                                     String emailNuevoMiembro, String nombreNuevoMiembro) {
        // Construye el texto del mensaje de notificación
        String contenido = nombreAdmin + " (" + emailAdmin + ") agregó a " +
                nombreNuevoMiembro + " (" + emailNuevoMiembro + ")";

        // Crea un mensaje del sistema con el contenido
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,    // Nombre del grupo
                "Sistema",      // Nombre del remitente
                contenido       // Texto de la notificación
        );

        // Agrega el mensaje a la lista
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Genera y agrega un mensaje del sistema cuando se elimina un miembro.
     *
     * @param emailAdmin Email del administrador que eliminó
     * @param nombreAdmin Nombre del administrador
     * @param emailMiembroEliminado Email del miembro eliminado
     * @param nombreMiembroEliminado Nombre del miembro eliminado
     */
    public void agregarMensajeSistemaMiembroEliminado(String emailAdmin, String nombreAdmin,
                                                      String emailMiembroEliminado, String nombreMiembroEliminado) {
        // Construye el texto de notificación
        String contenido = nombreAdmin + " (" + emailAdmin + ") eliminó a " +
                nombreMiembroEliminado + " (" + emailMiembroEliminado + ")";

        // Crea el mensaje del sistema
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,
                "Sistema",
                contenido
        );

        // Agrega el mensaje
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Genera y agrega un mensaje del sistema cuando un miembro sale voluntariamente.
     *
     * @param emailMiembro Email del miembro que salió
     * @param nombreMiembro Nombre del miembro que salió
     */
    public void agregarMensajeSistemaMiembroSalio(String emailMiembro, String nombreMiembro) {
        // Construye el texto de notificación
        String contenido = nombreMiembro + " (" + emailMiembro + ") salió del grupo";

        // Crea el mensaje del sistema
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,
                "Sistema",
                contenido
        );

        // Agrega el mensaje
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Genera y agrega un mensaje del sistema cuando se agrega un nuevo administrador.
     *
     * @param emailQuienAgrega Email del administrador que otorgó permisos
     * @param nombreQuienAgrega Nombre del administrador que otorgó permisos
     * @param emailNuevoAdmin Email del nuevo administrador
     * @param nombreNuevoAdmin Nombre del nuevo administrador
     */
    public void agregarMensajeSistemaAdminAgregado(String emailQuienAgrega, String nombreQuienAgrega,
                                                   String emailNuevoAdmin, String nombreNuevoAdmin) {
        // Construye el texto de notificación
        String contenido = nombreQuienAgrega + " (" + emailQuienAgrega + ") hizo administrador a " +
                nombreNuevoAdmin + " (" + emailNuevoAdmin + ")";

        // Crea el mensaje del sistema
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,
                "Sistema",
                contenido
        );

        // Agrega el mensaje
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Genera y agrega un mensaje del sistema cuando se quita un administrador.
     *
     * @param emailQuienQuita Email del administrador que quitó permisos
     * @param nombreQuienQuita Nombre del administrador que quitó permisos
     * @param emailAdminQuitado Email del administrador removido
     * @param nombreAdminQuitado Nombre del administrador removido
     */
    public void agregarMensajeSistemaAdminQuitado(String emailQuienQuita, String nombreQuienQuita,
                                                  String emailAdminQuitado, String nombreAdminQuitado) {
        // Construye el texto de notificación
        String contenido = nombreQuienQuita + " (" + emailQuienQuita + ") quitó como administrador a " +
                nombreAdminQuitado + " (" + emailAdminQuitado + ")";

        // Crea el mensaje del sistema
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,
                "Sistema",
                contenido
        );

        // Agrega el mensaje
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Genera y agrega un mensaje del sistema cuando se cambia el nombre del grupo.
     *
     * @param emailAdmin Email del administrador que cambió el nombre
     * @param nombreAdmin Nombre del administrador
     * @param nombreAntiguo Nombre anterior del grupo
     * @param nombreNuevo Nombre nuevo del grupo
     */
    public void agregarMensajeSistemaNombreCambiado(String emailAdmin, String nombreAdmin,
                                                    String nombreAntiguo, String nombreNuevo) {
        // Construye el texto de notificación mostrando ambos nombres
        String contenido = nombreAdmin + " (" + emailAdmin + ") cambió el nombre del grupo de \"" +
                nombreAntiguo + "\" a \"" + nombreNuevo + "\"";

        // Crea el mensaje del sistema
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,
                "Sistema",
                contenido
        );

        // Agrega el mensaje
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Genera y agrega un mensaje del sistema cuando se cambia la descripción.
     *
     * @param emailAdmin Email del administrador que cambió la descripción
     * @param nombreAdmin Nombre del administrador
     */
    public void agregarMensajeSistemaDescripcionCambiada(String emailAdmin, String nombreAdmin) {
        // Construye el texto de notificación
        String contenido = nombreAdmin + " (" + emailAdmin + ") cambió la descripción del grupo";

        // Crea el mensaje del sistema
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,
                "Sistema",
                contenido
        );

        // Agrega el mensaje
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Genera y agrega un mensaje del sistema cuando se crea el grupo.
     *
     * @param emailCreador Email del creador del grupo
     * @param nombreCreador Nombre del creador
     */
    public void agregarMensajeSistemaGrupoCreado(String emailCreador, String nombreCreador) {
        // Construye el texto de notificación
        String contenido = nombreCreador + " (" + emailCreador + ") creó el grupo";

        // Crea el mensaje del sistema
        MensajeGrupo mensajeSistema = MensajeGrupo.crearMensajeSistema(
                this.nombre,
                "Sistema",
                contenido
        );

        // Agrega el mensaje
        this.mensajes.add(mensajeSistema);
    }

    /**
     * Convierte el grupo a una representación en String.
     * Override del método toString() de Object.
     *
     * @return String con información del grupo
     */
    @Override
    public String toString() {
        // Construye un string con los detalles del grupo
        return "Grupo{" +
                "nombre='" + nombre + '\'' +                            // Nombre del grupo
                ", descripcion='" + descripcion + '\'' +                // Descripción
                ", emailAdministrador='" + emailAdministrador + '\'' +  // Admin principal
                ", cantidadMiembros=" + miembros.size() +               // Número de miembros
                ", cantidadMensajes=" + mensajes.size() +               // Número de mensajes
                ", cantidadAdministradores=" + administradores.size() + // Número de admins
                '}';
    }
}