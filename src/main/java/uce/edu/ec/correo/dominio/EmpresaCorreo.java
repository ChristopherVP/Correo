package uce.edu.ec.correo.dominio;

import uce.edu.ec.correo.util.Validador;
import uce.edu.ec.correo.util.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                if (u.validarPassword(password)) {
                    usuarioActual = u;
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
}