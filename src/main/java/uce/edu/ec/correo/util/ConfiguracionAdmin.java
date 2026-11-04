package uce.edu.ec.correo.util;

public final class ConfiguracionAdmin {

    public static final String VARIABLE_EMAIL = "ECUMAIL_ADMIN_EMAIL";
    public static final String VARIABLE_PASSWORD = "ECUMAIL_ADMIN_PASSWORD";

    public static final String DOMINIO_ADMIN = "@ecumail.ec";

    private static final String EMAIL_POR_DEFECTO = "admin" + DOMINIO_ADMIN;
    private static final String PASSWORD_POR_DEFECTO = "CambiarAdmin1";

    private ConfiguracionAdmin() {
    }

    public static String emailInicial() {
        String valor = System.getenv(VARIABLE_EMAIL);
        return valor == null || valor.isBlank() ? EMAIL_POR_DEFECTO : valor.trim().toLowerCase();
    }

    public static String passwordInicial() {
        String valor = System.getenv(VARIABLE_PASSWORD);
        return valor == null || valor.isBlank() ? PASSWORD_POR_DEFECTO : valor;
    }

    public static boolean esEmailDeAdministrador(String email) {
        return email != null && email.trim().toLowerCase().endsWith(DOMINIO_ADMIN);
    }

    public static boolean usaPasswordPorDefecto() {
        String valor = System.getenv(VARIABLE_PASSWORD);
        return valor == null || valor.isBlank();
    }
}