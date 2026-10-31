package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.ValidationException;
import uce.edu.ec.correo.util.Validador;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase que representa el diálogo para reenviar un correo existente.
 * Permite al usuario reenviar un correo previamente enviado o recibido
 * a un nuevo destinatario, manteniendo el contenido y adjuntos originales.
 */
public class DialogoReenviarCorreo extends JDialog {
    // Referencia a la empresa para acceder a contactos y operaciones de correo
    private EmpresaCorreo empresa;
    // Correo que se va a reenviar
    private Correo correoOriginal;
    // Asunto del correo original
    private String asuntoOriginal;
    // Bandera que indica si el correo fue reenviado exitosamente
    private boolean reenviado = false;

    // Componentes de la interfaz para mostrar información del correo original
    private JLabel lblDeValor; // Muestra el remitente original
    private JLabel lblAsuntoValor; // Muestra el asunto original
    private JLabel lblFechaValor; // Muestra la fecha de envío original
    private JTextArea txtCuerpoOriginal; // Muestra el cuerpo del correo original
    private JTextField txtNuevoDestinatario; // Campo para ingresar el nuevo destinatario
    private JButton btnAgregarContacto; // Botón para seleccionar desde contactos
    private JButton btnReenviar; // Botón para confirmar el reenvío
    private JButton btnCancelar; // Botón para cancelar la operación

    /**
     * Constructor del diálogo de reenviar correo.
     * @param parent Ventana padre del diálogo
     * @param empresa Referencia a la empresa de correo
     * @param correoOriginal Correo que se desea reenviar
     */
    public DialogoReenviarCorreo(JFrame parent, EmpresaCorreo empresa, Correo correoOriginal) {
        // Llama al constructor de JDialog con título y modo modal
        super(parent, "Reenviar Correo", true);
        // Guarda la referencia a la empresa
        this.empresa = empresa;
        // Guarda la referencia al correo original
        this.correoOriginal = correoOriginal;
        // Guarda el asunto del correo original
        this.asuntoOriginal = correoOriginal.getAsunto();
        // Inicializa todos los componentes visuales
        inicializarComponentes();
        // Carga los datos del correo original en los campos
        cargarDatos();

        // Establece el tamaño del diálogo
        setSize(700, 600);
        // Centra el diálogo respecto a la ventana padre
        setLocationRelativeTo(parent);
    }

