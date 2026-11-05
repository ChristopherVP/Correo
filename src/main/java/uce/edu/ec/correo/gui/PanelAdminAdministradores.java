package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.Administrador;
import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.util.ConfiguracionAdmin;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelAdminAdministradores extends JPanel implements PanelAdmin.Refrescable {

    private final EmpresaCorreo empresa;
    private final DefaultTableModel modelo = EstiloUI.modelo("Email", "Nombre", "Rol", "Creado por",
            "Fecha de creación", "Cambio de contraseña pendiente");
    private final JTable tabla = EstiloUI.tabla(modelo);

    public PanelAdminAdministradores(EmpresaCorreo empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        add(EstiloUI.titulo("Administradores y colaboradores"), BorderLayout.NORTH);
        add(EstiloUI.conDesplazamiento(tabla), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(10, 10));
        inferior.setOpaque(false);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        JButton btnActualizar = EstiloUI.boton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());
        JButton btnNuevo = EstiloUI.boton("Nuevo administrador");
        btnNuevo.addActionListener(e -> crearAdministrador());
        JButton btnEliminar = EstiloUI.botonPeligro("Eliminar administrador");
        btnEliminar.addActionListener(e -> eliminarAdministrador());
        botones.add(btnActualizar);
        botones.add(btnNuevo);
        botones.add(btnEliminar);
        inferior.add(botones, BorderLayout.NORTH);
        inferior.add(EstiloUI.nota("Los colaboradores pueden atender el Centro de Solicitudes y Mejoras y suspender, "
                + "reactivar o eliminar cuentas, y solo exportan su propio trabajo. Deben cambiar su contraseña "
                + "temporal en el primer ingreso."), BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        try {
            modelo.setRowCount(0);
            for (Administrador a : empresa.listarAdministradores()) {
                modelo.addRow(new Object[]{
                        a.getEmail(), a.getNombre(), a.esPrincipal() ? "Principal" : "Colaborador",
                        a.getCreadoPor(), EstiloUI.fecha(a.getFechaCreacion()),
                        a.isCambioPasswordPendiente() ? "Sí" : "No"});
            }
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private void crearAdministrador() {
        JTextField txtEmail = new JTextField(24);
        JTextField txtNombre = new JTextField(24);
        JPasswordField txtPassword = new JPasswordField(24);

        JPanel formulario = new JPanel(new GridLayout(0, 1, 4, 4));
        formulario.add(new JLabel("Email del colaborador (solo escriba el nombre, el dominio se agrega solo):"));
        JPanel filaEmail = new JPanel(new BorderLayout(4, 0));
        filaEmail.add(txtEmail, BorderLayout.CENTER);
        JLabel lblDominio = new JLabel(ConfiguracionAdmin.DOMINIO_ADMIN);
        lblDominio.setFont(new Font("Arial", Font.BOLD, 13));
        filaEmail.add(lblDominio, BorderLayout.EAST);
        formulario.add(filaEmail);
        formulario.add(new JLabel("Nombre completo:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Contraseña temporal (8-14 caracteres, al menos una mayúscula):"));
        formulario.add(txtPassword);

        while (true) {
            int opcion = JOptionPane.showConfirmDialog(this, formulario, "Nuevo administrador",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }
            try {
                empresa.crearAdministrador(emailCompleto(txtEmail.getText()), txtNombre.getText(),
                        new String(txtPassword.getPassword()));
                EstiloUI.info(this, "Administrador creado. Deberá cambiar su contraseña al ingresar.");
                refrescar();
                return;
            } catch (ValidationException e) {
                EstiloUI.error(this, e.getMensaje());
            }
        }
    }

    private String emailCompleto(String texto) {
        String limpio = texto == null ? "" : texto.trim().toLowerCase();
        if (limpio.isEmpty() || limpio.contains("@")) {
            return limpio;
        }
        return limpio + ConfiguracionAdmin.DOMINIO_ADMIN;
    }

    private void eliminarAdministrador() {
        Object email = EstiloUI.valorSeleccionado(tabla, 0);
        if (email == null) {
            EstiloUI.info(this, "Seleccione un administrador de la tabla.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la cuenta de administrador " + email + "?",
                "Eliminar administrador", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            empresa.eliminarAdministrador(email.toString());
            refrescar();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }
}
