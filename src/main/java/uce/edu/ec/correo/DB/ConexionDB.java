package uce.edu.ec.correo.DB;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {

    private static final String ARCHIVO_CONFIGURACION = "db.properties";

    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {
        Properties configuracion = cargarConfiguracion();
        return DriverManager.getConnection(
                obtenerRequerida(configuracion, "db.url"),
                obtenerRequerida(configuracion, "db.user"),
                configuracion.getProperty("db.password", ""));
    }

    private static Properties cargarConfiguracion() throws SQLException {
        Properties configuracion = new Properties();
        Path archivo = Path.of(ARCHIVO_CONFIGURACION);
        try {
            if (Files.isRegularFile(archivo)) {
                try (InputStream entrada = Files.newInputStream(archivo)) {
                    configuracion.load(entrada);
                }
                return configuracion;
            }
            try (InputStream entrada = ConexionDB.class.getResourceAsStream("/" + ARCHIVO_CONFIGURACION)) {
                if (entrada == null) {
                    throw new SQLException("No se encontró " + ARCHIVO_CONFIGURACION + " en "
                            + archivo.toAbsolutePath().getParent()
                            + ". Copie db.properties.example como db.properties y complete sus datos.");
                }
                configuracion.load(entrada);
                return configuracion;
            }
        } catch (IOException e) {
            throw new SQLException("No se pudo leer " + ARCHIVO_CONFIGURACION + ": " + e.getMessage(), e);
        }
    }

    private static String obtenerRequerida(Properties configuracion, String clave) throws SQLException {
        String valor = configuracion.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new SQLException("Falta la propiedad '" + clave + "' en " + ARCHIVO_CONFIGURACION);
        }
        return valor.trim();
    }
}
/*
-- -----------------------------DB-------------------------------------
DROP TABLE IF EXISTS adjunto, administrador, contacto, correo_destinatario, correo, grupo_miembro, grupo, mensaje_grupo, registro_auditoria, solicitud, suspension, usuario CASCADE;
DROP TABLE IF EXISTS archivos_adjuntos, correo_usuario, correo_destinatarios, correos, mensajes_grupo, grupo_miembros, grupos, contactos, auditoria_admin, solicitudes, administradores, usuarios CASCADE;

CREATE TABLE IF NOT EXISTS usuarios (
    email             VARCHAR(255) PRIMARY KEY,
    cedula            VARCHAR(10)  NOT NULL UNIQUE,
    nombre            VARCHAR(100) NOT NULL,
    apellido          VARCHAR(100) NOT NULL,
    password          TEXT         NOT NULL,
    telefono          VARCHAR(20),
    fecha_nacimiento  DATE,
    genero            VARCHAR(20)  NOT NULL CHECK (genero IN ('MASCULINO', 'FEMENINO', 'OTRO')),
    fecha_registro    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    suspendida        BOOLEAN      NOT NULL DEFAULT FALSE,
    motivo_suspension TEXT,
    fecha_suspension  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS administradores (
    email                      VARCHAR(255) PRIMARY KEY,
    nombre                     VARCHAR(150) NOT NULL,
    password_hash              TEXT         NOT NULL,
    salt                       TEXT         NOT NULL,
    es_principal               BOOLEAN      NOT NULL DEFAULT FALSE,
    cambio_password_pendiente  BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_creacion             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creado_por                 VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS solicitudes (
    id              INTEGER      PRIMARY KEY,
    usuario_email   VARCHAR(255) NOT NULL,
    tipo            VARCHAR(30)  NOT NULL CHECK (tipo IN ('QUEJA', 'REPORTE_TECNICO', 'RECOMENDACION', 'APELACION')),
    asunto          TEXT         NOT NULL,
    descripcion     TEXT,
    estado          VARCHAR(20)  NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_REVISION', 'RESUELTA', 'RECHAZADA')),
    fecha_creacion  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    respuesta       TEXT,
    atendida_por    VARCHAR(255),
    fecha_respuesta TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auditoria_admin (
    id          BIGSERIAL    PRIMARY KEY,
    fecha       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    admin_email VARCHAR(255),
    accion      TEXT         NOT NULL,
    objetivo    TEXT,
    detalle     TEXT
);

CREATE TABLE IF NOT EXISTS contactos (
    id             SERIAL       PRIMARY KEY,
    usuario_email  VARCHAR(255) NOT NULL REFERENCES usuarios (email) ON DELETE CASCADE ON UPDATE CASCADE,
    email          VARCHAR(255) NOT NULL,
    nombre         VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS grupos (
    id            SERIAL       PRIMARY KEY,
    nombre        VARCHAR(200) NOT NULL,
    descripcion   TEXT,
    creador_email VARCHAR(255) NOT NULL REFERENCES usuarios (email) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS grupo_miembros (
    grupo_id  INTEGER      NOT NULL REFERENCES grupos (id) ON DELETE CASCADE,
    email     VARCHAR(255) NOT NULL,
    nombre    VARCHAR(200),
    es_admin  BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (grupo_id, email)
);

CREATE TABLE IF NOT EXISTS mensajes_grupo (
    id                 SERIAL       PRIMARY KEY,
    grupo_id           INTEGER      NOT NULL REFERENCES grupos (id) ON DELETE CASCADE,
    remitente          VARCHAR(255),
    remitente_nombre   VARCHAR(200),
    contenido          TEXT,
    fecha_envio        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    es_mensaje_sistema BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS correos (
    id                 SERIAL       PRIMARY KEY,
    remitente          VARCHAR(255) NOT NULL,
    asunto             TEXT,
    cuerpo             TEXT,
    fecha_envio        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado             VARCHAR(20)  NOT NULL CHECK (estado IN ('TODOS', 'BORRADOR', 'ENVIADO', 'RECIBIDO', 'REENVIADO', 'ELIMINADO')),
    es_reenviado       BOOLEAN      NOT NULL DEFAULT FALSE,
    asunto_original    TEXT,
    remitente_original VARCHAR(255),
    fecha_original     TIMESTAMP
);

CREATE TABLE IF NOT EXISTS correo_destinatarios (
    correo_id INTEGER      NOT NULL REFERENCES correos (id) ON DELETE CASCADE,
    email     VARCHAR(255) NOT NULL,
    tipo      VARCHAR(10)  NOT NULL CHECK (tipo IN ('PARA', 'CC', 'CCO')),
    PRIMARY KEY (correo_id, email, tipo)
);

CREATE TABLE IF NOT EXISTS correo_usuario (
    correo_id     INTEGER      NOT NULL REFERENCES correos (id) ON DELETE CASCADE,
    usuario_email VARCHAR(255) NOT NULL REFERENCES usuarios (email) ON DELETE CASCADE ON UPDATE CASCADE,
    destacado     BOOLEAN      NOT NULL DEFAULT FALSE,
    eliminado     BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (correo_id, usuario_email)
);

CREATE TABLE IF NOT EXISTS archivos_adjuntos (
    id             SERIAL       PRIMARY KEY,
    correo_id      INTEGER      REFERENCES correos (id) ON DELETE CASCADE,
    mensaje_id     INTEGER      REFERENCES mensajes_grupo (id) ON DELETE CASCADE,
    nombre_archivo VARCHAR(500) NOT NULL,
    tamano         BIGINT       NOT NULL DEFAULT 0,
    contenido      BYTEA,
    CHECK (correo_id IS NOT NULL OR mensaje_id IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_contactos_usuario     ON contactos (usuario_email);
CREATE INDEX IF NOT EXISTS idx_grupos_creador        ON grupos (creador_email);
CREATE INDEX IF NOT EXISTS idx_miembros_email        ON grupo_miembros (email);
CREATE INDEX IF NOT EXISTS idx_mensajes_grupo        ON mensajes_grupo (grupo_id, fecha_envio);
CREATE INDEX IF NOT EXISTS idx_correos_remitente     ON correos (remitente);
CREATE INDEX IF NOT EXISTS idx_dest_email            ON correo_destinatarios (email);
CREATE INDEX IF NOT EXISTS idx_correo_usuario_email  ON correo_usuario (usuario_email);
CREATE INDEX IF NOT EXISTS idx_adjuntos_correo       ON archivos_adjuntos (correo_id);
CREATE INDEX IF NOT EXISTS idx_adjuntos_mensaje      ON archivos_adjuntos (mensaje_id);
CREATE INDEX IF NOT EXISTS idx_solicitudes_usuario   ON solicitudes (usuario_email);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha       ON auditoria_admin (fecha);
 */