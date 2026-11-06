package uce.edu.ec.correo.dominio;

import uce.edu.ec.correo.util.ConfiguracionAdmin;
import uce.edu.ec.correo.util.CuentaSuspendidaException;
import uce.edu.ec.correo.util.ControlIntentos;
import uce.edu.ec.correo.util.SeguridadUtil;
import uce.edu.ec.correo.util.Validador;
import uce.edu.ec.correo.util.ValidationException;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.*;

/**
 * Clase principal que representa el sistema de correo electrónico.
 *
 * Gestiona:
 * - Usuarios (registro, autenticación, eliminación)
 * - Contactos (CRUD completo)
 * - Grupos (CRUD completo con mensajes grupales)
 * - Correos (CRUD, envío, reenvío, estados)
 * - Persistencia de datos (guardado y carga desde archivo)
 *
 * Utiliza serialización de Java para guardar y cargar datos del sistema.
 *
 * @author Sistema de Correo UCE
 * @version 1.0
 */
public class EmpresaCorreo implements Serializable {

    // ==================== CONSTANTES ====================

    /**
     * ID de versión para la serialización de objetos.
     * Garantiza compatibilidad al deserializar.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Nombre del archivo donde se guardan los datos del sistema.
     * Se crea en el directorio de ejecución del programa.
     */
    private static final String ARCHIVO_DATOS = "datos_sistema.dat";

    // ==================== ATRIBUTOS ====================

    /**
     * Lista de todos los usuarios registrados en el sistema.
     * Cada usuario mantiene sus propios correos, contactos y grupos.
     */
    private ArrayList<Usuario> usuarios;

    /**
     * Usuario que ha iniciado sesión actualmente.
     * Es null cuando no hay sesión activa.
     */
    private Usuario usuarioActual;

    private CentroAdministracion administracion;

    private transient Administrador administradorActual;

    private transient ControlIntentos controlRecuperacion;

    // ==================== CONSTRUCTOR ====================

    /**
     * Constructor que inicializa el sistema de correo.
     * Crea listas vacías y establece que no hay usuario logueado.
     */
    public EmpresaCorreo() {
        // Inicializa la lista de usuarios vacía
        this.usuarios = new ArrayList<>();

        // No hay usuario logueado al inicio
        this.usuarioActual = null;
        this.administracion = new CentroAdministracion();
        this.administracion.asegurarAdministradorPrincipal();
    }

    // ==================== PERSISTENCIA DE DATOS ====================

