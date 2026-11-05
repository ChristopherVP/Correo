package uce.edu.ec.correo.dominio;

import java.time.LocalDateTime;
import java.util.List;

public record InfoSuspension(String motivo, LocalDateTime fecha, List<Solicitud> apelaciones) {

    public boolean tieneApelacionAbierta() {
        return apelaciones.stream().anyMatch(s -> s.getEstado().estaAbierta());
    }
}
