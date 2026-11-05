package uce.edu.ec.correo.dominio;

import uce.edu.ec.correo.util.SeguridadUtil;
import uce.edu.ec.correo.util.Validador;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GeneradorReportes {

    private static final List<String> ACCIONES_CUENTA = List.of(
            AccionAuditoria.SUSPENDER_USUARIO, AccionAuditoria.REACTIVAR_USUARIO, AccionAuditoria.ELIMINAR_USUARIO);

    private GeneradorReportes() {
    }

    public static List<HojaReporte> generar(TipoReporte tipo, RangoFechas rango, Administrador actor,
                                            List<Usuario> usuarios, CentroAdministracion centro) {
        return switch (tipo) {
            case MI_TRABAJO -> miTrabajo(rango, actor, centro);
            case GENERAL -> general(rango, usuarios, centro);
            case DESEMPENO_COLABORADORES -> desempeno(rango, centro);
        };
    }

    private static List<HojaReporte> miTrabajo(RangoFechas rango, Administrador actor, CentroAdministracion centro) {
        List<RegistroAuditoria> propios = auditoriaEn(rango, centro).stream()
                .filter(r -> r.getEmailAdmin().equalsIgnoreCase(actor.getEmail()))
                .toList();
        String titulo = "Mi trabajo — " + actor.getNombre() + " — " + rango.etiqueta();

        List<List<String>> resumen = new ArrayList<>();
        resumen.add(fila("Periodo", rango.etiqueta()));
        resumen.add(fila("Administrador", actor.getNombre() + " (" + actor.getEmail() + ")"));
        resumen.add(fila("Solicitudes atendidas", contarAcciones(propios, AccionAuditoria.ATENDER_SOLICITUD)));
        resumen.add(fila("   En revisión", contarEstado(propios, EstadoSolicitud.EN_REVISION)));
        resumen.add(fila("   Resueltas", contarEstado(propios, EstadoSolicitud.RESUELTA)));
        resumen.add(fila("   Rechazadas", contarEstado(propios, EstadoSolicitud.RECHAZADA)));
        resumen.add(fila("Cuentas suspendidas", contarAcciones(propios, AccionAuditoria.SUSPENDER_USUARIO)));
        resumen.add(fila("Cuentas reactivadas", contarAcciones(propios, AccionAuditoria.REACTIVAR_USUARIO)));
        resumen.add(fila("Cuentas eliminadas", contarAcciones(propios, AccionAuditoria.ELIMINAR_USUARIO)));

        List<List<String>> atendidas = propios.stream()
                .filter(r -> r.getAccion().equals(AccionAuditoria.ATENDER_SOLICITUD))
                .map(r -> filaSolicitudAtendida(r, centro))
                .toList();
        List<List<String>> cuentas = propios.stream()
                .filter(r -> ACCIONES_CUENTA.contains(r.getAccion()))
                .map(r -> filaCuenta(r))
                .toList();

        return List.of(
                new HojaReporte("Resumen", titulo, List.of("Indicador", "Valor"), resumen),
                new HojaReporte("Solicitudes atendidas", titulo,
                        List.of("Fecha", "N.º solicitud", "Tipo", "Asunto", "Estado asignado"), atendidas),
                new HojaReporte("Acciones sobre cuentas", titulo,
                        List.of("Fecha", "Acción", "Cuenta afectada", "Motivo"), cuentas));
    }

    private static List<HojaReporte> desempeno(RangoFechas rango, CentroAdministracion centro) {
        List<RegistroAuditoria> registros = auditoriaEn(rango, centro);
        String titulo = "Desempeño de colaboradores — " + rango.etiqueta();

        List<List<String>> filasDesempeno = new ArrayList<>();
        for (Administrador admin : centro.getAdministradores()) {
            List<RegistroAuditoria> propios = registros.stream()
                    .filter(r -> r.getEmailAdmin().equalsIgnoreCase(admin.getEmail()))
                    .toList();
            filasDesempeno.add(fila(admin.getNombre(), admin.getEmail(),
                    admin.esPrincipal() ? "Principal" : "Colaborador",
                    contarAcciones(propios, AccionAuditoria.ATENDER_SOLICITUD),
                    contarEstado(propios, EstadoSolicitud.EN_REVISION),
                    contarEstado(propios, EstadoSolicitud.RESUELTA),
                    contarEstado(propios, EstadoSolicitud.RECHAZADA),
                    contarAcciones(propios, AccionAuditoria.SUSPENDER_USUARIO),
                    contarAcciones(propios, AccionAuditoria.REACTIVAR_USUARIO),
                    contarAcciones(propios, AccionAuditoria.ELIMINAR_USUARIO)));
        }

        List<List<String>> cuentas = registros.stream()
                .filter(r -> ACCIONES_CUENTA.contains(r.getAccion()))
                .map(r -> {
                    List<String> base = new ArrayList<>(filaCuenta(r));
                    base.add(1, r.getEmailAdmin());
                    return base;
                })
                .toList();
        List<List<String>> atendidas = registros.stream()
                .filter(r -> r.getAccion().equals(AccionAuditoria.ATENDER_SOLICITUD))
                .map(r -> {
                    List<String> base = new ArrayList<>(filaSolicitudAtendida(r, centro));
                    base.add(1, r.getEmailAdmin());
                    return base;
                })
                .toList();

        return List.of(
                new HojaReporte("Desempeño", titulo,
                        List.of("Nombre", "Email", "Rol", "Solicitudes atendidas", "En revisión", "Resueltas",
                                "Rechazadas", "Cuentas suspendidas", "Cuentas reactivadas", "Cuentas eliminadas"),
                        filasDesempeno),
                new HojaReporte("Cuentas y motivos", titulo,
                        List.of("Fecha", "Colaborador", "Acción", "Cuenta afectada", "Motivo"), cuentas),
                new HojaReporte("Solicitudes atendidas", titulo,
                        List.of("Fecha", "Colaborador", "N.º solicitud", "Tipo", "Asunto", "Estado asignado"),
                        atendidas));
    }

    private static List<HojaReporte> general(RangoFechas rango, List<Usuario> usuarios, CentroAdministracion centro) {
        List<RegistroAuditoria> registros = auditoriaEn(rango, centro);
        List<Solicitud> solicitudesPeriodo = centro.getSolicitudes().stream()
                .filter(s -> rango.contiene(s.getFechaCreacion()))
                .toList();
        String titulo = "Resumen general del sistema — " + rango.etiqueta();

        long nuevos = usuarios.stream().filter(u -> rango.contiene(u.getFechaRegistro())).count();
        long correosEnviados = usuarios.stream()
                .flatMap(u -> u.getCorreos().stream().filter(c -> c.getRemitente().equalsIgnoreCase(u.getEmail())))
                .filter(c -> (c.getEstado() == EstadoCorreo.ENVIADO || c.getEstado() == EstadoCorreo.REENVIADO)
                        && rango.contiene(c.getFechaEnvio()))
                .count();

        List<List<String>> resumen = new ArrayList<>();
        resumen.add(fila("Periodo", rango.etiqueta()));
        resumen.add(fila("Usuarios registrados (total actual)", usuarios.size()));
        resumen.add(fila("Nuevos registros en el periodo", nuevos));
        resumen.add(fila("Cuentas suspendidas (actual)", usuarios.stream().filter(Usuario::isSuspendida).count()));
        resumen.add(fila("Suspensiones en el periodo", contarAcciones(registros, AccionAuditoria.SUSPENDER_USUARIO)));
        resumen.add(fila("Reactivaciones en el periodo", contarAcciones(registros, AccionAuditoria.REACTIVAR_USUARIO)));
        resumen.add(fila("Eliminaciones en el periodo", contarAcciones(registros, AccionAuditoria.ELIMINAR_USUARIO)));
        resumen.add(fila("Correos enviados en el periodo", correosEnviados));
        resumen.add(fila("Solicitudes recibidas en el periodo", solicitudesPeriodo.size()));
        resumen.add(fila("Solicitudes atendidas en el periodo", contarAcciones(registros, AccionAuditoria.ATENDER_SOLICITUD)));
        resumen.add(fila("Solicitudes abiertas (actual)",
                centro.getSolicitudes().stream().filter(s -> s.getEstado().estaAbierta()).count()));
        resumen.add(fila("Administradores", centro.getAdministradores().size()));

        List<List<String>> solicitudes = solicitudesPeriodo.stream()
                .map(s -> fila(s.getId(), EstiloFecha.formato(s.getFechaCreacion()), s.getTipo().getDescripcion(),
                        s.getAsunto(), SeguridadUtil.enmascararEmail(s.getEmailUsuario()),
                        s.getEstado().getDescripcion(), s.getAtendidaPor() == null ? "—" : s.getAtendidaPor(),
                        EstiloFecha.formato(s.getFechaRespuesta())))
                .toList();

        List<List<String>> distribucion = new ArrayList<>();
        agregarDistribucion(distribucion, "Dominio de correo", usuarios,
                u -> u.getEmail().substring(u.getEmail().indexOf('@') + 1));
        agregarDistribucion(distribucion, "Provincia", usuarios, u -> Validador.obtenerProvinciaPorCedula(u.getCedula()));
        agregarDistribucion(distribucion, "Género", usuarios, u -> u.getGenero().getDescripcion());

        List<List<String>> auditoria = registros.stream()
                .map(r -> fila(EstiloFecha.formato(r.getFecha()), r.getEmailAdmin(), r.getAccion(),
                        r.getObjetivo(), r.getDetalle()))
                .toList();

        return List.of(
                new HojaReporte("Resumen general", titulo, List.of("Indicador", "Valor"), resumen),
                new HojaReporte("Solicitudes", titulo,
                        List.of("N.º", "Fecha", "Tipo", "Asunto", "Usuario", "Estado", "Atendida por",
                                "Fecha de respuesta"), solicitudes),
                new HojaReporte("Distribución de usuarios", titulo,
                        List.of("Criterio", "Categoría", "Total"), distribucion),
                new HojaReporte("Auditoría", titulo,
                        List.of("Fecha", "Administrador", "Acción", "Objetivo", "Detalle"), auditoria));
    }

    private static List<RegistroAuditoria> auditoriaEn(RangoFechas rango, CentroAdministracion centro) {
        return centro.getAuditoria().stream().filter(r -> rango.contiene(r.getFecha())).toList();
    }

    private static long contarAcciones(List<RegistroAuditoria> registros, String accion) {
        return registros.stream().filter(r -> r.getAccion().equals(accion)).count();
    }

    private static long contarEstado(List<RegistroAuditoria> registros, EstadoSolicitud estado) {
        return registros.stream()
                .filter(r -> r.getAccion().equals(AccionAuditoria.ATENDER_SOLICITUD)
                        && EstadoSolicitud.desdeTexto(r.getDetalle()) == estado)
                .count();
    }

    private static List<String> filaSolicitudAtendida(RegistroAuditoria registro, CentroAdministracion centro) {
        Solicitud solicitud = buscarSolicitud(registro.getObjetivo(), centro);
        EstadoSolicitud estado = EstadoSolicitud.desdeTexto(registro.getDetalle());
        return fila(EstiloFecha.formato(registro.getFecha()), registro.getObjetivo(),
                solicitud == null ? "—" : solicitud.getTipo().getDescripcion(),
                solicitud == null ? "—" : solicitud.getAsunto(),
                estado == null ? registro.getDetalle() : estado.getDescripcion());
    }

    private static Solicitud buscarSolicitud(String objetivo, CentroAdministracion centro) {
        if (objetivo == null || !objetivo.startsWith("#")) {
            return null;
        }
        try {
            return centro.buscarSolicitud(Integer.parseInt(objetivo.substring(1))).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static List<String> filaCuenta(RegistroAuditoria registro) {
        return fila(EstiloFecha.formato(registro.getFecha()), nombreAccion(registro.getAccion()),
                registro.getObjetivo(), registro.getDetalle());
    }

    private static String nombreAccion(String accion) {
        return switch (accion) {
            case AccionAuditoria.SUSPENDER_USUARIO -> "Suspensión";
            case AccionAuditoria.REACTIVAR_USUARIO -> "Reactivación";
            case AccionAuditoria.ELIMINAR_USUARIO -> "Eliminación";
            default -> accion;
        };
    }

    private static <T> void agregarDistribucion(List<List<String>> destino, String criterio,
                                                Collection<T> elementos, Function<T, String> clasificador) {
        Map<String, Long> conteo = elementos.stream().collect(Collectors.groupingBy(
                e -> Objects.requireNonNullElse(clasificador.apply(e), "Desconocido"),
                TreeMap::new, Collectors.counting()));
        conteo.forEach((categoria, total) -> destino.add(fila(criterio, categoria, total)));
    }

    private static List<String> fila(Object... valores) {
        List<String> fila = new ArrayList<>();
        for (Object valor : valores) {
            fila.add(valor == null ? "" : String.valueOf(valor));
        }
        return fila;
    }
}
