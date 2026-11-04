package uce.edu.ec.correo.dominio;

import java.time.LocalDateTime;

public record ResumenUsuario(
        String email,
        String nombreCompleto,
        String cedulaEnmascarada,
        String provincia,
        String telefonoEnmascarado,
        LocalDateTime fechaRegistro,
        int correosEnviados,
        int totalContactos,
        int totalGrupos,
        boolean suspendida) {
}
