package uce.edu.ec.correo.dominio;

import uce.edu.ec.correo.util.ConfiguracionAdmin;
import uce.edu.ec.correo.util.ValidationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CentroAdministracion implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int ASUNTO_MIN = 5;
    public static final int ASUNTO_MAX = 100;
    public static final int DESCRIPCION_MIN = 20;
    public static final int DESCRIPCION_MAX = 1000;
    public static final int MAX_SOLICITUDES_ABIERTAS = 5;

    private final ArrayList<Administrador> administradores = new ArrayList<>();
    private final ArrayList<Solicitud> solicitudes = new ArrayList<>();
    private final ArrayList<RegistroAuditoria> auditoria = new ArrayList<>();
    private int ultimoIdSolicitud = 0;

    public boolean asegurarAdministradorPrincipal() {
        boolean existe = administradores.stream().anyMatch(Administrador::esPrincipal);
        if (existe) {
            return false;
        }
        administradores.add(Administrador.crear(
                ConfiguracionAdmin.emailInicial(),
                "Administrador Principal",
                ConfiguracionAdmin.passwordInicial(),
                true,
                ConfiguracionAdmin.usaPasswordPorDefecto(),
                "sistema"));
        return true;
    }

    public Optional<Administrador> buscarAdministrador(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return administradores.stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    public List<Administrador> getAdministradores() {
        return List.copyOf(administradores);
    }

    public void agregarAdministrador(Administrador nuevo) throws ValidationException {
        if (buscarAdministrador(nuevo.getEmail()).isPresent()) {
            throw new ValidationException("Ya existe un administrador con este email", "Email");
        }
        administradores.add(nuevo);
    }

    public void reemplazarAdministrador(Administrador actualizado) {
        for (int i = 0; i < administradores.size(); i++) {
            if (administradores.get(i).getEmail().equalsIgnoreCase(actualizado.getEmail())) {
                administradores.set(i, actualizado);
                return;
            }
        }
    }

    public boolean eliminarAdministrador(String email) {
        return administradores.removeIf(a -> a.getEmail().equalsIgnoreCase(email));
    }

    public Solicitud agregarSolicitud(String emailUsuario, TipoSolicitud tipo, String asunto, String descripcion)
            throws ValidationException {
        if (tipo == null) {
            throw new ValidationException("Seleccione el tipo de solicitud", "Tipo");
        }
        String asuntoLimpio = asunto == null ? "" : asunto.trim();
        String descripcionLimpia = descripcion == null ? "" : descripcion.trim();
        if (asuntoLimpio.length() < ASUNTO_MIN || asuntoLimpio.length() > ASUNTO_MAX) {
            throw new ValidationException(
                    "El asunto debe tener entre " + ASUNTO_MIN + " y " + ASUNTO_MAX + " caracteres", "Asunto");
        }
        if (descripcionLimpia.length() < DESCRIPCION_MIN || descripcionLimpia.length() > DESCRIPCION_MAX) {
            throw new ValidationException(
                    "La descripción debe tener entre " + DESCRIPCION_MIN + " y " + DESCRIPCION_MAX + " caracteres",
                    "Descripción");
        }
        if (contarAbiertasDe(emailUsuario) >= MAX_SOLICITUDES_ABIERTAS) {
            throw new ValidationException(
                    "Tiene " + MAX_SOLICITUDES_ABIERTAS + " solicitudes abiertas. Espere a que sean atendidas.");
        }
        Solicitud nueva = Solicitud.nueva(++ultimoIdSolicitud, emailUsuario, tipo, asuntoLimpio, descripcionLimpia);
        solicitudes.add(nueva);
        return nueva;
    }

    public List<Solicitud> getSolicitudes() {
        return List.copyOf(solicitudes);
    }

    public List<Solicitud> solicitudesDe(String email) {
        return solicitudes.stream().filter(s -> s.perteneceA(email)).toList();
    }

    public int contarAbiertasDe(String email) {
        return (int) solicitudes.stream()
                .filter(s -> s.perteneceA(email) && s.getEstado().estaAbierta())
                .count();
    }

    public Optional<Solicitud> buscarSolicitud(int id) {
        return solicitudes.stream().filter(s -> s.getId() == id).findFirst();
    }

    public void reemplazarSolicitud(Solicitud actualizada) {
        for (int i = 0; i < solicitudes.size(); i++) {
            if (solicitudes.get(i).getId() == actualizada.getId()) {
                solicitudes.set(i, actualizada);
                return;
            }
        }
    }

    public void anonimizarSolicitudesDe(String email) {
        solicitudes.replaceAll(s -> s.perteneceA(email) ? s.anonimizada() : s);
    }

    public void registrarAuditoria(String emailAdmin, String accion, String objetivo, String detalle) {
        auditoria.add(new RegistroAuditoria(emailAdmin, accion, objetivo, detalle));
    }

    public List<RegistroAuditoria> getAuditoria() {
        return List.copyOf(auditoria);
    }
}
