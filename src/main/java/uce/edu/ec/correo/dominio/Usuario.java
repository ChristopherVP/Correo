package uce.edu.ec.correo.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Clase que representa un usuario del sistema de correo electrónico.
 * Contiene información personal del usuario y maneja sus contactos, grupos y correos.
 * Implementa Serializable para permitir guardar usuarios en archivos.
 */
public class Usuario implements Serializable {
    // Versión de serialización para compatibilidad
    private static final long serialVersionUID = 1L;

    // ==================== ATRIBUTOS PERSONALES ====================

    // Número de cédula ecuatoriana (identificador único)
    private String cedula;

    // Nombre(s) del usuario
    private String nombre;

    // Apellido(s) del usuario
    private String apellido;

    // Dirección de correo electrónico (identificador de login)
    private String email;

    // Contraseña para autenticación
    private String password;

    // Número de teléfono del usuario
    private String telefono;

    // Fecha de nacimiento
    private LocalDate fechaNacimiento;

    // Género del usuario (MASCULINO, FEMENINO, OTRO)
    private Genero genero;

    // ==================== COLECCIONES ====================

    // Lista de contactos personales del usuario
    private ArrayList<Contacto> contactos;

    // Lista de grupos creados o en los que participa
    private ArrayList<Grupo> grupos;

    // Lista de correos electrónicos del usuario
    private ArrayList<Correo> correos;

    /**
     * Constructor que crea un nuevo usuario.
     * Inicializa todas las listas vacías.
     *
     * @param cedula Número de cédula ecuatoriana
     * @param nombre Nombre(s) del usuario
     * @param apellido Apellido(s) del usuario
     * @param email Dirección de correo electrónico
     * @param password Contraseña
     * @param telefono Número de teléfono
     * @param fechaNacimiento Fecha de nacimiento
     * @param genero Género del usuario
     */
    public Usuario(String cedula, String nombre, String apellido, String email,
                   String password, String telefono, LocalDate fechaNacimiento, Genero genero) {
        // Asigna la cédula
        this.cedula = cedula;

        // Asigna el nombre
        this.nombre = nombre;

        // Asigna el apellido
        this.apellido = apellido;

        // Asigna el email
        this.email = email;

        // Asigna la contraseña
        this.password = password;

        // Asigna el teléfono
        this.telefono = telefono;

        // Asigna la fecha de nacimiento
        this.fechaNacimiento = fechaNacimiento;

        // Asigna el género
        this.genero = genero;

        // Inicializa la lista vacía de contactos
        this.contactos = new ArrayList<>();

        // Inicializa la lista vacía de grupos
        this.grupos = new ArrayList<>();

        // Inicializa la lista vacía de correos
        this.correos = new ArrayList<>();
    }

    // ==================== GETTERS Y SETTERS ====================

    /**
     * Obtiene el número de cédula del usuario.
     *
     * @return Número de cédula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Establece o modifica el número de cédula.
     *
     * @param cedula Nuevo número de cédula
     */
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return Nombre(s)
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece o modifica el nombre.
     *
     * @param nombre Nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del usuario.
     *
     * @return Apellido(s)
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Obtiene el email del usuario.
     *
     * @return Dirección de correo electrónico
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece o modifica el email.
     *
     * @param email Nuevo email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece o modifica la contraseña.
     *
     * @param password Nueva contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el número de teléfono.
     *
     * @return Número de teléfono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Obtiene la fecha de nacimiento.
     *
     * @return Fecha de nacimiento
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Obtiene el género del usuario.
     *
     * @return Género
     */
    public Genero getGenero() {
        return genero;
    }

    /**
     * Obtiene la lista de contactos del usuario.
     *
     * @return ArrayList de contactos
     */
    public ArrayList<Contacto> getContactos() {
        return contactos;
    }

    /**
     * Obtiene la lista de grupos del usuario.
     *
     * @return ArrayList de grupos
     */
    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    /**
     * Obtiene la lista de correos del usuario.
     *
     * @return ArrayList de correos
     */
    public ArrayList<Correo> getCorreos() {
        return correos;
    }

    /**
     * Obtiene el nombre completo concatenando nombre y apellido.
     *
     * @return Nombre completo (nombre + espacio + apellido)
     */
    public String getNombreCompleto() {
        // Concatena nombre, un espacio y apellido
        return nombre + " " + apellido;
    }

