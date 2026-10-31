package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.ValidationException;
import uce.edu.ec.correo.util.VisorDocumentos;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Clase DialogoDetalleCorreo
 *
 * Esta clase representa un diálogo modal que muestra los detalles completos
 * de un correo electrónico. Permite visualizar:
 * - Información del correo (remitente, destinatarios, fecha, asunto)
 * - Contenido del mensaje
 * - Archivos adjuntos con opciones de visualización y descarga
 * - Acciones disponibles según el estado del correo (enviar, reenviar, eliminar)
 *
 * Extiende de JDialog para crear una ventana modal independiente.
 */
public class DialogoDetalleCorreo extends JDialog {
    // Referencia a la empresa de correo que contiene toda la información del sistema
    private EmpresaCorreo empresa;

    // Correo electrónico del cual se muestran los detalles
    private Correo correo;

    // Asunto original del correo (usado como identificador para operaciones)
    private String asuntoOriginal;

    // Referencia al panel de correos para poder actualizarlo después de operaciones
    private PanelCorreos panelCorreos;

    /**
     * Constructor de DialogoDetalleCorreo
     *
     * Inicializa el diálogo con la información del correo y configura
     * el interceptor de cierre de ventana.
     *
     * @param padre Ventana padre (JFrame principal)
     * @param empresa Referencia a la empresa de correo
     * @param correo Correo del cual se mostrarán los detalles
     * @param panelCorreos Panel de correos para actualizar después de operaciones
     */
    public DialogoDetalleCorreo(JFrame padre, EmpresaCorreo empresa, Correo correo, PanelCorreos panelCorreos) {
        // Llama al constructor de JDialog con título fijo y modo modal
        super(padre, "Detalle del Correo", true);

        this.empresa = empresa; // Guarda la referencia a la empresa
        this.correo = correo; // Guarda la referencia al correo
        this.asuntoOriginal = correo.getAsunto(); // Guarda el asunto original
        this.panelCorreos = panelCorreos; // Guarda la referencia al panel de correos

        inicializarComponentes(); // Crea la interfaz gráfica

        setSize(750, 700); // Establece el tamaño del diálogo
        setLocationRelativeTo(padre); // Centra el diálogo respecto a la ventana padre

        // ========== CONFIGURACIÓN DEL CIERRE DE VENTANA ==========
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // No cierra automáticamente
        // Agrega un listener para el evento de cierre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmarCierre(); // Llama al método de confirmación
            }
        });
    }

    /**
     * Método inicializarComponentes
     *
     * Crea y configura todos los componentes de la interfaz gráfica del diálogo.
     * Incluye información del correo, mensaje, archivos adjuntos y botones de acción.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL PRINCIPAL DE CONTENIDO ==========
        // Crea el panel principal que contendrá toda la información del correo
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS)); // Layout vertical
        panelContenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding
        panelContenido.setBackground(Color.WHITE); // Fondo blanco

        // ========== DETERMINACIÓN DEL ESTADO A MOSTRAR ==========
        String emailUsuario = empresa.getUsuarioActual().getEmail(); // Obtiene email del usuario actual
        String estadoMostrar; // Variable para el estado que se mostrará

        // Lógica para determinar qué estado mostrar
        if (correo.getEstado() == EstadoCorreo.ELIMINADO) {
            // Si el correo está eliminado, muestra "Eliminado"
            estadoMostrar = "Eliminado";
        } else if (!correo.getRemitente().equalsIgnoreCase(emailUsuario) &&
                (correo.getDestinatarios().contains(emailUsuario) ||
                        correo.getCc().contains(emailUsuario) ||
                        correo.getCco().contains(emailUsuario))) {
            // Si el usuario no es el remitente pero está en los destinatarios, muestra "Recibido"
            estadoMostrar = "Recibido";
        } else {
            // En otros casos, muestra la descripción del estado del correo
            estadoMostrar = correo.getEstado().getDescripcion();
        }

        // ========== TÍTULO DEL DIÁLOGO ==========
        JLabel lblTitulo = new JLabel("Detalle del Correo");
        lblTitulo.setIcon(IconoUtils.iconoCorreo(24)); // Icono de correo
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20)); // Fuente grande y en negrita
        lblTitulo.setForeground(new Color(0, 102, 204)); // Color azul
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
        lblTitulo.setIconTextGap(10); // Espacio entre icono y texto
        panelContenido.add(lblTitulo); // Agrega el título al panel
        panelContenido.add(Box.createVerticalStrut(10)); // Agrega espacio vertical

        // ========== SEPARADOR DECORATIVO ==========
        JSeparator sep = new JSeparator(); // Línea separadora horizontal
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); // Ancho completo, alto de 1px
        sep.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
        panelContenido.add(sep); // Agrega el separador
        panelContenido.add(Box.createVerticalStrut(15)); // Agrega espacio vertical

        // ========== CAMPOS DE INFORMACIÓN DEL CORREO ==========

        // Campo "De:" (Remitente)
        agregarCampo(panelContenido, "De:", correo.getRemitente());

        // Campo "Para:" (Destinatarios)
        String destinatarios = correo.getDestinatarios().isEmpty()
                ? "-" // Si no hay destinatarios, muestra "-"
                : String.join(", ", correo.getDestinatarios()); // Une emails con comas
        agregarCampo(panelContenido, "Para:", destinatarios);

        // Campo "CC:" (Con Copia)
        String cc = correo.getCc().isEmpty()
                ? "-" // Si no hay CC, muestra "-"
                : String.join(", ", correo.getCc()); // Une emails con comas
        agregarCampo(panelContenido, "CC:", cc);

        // Campo "CCO:" (Con Copia Oculta)
        // Solo el remitente puede ver los destinatarios CCO
        String cco = correo.getRemitente().equalsIgnoreCase(emailUsuario)
                ? (correo.getCco().isEmpty() ? "-" : String.join(", ", correo.getCco()))
                : "(Oculto)"; // Para los demás, se muestra como "(Oculto)"
        agregarCampo(panelContenido, "CCO:", cco);

        // Campo "Fecha:" (Fecha de envío formateada)
        agregarCampo(panelContenido, "Fecha:", correo.getFechaEnvioFormateada());

        // Campo "Estado:" (Estado determinado anteriormente)
        agregarCampo(panelContenido, "Estado:", estadoMostrar);

        // Campo "Asunto:"
        agregarCampo(panelContenido, "Asunto:", correo.getAsunto());

        // ========== PANEL DEL MENSAJE ==========
        JPanel panelMensaje = new JPanel(new BorderLayout(5, 5));
        panelMensaje.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
        panelMensaje.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Altura máxima de 200px

        // Label "Mensaje:"
        JLabel lblMensaje = new JLabel("Mensaje:");
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        panelMensaje.add(lblMensaje, BorderLayout.NORTH); // Agrega al norte del panel

        // Área de texto para el contenido del mensaje
        JTextArea txtMensaje = new JTextArea(correo.getCuerpo());
        txtMensaje.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente normal
        txtMensaje.setEditable(false); // No editable (solo lectura)
        txtMensaje.setLineWrap(true); // Ajuste de línea automático
        txtMensaje.setWrapStyleWord(true); // Ajuste por palabras completas
        txtMensaje.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Borde gris
        txtMensaje.setBackground(new Color(250, 250, 250)); // Fondo gris muy claro

        // Scroll pane para el área de texto del mensaje
        JScrollPane scrollMensaje = new JScrollPane(txtMensaje);
        scrollMensaje.setPreferredSize(new Dimension(680, 150)); // Tamaño preferido
        panelMensaje.add(scrollMensaje, BorderLayout.CENTER); // Agrega al centro

        panelContenido.add(panelMensaje); // Agrega el panel de mensaje al contenido principal
        panelContenido.add(Box.createVerticalStrut(15)); // Agrega espacio vertical

        // ========== PANEL DE ARCHIVOS ADJUNTOS ==========
        JPanel panelAdjuntos = new JPanel();
        panelAdjuntos.setLayout(new BoxLayout(panelAdjuntos, BoxLayout.Y_AXIS)); // Layout vertical
        panelAdjuntos.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
        panelAdjuntos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250)); // Altura máxima
        // Borde con título "Archivos Adjuntos"
        panelAdjuntos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Archivos Adjuntos",
                0, // Posición del título
                0, // Justificación del título
                new Font("Arial", Font.BOLD, 14), // Fuente del título
                new Color(0, 102, 204) // Color del título
        ));

        // Verifica si hay archivos adjuntos
        if (correo.getArchivosAdjuntos().isEmpty()) {
            // Si no hay adjuntos, muestra un mensaje
            JLabel lblSinAdjuntos = new JLabel("  (Sin archivos adjuntos)");
            lblSinAdjuntos.setFont(new Font("Arial", Font.ITALIC, 13)); // Fuente en cursiva
            lblSinAdjuntos.setForeground(Color.GRAY); // Color gris
            panelAdjuntos.add(lblSinAdjuntos); // Agrega el mensaje
        } else {
            // Si hay adjuntos, crea un panel para cada uno
            for (ArchivoAdjunto adjunto : correo.getArchivosAdjuntos()) {
                // Panel para cada archivo adjunto
                JPanel panelArchivo = new JPanel(new BorderLayout(10, 5));
                panelArchivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Altura máxima
                panelArchivo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding
                panelArchivo.setBackground(Color.WHITE); // Fondo blanco

                // ========== INFORMACIÓN DEL ARCHIVO CON ICONO ==========
                ImageIcon iconoArchivo = obtenerIconoArchivo(adjunto); // Obtiene icono según tipo
                // Label con nombre y tamaño del archivo
                JLabel lblInfo = new JLabel(adjunto.getNombreArchivo() + " (" + adjunto.getTamanoFormateado() + ")");
                lblInfo.setIcon(iconoArchivo); // Establece el icono
                lblInfo.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente normal
                lblInfo.setIconTextGap(8); // Espacio entre icono y texto
                panelArchivo.add(lblInfo, BorderLayout.CENTER); // Agrega al centro

                // ========== BOTONES PARA EL ARCHIVO ==========
                JPanel panelBotonesAdj = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                panelBotonesAdj.setBackground(Color.WHITE); // Fondo blanco

                // Si es una imagen, agrega botón de visualización
                if (adjunto.getTipo() == ArchivoAdjunto.TipoArchivo.IMAGEN) {
                    // Botón "Ver" para visualizar imágenes
                    JButton btnVisualizar = new JButton("Ver", IconoUtils.iconoOjoVisible(16));
                    btnVisualizar.setFont(new Font("Arial", Font.PLAIN, 11)); // Fuente pequeña
                    btnVisualizar.setBackground(Color.WHITE); // Fondo blanco
                    btnVisualizar.setForeground(Color.BLACK); // Texto negro
                    btnVisualizar.setFocusPainted(false); // Sin borde de foco
                    btnVisualizar.setPreferredSize(new Dimension(80, 28)); // Tamaño del botón
                    // Borde compuesto: línea gris + padding
                    btnVisualizar.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                            BorderFactory.createEmptyBorder(3, 8, 3, 8)
                    ));
                    // Acción: visualizar el archivo
                    btnVisualizar.addActionListener(e -> VisorDocumentos.visualizar(this, adjunto));
                    panelBotonesAdj.add(btnVisualizar); // Agrega el botón
                }

                // Botón "Descargar" (siempre presente para todos los archivos)
                JButton btnDescargar = new JButton("Descargar", IconoUtils.iconoRecibidos(16));
                btnDescargar.setFont(new Font("Arial", Font.PLAIN, 11)); // Fuente pequeña
                btnDescargar.setBackground(Color.WHITE); // Fondo blanco
                btnDescargar.setForeground(Color.BLACK); // Texto negro
                btnDescargar.setFocusPainted(false); // Sin borde de foco
                btnDescargar.setPreferredSize(new Dimension(110, 28)); // Tamaño del botón
                // Borde compuesto: línea gris + padding
                btnDescargar.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(3, 8, 3, 8)
                ));
                // Acción: descargar el archivo (usa el mismo método visualizar)
                btnDescargar.addActionListener(e -> VisorDocumentos.visualizar(this, adjunto));
                panelBotonesAdj.add(btnDescargar); // Agrega el botón

                panelArchivo.add(panelBotonesAdj, BorderLayout.EAST); // Agrega botones a la derecha
                panelAdjuntos.add(panelArchivo); // Agrega el panel del archivo
            }
        }

        panelContenido.add(panelAdjuntos); // Agrega el panel de adjuntos al contenido principal

        // ========== SCROLL PRINCIPAL ==========
        // Envuelve todo el contenido en un scroll pane
        JScrollPane scrollPrincipal = new JScrollPane(panelContenido);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16); // Velocidad de scroll
        add(scrollPrincipal, BorderLayout.CENTER); // Agrega al centro del diálogo

        // ========== PANEL DE BOTONES DE ACCIÓN ==========
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(245, 245, 245)); // Fondo gris claro

        // Determina el estado del correo para mostrar botones apropiados
        boolean esBorrador = correo.getEstado() == EstadoCorreo.BORRADOR;
        boolean esEliminado = correo.getEstado() == EstadoCorreo.ELIMINADO;

        // ========== BOTONES SEGÚN EL ESTADO ==========

        if (esBorrador) {
            // Si es un borrador, muestra botón "Enviar Borrador"
            JButton btnEnviarBorrador = new JButton("Enviar Borrador", IconoUtils.iconoEnviar(18));
            btnEnviarBorrador.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
            btnEnviarBorrador.setBackground(Color.WHITE); // Fondo blanco
            btnEnviarBorrador.setForeground(Color.BLACK); // Texto negro
            btnEnviarBorrador.setFocusPainted(false); // Sin borde de foco
            btnEnviarBorrador.setPreferredSize(new Dimension(180, 38)); // Tamaño del botón
            // Borde compuesto: línea gris + padding
            btnEnviarBorrador.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btnEnviarBorrador.addActionListener(e -> enviarBorrador()); // Acción: enviar borrador
            panelBotones.add(btnEnviarBorrador); // Agrega el botón

        } else if (!esEliminado) {
            // Si NO es borrador y NO está eliminado, muestra botón "Reenviar"
            JButton btnReenviar = new JButton("Reenviar", IconoUtils.iconoReenviar(18));
            btnReenviar.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
            btnReenviar.setBackground(Color.WHITE); // Fondo blanco
            btnReenviar.setForeground(Color.BLACK); // Texto negro
            btnReenviar.setFocusPainted(false); // Sin borde de foco
            btnReenviar.setPreferredSize(new Dimension(140, 38)); // Tamaño del botón
            // Borde compuesto: línea gris + padding
            btnReenviar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btnReenviar.addActionListener(e -> reenviarCorreo()); // Acción: reenviar correo
            panelBotones.add(btnReenviar); // Agrega el botón
        }

        // Botón "Eliminar" (visible excepto para correos ya eliminados)
        if (!esEliminado) {
            JButton btnEliminar = new JButton("Eliminar", IconoUtils.iconoEliminar(18));
            btnEliminar.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
            btnEliminar.setBackground(Color.WHITE); // Fondo blanco
            btnEliminar.setForeground(Color.BLACK); // Texto negro
            btnEliminar.setFocusPainted(false); // Sin borde de foco
            btnEliminar.setPreferredSize(new Dimension(130, 38)); // Tamaño del botón
            // Borde compuesto: línea gris + padding
            btnEliminar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btnEliminar.addActionListener(e -> eliminarCorreo()); // Acción: eliminar correo
            panelBotones.add(btnEliminar); // Agrega el botón

        } else {
            // Para correos en papelera: botón "Eliminar Definitivamente"
            JButton btnEliminarDefinitivo = new JButton("Eliminar Definitivamente", IconoUtils.iconoEliminarX(18));
            btnEliminarDefinitivo.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
            btnEliminarDefinitivo.setBackground(Color.WHITE); // Fondo blanco
            btnEliminarDefinitivo.setForeground(Color.BLACK); // Texto negro
            btnEliminarDefinitivo.setFocusPainted(false); // Sin borde de foco
            btnEliminarDefinitivo.setPreferredSize(new Dimension(240, 38)); // Tamaño del botón
            // Borde compuesto: línea gris + padding
            btnEliminarDefinitivo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btnEliminarDefinitivo.addActionListener(e -> eliminarDefinitivamente()); // Acción: eliminar permanentemente
            panelBotones.add(btnEliminarDefinitivo); // Agrega el botón
        }

        // Botón "Cerrar" (siempre visible)
        JButton btnCerrar = new JButton("Cerrar", IconoUtils.iconoCerrar(18));
        btnCerrar.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        btnCerrar.setBackground(Color.WHITE); // Fondo blanco
        btnCerrar.setForeground(Color.BLACK); // Texto negro
        btnCerrar.setFocusPainted(false); // Sin borde de foco
        btnCerrar.setPreferredSize(new Dimension(110, 38)); // Tamaño del botón
        // Borde compuesto: línea gris + padding
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        btnCerrar.addActionListener(e -> confirmarCierre()); // Acción: confirmar antes de cerrar
        panelBotones.add(btnCerrar); // Agrega el botón

        add(panelBotones, BorderLayout.SOUTH); // Agrega el panel de botones al sur del diálogo
    }

    /**
     * Método obtenerIconoArchivo
     *
     * Retorna el icono apropiado según la extensión del archivo adjunto.
     *
     * @param adjunto El archivo adjunto
     * @return ImageIcon correspondiente al tipo de archivo
     */
    private ImageIcon obtenerIconoArchivo(ArchivoAdjunto adjunto) {
        String nombre = adjunto.getNombreArchivo().toLowerCase(); // Convierte a minúsculas

        // Verifica extensiones de imágenes
        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") ||
                nombre.endsWith(".png") || nombre.endsWith(".gif") || nombre.endsWith(".bmp")) {
            return IconoUtils.iconoOjoVisible(20); // Icono de ojo para imágenes
        }
        // Verifica extensiones de documentos (PDF, Word, texto)
        else if (nombre.endsWith(".pdf") || nombre.endsWith(".doc") ||
                nombre.endsWith(".docx") || nombre.endsWith(".txt")) {
            return IconoUtils.iconoBorradores(20); // Icono de documento
        }
        // Verifica extensiones de Excel
        else if (nombre.endsWith(".xls") || nombre.endsWith(".xlsx")) {
            return IconoUtils.iconoLista(20); // Icono de lista/tabla
        }
        // Para otros tipos de archivos
        else {
            return IconoUtils.iconoAdjuntar(20); // Icono genérico de adjunto
        }
    }

    /**
     * Método agregarCampo
     *
     * Crea y agrega un campo de información (etiqueta + valor) al panel.
     * Usado para mostrar información del correo como "De:", "Para:", etc.
     *
     * @param panel Panel al que se agregará el campo
     * @param etiqueta Texto de la etiqueta (ej: "De:")
     * @param valor Valor a mostrar (ej: email del remitente)
     */
    private void agregarCampo(JPanel panel, String etiqueta, String valor) {
        // Crea un panel para cada campo con FlowLayout alineado a la izquierda
        JPanel panelCampo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelCampo.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineación a la izquierda
        panelCampo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // Altura máxima
        panelCampo.setBackground(Color.WHITE); // Fondo blanco

        // Label para la etiqueta (ej: "De:")
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        lblEtiqueta.setPreferredSize(new Dimension(100, 25)); // Ancho fijo para alinear
        panelCampo.add(lblEtiqueta); // Agrega la etiqueta

        // Label para el valor
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente normal
        panelCampo.add(lblValor); // Agrega el valor

        panel.add(panelCampo); // Agrega el panel del campo al panel principal
    }
    /**
     * Método confirmarCierre
     *
     * Pide confirmación al usuario antes de cerrar el diálogo de detalle del correo.
     */
    private void confirmarCierre() {
        // Muestra diálogo de confirmación
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro/a que desea salir?",
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION, // Botones Sí/No
                JOptionPane.QUESTION_MESSAGE); // Icono de pregunta

        // Si el usuario confirma (Sí)
        if (opcion == JOptionPane.YES_OPTION) {
            dispose(); // Cierra el diálogo
        }
        // Si responde No, no hace nada (permanece abierto)
    }

    /**
     * Método enviarBorrador
     *
     * Permite enviar un correo que está en estado de borrador.
     * Solicita confirmación antes de realizar el envío.
     */
    private void enviarBorrador() {
        // Muestra diálogo de confirmación con el asunto del borrador
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea enviar este borrador?\n\nAsunto: " + correo.getAsunto(),
                "Confirmar envío",
                JOptionPane.YES_NO_OPTION, // Botones Sí/No
                JOptionPane.QUESTION_MESSAGE); // Icono de pregunta

        // Si el usuario confirma el envío
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Intenta enviar el borrador usando el asunto original como identificador
                if (empresa.enviarBorrador(asuntoOriginal)) {
                    // Si se envió exitosamente, muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            "✓ Borrador enviado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    panelCorreos.refrescar(); // Actualiza el panel de correos
                    dispose(); // Cierra el diálogo
                }
            } catch (ValidationException e) {
                // Si hay error al enviar, muestra el mensaje de error
                JOptionPane.showMessageDialog(this,
                        e.getMensaje(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si responde No, no hace nada (no envía)
    }

    /**
     * Método reenviarCorreo
     *
     * Abre el diálogo de reenvío de correo, permitiendo al usuario
     * enviar una copia del correo actual a nuevos destinatarios.
     */
    private void reenviarCorreo() {
        // Crea una nueva instancia del diálogo de reenvío
        DialogoReenviarCorreo dialogo = new DialogoReenviarCorreo(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre principal
                empresa, // Referencia a la empresa
                correo); // Correo a reenviar
        dialogo.setVisible(true); // Muestra el diálogo (modal)

        // Si el correo fue enviado exitosamente desde el diálogo de reenvío
        if (dialogo.isEnviado()) {
            panelCorreos.refrescar(); // Actualiza el panel de correos
            dispose(); // Cierra el diálogo actual
        }
    }

    /**
     * Método eliminarCorreo
     *
     * Mueve el correo a la papelera (eliminación lógica).
     * El correo cambia su estado a ELIMINADO pero no se borra definitivamente.
     * Solicita confirmación antes de realizar la operación.
     */
    private void eliminarCorreo() {
        // Muestra diálogo de confirmación con información del correo
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este correo?\n\n" +
                        "Asunto: " + correo.getAsunto() + "\n\n" +
                        "El correo se moverá a la papelera.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, // Botones Sí/No
                JOptionPane.WARNING_MESSAGE); // Icono de advertencia

        // Si el usuario confirma la eliminación
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Intenta eliminar el correo (moverlo a papelera)
                if (empresa.eliminarCorreo(asuntoOriginal)) {
                    // Si se eliminó exitosamente, muestra mensaje
                    JOptionPane.showMessageDialog(this,
                            "Correo movido a Eliminados",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    panelCorreos.refrescar(); // Actualiza el panel de correos
                    dispose(); // Cierra el diálogo
                }
            } catch (ValidationException e) {
                // Si hay error al eliminar, muestra el mensaje de error
                JOptionPane.showMessageDialog(this,
                        e.getMensaje(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si responde No, no hace nada (no elimina)
    }

    /**
     * Método eliminarDefinitivamente
     *
     * Elimina permanentemente el correo del sistema (eliminación física).
     * Esta acción es IRREVERSIBLE. El correo no se puede recuperar.
     * Muestra una advertencia fuerte antes de realizar la operación.
     */
    private void eliminarDefinitivamente() {
        // Muestra diálogo de confirmación con advertencia muy clara
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "⚠️ ADVERTENCIA ⚠️\n\n" +
                        "¿Está seguro que desea ELIMINAR DEFINITIVAMENTE este correo?\n\n" +
                        "Asunto: " + correo.getAsunto() + "\n\n" +
                        "Esta acción es IRREVERSIBLE y NO SE PUEDE DESHACER.",
                "Eliminar Definitivamente",
                JOptionPane.YES_NO_OPTION, // Botones Sí/No
                JOptionPane.WARNING_MESSAGE); // Icono de advertencia

        // Si el usuario confirma la eliminación definitiva
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Intenta eliminar el correo definitivamente del sistema
                if (empresa.eliminarCorreoDefinitivamente(asuntoOriginal)) {
                    // Si se eliminó permanentemente, muestra mensaje
                    JOptionPane.showMessageDialog(this,
                            "Correo eliminado permanentemente del sistema",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    panelCorreos.refrescar(); // Actualiza el panel de correos
                    dispose(); // Cierra el diálogo
                }
            } catch (ValidationException e) {
                // Si hay error al eliminar, muestra el mensaje de error
                JOptionPane.showMessageDialog(this,
                        e.getMensaje(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si responde No, no hace nada (no elimina)
    }
}