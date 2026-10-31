package uce.edu.ec.correo.dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Clase que representa un correo electrónico en el sistema.
 * Maneja todos los aspectos de un correo: destinatarios, adjuntos, estados, reenvíos, etc.
 * Implementa Serializable para permitir guardar correos en archivos.
 */
public class Correo implements Serializable {
    // Versión de serialización para compatibilidad al guardar/cargar
    private static final long serialVersionUID = 1L;

    // Asunto o título del correo
    private String asunto;

    // Contenido o cuerpo del mensaje
    private String cuerpo;

    // Email del usuario que envía el correo
    private String remitente;

    // Lista de destinatarios principales (campo "Para:")
    private ArrayList<String> destinatarios;

    // Lista de destinatarios en copia (campo "CC:")
    private ArrayList<String> cc;

    // Lista de destinatarios en copia oculta (campo "CCO:")
    private ArrayList<String> cco;

    // Fecha y hora en que se envió el correo
    private LocalDateTime fechaEnvio;

    // Estado actual del correo (BORRADOR, ENVIADO, etc.)
    private EstadoCorreo estado;

    // Lista de archivos adjuntos al correo
    private ArrayList<ArchivoAdjunto> archivosAdjuntos;

    // Indica si el correo está marcado como destacado/importante
    private boolean destacado = false;

    // ===== Campos específicos para correos reenviados =====

    // Indica si este correo es un reenvío de otro correo
    private boolean esReenviado;

    // Asunto del correo original (antes del prefijo "RV:")
    private String asuntoOriginal;

    // Email del remitente original del correo
    private String remitenteOriginal;

    // Fecha de envío del correo original
    private LocalDateTime fechaOriginal;

    /**
     * Constructor que crea un nuevo correo electrónico.
     *
     * @param asunto Asunto o título del correo
     * @param cuerpo Contenido del mensaje
     * @param remitente Email del usuario que crea el correo
     */
    public Correo(String asunto, String cuerpo, String remitente) {
        // Asigna el asunto del correo
        this.asunto = asunto;

        // Asigna el cuerpo del mensaje
        this.cuerpo = cuerpo;

        // Asigna el email del remitente
        this.remitente = remitente;

        // Inicializa la lista vacía de destinatarios principales
        this.destinatarios = new ArrayList<>();

        // Inicializa la lista vacía de destinatarios en copia
        this.cc = new ArrayList<>();

        // Inicializa la lista vacía de destinatarios en copia oculta
        this.cco = new ArrayList<>();

        // Establece el estado inicial como BORRADOR
        this.estado = EstadoCorreo.BORRADOR;

        // Inicializa la lista vacía de archivos adjuntos
        this.archivosAdjuntos = new ArrayList<>();

        // Por defecto, el correo no es un reenvío
        this.esReenviado = false;
    }

    // ==================== GETTERS Y SETTERS BÁSICOS ====================

    /**
     * Obtiene el asunto del correo.
     *
     * @return Asunto del correo
     */
    public String getAsunto() {
        return asunto;
    }

    /**
     * Establece o modifica el asunto del correo.
     *
     * @param asunto Nuevo asunto
     */
    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    /**
     * Obtiene el cuerpo del correo.
     *
     * @return Contenido del mensaje
     */
    public String getCuerpo() {
        return cuerpo;
    }

    /**
     * Establece o modifica el cuerpo del correo.
     *
     * @param cuerpo Nuevo contenido
     */
    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    /**
     * Obtiene el email del remitente.
     *
     * @return Email del remitente
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * Establece o modifica el remitente del correo.
     *
     * @param remitente Nuevo email del remitente
     */
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    /**
     * Obtiene la lista de destinatarios principales.
     *
     * @return ArrayList con emails de destinatarios
     */
    public ArrayList<String> getDestinatarios() {
        return destinatarios;
    }

    /**
     * Establece la lista completa de destinatarios principales.
     *
     * @param destinatarios Nueva lista de destinatarios
     */
    public void setDestinatarios(ArrayList<String> destinatarios) {
        this.destinatarios = destinatarios;
    }

    /**
     * Obtiene la lista de destinatarios en copia (CC).
     *
     * @return ArrayList con emails en copia
     */
    public ArrayList<String> getCc() {
        return cc;
    }

    /**
     * Establece la lista completa de destinatarios en copia.
     *
     * @param cc Nueva lista de destinatarios CC
     */
    public void setCc(ArrayList<String> cc) {
        this.cc = cc;
    }

