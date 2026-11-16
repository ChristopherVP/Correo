package uce.edu.ec.correo.mantenimiento;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import uce.edu.ec.correo.DB.PersistenciaDB;
import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.util.ConfiguracionAdmin;

/**
 * HERRAMIENTA DE MANTENIMIENTO: deja EcuMail como recién instalado.
 *
 * Ejecútala con clic derecho > Run File sobre esta clase. NO forma parte del programa normal.
 *
 * Qué borra: todo lo creado desde que se usa el sistema: usuarios, correos, contactos, grupos, solicitudes, apelaciones,
 * auditoría y administradores adicionales (archivo datos_sistema.dat, usuarios_registrados.txt y las tablas de PostgreSQL).
 * Qué conserva / vuelve a crear: el administrador principal por defecto, igual que en el primer arranque.
 *
 * ADMINISTRADOR POR DEFECTO PARA ENTRAR DESPUÉS DEL RESTABLECIMIENTO:
 *     Correo:     admin@ecumail.ec
 *     Contraseña: CambiarAdmin1
 * (el sistema te obliga a cambiarla en el primer inicio de sesión; si definiste las variables de entorno
 *  ECUMAIL_ADMIN_EMAIL / ECUMAIL_ADMIN_PASSWORD se usan esas en lugar de estas)
 */
public final class RestablecerSistema {

    private static final Path[] ARCHIVOS_LOCALES = {Path.of("datos_sistema.dat"), Path.of("usuarios_registrados.txt")};

    private RestablecerSistema() {
    }

    public static void main(String[] args) {
        String confirmacion = JOptionPane.showInputDialog(null,
                "Esto BORRARÁ todos los usuarios, correos y demás datos que se crearon en EcuMail.\n"
                        + "Se mantiene solo el administrador por defecto.\n\nEscribe BORRAR para continuar:",
                "Restablecer EcuMail", JOptionPane.WARNING_MESSAGE);
        if (confirmacion == null || !confirmacion.trim().equals("BORRAR")) {
            System.out.println("Cancelado: no se borró nada.");
            return;
        }
        StringBuilder informe = new StringBuilder();
        try {
            for (Path archivo : ARCHIVOS_LOCALES) {
                Files.deleteIfExists(archivo);
            }
            EmpresaCorreo vacia = new EmpresaCorreo();
            vacia.guardarDatos();
            informe.append("Datos locales restablecidos.\n");
            if (PersistenciaDB.guardarTodo(vacia)) {
                informe.append("PostgreSQL restablecido.\n");
            } else {
                informe.append("AVISO: no se pudo limpiar PostgreSQL (¿está encendido el servidor?). Revisa db.properties y vuelve a ejecutar.\n");
            }
            informe.append("\nAdministrador: ").append(ConfiguracionAdmin.emailInicial()).append(" / ")
                    .append(ConfiguracionAdmin.usaPasswordPorDefecto() ? "CambiarAdmin1" : "(la de tu variable ECUMAIL_ADMIN_PASSWORD)");
            JOptionPane.showMessageDialog(null, informe.toString(), "Restablecer EcuMail", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo restablecer: " + e.getMessage(), "Restablecer EcuMail", JOptionPane.ERROR_MESSAGE);
        }
    }
}