    /**
     * Método que inicializa y configura todos los componentes visuales del diálogo.
     * Crea tres secciones: información del correo original, campo para nuevo destinatario y botones.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado de 10 píxeles
        setLayout(new BorderLayout(10, 10));

        // ===== PANEL SUPERIOR - INFORMACIÓN DEL CORREO ORIGINAL =====
        // Crea el panel con GridBagLayout para mejor control de posicionamiento
        JPanel panelInfo = new JPanel(new GridBagLayout());
        // Agrega un borde con título descriptivo
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Correo Original"));
        // Establece color de fondo azul claro
        panelInfo.setBackground(new Color(240, 248, 255));

        // Configura las restricciones del GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Relleno horizontal
        gbc.anchor = GridBagConstraints.WEST; // Ancla a la izquierda

        // ===== CAMPO "DE" (REMITENTE) =====
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.weightx = 0.3; // 30% del ancho para etiquetas
        // Crea la etiqueta "De:"
        JLabel lblDe = new JLabel("De:");
        lblDe.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblDe, gbc);

        gbc.gridx = 1; // Columna 1
        gbc.weightx = 0.7; // 70% del ancho para valores
        // Crea la etiqueta que mostrará el remitente
        lblDeValor = new JLabel();
        lblDeValor.setFont(new Font("Arial", Font.PLAIN, 14));
        panelInfo.add(lblDeValor, gbc);

        // ===== CAMPO "ASUNTO" =====
        gbc.gridx = 0;
        gbc.gridy = 1; // Fila 1
        gbc.weightx = 0.3;
        JLabel lblAsunto = new JLabel("Asunto:");
        lblAsunto.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblAsunto, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        // Crea la etiqueta que mostrará el asunto
        lblAsuntoValor = new JLabel();
        lblAsuntoValor.setFont(new Font("Arial", Font.PLAIN, 14));
        panelInfo.add(lblAsuntoValor, gbc);

        // ===== CAMPO "FECHA" =====
        gbc.gridx = 0;
        gbc.gridy = 2; // Fila 2
        gbc.weightx = 0.3;
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblFecha, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        // Crea la etiqueta que mostrará la fecha
        lblFechaValor = new JLabel();
        lblFechaValor.setFont(new Font("Arial", Font.PLAIN, 14));
        panelInfo.add(lblFechaValor, gbc);

        // ===== CAMPO "MENSAJE ORIGINAL" =====
        gbc.gridx = 0;
        gbc.gridy = 3; // Fila 3
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Ancla arriba-izquierda para textarea
        JLabel lblMensaje = new JLabel("Mensaje:");
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblMensaje, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0; // Permite expansión vertical
        gbc.fill = GridBagConstraints.BOTH; // Relleno en ambas direcciones
        // Crea el área de texto para mostrar el mensaje original
        txtCuerpoOriginal = new JTextArea(8, 30); // 8 filas, 30 columnas
        txtCuerpoOriginal.setFont(new Font("Arial", Font.PLAIN, 13));
        txtCuerpoOriginal.setLineWrap(true); // Ajuste automático de línea
        txtCuerpoOriginal.setWrapStyleWord(true); // Ajusta por palabras completas
        txtCuerpoOriginal.setEditable(false); // Solo lectura
        txtCuerpoOriginal.setBackground(new Color(245, 245, 245)); // Fondo gris claro
        // Envuelve el área de texto en un JScrollPane para permitir desplazamiento
        JScrollPane scrollCuerpo = new JScrollPane(txtCuerpoOriginal);
        scrollCuerpo.setPreferredSize(new Dimension(0, 200)); // Altura preferida de 200 píxeles
        panelInfo.add(scrollCuerpo, gbc);

        // Agrega el panel de información en el centro del diálogo
        add(panelInfo, BorderLayout.CENTER);

        // ===== PANEL INFERIOR - NUEVO DESTINATARIO =====
        // Crea el panel para ingresar el nuevo destinatario
        JPanel panelReenvio = new JPanel(new GridBagLayout());
        // Agrega un borde con título
        panelReenvio.setBorder(BorderFactory.createTitledBorder("Reenviar a:"));
        panelReenvio.setBackground(Color.WHITE);

        // Configura las restricciones para este panel
        GridBagConstraints gbcReenvio = new GridBagConstraints();
        gbcReenvio.insets = new Insets(10, 10, 10, 10);
        gbcReenvio.fill = GridBagConstraints.HORIZONTAL;
        gbcReenvio.anchor = GridBagConstraints.WEST;

        // Etiqueta "Destinatario:"
        gbcReenvio.gridx = 0;
        gbcReenvio.gridy = 0;
        JLabel lblNuevoDestinatario = new JLabel("Destinatario: *");
        lblNuevoDestinatario.setFont(new Font("Arial", Font.BOLD, 14));
        panelReenvio.add(lblNuevoDestinatario, gbcReenvio);

        // Campo de texto para el nuevo destinatario
        gbcReenvio.gridx = 1;
        gbcReenvio.weightx = 1.0; // Permite expansión horizontal
        txtNuevoDestinatario = new JTextField(25);
        txtNuevoDestinatario.setFont(new Font("Arial", Font.PLAIN, 14));
        panelReenvio.add(txtNuevoDestinatario, gbcReenvio);

        // Botón "Contactos" para seleccionar desde la lista
        gbcReenvio.gridx = 2;
        gbcReenvio.weightx = 0; // No se expande
        btnAgregarContacto = new JButton("Contactos");
        btnAgregarContacto.setFont(new Font("Arial", Font.PLAIN, 12));
        btnAgregarContacto.setForeground(Color.BLACK);
        btnAgregarContacto.setFocusPainted(false); // Sin borde de foco
        // Asigna la acción de seleccionar contacto
        btnAgregarContacto.addActionListener(e -> seleccionarContacto());
        panelReenvio.add(btnAgregarContacto, gbcReenvio);

        // Agrega el panel de reenvío en la parte superior del diálogo
        add(panelReenvio, BorderLayout.NORTH);

        // ===== PANEL DE BOTONES =====
        // Crea el panel para los botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE);

        // Botón "Reenviar"
        btnReenviar = new JButton("Reenviar");
        btnReenviar.setFont(new Font("Arial", Font.BOLD, 15));
        btnReenviar.setBackground(new Color(40, 167, 69)); // Verde
        btnReenviar.setForeground(Color.BLACK);
        btnReenviar.setFocusPainted(false);
        btnReenviar.setPreferredSize(new Dimension(140, 40));
        // Asigna la acción de reenviar
        btnReenviar.addActionListener(e -> reenviar());
        panelBotones.add(btnReenviar);

        // Botón "Cancelar"
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(108, 117, 125)); // Gris
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(120, 40));
        // Cierra el diálogo al cancelar
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        // Agrega el panel de botones en la parte inferior del diálogo
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Método que carga los datos del correo original en los campos de visualización.
     * Muestra el remitente, asunto, fecha y cuerpo del mensaje.
     */
    private void cargarDatos() {
        // Verifica que el correo original no sea nulo
        if (correoOriginal != null) {
            // Establece el remitente en la etiqueta
            lblDeValor.setText(correoOriginal.getRemitente());
            // Establece el asunto
            lblAsuntoValor.setText(correoOriginal.getAsunto());
            // Establece la fecha formateada
            lblFechaValor.setText(correoOriginal.getFechaEnvioFormateada());
            // Establece el cuerpo del mensaje
            txtCuerpoOriginal.setText(correoOriginal.getCuerpo());
            // Mueve el cursor al inicio del texto
            txtCuerpoOriginal.setCaretPosition(0);
        }
    }

