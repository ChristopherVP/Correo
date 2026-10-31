package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.VisorDocumentos;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase DialogoArchivosChat
 *
 * Esta clase representa un diálogo modal que muestra todos los archivos
 * que han sido compartidos en un chat grupal. Permite visualizar, descargar
 * y gestionar los archivos adjuntos de los mensajes del grupo.
 *
 * Extiende de JDialog para crear una ventana modal independiente.
 */
public class DialogoArchivosChat extends JDialog {
    // Referencia a la empresa de correo que contiene la información del sistema
    private EmpresaCorreo empresa;

    // Referencia al grupo del cual se mostrarán los archivos
    private Grupo grupo;

    // Lista que almacena información de archivos junto con metadatos (remitente, fecha)
    private ArrayList<InfoArchivo> archivosConInfo;

    // Componentes de la interfaz gráfica
    private JTable tablaArchivos; // Tabla que muestra la lista de archivos
    private DefaultTableModel modeloTabla; // Modelo de datos para la tabla
    private JButton btnVisualizarGuardar; // Botón para ver/descargar archivos
    private JButton btnCerrar; // Botón para cerrar el diálogo
    private JButton btnActualizar; // Botón para refrescar la lista de archivos

    /**
     * Clase interna InfoArchivo
     *
     * Encapsula la información de un archivo adjunto junto con
     * metadatos adicionales como el remitente y la fecha de envío.
     * Esta clase facilita el manejo conjunto de estos datos relacionados.
     */
    private class InfoArchivo {
        ArchivoAdjunto archivo; // El archivo adjunto en sí
        String remitente; // Email del remitente
        String remitenteNombre; // Nombre legible del remitente
        String fecha; // Fecha de envío formateada

        /**
         * Constructor de InfoArchivo
         *
         * @param archivo El archivo adjunto
         * @param remitente Email del remitente
         * @param remitenteNombre Nombre del remitente para mostrar
         * @param fecha Fecha formateada de envío
         */
        InfoArchivo(ArchivoAdjunto archivo, String remitente, String remitenteNombre, String fecha) {
            this.archivo = archivo; // Asigna el archivo
            this.remitente = remitente; // Asigna el email del remitente
            this.remitenteNombre = remitenteNombre; // Asigna el nombre del remitente
            this.fecha = fecha; // Asigna la fecha
        }
    }

    /**
     * Constructor de DialogoArchivosChat
     *
     * Inicializa el diálogo con todos sus componentes y carga los archivos del grupo.
     *
     * @param parent Ventana padre (JFrame principal)
     * @param empresa Referencia a la empresa de correo
     * @param grupo Grupo del cual se mostrarán los archivos
     */
    public DialogoArchivosChat(JFrame parent, EmpresaCorreo empresa, Grupo grupo) {
        // Llama al constructor de JDialog con título dinámico y modo modal
        super(parent, "Archivos del Chat - " + grupo.getNombre(), true);

        this.empresa = empresa; // Guarda la referencia a la empresa
        this.grupo = grupo; // Guarda la referencia al grupo
        this.archivosConInfo = new ArrayList<>(); // Inicializa la lista de archivos

        recolectarArchivos(); // Recopila todos los archivos del grupo
        inicializarComponentes(); // Crea la interfaz gráfica
        cargarArchivos(); // Carga los archivos en la tabla

        setSize(900, 550); // Establece el tamaño del diálogo
        setLocationRelativeTo(parent); // Centra el diálogo respecto a la ventana padre
    }

    /**
     * Método recolectarArchivos
     *
     * Recorre todos los mensajes del grupo y extrae los archivos adjuntos,
     * creando objetos InfoArchivo con metadatos adicionales (remitente, fecha).
     */
    private void recolectarArchivos() {
        archivosConInfo.clear(); // Limpia la lista existente de archivos

        // Itera sobre todos los mensajes del grupo
        for (MensajeGrupo msg : grupo.getMensajes()) {
            String emailRemitente = msg.getRemitente(); // Obtiene el email del remitente
            String nombreRemitente = obtenerNombreRemitente(emailRemitente); // Obtiene nombre legible
            String fecha = msg.getFechaEnvioFormateada(); // Obtiene fecha formateada

            // Itera sobre todos los archivos adjuntos del mensaje
            for (ArchivoAdjunto adj : msg.getArchivosAdjuntos()) {
                // Crea un objeto InfoArchivo y lo agrega a la lista
                archivosConInfo.add(new InfoArchivo(adj, emailRemitente, nombreRemitente, fecha));
            }
        }
    }

