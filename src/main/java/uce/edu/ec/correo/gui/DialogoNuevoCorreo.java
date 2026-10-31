package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.ValidationException;
import uce.edu.ec.correo.util.Validador;
import uce.edu.ec.correo.util.VisorDocumentos;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Diálogo modal para redactar y enviar un nuevo correo electrónico.
 * Permite agregar destinatarios, asunto, cuerpo, archivos adjuntos y enviar o guardar como borrador.
 */
public class DialogoNuevoCorreo extends JDialog {
    // ========== CONSTANTES ==========

    // Tamaño máximo permitido para archivos adjuntos (10 MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // ========== ATRIBUTOS ==========

    // Referencia a la empresa para acceder a funcionalidades
    private EmpresaCorreo empresa;
    // Grupo seleccionado (si el correo es para un grupo)
    private Grupo grupoSeleccionado;
    // Indica si se guardó o envió el correo exitosamente
    private boolean guardado = false;
    // Lista de archivos adjuntos al correo
    private ArrayList<ArchivoAdjunto> archivosAdjuntos = new ArrayList<>();

    // ========== COMPONENTES DE LA INTERFAZ ==========

    // Campo de texto para el asunto del correo
    private JTextField txtAsunto;
    // Área de texto para el cuerpo del mensaje
    private JTextArea txtCuerpo;
    // Campo de texto para el destinatario principal
    private JTextField txtDestinatario;
    // Área de texto para destinatarios con copia (CC)
    private JTextArea txtCC;
    // Área de texto para destinatarios con copia oculta (CCO)
    private JTextArea txtCCO;
    // Panel que contiene la lista de archivos adjuntos
    private JPanel panelAdjuntos;

    // Botón para adjuntar archivos
    private JButton btnAdjuntarArchivo;
    // Botón para guardar como borrador
    private JButton btnGuardarBorrador;
    // Botón para enviar el correo
    private JButton btnEnviar;
    // Botón para cancelar y cerrar
    private JButton btnCancelar;

    /**
     * Constructor simple para crear un correo nuevo sin destinatario predefinido.
     * @param parent Ventana padre
     * @param empresa Instancia de EmpresaCorreo
     */
    public DialogoNuevoCorreo(JFrame parent, EmpresaCorreo empresa) {
        // Llama al constructor que acepta grupo (pasando null)
        this(parent, empresa, (Grupo) null);
    }

    /**
     * Constructor para crear un correo nuevo dirigido a un grupo.
     * @param parent Ventana padre
     * @param empresa Instancia de EmpresaCorreo
     * @param grupo Grupo al que se enviará el correo
     */
    public DialogoNuevoCorreo(JFrame parent, EmpresaCorreo empresa, Grupo grupo) {
        // Llama al constructor de JDialog con título y modal
        super(parent, "Nuevo Correo", true);
        // Almacena la referencia a la empresa
        this.empresa = empresa;
        // Almacena el grupo seleccionado
        this.grupoSeleccionado = grupo;
        // Inicializa los componentes gráficos
        inicializarComponentes();

        // Si hay un grupo seleccionado
        if (grupo != null) {
            // Carga los destinatarios del grupo
            cargarDestinatariosGrupo();
        }

        // Establece el tamaño del diálogo
        setSize(750, 750);
        // Centra el diálogo respecto a la ventana padre
        setLocationRelativeTo(parent);

        // Configura que no cierre automáticamente al hacer clic en X
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // Añade listener para manejar el evento de cierre de ventana
        addWindowListener(new WindowAdapter() {
            /**
             * Se ejecuta cuando el usuario intenta cerrar la ventana.
             */
            @Override
            public void windowClosing(WindowEvent e) {
                // Llama al método que confirma la salida
                confirmarSalida();
            }
        });
    }

