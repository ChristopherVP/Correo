package uce.edu.ec.correo.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class SeguridadUtil {

    private static final int ITERACIONES = 120_000;
    private static final int BITS_CLAVE = 256;
    private static final int BYTES_SALT = 16;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private SeguridadUtil() {
    }

    public static String generarSalt() {
        byte[] salt = new byte[BYTES_SALT];
        ALEATORIO.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashear(String password, String saltBase64) {
        if (password == null || saltBase64 == null) {
            throw new IllegalArgumentException("La contraseña y el salt son obligatorios");
        }
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                Base64.getDecoder().decode(saltBase64),
                ITERACIONES,
                BITS_CLAVE);
        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return Base64.getEncoder().encodeToString(fabrica.generateSecret(spec).getEncoded());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        } finally {
            spec.clearPassword();
        }
    }

    public static boolean verificar(String password, String saltBase64, String hashEsperado) {
        if (password == null || saltBase64 == null || hashEsperado == null) {
            return false;
        }
        byte[] calculado = hashear(password, saltBase64).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] esperado = hashEsperado.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return MessageDigest.isEqual(calculado, esperado);
    }

    public static String enmascararEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int arroba = email.indexOf('@');
        String local = email.substring(0, arroba);
        String visible = local.length() <= 2 ? local.substring(0, 1) : local.substring(0, 2);
        return visible + "***" + email.substring(arroba);
    }

    public static String enmascararCedula(String cedula) {
        if (cedula == null || cedula.length() < 5) {
            return "***";
        }
        return cedula.substring(0, 2) + "******" + cedula.substring(cedula.length() - 2);
    }

    public static String enmascararTelefono(String telefono) {
        if (telefono == null || telefono.length() < 5) {
            return "No registrado";
        }
        return telefono.substring(0, 2) + "******" + telefono.substring(telefono.length() - 2);
    }
}