    /**
     * Valida si una contraseña coincide con la del usuario.
     *
     * @param password Contraseña a validar
     * @return true si coincide, false si no
     */
    public boolean validarPassword(String password) {
        // Compara la contraseña dada con la almacenada
        return this.password.equals(password);
    }

    // ==================== MÉTODOS DE CONTACTO ====================

    /**
     * Agrega un nuevo contacto a la lista.
     *
     * @param contacto Contacto a agregar
     */
    public void agregarContacto(Contacto contacto) {
        // Agrega el contacto a la lista
        contactos.add(contacto);
    }

    /**
     * Modifica un contacto existente identificado por su email antiguo.
     *
     * @param emailAntiguo Email del contacto a modificar
     * @param contactoNuevo Nuevo objeto contacto con los datos actualizados
     * @return true si se modificó, false si no se encontró
     */
    public boolean modificarContacto(String emailAntiguo, Contacto contactoNuevo) {
        // Recorre todos los contactos usando índice
        for (int i = 0; i < contactos.size(); i++) {
            // Compara el email del contacto actual con el antiguo (ignorando mayúsculas)
            if (contactos.get(i).getEmail().equalsIgnoreCase(emailAntiguo)) {
                // Reemplaza el contacto en esa posición
                contactos.set(i, contactoNuevo);
                return true; // Modificación exitosa
            }
        }
        return false; // No se encontró el contacto
    }