    /**
     * Constructor para crear un correo con un destinatario específico.
     * @param parent Ventana padre
     * @param empresa Instancia de EmpresaCorreo
     * @param emailDestinatario Email del destinatario principal
     */
    public DialogoNuevoCorreo(JFrame parent, EmpresaCorreo empresa, String emailDestinatario) {
        // Llama al constructor de JDialog
        super(parent, "Nuevo Correo", true);
        // Almacena la referencia a la empresa
        this.empresa = empresa;
        // No hay grupo seleccionado
        this.grupoSeleccionado = null;
        // Inicializa los componentes gráficos
        inicializarComponentes();

        // Si se proporcionó un email y no está vacío
        if (emailDestinatario != null && !emailDestinatario.trim().isEmpty()) {
            // Establece el email en el campo de destinatario (sin espacios)
            txtDestinatario.setText(emailDestinatario.trim());
        }

        // Establece el tamaño del diálogo
        setSize(750, 750);
        // Centra el diálogo
        setLocationRelativeTo(parent);

        // Configura que no cierre automáticamente
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        // Añade listener para el evento de cierre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Confirma antes de cerrar
                confirmarSalida();
            }
        });
    }

    /**
     * Inicializa y configura todos los componentes gráficos del diálogo.
     */
    private void inicializarComponentes() {
        // Establece BorderLayout con espaciado
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL CENTRAL CON FORMULARIO ==========

        // Crea panel con GridBagLayout para organización flexible
        JPanel panelCentral = new JPanel(new GridBagLayout());
        // Añade borde vacío para márgenes
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Crea objeto para controlar el posicionamiento de componentes
        GridBagConstraints gbc = new GridBagConstraints();
        // Establece márgenes de 5 píxeles alrededor de cada componente
        gbc.insets = new Insets(5, 5, 5, 5);
        // Los componentes se expanden horizontalmente
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Alineación a la izquierda
        gbc.anchor = GridBagConstraints.WEST;

        // ========== CAMPO PARA (DESTINATARIO PRINCIPAL) ==========

        // Posiciona en columna 0, fila 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        // Crea label con asterisco indicando campo obligatorio
        JLabel lblPara = new JLabel("Para: *");
        // Establece fuente en negrita
        lblPara.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label al panel
        panelCentral.add(lblPara, gbc);

        // Posiciona en columna 1, fila 0
        gbc.gridx = 1;
        // Ocupa 2 columnas
        gbc.gridwidth = 2;
        // Permite expansión horizontal
        gbc.weightx = 1.0;
        // Crea campo de texto con 30 columnas
        txtDestinatario = new JTextField(30);
        // Establece fuente normal
        txtDestinatario.setFont(new Font("Arial", Font.PLAIN, 12));
        // Establece tamaño preferido
        txtDestinatario.setPreferredSize(new Dimension(350, 28));
        // Añade el campo al panel
        panelCentral.add(txtDestinatario, gbc);

        // ========== CAMPO CC (CON COPIA) ==========

        // Posiciona en columna 0, fila 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        // Ocupa 1 columna
        gbc.gridwidth = 1;
        // Sin expansión horizontal
        gbc.weightx = 0;
        // Relleno horizontal
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Crea label para CC
        JLabel lblCC = new JLabel("CC:");
        // Fuente en negrita
        lblCC.setFont(new Font("Arial", Font.BOLD, 14));
        // Tooltip explicativo
        lblCC.setToolTipText("Con Copia - Visible para todos (Opcional)");
        // Añade el label
        panelCentral.add(lblCC, gbc);

        // Posiciona en columna 1, fila 1
        gbc.gridx = 1;
        // Ocupa 2 columnas
        gbc.gridwidth = 2;
        // Permite expansión horizontal
        gbc.weightx = 1.0;
        // Relleno horizontal y vertical
        gbc.fill = GridBagConstraints.BOTH;
        // Crea área de texto con 2 filas y 30 columnas
        txtCC = new JTextArea(2, 30);
        // Establece fuente
        txtCC.setFont(new Font("Arial", Font.PLAIN, 12));
        // Activa ajuste de línea automático
        txtCC.setLineWrap(true);
        // Ajusta por palabras completas
        txtCC.setWrapStyleWord(true);
        // Envuelve en scroll pane
        JScrollPane scrollCC = new JScrollPane(txtCC);
        // Añade el scroll pane al panel
        panelCentral.add(scrollCC, gbc);

        // ========== CAMPO CCO (CON COPIA OCULTA) ==========

        // Posiciona en columna 0, fila 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        // Ocupa 1 columna
        gbc.gridwidth = 1;
        // Sin expansión
        gbc.weightx = 0;
        // Crea label para CCO
        JLabel lblCCO = new JLabel("CCO:");
        // Fuente en negrita
        lblCCO.setFont(new Font("Arial", Font.BOLD, 14));
        // Tooltip explicativo
        lblCCO.setToolTipText("Con Copia Oculta - Solo el remitente ve estos destinatarios (Opcional)");
        // Añade el label
        panelCentral.add(lblCCO, gbc);

        // Posiciona en columna 1, fila 2
        gbc.gridx = 1;
        // Ocupa 2 columnas
        gbc.gridwidth = 2;
        // Permite expansión
        gbc.weightx = 1.0;
        // Relleno horizontal y vertical
        gbc.fill = GridBagConstraints.BOTH;
        // Crea área de texto para CCO
        txtCCO = new JTextArea(2, 30);
        // Establece fuente
        txtCCO.setFont(new Font("Arial", Font.PLAIN, 12));
        // Activa ajuste de línea
        txtCCO.setLineWrap(true);
        // Ajusta por palabras
        txtCCO.setWrapStyleWord(true);
        // Envuelve en scroll pane
        JScrollPane scrollCCO = new JScrollPane(txtCCO);
        // Añade al panel
        panelCentral.add(scrollCCO, gbc);

        // ========== CAMPO ASUNTO ==========

        // Posiciona en columna 0, fila 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        // Ocupa 1 columna
        gbc.gridwidth = 1;
        // Sin expansión
        gbc.weightx = 0;
        // Relleno horizontal
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Crea label para asunto
        JLabel lblAsunto = new JLabel("Asunto:");
        // Fuente en negrita
        lblAsunto.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label
        panelCentral.add(lblAsunto, gbc);

        // Posiciona en columna 1, fila 3
        gbc.gridx = 1;
        // Ocupa 2 columnas
        gbc.gridwidth = 2;
        // Permite expansión
        gbc.weightx = 1.0;
        // Crea campo de texto para asunto
        txtAsunto = new JTextField(30);
        // Establece fuente más grande
        txtAsunto.setFont(new Font("Arial", Font.PLAIN, 14));
        // Añade al panel
        panelCentral.add(txtAsunto, gbc);

        // ========== CAMPO MENSAJE (CUERPO) ==========

        // Posiciona en columna 0, fila 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        // Ocupa 1 columna
        gbc.gridwidth = 1;
        // Sin expansión horizontal
        gbc.weightx = 0;
        // Alineación arriba-izquierda
        gbc.anchor = GridBagConstraints.NORTHWEST;
        // Crea label para mensaje
        JLabel lblCuerpo = new JLabel("Mensaje:");
        // Fuente en negrita
        lblCuerpo.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label
        panelCentral.add(lblCuerpo, gbc);

        // Posiciona en columna 1, fila 4
        gbc.gridx = 1;
        gbc.gridy = 4;
        // Ocupa 2 columnas
        gbc.gridwidth = 2;
        // Permite expansión horizontal
        gbc.weightx = 1.0;
        // Permite expansión vertical (50% del espacio disponible)
        gbc.weighty = 0.5;
        // Relleno horizontal y vertical
        gbc.fill = GridBagConstraints.BOTH;
        // Crea área de texto con 6 filas
        txtCuerpo = new JTextArea(6, 30);
        // Establece fuente
        txtCuerpo.setFont(new Font("Arial", Font.PLAIN, 14));
        // Activa ajuste de línea
        txtCuerpo.setLineWrap(true);
        // Ajusta por palabras
        txtCuerpo.setWrapStyleWord(true);
        // Envuelve en scroll pane
        JScrollPane scrollCuerpo = new JScrollPane(txtCuerpo);
        // Añade al panel
        panelCentral.add(scrollCuerpo, gbc);

        // ========== PANEL DE ARCHIVOS ADJUNTOS ==========

        // Posiciona en columna 0, fila 5
        gbc.gridx = 0;
        gbc.gridy = 5;
        // Ocupa 1 columna
        gbc.gridwidth = 1;
        // Sin expansión horizontal
        gbc.weightx = 0;
        // Sin expansión vertical
        gbc.weighty = 0;
        // Relleno horizontal
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Alineación arriba-izquierda
        gbc.anchor = GridBagConstraints.NORTHWEST;
        // Crea label para adjuntos
        JLabel lblAdjuntos = new JLabel("Adjuntos:");
        // Fuente en negrita
        lblAdjuntos.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label
        panelCentral.add(lblAdjuntos, gbc);

        // Posiciona en columna 1, fila 5
        gbc.gridx = 1;
        // Ocupa 2 columnas
        gbc.gridwidth = 2;
        // Permite expansión horizontal
        gbc.weightx = 1.0;
        // Expansión vertical (30% del espacio)
        gbc.weighty = 0.3;
        // Relleno horizontal y vertical
        gbc.fill = GridBagConstraints.BOTH;

        // Crea panel para mostrar archivos adjuntos
        panelAdjuntos = new JPanel();
        // Establece layout vertical (uno debajo de otro)
        panelAdjuntos.setLayout(new BoxLayout(panelAdjuntos, BoxLayout.Y_AXIS));
        // Fondo blanco
        panelAdjuntos.setBackground(Color.WHITE);

        // Envuelve el panel en scroll pane
        JScrollPane scrollAdjuntos = new JScrollPane(panelAdjuntos);
        // Establece altura preferida de 100 píxeles
        scrollAdjuntos.setPreferredSize(new Dimension(0, 100));
        // Añade borde gris claro
        scrollAdjuntos.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        // Añade al panel central
        panelCentral.add(scrollAdjuntos, gbc);

        // ========== BOTÓN ADJUNTAR ARCHIVO ==========

        // Posiciona en columna 0, fila 6
        gbc.gridx = 0;
        gbc.gridy = 6;
        // Ocupa 3 columnas
        gbc.gridwidth = 3;
        // Sin expansión vertical
        gbc.weighty = 0;
        // Sin relleno
        gbc.fill = GridBagConstraints.NONE;
        // Alineación a la izquierda
        gbc.anchor = GridBagConstraints.WEST;
        // Crea panel para botones de adjuntos
        JPanel panelBotonesAdjuntos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Crea botón con icono de clip
        btnAdjuntarArchivo = new JButton(IconoUtils.iconoAdjuntar(30));
        // Establece fuente
        btnAdjuntarArchivo.setFont(new Font("Arial", Font.PLAIN, 13));
        // Texto negro
        btnAdjuntarArchivo.setForeground(Color.BLACK);
        // Tooltip explicativo
        btnAdjuntarArchivo.setToolTipText("Adjuntar archivo");
        // Desactiva borde de enfoque
        btnAdjuntarArchivo.setFocusPainted(false);
        // Muestra el borde del botón
        btnAdjuntarArchivo.setBorderPainted(true);
        // Establece tamaño preferido
        btnAdjuntarArchivo.setPreferredSize(new Dimension(50, 32));
        // Cambia el cursor a mano al pasar sobre el botón
        btnAdjuntarArchivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Añade listener que ejecuta adjuntarArchivo()
        btnAdjuntarArchivo.addActionListener(e -> adjuntarArchivo());
        // Añade el botón al panel
        panelBotonesAdjuntos.add(btnAdjuntarArchivo);

        // Añade el panel de botones de adjuntos al panel central
        panelCentral.add(panelBotonesAdjuntos, gbc);
        // Añade el panel central al centro del diálogo
        add(panelCentral, BorderLayout.CENTER);

        // Crea panel con BorderLayout para organizar botones
        JPanel panelBotones = new JPanel(new BorderLayout(10, 10));
        // Fondo blanco
        panelBotones.setBackground(Color.WHITE);
        // Añade borde vacío para márgenes
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // ========== BOTONES DE LA IZQUIERDA ==========

        // Crea panel para botones del lado izquierdo
        JPanel panelIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        // Fondo blanco
        panelIzquierda.setBackground(Color.WHITE);

        // Crea botón Cancelar con icono de cerrar
        btnCancelar = new JButton("Cancelar", IconoUtils.iconoCerrar(18));
        // Establece fuente
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 13));
        // Texto negro
        btnCancelar.setForeground(Color.BLACK);
        // Desactiva borde de enfoque
        btnCancelar.setFocusPainted(false);
        // Establece tamaño preferido
        btnCancelar.setPreferredSize(new Dimension(130, 38));
        // Cursor de mano
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Añade listener que ejecuta confirmarSalida()
        btnCancelar.addActionListener(e -> confirmarSalida());
        // Añade el botón al panel izquierdo
        panelIzquierda.add(btnCancelar);

        // Crea botón Guardar Borrador con icono
        btnGuardarBorrador = new JButton("Guardar Borrador", IconoUtils.iconoBorradores(18));
        // Establece fuente
        btnGuardarBorrador.setFont(new Font("Arial", Font.PLAIN, 13));
        // Texto negro
        btnGuardarBorrador.setForeground(Color.BLACK);
        // Desactiva borde de enfoque
        btnGuardarBorrador.setFocusPainted(false);
        // Establece tamaño preferido
        btnGuardarBorrador.setPreferredSize(new Dimension(180, 38));
        // Cursor de mano
        btnGuardarBorrador.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Añade listener que ejecuta guardarBorrador()
        btnGuardarBorrador.addActionListener(e -> guardarBorrador());
        // Añade el botón al panel izquierdo
        panelIzquierda.add(btnGuardarBorrador);

        // ========== BOTONES DE LA DERECHA ==========

        // Crea panel para botones del lado derecho
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        // Fondo blanco
        panelDerecha.setBackground(Color.WHITE);

        // Crea botón Enviar con icono (sin texto visible)
        btnEnviar = new JButton( IconoUtils.iconoEnviar(20));
        // Establece fuente en negrita
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 13));
        // Texto negro
        btnEnviar.setForeground(Color.BLACK);
        // Desactiva borde de enfoque
        btnEnviar.setFocusPainted(false);
        // Establece tamaño preferido
        btnEnviar.setPreferredSize(new Dimension(40, 38));
        // Cursor de mano
        btnEnviar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Tooltip que indica "Enviar correo"
        btnEnviar.setToolTipText("Enviar correo");
        // Añade listener que ejecuta enviar()
        btnEnviar.addActionListener(e -> enviar());
        // Añade el botón al panel derecho
        panelDerecha.add(btnEnviar);

        // Añade panel izquierdo al oeste del panel de botones
        panelBotones.add(panelIzquierda, BorderLayout.WEST);
        // Añade panel derecho al este del panel de botones
        panelBotones.add(panelDerecha, BorderLayout.EAST);
        // Añade el panel de botones al sur del diálogo
        add(panelBotones, BorderLayout.SOUTH);

        // ========== LABEL DE NOTA INFORMATIVA ==========

        // Crea label con información sobre campos obligatorios
        JLabel lblNota = new JLabel("* Campo obligatorio | CC y CCO son opcionales | Separe con comas (,)");
        // Fuente itálica pequeña
        lblNota.setFont(new Font("Arial", Font.ITALIC, 11));
        // Color gris
        lblNota.setForeground(Color.GRAY);
        // Añade borde vacío para márgenes
        lblNota.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        // Añade el label al norte del diálogo
        add(lblNota, BorderLayout.NORTH);
    }

    /**
     * Actualiza el panel de archivos adjuntos mostrando todos los archivos agregados.
     */
    private void actualizarPanelAdjuntos() {
        // Elimina todos los componentes del panel
        panelAdjuntos.removeAll();

        // Itera sobre todos los archivos adjuntos
        for (int i = 0; i < archivosAdjuntos.size(); i++) {
            // Obtiene el archivo en el índice actual
            ArchivoAdjunto archivo = archivosAdjuntos.get(i);
            // Guarda el índice en variable final para usar en listeners
            final int index = i;

            // ========== CREA FILA PARA CADA ARCHIVO ==========

            // Crea panel con BorderLayout para la fila del archivo
            JPanel filaArchivo = new JPanel(new BorderLayout(10, 0));
            // Fondo blanco
            filaArchivo.setBackground(Color.WHITE);
            // Establece borde: línea inferior gris + márgenes internos
            filaArchivo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            // Establece altura máxima de 45 píxeles
            filaArchivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

            // Crea label con nombre y tamaño del archivo
            JLabel lblArchivo = new JLabel(archivo.getNombreArchivo() +
                    " (" + archivo.getTamanoFormateado() + ")");
            // Establece icono según el tipo de archivo
            lblArchivo.setIcon(IconoUtils.iconoParaArchivo(archivo, 16));
            // Establece fuente
            lblArchivo.setFont(new Font("Arial", Font.PLAIN, 12));
            // Añade el label al centro de la fila
            filaArchivo.add(lblArchivo, BorderLayout.CENTER);

            // Crea panel para los botones de acción
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            // Fondo blanco
            panelBotones.setBackground(Color.WHITE);

            // ========== BOTÓN OJO (SOLO PARA IMÁGENES) ==========

            // Si el archivo es una imagen
            if (archivo.getTipo() == ArchivoAdjunto.TipoArchivo.IMAGEN) {
                // Crea botón con icono de ojo visible
                JButton btnOjo = new JButton(IconoUtils.iconoOjoVisible(16));
                // Tooltip explicativo
                btnOjo.setToolTipText("Visualizar imagen");
                // Establece tamaño preferido
                btnOjo.setPreferredSize(new Dimension(32, 28));
                // Borde gris claro
                btnOjo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                // Desactiva borde de enfoque
                btnOjo.setFocusPainted(false);
                // Cursor de mano
                btnOjo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                // Añade listener para visualizar la imagen
                btnOjo.addActionListener(e -> {
                    // Llama al visor de documentos para mostrar la imagen
                    VisorDocumentos.visualizar(this, archivo);
                });
                // Añade el botón al panel de botones
                panelBotones.add(btnOjo);
            }

            // ========== BOTÓN ELIMINAR ==========

            // Crea botón con icono X para eliminar
            JButton btnEliminar = new JButton(IconoUtils.iconoEliminarX(16));
            // Tooltip explicativo
            btnEliminar.setToolTipText("Eliminar archivo");
            // Establece tamaño preferido
            btnEliminar.setPreferredSize(new Dimension(32, 28));
            // Borde gris claro
            btnEliminar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            // Desactiva borde de enfoque
            btnEliminar.setFocusPainted(false);
            // Cursor de mano
            btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // Añade listener para eliminar el archivo
            btnEliminar.addActionListener(e -> {
                // Muestra diálogo de confirmación
                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        "¿Eliminar el archivo: " + archivo.getNombreArchivo() + "?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                // Si el usuario confirmó
                if (confirmacion == JOptionPane.YES_OPTION) {
                    // Elimina el archivo de la lista
                    archivosAdjuntos.remove(index);
                    // Actualiza el panel de adjuntos
                    actualizarPanelAdjuntos();
                }
            });
            // Añade el botón al panel de botones
            panelBotones.add(btnEliminar);

            // Añade el panel de botones al este de la fila
            filaArchivo.add(panelBotones, BorderLayout.EAST);
            // Añade la fila completa al panel de adjuntos
            panelAdjuntos.add(filaArchivo);
        }

        // Revalida el panel (recalcula layout)
        panelAdjuntos.revalidate();
        // Repinta el panel
        panelAdjuntos.repaint();
    }

    /**
     * Carga los emails de los miembros del grupo en el campo destinatario.
     */
    private void cargarDestinatariosGrupo() {
        // Si hay grupo seleccionado y tiene miembros
        if (grupoSeleccionado != null && grupoSeleccionado.getCantidadMiembros() > 0) {
            // Establece el email del primer miembro en el campo destinatario
            txtDestinatario.setText(grupoSeleccionado.obtenerEmailsMiembros().get(0));
        }
    }

    /**
     * Abre un diálogo para seleccionar y adjuntar un archivo al correo.
     */
    private void adjuntarArchivo() {
        // Crea selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        // Establece título del diálogo
        fileChooser.setDialogTitle("Seleccionar Archivo");

        // ========== FILTROS DE TIPO DE ARCHIVO ==========

        // Crea filtro para imágenes
        FileNameExtensionFilter filterImagenes = new FileNameExtensionFilter(
                "Imágenes (JPG, JPEG, PNG, GIF, BMP)", "jpg", "jpeg", "png", "gif", "bmp");
        // Crea filtro para videos
        FileNameExtensionFilter filterVideos = new FileNameExtensionFilter(
                "Videos (MP4, AVI, MOV, WMV, FLV)", "mp4", "avi", "mov", "wmv", "flv");
        // Crea filtro para documentos
        FileNameExtensionFilter filterDocumentos = new FileNameExtensionFilter(
                "Documentos (PDF, DOC, DOCX, TXT, XLS, XLSX)", "pdf", "doc", "docx", "txt", "xls", "xlsx");
        // Crea filtro para todos los tipos soportados
        FileNameExtensionFilter filterTodos = new FileNameExtensionFilter(
                "Todos los archivos soportados",
                "jpg", "jpeg", "png", "gif", "bmp", "mp4", "avi", "mov", "wmv", "flv",
                "pdf", "doc", "docx", "txt", "xls", "xlsx");

        // Añade todos los filtros al selector
        fileChooser.addChoosableFileFilter(filterTodos);
        fileChooser.addChoosableFileFilter(filterImagenes);
        fileChooser.addChoosableFileFilter(filterVideos);
        fileChooser.addChoosableFileFilter(filterDocumentos);
        // Establece filtro "Todos" como predeterminado
        fileChooser.setFileFilter(filterTodos);

        // Muestra el diálogo de selección
        int resultado = fileChooser.showOpenDialog(this);
        // Si el usuario seleccionó un archivo
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Obtiene el archivo seleccionado
            File archivo = fileChooser.getSelectedFile();

            // Verifica si el archivo excede el tamaño máximo
            if (archivo.length() > MAX_FILE_SIZE) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "El archivo excede el límite de 10MB\nTamaño: " + formatearTamano(archivo.length()),
                        "Archivo demasiado grande",
                        JOptionPane.WARNING_MESSAGE);
                // Sale del método
                return;
            }

            try {
                // Crea stream de entrada para leer el archivo
                FileInputStream fis = new FileInputStream(archivo);
                // Crea array de bytes del tamaño del archivo
                byte[] contenido = new byte[(int) archivo.length()];
                // Lee todo el contenido del archivo
                fis.read(contenido);
                // Cierra el stream
                fis.close();

                // Crea objeto ArchivoAdjunto con los datos leídos
                ArchivoAdjunto adjunto = new ArchivoAdjunto(archivo.getName(), contenido, archivo.length());
                // Añade el archivo a la lista de adjuntos
                archivosAdjuntos.add(adjunto);
                // Actualiza el panel de adjuntos
                actualizarPanelAdjuntos();

                // Muestra mensaje de éxito
                JOptionPane.showMessageDialog(this,
                        "Archivo adjuntado correctamente\n" + archivo.getName(),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                // Si ocurre error al leer, muestra mensaje de error
                JOptionPane.showMessageDialog(this,
                        "Error al leer el archivo: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Formatea un tamaño en bytes a una representación legible.
     * @param bytes Tamaño en bytes
     * @return String formateado (ej: "1.5 MB")
     */
    private String formatearTamano(long bytes) {
        // Si es menor a 1 KB, muestra en bytes
        if (bytes < 1024) return bytes + " B";
        // Calcula el exponente (KB=1, MB=2, GB=3, etc.)
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        // Obtiene el prefijo correspondiente (K, M, G, T, P, E)
        String pre = "KMGTPE".charAt(exp - 1) + "";
        // Retorna el tamaño formateado con 1 decimal
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Crea un objeto Correo con los datos ingresados en el formulario.
     * @return Objeto Correo creado
     * @throws ValidationException Si hay errores de validación
     */
    private Correo crearCorreo() throws ValidationException {
        // Obtiene el asunto del campo de texto (sin espacios)
        String asunto = txtAsunto.getText().trim();
        // Obtiene el cuerpo del área de texto (sin espacios)
        String cuerpo = txtCuerpo.getText().trim();

        // Crea nuevo objeto Correo con asunto, cuerpo y remitente
        Correo correo = new Correo(asunto, cuerpo, empresa.getUsuarioActual().getEmail());

        // Añade todos los archivos adjuntos al correo
        for (ArchivoAdjunto archivo : archivosAdjuntos) {
            correo.agregarArchivoAdjunto(archivo);
        }

        // ========== PROCESAR DESTINATARIO PRINCIPAL (PARA) ==========

        // Obtiene el email del campo Para (sin espacios)
        String emailPara = txtDestinatario.getText().trim();
        // Si no está vacío
        if (!emailPara.isEmpty()) {
            // Valida el formato del email
            if (!Validador.validarEmail(emailPara)) {
                // Lanza excepción si es inválido
                throw new ValidationException("Email en campo Para es inválido: " + emailPara, "Para");
            }
            // Añade el destinatario al correo (en minúsculas)
            correo.agregarDestinatario(emailPara.toLowerCase());
        }

        // ========== PROCESAR DESTINATARIOS CC ==========

        // Divide el contenido del campo CC por comas
        String[] cc = txtCC.getText().split(",");
        // Itera sobre cada email en CC
        for (String email : cc) {
            // Elimina espacios
            email = email.trim();
            // Si no está vacío
            if (!email.isEmpty()) {
                // Valida el formato
                if (!Validador.validarEmail(email)) {
                    // Lanza excepción si es inválido
                    throw new ValidationException("Email inválido en CC: " + email, "CC");
                }
                // Añade el email a CC (en minúsculas)
                correo.agregarCC(email.toLowerCase());
            }
        }

        // ========== PROCESAR DESTINATARIOS CCO ==========

        // Divide el contenido del campo CCO por comas
        String[] cco = txtCCO.getText().split(",");
        // Itera sobre cada email en CCO
        for (String email : cco) {
            // Elimina espacios
            email = email.trim();
            // Si no está vacío
            if (!email.isEmpty()) {
                // Valida el formato
                if (!Validador.validarEmail(email)) {
                    // Lanza excepción si es inválido
                    throw new ValidationException("Email inválido en CCO: " + email, "CCO");
                }
                // Añade el email a CCO (en minúsculas)
                correo.agregarCCO(email.toLowerCase());
            }
        }

        // Retorna el correo creado
        return correo;
    }

    /**
     * Confirma la salida del diálogo si hay contenido sin guardar.
     */
    private void confirmarSalida() {
        // Obtiene el contenido de los campos (sin espacios)
        String emailPara = txtDestinatario.getText().trim();
        String asunto = txtAsunto.getText().trim();
        String cuerpo = txtCuerpo.getText().trim();

        // Verifica si hay algún contenido ingresado
        boolean hayContenido = !emailPara.isEmpty() || !asunto.isEmpty() ||
                !cuerpo.isEmpty() || !archivosAdjuntos.isEmpty();

        // Si hay contenido y no se ha guardado
        if (hayContenido && !guardado) {
            // Muestra diálogo de confirmación
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea salir?\n\nSe perderán los cambios no guardados.",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            // Si el usuario confirmó
            if (opcion == JOptionPane.YES_OPTION) {
                // Cierra el diálogo
                dispose();
            }
        } else {
            // Si no hay contenido o ya se guardó, cierra directamente
            dispose();
        }
    }

    /**
     * Guarda el correo como borrador.
     */
    private void guardarBorrador() {
        try {
            // Obtiene el email del campo Para (sin espacios)
            String emailPara = txtDestinatario.getText().trim();

            // Valida que el campo Para no esté vacío
            if (emailPara.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "El campo Para es obligatorio.\n\n" +
                                "Complete el campo Para para guardar el borrador.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                // Sale del método
                return;
            }

            // Obtiene asunto y cuerpo
            String asunto = txtAsunto.getText().trim();
            String cuerpo = txtCuerpo.getText().trim();

            // Valida que haya al menos un contenido
            if (asunto.isEmpty() && cuerpo.isEmpty() && archivosAdjuntos.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "El correo está vacío.\n\n" +
                                "Debe completar al menos uno de los siguientes:\n" +
                                "• Asunto\n" +
                                "• Mensaje\n" +
                                "• Adjuntos",
                        "Contenido requerido",
                        JOptionPane.WARNING_MESSAGE);
                // Sale del método
                return;
            }

            // Crea el objeto Correo
            Correo correo = crearCorreo();

            // Intenta guardar el correo
            if (empresa.guardarCorreo(correo)) {
                // Crea mensaje de éxito
                String mensaje = "Correo guardado como borrador";
                // Si hay archivos adjuntos, añade información
                if (!archivosAdjuntos.isEmpty()) {
                    mensaje += "\n\n" + archivosAdjuntos.size() + " archivo(s) adjunto(s)";
                }

                // Muestra mensaje de éxito
                JOptionPane.showMessageDialog(this,
                        mensaje,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                // Marca que se guardó exitosamente
                guardado = true;
                // Cierra el diálogo
                dispose();
            }

        } catch (ValidationException e) {
            // Si hay error de validación, muestra el mensaje
            JOptionPane.showMessageDialog(this,
                    e.getMensaje(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Envía el correo electrónico.
     */
    private void enviar() {
        try {
            // Obtiene los emails de los campos (sin espacios)
            String emailPara = txtDestinatario.getText().trim();
            String emailCC = txtCC.getText().trim();
            String emailCCO = txtCCO.getText().trim();

            // Valida que haya al menos un destinatario
            if (emailPara.isEmpty() && emailCC.isEmpty() && emailCCO.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "Debe ingresar al menos un destinatario.\n\n" +
                                "Complete el campo Para, CC o CCO para enviar el correo.",
                        "Destinatarios requeridos",
                        JOptionPane.WARNING_MESSAGE);
                // Sale del método
                return;
            }

            // Obtiene asunto y cuerpo
            String asunto = txtAsunto.getText().trim();
            String cuerpo = txtCuerpo.getText().trim();

            // Valida que haya contenido en el correo
            if (asunto.isEmpty() && cuerpo.isEmpty() && archivosAdjuntos.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "El correo está vacío.\n\n" +
                                "Debe completar al menos uno de los siguientes:\n" +
                                "• Asunto\n" +
                                "• Mensaje\n" +
                                "• Adjuntos",
                        "Contenido requerido",
                        JOptionPane.WARNING_MESSAGE);
                // Sale del método
                return;
            }

            // Crea el objeto Correo
            Correo correo = crearCorreo();

            // Verifica que el correo tenga destinatarios
            if (!correo.tieneDestinatarios()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "Debe ingresar al menos un destinatario.\n\n" +
                                "Complete el campo Para, CC o CCO para enviar el correo.",
                        "Destinatarios requeridos",
                        JOptionPane.WARNING_MESSAGE);
                // Sale del método
                return;
            }

            // Intenta guardar el correo
            if (empresa.guardarCorreo(correo)) {
                // Intenta enviar el correo
                if (empresa.enviarCorreo(correo.getAsunto())) {
                    // Crea mensaje de éxito
                    String mensajeExito = "Correo enviado exitosamente";
                    // Si hay archivos adjuntos, añade información
                    if (!archivosAdjuntos.isEmpty()) {
                        mensajeExito += "\nAdjuntos incluidos: " + archivosAdjuntos.size();
                    }

                    // Muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            mensajeExito,
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Marca que se guardó/envió exitosamente
                    guardado = true;
                    // Cierra el diálogo
                    dispose();
                }
            }

        } catch (ValidationException e) {
            // Si hay error de validación, muestra el mensaje
            JOptionPane.showMessageDialog(this,
                    e.getMensaje(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retorna si se guardó o envió el correo exitosamente.
     * @return true si se guardó/envió, false si no
     */
    public boolean isGuardado() {
        // Retorna el estado de guardado
        return guardado;
    }
}