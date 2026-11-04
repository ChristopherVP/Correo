package uce.edu.ec.correo.dominio;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Solicitud implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String USUARIO_ELIMINADO = "usuario-eliminado";

    private final int id;
    private final String emailUsuario;
    private final TipoSolicitud tipo;
    private final String asunto;
    private final String descripcion;
    private final LocalDateTime fechaCreacion;
    private final EstadoSolicitud estado;
    private final String respuesta;
    private final String atendidaPor;
    private final LocalDateTime fechaRespuesta;

    public Solicitud(int id, String emailUsuario, TipoSolicitud tipo, String asunto, String descripcion,
                     LocalDateTime fechaCreacion, EstadoSolicitud estado, String respuesta,
                     String atendidaPor, LocalDateTime fechaRespuesta) {
        this.id = id;
        this.emailUsuario = emailUsuario;
        this.tipo = tipo;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.respuesta = respuesta;
        this.atendidaPor = atendidaPor;
        this.fechaRespuesta = fechaRespuesta;
    }

    public static Solicitud nueva(int id, String emailUsuario, TipoSolicitud tipo, String asunto, String descripcion) {
        return new Solicitud(id, emailUsuario, tipo, asunto, descripcion, LocalDateTime.now(),
                EstadoSolicitud.PENDIENTE, null, null, null);
    }

    public Solicitud conRespuesta(EstadoSolicitud nuevoEstado, String textoRespuesta, String emailAdmin) {
        return new Solicitud(id, emailUsuario, tipo, asunto, descripcion, fechaCreacion,
                nuevoEstado, textoRespuesta, emailAdmin, LocalDateTime.now());
    }

    public Solicitud anonimizada() {
        return new Solicitud(id, USUARIO_ELIMINADO, tipo, asunto, descripcion, fechaCreacion,
                estado, respuesta, atendidaPor, fechaRespuesta);
    }

    public int getId() {
        return id;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public String getAtendidaPor() {
        return atendidaPor;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public boolean perteneceA(String email) {
        return emailUsuario != null && emailUsuario.equalsIgnoreCase(email);
    }
}
