package uce.edu.ec.correo.dominio;

public final class AccionAuditoria {

    public static final String LOGIN_ADMIN = "LOGIN_ADMIN";
    public static final String LOGIN_ADMIN_FALLIDO = "LOGIN_ADMIN_FALLIDO";
    public static final String CAMBIO_PASSWORD_ADMIN = "CAMBIO_PASSWORD_ADMIN";
    public static final String CREAR_ADMIN = "CREAR_ADMIN";
    public static final String ELIMINAR_ADMIN = "ELIMINAR_ADMIN";
    public static final String SUSPENDER_USUARIO = "SUSPENDER_USUARIO";
    public static final String REACTIVAR_USUARIO = "REACTIVAR_USUARIO";
    public static final String ELIMINAR_USUARIO = "ELIMINAR_USUARIO";
    public static final String ATENDER_SOLICITUD = "ATENDER_SOLICITUD";
    public static final String EXPORTAR_REPORTE = "EXPORTAR_REPORTE";

    private AccionAuditoria() {
    }
}