    /**
     * Método obtenerNombreRemitente
     *
     * Determina el nombre a mostrar para un remitente dado su email.
     * Busca primero en el usuario actual, luego en contactos del grupo,
     * y finalmente en todos los contactos de la empresa.
     *
     * @param emailRemitente Email del remitente
     * @return Nombre a mostrar ("Tú", nombre del contacto, o el email)
     */
    private String obtenerNombreRemitente(String emailRemitente) {
        // Si el remitente es el usuario actual, retorna "Tú"
        if (empresa.getUsuarioActual().getEmail().equalsIgnoreCase(emailRemitente)) {
            return "Tú";
        }

        // Busca el contacto en la lista de miembros del grupo
        Contacto contactoGrupo = grupo.buscarContactoPorEmail(emailRemitente);
        // Si lo encuentra y tiene nombre, lo retorna
        if (contactoGrupo != null && contactoGrupo.getNombre() != null && !contactoGrupo.getNombre().isEmpty()) {
            return contactoGrupo.getNombre();
        }

        // Busca el contacto en la lista general de la empresa
        Contacto contacto = empresa.buscarContactoPorEmail(emailRemitente);
        // Si lo encuentra y tiene nombre, lo retorna
        if (contacto != null && contacto.getNombre() != null && !contacto.getNombre().isEmpty()) {
            return contacto.getNombre();
        }

        // Si no encuentra nombre, retorna el email
        return emailRemitente;
    }

