package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.ResumenUsuario;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelAdminUsuarios extends JPanel implements PanelAdmin.Refrescable {

    private static final int COLUMNA_EMAIL = 0;
    private static final int COLUMNA_ESTADO = 9;

    private final EmpresaCorreo empresa;
    private final DefaultTableModel modelo = EstiloUI.modelo("Email", "Nombre", "Cédula", "Provincia",
            "Teléfono", "Registro", "Enviados", "Contactos", "Grupos", "Estado");
    private final JTable tabla = EstiloUI.tabla(modelo);
    private final JTextField txtBuscar = new JTextField(24);
    private List<ResumenUsuario> usuarios = List.of();

    public PanelAdminUsuarios(EmpresaCorreo empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel superior = new JPanel(new BorderLayout(10, 0));
        superior.setOpaque(false);
        superior.add(EstiloUI.titulo("Gestión de usuarios"), BorderLayout.WEST);
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        busqueda.setOpaque(false);
        busqueda.add(new JLabel("Buscar:"));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                llenarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                llenarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                llenarTabla();
            }
        });
        busqueda.add(txtBuscar);
        superior.add(busqueda, BorderLayout.EAST);
        add(superior, BorderLayout.NORTH);

        add(EstiloUI.conDesplazamiento(tabla), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(10, 10));
        inferior.setOpaque(false);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        JButton btnActualizar = EstiloUI.boton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());
        JButton btnSuspender = EstiloUI.boton("Suspender cuenta");
        btnSuspender.addActionListener(e -> suspender());
        JButton btnReactivar = EstiloUI.boton("Reactivar cuenta");
        btnReactivar.addActionListener(e -> reactivar());
        JButton btnEliminar = EstiloUI.botonPeligro("Eliminar cuenta");
        btnEliminar.addActionListener(e -> eliminar());
        botones.add(btnActualizar);
        botones.add(btnSuspender);
        botones.add(btnReactivar);
        botones.add(btnEliminar);
        inferior.add(botones, BorderLayout.NORTH);
        inferior.add(EstiloUI.nota("Privacidad: la cédula y el teléfono se muestran enmascarados y no existe acceso "
                        + "a contraseñas, correos, contactos ni chats. Toda acción queda registrada en Auditoría."),
                BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        try {
            usuarios = empresa.listarResumenUsuarios();
            llenarTabla();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private void llenarTabla() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        modelo.setRowCount(0);
        for (ResumenUsuario u : usuarios) {
            if (!filtro.isEmpty() && !u.email().toLowerCase().contains(filtro)
                    && !u.nombreCompleto().toLowerCase().contains(filtro)) {
                continue;
            }
            modelo.addRow(new Object[]{
                    u.email(), u.nombreCompleto(), u.cedulaEnmascarada(), u.provincia(), u.telefonoEnmascarado(),
                    EstiloUI.fecha(u.fechaRegistro()), u.correosEnviados(), u.totalContactos(), u.totalGrupos(),
                    u.suspendida() ? "Suspendida" : "Activa"});
        }
    }

    private String emailSeleccionado() {
        Object valor = EstiloUI.valorSeleccionado(tabla, COLUMNA_EMAIL);
        if (valor == null) {
            EstiloUI.info(this, "Seleccione un usuario de la tabla.");
            return null;
        }
        return valor.toString();
    }

    private String pedirMotivo(String titulo) {
        Object motivo = JOptionPane.showInputDialog(this,
                "Motivo (mínimo 10 caracteres, quedará registrado en Auditoría):",
                titulo, JOptionPane.PLAIN_MESSAGE);
        return motivo == null ? null : motivo.toString();
    }

    private void suspender() {
        String email = emailSeleccionado();
        if (email == null) {
            return;
        }
        String motivo = pedirMotivo("Suspender cuenta");
        if (motivo == null) {
            return;
        }
        try {
            empresa.suspenderUsuario(email, motivo);
            EstiloUI.info(this, "La cuenta fue suspendida.");
            refrescar();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private void reactivar() {
        String email = emailSeleccionado();
        if (email == null) {
            return;
        }
        Object estado = EstiloUI.valorSeleccionado(tabla, COLUMNA_ESTADO);
        if (!"Suspendida".equals(estado)) {
            EstiloUI.info(this, "La cuenta seleccionada no está suspendida.");
            return;
        }
        try {
            empresa.reactivarUsuario(email);
            EstiloUI.info(this, "La cuenta fue reactivada.");
            refrescar();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private void eliminar() {
        String email = emailSeleccionado();
        if (email == null) {
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "<html><body style='width: 320px;'><b>Esta acción es irreversible.</b><br>Se eliminará la cuenta "
                        + email + " junto con sus correos, contactos y grupos creados. "
                        + "Sus solicitudes quedarán anonimizadas.<br><br>¿Desea continuar?</body></html>",
                "Eliminar cuenta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        String motivo = pedirMotivo("Eliminar cuenta");
        if (motivo == null) {
            return;
        }
        try {
            empresa.eliminarUsuarioComoAdmin(email, motivo);
            EstiloUI.info(this, "La cuenta fue eliminada.");
            refrescar();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }
}
