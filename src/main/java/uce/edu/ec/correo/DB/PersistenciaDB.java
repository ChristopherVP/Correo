package uce.edu.ec.correo.DB;

import uce.edu.ec.correo.dominio.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class PersistenciaDB {

    public static boolean guardarTodo(EmpresaCorreo empresa) {
        try (Connection con = ConexionDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (Statement st = con.createStatement()) {
                    st.execute("TRUNCATE usuarios, correos, grupos RESTART IDENTITY CASCADE");
                }
                // 1) Usuarios primero (las demás tablas dependen de ellos)
                for (Usuario u : empresa.listarUsuarios()) insertarUsuario(con, u);
                // 2) Contenido compartido: una sola vez
                for (Usuario u : empresa.listarUsuarios()) {
                    for (Contacto c : u.getContactos()) insertarContacto(con, u.getEmail(), c);
                    for (Grupo g : u.getGrupos()) insertarGrupo(con, u.getEmail(), g);
                    for (Correo c : u.getCorreos()) insertarCorreo(con, u.getEmail(), c);
                }
                con.commit();
                System.out.println("Datos guardados en PostgreSQL");
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar en BD: " + e.getMessage());
            return false;
        }
    }

    private static void insertarUsuario(Connection con, Usuario u) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO usuarios (email,cedula,nombre,apellido,password,telefono,fecha_nacimiento,genero) " +
                        "VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getCedula());
            ps.setString(3, u.getNombre());
            ps.setString(4, u.getApellido());
            ps.setString(5, u.getPassword());
            ps.setString(6, u.getTelefono());
            ps.setDate(7, u.getFechaNacimiento() != null ? Date.valueOf(u.getFechaNacimiento()) : null);
            ps.setString(8, u.getGenero().name());
            ps.executeUpdate();
        }
    }

    private static void insertarContacto(Connection con, String dueno, Contacto c) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO contactos (usuario_email,email,nombre) VALUES (?,?,?)")) {
            ps.setString(1, dueno);
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getNombre());
            ps.executeUpdate();
        }
    }

    private static void insertarGrupo(Connection con, String creador, Grupo g) throws SQLException {
        int grupoId;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO grupos (nombre,descripcion,creador_email) VALUES (?,?,?) RETURNING id")) {
            ps.setString(1, g.getNombre());
            ps.setString(2, g.getDescripcion());
            ps.setString(3, creador);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                grupoId = rs.getInt(1);
            }
        }

        for (Contacto m : g.getMiembros()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO grupo_miembros (grupo_id,email,nombre,es_admin) VALUES (?,?,?,?) " +
                            "ON CONFLICT DO NOTHING")) {
                ps.setInt(1, grupoId);
                ps.setString(2, m.getEmail().toLowerCase());
                ps.setString(3, m.getNombre());
                ps.setBoolean(4, g.esAdministrador(m.getEmail()));
                ps.executeUpdate();
            }
        }

        for (MensajeGrupo msg : g.getMensajes()) {
            int mensajeId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO mensajes_grupo (grupo_id,remitente,remitente_nombre,contenido,fecha_envio,es_mensaje_sistema) " +
                            "VALUES (?,?,?,?,?,?) RETURNING id")) {
                ps.setInt(1, grupoId);
                ps.setString(2, msg.getRemitente());
                ps.setString(3, msg.getRemitenteNombre());
                ps.setString(4, msg.getContenido());
                ps.setTimestamp(5, ts(msg.getFechaEnvio()));
                ps.setBoolean(6, msg.isEsMensajeSistema());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    mensajeId = rs.getInt(1);
                }
            }
            for (ArchivoAdjunto a : msg.getArchivosAdjuntos()) insertarAdjunto(con, null, mensajeId, a);
        }
    }

    private static void insertarCorreo(Connection con, String duenoBandeja, Correo c) throws SQLException {
        int correoId;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO correos (remitente,asunto,cuerpo,fecha_envio,estado,es_reenviado," +
                        "asunto_original,remitente_original,fecha_original) " +
                        "VALUES (?,?,?,?,?,?,?,?,?) RETURNING id")) {
            ps.setString(1, c.getRemitente());
            ps.setString(2, c.getAsunto());
            ps.setString(3, c.getCuerpo());
            ps.setTimestamp(4, ts(c.getFechaEnvio()));
            ps.setString(5, c.getEstado().name());
            ps.setBoolean(6, c.isEsReenviado());
            ps.setString(7, c.getAsuntoOriginal());
            ps.setString(8, c.getRemitenteOriginal());
            ps.setTimestamp(9, ts(c.getFechaOriginal()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                correoId = rs.getInt(1);
            }
        }

        insertarDestinatarios(con, correoId, c.getDestinatarios(), "PARA");
        insertarDestinatarios(con, correoId, c.getCc(), "CC");
        insertarDestinatarios(con, correoId, c.getCco(), "CCO");

        // Estado privado de quien guarda este correo en su bandeja
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO correo_usuario (correo_id,usuario_email,destacado,eliminado) VALUES (?,?,?,?)")) {
            ps.setInt(1, correoId);
            ps.setString(2, duenoBandeja);
            ps.setBoolean(3, c.isDestacado());
            ps.setBoolean(4, c.getEstado() == EstadoCorreo.ELIMINADO);
            ps.executeUpdate();
        }

        for (ArchivoAdjunto a : c.getArchivosAdjuntos()) insertarAdjunto(con, correoId, null, a);
    }

    private static void insertarDestinatarios(Connection con, int correoId, List<String> emails, String tipo)
            throws SQLException {
        for (String email : emails) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO correo_destinatarios (correo_id,email,tipo) VALUES (?,?,?) ON CONFLICT DO NOTHING")) {
                ps.setInt(1, correoId);
                ps.setString(2, email);
                ps.setString(3, tipo);
                ps.executeUpdate();
            }
        }
    }

    private static void insertarAdjunto(Connection con, Integer correoId, Integer mensajeId, ArchivoAdjunto a)
            throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO archivos_adjuntos (correo_id,mensaje_id,nombre_archivo,tamano,contenido) " +
                        "VALUES (?,?,?,?,?)")) {
            if (correoId != null) ps.setInt(1, correoId); else ps.setNull(1, Types.INTEGER);
            if (mensajeId != null) ps.setInt(2, mensajeId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, a.getNombreArchivo());
            ps.setLong(4, a.getContenido() != null ? a.getContenido().length : 0);
            ps.setBytes(5, a.getContenido());
            ps.executeUpdate();
        }
    }

    private static Timestamp ts(LocalDateTime f) {
        return f != null ? Timestamp.valueOf(f) : null;
    }
}