package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.util.IconoUtils;
import uce.edu.ec.correo.util.ValidationException;
import javax.swing.*;
import java.awt.*;

/**
 * Panel de inicio de sesión que permite a los usuarios autenticarse en la aplicación.
 * Incluye validación de email en tiempo real y opción de mostrar/ocultar contraseña.
 * Controla intentos fallidos y cierra la aplicación después de 3 errores.
 */
public class PanelLogin extends JPanel {
    // ========== REFERENCIAS A COMPONENTES PRINCIPALES ==========

    // Referencia a la ventana principal
    private VentanaPrincipal ventanaPrincipal;
    // Referencia a la empresa para operaciones de autenticación
    private EmpresaCorreo empresa;

    // ========== COMPONENTES DE LA INTERFAZ ==========

    // Campo de texto para el email del usuario
    private JTextField txtEmail;
    // Campo de contraseña (oculta los caracteres por defecto)
    private JPasswordField txtPassword;
    // Botón toggle para mostrar/ocultar la contraseña
    private JToggleButton btnMostrarPassword;
    // Botón para iniciar sesión
    private JButton btnIniciarSesion;
    // Botón para ir al panel de registro
    private JButton btnRegistrarse;
    // Label para mostrar intentos restantes
    private JLabel lblIntentos;

    // ========== CONTROL DE INTENTOS FALLIDOS ==========

    // Contador de intentos fallidos de inicio de sesión
    private int intentosFallidos = 0;
    // Número máximo de intentos permitidos
    private static final int MAX_INTENTOS = 3;

    /**
     * Constructor del panel de login.
     * @param ventanaPrincipal Referencia a la ventana principal
     * @param empresa Instancia de EmpresaCorreo
     */
    public PanelLogin(VentanaPrincipal ventanaPrincipal, EmpresaCorreo empresa) {
        // Almacena la referencia a la ventana principal
        this.ventanaPrincipal = ventanaPrincipal;
        // Almacena la referencia a la empresa
        this.empresa = empresa;
        // Inicializa los componentes gráficos
        inicializarComponentes();
    }