    /**
     * Método inicializarComponentes
     *
     * Crea y configura todos los componentes de la interfaz gráfica del diálogo.
     * Incluye el panel superior, la tabla de archivos y el panel de botones.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL SUPERIOR ==========
        // Crea el panel superior que contendrá título e información
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(240, 248, 255)); // Color azul claro
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Márgenes

        // Panel izquierdo con título y contador de archivos
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelInfo.setBackground(new Color(240, 248, 255)); // Mismo color de fondo

        // Icono de archivos
        JLabel lblIconoArchivos = new JLabel(IconoUtils.iconoArchivosChat(24));
        panelInfo.add(lblIconoArchivos); // Agrega el icono al panel

        // Título "Archivos Compartidos"
        JLabel lblTitulo = new JLabel("Archivos Compartidos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16)); // Fuente en negrita
        lblTitulo.setForeground(new Color(0, 102, 204)); // Color azul
        panelInfo.add(lblTitulo); // Agrega el título

        // Contador de archivos entre paréntesis
        JLabel lblTotal = new JLabel("(" + archivosConInfo.size() + " archivo(s))");
        lblTotal.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente normal
        lblTotal.setForeground(Color.GRAY); // Color gris
        panelInfo.add(lblTotal); // Agrega el contador

        // Panel derecho con botón actualizar
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panelDerecha.setBackground(new Color(240, 248, 255)); // Mismo color de fondo

        // Botón Actualizar
        btnActualizar = new JButton("Actualizar", IconoUtils.iconoActualizar(16));
        btnActualizar.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
        btnActualizar.setBackground(Color.WHITE); // Fondo blanco
        btnActualizar.setForeground(Color.BLACK); // Texto negro
        btnActualizar.setFocusPainted(false); // Sin borde de foco
        btnActualizar.setPreferredSize(new Dimension(130, 32)); // Tamaño del botón
        btnActualizar.setHorizontalTextPosition(SwingConstants.RIGHT); // Texto a la derecha del icono
        btnActualizar.setIconTextGap(6); // Espacio entre icono y texto
        btnActualizar.addActionListener(e -> actualizarListaArchivos()); // Acción al hacer clic
        panelDerecha.add(btnActualizar); // Agrega el botón al panel

        // Agrega los paneles al panel superior
        panelSuperior.add(panelInfo, BorderLayout.WEST); // Panel izquierdo
        panelSuperior.add(panelDerecha, BorderLayout.EAST); // Panel derecho

        // Agrega el panel superior al diálogo
        add(panelSuperior, BorderLayout.NORTH);

        // ========== TABLA DE ARCHIVOS ==========
        // Define las columnas de la tabla
        String[] columnas = {"Nombre del Archivo", "Tipo", "Enviado por", "Fecha/Hora", "Tamaño"};

        // Crea el modelo de tabla con las columnas y 0 filas iniciales
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que las celdas no sean editables
            }
        };

        // Crea la tabla con el modelo
        tablaArchivos = new JTable(modeloTabla);
        tablaArchivos.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente de la tabla
        tablaArchivos.setRowHeight(36); // Altura de las filas
        tablaArchivos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); // Fuente del encabezado
        tablaArchivos.getTableHeader().setBackground(new Color(240, 240, 240)); // Fondo del encabezado
        tablaArchivos.getTableHeader().setForeground(Color.BLACK); // Texto del encabezado
        tablaArchivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable
        tablaArchivos.setSelectionBackground(new Color(220, 235, 255)); // Color de selección
        tablaArchivos.setSelectionForeground(Color.BLACK); // Texto de selección

        // Renderer personalizado para la columna 0 (Nombre del Archivo con icono)
        tablaArchivos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                // Obtiene el componente por defecto (JLabel)
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                // Verifica que el índice sea válido
                if (row < archivosConInfo.size()) {
                    InfoArchivo info = archivosConInfo.get(row); // Obtiene la info del archivo
                    label.setIcon(IconoUtils.iconoParaArchivo(info.archivo, 20)); // Establece icono según tipo
                    label.setText(" " + info.archivo.getNombreArchivo()); // Establece el nombre
                    label.setIconTextGap(8); // Espacio entre icono y texto
                }

                return label; // Retorna el label configurado
            }
        });

        // Renderer personalizado para la columna 1 (Tipo con icono)
        tablaArchivos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                // Obtiene el componente por defecto
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                // Verifica que el índice sea válido
                if (row < archivosConInfo.size()) {
                    InfoArchivo info = archivosConInfo.get(row); // Obtiene la info del archivo
                    ImageIcon iconoTipo = null; // Variable para el icono

                    // Determina el icono según el tipo de archivo
                    switch (info.archivo.getTipo()) {
                        case IMAGEN -> iconoTipo = IconoUtils.iconoImagen(14);
                        case VIDEO -> iconoTipo = IconoUtils.iconoVideo(14);
                        case DOCUMENTO -> iconoTipo = IconoUtils.iconoParaDocumento(info.archivo.getNombreArchivo(), 14);
                    }

                    label.setIcon(iconoTipo); // Establece el icono
                    label.setIconTextGap(6); // Espacio entre icono y texto
                }

                return label; // Retorna el label configurado
            }
        });

        // Renderer para la columna 2 (Remitente con icono de usuario)
        tablaArchivos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                // Obtiene el componente por defecto
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setIcon(IconoUtils.iconoUsuario(14)); // Establece icono de usuario
                label.setIconTextGap(6); // Espacio entre icono y texto

                return label; // Retorna el label configurado
            }
        });

        // Establece los anchos preferidos de las columnas
        tablaArchivos.getColumnModel().getColumn(0).setPreferredWidth(280); // Nombre
        tablaArchivos.getColumnModel().getColumn(1).setPreferredWidth(110); // Tipo
        tablaArchivos.getColumnModel().getColumn(2).setPreferredWidth(160); // Remitente
        tablaArchivos.getColumnModel().getColumn(3).setPreferredWidth(140); // Fecha
        tablaArchivos.getColumnModel().getColumn(4).setPreferredWidth(90); // Tamaño

        // Listener para cambios en la selección de la tabla
        tablaArchivos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Evita múltiples eventos
                actualizarBotones(); // Actualiza el estado de los botones
            }
        });

        // Listener para doble clic en la tabla
        tablaArchivos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Si es doble clic
                    visualizarGuardarArchivo(); // Visualiza/descarga el archivo
                }
            }
        });

        // Crea un scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaArchivos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15)); // Márgenes
        add(scrollPane, BorderLayout.CENTER); // Agrega al centro del diálogo

        // ========== PANEL DE BOTONES ==========
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(Color.WHITE); // Fondo blanco

        // Botón Visualizar/Guardar (dinámico según el tipo de archivo)
        btnVisualizarGuardar = new JButton();
        btnVisualizarGuardar.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        btnVisualizarGuardar.setBackground(new Color(0, 123, 255)); // Fondo azul
        btnVisualizarGuardar.setForeground(Color.WHITE); // Texto blanco
        btnVisualizarGuardar.setFocusPainted(false); // Sin borde de foco
        btnVisualizarGuardar.setPreferredSize(new Dimension(200, 42)); // Tamaño del botón
        btnVisualizarGuardar.setHorizontalTextPosition(SwingConstants.RIGHT); // Texto a la derecha
        btnVisualizarGuardar.setIconTextGap(8); // Espacio entre icono y texto
        // Borde compuesto con línea y padding
        btnVisualizarGuardar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btnVisualizarGuardar.addActionListener(e -> visualizarGuardarArchivo()); // Acción al hacer clic
        panelBotones.add(btnVisualizarGuardar); // Agrega el botón

        // Botón Cerrar
        btnCerrar = new JButton("Cerrar", IconoUtils.iconoCerrar(16));
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 13)); // Fuente en negrita
        btnCerrar.setBackground(Color.WHITE); // Fondo blanco
        btnCerrar.setForeground(Color.BLACK); // Texto negro
        btnCerrar.setFocusPainted(false); // Sin borde de foco
        btnCerrar.setPreferredSize(new Dimension(120, 42)); // Tamaño del botón
        btnCerrar.setHorizontalTextPosition(SwingConstants.RIGHT); // Texto a la derecha
        btnCerrar.setIconTextGap(8); // Espacio entre icono y texto
        // Borde compuesto con línea gris y padding
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btnCerrar.addActionListener(e -> dispose()); // Cierra el diálogo al hacer clic
        panelBotones.add(btnCerrar); // Agrega el botón

        add(panelBotones, BorderLayout.SOUTH); // Agrega el panel al sur del diálogo

        actualizarBotones(); // Actualiza el estado inicial de los botones
    }

    /**
     * Método cargarArchivos
     *
     * Carga los archivos recolectados en el modelo de la tabla para su visualización.
     */
    private void cargarArchivos() {
        modeloTabla.setRowCount(0); // Limpia todas las filas existentes de la tabla

        // Si no hay archivos, simplemente retorna
        if (archivosConInfo.isEmpty()) {
            return;
        }

        // Itera sobre todos los archivos con información
        for (InfoArchivo info : archivosConInfo) {
            String tipo = getTipoTexto(info.archivo); // Obtiene el texto del tipo de archivo

            // Agrega una fila con todos los datos del archivo
            modeloTabla.addRow(new Object[]{
                    info.archivo.getNombreArchivo(), // Columna 0: Nombre
                    tipo, // Columna 1: Tipo
                    info.remitenteNombre, // Columna 2: Remitente
                    info.fecha, // Columna 3: Fecha
                    info.archivo.getTamanoFormateado() // Columna 4: Tamaño
            });
        }
    }

