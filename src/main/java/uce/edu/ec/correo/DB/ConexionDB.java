package uce.edu.ec.correo.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/correo";
    private static final String USER = "postgres";
    private static final String PASSWORD = "DB_Password";

    private ConexionDB() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

/*

	DROP TABLE IF EXISTS archivos_adjuntos, correo_usuario, correo_destinatarios, correos,
    mensajes_grupo, grupo_miembros, grupos, contactos, usuarios CASCADE;

CREATE TABLE usuarios (
    email            VARCHAR(150) PRIMARY KEY,
    cedula           VARCHAR(10)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    password         VARCHAR(14)  NOT NULL,
    telefono         VARCHAR(10),
    fecha_nacimiento DATE,
    genero           VARCHAR(15)  NOT NULL
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

CREATE INDEX idx_dest_email ON correo_destinatarios(email);
CREATE INDEX idx_miembros_email ON grupo_miembros(email);

SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

SELECT * FROM usuarios;
SELECT * FROM correos;
SELECT * FROM grupos;

 */
