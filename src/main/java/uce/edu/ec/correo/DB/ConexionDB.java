package uce.edu.ec.correo.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = valorEntorno("ECUMAIL_DB_URL", "jdbc:postgresql://localhost:5432/correo");
    private static final String USER = valorEntorno("ECUMAIL_DB_USER", "postgres");
    private static final String PASSWORD = valorEntorno("ECUMAIL_DB_PASSWORD", "DB_Password");

    private static String valorEntorno(String nombre, String valorPorDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? valorPorDefecto : valor;
    }

    private ConexionDB() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

/*

	DROP TABLE IF EXISTS archivos_adjuntos, correo_usuario, correo_destinatarios, correos,
    mensajes_grupo, grupo_miembros, grupos, contactos, usuarios,
    administradores, solicitudes, auditoria_admin CASCADE;

CREATE TABLE usuarios (
    email            VARCHAR(150) PRIMARY KEY,
    cedula           VARCHAR(10)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    password         VARCHAR(14)  NOT NULL,
    telefono         VARCHAR(10),
    fecha_nacimiento DATE,
    genero           VARCHAR(15)  NOT NULL,
    fecha_registro   TIMESTAMP,
    suspendida       BOOLEAN      NOT NULL DEFAULT FALSE,
    motivo_suspension TEXT,
    fecha_suspension TIMESTAMP
);

-- Agenda privada de cada usuario
CREATE TABLE contactos (
    usuario_email VARCHAR(150) NOT NULL REFERENCES usuarios(email) ON DELETE CASCADE,
    email         VARCHAR(150) NOT NULL,
    nombre        VARCHAR(100),
    PRIMARY KEY (usuario_email, email)
);

-- Grupo único y compartido
CREATE TABLE grupos (
    id            SERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    descripcion   VARCHAR(300),
    creador_email VARCHAR(150) NOT NULL REFERENCES usuarios(email) ON DELETE CASCADE,
    UNIQUE (creador_email, nombre)
);

CREATE TABLE grupo_miembros (
    grupo_id INT NOT NULL REFERENCES grupos(id) ON DELETE CASCADE,
    email    VARCHAR(150) NOT NULL,
    nombre   VARCHAR(150),
    es_admin BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (grupo_id, email)
);

CREATE TABLE mensajes_grupo (
    id                 SERIAL PRIMARY KEY,
    grupo_id           INT NOT NULL REFERENCES grupos(id) ON DELETE CASCADE,
    remitente          VARCHAR(150),
    remitente_nombre   VARCHAR(150),
    contenido          TEXT,
    fecha_envio        TIMESTAMP,
    es_mensaje_sistema BOOLEAN NOT NULL DEFAULT FALSE
);

-- Un correo = una fila, sin copias por usuario
CREATE TABLE correos (
    id                 SERIAL PRIMARY KEY,
    remitente          VARCHAR(150) NOT NULL,
    asunto             VARCHAR(300),
    cuerpo             TEXT,
    fecha_envio        TIMESTAMP,
    estado             VARCHAR(20) NOT NULL,
    es_reenviado       BOOLEAN NOT NULL DEFAULT FALSE,
    asunto_original    VARCHAR(300),
    remitente_original VARCHAR(150),
    fecha_original     TIMESTAMP
);

CREATE TABLE correo_destinatarios (
    correo_id INT NOT NULL REFERENCES correos(id) ON DELETE CASCADE,
    email     VARCHAR(150) NOT NULL,
    tipo      VARCHAR(5) NOT NULL CHECK (tipo IN ('PARA','CC','CCO')),
    PRIMARY KEY (correo_id, email, tipo)
);

-- Estado PRIVADO por usuario (bandeja propia)
CREATE TABLE correo_usuario (
    correo_id     INT NOT NULL REFERENCES correos(id) ON DELETE CASCADE,
    usuario_email VARCHAR(150) NOT NULL REFERENCES usuarios(email) ON DELETE CASCADE,
    destacado     BOOLEAN NOT NULL DEFAULT FALSE,
    eliminado     BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (correo_id, usuario_email)
);

CREATE TABLE archivos_adjuntos (
    id             SERIAL PRIMARY KEY,
    correo_id      INT REFERENCES correos(id) ON DELETE CASCADE,
    mensaje_id     INT REFERENCES mensajes_grupo(id) ON DELETE CASCADE,
    nombre_archivo VARCHAR(255) NOT NULL,
    tamano         BIGINT,
    contenido      BYTEA,
    CHECK (correo_id IS NOT NULL OR mensaje_id IS NOT NULL)
);

CREATE TABLE administradores (
    email                     VARCHAR(150) PRIMARY KEY,
    nombre                    VARCHAR(150) NOT NULL,
    password_hash             VARCHAR(100) NOT NULL,
    salt                      VARCHAR(40)  NOT NULL,
    es_principal              BOOLEAN      NOT NULL DEFAULT FALSE,
    cambio_password_pendiente BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_creacion            TIMESTAMP    NOT NULL,
    creado_por                VARCHAR(150)
);

CREATE TABLE solicitudes (
    id              INT PRIMARY KEY,
    usuario_email   VARCHAR(150) NOT NULL,
    tipo            VARCHAR(20)  NOT NULL CHECK (tipo IN ('QUEJA','REPORTE_TECNICO','RECOMENDACION','APELACION')),
    asunto          VARCHAR(100) NOT NULL,
    descripcion     TEXT         NOT NULL,
    estado          VARCHAR(15)  NOT NULL CHECK (estado IN ('PENDIENTE','EN_REVISION','RESUELTA','RECHAZADA')),
    fecha_creacion  TIMESTAMP    NOT NULL,
    respuesta       TEXT,
    atendida_por    VARCHAR(150),
    fecha_respuesta TIMESTAMP
);

CREATE TABLE auditoria_admin (
    id          SERIAL PRIMARY KEY,
    fecha       TIMESTAMP    NOT NULL,
    admin_email VARCHAR(150) NOT NULL,
    accion      VARCHAR(40)  NOT NULL,
    objetivo    VARCHAR(200),
    detalle     TEXT
);

CREATE INDEX idx_solicitudes_estado ON solicitudes(estado);
CREATE INDEX idx_auditoria_fecha ON auditoria_admin(fecha);
CREATE INDEX idx_dest_email ON correo_destinatarios(email);
CREATE INDEX idx_miembros_email ON grupo_miembros(email);

CREATE OR REPLACE VIEW v_resumen_usuarios AS
SELECT u.email,
       u.nombre || ' ' || u.apellido AS nombre_completo,
       split_part(u.email, '@', 2)   AS dominio,
       u.fecha_registro,
       u.suspendida,
       (SELECT COUNT(*) FROM correos c WHERE c.remitente = u.email AND c.estado IN ('ENVIADO','REENVIADO')) AS correos_enviados,
       (SELECT COUNT(*) FROM contactos ct WHERE ct.usuario_email = u.email) AS contactos,
       (SELECT COUNT(*) FROM grupos g WHERE g.creador_email = u.email) AS grupos_creados
FROM usuarios u;

CREATE OR REPLACE VIEW v_solicitudes_resumen AS
SELECT tipo, estado, COUNT(*) AS total,
       ROUND(AVG(EXTRACT(EPOCH FROM (fecha_respuesta - fecha_creacion)) / 3600)::numeric, 1) AS horas_promedio_respuesta
FROM solicitudes
GROUP BY tipo, estado;

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS fecha_registro TIMESTAMP;
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS suspendida BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS motivo_suspension TEXT;
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS fecha_suspension TIMESTAMP;
ALTER TABLE solicitudes DROP CONSTRAINT IF EXISTS solicitudes_tipo_check;
ALTER TABLE solicitudes ADD CONSTRAINT solicitudes_tipo_check
    CHECK (tipo IN ('QUEJA','REPORTE_TECNICO','RECOMENDACION','APELACION'));

CREATE OR REPLACE VIEW v_desempeno_colaboradores AS
SELECT date_trunc('month', fecha)::date AS mes,
       admin_email,
       COUNT(*) FILTER (WHERE accion = 'ATENDER_SOLICITUD') AS solicitudes_atendidas,
       COUNT(*) FILTER (WHERE accion = 'ATENDER_SOLICITUD' AND upper(detalle) = 'RESUELTA') AS resueltas,
       COUNT(*) FILTER (WHERE accion = 'ATENDER_SOLICITUD' AND upper(detalle) = 'RECHAZADA') AS rechazadas,
       COUNT(*) FILTER (WHERE accion = 'SUSPENDER_USUARIO') AS cuentas_suspendidas,
       COUNT(*) FILTER (WHERE accion = 'REACTIVAR_USUARIO') AS cuentas_reactivadas,
       COUNT(*) FILTER (WHERE accion = 'ELIMINAR_USUARIO') AS cuentas_eliminadas
FROM auditoria_admin
GROUP BY date_trunc('month', fecha), admin_email;

SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

SELECT * FROM usuarios;
SELECT * FROM correos;
SELECT * FROM grupos;
SELECT * FROM v_resumen_usuarios;
SELECT * FROM v_solicitudes_resumen;
SELECT * FROM v_desempeno_colaboradores;
SELECT * FROM solicitudes ORDER BY fecha_creacion DESC;
SELECT * FROM auditoria_admin ORDER BY fecha DESC;

 */