    /**
     * Guarda el estado completo del sistema en un archivo binario.
     *
     * Proceso:
     * 1. Crea un ObjectOutputStream sobre el archivo
     * 2. Serializa el objeto completo (this)
     * 3. Cierra el stream automáticamente (try-with-resources)
     * 4. Muestra mensaje de confirmación
     *
     * En caso de error, muestra el mensaje de error en consola.
     */
    public void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO_DATOS))) {

            // Serializa el objeto completo al archivo
            oos.writeObject(this);

            // Mensaje de confirmación en consola
            System.out.println("Datos guardados exitosamente");

        } catch (IOException e) {
            // Captura y muestra errores de entrada/salida
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
    }

    /**
     * Carga el estado del sistema desde el archivo de datos.
     *
     * Proceso:
     * 1. Verifica si existe el archivo
     * 2. Si no existe, retorna una nueva instancia
     * 3. Si existe, lo deserializa
     * 4. Retorna la instancia cargada
     *
     * @return Instancia de EmpresaCorreo (cargada o nueva)
     */
    public static EmpresaCorreo cargarDatos() {
        // Crea objeto File para verificar existencia
        File archivo = new File(ARCHIVO_DATOS);

        // Si el archivo no existe, retorna instancia nueva
        if (!archivo.exists()) {
            System.out.println("No existe archivo de datos, creando nueva instancia");
            return new EmpresaCorreo();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(ARCHIVO_DATOS))) {

            // Deserializa el objeto desde el archivo
            EmpresaCorreo empresa = (EmpresaCorreo) ois.readObject();

            // Mensaje de confirmación
            System.out.println("Datos cargados exitosamente");

            // Retorna la instancia cargada
            empresa.inicializarAdministracion();
            return empresa;

        } catch (IOException | ClassNotFoundException e) {
            // Si hay error al cargar, retorna instancia nueva
            System.err.println("Error al cargar datos: " + e.getMessage());
            return new EmpresaCorreo();
        }
    }

    /**
     * Método de conveniencia que llama a guardarDatos().
     * Usado cuando se realizan cambios que deben persistirse.
     */
    public void guardarDatosAutomaticamente() {
        // Delega al método guardarDatos()
        guardarDatos();
    }

    // ==================== MÉTODOS DE USUARIO Y SESIÓN ====================

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Validaciones:
     * - Campos no vacíos
     * - Cédula ecuatoriana válida y única
     * - Email válido y único (normalizado a minúsculas)
     * - Nombre y apellido válidos (solo letras)
     * - Password válido (8-14 caracteres, al menos una mayúscula)
     * - Teléfono válido (opcional, 10 dígitos)
     * - Fecha de nacimiento válida
     *
     * @param usuario Usuario a registrar
     * @return true si el registro fue exitoso
     * @throws ValidationException si alguna validación falla
     */
    public boolean registrarUsuario(Usuario usuario) throws ValidationException {
        // Valida que la cédula no esté vacía
        if (!Validador.validarCampoNoVacio(usuario.getCedula(), "Cédula")) return false;

        // Valida que el nombre no esté vacío
        if (!Validador.validarCampoNoVacio(usuario.getNombre(), "Nombre")) return false;

        // Valida que el apellido no esté vacío
        if (!Validador.validarCampoNoVacio(usuario.getApellido(), "Apellido")) return false;

        // Valida que el email no esté vacío
        if (!Validador.validarCampoNoVacio(usuario.getEmail(), "Email")) return false;

        // Valida que el password no esté vacío
        if (!Validador.validarCampoNoVacio(usuario.getPassword(), "Password")) return false;

        // Valida el formato de la cédula ecuatoriana
        if (!Validador.validarCedulaEcuatoriana(usuario.getCedula())) {
            throw new ValidationException("Cédula ecuatoriana inválida", "Cédula");
        }

        // Verifica que no exista otro usuario con la misma cédula
        for (Usuario u : usuarios) {
            if (u.getCedula().equals(usuario.getCedula())) {
                throw new ValidationException("Ya existe un usuario con esta cédula", "Cédula");
            }
        }

        // Valida el formato del email
        if (!Validador.validarEmail(usuario.getEmail())) {
            throw new ValidationException("Email inválido", "Email");
        }

        // Normaliza el email a minúsculas y sin espacios
        String emailNormalizado = usuario.getEmail().trim().toLowerCase();
        usuario.setEmail(emailNormalizado);

        // Verifica que no exista otro usuario con el mismo email (ignora mayúsculas/minúsculas)
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuario.getEmail())) {
                throw new ValidationException("Ya existe un usuario con este email", "Email");
            }
        }
        if (ConfiguracionAdmin.esEmailDeAdministrador(usuario.getEmail())) {
            throw new ValidationException(
                    "El dominio " + ConfiguracionAdmin.DOMINIO_ADMIN + " está reservado para administradores", "Email");
        }
        if (centro().buscarAdministrador(usuario.getEmail()).isPresent()) {
            throw new ValidationException("Ya existe una cuenta con este email", "Email");
        }

        // Valida que el nombre solo contenga letras y espacios
        if (!Validador.validarNombre(usuario.getNombre())) {
            throw new ValidationException("Nombre inválido (solo letras y espacios)", "Nombre");
        }

        // Valida que el apellido solo contenga letras y espacios
        if (!Validador.validarNombre(usuario.getApellido())) {
            throw new ValidationException("Apellido inválido (solo letras y espacios)", "Apellido");
        }

        // Valida el formato del password (8-14 caracteres, al menos una mayúscula)
        if (!Validador.validarPassword(usuario.getPassword())) {
            throw new ValidationException("Password debe tener entre 8 y 14 caracteres y al menos una mayúscula", "Password");
        }

        // Valida el teléfono solo si fue proporcionado (opcional)
        if (usuario.getTelefono() != null && !usuario.getTelefono().isEmpty()) {
            if (!Validador.validarTelefono(usuario.getTelefono())) {
                throw new ValidationException("Teléfono inválido (debe tener 10 dígitos)", "Teléfono");
            }
        }

        // Valida la fecha de nacimiento
        if (!Validador.validarFechaNacimiento(usuario.getFechaNacimiento())) {
            throw new ValidationException("Fecha de nacimiento inválida", "Fecha Nacimiento");
        }

        // Agrega el usuario a la lista
        boolean resultado = usuarios.add(usuario);

        // Si se agregó correctamente, guarda los datos
        if (resultado) {
            guardarDatosAutomaticamente();
        }

        // Retorna el resultado de la operación
        return resultado;
    }

    public boolean eliminarCuentaUsuario() throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Usuario usuario = usuarioActual;
        boolean eliminado = usuarios.removeIf(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()));
        if (eliminado) {
            centro().anonimizarSolicitudesDe(usuario.getEmail());
            usuarioActual = null;
            guardarDatosAutomaticamente();
        }
        return eliminado;
    }

    public boolean iniciarSesion(String email, String password) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Debe ingresar un email", "Email");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Debe ingresar un password", "Password");
        }
        Administrador administrador = centro().buscarAdministrador(email).orElse(null);
        if (administrador != null) {
            return iniciarSesionAdministrador(administrador, password);
        }
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                if (u.validarPassword(password)) {
                    if (u.isSuspendida()) {
                        throw new CuentaSuspendidaException(u.getEmail());
                    }
                    usuarioActual = u;
                    administradorActual = null;
                    return true;
                } else {
                    throw new ValidationException("Password incorrecto", "Password");
                }
            }
        }
        throw new ValidationException("No existe un usuario con este email", "Email");
    }

    public void cerrarSesion() {
        usuarioActual = null;
        administradorActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean hayUsuarioLogueado() {
        return usuarioActual != null;
    }

    public int getCantidadUsuarios() {
        return usuarios.size();
    }

    public ArrayList<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    // ==================== MÉTODOS CRUD CONTACTOS ====================

    public boolean agregarContacto(Contacto contacto) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        if (!Validador.validarCampoNoVacio(contacto.getEmail(), "Email")) {
            return false;
        }
        if (!Validador.validarEmail(contacto.getEmail())) {
            throw new ValidationException("Email inválido", "Email");
        }
        String emailNormalizado = contacto.getEmail().trim().toLowerCase();
        contacto.setEmail(emailNormalizado);
        if (contacto.getEmail().equalsIgnoreCase(usuarioActual.getEmail())) {
            throw new ValidationException("No puedes agregarte a ti mismo como contacto", "Email");
        }
        for (Contacto c : usuarioActual.getContactos()) {
            if (c.getEmail().equalsIgnoreCase(contacto.getEmail())) {
                throw new ValidationException("Ya existe un contacto con este email", "Email");
            }
        }
        if (contacto.getNombre() != null && !contacto.getNombre().trim().isEmpty()) {
            if (!Validador.validarNombre(contacto.getNombre())) {
                throw new ValidationException("Nombre inválido (solo letras y espacios)", "Nombre");
            }
        }
        usuarioActual.agregarContacto(contacto);
        guardarDatosAutomaticamente();
        return true;
    }

    public boolean modificarContacto(String emailAntiguo, Contacto contactoNuevo) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Contacto existente = usuarioActual.buscarContactoPorEmail(emailAntiguo);
        if (existente == null) {
            throw new ValidationException("No existe un contacto con email: " + emailAntiguo);
        }
        if (!existente.getEmail().equalsIgnoreCase(contactoNuevo.getEmail())) {
            if (!Validador.validarEmail(contactoNuevo.getEmail())) {
                throw new ValidationException("Email inválido", "Email");
            }
            String emailNormalizado = contactoNuevo.getEmail().trim().toLowerCase();
            contactoNuevo.setEmail(emailNormalizado);
            if (contactoNuevo.getEmail().equalsIgnoreCase(usuarioActual.getEmail())) {
                throw new ValidationException("No puedes usar tu propio email como contacto", "Email");
            }
            for (Contacto c : usuarioActual.getContactos()) {
                if (c.getEmail().equalsIgnoreCase(contactoNuevo.getEmail()) &&
                        !c.getEmail().equalsIgnoreCase(emailAntiguo)) {
                    throw new ValidationException("Ya existe otro contacto con este email", "Email");
                }
            }
        }
        if (contactoNuevo.getNombre() != null && !contactoNuevo.getNombre().trim().isEmpty()) {
            if (!Validador.validarNombre(contactoNuevo.getNombre())) {
                throw new ValidationException("Nombre inválido", "Nombre");
            }
        }
        boolean resultado = usuarioActual.modificarContacto(emailAntiguo, contactoNuevo);
        if (resultado) {
            guardarDatosAutomaticamente();
        }
        return resultado;
    }

    public boolean eliminarContacto(String email) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        boolean resultado = usuarioActual.eliminarContactoPorEmail(email);
        if (resultado) {
            guardarDatosAutomaticamente();
        }
        return resultado;
    }

    public Contacto buscarContactoPorEmail(String email) {
        if (!hayUsuarioLogueado()) {
            return null;
        }
        return usuarioActual.buscarContactoPorEmail(email);
    }

    public Contacto buscarContactoPorNombre(String nombre) {
        if (!hayUsuarioLogueado()) {
            return null;
        }
        return usuarioActual.buscarContactoPorNombre(nombre);
    }

    public ArrayList<Contacto> listarContactos() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(usuarioActual.getContactos());
    }

    // ==================== MÉTODOS CRUD GRUPOS ====================

    public boolean agregarGrupo(Grupo grupo) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        if (!Validador.validarCampoNoVacio(grupo.getNombre(), "Nombre del grupo")) {
            return false;
        }
        for (Grupo g : usuarioActual.getGrupos()) {
            if (g.getNombre().equalsIgnoreCase(grupo.getNombre())) {
                throw new ValidationException("Ya existe un grupo con este nombre", "Nombre");
            }
        }
        grupo.setEmailAdministrador(usuarioActual.getEmail());
        boolean creadorYaEsMiembro = false;
        for (Contacto miembro : grupo.getMiembros()) {
            if (miembro.getEmail().equalsIgnoreCase(usuarioActual.getEmail())) {
                creadorYaEsMiembro = true;
                break;
            }
        }
        if (!creadorYaEsMiembro) {
            Contacto creadorComoMiembro = new Contacto(
                    usuarioActual.getEmail(),
                    usuarioActual.getNombreCompleto()
            );
            grupo.getMiembros().add(0, creadorComoMiembro);
        }
        usuarioActual.agregarGrupo(grupo);
        guardarDatosAutomaticamente();
        return true;
    }

    public boolean modificarGrupo(String nombreAntiguo, Grupo grupoNuevo) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Grupo existente = usuarioActual.buscarGrupoPorNombre(nombreAntiguo);
        if (existente == null) {
            throw new ValidationException("No existe un grupo con nombre: " + nombreAntiguo);
        }
        if (!Validador.validarCampoNoVacio(grupoNuevo.getNombre(), "Nombre del grupo")) {
            return false;
        }
        if (!nombreAntiguo.equalsIgnoreCase(grupoNuevo.getNombre())) {
            for (Grupo g : usuarioActual.getGrupos()) {
                if (g.getNombre().equalsIgnoreCase(grupoNuevo.getNombre())) {
                    throw new ValidationException("Ya existe otro grupo con este nombre", "Nombre");
                }
            }
        }
        boolean resultado = usuarioActual.modificarGrupo(nombreAntiguo, grupoNuevo);
        if (resultado) {
            guardarDatosAutomaticamente();
        }
        return resultado;
    }

    public boolean eliminarGrupo(String nombre) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        boolean resultado = usuarioActual.eliminarGrupoPorNombre(nombre);
        if (resultado) {
            guardarDatosAutomaticamente();
        }
        return resultado;
    }

    public Grupo buscarGrupoPorNombre(String nombre) {
        if (!hayUsuarioLogueado()) {
            return null;
        }
        Grupo grupo = usuarioActual.buscarGrupoPorNombre(nombre);
        if (grupo != null) {
            return grupo;
        }
        String emailActual = usuarioActual.getEmail();
        for (Usuario u : usuarios) {
            grupo = u.buscarGrupoPorNombre(nombre);
            if (grupo != null && (grupo.esMiembro(emailActual) || grupo.esAdministrador(emailActual))) {
                return grupo;
            }
        }
        return null;
    }

    public Grupo buscarGrupoPorDescripcion(String descripcion) {
        if (!hayUsuarioLogueado()) {
            return null;
        }
        Grupo grupo = usuarioActual.buscarGrupoPorDescripcion(descripcion);
        if (grupo != null) {
            return grupo;
        }
        String emailActual = usuarioActual.getEmail();
        for (Usuario u : usuarios) {
            grupo = u.buscarGrupoPorDescripcion(descripcion);
            if (grupo != null && (grupo.esMiembro(emailActual) || grupo.esAdministrador(emailActual))) {
                return grupo;
            }
        }
        return null;
    }

    public boolean esAdministradorGrupo(String nombreGrupo) {
        if (!hayUsuarioLogueado()) {
            return false;
        }
        Grupo grupo = buscarGrupoPorNombre(nombreGrupo);
        return grupo != null && grupo.esAdministrador(usuarioActual.getEmail());
    }

    public boolean enviarMensajeGrupal(String nombreGrupo, MensajeGrupo mensaje) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Grupo grupo = buscarGrupoPorNombre(nombreGrupo);
        if (grupo == null) {
            throw new ValidationException("No existe un grupo con nombre: " + nombreGrupo);
        }
        String emailActual = usuarioActual.getEmail();
        if (!grupo.esMiembro(emailActual) && !grupo.esAdministrador(emailActual)) {
            throw new ValidationException("No eres miembro de este grupo");
        }
        grupo.agregarMensaje(mensaje);
        guardarDatosAutomaticamente();
        return true;
    }

    public ArrayList<Grupo> listarTodosLosGrupos() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Grupo> todosLosGrupos = new ArrayList<>();
        String emailActual = usuarioActual.getEmail();
        todosLosGrupos.addAll(usuarioActual.getGrupos());
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuarioActual.getEmail())) {
                continue;
            }
            for (Grupo g : u.getGrupos()) {
                if (g.esMiembro(emailActual) || g.esAdministrador(emailActual)) {
                    boolean yaExiste = false;
                    for (Grupo existente : todosLosGrupos) {
                        if (existente.getNombre().equalsIgnoreCase(g.getNombre())) {
                            yaExiste = true;
                            break;
                        }
                    }
                    if (!yaExiste) {
                        todosLosGrupos.add(g);
                    }
                }
            }
        }
        return todosLosGrupos;
    }

    // ==================== MÉTODOS CRUD CORREOS ====================

    public boolean guardarCorreo(Correo correo) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        if (!Validador.validarCampoNoVacio(correo.getAsunto(), "Asunto")) {
            return false;
        }
        if (!Validador.validarCampoNoVacio(correo.getCuerpo(), "Cuerpo")) {
            return false;
        }
        correo.setRemitente(usuarioActual.getEmail());
        for (String email : correo.getDestinatarios()) {
            if (!Validador.validarEmail(email)) {
                throw new ValidationException("Email de destinatario inválido: " + email, "Destinatarios");
            }
        }
        for (String email : correo.getCc()) {
            if (!Validador.validarEmail(email)) {
                throw new ValidationException("Email de CC inválido: " + email, "CC");
            }
        }
        for (String email : correo.getCco()) {
            if (!Validador.validarEmail(email)) {
                throw new ValidationException("Email de CCO inválido: " + email, "CCO");
            }
        }
        correo.setEstado(EstadoCorreo.BORRADOR);
        usuarioActual.agregarCorreo(correo);
        guardarDatosAutomaticamente();
        return true;
    }

    public boolean eliminarCorreo(String asunto) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Correo correo = buscarCorreoPorAsunto(asunto);
        if (correo == null) {
            throw new ValidationException("No existe un correo con asunto: " + asunto);
        }
        correo.setEstado(EstadoCorreo.ELIMINADO);
        guardarDatosAutomaticamente();
        return true;
    }

    public boolean eliminarCorreoDefinitivamente(String asunto) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        boolean resultado = usuarioActual.eliminarCorreoPorAsunto(asunto);
        if (resultado) {
            guardarDatosAutomaticamente();
        }
        return resultado;
    }

    public Correo buscarCorreoPorAsunto(String asunto) {
        if (!hayUsuarioLogueado()) {
            return null;
        }
        Correo correo = usuarioActual.buscarCorreoPorAsunto(asunto);
        if (correo != null) {
            return correo;
        }
        for (Usuario u : usuarios) {
            correo = u.buscarCorreoPorAsunto(asunto);
            if (correo != null) {
                return correo;
            }
        }
        return null;
    }

    public Correo buscarCorreoPorRemitente(String remitente) {
        if (!hayUsuarioLogueado()) {
            return null;
        }
        return usuarioActual.buscarCorreoPorRemitente(remitente);
    }

    public ArrayList<Correo> buscarCorreosPorRemitente(String remitente) {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        return usuarioActual.buscarCorreosPorRemitente(remitente);
    }

    public ArrayList<Correo> listarCorreosPorEstado(EstadoCorreo estado) {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Correo> resultados = new ArrayList<>();
        for (Correo c : usuarioActual.getCorreos()) {
            if (c.getEstado() == estado) {
                resultados.add(c);
            }
        }
        return resultados;
    }

    public ArrayList<Correo> listarCorreosEnviados() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Correo> enviados = new ArrayList<>();
        for (Correo c : usuarioActual.getCorreos()) {
            if (c.getEstado() == EstadoCorreo.ENVIADO &&
                    c.getRemitente().equals(usuarioActual.getEmail()) &&
                    !c.isEsReenviado()) {
                enviados.add(c);
            }
        }
        return enviados;
    }

    public ArrayList<Correo> listarCorreosRecibidos() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Correo> recibidos = new ArrayList<>();
        for (Usuario u : usuarios) {
            for (Correo c : u.getCorreos()) {
                if ((c.getEstado() == EstadoCorreo.ENVIADO || c.getEstado() == EstadoCorreo.REENVIADO) &&
                        !c.getRemitente().equalsIgnoreCase(usuarioActual.getEmail()) &&
                        (c.getDestinatarios().contains(usuarioActual.getEmail()) ||
                                c.getCc().contains(usuarioActual.getEmail()) ||
                                c.getCco().contains(usuarioActual.getEmail()))) {
                    recibidos.add(c);
                }
            }
        }
        return recibidos;
    }

    public ArrayList<Correo> listarBorradores() {
        return listarCorreosPorEstado(EstadoCorreo.BORRADOR);
    }

    public ArrayList<Correo> listarCorreosReenviados() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Correo> reenviados = new ArrayList<>();
        for (Correo c : usuarioActual.getCorreos()) {
            if (c.getEstado() == EstadoCorreo.REENVIADO &&
                    c.getRemitente().equals(usuarioActual.getEmail())) {
                reenviados.add(c);
            }
        }
        return reenviados;
    }

    public ArrayList<Correo> listarCorreosEliminados() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Correo> eliminados = new ArrayList<>();
        for (Correo c : usuarioActual.getCorreos()) {
            if (c.getEstado() == EstadoCorreo.ELIMINADO) {
                eliminados.add(c);
            }
        }
        return eliminados;
    }

    public boolean enviarCorreo(String asunto) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Correo correo = buscarCorreoPorAsunto(asunto);
        if (correo == null) {
            throw new ValidationException("No existe un correo con asunto: " + asunto);
        }
        if (correo.getEstado() == EstadoCorreo.ENVIADO) {
            throw new ValidationException("El correo ya fue enviado");
        }
        validarDestinatariosExisten(correo);
        if (!correo.getRemitente().equals(usuarioActual.getEmail())) {
            throw new ValidationException("Solo puede enviar correos propios");
        }
        correo.setEstado(EstadoCorreo.ENVIADO);
        correo.setFechaEnvio(LocalDateTime.now());
        guardarDatosAutomaticamente();
        return true;
    }

    public boolean reenviarCorreo(String asuntoOriginal, String nuevoDestinatario) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Correo correoOriginal = buscarCorreoPorAsunto(asuntoOriginal);
        if (correoOriginal == null) {
            throw new ValidationException("No existe un correo con asunto: " + asuntoOriginal);
        }
        if (!Validador.validarEmail(nuevoDestinatario)) {
            throw new ValidationException("Email del destinatario inválido", "Destinatario");
        }
        if (buscarUsuarioOpcional(normalizarEmail(nuevoDestinatario)) == null) {
            throw new ValidationException("El usuario " + nuevoDestinatario + " no existe", "Destinatario");
        }
        Correo correoReenviado = correoOriginal.clonarParaReenvio(usuarioActual.getEmail());
        correoReenviado.agregarDestinatario(nuevoDestinatario.toLowerCase());
        correoReenviado.setEstado(EstadoCorreo.REENVIADO);
        correoReenviado.setFechaEnvio(LocalDateTime.now());
        usuarioActual.agregarCorreo(correoReenviado);
        guardarDatosAutomaticamente();
        return true;
    }

    public ArrayList<Correo> listarTodosLosCorreosActivos() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Correo> todosActivos = new ArrayList<>();
        String emailUsuario = usuarioActual.getEmail();
        for (Correo c : usuarioActual.getCorreos()) {
            if (c.getEstado() != EstadoCorreo.ELIMINADO) {
                todosActivos.add(c);
            }
        }
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(usuarioActual.getEmail())) {
                continue;
            }
            for (Correo c : u.getCorreos()) {
                if (c.getEstado() != EstadoCorreo.ELIMINADO &&
                        (c.getEstado() == EstadoCorreo.ENVIADO || c.getEstado() == EstadoCorreo.REENVIADO) &&
                        (c.getDestinatarios().contains(emailUsuario) ||
                                c.getCc().contains(emailUsuario) ||
                                c.getCco().contains(emailUsuario))) {
                    todosActivos.add(c);
                }
            }
        }
        return todosActivos;
    }

    public boolean enviarBorrador(String asunto) throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        Correo correo = buscarCorreoPorAsunto(asunto);
        if (correo == null) {
            throw new ValidationException("No existe un correo con asunto: " + asunto);
        }
        if (correo.getEstado() != EstadoCorreo.BORRADOR) {
            throw new ValidationException("El correo no es un borrador");
        }
        if (!correo.getRemitente().equals(usuarioActual.getEmail())) {
            throw new ValidationException("Solo puede enviar sus propios borradores");
        }
        if (correo.getDestinatarios().isEmpty() && correo.getCc().isEmpty() && correo.getCco().isEmpty()) {
            throw new ValidationException("El correo debe tener al menos un destinatario");
        }
        correo.setEstado(EstadoCorreo.ENVIADO);
        correo.setFechaEnvio(LocalDateTime.now());
        guardarDatosAutomaticamente();
        return true;
    }

    public ArrayList<Correo> listarCorreosBorradores() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        ArrayList<Correo> borradores = new ArrayList<>();
        for (Correo c : usuarioActual.getCorreos()) {
            if (c.getEstado() == EstadoCorreo.BORRADOR) {
                borradores.add(c);
            }
        }
        return borradores;
    }

    private static final Pattern PATRON_EMAIL_ADMIN =
            Pattern.compile("^[a-z0-9._+-]+@[a-z0-9-]+(\\.[a-z0-9-]+)+$");

    private CentroAdministracion centro() {
        if (administracion == null) {
            administracion = new CentroAdministracion();
            administracion.asegurarAdministradorPrincipal();
        }
        return administracion;
    }

    private void inicializarAdministracion() {
        boolean huboCambios = administracion == null;
        if (huboCambios) {
            administracion = new CentroAdministracion();
        }
        if (administracion.asegurarAdministradorPrincipal()) {
            huboCambios = true;
        }
        if (huboCambios) {
            guardarDatosAutomaticamente();
        }
    }

    private ControlIntentos controlRecuperacion() {
        if (controlRecuperacion == null) {
            controlRecuperacion = new ControlIntentos(3, Duration.ofMinutes(15));
        }
        return controlRecuperacion;
    }

    private boolean iniciarSesionAdministrador(Administrador admin, String password) throws ValidationException {
        if (!admin.validarPassword(password)) {
            centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.LOGIN_ADMIN_FALLIDO, admin.getEmail(),
                    "Contraseña incorrecta");
            guardarDatosAutomaticamente();
            throw new ValidationException("Password incorrecto", "Password");
        }
        usuarioActual = null;
        administradorActual = admin;
        centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.LOGIN_ADMIN, admin.getEmail(), "Inicio de sesión");
        guardarDatosAutomaticamente();
        return true;
    }

    public CentroAdministracion getCentroAdministracion() {
        return centro();
    }

    public boolean hayAdministradorLogueado() {
        return administradorActual != null;
    }

    public Administrador getAdministradorActual() {
        return administradorActual;
    }

    private Administrador exigirAdministrador() throws ValidationException {
        if (administradorActual == null) {
            throw new ValidationException("Debe iniciar sesión como administrador");
        }
        return administradorActual;
    }

    private Administrador exigirAdministradorPrincipal() throws ValidationException {
        Administrador admin = exigirAdministrador();
        if (!admin.esPrincipal()) {
            throw new ValidationException("Esta acción solo está permitida al administrador principal");
        }
        return admin;
    }

    private Usuario buscarUsuarioPorEmail(String email) throws ValidationException {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new ValidationException("No existe un usuario con este email", "Email"));
    }

    private String exigirMotivo(String motivo) throws ValidationException {
        String limpio = motivo == null ? "" : motivo.trim();
        if (limpio.length() < 10) {
            throw new ValidationException("Indique un motivo de al menos 10 caracteres", "Motivo");
        }
        return limpio;
    }

    public boolean cambiarPasswordAdministrador(String passwordActual, String passwordNueva)
            throws ValidationException {
        Administrador admin = exigirAdministrador();
        if (!admin.validarPassword(passwordActual)) {
            throw new ValidationException("La contraseña actual es incorrecta", "Contraseña actual");
        }
        if (!Validador.validarPassword(passwordNueva)) {
            throw new ValidationException(
                    "La contraseña debe tener entre 8 y 14 caracteres y al menos una mayúscula", "Nueva contraseña");
        }
        if (admin.validarPassword(passwordNueva)) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la actual", "Nueva contraseña");
        }
        Administrador actualizado = admin.conNuevaPassword(passwordNueva, false);
        centro().reemplazarAdministrador(actualizado);
        administradorActual = actualizado;
        centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.CAMBIO_PASSWORD_ADMIN, admin.getEmail(), "Contraseña actualizada");
        guardarDatosAutomaticamente();
        return true;
    }

    public List<Administrador> listarAdministradores() throws ValidationException {
        exigirAdministradorPrincipal();
        return centro().getAdministradores();
    }

    public Administrador crearAdministrador(String email, String nombre, String passwordTemporal)
            throws ValidationException {
        Administrador principal = exigirAdministradorPrincipal();
        String emailNormalizado = email == null ? "" : email.trim().toLowerCase();
        if (!PATRON_EMAIL_ADMIN.matcher(emailNormalizado).matches()) {
            throw new ValidationException("Email inválido", "Email");
        }
        if (!ConfiguracionAdmin.esEmailDeAdministrador(emailNormalizado)) {
            throw new ValidationException(
                    "Los administradores deben usar un correo " + ConfiguracionAdmin.DOMINIO_ADMIN, "Email");
        }
        if (usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(emailNormalizado))) {
            throw new ValidationException("Este email pertenece a un usuario del sistema", "Email");
        }
        if (!Validador.validarNombre(nombre)) {
            throw new ValidationException("Nombre inválido (solo letras y espacios)", "Nombre");
        }
        if (!Validador.validarPassword(passwordTemporal)) {
            throw new ValidationException(
                    "La contraseña debe tener entre 8 y 14 caracteres y al menos una mayúscula", "Contraseña");
        }
        Administrador nuevo = Administrador.crear(emailNormalizado, nombre.trim(), passwordTemporal,
                false, true, principal.getEmail());
        centro().agregarAdministrador(nuevo);
        centro().registrarAuditoria(principal.getEmail(), AccionAuditoria.CREAR_ADMIN, emailNormalizado,
                "Administrador secundario creado");
        guardarDatosAutomaticamente();
        return nuevo;
    }

    public boolean eliminarAdministrador(String email) throws ValidationException {
        Administrador principal = exigirAdministradorPrincipal();
        Administrador objetivo = centro().buscarAdministrador(email)
                .orElseThrow(() -> new ValidationException("No existe ese administrador", "Email"));
        if (objetivo.esPrincipal()) {
            throw new ValidationException("No se puede eliminar al administrador principal");
        }
        centro().eliminarAdministrador(objetivo.getEmail());
        centro().registrarAuditoria(principal.getEmail(), AccionAuditoria.ELIMINAR_ADMIN, objetivo.getEmail(),
                "Administrador secundario eliminado");
        guardarDatosAutomaticamente();
        return true;
    }

    public List<ResumenUsuario> listarResumenUsuarios() throws ValidationException {
        exigirAdministrador();
        return usuarios.stream().map(this::resumirUsuario).toList();
    }

    private int contarEnviados(Usuario usuario) {
        return (int) usuario.getCorreos().stream()
                .filter(c -> (c.getEstado() == EstadoCorreo.ENVIADO || c.getEstado() == EstadoCorreo.REENVIADO)
                        && c.getRemitente().equalsIgnoreCase(usuario.getEmail()))
                .count();
    }

    private ResumenUsuario resumirUsuario(Usuario u) {
        return new ResumenUsuario(
                u.getEmail(),
                u.getNombreCompleto(),
                SeguridadUtil.enmascararCedula(u.getCedula()),
                Validador.obtenerProvinciaPorCedula(u.getCedula()),
                SeguridadUtil.enmascararTelefono(u.getTelefono()),
                u.getFechaRegistro(),
                contarEnviados(u),
                u.getContactos().size(),
                u.getGrupos().size(),
                u.isSuspendida());
    }

    public void suspenderUsuario(String email, String motivo) throws ValidationException {
        Administrador admin = exigirAdministrador();
        String motivoLimpio = exigirMotivo(motivo);
        Usuario usuario = buscarUsuarioPorEmail(email);
        usuario.suspender(motivoLimpio);
        centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.SUSPENDER_USUARIO,
                SeguridadUtil.enmascararEmail(usuario.getEmail()), motivoLimpio);
        guardarDatosAutomaticamente();
    }

    public void reactivarUsuario(String email) throws ValidationException {
        Administrador admin = exigirAdministrador();
        Usuario usuario = buscarUsuarioPorEmail(email);
        usuario.reactivar();
        for (Solicitud apelacion : centro().solicitudesDe(usuario.getEmail())) {
            if (apelacion.getTipo() == TipoSolicitud.APELACION && apelacion.getEstado().estaAbierta()) {
                centro().reemplazarSolicitud(apelacion.conRespuesta(EstadoSolicitud.RESUELTA,
                        "Su cuenta fue reactivada.", admin.getEmail()));
                centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.ATENDER_SOLICITUD,
                        "#" + apelacion.getId(), EstadoSolicitud.RESUELTA.name());
            }
        }
        centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.REACTIVAR_USUARIO,
                SeguridadUtil.enmascararEmail(usuario.getEmail()), "Cuenta reactivada");
        guardarDatosAutomaticamente();
    }

    public void eliminarUsuarioComoAdmin(String email, String motivo) throws ValidationException {
        Administrador admin = exigirAdministrador();
        String motivoLimpio = exigirMotivo(motivo);
        Usuario usuario = buscarUsuarioPorEmail(email);
        usuarios.remove(usuario);
        centro().anonimizarSolicitudesDe(usuario.getEmail());
        centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.ELIMINAR_USUARIO,
                SeguridadUtil.enmascararEmail(usuario.getEmail()), motivoLimpio);
        guardarDatosAutomaticamente();
    }

    private static <T> Map<String, Long> contarPor(Collection<T> elementos, Function<T, String> clasificador) {
        return elementos.stream().collect(Collectors.groupingBy(
                e -> Objects.requireNonNullElse(clasificador.apply(e), "Desconocido"),
                TreeMap::new,
                Collectors.counting()));
    }

    public EstadisticasSistema obtenerEstadisticas() throws ValidationException {
        exigirAdministrador();
        List<Solicitud> solicitudes = centro().getSolicitudes();
        return new EstadisticasSistema(
                usuarios.size(),
                (int) usuarios.stream().filter(Usuario::isSuspendida).count(),
                centro().getAdministradores().size(),
                usuarios.stream().mapToInt(this::contarEnviados).sum(),
                usuarios.stream().mapToInt(u -> u.getGrupos().size()).sum(),
                solicitudes.size(),
                (int) solicitudes.stream().filter(s -> s.getEstado().estaAbierta()).count(),
                contarPor(usuarios, u -> u.getEmail().substring(u.getEmail().indexOf('@') + 1)),
                contarPor(usuarios, u -> Validador.obtenerProvinciaPorCedula(u.getCedula())),
                contarPor(usuarios, u -> u.getGenero().getDescripcion()),
                contarPor(solicitudes, s -> s.getTipo().getDescripcion()),
                contarPor(solicitudes, s -> s.getEstado().getDescripcion()));
    }

    public Solicitud crearSolicitud(TipoSolicitud tipo, String asunto, String descripcion)
            throws ValidationException {
        if (!hayUsuarioLogueado()) {
            throw new ValidationException("Debe iniciar sesión primero");
        }
        if (tipo == TipoSolicitud.APELACION) {
            throw new ValidationException("Tipo de solicitud no permitido", "Tipo");
        }
        Solicitud solicitud = centro().agregarSolicitud(usuarioActual.getEmail(), tipo, asunto, descripcion);
        guardarDatosAutomaticamente();
        return solicitud;
    }

    public List<Solicitud> listarMisSolicitudes() {
        if (!hayUsuarioLogueado()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(centro().solicitudesDe(usuarioActual.getEmail()));
    }

    public List<Solicitud> listarSolicitudesAdmin() throws ValidationException {
        exigirAdministrador();
        return centro().getSolicitudes();
    }

    public Solicitud responderSolicitud(int id, EstadoSolicitud nuevoEstado, String respuesta)
            throws ValidationException {
        Administrador admin = exigirAdministrador();
        Solicitud actual = centro().buscarSolicitud(id)
                .orElseThrow(() -> new ValidationException("No existe la solicitud #" + id));
        if (nuevoEstado == null || nuevoEstado == EstadoSolicitud.PENDIENTE) {
            throw new ValidationException("Seleccione un estado válido", "Estado");
        }
        if (actual.getTipo() == TipoSolicitud.APELACION && !actual.getEstado().estaAbierta()) {
            throw new ValidationException("Esta apelación ya fue atendida");
        }
        String texto = respuesta == null ? "" : respuesta.trim();
        if (texto.length() > CentroAdministracion.DESCRIPCION_MAX) {
            throw new ValidationException("La respuesta es demasiado larga", "Respuesta");
        }
        if (nuevoEstado != EstadoSolicitud.EN_REVISION && texto.length() < 10) {
            throw new ValidationException("Escriba una respuesta de al menos 10 caracteres para el usuario",
                    "Respuesta");
        }
        String respuestaFinal = texto.isEmpty() ? actual.getRespuesta() : texto;
        Solicitud actualizada = actual.conRespuesta(nuevoEstado, respuestaFinal, admin.getEmail());
        centro().reemplazarSolicitud(actualizada);
        centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.ATENDER_SOLICITUD, "#" + id, nuevoEstado.name());
        if (actual.getTipo() == TipoSolicitud.APELACION && nuevoEstado == EstadoSolicitud.RESUELTA) {
            Usuario cuenta = buscarUsuarioOpcional(normalizarEmail(actual.getEmailUsuario()));
            if (cuenta != null && cuenta.isSuspendida()) {
                cuenta.reactivar();
                centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.REACTIVAR_USUARIO,
                        SeguridadUtil.enmascararEmail(cuenta.getEmail()), "Apelación aceptada");
            }
        }
        guardarDatosAutomaticamente();
        return actualizada;
    }

    public List<RegistroAuditoria> listarAuditoria() throws ValidationException {
        exigirAdministradorPrincipal();
        return centro().getAuditoria();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private Usuario buscarUsuarioOpcional(String emailNormalizado) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(emailNormalizado))
                .findFirst()
                .orElse(null);
    }

    public long segundosBloqueoRecuperacion(String email) {
        return controlRecuperacion().segundosRestantes(normalizarEmail(email));
    }

    private void verificarRecuperacionDisponible(String clave) throws ValidationException {
        long segundos = controlRecuperacion().segundosRestantes(clave);
        if (segundos > 0) {
            throw new ValidationException(
                    "Demasiados intentos fallidos. Intente de nuevo en " + ((segundos + 59) / 60) + " minuto(s).");
        }
    }

    public List<PreguntaSeguridad> obtenerPreguntasRecuperacion(String email) throws ValidationException {
        String clave = normalizarEmail(email);
        if (clave.isEmpty()) {
            throw new ValidationException("Ingrese su email", "Email");
        }
        verificarRecuperacionDisponible(clave);
        Usuario usuario = buscarUsuarioOpcional(clave);
        if (usuario == null) {
            String mensaje = centro().buscarAdministrador(clave).isPresent()
                    ? "Las cuentas de administrador no se recuperan por este medio"
                    : "No existe una cuenta con este email";
            throw new ValidationException(mensaje, "Email");
        }
        List<PreguntaSeguridad> candidatas = new ArrayList<>();
        for (PreguntaSeguridad pregunta : PreguntaSeguridad.values()) {
            if (pregunta.estaDisponiblePara(usuario)) {
                candidatas.add(pregunta);
            }
        }
        Collections.shuffle(candidatas);
        return new ArrayList<>(candidatas.subList(0, Math.min(2, candidatas.size())));
    }

    public boolean restablecerPassword(String email, Map<PreguntaSeguridad, String> respuestas,
                                       String passwordNueva) throws ValidationException {
        String clave = normalizarEmail(email);
        verificarRecuperacionDisponible(clave);
        Usuario usuario = buscarUsuarioOpcional(clave);
        boolean coincide = usuario != null
                && respuestas != null
                && respuestas.size() >= 2
                && respuestas.entrySet().stream().allMatch(e -> e.getKey().verificar(usuario, e.getValue()));
        if (!coincide) {
            controlRecuperacion().registrarFallo(clave);
            int restantes = controlRecuperacion().intentosRestantes(clave);
            if (restantes == 0) {
                throw new ValidationException(
                        "Los datos no coinciden. Se bloqueó la recuperación de esta cuenta por 15 minutos",
                        "Verificación");
            }
            throw new ValidationException("Los datos ingresados no coinciden con los de la cuenta. "
                    + "Intentos restantes: " + restantes, "Verificación");
        }
        if (!Validador.validarPassword(passwordNueva)) {
            throw new ValidationException(
                    "La contraseña debe tener entre 8 y 14 caracteres y al menos una mayúscula", "Nueva contraseña");
        }
        if (usuario.validarPassword(passwordNueva)) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la anterior", "Nueva contraseña");
        }
        usuario.setPassword(passwordNueva);
        controlRecuperacion().reiniciar(clave);
        guardarDatosAutomaticamente();
        return true;
    }

    public void validarDestinatariosExisten(Correo correo) throws ValidationException {
        List<String> inexistentes = new ArrayList<>();
        List<String> todos = new ArrayList<>(correo.getDestinatarios());
        todos.addAll(correo.getCc());
        todos.addAll(correo.getCco());
        for (String email : todos) {
            if (buscarUsuarioOpcional(normalizarEmail(email)) == null && !inexistentes.contains(email)) {
                inexistentes.add(email);
            }
        }
        if (inexistentes.size() == 1) {
            throw new ValidationException("El usuario " + inexistentes.get(0) + " no existe", "Destinatarios");
        }
        if (!inexistentes.isEmpty()) {
            throw new ValidationException(
                    "Los siguientes usuarios no existen: " + String.join(", ", inexistentes), "Destinatarios");
        }
    }

    private Usuario verificarCuentaSuspendida(String email, String password) throws ValidationException {
        String clave = "apelacion:" + normalizarEmail(email);
        verificarRecuperacionDisponible(clave);
        Usuario usuario = buscarUsuarioOpcional(normalizarEmail(email));
        if (usuario == null || !usuario.validarPassword(password) || !usuario.isSuspendida()) {
            controlRecuperacion().registrarFallo(clave);
            throw new ValidationException("No se pudo verificar la cuenta o ya no está suspendida", "Email");
        }
        controlRecuperacion().reiniciar(clave);
        return usuario;
    }

    public InfoSuspension consultarSuspension(String email, String password) throws ValidationException {
        Usuario usuario = verificarCuentaSuspendida(email, password);
        List<Solicitud> apelaciones = centro().solicitudesDe(usuario.getEmail()).stream()
                .filter(s -> s.getTipo() == TipoSolicitud.APELACION)
                .toList();
        return new InfoSuspension(usuario.getMotivoSuspension(), usuario.getFechaSuspension(), apelaciones);
    }

    public Solicitud enviarApelacion(String email, String password, String descripcion) throws ValidationException {
        Usuario usuario = verificarCuentaSuspendida(email, password);
        boolean hayAbierta = centro().solicitudesDe(usuario.getEmail()).stream()
                .anyMatch(s -> s.getTipo() == TipoSolicitud.APELACION && s.getEstado().estaAbierta());
        if (hayAbierta) {
            throw new ValidationException("Ya tiene una apelación en curso. Espere la respuesta del soporte.");
        }
        Solicitud apelacion = centro().agregarSolicitud(usuario.getEmail(), TipoSolicitud.APELACION,
                "Apelación de cuenta suspendida", descripcion);
        guardarDatosAutomaticamente();
        return apelacion;
    }

    public Solicitud ultimaApelacion(String email) throws ValidationException {
        exigirAdministrador();
        return centro().solicitudesDe(email).stream()
                .filter(s -> s.getTipo() == TipoSolicitud.APELACION)
                .max(java.util.Comparator.comparingInt(Solicitud::getId))
                .orElse(null);
    }

    public InfoSuspension consultarSuspensionComoAdmin(String email) throws ValidationException {
        exigirAdministrador();
        Usuario usuario = buscarUsuarioOpcional(normalizarEmail(email));
        if (usuario == null || !usuario.isSuspendida()) {
            return null;
        }
        return new InfoSuspension(usuario.getMotivoSuspension(), usuario.getFechaSuspension(), List.of());
    }

    public List<TipoReporte> tiposDeReportePermitidos() throws ValidationException {
        Administrador admin = exigirAdministrador();
        return java.util.Arrays.stream(TipoReporte.values()).filter(t -> t.estaPermitidoPara(admin)).toList();
    }

    public List<HojaReporte> generarReporte(TipoReporte tipo, PeriodoReporte periodo, LocalDate referencia)
            throws ValidationException {
        Administrador admin = exigirAdministrador();
        if (tipo == null || periodo == null || referencia == null) {
            throw new ValidationException("Seleccione el tipo de reporte, el periodo y la fecha");
        }
        if (!tipo.estaPermitidoPara(admin)) {
            throw new ValidationException("Este reporte solo está permitido al administrador principal");
        }
        RangoFechas rango = periodo.rango(referencia);
        if (rango.desde().isAfter(java.time.LocalDateTime.now())) {
            throw new ValidationException("No se pueden generar reportes de un periodo que aún no comienza");
        }
        List<HojaReporte> hojas = GeneradorReportes.generar(tipo, rango, admin, List.copyOf(usuarios), centro());
        centro().registrarAuditoria(admin.getEmail(), AccionAuditoria.EXPORTAR_REPORTE, tipo.name(),
                periodo.name() + " — " + rango.etiqueta());
        guardarDatosAutomaticamente();
        return hojas;
    }
}
