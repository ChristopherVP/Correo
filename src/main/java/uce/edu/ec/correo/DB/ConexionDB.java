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
