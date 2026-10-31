package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.Usuario;
import uce.edu.ec.correo.util.IconoUtils;
import uce.edu.ec.correo.util.Validador;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import java.awt.*;

/**
 * Clase que representa el panel de información personal del usuario.
 * Muestra todos los datos del perfil del usuario de forma organizada y legible.
 * Incluye opciones para cambiar contraseña, eliminar cuenta y regresar al menú.
 */
public class PanelInformacionUsuario extends JPanel {

    // Referencias a otros componentes del sistema
    private EmpresaCorreo empresa;
    private VentanaPrincipal ventanaPrincipal;
    private PanelMenuPrincipal panelMenuPrincipal;

    // Etiquetas que muestran la información del usuario
    private JLabel lblCedula;
    private JLabel lblProvincia;
    private JLabel lblNombre;
    private JLabel lblApellido;
    private JLabel lblEmail;
    private JLabel lblTelefono;
    private JLabel lblFechaNacimiento;
    private JLabel lblEdad;
    private JLabel lblGenero;

    // Botones de acción
    private JButton btnCambiarPassword;
    private JButton btnEliminarCuenta;
    private JButton btnRegresar;

    public PanelInformacionUsuario(EmpresaCorreo empresa) {
        this.empresa = empresa;
        inicializarComponentes();
    }

    public void setVentanaPrincipal(VentanaPrincipal ventana) {
        this.ventanaPrincipal = ventana;
    }