    /**
     * Elimina un contacto identificado por su email.
     *
     * @param email Email del contacto a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminarContactoPorEmail(String email) {
        // Usa removeIf para eliminar el contacto cuyo email coincida
        return contactos.removeIf(c -> c.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Busca un contacto por su email.
     *
     * @param email Email a buscar
     * @return Contacto encontrado o null si no existe
     */
    public Contacto buscarContactoPorEmail(String email) {
        // Recorre todos los contactos
        for (Contacto c : contactos) {
            // Compara emails ignorando mayúsculas/minúsculas
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c; // Retorna el contacto encontrado
            }
        }
        return null; // No se encontró
    }

    /**
     * Busca un contacto por su nombre.
     *
     * @param nombre Nombre a buscar
     * @return Primer contacto que coincida o null
     */
    public Contacto buscarContactoPorNombre(String nombre) {
        // Recorre todos los contactos
        for (Contacto c : contactos) {
            // Verifica que el nombre no sea null y compara (ignorando mayúsculas)
            if (c.getNombre() != null && c.getNombre().equalsIgnoreCase(nombre)) {
                return c; // Retorna el contacto encontrado
            }
        }
        return null; // No se encontró
    }

    // ==================== MÉTODOS DE GRUPO ====================

    /**
     * Agrega un nuevo grupo a la lista.
     *
     * @param grupo Grupo a agregar
     */
    public void agregarGrupo(Grupo grupo) {
        // Agrega el grupo a la lista
        grupos.add(grupo);
    }

    /**
     * Modifica un grupo existente identificado por su nombre antiguo.
     *
     * @param nombreAntiguo Nombre del grupo a modificar
     * @param grupoNuevo Nuevo objeto grupo con los datos actualizados
     * @return true si se modificó, false si no se encontró
     */
    public boolean modificarGrupo(String nombreAntiguo, Grupo grupoNuevo) {
        // Recorre todos los grupos usando índice
        for (int i = 0; i < grupos.size(); i++) {
            // Compara el nombre del grupo actual con el antiguo (ignorando mayúsculas)
            if (grupos.get(i).getNombre().equalsIgnoreCase(nombreAntiguo)) {
                // Reemplaza el grupo en esa posición
                grupos.set(i, grupoNuevo);
                return true; // Modificación exitosa
            }
        }
        return false; // No se encontró el grupo
    }

    /**
     * Elimina un grupo identificado por su nombre.
     *
     * @param nombre Nombre del grupo a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminarGrupoPorNombre(String nombre) {
        // Usa removeIf para eliminar el grupo cuyo nombre coincida
        return grupos.removeIf(g -> g.getNombre().equalsIgnoreCase(nombre));
    }

    /**
     * Busca un grupo por su nombre.
     *
     * @param nombre Nombre a buscar
     * @return Grupo encontrado o null si no existe
     */
    public Grupo buscarGrupoPorNombre(String nombre) {
        // Recorre todos los grupos
        for (Grupo g : grupos) {
            // Compara nombres ignorando mayúsculas/minúsculas
            if (g.getNombre().equalsIgnoreCase(nombre)) {
                return g; // Retorna el grupo encontrado
            }
        }
        return null; // No se encontró
    }

    /**
     * Busca un grupo cuya descripción contenga el texto buscado.
     *
     * @param descripcion Texto a buscar en la descripción
     * @return Primer grupo que coincida o null
     */
    public Grupo buscarGrupoPorDescripcion(String descripcion) {
        // Recorre todos los grupos
        for (Grupo g : grupos) {
            // Verifica que la descripción no sea null
            if (g.getDescripcion() != null &&
                    // Verifica si la descripción contiene el texto buscado (ignorando mayúsculas)
                    g.getDescripcion().toLowerCase().contains(descripcion.toLowerCase())) {
                return g; // Retorna el grupo encontrado
            }
        }
        return null; // No se encontró
    }

    // ==================== MÉTODOS DE CORREO ====================

    /**
     * Agrega un nuevo correo a la lista.
     *
     * @param correo Correo a agregar
     */
    public void agregarCorreo(Correo correo) {
        // Agrega el correo a la lista
        correos.add(correo);
    }

    /**
     * Elimina un correo identificado por su asunto.
     *
     * @param asunto Asunto del correo a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminarCorreoPorAsunto(String asunto) {
        // Usa removeIf para eliminar el correo cuyo asunto coincida
        return correos.removeIf(c -> c.getAsunto().equalsIgnoreCase(asunto));
    }

    /**
     * Busca un correo por su asunto.
     *
     * @param asunto Asunto a buscar
     * @return Primer correo que coincida o null
     */
    public Correo buscarCorreoPorAsunto(String asunto) {
        // Recorre todos los correos
        for (Correo c : correos) {
            // Compara asuntos ignorando mayúsculas/minúsculas
            if (c.getAsunto().equalsIgnoreCase(asunto)) {
                return c; // Retorna el correo encontrado
            }
        }
        return null; // No se encontró
    }

    /**
     * Busca el primer correo de un remitente específico.
     *
     * @param remitente Email del remitente a buscar
     * @return Primer correo encontrado o null
     */
    public Correo buscarCorreoPorRemitente(String remitente) {
        // Recorre todos los correos
        for (Correo c : correos) {
            // Compara emails de remitente (ignorando mayúsculas)
            if (c.getRemitente().equalsIgnoreCase(remitente)) {
                return c; // Retorna el primer correo encontrado
            }
        }
        return null; // No se encontró
    }

    /**
     * Busca TODOS los correos de un remitente específico.
     *
     * @param remitente Email del remitente a buscar
     * @return ArrayList con todos los correos del remitente
     */
    public ArrayList<Correo> buscarCorreosPorRemitente(String remitente) {
        // Crea una lista para almacenar los resultados
        ArrayList<Correo> resultados = new ArrayList<>();

        // Recorre todos los correos
        for (Correo c : correos) {
            // Si el remitente coincide (ignorando mayúsculas)
            if (c.getRemitente().equalsIgnoreCase(remitente)) {
                // Agrega el correo a los resultados
                resultados.add(c);
            }
        }

        // Retorna la lista de correos encontrados (puede estar vacía)
        return resultados;
    }

    /**
     * Busca correos cuyo asunto contenga una palabra clave.
     * Búsqueda parcial e insensible a mayúsculas.
     *
     * @param palabraClave Texto a buscar en el asunto
     * @return ArrayList con todos los correos que coincidan
     */
    public ArrayList<Correo> buscarCorreosPorAsuntoParcial(String palabraClave) {
        // Crea una lista para almacenar los resultados
        ArrayList<Correo> resultados = new ArrayList<>();

        // Recorre todos los correos
        for (Correo c : correos) {
            // Verifica si el asunto contiene la palabra clave (ignorando mayúsculas)
            if (c.getAsunto().toLowerCase().contains(palabraClave.toLowerCase())) {
                // Agrega el correo a los resultados
                resultados.add(c);
            }
        }

        // Retorna la lista de correos encontrados (puede estar vacía)
        return resultados;
    }
}