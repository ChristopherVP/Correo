package uce.edu.ec.correo.util;

public final class ReglasPassword {

    public static final int MINIMO = 8;
    public static final int MAXIMO = 14;
    public static final String DESCRIPCION = "Entre 8 y 14 caracteres y al menos una mayúscula";

    private ReglasPassword() {
    }

    public static boolean longitudValida(String password) {
        return password != null && password.length() >= MINIMO && password.length() <= MAXIMO;
    }

    public static boolean tieneMayuscula(String password) {
        return password != null && password.chars().anyMatch(Character::isUpperCase);
    }

    public static boolean cumple(String password) {
        return longitudValida(password) && tieneMayuscula(password);
    }
}
