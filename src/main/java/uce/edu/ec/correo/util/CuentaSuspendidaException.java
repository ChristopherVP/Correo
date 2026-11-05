package uce.edu.ec.correo.util;

public class CuentaSuspendidaException extends ValidationException {

    private final String emailCuenta;

    public CuentaSuspendidaException(String emailCuenta) {
        super("Su cuenta está suspendida. Puede contactar al soporte de EcuMail para apelar.", "Email");
        this.emailCuenta = emailCuenta;
    }

    public String getEmailCuenta() {
        return emailCuenta;
    }
}
