package uce.edu.ec.correo.dominio;

import uce.edu.ec.correo.util.SeguridadUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Administrador implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String email;
    private final String nombre;
    private final String salt;
    private final String passwordHash;
    private final boolean principal;
    private final boolean cambioPasswordPendiente;
    private final LocalDateTime fechaCreacion;
    private final String creadoPor;

    private Administrador(String email, String nombre, String salt, String passwordHash, boolean principal,
                          boolean cambioPasswordPendiente, LocalDateTime fechaCreacion, String creadoPor) {
        this.email = email;
        this.nombre = nombre;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.principal = principal;
        this.cambioPasswordPendiente = cambioPasswordPendiente;
        this.fechaCreacion = fechaCreacion;
        this.creadoPor = creadoPor;
    }

    public static Administrador crear(String email, String nombre, String password, boolean principal,
                                      boolean cambioPasswordPendiente, String creadoPor) {
        String salt = SeguridadUtil.generarSalt();
        return new Administrador(email, nombre, salt, SeguridadUtil.hashear(password, salt),
                principal, cambioPasswordPendiente, LocalDateTime.now(), creadoPor);
    }

    public Administrador conNuevaPassword(String nuevaPassword, boolean cambioPendiente) {
        String nuevoSalt = SeguridadUtil.generarSalt();
        return new Administrador(email, nombre, nuevoSalt, SeguridadUtil.hashear(nuevaPassword, nuevoSalt),
                principal, cambioPendiente, fechaCreacion, creadoPor);
    }

    public boolean validarPassword(String password) {
        return SeguridadUtil.verificar(password, salt, passwordHash);
    }

    public String getEmail() {
        return email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSalt() {
        return salt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean esPrincipal() {
        return principal;
    }

    public boolean isCambioPasswordPendiente() {
        return cambioPasswordPendiente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }
}