    /**
     * Inicializa y configura todos los componentes del panel de login.
     */
    private void inicializarComponentes() {
        // Establece GridBagLayout para posicionamiento flexible
        setLayout(new GridBagLayout());
        // Color de fondo azul muy claro
        setBackground(new Color(240, 248, 255));

        // Crea objeto GridBagConstraints para controlar posicionamiento
        GridBagConstraints gbc = new GridBagConstraints();
        // Márgenes de 10 píxeles alrededor de cada componente
        gbc.insets = new Insets(10, 10, 10, 10);

        // ========== PANEL CENTRAL (FORMULARIO DE LOGIN) ==========

        // Crea panel central con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        // Fondo blanco
        panelCentral.setBackground(Color.WHITE);
        // Borde compuesto: línea gris + márgenes internos grandes
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Crea GridBagConstraints para los componentes internos
        GridBagConstraints gbcCentral = new GridBagConstraints();
        // Márgenes de 10 píxeles
        gbcCentral.insets = new Insets(10, 10, 10, 10);
        // Componentes se expanden horizontalmente
        gbcCentral.fill = GridBagConstraints.HORIZONTAL;

        // ========== LOGO ECUMAIL ==========

        // Posiciona en columna 0, fila 0
        gbcCentral.gridx = 0;
        gbcCentral.gridy = 0;
        // Ocupa 2 columnas
        gbcCentral.gridwidth = 2;

        // Crea el icono de EcuMail (imagen de 128x128)
        Image iconoEcuMail = IconoUtils.crearIconoEcuMail();
        // Escala la imagen a 100x100 píxeles con suavizado
        Image iconoEscalado = iconoEcuMail.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        // Crea label con el icono escalado
        JLabel lblLogo = new JLabel(new ImageIcon(iconoEscalado));
        // Centra el icono horizontalmente
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        // Añade el logo al panel
        panelCentral.add(lblLogo, gbcCentral);

        // ========== TÍTULO "ECUMAIL" ==========

        // Posiciona en fila 1
        gbcCentral.gridy = 1;
        // Márgenes: 5 arriba, 10 a los lados, 10 abajo
        gbcCentral.insets = new Insets(5, 10, 10, 10);
        // Crea label con el título
        JLabel lblTitulo = new JLabel("EcuMail");
        // Fuente grande y negrita
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));
        // Color azul
        lblTitulo.setForeground(new Color(0, 102, 204));
        // Centra el texto
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        // Añade el título al panel
        panelCentral.add(lblTitulo, gbcCentral);

        // ========== SUBTÍTULO ==========

        // Posiciona en fila 2
        gbcCentral.gridy = 2;
        // Sin margen superior
        gbcCentral.insets = new Insets(0, 10, 10, 10);
        // Crea label con el subtítulo
        JLabel lblSubtitulo = new JLabel("Correo Electrónico Ecuatoriano");
        // Fuente normal
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        // Color gris
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        // Centra el texto
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        // Añade el subtítulo al panel
        panelCentral.add(lblSubtitulo, gbcCentral);

        // ========== SEPARADOR VISUAL ==========

        // Posiciona en fila 3
        gbcCentral.gridy = 3;
        // Márgenes: 15 arriba, 10 a los lados, 20 abajo
        gbcCentral.insets = new Insets(15, 10, 20, 10);
        // Añade una línea horizontal separadora
        panelCentral.add(new JSeparator(), gbcCentral);

        // Restaura márgenes normales
        gbcCentral.insets = new Insets(10, 10, 10, 10);
        // Vuelve a ocupar 1 columna
        gbcCentral.gridwidth = 1;

        // ========== CAMPO EMAIL ==========

        // Posiciona en columna 0, fila 4
        gbcCentral.gridx = 0;
        gbcCentral.gridy = 4;
        // Alineación a la izquierda
        gbcCentral.anchor = GridBagConstraints.WEST;
        // Crea label para email
        JLabel lblEmail = new JLabel("Email:");
        // Fuente en negrita
        lblEmail.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label al panel
        panelCentral.add(lblEmail, gbcCentral);

        // Posiciona en columna 1, fila 4
        gbcCentral.gridx = 1;
        // Permite expansión horizontal
        gbcCentral.weightx = 1.0;
        // Crea campo de texto con 20 columnas
        txtEmail = new JTextField(20);
        // Establece fuente
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        // Establece tamaño preferido
        txtEmail.setPreferredSize(new Dimension(250, 35));

        // ========== VALIDACIÓN EN TIEMPO REAL DEL EMAIL ==========

        // Añade listener para validar cada tecla presionada
        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            /**
             * Se ejecuta cuando se presiona una tecla en el campo de email.
             */
            public void keyTyped(java.awt.event.KeyEvent evt) {
                // Obtiene el carácter presionado
                char c = evt.getKeyChar();

                // Si es una letra mayúscula
                if (Character.isUpperCase(c)) {
                    // Consume el evento (bloquea la tecla)
                    evt.consume();
                    // Emite un sonido de alerta
                    Toolkit.getDefaultToolkit().beep();
                    // Sale del método
                    return;
                }

                // Verifica si el carácter es válido para email
                // Permite: minúsculas, números, @, ., -, _, +, backspace, delete
                if (!Character.isLowerCase(c) &&           // No es minúscula
                        !Character.isDigit(c) &&            // No es número
                        c != '@' &&                         // No es arroba
                        c != '.' &&                         // No es punto
                        c != '-' &&                         // No es guion
                        c != '_' &&                         // No es guion bajo
                        c != '+' &&                         // No es más
                        c != java.awt.event.KeyEvent.VK_BACK_SPACE &&  // No es retroceso
                        c != java.awt.event.KeyEvent.VK_DELETE) {      // No es suprimir
                    // Consume el evento (bloquea caracteres no válidos)
                    evt.consume();
                }
            }
        });

        // Añade el campo de email al panel
        panelCentral.add(txtEmail, gbcCentral);

        // ========== CAMPO CONTRASEÑA CON BOTÓN MOSTRAR ==========

        // Posiciona en columna 0, fila 5
        gbcCentral.gridx = 0;
        gbcCentral.gridy = 5;
        // Sin expansión horizontal
        gbcCentral.weightx = 0;
        // Crea label para contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        // Fuente en negrita
        lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label al panel
        panelCentral.add(lblPassword, gbcCentral);

        // Posiciona en columna 1, fila 5
        gbcCentral.gridx = 1;
        // Permite expansión horizontal
        gbcCentral.weightx = 1.0;
        // Crea panel para contener el campo de contraseña y el botón
        JPanel panelPassword = new JPanel(new BorderLayout(5, 0));
        // Fondo blanco
        panelPassword.setBackground(Color.WHITE);

        // Crea campo de contraseña
        txtPassword = new JPasswordField(20);
        // Establece fuente
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        // Establece tamaño preferido
        txtPassword.setPreferredSize(new Dimension(200, 35));
        // Añade el campo al centro del panel
        panelPassword.add(txtPassword, BorderLayout.CENTER);

        // ========== BOTÓN MOSTRAR/OCULTAR CONTRASEÑA ==========

        // Crea botón toggle con icono de ojo visible
        btnMostrarPassword = new JToggleButton(IconoUtils.iconoOjoVisible(20));
        // Desactiva borde de enfoque
        btnMostrarPassword.setFocusPainted(false);
        // Establece tamaño preferido
        btnMostrarPassword.setPreferredSize(new Dimension(45, 35));
        // Tooltip inicial
        btnMostrarPassword.setToolTipText("Mostrar contraseña");
        // Fondo blanco
        btnMostrarPassword.setBackground(Color.WHITE);
        // Muestra el borde del botón
        btnMostrarPassword.setBorderPainted(true);
        // Cursor de mano
        btnMostrarPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Añade listener para alternar visibilidad de contraseña
        btnMostrarPassword.addActionListener(e -> {
            // Si el botón está presionado (seleccionado)
            if (btnMostrarPassword.isSelected()) {
                // Muestra la contraseña (sin carácter de eco)
                txtPassword.setEchoChar((char) 0);
                // Cambia el icono a ojo tachado
                btnMostrarPassword.setIcon(IconoUtils.iconoOjoOculto(20));
                // Actualiza el tooltip
                btnMostrarPassword.setToolTipText("Ocultar contraseña");
            } else {
                // Oculta la contraseña (carácter de punto)
                txtPassword.setEchoChar('•');
                // Cambia el icono a ojo visible
                btnMostrarPassword.setIcon(IconoUtils.iconoOjoVisible(20));
                // Actualiza el tooltip
                btnMostrarPassword.setToolTipText("Mostrar contraseña");
            }
        });

        // Añade el botón al este del panel de contraseña
        panelPassword.add(btnMostrarPassword, BorderLayout.EAST);

        // Añade el panel de contraseña al panel central
        panelCentral.add(panelPassword, gbcCentral);

        // ========== LABEL DE INTENTOS RESTANTES ==========

        // Posiciona en columna 0, fila 6
        gbcCentral.gridx = 0;
        gbcCentral.gridy = 6;
        // Ocupa 2 columnas
        gbcCentral.gridwidth = 2;
        // Márgenes reducidos
        gbcCentral.insets = new Insets(5, 10, 5, 10);
        // Crea label para mostrar intentos restantes
        lblIntentos = new JLabel(" ");
        // Fuente pequeña e itálica
        lblIntentos.setFont(new Font("Arial", Font.ITALIC, 11));
        // Color gris
        lblIntentos.setForeground(Color.GRAY);
        // Centra el texto
        lblIntentos.setHorizontalAlignment(SwingConstants.CENTER);
        // Añade el label al panel
        panelCentral.add(lblIntentos, gbcCentral);

        // ========== BOTÓN INICIAR SESIÓN ==========

        // Posiciona en columna 0, fila 7
        gbcCentral.gridx = 0;
        gbcCentral.gridy = 7;
        // Ocupa 2 columnas
        gbcCentral.gridwidth = 2;
        // Márgenes: 20 arriba, 10 a los lados y abajo
        gbcCentral.insets = new Insets(20, 10, 10, 10);
        // Crea botón de iniciar sesión
        btnIniciarSesion = new JButton("Iniciar Sesión");
        // Fuente grande y negrita
        btnIniciarSesion.setFont(new Font("Arial", Font.BOLD, 16));
        // Texto negro
        btnIniciarSesion.setForeground(Color.BLACK);
        // Desactiva borde de enfoque
        btnIniciarSesion.setFocusPainted(false);
        // Establece tamaño preferido
        btnIniciarSesion.setPreferredSize(new Dimension(250, 45));
        // Añade listener que ejecuta iniciarSesion()
        btnIniciarSesion.addActionListener(e -> iniciarSesion());
        // Añade el botón al panel
        panelCentral.add(btnIniciarSesion, gbcCentral);

        // ========== BOTÓN REGISTRARSE ==========

        // Posiciona en fila 8
        gbcCentral.gridy = 8;
        // Márgenes normales
        gbcCentral.insets = new Insets(10, 10, 10, 10);
        // Crea botón para ir al registro
        btnRegistrarse = new JButton("¿No tienes cuenta? Regístrate");
        // Fuente negrita tamaño 13
        btnRegistrarse.setFont(new Font("Arial", Font.BOLD, 13));
        // Color azul para el texto
        btnRegistrarse.setForeground(new Color(0, 102, 204));
        // Fondo blanco
        btnRegistrarse.setBackground(Color.WHITE);
        // Sin borde
        btnRegistrarse.setBorderPainted(false);
        // Desactiva borde de enfoque
        btnRegistrarse.setFocusPainted(false);
        // Cursor de mano
        btnRegistrarse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Añade listener que ejecuta irARegistro()
        btnRegistrarse.addActionListener(e -> irARegistro());
        // Añade el botón al panel
        panelCentral.add(btnRegistrarse, gbcCentral);

        // ========== AÑADIR PANEL CENTRAL AL PANEL PRINCIPAL ==========

        // Posiciona en columna 0, fila 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        // Añade el panel central al panel principal (centrado)
        add(panelCentral, gbc);

        // ========== CONFIGURAR ENTER PARA LOGIN ==========

        // Añade listener al campo de contraseña para permitir login con Enter
        txtPassword.addActionListener(e -> iniciarSesion());
    }

    /**
     * Intenta iniciar sesión con las credenciales ingresadas.
     * Controla los intentos fallidos y cierra la aplicación después de 3 errores.
     */
    private void iniciarSesion() {
        // Obtiene el email del campo de texto (sin espacios)
        String email = txtEmail.getText().trim();
        // Obtiene la contraseña del campo (convierte char[] a String)
        String password = new String(txtPassword.getPassword());

        // Valida que los campos no estén vacíos
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Por favor, complete todos los campos",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Intenta iniciar sesión en la empresa
            if (empresa.iniciarSesion(email, password)) {
                // Si el inicio de sesión es exitoso, resetea el contador
                intentosFallidos = 0;
                actualizarLabelIntentos();
                // Limpia los campos
                limpiarCampos();
                // Muestra el panel del menú principal
                ventanaPrincipal.mostrarPanel("MENU_PRINCIPAL");
            }
        } catch (ValidationException e) {
            // Incrementa el contador de intentos fallidos
            intentosFallidos++;

            // Determina el tipo de error según el mensaje
            String mensaje = e.getMensaje();
            String tipoError;

            if (mensaje.contains("no está registrado") || mensaje.contains("no existe")) {
                tipoError = "Usuario no encontrado";
            } else if (mensaje.contains("contraseña") || mensaje.contains("incorrecta")) {
                tipoError = "Contraseña incorrecta";
            } else {
                tipoError = "Error de autenticación";
            }

            // Actualiza el label de intentos
            actualizarLabelIntentos();

            // Muestra mensaje de error con contador de intentos
            JOptionPane.showMessageDialog(this,
                    "❌ " + tipoError + "\n\n" + mensaje + "\n\n" +
                            "Intento " + intentosFallidos + " de " + MAX_INTENTOS,
                    "Error de inicio de sesión",
                    JOptionPane.ERROR_MESSAGE);

            // Limpia ambos campos
            limpiarCampos();

            // Verifica si se alcanzó el máximo de intentos
            verificarMaximoIntentos();
        }
    }

    /**
     * Actualiza el label que muestra los intentos restantes.
     */
    private void actualizarLabelIntentos() {
        if (intentosFallidos > 0) {
            int intentosRestantes = MAX_INTENTOS - intentosFallidos;
            lblIntentos.setText("Intentos restantes: " + intentosRestantes);

            // Cambia el color según los intentos restantes
            if (intentosRestantes == 1) {
                // Rojo si solo queda 1 intento
                lblIntentos.setForeground(Color.RED);
            } else if (intentosRestantes == 2) {
                // Naranja si quedan 2 intentos
                lblIntentos.setForeground(new Color(255, 140, 0));
            } else {
                // Gris para otros casos
                lblIntentos.setForeground(Color.GRAY);
            }
        } else {
            // Si no hay intentos fallidos, limpia el label
            lblIntentos.setText(" ");
            lblIntentos.setForeground(Color.GRAY);
        }
    }

    /**
     * Verifica si se alcanzó el máximo de intentos y cierra la aplicación.
     */
    private void verificarMaximoIntentos() {
        if (intentosFallidos >= MAX_INTENTOS) {
            // Muestra mensaje de cierre
            JOptionPane.showMessageDialog(this,
                    "🔒 Máximo de intentos alcanzado\n\n" +
                            "Ha superado el número máximo de intentos de inicio de sesión (" + MAX_INTENTOS + ").\n" +
                            "La aplicación se cerrará por seguridad.",
                    "Seguridad",
                    JOptionPane.ERROR_MESSAGE);

            // Cierra la aplicación
            System.exit(0);
        }
    }

    /**
     * Navega al panel de registro.
     */
    private void irARegistro() {
        // Limpia los campos del formulario
        limpiarCampos();
        // Resetea el contador de intentos al cambiar de pantalla
        intentosFallidos = 0;
        actualizarLabelIntentos();
        // Muestra el panel de registro
        ventanaPrincipal.mostrarPanel("REGISTRO");
    }

    /**
     * Limpia todos los campos del formulario y resetea el estado del botón de mostrar contraseña.
     */
    private void limpiarCampos() {
        // Limpia el campo de email
        txtEmail.setText("");
        // Limpia el campo de contraseña
        txtPassword.setText("");
        // Deselecciona el botón toggle (vuelve a estado no presionado)
        btnMostrarPassword.setSelected(false);
        // Oculta la contraseña con puntos
        txtPassword.setEchoChar('•');
        // Restaura el icono de ojo visible
        btnMostrarPassword.setIcon(IconoUtils.iconoOjoVisible(20));
        // Restaura el tooltip original
        btnMostrarPassword.setToolTipText("Mostrar contraseña");
        // Pone el foco en el campo de email
        txtEmail.requestFocus();
    }

    /**
     * Resetea el contador de intentos.
     * Útil cuando se vuelve a esta pantalla desde otras secciones.
     */
    public void resetearIntentos() {
        intentosFallidos = 0;
        actualizarLabelIntentos();
    }

    // ========== MÉTODOS DE COMPATIBILIDAD (SIN FUNCIONALIDAD) ==========

    /**
     * Método vacío para compatibilidad con otras clases.
     * Ya no se usa pero se mantiene para evitar errores en código legacy.
     * @param nombre Nombre de la cuenta (no utilizado)
     * @param email Email de la cuenta (no utilizado)
     */
    public void agregarCuentaRegistrada(String nombre, String email) {
        // No se usa, pero se mantiene para evitar errores
    }
}