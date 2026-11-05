package uce.edu.ec.correo.dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class EstiloFecha {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private EstiloFecha() {
    }

    static String formato(LocalDateTime fecha) {
        return fecha == null ? "—" : fecha.format(FORMATO);
    }
}
