package uce.edu.ec.correo.dominio;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RegistroAuditoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDateTime fecha;
    private final String emailAdmin;
    private final String accion;
    private final String objetivo;
    private final String detalle;

    public RegistroAuditoria(String emailAdmin, String accion, String objetivo, String detalle) {
        this.fecha = LocalDateTime.now();
        this.emailAdmin = emailAdmin;
        this.accion = accion;
        this.objetivo = objetivo;
        this.detalle = detalle;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getEmailAdmin() {
        return emailAdmin;
    }

    public String getAccion() {
        return accion;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public String getDetalle() {
        return detalle;
    }
}
