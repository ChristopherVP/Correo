package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.Administrador;
import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import java.awt.*;

public class PanelAdmin extends JPanel {

    public interface Refrescable {
        void refrescar();
    }

    private final VentanaPrincipal ventanaPrincipal;
    private final EmpresaCorreo empresa;
    private final JLabel lblBienvenida = new JLabel();
    private final JTabbedPane pestanas = new JTabbedPane();

    public PanelAdmin(VentanaPrincipal ventanaPrincipal, EmpresaCorreo empresa) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.empresa = empresa;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout());
        setBackground(EstiloUI.FONDO);

        JPanel encabezado = new JPanel(new BorderLayout(10, 0));
        encabezado.setBackground(EstiloUI.AZUL);
        encabezado.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        lblBienvenida.setForeground(Color.WHITE);
        encabezado.add(lblBienvenida, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        JButton btnPassword = EstiloUI.boton("Cambiar contraseña");
        btnPassword.addActionListener(e -> solicitarCambioPassword());
        JButton btnSalir = EstiloUI.botonPeligro("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion(true));
        acciones.add(btnPassword);
        acciones.add(btnSalir);
        encabezado.add(acciones, BorderLayout.EAST);

        add(encabezado, BorderLayout.NORTH);

        pestanas.setFont(new Font("Arial", Font.BOLD, 13));
        pestanas.addChangeListener(e -> refrescarSeleccionada());
        add(pestanas, BorderLayout.CENTER);
    }

    public void actualizar() {
        Administrador admin = empresa.getAdministradorActual();
        if (admin == null) {
            return;
        }
        lblBienvenida.setText("Panel de Administración — " + admin.getNombre()
                + (admin.esPrincipal() ? " (Administrador principal)" : " (Colaborador)"));

        pestanas.removeAll();
        pestanas.addTab("Resumen", new PanelAdminResumen(empresa));
        pestanas.addTab("Centro de Solicitudes y Mejoras", new PanelAdminSolicitudes(empresa));
        if (admin.esPrincipal()) {
            pestanas.addTab("Usuarios", new PanelAdminUsuarios(empresa));
            pestanas.addTab("Administradores", new PanelAdminAdministradores(empresa));
            pestanas.addTab("Auditoría", new PanelAdminAuditoria(empresa));
        }
        pestanas.setSelectedIndex(0);
        refrescarSeleccionada();
        SwingUtilities.invokeLater(this::exigirCambioPasswordSiCorresponde);
    }

    private void refrescarSeleccionada() {
        Component seleccionado = pestanas.getSelectedComponent();
        if (seleccionado instanceof Refrescable refrescable) {
            refrescable.refrescar();
        }
    }

    private void exigirCambioPasswordSiCorresponde() {
        Administrador admin = empresa.getAdministradorActual();
        if (admin == null || !admin.isCambioPasswordPendiente()) {
            return;
        }
        EstiloUI.info(this, "Por seguridad debe cambiar su contraseña antes de continuar.");
        if (!solicitarCambioPassword()) {
            cerrarSesion(false);
        }
    }

    private boolean solicitarCambioPassword() {
        JPasswordField actual = new JPasswordField(18);
        JPasswordField nueva = new JPasswordField(18);
        JPasswordField confirmar = new JPasswordField(18);

        JPanel formulario = new JPanel(new GridLayout(0, 1, 4, 4));
        formulario.add(new JLabel("Contraseña actual:"));
        formulario.add(actual);
        formulario.add(new JLabel("Nueva contraseña (8-14 caracteres, al menos una mayúscula):"));
        formulario.add(nueva);
        formulario.add(new JLabel("Confirmar nueva contraseña:"));
        formulario.add(confirmar);

        while (true) {
            actual.setText("");
            nueva.setText("");
            confirmar.setText("");
            int opcion = JOptionPane.showConfirmDialog(this, formulario, "Cambiar contraseña",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return false;
            }
            String nuevaPassword = new String(nueva.getPassword());
            if (!nuevaPassword.equals(new String(confirmar.getPassword()))) {
                EstiloUI.error(this, "Las contraseñas no coinciden.");
                continue;
            }
            try {
                empresa.cambiarPasswordAdministrador(new String(actual.getPassword()), nuevaPassword);
                EstiloUI.info(this, "Contraseña actualizada correctamente.");
                return true;
            } catch (ValidationException e) {
                EstiloUI.error(this, e.getMensaje());
            }
        }
    }

    private void cerrarSesion(boolean confirmar) {
        if (confirmar) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea cerrar sesión?",
                    "Confirmar cierre de sesión", JOptionPane.YES_NO_OPTION);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        empresa.cerrarSesion();
        pestanas.removeAll();
        ventanaPrincipal.mostrarPanel("LOGIN");
    }
}