    /**
     * Método getTipoTexto
     *
     * Convierte el tipo de archivo (enum) a una cadena de texto legible.
     *
     * @param archivo El archivo adjunto
     * @return String con el tipo de archivo en español
     */
    private String getTipoTexto(ArchivoAdjunto archivo) {
        // Usa switch expression para determinar el texto según el tipo
        return switch (archivo.getTipo()) {
            case IMAGEN -> "Imagen";
            case VIDEO -> "Video";
            case DOCUMENTO -> "Documento";
            default -> "Otro";
        };
    }

    /**
     * Método actualizarBotones
     *
     * Actualiza el estado y la apariencia del botón principal según
     * si hay un archivo seleccionado y de qué tipo es.
     */
    private void actualizarBotones() {
        int filaSeleccionada = tablaArchivos.getSelectedRow(); // Obtiene la fila seleccionada

        // Si no hay selección, deshabilita el botón
        if (filaSeleccionada == -1) {
            btnVisualizarGuardar.setText("Seleccione un archivo"); // Texto del botón
            btnVisualizarGuardar.setIcon(IconoUtils.iconoBuscar(16)); // Icono de búsqueda
            btnVisualizarGuardar.setBackground(new Color(200, 200, 200)); // Fondo gris
            btnVisualizarGuardar.setForeground(Color.DARK_GRAY); // Texto gris oscuro
            btnVisualizarGuardar.setEnabled(false); // Deshabilita el botón
            return; // Sale del método
        }

        btnVisualizarGuardar.setEnabled(true); // Habilita el botón
        InfoArchivo info = archivosConInfo.get(filaSeleccionada); // Obtiene info del archivo

        // Configura el botón según el tipo de archivo
        if (info.archivo.getTipo() == ArchivoAdjunto.TipoArchivo.IMAGEN) {
            // Para imágenes: visualizar
            btnVisualizarGuardar.setText("Visualizar Imagen");
            btnVisualizarGuardar.setIcon(IconoUtils.iconoOjoVisible(18));
            btnVisualizarGuardar.setForeground(Color.BLACK);
        } else if (info.archivo.getTipo() == ArchivoAdjunto.TipoArchivo.VIDEO) {
            // Para videos: descargar
            btnVisualizarGuardar.setText("Descargar Video");
            btnVisualizarGuardar.setIcon(IconoUtils.iconoVideo(18));
            btnVisualizarGuardar.setForeground(Color.BLACK);
        } else {
            // Para documentos: descargar
            btnVisualizarGuardar.setText("Descargar Archivo");
            btnVisualizarGuardar.setIcon(IconoUtils.iconoAdjuntar(18));
            btnVisualizarGuardar.setForeground(Color.BLACK);
        }
    }