    /**
     * Método que muestra un diálogo para seleccionar un contacto de la lista.
     * El email del contacto seleccionado se coloca automáticamente en el campo de destinatario.
     */
    private void seleccionarContacto() {
        // Obtiene la lista de todos los contactos de la empresa
        ArrayList<Contacto> contactos = empresa.listarContactos();

        // Verifica si hay contactos disponibles
        if (contactos.isEmpty()) {
            // Muestra mensaje informativo si no hay contactos
            JOptionPane.showMessageDialog(this,
                    "No hay contactos disponibles",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return; // Sale del método
        }

        // Crea un array de strings con la información de cada contacto
        String[] opciones = new String[contactos.size()];
        for (int i = 0; i < contactos.size(); i++) {
            // Formato: "email@example.com - Nombre del Contacto"
            opciones[i] = contactos.get(i).getEmail() + " - " + contactos.get(i).getNombre();
        }

        // Muestra un diálogo de selección
        String seleccion = (String) JOptionPane.showInputDialog(this,
                "Seleccione el contacto:",
                "Seleccionar Contacto",
                JOptionPane.PLAIN_MESSAGE,
                null, // Sin ícono personalizado
                opciones, // Array de opciones
                opciones[0]); // Selección por defecto (primer contacto)

        // Si el usuario seleccionó algo (no canceló)
        if (seleccion != null) {
            // Obtiene el índice de la selección en el array
            int index = java.util.Arrays.asList(opciones).indexOf(seleccion);
            // Extrae el email del contacto seleccionado
            String email = contactos.get(index).getEmail();
            // Coloca el email en el campo de texto
            txtNuevoDestinatario.setText(email);
        }
    }

    /**
     * Método que procesa el reenvío del correo.
     * Valida el destinatario y ejecuta la operación de reenvío.
     */
    private void reenviar() {
        try {
            // Obtiene y limpia el email del nuevo destinatario
            String nuevoDestinatario = txtNuevoDestinatario.getText().trim();

            // Valida que el campo no esté vacío
            if (nuevoDestinatario.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debe ingresar un destinatario",
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE);
                return; // Sale del método
            }

            // SOLO VALIDAR FORMATO - NO VALIDAR SI EXISTE EN EL SISTEMA
            // Valida que el email tenga un formato correcto
            if (!Validador.validarEmail(nuevoDestinatario)) {
                // Lanza excepción si el formato es inválido
                throw new ValidationException("Email del destinatario inválido", "Destinatario");
            }

            // Muestra un diálogo de confirmación
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea reenviar este correo a:\n\n" + nuevoDestinatario + "?",
                    "Confirmar Reenvío",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // Si el usuario confirma
            if (confirmacion == JOptionPane.YES_OPTION) {
                // Intenta reenviar el correo usando el método de la empresa
                if (empresa.reenviarCorreo(asuntoOriginal, nuevoDestinatario)) {
                    // Construye el mensaje de éxito
                    String mensaje = "✓ Correo reenviado exitosamente a:\n" + nuevoDestinatario;

                    // Si el correo tiene adjuntos, agrega información adicional
                    if (correoOriginal.tieneAdjuntos()) {
                        mensaje += "\n\n📎 Se incluyeron " + correoOriginal.getCantidadAdjuntos() +
                                " archivo(s) adjunto(s)";
                    }

                    // Muestra el mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            mensaje,
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Marca el reenvío como exitoso
                    reenviado = true;
                    // Cierra el diálogo
                    dispose();
                }
            }

        } catch (ValidationException e) {
            // Captura excepciones de validación
            JOptionPane.showMessageDialog(this,
                    e.getMensaje(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método getter que indica si el correo fue enviado exitosamente.
     * @return true si el correo fue reenviado, false en caso contrario
     */
    public boolean isEnviado() {
        return reenviado;
    }

}