    /**
     * Obtiene la lista de destinatarios en copia oculta (CCO).
     *
     * @return ArrayList con emails en copia oculta
     */
    public ArrayList<String> getCco() {
        return cco;
    }

    /**
     * Establece la lista completa de destinatarios en copia oculta.
     *
     * @param cco Nueva lista de destinatarios CCO
     */
    public void setCco(ArrayList<String> cco) {
        this.cco = cco;
    }

    /**
     * Obtiene la fecha y hora de envío del correo.
     *
     * @return Objeto LocalDateTime con la fecha de envío
     */
    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    /**
     * Establece la fecha y hora de envío.
     *
     * @param fechaEnvio Nueva fecha de envío
     */
    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    /**
     * Obtiene el estado actual del correo.
     *
     * @return Estado del correo (BORRADOR, ENVIADO, etc.)
     */
    public EstadoCorreo getEstado() {
        return estado;
    }

    /**
     * Establece el estado del correo.
     *
     * @param estado Nuevo estado
     */
    public void setEstado(EstadoCorreo estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la lista de archivos adjuntos.
     *
     * @return ArrayList con los archivos adjuntos
     */
    public ArrayList<ArchivoAdjunto> getArchivosAdjuntos() {
        return archivosAdjuntos;
    }

    // ==================== GETTERS Y SETTERS PARA REENVÍO ====================

    /**
     * Verifica si el correo es un reenvío.
     *
     * @return true si es reenvío, false si es correo original
     */
    public boolean isEsReenviado() {
        return esReenviado;
    }

    /**
     * Marca o desmarca el correo como reenvío.
     *
     * @param esReenviado true para marcar como reenvío
     */
    public void setEsReenviado(boolean esReenviado) {
        this.esReenviado = esReenviado;
    }

    /**
     * Obtiene el asunto original del correo (sin prefijo "RV:").
     *
     * @return Asunto original
     */
    public String getAsuntoOriginal() {
        return asuntoOriginal;
    }

    /**
     * Establece el asunto original del correo reenviado.
     *
     * @param asuntoOriginal Asunto del correo original
     */
    public void setAsuntoOriginal(String asuntoOriginal) {
        this.asuntoOriginal = asuntoOriginal;
    }

    /**
     * Obtiene el email del remitente original.
     *
     * @return Email del remitente original
     */
    public String getRemitenteOriginal() {
        return remitenteOriginal;
    }

    /**
     * Establece el remitente original del correo.
     *
     * @param remitenteOriginal Email del remitente original
     */
    public void setRemitenteOriginal(String remitenteOriginal) {
        this.remitenteOriginal = remitenteOriginal;
    }

    /**
     * Obtiene la fecha de envío original.
     *
     * @return Fecha del correo original
     */
    public LocalDateTime getFechaOriginal() {
        return fechaOriginal;
    }

    /**
     * Establece la fecha de envío original.
     *
     * @param fechaOriginal Fecha del correo original
     */
    public void setFechaOriginal(LocalDateTime fechaOriginal) {
        this.fechaOriginal = fechaOriginal;
    }

    // ==================== MÉTODOS DE DESTINATARIOS ====================

    /**
     * Agrega un destinatario principal al correo.
     * No permite duplicados.
     *
     * @param email Email del destinatario a agregar
     */
    public void agregarDestinatario(String email) {
        // Verifica que el email no esté ya en la lista antes de agregarlo
        if (!destinatarios.contains(email)) {
            // Agrega el email a la lista de destinatarios
            destinatarios.add(email);
        }
    }

    /**
     * Agrega un destinatario en copia (CC) al correo.
     * No permite duplicados.
     *
     * @param email Email del destinatario CC a agregar
     */
    public void agregarCC(String email) {
        // Verifica que el email no esté ya en la lista CC
        if (!cc.contains(email)) {
            // Agrega el email a la lista CC
            cc.add(email);
        }
    }

    /**
     * Agrega un destinatario en copia oculta (CCO) al correo.
     * No permite duplicados.
     *
     * @param email Email del destinatario CCO a agregar
     */
    public void agregarCCO(String email) {
        // Verifica que el email no esté ya en la lista CCO
        if (!cco.contains(email)) {
            // Agrega el email a la lista CCO
            cco.add(email);
        }
    }

    // ==================== MÉTODOS DE ADJUNTOS ====================

    /**
     * Agrega un archivo adjunto al correo.
     *
     * @param archivo Objeto ArchivoAdjunto a agregar
     */
    public void agregarArchivoAdjunto(ArchivoAdjunto archivo) {
        // Agrega el archivo a la lista de adjuntos
        archivosAdjuntos.add(archivo);
    }

    /**
     * Verifica si el correo tiene archivos adjuntos.
     *
     * @return true si tiene adjuntos, false si no tiene
     */
    public boolean tieneAdjuntos() {
        // Retorna true si la lista NO está vacía
        return !archivosAdjuntos.isEmpty();
    }

    /**
     * Obtiene la cantidad de archivos adjuntos.
     *
     * @return Número de archivos adjuntos
     */
    public int getCantidadAdjuntos() {
        // Retorna el tamaño de la lista de adjuntos
        return archivosAdjuntos.size();
    }

    // ==================== MÉTODOS DE ENVÍO ====================

    /**
     * Marca el correo como enviado.
     * Establece la fecha de envío al momento actual y cambia el estado a ENVIADO.
     */
    public void enviar() {
        // Establece la fecha y hora actual como momento de envío
        this.fechaEnvio = LocalDateTime.now();

        // Cambia el estado del correo a ENVIADO
        this.estado = EstadoCorreo.ENVIADO;
    }

    /**
     * Calcula el total de destinatarios (Para + CC + CCO).
     *
     * @return Número total de destinatarios
     */
    public int getTotalDestinatarios() {
        // Suma el tamaño de las tres listas de destinatarios
        return destinatarios.size() + cc.size() + cco.size();
    }

    /**
     * Verifica si el correo tiene al menos un destinatario.
     *
     * @return true si tiene destinatarios, false si no tiene ninguno
     */
    public boolean tieneDestinatarios() {
        // Retorna true si el total de destinatarios es mayor a 0
        return getTotalDestinatarios() > 0;
    }

    /**
     * Verifica si el correo fue enviado por un usuario específico.
     *
     * @param email Email a verificar
     * @return true si el email coincide con el remitente
     */
    public boolean esEnviado(String email) {
        // Compara el email dado con el remitente del correo
        return remitente.equals(email);
    }

    /**
     * Formatea la fecha de envío en un formato legible.
     *
     * @return String con la fecha formateada o "Sin enviar" si no se ha enviado
     */
    public String getFechaEnvioFormateada() {
        // Si no hay fecha de envío, retorna "Sin enviar"
        if (fechaEnvio == null) {
            return "Sin enviar";
        }

        // Crea un formateador con el patrón día/mes/año hora:minuto
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Formatea la fecha según el patrón y la retorna
        return fechaEnvio.format(formatter);
    }

    /**
     * Crea una copia del correo para ser reenviado.
     * El nuevo correo conserva el contenido y adjuntos del original,
     * pero con un nuevo remitente y asunto con prefijo "RV:".
     *
     * @param nuevoRemitente Email del usuario que reenvía el correo
     * @return Nuevo objeto Correo configurado para reenvío
     */
    public Correo clonarParaReenvio(String nuevoRemitente) {
        // Crea un nuevo correo con asunto modificado (agrega "RV:")
        Correo nuevoCorreo = new Correo(
                "RV: " + this.asunto,  // Prefijo "RV:" al asunto
                this.cuerpo,           // Mismo cuerpo del original
                nuevoRemitente         // Nuevo remitente
        );

        // ===== Marca el nuevo correo como reenvío =====

        // Marca que este correo es un reenvío
        nuevoCorreo.setEsReenviado(true);

        // Guarda el asunto original (sin "RV:")
        nuevoCorreo.setAsuntoOriginal(this.asunto);

        // Guarda el remitente original
        nuevoCorreo.setRemitenteOriginal(this.remitente);

        // Guarda la fecha de envío original
        nuevoCorreo.setFechaOriginal(this.fechaEnvio);

        // ===== Copia los archivos adjuntos =====

        // Recorre todos los adjuntos del correo original
        for (ArchivoAdjunto adjunto : this.archivosAdjuntos) {
            // Agrega cada adjunto al nuevo correo
            nuevoCorreo.agregarArchivoAdjunto(adjunto);
        }

        // Retorna el nuevo correo listo para ser reenviado
        return nuevoCorreo;
    }

    /**
     * Verifica si el correo está marcado como destacado.
     *
     * @return true si está destacado, false si no
     */
    public boolean isDestacado() {
        return destacado;
    }

    /**
     * Marca o desmarca el correo como destacado/importante.
     *
     * @param destacado true para marcar como destacado
     */
    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }
}