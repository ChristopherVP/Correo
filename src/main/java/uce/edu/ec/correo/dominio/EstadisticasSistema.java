package uce.edu.ec.correo.dominio;

import java.util.Map;

public record EstadisticasSistema(
        int totalUsuarios,
        int usuariosSuspendidos,
        int totalAdministradores,
        int correosEnviados,
        int totalGrupos,
        int solicitudesTotales,
        int solicitudesAbiertas,
        Map<String, Long> usuariosPorDominio,
        Map<String, Long> usuariosPorProvincia,
        Map<String, Long> usuariosPorGenero,
        Map<String, Long> solicitudesPorTipo,
        Map<String, Long> solicitudesPorEstado) {
}
