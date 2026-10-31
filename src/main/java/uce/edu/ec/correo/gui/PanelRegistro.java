package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.Usuario;
import uce.edu.ec.correo.dominio.Genero;
import uce.edu.ec.correo.util.IconoUtils;
import uce.edu.ec.correo.util.ValidationException;
import uce.edu.ec.correo.util.Validador;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Clase que representa el panel de registro de nuevos usuarios.
 * Contiene el formulario completo con todos los campos necesarios
 * para registrar un nuevo usuario en el sistema EcuMail.
 */
public class PanelRegistro extends JPanel {
    // Referencia a la ventana principal para poder cambiar entre paneles
    private VentanaPrincipal ventanaPrincipal;
    // Referencia a la empresa para registrar nuevos usuarios
    private EmpresaCorreo empresa;

    // Campos de texto del formulario
    private JTextField txtCedula; // Campo para ingresar la cédula ecuatoriana
    private JTextField txtProvincia; // Campo de solo lectura que muestra la provincia según la cédula
    private JTextField txtNombre; // Campo para el nombre del usuario
    private JTextField txtApellido; // Campo para el apellido del usuario
    private JTextField txtEmail; // Campo para el correo electrónico
    private JPasswordField txtPassword; // Campo oculto para la contraseña
    private JPasswordField txtConfirmarPassword; // Campo oculto para confirmar la contraseña
    private JToggleButton btnMostrarPassword1; // Botón para mostrar/ocultar la contraseña principal
    private JToggleButton btnMostrarPassword2; // Botón para mostrar/ocultar la confirmación de contraseña
    private JTextField txtTelefono; // Campo para el número de teléfono celular
    private JTextField txtFechaNacimiento; // Campo para la fecha de nacimiento (formato dd/mm/aaaa)
    private JComboBox<Genero> cmbGenero; // Lista desplegable para seleccionar el género
    private JButton btnRegistrar; // Botón para enviar el formulario de registro
    private JButton btnVolver; // Botón para volver al panel de login

    /**
     * Constructor del panel de registro.
     * @param ventanaPrincipal Referencia a la ventana principal
     * @param empresa Referencia a la empresa de correo
     */
    public PanelRegistro(VentanaPrincipal ventanaPrincipal, EmpresaCorreo empresa) {
        // Guarda la referencia a la ventana principal
        this.ventanaPrincipal = ventanaPrincipal;
        // Guarda la referencia a la empresa
        this.empresa = empresa;
        // Llama al método que crea todos los componentes visuales
        inicializarComponentes();
    }

