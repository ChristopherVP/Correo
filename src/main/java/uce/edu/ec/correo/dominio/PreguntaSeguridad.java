package uce.edu.ec.correo.dominio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public enum PreguntaSeguridad {
    CEDULA("Número de cédula"),
    FECHA_NACIMIENTO("Fecha de nacimiento (dd/MM/aaaa)"),
    TELEFONO("Número de teléfono registrado");

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final String enunciado;

    PreguntaSeguridad(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public boolean estaDisponiblePara(Usuario usuario) {
        return switch (this) {
            case CEDULA -> usuario.getCedula() != null;
            case FECHA_NACIMIENTO -> usuario.getFechaNacimiento() != null;
            case TELEFONO -> usuario.getTelefono() != null && !usuario.getTelefono().isBlank();
        };
    }

    public boolean verificar(Usuario usuario, String respuesta) {
        if (respuesta == null || respuesta.isBlank() || !estaDisponiblePara(usuario)) {
            return false;
        }
        String limpia = respuesta.trim();
        return switch (this) {
            case CEDULA -> usuario.getCedula().equals(limpia);
            case TELEFONO -> usuario.getTelefono().equals(limpia);
            case FECHA_NACIMIENTO -> coincideFecha(usuario.getFechaNacimiento(), limpia);
        };
    }

    private static boolean coincideFecha(LocalDate fechaUsuario, String texto) {
        try {
            return fechaUsuario.equals(LocalDate.parse(texto, FORMATO_FECHA));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