    /**
     * Método actualizarListaArchivos
     *
     * Refresca la lista de archivos desde el grupo y actualiza la interfaz.
     * Muestra un mensaje de confirmación al usuario.
     */
    private void actualizarListaArchivos() {
        recolectarArchivos(); // Vuelve a recolectar archivos del grupo
        cargarArchivos(); // Recarga la tabla con los nuevos datos

        // Actualiza el contador en el panel superior
        // Obtiene los componentes del panel superior para encontrar el label del contador
        Component[] components = ((JPanel)((JPanel)getContentPane().getComponent(0)).getComponent(0)).getComponents();
        for (Component comp : components) {
            // Busca el JLabel que contiene el contador (empieza con "(")
            if (comp instanceof JLabel && ((JLabel)comp).getText().startsWith("(")) {
                // Actualiza el texto con el nuevo total
                ((JLabel)comp).setText("(" + archivosConInfo.size() + " archivo(s))");
                break; // Sale del bucle una vez encontrado
            }
        }

        actualizarBotones(); // Actualiza el estado de los botones

        // Muestra mensaje de confirmación
        JOptionPane.showMessageDialog(this,
                "Lista de archivos actualizada.\nTotal: " + archivosConInfo.size() + " archivo(s)",
                "Actualización Completa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Método visualizarGuardarArchivo
     *
     * Visualiza o guarda el archivo seleccionado según su tipo.
     * Usa la clase VisorDocumentos para manejar la visualización/descarga.
     */
    private void visualizarGuardarArchivo() {
        int filaSeleccionada = tablaArchivos.getSelectedRow(); // Obtiene la fila seleccionada

        // Verifica si hay una fila seleccionada
        if (filaSeleccionada == -1) {
            // Muestra advertencia si no hay selección
            JOptionPane.showMessageDialog(this,
                    "Seleccione un archivo de la lista",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Obtiene la información del archivo seleccionado
        InfoArchivo info = archivosConInfo.get(filaSeleccionada);
        // Llama al visor de documentos para visualizar/descargar el archivo
        VisorDocumentos.visualizar(this, info.archivo);
    }
}