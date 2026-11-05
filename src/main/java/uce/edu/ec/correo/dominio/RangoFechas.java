package uce.edu.ec.correo.dominio;

import java.time.LocalDateTime;

public record RangoFechas(LocalDateTime desde, LocalDateTime hasta, String etiqueta) {

    public boolean contiene(LocalDateTime momento) {
        return momento != null && !momento.isBefore(desde) && momento.isBefore(hasta);
    }
}