    public void setPanelMenuPrincipal(PanelMenuPrincipal panelMenu) {
        this.panelMenuPrincipal = panelMenu;
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 250));

        // Panel contenedor principal
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        panelContenedor.setBackground(new Color(245, 248, 250));
        GridBagConstraints gbcContenedor = new GridBagConstraints();
        gbcContenedor.gridx = 0;
        gbcContenedor.gridy = 0;
        gbcContenedor.weightx = 1.0;
        gbcContenedor.weighty = 1.0;
        gbcContenedor.anchor = GridBagConstraints.CENTER;

        // Panel central
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        panelCentral.setPreferredSize(new Dimension(700, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelTitulo.setBackground(Color.WHITE);
        JLabel lblIcono = new JLabel(IconoUtils.iconoInformacionPersonal(36));
        panelTitulo.add(lblIcono);
        JLabel lblTitulo = new JLabel("Mi Información Personal");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(0, 102, 204));
        panelTitulo.add(lblTitulo);
        panelCentral.add(panelTitulo, gbc);

        // Separador
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 50, 25, 50);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0, 102, 204));
        sep.setPreferredSize(new Dimension(500, 2));
        panelCentral.add(sep, gbc);

        // Panel de datos
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBackground(Color.WHITE);

        GridBagConstraints gbcDatos = new GridBagConstraints();
        gbcDatos.insets = new Insets(10, 20, 10, 20);
        gbcDatos.fill = GridBagConstraints.HORIZONTAL;
        gbcDatos.anchor = GridBagConstraints.WEST;

        int fila = 0;
        agregarCampo(panelDatos, gbcDatos, fila++, "Cédula:", lblCedula = new JLabel());
        agregarCampo(panelDatos, gbcDatos, fila++, "Provincia:", lblProvincia = new JLabel());
        lblProvincia.setForeground(new Color(80, 80, 80));
        agregarCampo(panelDatos, gbcDatos, fila++, "Nombre:", lblNombre = new JLabel());
        agregarCampo(panelDatos, gbcDatos, fila++, "Apellido:", lblApellido = new JLabel());
        agregarCampo(panelDatos, gbcDatos, fila++, "Email:", lblEmail = new JLabel());
        agregarCampo(panelDatos, gbcDatos, fila++, "Teléfono:", lblTelefono = new JLabel());
        agregarCampo(panelDatos, gbcDatos, fila++, "Fecha de Nacimiento:", lblFechaNacimiento = new JLabel());
        agregarCampo(panelDatos, gbcDatos, fila++, "Edad:", lblEdad = new JLabel());
        agregarCampo(panelDatos, gbcDatos, fila++, "Género:", lblGenero = new JLabel());

        panelCentral.add(panelDatos, gbc);

        // Botones
        gbc.gridy = 3;
        gbc.insets = new Insets(30, 15, 10, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(Color.WHITE);

        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.gridx = 0;
        gbcBtn.gridy = 0;
        gbcBtn.insets = new Insets(6, 10, 6, 10);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.anchor = GridBagConstraints.CENTER;

        // Botón Cambiar Contraseña
        btnCambiarPassword = new JButton(" Cambiar Contraseña", IconoUtils.iconoCambiarPassword(20));
        btnCambiarPassword.setFont(new Font("Arial", Font.BOLD, 14));
        btnCambiarPassword.setForeground(Color.BLACK);
        btnCambiarPassword.setBackground(Color.WHITE);
        btnCambiarPassword.setFocusPainted(false);
        btnCambiarPassword.setPreferredSize(new Dimension(260, 45));
        btnCambiarPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCambiarPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btnCambiarPassword.setHorizontalAlignment(SwingConstants.LEFT);
        btnCambiarPassword.setIconTextGap(10);
        btnCambiarPassword.addActionListener(e -> cambiarPassword());
        panelBotones.add(btnCambiarPassword, gbcBtn);

        gbcBtn.gridy = 1;

        // Botón Eliminar Cuenta
        btnEliminarCuenta = new JButton(" Eliminar Mi Cuenta", IconoUtils.iconoEliminarCuenta(20));
        btnEliminarCuenta.setFont(new Font("Arial", Font.BOLD, 14));
        btnEliminarCuenta.setForeground(Color.BLACK);
        btnEliminarCuenta.setBackground(Color.WHITE);
        btnEliminarCuenta.setFocusPainted(false);
        btnEliminarCuenta.setPreferredSize(new Dimension(260, 45));
        btnEliminarCuenta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminarCuenta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btnEliminarCuenta.setHorizontalAlignment(SwingConstants.LEFT);
        btnEliminarCuenta.setIconTextGap(10);
        btnEliminarCuenta.addActionListener(e -> eliminarCuenta());
        panelBotones.add(btnEliminarCuenta, gbcBtn);

        panelCentral.add(panelBotones, gbc);
        panelContenedor.add(panelCentral, gbcContenedor);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(panelContenedor);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelInferior.setBackground(new Color(245, 248, 250));

        btnRegresar = new JButton(" Regresar", IconoUtils.iconoRegresar(18));
        btnRegresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegresar.setForeground(Color.BLACK);
        btnRegresar.setBackground(Color.WHITE);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setPreferredSize(new Dimension(140, 42));
        btnRegresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegresar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btnRegresar.setHorizontalAlignment(SwingConstants.LEFT);
        btnRegresar.setIconTextGap(8);
        btnRegresar.addActionListener(e -> regresar());
        panelInferior.add(btnRegresar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String titulo, JLabel valorLabel) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.4;

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitulo.setForeground(new Color(40, 40, 40));
        panel.add(lblTitulo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;

        valorLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        valorLabel.setForeground(new Color(60, 60, 60));
        panel.add(valorLabel, gbc);
    }

    public void actualizarInformacion() {
        if (empresa.hayUsuarioLogueado()) {
            Usuario usuario = empresa.getUsuarioActual();

            lblCedula.setText(usuario.getCedula());
            String provincia = Validador.obtenerProvinciaPorCedula(usuario.getCedula());
            lblProvincia.setText(provincia);
            lblNombre.setText(usuario.getNombre());
            lblApellido.setText(usuario.getApellido());
            lblEmail.setText(usuario.getEmail());
            lblTelefono.setText(usuario.getTelefono() != null && !usuario.getTelefono().isEmpty()
                    ? usuario.getTelefono()
                    : "No registrado");

            if (usuario.getFechaNacimiento() != null) {
                lblFechaNacimiento.setText(usuario.getFechaNacimiento().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                int edad = Validador.calcularEdad(usuario.getFechaNacimiento());
                lblEdad.setText(edad + " años");
            } else {
                lblFechaNacimiento.setText("No registrada");
                lblEdad.setText("-");
            }

            lblGenero.setText(usuario.getGenero().getDescripcion());
        }
    }

    private void regresar() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea regresar al menú principal?",
                "Confirmar regreso",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            if (panelMenuPrincipal != null) {
                panelMenuPrincipal.volverAlMenuPrincipal();
            }
        }
    }

    /**
     * Método que maneja el cambio de contraseña con validación completa.
     */
    private void cambiarPassword() {
        if (!empresa.hayUsuarioLogueado()) return;

        Usuario usuario = empresa.getUsuarioActual();

        // ===== PASO 1: Verificar contraseña actual =====
        JPasswordField passwordActualField = new JPasswordField(20);
        JPanel panelPasswordActual = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblInstruccion = new JLabel("Ingrese su contraseña actual:");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 12));
        panelPasswordActual.add(lblInstruccion, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelPasswordActual.add(passwordActualField, gbc);

        int resultadoActual = JOptionPane.showConfirmDialog(
                this,
                panelPasswordActual,
                "Verificar Contraseña Actual",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultadoActual != JOptionPane.OK_OPTION) {
            // Confirmar cancelación
            int confirmarCancelar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea cancelar el cambio de contraseña?",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirmarCancelar == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cambio de contraseña cancelado.",
                        "Operación Cancelada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            return;
        }

        String passwordActual = new String(passwordActualField.getPassword());

        // Validar contraseña actual
        if (!usuario.validarPassword(passwordActual)) {
            JOptionPane.showMessageDialog(
                    this,
                    "La contraseña actual es incorrecta.",
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ===== PASO 2: Ingresar nueva contraseña =====
        JPanel panelNuevaPassword = new JPanel(new GridBagLayout());
        GridBagConstraints gbcNueva = new GridBagConstraints();
        gbcNueva.insets = new Insets(5, 5, 5, 5);
        gbcNueva.anchor = GridBagConstraints.WEST;
        gbcNueva.fill = GridBagConstraints.HORIZONTAL;

        JPasswordField nuevaPasswordField = new JPasswordField(20);
        JPasswordField confirmarPasswordField = new JPasswordField(20);

        // Botones de mostrar/ocultar para nueva contraseña
        JToggleButton btnMostrarNueva = new JToggleButton(IconoUtils.iconoOjoOculto(20));
        btnMostrarNueva.setPreferredSize(new Dimension(40, 30));
        btnMostrarNueva.setFocusPainted(false);
        btnMostrarNueva.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnMostrarNueva.setBackground(Color.WHITE);
        btnMostrarNueva.addActionListener(e -> {
            if (btnMostrarNueva.isSelected()) {
                nuevaPasswordField.setEchoChar((char) 0);
                btnMostrarNueva.setIcon(IconoUtils.iconoOjoVisible(20));
            } else {
                nuevaPasswordField.setEchoChar('●');
                btnMostrarNueva.setIcon(IconoUtils.iconoOjoOculto(20));
            }
        });

        JToggleButton btnMostrarConfirmar = new JToggleButton(IconoUtils.iconoOjoOculto(20));
        btnMostrarConfirmar.setPreferredSize(new Dimension(40, 30));
        btnMostrarConfirmar.setFocusPainted(false);
        btnMostrarConfirmar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnMostrarConfirmar.setBackground(Color.WHITE);
        btnMostrarConfirmar.addActionListener(e -> {
            if (btnMostrarConfirmar.isSelected()) {
                confirmarPasswordField.setEchoChar((char) 0);
                btnMostrarConfirmar.setIcon(IconoUtils.iconoOjoVisible(20));
            } else {
                confirmarPasswordField.setEchoChar('●');
                btnMostrarConfirmar.setIcon(IconoUtils.iconoOjoOculto(20));
            }
        });

        // Título
        gbcNueva.gridx = 0;
        gbcNueva.gridy = 0;
        gbcNueva.gridwidth = 2;
        JLabel lblTitulo = new JLabel("Ingrese su nueva contraseña:");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        panelNuevaPassword.add(lblTitulo, gbcNueva);

        // Requisitos
        gbcNueva.gridy = 1;
        JLabel lblRequisitos = new JLabel("<html>• 8-14 caracteres<br>• Al menos una mayúscula</html>");
        lblRequisitos.setFont(new Font("Arial", Font.PLAIN, 10));
        lblRequisitos.setForeground(Color.GRAY);
        panelNuevaPassword.add(lblRequisitos, gbcNueva);

        // Nueva contraseña
        gbcNueva.gridy = 2;
        gbcNueva.gridwidth = 1;
        JLabel lblNueva = new JLabel("Nueva Contraseña:");
        panelNuevaPassword.add(lblNueva, gbcNueva);

        gbcNueva.gridx = 1;
        panelNuevaPassword.add(nuevaPasswordField, gbcNueva);

        gbcNueva.gridx = 2;
        gbcNueva.fill = GridBagConstraints.NONE;
        panelNuevaPassword.add(btnMostrarNueva, gbcNueva);

        // Confirmar contraseña
        gbcNueva.gridx = 0;
        gbcNueva.gridy = 3;
        gbcNueva.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblConfirmar = new JLabel("Confirmar Contraseña:");
        panelNuevaPassword.add(lblConfirmar, gbcNueva);

        gbcNueva.gridx = 1;
        panelNuevaPassword.add(confirmarPasswordField, gbcNueva);

        gbcNueva.gridx = 2;
        gbcNueva.fill = GridBagConstraints.NONE;
        panelNuevaPassword.add(btnMostrarConfirmar, gbcNueva);

        int resultadoNueva = JOptionPane.showConfirmDialog(
                this,
                panelNuevaPassword,
                "Cambiar Contraseña",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultadoNueva != JOptionPane.OK_OPTION) {
            // Confirmar cancelación
            int confirmarCancelar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea cancelar el cambio de contraseña?",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirmarCancelar == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cambio de contraseña cancelado.",
                        "Operación Cancelada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            return;
        }

        String nuevaPassword = new String(nuevaPasswordField.getPassword());
        String confirmarPassword = new String(confirmarPasswordField.getPassword());

        // Validaciones
        try {
            // Validar que no esté vacía
            if (!Validador.validarCampoNoVacio(nuevaPassword, "Nueva contraseña")) {
                return;
            }

            // Validar formato de contraseña
            if (!Validador.validarPassword(nuevaPassword)) {
                throw new ValidationException(
                        "La contraseña debe tener entre 8 y 14 caracteres y al menos una mayúscula.",
                        "Nueva Contraseña"
                );
            }

            // Validar que coincidan
            if (!nuevaPassword.equals(confirmarPassword)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Las contraseñas no coinciden.",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Validar que no sea igual a la actual
            if (nuevaPassword.equals(passwordActual)) {
                JOptionPane.showMessageDialog(
                        this,
                        "La nueva contraseña debe ser diferente a la actual.",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Cambiar la contraseña
            usuario.setPassword(nuevaPassword);
            empresa.guardarDatosAutomaticamente();

            JOptionPane.showMessageDialog(
                    this,
                    "Contraseña cambiada exitosamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Método que maneja la eliminación de cuenta con advertencias y confirmación.
     */
    private void eliminarCuenta() {
        if (!empresa.hayUsuarioLogueado()) return;

        Usuario usuario = empresa.getUsuarioActual();

        // ===== PASO 1: Advertencia inicial =====
        String mensaje = "<html><body style='width: 300px;'>"
                + "<h3 style='color: #DC3545;'>⚠️ ADVERTENCIA</h3>"
                + "<p>Esta acción es <b>irreversible</b> y eliminará permanentemente:</p>"
                + "<ul>"
                + "<li>Su cuenta de usuario</li>"
                + "<li>Todos sus correos</li>"
                + "<li>Todos sus contactos</li>"
                + "<li>Todos sus grupos</li>"
                + "<li>Toda su información personal</li>"
                + "</ul>"
                + "<p><b>¿Está seguro que desea continuar?</b></p>"
                + "</body></html>";

        int respuestaAdvertencia = JOptionPane.showConfirmDialog(
                this,
                mensaje,
                "Advertencia - Eliminar Cuenta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuestaAdvertencia != JOptionPane.YES_OPTION) {
            // Usuario canceló en la advertencia inicial
            JOptionPane.showMessageDialog(
                    this,
                    "Eliminación de cuenta cancelada.",
                    "Operación Cancelada",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // ===== PASO 2: Verificar contraseña =====
        JPasswordField passwordField = new JPasswordField(20);
        JPanel panelPassword = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblInstruccion = new JLabel("<html><b>Para confirmar, ingrese su contraseña:</b></html>");
        lblInstruccion.setFont(new Font("Arial", Font.PLAIN, 12));
        panelPassword.add(lblInstruccion, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelPassword.add(passwordField, gbc);

        int resultadoPassword = JOptionPane.showConfirmDialog(
                this,
                panelPassword,
                "Verificar Contraseña",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultadoPassword != JOptionPane.OK_OPTION) {
            // Confirmar cancelación
            int confirmarCancelar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea cancelar la eliminación de su cuenta?",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirmarCancelar == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                        this,
                        "Eliminación de cuenta cancelada.",
                        "Operación Cancelada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            return;
        }

        String password = new String(passwordField.getPassword());

        // Validar contraseña
        if (!usuario.validarPassword(password)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Contraseña incorrecta. La cuenta no ha sido eliminada.",
                    "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ===== PASO 3: Confirmación final =====
        String mensajeFinal = "<html><body style='width: 300px;'>"
                + "<h3 style='color: #DC3545;'>⚠️ ÚLTIMA CONFIRMACIÓN</h3>"
                + "<p><b>Esta es su última oportunidad para cancelar.</b></p>"
                + "<p>¿Está <b>completamente seguro</b> que desea eliminar su cuenta?</p>"
                + "<p style='color: red;'><b>ESTA ACCIÓN NO SE PUEDE DESHACER</b></p>"
                + "</body></html>";

        int confirmacionFinal = JOptionPane.showConfirmDialog(
                this,
                mensajeFinal,
                "Confirmación Final",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacionFinal != JOptionPane.YES_OPTION) {
            // Confirmar cancelación final
            int confirmarCancelar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea cancelar la eliminación de su cuenta?",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirmarCancelar == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                        this,
                        "Eliminación de cuenta cancelada.",
                        "Operación Cancelada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            return;
        }

        // ===== PASO 4: Eliminar cuenta =====
        try {
            boolean eliminado = empresa.eliminarCuentaUsuario();

            if (eliminado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Su cuenta ha sido eliminada exitosamente.\nSerá redirigido a la pantalla de inicio de sesión.",
                        "Cuenta Eliminada",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Redirigir a la pantalla de login usando el nombre del panel correcto
                if (ventanaPrincipal != null) {
                    ventanaPrincipal.mostrarPanel("LOGIN");
                }
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar la cuenta. Por favor, intente nuevamente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}