    /**
     * Método que inicializa y configura todos los componentes del panel de registro.
     * Crea el formulario completo con todos sus campos, validaciones y botones.
     */
    private void inicializarComponentes() {
        // Establece el layout BorderLayout con espaciado de 10 píxeles
        setLayout(new BorderLayout(10, 10));
        // Establece el color de fondo del panel (azul claro)
        setBackground(new Color(240, 248, 255));

        // ===== PANEL DE TÍTULO =====
        // Crea un panel para el título
        JPanel panelTitulo = new JPanel();
        // Establece el color de fondo azul oscuro
        panelTitulo.setBackground(new Color(0, 102, 204));
        // Crea la etiqueta del título
        JLabel lblTitulo = new JLabel("Registro de Nuevo Usuario");
        // Establece la fuente del título (Arial, negrita, tamaño 24)
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        // Establece el color del texto en blanco
        lblTitulo.setForeground(Color.WHITE);
        // Agrega el título al panel
        panelTitulo.add(lblTitulo);
        // Agrega el panel de título en la parte superior (NORTH)
        add(panelTitulo, BorderLayout.NORTH);

        // ===== PANEL CENTRAL CON FORMULARIO CENTRADO =====
        // Crea un panel contenedor con GridBagLayout para centrar el formulario
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        // Establece el fondo blanco
        panelContenedor.setBackground(Color.WHITE);

        // Configura las restricciones para centrar el formulario
        GridBagConstraints gbcContenedor = new GridBagConstraints();
        gbcContenedor.gridx = 0; // Columna 0
        gbcContenedor.gridy = 0; // Fila 0
        gbcContenedor.weightx = 1.0; // Permite expansión horizontal
        gbcContenedor.weighty = 1.0; // Permite expansión vertical
        gbcContenedor.anchor = GridBagConstraints.CENTER; // Ancla al centro

        // Crea el panel que contendrá el formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        // Establece el fondo blanco
        panelFormulario.setBackground(Color.WHITE);
        // Agrega un borde compuesto (línea gris + márgenes internos)
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // Borde gris de 1 píxel
                BorderFactory.createEmptyBorder(30, 40, 30, 40) // Márgenes: arriba=30, izq=40, abajo=30, der=40
        ));

        // Configura las restricciones generales para los componentes del formulario
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Relleno horizontal
        gbc.anchor = GridBagConstraints.WEST; // Ancla a la izquierda

        // Variable para controlar la fila actual del formulario
        int fila = 0;

        // ===== CAMPO CÉDULA =====
        // Posiciona la etiqueta en la columna 0
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3; // 30% del ancho para etiquetas
        // Crea la etiqueta con asterisco indicando campo obligatorio
        JLabel lblCedula = new JLabel("Cédula: *");
        lblCedula.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblCedula, gbc);

        // Posiciona el campo de texto en la columna 1
        gbc.gridx = 1;
        gbc.weightx = 0.7; // 70% del ancho para campos de entrada
        // Crea el campo de texto con tamaño de 25 columnas
        txtCedula = new JTextField(25);
        txtCedula.setFont(new Font("Arial", Font.PLAIN, 14));
        // Agrega un KeyListener para validar entrada en tiempo real
        txtCedula.addKeyListener(new java.awt.event.KeyAdapter() {
            /**
             * Método que se ejecuta cuando se presiona una tecla.
             * Valida que solo se ingresen números y máximo 10 dígitos.
             */
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar(); // Obtiene el carácter presionado
                // Si no es un dígito y no es la tecla de retroceso
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume(); // Ignora la entrada
                }
                // Si ya hay 10 caracteres y no es retroceso
                if (txtCedula.getText().length() >= 10 && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume(); // Ignora la entrada
                }
            }

            /**
             * Método que se ejecuta cuando se suelta una tecla.
             * Actualiza automáticamente el campo de provincia.
             */
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // Actualiza la provincia basándose en la cédula ingresada
                actualizarProvincia();
            }
        });
        panelFormulario.add(txtCedula, gbc);

        // ===== CAMPO PROVINCIA (AUTOMÁTICO) =====
        fila++; // Incrementa la fila
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        // Crea la etiqueta de provincia (sin asterisco, no es obligatorio ingresarlo)
        JLabel lblProvinciaLabel = new JLabel("Provincia:");
        lblProvinciaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblProvinciaLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        // Crea el campo de provincia (solo lectura)
        txtProvincia = new JTextField(25);
        txtProvincia.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProvincia.setEditable(false); // No se puede editar
        txtProvincia.setBackground(new Color(245, 245, 245)); // Fondo gris claro
        txtProvincia.setForeground(new Color(100, 100, 100)); // Texto gris
        txtProvincia.setText("Ingrese su cédula"); // Texto placeholder
        panelFormulario.add(txtProvincia, gbc);

        // ===== CAMPO NOMBRE =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblNombre = new JLabel("Nombre: *");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblNombre, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtNombre = new JTextField(25);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 14));
        // Agrega validación para permitir solo letras y espacios
        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            /**
             * Método que valida que solo se ingresen letras y espacios.
             */
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                // Si no es letra, espacio o retroceso, ignora la entrada
                if (!Character.isLetter(c) && c != ' ' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
        panelFormulario.add(txtNombre, gbc);

        // ===== CAMPO APELLIDO =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblApellido = new JLabel("Apellido: *");
        lblApellido.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblApellido, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtApellido = new JTextField(25);
        txtApellido.setFont(new Font("Arial", Font.PLAIN, 14));
        // Agrega la misma validación que el nombre (solo letras y espacios)
        txtApellido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isLetter(c) && c != ' ' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
        panelFormulario.add(txtApellido, gbc);

        // ===== CAMPO EMAIL =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email: *");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblEmail, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtEmail = new JTextField(25);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));

        // VALIDACIÓN CRÍTICA: No permitir mayúsculas en el email
        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            /**
             * Método que valida la entrada del email.
             * Bloquea mayúsculas y caracteres no válidos para emails.
             */
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();

                // Si es una letra mayúscula, bloquearla
                if (Character.isUpperCase(c)) {
                    evt.consume(); // Ignora la entrada
                    // Feedback visual/auditivo - emite un sonido de error
                    Toolkit.getDefaultToolkit().beep();
                    return; // Sale del método
                }

                // Permitir: minúsculas, números, @, ., -, _, +, y teclas de control
                // Si el carácter no está en la lista de permitidos
                if (!Character.isLowerCase(c) &&
                        !Character.isDigit(c) &&
                        c != '@' &&
                        c != '.' &&
                        c != '-' &&
                        c != '_' &&
                        c != '+' &&
                        c != java.awt.event.KeyEvent.VK_BACK_SPACE &&
                        c != java.awt.event.KeyEvent.VK_DELETE) {
                    evt.consume(); // Ignora la entrada
                }
            }
        });

        panelFormulario.add(txtEmail, gbc);

        // ===== NOTA PARA EMAIL =====
        fila++;
        gbc.gridx = 1;
        gbc.gridy = fila;
        // Crea una etiqueta con texto en cursiva como ayuda
        JLabel lblNotaEmail = new JLabel("<html><i>Solo letras minúsculas permitidas</i></html>");
        lblNotaEmail.setFont(new Font("Arial", Font.PLAIN, 11));
        lblNotaEmail.setForeground(new Color(100, 100, 100)); // Texto gris
        panelFormulario.add(lblNotaEmail, gbc);

        // ===== CAMPO CONTRASEÑA =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblPassword = new JLabel("Contraseña: *");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblPassword, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        // Crea un panel para combinar el campo de contraseña con el botón de mostrar/ocultar
        JPanel panelPassword1 = new JPanel(new BorderLayout(5, 0));
        panelPassword1.setBackground(Color.WHITE);

        // Crea el campo de contraseña (oculta los caracteres)
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(180, 30));
        // Limita la contraseña a 14 caracteres
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                // Si ya hay 14 caracteres y no es retroceso
                if (txtPassword.getPassword().length >= 14 &&
                        evt.getKeyChar() != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume(); // Ignora la entrada
                }
            }
        });
        panelPassword1.add(txtPassword, BorderLayout.CENTER);

        // Crea el botón toggle para mostrar/ocultar contraseña
        btnMostrarPassword1 = new JToggleButton(IconoUtils.iconoOjoVisible(18));
        btnMostrarPassword1.setFocusPainted(false); // Quita el borde de foco
        btnMostrarPassword1.setPreferredSize(new Dimension(45, 30));
        btnMostrarPassword1.setToolTipText("Mostrar contraseña"); // Texto de ayuda
        btnMostrarPassword1.setBackground(Color.WHITE);
        btnMostrarPassword1.setBorderPainted(true);
        btnMostrarPassword1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor a manita
        // Agrega el comportamiento de mostrar/ocultar
        btnMostrarPassword1.addActionListener(e -> {
            if (btnMostrarPassword1.isSelected()) { // Si está seleccionado (presionado)
                txtPassword.setEchoChar((char) 0); // Muestra los caracteres reales
                btnMostrarPassword1.setIcon(IconoUtils.iconoOjoOculto(18)); // Cambia el ícono
                btnMostrarPassword1.setToolTipText("Ocultar contraseña");
            } else { // Si no está seleccionado
                txtPassword.setEchoChar('•'); // Oculta con puntos
                btnMostrarPassword1.setIcon(IconoUtils.iconoOjoVisible(18));
                btnMostrarPassword1.setToolTipText("Mostrar contraseña");
            }
        });
        panelPassword1.add(btnMostrarPassword1, BorderLayout.EAST);
        panelFormulario.add(panelPassword1, gbc);

        // ===== NOTA REQUISITOS DE CONTRASEÑA =====
        fila++;
        gbc.gridx = 1;
        gbc.gridy = fila;
        JLabel lblRequisitosPass = new JLabel("<html><i>8-14 caracteres, al menos 1 mayúscula</i></html>");
        lblRequisitosPass.setFont(new Font("Arial", Font.PLAIN, 11));
        lblRequisitosPass.setForeground(new Color(100, 100, 100));
        panelFormulario.add(lblRequisitosPass, gbc);

        // ===== CAMPO CONFIRMAR CONTRASEÑA =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblConfirmarPassword = new JLabel("Confirmar Contraseña: *");
        lblConfirmarPassword.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblConfirmarPassword, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        // Crea el panel para el segundo campo de contraseña
        JPanel panelPassword2 = new JPanel(new BorderLayout(5, 0));
        panelPassword2.setBackground(Color.WHITE);

        // Crea el campo de confirmación de contraseña
        txtConfirmarPassword = new JPasswordField(18);
        txtConfirmarPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtConfirmarPassword.setPreferredSize(new Dimension(180, 30));
        // Limita también a 14 caracteres
        txtConfirmarPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (txtConfirmarPassword.getPassword().length >= 14 &&
                        evt.getKeyChar() != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
        panelPassword2.add(txtConfirmarPassword, BorderLayout.CENTER);

        // Crea el segundo botón toggle para mostrar/ocultar
        btnMostrarPassword2 = new JToggleButton(IconoUtils.iconoOjoVisible(18));
        btnMostrarPassword2.setFocusPainted(false);
        btnMostrarPassword2.setPreferredSize(new Dimension(45, 30));
        btnMostrarPassword2.setToolTipText("Mostrar contraseña");
        btnMostrarPassword2.setBackground(Color.WHITE);
        btnMostrarPassword2.setBorderPainted(true);
        btnMostrarPassword2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Agrega la misma funcionalidad de mostrar/ocultar
        btnMostrarPassword2.addActionListener(e -> {
            if (btnMostrarPassword2.isSelected()) {
                txtConfirmarPassword.setEchoChar((char) 0);
                btnMostrarPassword2.setIcon(IconoUtils.iconoOjoOculto(18));
                btnMostrarPassword2.setToolTipText("Ocultar contraseña");
            } else {
                txtConfirmarPassword.setEchoChar('•');
                btnMostrarPassword2.setIcon(IconoUtils.iconoOjoVisible(18));
                btnMostrarPassword2.setToolTipText("Mostrar contraseña");
            }
        });
        panelPassword2.add(btnMostrarPassword2, BorderLayout.EAST);
        panelFormulario.add(panelPassword2, gbc);

        // ===== CAMPO TELÉFONO =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblTelefono = new JLabel("Teléfono: *");
        lblTelefono.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblTelefono, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtTelefono = new JTextField(25);
        txtTelefono.setFont(new Font("Arial", Font.PLAIN, 14));
        // Agrega validación específica para teléfonos ecuatorianos
        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            /**
             * Método que valida la entrada del teléfono.
             * En Ecuador, los celulares empiezan con 09 y tienen 10 dígitos.
             */
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                String textoActual = txtTelefono.getText();

                // Solo permite dígitos y retroceso
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                    return;
                }

                // El primer dígito debe ser 0
                if (textoActual.isEmpty() && c != '0' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                    return;
                }

                // El segundo dígito debe ser 9 (formato 09...)
                if (textoActual.length() == 1 && c != '9' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                    return;
                }

                // Limita a 10 dígitos
                if (textoActual.length() >= 10 && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
        panelFormulario.add(txtTelefono, gbc);

        // ===== CAMPO FECHA DE NACIMIENTO =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblFechaNac = new JLabel("Fecha de Nacimiento: *");
        lblFechaNac.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblFechaNac, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        // Crea un panel para el campo de fecha y su formato
        JPanel panelFecha = new JPanel(new BorderLayout(5, 0));
        panelFecha.setBackground(Color.WHITE);

        txtFechaNacimiento = new JTextField(15);
        txtFechaNacimiento.setFont(new Font("Arial", Font.PLAIN, 14));
        // Agrega validación y formateo automático de fecha
        txtFechaNacimiento.addKeyListener(new java.awt.event.KeyAdapter() {
            // Variable para controlar si se está eliminando texto
            private boolean eliminando = false;

            /**
             * Detecta cuando se presiona la tecla de retroceso.
             */
            public void keyPressed(java.awt.event.KeyEvent evt) {
                eliminando = (evt.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE);
            }

            /**
             * Método que formatea automáticamente la fecha con barras (/).
             * Formato: dd/mm/aaaa
             */
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (eliminando) return; // No formatea si se está borrando
                String texto = txtFechaNacimiento.getText().replace("/", ""); // Remueve barras existentes
                StringBuilder resultado = new StringBuilder();
                // Itera sobre los primeros 8 dígitos (ddmmaaaa)
                for (int i = 0; i < texto.length() && i < 8; i++) {
                    // Agrega barra después del día (posición 2) y mes (posición 4)
                    if (i == 2 || i == 4) resultado.append("/");
                    resultado.append(texto.charAt(i)); // Agrega el dígito
                }
                // Actualiza el campo con el texto formateado
                txtFechaNacimiento.setText(resultado.toString());
                // Mueve el cursor al final
                txtFechaNacimiento.setCaretPosition(txtFechaNacimiento.getText().length());
            }

            /**
             * Método que valida que solo se ingresen números.
             */
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                // Solo permite dígitos y retroceso
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                    return;
                }
                // Limita a 8 dígitos (sin contar las barras)
                String textoActual = txtFechaNacimiento.getText().replace("/", "");
                if (textoActual.length() >= 8 && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
        panelFecha.add(txtFechaNacimiento, BorderLayout.CENTER);

        // Agrega una etiqueta con el formato esperado
        JLabel lblFormatoFecha = new JLabel("(dd/mm/aaaa)");
        lblFormatoFecha.setFont(new Font("Arial", Font.ITALIC, 11));
        lblFormatoFecha.setForeground(Color.GRAY);
        panelFecha.add(lblFormatoFecha, BorderLayout.EAST);
        panelFormulario.add(panelFecha, gbc);

        // ===== CAMPO GÉNERO =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.3;
        JLabel lblGenero = new JLabel("Género: *");
        lblGenero.setFont(new Font("Arial", Font.BOLD, 14));
        panelFormulario.add(lblGenero, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        // Crea el JComboBox con los valores del enum Genero
        cmbGenero = new JComboBox<>(Genero.values());
        cmbGenero.setFont(new Font("Arial", Font.PLAIN, 14));
        // Inserta un elemento nulo en la posición 0 como opción por defecto
        cmbGenero.insertItemAt(null, 0);
        cmbGenero.setSelectedIndex(0); // Selecciona la opción nula
        // Personaliza cómo se muestra el elemento nulo
        cmbGenero.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                // Si el valor es nulo, muestra un texto placeholder
                if (value == null) {
                    setText("-- Seleccione un género --");
                    setForeground(Color.GRAY); // Texto en gris
                }
                return this;
            }
        });
        panelFormulario.add(cmbGenero, gbc);

        // ===== NOTA DE CAMPOS OBLIGATORIOS =====
        fila++;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2; // Ocupa 2 columnas
        gbc.insets = new Insets(15, 10, 10, 10); // Más espacio arriba
        JLabel lblNota = new JLabel("* Todos los campos son obligatorios");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 12));
        lblNota.setForeground(Color.GRAY);
        lblNota.setHorizontalAlignment(SwingConstants.CENTER); // Centrado
        panelFormulario.add(lblNota, gbc);

        // Agrega el formulario al contenedor principal
        panelContenedor.add(panelFormulario, gbcContenedor);

        // Envuelve el contenedor en un JScrollPane para permitir desplazamiento
        JScrollPane scrollPane = new JScrollPane(panelContenedor);
        scrollPane.setBorder(null); // Sin borde
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Velocidad de scroll
        add(scrollPane, BorderLayout.CENTER);

        // ===== PANEL DE BOTONES =====
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.setBackground(Color.WHITE);

        // Botón de Registrarse
        btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFocusPainted(false); // Sin borde de foco
        btnRegistrar.setPreferredSize(new Dimension(150, 40));
        // Asigna la acción de registrar usuario al botón
        btnRegistrar.addActionListener(e -> registrarUsuario());
        panelBotones.add(btnRegistrar);

        // Botón de Volver al Login
        btnVolver = new JButton("Volver al Login");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.setForeground(Color.BLACK);
        btnVolver.setFocusPainted(false);
        btnVolver.setPreferredSize(new Dimension(150, 40));
        // Asigna la acción de volver al login
        btnVolver.addActionListener(e -> volverAlLogin());
        panelBotones.add(btnVolver);

        // Agrega el panel de botones en la parte inferior
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Método que actualiza automáticamente el campo de provincia
     * según los dos primeros dígitos de la cédula ingresada.
     */
    private void actualizarProvincia() {
        // Obtiene el texto de la cédula sin espacios
        String cedula = txtCedula.getText().trim();

        // Si tiene al menos 2 caracteres
        if (cedula.length() >= 2) {
            // Obtiene la provincia correspondiente usando el validador
            String provincia = Validador.obtenerProvinciaPorCedula(cedula);
            txtProvincia.setText(provincia);

            // Si la provincia es desconocida (código inválido)
            if (provincia.equals("Desconocida")) {
                txtProvincia.setForeground(new Color(220, 53, 69)); // Rojo
            } else {
                txtProvincia.setForeground(new Color(40, 167, 69)); // Verde
            }
        } else {
            // Si no hay suficientes dígitos, muestra el placeholder
            txtProvincia.setText("Ingrese su cédula");
            txtProvincia.setForeground(new Color(100, 100, 100)); // Gris
        }
    }

    /**
     * Método que procesa el registro de un nuevo usuario.
     * Valida todos los campos y crea el usuario en el sistema.
     */
    private void registrarUsuario() {
        try {
            // ===== OBTENER DATOS DE LOS CAMPOS =====
            // Obtiene y limpia espacios de cada campo
            String cedula = txtCedula.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String email = txtEmail.getText().trim();
            // Convierte el array de chars del password a String
            String password = new String(txtPassword.getPassword());
            String confirmarPassword = new String(txtConfirmarPassword.getPassword());
            String telefono = txtTelefono.getText().trim();
            String fechaTexto = txtFechaNacimiento.getText().trim();
            // Obtiene el género seleccionado (puede ser null)
            Genero genero = (Genero) cmbGenero.getSelectedItem();

            // ===== VALIDAR CAMPOS VACÍOS CON MENSAJES ESPECÍFICOS =====
            // Cada validación muestra un mensaje específico y pone el foco en el campo

            if (cedula.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Cédula requerida\n\nDebe ingresar su número de cédula ecuatoriana.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtCedula.requestFocus(); // Pone el cursor en el campo
                return; // Sale del método
            }

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Nombre requerido\n\nDebe ingresar su nombre.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtNombre.requestFocus();
                return;
            }

            if (apellido.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Apellido requerido\n\nDebe ingresar su apellido.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtApellido.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Email requerido\n\nDebe ingresar su dirección de correo electrónico.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Contraseña requerida\n\nDebe ingresar una contraseña.\n\n" +
                                "Requisitos:\n" +
                                "• Entre 8 y 14 caracteres\n" +
                                "• Al menos una letra mayúscula",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtPassword.requestFocus();
                return;
            }

            if (confirmarPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Confirmación de contraseña requerida\n\nDebe confirmar su contraseña.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtConfirmarPassword.requestFocus();
                return;
            }

            // Valida que ambas contraseñas sean idénticas
            if (!password.equals(confirmarPassword)) {
                JOptionPane.showMessageDialog(this,
                        "❌ Las contraseñas no coinciden\n\n" +
                                "La contraseña y su confirmación deben ser idénticas.\n" +
                                "Por favor, verifique e intente nuevamente.",
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                // Limpia ambos campos de contraseña
                txtPassword.setText("");
                txtConfirmarPassword.setText("");
                txtPassword.requestFocus();
                return;
            }

            if (telefono.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Teléfono requerido\n\nDebe ingresar su número de teléfono celular.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtTelefono.requestFocus();
                return;
            }

            if (fechaTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Fecha de nacimiento requerida\n\nDebe ingresar su fecha de nacimiento.\n\n" +
                                "Formato: dd/mm/aaaa (ejemplo: 15/03/2000)",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtFechaNacimiento.requestFocus();
                return;
            }

            if (genero == null) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ Género requerido\n\nDebe seleccionar su género de la lista.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                cmbGenero.requestFocus();
                return;
            }

            // ===== VALIDAR FORMATO DE FECHA =====
            // Valida que la fecha tenga el formato correcto (dd/mm/aaaa)
            Validador.validarFormatoFecha(fechaTexto);

            // Intenta parsear la fecha
            LocalDate fechaNacimiento;
            try {
                // Crea un formateador para el patrón dd/MM/yyyy
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                // Convierte el String a LocalDate
                fechaNacimiento = LocalDate.parse(fechaTexto, formatter);
            } catch (DateTimeParseException e) {
                // Si falla el parseo, muestra un error
                JOptionPane.showMessageDialog(this,
                        "❌ Formato de fecha inválido\n\n" +
                                "Use el formato: dd/mm/aaaa\n" +
                                "Ejemplo: 15/03/2000",
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                txtFechaNacimiento.requestFocus();
                return;
            }

            // ===== CREAR USUARIO =====
            // Crea una nueva instancia de Usuario con todos los datos
            Usuario nuevoUsuario = new Usuario(cedula, nombre, apellido, email,
                    password, telefono, fechaNacimiento, genero);

            // Intenta registrar el usuario en la empresa
            if (empresa.registrarUsuario(nuevoUsuario)) {
                // Si el registro fue exitoso, muestra mensaje de confirmación
                JOptionPane.showMessageDialog(this,
                        "✓ Usuario registrado exitosamente\n\n¡Ahora puedes iniciar sesión!",
                        "Registro exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                // Limpia todos los campos del formulario
                limpiarCampos();
                // Obtiene referencia al panel de login
                PanelLogin panelLogin = ventanaPrincipal.getPanelLogin();
                // Agrega la cuenta registrada a la lista del panel de login
                panelLogin.agregarCuentaRegistrada(nombre + " " + apellido, email);
                // Cambia al panel de login
                ventanaPrincipal.mostrarPanel("LOGIN");
            }

        } catch (ValidationException e) {
            // Captura excepciones de validación personalizadas
            JOptionPane.showMessageDialog(this,
                    "❌ " + e.getMensaje(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);

            // Limpia campos con errores según el tipo de error
            String campo = e.getCampo();
            if ("Cédula".equals(campo)) {
                txtCedula.setText("");
                txtCedula.requestFocus();
            } else if ("Password".equals(campo)) {
                txtPassword.setText("");
                txtConfirmarPassword.setText("");
                txtPassword.requestFocus();
            } else if ("Email".equals(campo)) {
                txtEmail.setText("");
                txtEmail.requestFocus();
            }
        } catch (Exception e) {
            // Captura cualquier otra excepción no esperada
            JOptionPane.showMessageDialog(this,
                    "❌ Error al registrar usuario\n\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
        }
    }

    /**
     * Método que regresa al panel de login y limpia el formulario.
     */
    private void volverAlLogin() {
        // Limpia todos los campos
        limpiarCampos();
        // Cambia al panel de login
        ventanaPrincipal.mostrarPanel("LOGIN");
    }

    /**
     * Método que limpia todos los campos del formulario
     * y los restaura a su estado inicial.
     */
    private void limpiarCampos() {
        // Limpia los campos de texto
        txtCedula.setText("");
        txtProvincia.setText("Ingrese su cédula");
        txtProvincia.setForeground(new Color(100, 100, 100));
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirmarPassword.setText("");
        txtTelefono.setText("");
        txtFechaNacimiento.setText("");
        // Restaura el género a la opción por defecto (nulo)
        cmbGenero.setSelectedIndex(0);
        // Deselecciona los botones de mostrar contraseña
        btnMostrarPassword1.setSelected(false);
        btnMostrarPassword2.setSelected(false);
        // Oculta las contraseñas con puntos
        txtPassword.setEchoChar('•');
        txtConfirmarPassword.setEchoChar('•');
        // Restaura los íconos de ojo visible
        btnMostrarPassword1.setIcon(IconoUtils.iconoOjoVisible(18));
        btnMostrarPassword2.setIcon(IconoUtils.iconoOjoVisible(18));
        // Restaura los tooltips
        btnMostrarPassword1.setToolTipText("Mostrar contraseña");
        btnMostrarPassword2.setToolTipText("Mostrar contraseña");
    }
}