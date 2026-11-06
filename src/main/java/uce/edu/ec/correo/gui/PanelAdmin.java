package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.Administrador;
import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.util.EstiloUI;

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
        JButton btnExportar = EstiloUI.boton("Exportar reportes");
        btnExportar.addActionListener(e ->
                new DialogoExportarReporte(SwingUtilities.getWindowAncestor(this), empresa).setVisible(true));
        JButton btnPassword = EstiloUI.boton("Cambiar contraseña");
        btnPassword.addActionListener(e -> solicitarCambioPassword(false));
        JButton btnSalir = EstiloUI.botonPeligro("Cerrar sesión");
        btnSalir.addActionListener(e -> cerrarSesion(true));
        acciones.add(btnExportar);
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
        pestanas.addTab("Usuarios", new PanelAdminUsuarios(empresa));
        if (admin.esPrincipal()) {
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
        if (!solicitarCambioPassword(true)) {
            EstiloUI.info(this, "No se cambió la contraseña. Se cerrará su sesión.");
            cerrarSesion(false);
        }
    }

    private boolean solicitarCambioPassword(boolean obligatorio) {
        DialogoCambiarPasswordAdmin dialogo = new DialogoCambiarPasswordAdmin(
                SwingUtilities.getWindowAncestor(this), empresa, obligatorio);
        dialogo.setVisible(true);
        return dialogo.fueCambiada();
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
