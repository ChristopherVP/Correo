package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.ValidationException;
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
 * Clase DialogoChatGrupal
 *
 * Esta clase representa el diálogo principal de chat grupal donde los usuarios
 * pueden enviar mensajes, adjuntar archivos, ver la conversación, gestionar
 * miembros y administradores del grupo.
 *
 * Extiende de JDialog para crear una ventana modal de chat.
 */
public class DialogoChatGrupal extends JDialog {
    // Constante que define el tamaño máximo permitido para archivos adjuntos (10 MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // Referencia a la empresa de correo que contiene toda la información del sistema
    private EmpresaCorreo empresa;

    // Referencia al grupo actual del chat
    private Grupo grupo;

    // Nombre original del grupo (para detectar cambios)
    private String nombreGrupoOriginal;

    // Referencia al panel de grupos para poder actualizarlo cuando sea necesario
    private PanelGrupos panelGrupos;

    // Lista temporal de archivos adjuntos que el usuario está preparando para enviar
    private ArrayList<ArchivoAdjunto> archivosTemporales = new ArrayList<>();

    // Componentes de la interfaz gráfica
    private JTextArea txtAreaMensajes; // Área de texto para mostrar el historial de mensajes
    private JTextArea txtMensajeArea; // Área de texto para escribir nuevos mensajes
    private JPanel panelAdjuntos; // Panel que muestra los archivos adjuntos temporalmente

    // Botones de la interfaz
    private JButton btnAdjuntar; // Botón para adjuntar archivos
    private JButton btnVerArchivosChat; // Botón para ver todos los archivos del chat
    private JButton btnModificar; // Botón para modificar el grupo (solo administradores)
    private JButton btnVerMiembros; // Botón para ver la lista de miembros
    private JButton btnAdministradores; // Botón para gestionar administradores
    private JButton btnSalirGrupo; // Botón para salir del grupo (miembros normales)
    private JButton btnEliminarGrupo; // Botón para eliminar el grupo (solo administradores)
    private JButton btnEnviar; // Botón para enviar mensajes
    private JButton btnActualizar; // Botón para actualizar/refrescar el chat
    private JButton btnCerrar; // Botón para cerrar el diálogo

    /**
     * Constructor de DialogoChatGrupal
     *
     * Inicializa el diálogo de chat grupal con todos sus componentes.
     *
     * @param parent Ventana padre (JFrame principal de la aplicación)
     * @param empresa Referencia a la empresa de correo
     * @param grupo Grupo del chat que se está abriendo
     * @param panelGrupos Panel de grupos para actualizar cuando sea necesario
     */
    public DialogoChatGrupal(JFrame parent, EmpresaCorreo empresa, Grupo grupo, PanelGrupos panelGrupos) {
        // Llama al constructor de JDialog con título dinámico y modo modal
        super(parent, "Chat Grupal: " + grupo.getNombre(), true);

        this.empresa = empresa; // Guarda la referencia a la empresa
        this.grupo = grupo; // Guarda la referencia al grupo
        this.nombreGrupoOriginal = grupo.getNombre(); // Guarda el nombre original
        this.panelGrupos = panelGrupos; // Guarda la referencia al panel de grupos

        inicializarComponentes(); // Crea toda la interfaz gráfica
        cargarMensajes(); // Carga el historial de mensajes

        setSize(750, 700); // Establece el tamaño del diálogo
        setLocationRelativeTo(parent); // Centra el diálogo respecto a la ventana padre

        // Configura el comportamiento al cerrar la ventana
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // No cierra automáticamente

        // Agrega un listener para el evento de cierre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida(); // Llama al método de confirmación
            }
        });
    }

    /**
     * Método inicializarComponentes
     *
     * Crea y configura todos los componentes de la interfaz gráfica del chat.
     * Incluye el panel superior (info del grupo), área de mensajes, área de entrada,
     * panel de adjuntos y botones de gestión.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL SUPERIOR (Información del Grupo) ==========
        // Crea el panel superior con información del grupo
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 5));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15)); // Márgenes
        panelSuperior.setBackground(new Color(0, 102, 204)); // Fondo azul

        // Panel con información del grupo (nombre y cantidad de miembros)
        JPanel panelInfo = new JPanel(new BorderLayout(10, 5));
        panelInfo.setBackground(new Color(0, 102, 204)); // Mismo fondo azul

        // Label con el nombre del grupo
        JLabel lblNombreGrupo = new JLabel(grupo.getNombre());
        lblNombreGrupo.setFont(new Font("Arial", Font.BOLD, 18)); // Fuente grande y en negrita
        lblNombreGrupo.setForeground(Color.WHITE); // Texto blanco
        panelInfo.add(lblNombreGrupo, BorderLayout.NORTH); // Agrega al norte del panel

        // Label con la cantidad de miembros
        JLabel lblMiembros = new JLabel(grupo.getCantidadMiembros() + " miembros");
        lblMiembros.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente normal
        lblMiembros.setForeground(new Color(230, 230, 230)); // Texto blanco grisáceo
        panelInfo.add(lblMiembros, BorderLayout.CENTER); // Agrega al centro del panel

        panelSuperior.add(panelInfo, BorderLayout.WEST); // Agrega info a la izquierda

        // Panel con botones superiores (Ver Archivos y Actualizar)
        JPanel panelBotonesSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBotonesSuperior.setBackground(new Color(0, 102, 204)); // Fondo azul

        // Botón Ver Archivos del Chat
        btnVerArchivosChat = new JButton("Ver Archivos", IconoUtils.iconoArchivosChat(20));
        btnVerArchivosChat.setFont(new Font("Arial", Font.PLAIN, 11)); // Fuente pequeña
        btnVerArchivosChat.setBackground(Color.WHITE); // Fondo blanco
        btnVerArchivosChat.setForeground(Color.BLACK); // Texto negro
        btnVerArchivosChat.setFocusPainted(false); // Sin borde de foco
        btnVerArchivosChat.addActionListener(e -> verArchivosDelChat()); // Acción al hacer clic
        panelBotonesSuperior.add(btnVerArchivosChat); // Agrega el botón

        // Botón Actualizar
        btnActualizar = new JButton("Actualizar", IconoUtils.iconoActualizar(20));
        btnActualizar.setFont(new Font("Arial", Font.PLAIN, 11)); // Fuente pequeña
        btnActualizar.setBackground(Color.WHITE); // Fondo blanco
        btnActualizar.setFocusPainted(false); // Sin borde de foco
        btnActualizar.addActionListener(e -> cargarMensajes()); // Acción: recargar mensajes
        panelBotonesSuperior.add(btnActualizar); // Agrega el botón

        panelSuperior.add(panelBotonesSuperior, BorderLayout.EAST); // Agrega botones a la derecha
        add(panelSuperior, BorderLayout.NORTH); // Agrega panel superior al norte del diálogo

        // ========== PANEL CENTRAL (Área de Mensajes) ==========
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Márgenes

        // Área de texto para mostrar los mensajes del chat
        txtAreaMensajes = new JTextArea();
        txtAreaMensajes.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente normal
        txtAreaMensajes.setEditable(false); // No editable (solo lectura)
        txtAreaMensajes.setLineWrap(true); // Ajuste de línea automático
        txtAreaMensajes.setWrapStyleWord(true); // Ajuste por palabras completas
        txtAreaMensajes.setBackground(new Color(245, 245, 245)); // Fondo gris claro

        // Scroll pane para el área de mensajes
        JScrollPane scrollMensajes = new JScrollPane(txtAreaMensajes);
        scrollMensajes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Barra siempre visible
        panelCentral.add(scrollMensajes, BorderLayout.CENTER); // Agrega al centro
        add(panelCentral, BorderLayout.CENTER); // Agrega panel central al centro del diálogo

        // ========== PANEL INFERIOR (Entrada de Mensajes y Gestión) ==========
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10)); // Márgenes

        // Panel de entrada de mensajes
        JPanel panelEntrada = new JPanel(new BorderLayout(5, 0));

        // Área de texto para escribir nuevos mensajes
        txtMensajeArea = new JTextArea(2, 0); // 2 filas iniciales
        txtMensajeArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        txtMensajeArea.setLineWrap(true); // Ajuste de línea automático
        txtMensajeArea.setWrapStyleWord(true); // Ajuste por palabras

        // Scroll pane para el área de entrada
        JScrollPane scrollMensaje = new JScrollPane(txtMensajeArea);
        scrollMensaje.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Barra según necesidad
        scrollMensaje.setPreferredSize(new Dimension(0, 50)); // Altura preferida
        panelEntrada.add(scrollMensaje, BorderLayout.CENTER); // Agrega al centro

        // Botón Enviar
        btnEnviar = new JButton("Enviar", IconoUtils.iconoEnviar(20));
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        btnEnviar.setBackground(Color.WHITE); // Fondo blanco
        btnEnviar.setForeground(Color.BLACK); // Texto negro
        btnEnviar.setFocusPainted(false); // Sin borde de foco
        btnEnviar.setPreferredSize(new Dimension(120, 50)); // Tamaño del botón
        btnEnviar.addActionListener(e -> enviarMensaje()); // Acción: enviar mensaje
        panelEntrada.add(btnEnviar, BorderLayout.EAST); // Agrega a la derecha

        panelInferior.add(panelEntrada, BorderLayout.NORTH); // Agrega entrada al norte del panel inferior

        // ========== PANEL DE ADJUNTOS ==========
        // Container para el panel de adjuntos con título
        JPanel panelAdjuntosContainer = new JPanel(new BorderLayout(5, 5));
        panelAdjuntosContainer.setBorder(BorderFactory.createTitledBorder("Adjuntos")); // Borde con título

        // Panel que contendrá la lista de archivos adjuntos
        panelAdjuntos = new JPanel();
        panelAdjuntos.setLayout(new BoxLayout(panelAdjuntos, BoxLayout.Y_AXIS)); // Layout vertical
        panelAdjuntos.setBackground(Color.WHITE); // Fondo blanco

        // Scroll pane para el panel de adjuntos
        JScrollPane scrollAdjuntos = new JScrollPane(panelAdjuntos);
        scrollAdjuntos.setPreferredSize(new Dimension(0, 80)); // Altura preferida
        scrollAdjuntos.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Borde gris
        panelAdjuntosContainer.add(scrollAdjuntos, BorderLayout.CENTER); // Agrega al centro

        // Panel con botones de adjuntos
        JPanel panelBotonesAdjuntos = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        // Botón Adjuntar (icono de clip)
        btnAdjuntar = new JButton(IconoUtils.iconoAdjuntar(18));
        btnAdjuntar.setFont(new Font("Arial", Font.PLAIN, 11)); // Fuente pequeña
        btnAdjuntar.setBackground(Color.WHITE); // Fondo blanco
        btnAdjuntar.setFocusPainted(false); // Sin borde de foco
        btnAdjuntar.setToolTipText("Adjuntar archivo"); // Tooltip explicativo
        btnAdjuntar.setPreferredSize(new Dimension(45, 30)); // Tamaño del botón
        btnAdjuntar.addActionListener(e -> adjuntarArchivo()); // Acción: abrir selector de archivos
        panelBotonesAdjuntos.add(btnAdjuntar); // Agrega el botón

        panelAdjuntosContainer.add(panelBotonesAdjuntos, BorderLayout.SOUTH); // Agrega botones al sur
        panelInferior.add(panelAdjuntosContainer, BorderLayout.CENTER); // Agrega container al centro

        // ========== PANEL DE GESTIÓN (Botones de acciones del grupo) ==========
        JPanel panelGestion = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // Verifica si el usuario actual es administrador del grupo
        boolean esAdministrador = grupo.esAdministrador(empresa.getUsuarioActual().getEmail());

        // Botón Modificar (solo para administradores)
        btnModificar = new JButton("Modificar", IconoUtils.iconoEditar(18));
        btnModificar.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
        btnModificar.setBackground(Color.WHITE); // Fondo blanco
        btnModificar.setForeground(Color.BLACK); // Texto negro
        btnModificar.setFocusPainted(false); // Sin borde de foco
        btnModificar.addActionListener(e -> modificarGrupo()); // Acción: modificar grupo
        if (esAdministrador) {
            panelGestion.add(btnModificar); // Solo agrega si es administrador
        }

        // Botón Ver Miembros
        btnVerMiembros = new JButton("Ver Miembros", IconoUtils.iconoUsuario(18));
        btnVerMiembros.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
        btnVerMiembros.setBackground(Color.WHITE); // Fondo blanco
        btnVerMiembros.setForeground(Color.BLACK); // Texto negro
        btnVerMiembros.setFocusPainted(false); // Sin borde de foco
        btnVerMiembros.addActionListener(e -> verMiembros()); // Acción: ver lista de miembros
        panelGestion.add(btnVerMiembros); // Agrega el botón

        // Botón Administradores
        btnAdministradores = new JButton("Administradores", IconoUtils.iconoAdmin(18));
        btnAdministradores.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
        btnAdministradores.setBackground(Color.WHITE); // Fondo blanco
        btnAdministradores.setForeground(Color.BLACK); // Texto negro
        btnAdministradores.setFocusPainted(false); // Sin borde de foco
        btnAdministradores.addActionListener(e -> verAdministradores()); // Acción: gestionar admins
        panelGestion.add(btnAdministradores); // Agrega el botón

        // Botón Salir del Grupo (solo para miembros no administradores)
        btnSalirGrupo = new JButton("Salir del Grupo", IconoUtils.iconoSalir(18));
        btnSalirGrupo.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
        btnSalirGrupo.setBackground(Color.WHITE); // Fondo blanco
        btnSalirGrupo.setForeground(Color.BLACK); // Texto negro
        btnSalirGrupo.setFocusPainted(false); // Sin borde de foco
        btnSalirGrupo.addActionListener(e -> salirDelGrupo()); // Acción: salir del grupo
        if (!esAdministrador) {
            panelGestion.add(btnSalirGrupo); // Solo agrega si NO es administrador
        }

        // Botón Eliminar Grupo (solo para administradores)
        btnEliminarGrupo = new JButton("Eliminar Grupo", IconoUtils.iconoEliminar(18));
        btnEliminarGrupo.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
        btnEliminarGrupo.setBackground(Color.WHITE); // Fondo blanco
        btnEliminarGrupo.setForeground(Color.BLACK); // Texto negro
        btnEliminarGrupo.setFocusPainted(false); // Sin borde de foco
        btnEliminarGrupo.addActionListener(e -> eliminarGrupo()); // Acción: eliminar grupo
        if (esAdministrador) {
            panelGestion.add(btnEliminarGrupo); // Solo agrega si es administrador
        }

        // Botón Cerrar
        btnCerrar = new JButton("Cerrar", IconoUtils.iconoCerrar(18));
        btnCerrar.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
        btnCerrar.setBackground(Color.WHITE); // Fondo blanco
        btnCerrar.setForeground(Color.BLACK); // Texto negro
        btnCerrar.setFocusPainted(false); // Sin borde de foco
        btnCerrar.addActionListener(e -> confirmarSalida()); // Acción: confirmar antes de cerrar
        panelGestion.add(btnCerrar); // Agrega el botón

        panelInferior.add(panelGestion, BorderLayout.SOUTH); // Agrega panel de gestión al sur
        add(panelInferior, BorderLayout.SOUTH); // Agrega panel inferior al sur del diálogo
    }

    /**
     * Método actualizarPanelAdjuntos
     *
     * Actualiza visualmente el panel de adjuntos mostrando todos los archivos
     * temporales que el usuario ha seleccionado para enviar.
     */
    private void actualizarPanelAdjuntos() {
        panelAdjuntos.removeAll(); // Limpia todos los componentes existentes

        // Itera sobre todos los archivos temporales
        for (int i = 0; i < archivosTemporales.size(); i++) {
            ArchivoAdjunto archivo = archivosTemporales.get(i); // Obtiene el archivo
            final int index = i; // Captura el índice para el listener

            // Crea un panel para cada archivo (fila)
            JPanel filaArchivo = new JPanel(new BorderLayout(10, 0));
            filaArchivo.setBackground(Color.WHITE); // Fondo blanco
            // Borde compuesto: línea separadora inferior + padding interno
            filaArchivo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            // Establece altura máxima fija para cada fila
            filaArchivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

            // Label con información del archivo (nombre y tamaño)
            JLabel lblArchivo = new JLabel(archivo.getNombreArchivo() +
                    " (" + archivo.getTamanoFormateado() + ")");
            lblArchivo.setIcon(IconoUtils.iconoParaArchivo(archivo, 16)); // Icono según tipo
            lblArchivo.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente normal
            filaArchivo.add(lblArchivo, BorderLayout.CENTER); // Agrega al centro

            // Panel con botones de acción para el archivo
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            panelBotones.setBackground(Color.WHITE); // Fondo blanco

            // Si el archivo es una imagen, agrega botón de visualización
            if (archivo.getTipo() == ArchivoAdjunto.TipoArchivo.IMAGEN) {
                JButton btnOjo = new JButton(IconoUtils.iconoOjoVisible(16)); // Icono de ojo
                btnOjo.setToolTipText("Visualizar imagen"); // Tooltip
                btnOjo.setPreferredSize(new Dimension(32, 28)); // Tamaño del botón
                btnOjo.setBackground(Color.WHITE); // Fondo blanco
                btnOjo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Borde gris
                btnOjo.setFocusPainted(false); // Sin borde de foco
                btnOjo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano
                // Acción: visualizar la imagen
                btnOjo.addActionListener(e -> {
                    VisorDocumentos.visualizar(this, archivo);
                });
                panelBotones.add(btnOjo); // Agrega el botón
            }

            // Botón para eliminar el archivo de la lista temporal
            JButton btnEliminar = new JButton(IconoUtils.iconoEliminarX(16)); // Icono X
            btnEliminar.setToolTipText("Eliminar archivo"); // Tooltip
            btnEliminar.setPreferredSize(new Dimension(32, 28)); // Tamaño del botón
            btnEliminar.setBackground(Color.WHITE); // Fondo blanco
            btnEliminar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Borde gris
            btnEliminar.setFocusPainted(false); // Sin borde de foco
            btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano
            // Acción: eliminar el archivo de la lista y actualizar el panel
            btnEliminar.addActionListener(e -> {
                archivosTemporales.remove(index); // Elimina el archivo por índice
                actualizarPanelAdjuntos(); // Refresca el panel
            });
            panelBotones.add(btnEliminar); // Agrega el botón

            filaArchivo.add(panelBotones, BorderLayout.EAST); // Agrega botones a la derecha
            panelAdjuntos.add(filaArchivo); // Agrega la fila al panel principal
        }

        panelAdjuntos.revalidate(); // Revalida el layout
        panelAdjuntos.repaint(); // Repinta el panel
    }

    /**
     * Método cargarMensajes
     *
     * Carga y muestra todos los mensajes del grupo en el área de texto.
     * Diferencia entre mensajes normales y mensajes del sistema, formateándolos
     * de manera apropiada. Si no hay mensajes, muestra un mensaje de bienvenida.
     */
    private void cargarMensajes() {
        txtAreaMensajes.setText(""); // Limpia el área de mensajes
        ArrayList<MensajeGrupo> mensajes = grupo.getMensajes(); // Obtiene todos los mensajes del grupo

        // Si no hay mensajes, muestra un mensaje de bienvenida
        if (mensajes.isEmpty()) {
            txtAreaMensajes.append("═══════════════════════════════════════════\n");
            txtAreaMensajes.append("  No hay mensajes en este chat.\n");
            txtAreaMensajes.append("  ¡Sé el primero en escribir!\n");
            txtAreaMensajes.append("═══════════════════════════════════════════\n");
        } else {
            // Itera sobre todos los mensajes
            for (MensajeGrupo msg : mensajes) {
                // Verifica si es un mensaje del sistema o un mensaje normal
                if (msg.isEsMensajeSistema()) {
                    mostrarMensajeSistema(msg); // Muestra con formato de sistema
                } else {
                    mostrarMensajeNormal(msg); // Muestra con formato normal
                }
            }
        }

        // Posiciona el cursor al final del documento (scroll automático al final)
        txtAreaMensajes.setCaretPosition(txtAreaMensajes.getDocument().getLength());
    }

    /**
     * Método mostrarMensajeNormal
     *
     * Muestra un mensaje normal del chat con formato estándar:
     * remitente, fecha, contenido y archivos adjuntos si los hay.
     *
     * @param msg El mensaje a mostrar
     */
    private void mostrarMensajeNormal(MensajeGrupo msg) {
        // Determina si el mensaje es del usuario actual para agregar "(Tú)"
        String esPropio = msg.getRemitente().equalsIgnoreCase(empresa.getUsuarioActual().getEmail())
                ? " (Tú)" : "";

        // Agrega separador visual
        txtAreaMensajes.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        // Agrega nombre del remitente con indicador si es el usuario actual
        txtAreaMensajes.append(msg.getRemitenteNombre() + esPropio + "\n");
        // Agrega fecha y hora del mensaje
        txtAreaMensajes.append(msg.getFechaEnvioFormateada() + "\n\n");
        // Agrega el contenido del mensaje
        txtAreaMensajes.append(msg.getContenido() + "\n");

        // Si el mensaje tiene archivos adjuntos, los lista
        if (msg.tieneAdjuntos()) {
            txtAreaMensajes.append("Adjuntos (" + msg.getCantidadAdjuntos() + "):\n");
            // Itera sobre cada archivo adjunto
            for (ArchivoAdjunto adj : msg.getArchivosAdjuntos()) {
                String nombreIcono = obtenerNombreIcono(adj); // Obtiene emoji representativo
                // Agrega línea con información del archivo
                txtAreaMensajes.append("   • " + nombreIcono + " " + adj.getNombreArchivo() +
                        " (" + adj.getTamanoFormateado() + ")\n");
            }
        }
        txtAreaMensajes.append("\n"); // Línea en blanco al final
    }

    /**
     * Método obtenerNombreIcono
     *
     * Retorna un emoji apropiado según el tipo y extensión del archivo.
     *
     * @param archivo El archivo adjunto
     * @return String con el emoji correspondiente
     */
    private String obtenerNombreIcono(ArchivoAdjunto archivo) {
        // Switch para determinar el emoji según el tipo de archivo
        switch (archivo.getTipo()) {
            case IMAGEN: return "🖼"; // Emoji de imagen
            case VIDEO: return "🎬"; // Emoji de video
            case DOCUMENTO:
                // Para documentos, diferencia por extensión
                String nombre = archivo.getNombreArchivo().toLowerCase();
                if (nombre.endsWith(".pdf")) return "📄"; // PDF
                if (nombre.endsWith(".doc") || nombre.endsWith(".docx")) return "📝"; // Word
                if (nombre.endsWith(".xls") || nombre.endsWith(".xlsx")) return "📊"; // Excel
                if (nombre.endsWith(".txt")) return "📃"; // Texto plano
                return "📄"; // Documento genérico
            default: return "📎"; // Clip genérico
        }
    }

    /**
     * Método mostrarMensajeSistema
     *
     * Muestra un mensaje del sistema con formato especial.
     * Filtra mensajes según el contexto del usuario (si fue agregado, si salió, etc.)
     *
     * @param msg El mensaje del sistema a mostrar
     */
    private void mostrarMensajeSistema(MensajeGrupo msg) {
        String contenido = msg.getContenido(); // Obtiene el contenido del mensaje
        String emailUsuarioActual = empresa.getUsuarioActual().getEmail(); // Email del usuario actual

        // No muestra el mensaje si es sobre que el usuario fue agregado al grupo
        if (contenido.contains("agregó a") && contenido.contains(emailUsuarioActual)) {
            return; // Sale del método sin mostrar nada
        }

        // No muestra mensajes si el usuario ya no es miembro ni administrador
        if (!grupo.esMiembro(emailUsuarioActual) && !grupo.esAdministrador(emailUsuarioActual)) {
            return; // Sale del método sin mostrar nada
        }

        // Muestra el mensaje con formato de sistema
        txtAreaMensajes.append("┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈\n");
        txtAreaMensajes.append("🔔 " + contenido + "\n"); // Icono de campana + contenido
        txtAreaMensajes.append("   " + msg.getFechaEnvioFormateada() + "\n"); // Fecha indentada
        txtAreaMensajes.append("┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈\n\n");
    }

    /**
     * Método verArchivosDelChat
     *
     * Abre el diálogo que muestra todos los archivos compartidos en el chat grupal.
     */
    private void verArchivosDelChat() {
        // Crea una nueva instancia del diálogo de archivos
        DialogoArchivosChat dialogo = new DialogoArchivosChat(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre
                empresa, // Pasa la referencia a la empresa
                grupo); // Pasa la referencia al grupo
        dialogo.setVisible(true); // Muestra el diálogo (modal)
    }

    /**
     * Método adjuntarArchivo
     *
     * Abre un selector de archivos para que el usuario elija un archivo a adjuntar.
     * Valida el tamaño del archivo y lo agrega a la lista temporal de adjuntos.
     */
    private void adjuntarArchivo() {
        // Crea un selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Archivo"); // Título del diálogo

        // Crea un filtro para los tipos de archivo soportados
        FileNameExtensionFilter filterTodos = new FileNameExtensionFilter(
                "Todos los archivos soportados", // Descripción del filtro
                "jpg", "jpeg", "png", "gif", "bmp", // Imágenes
                "mp4", "avi", "mov", "wmv", "flv", // Videos
                "pdf", "doc", "docx", "txt", "xls", "xlsx"); // Documentos
        fileChooser.setFileFilter(filterTodos); // Aplica el filtro

        // Muestra el diálogo y obtiene la respuesta del usuario
        int resultado = fileChooser.showOpenDialog(this);

        // Si el usuario seleccionó un archivo (no canceló)
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile(); // Obtiene el archivo seleccionado

            // Valida que el archivo no exceda el tamaño máximo permitido
            if (archivo.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this,
                        "El archivo excede el límite de 10MB",
                        "Archivo demasiado grande",
                        JOptionPane.WARNING_MESSAGE);
                return; // Sale del método sin agregar el archivo
            }

            try {
                // Lee el contenido del archivo
                FileInputStream fis = new FileInputStream(archivo);
                byte[] contenido = new byte[(int) archivo.length()]; // Array de bytes del tamaño del archivo
                fis.read(contenido); // Lee el contenido en el array
                fis.close(); // Cierra el stream

                // Crea un objeto ArchivoAdjunto con los datos leídos
                ArchivoAdjunto adjunto = new ArchivoAdjunto(archivo.getName(), contenido, archivo.length());
                archivosTemporales.add(adjunto); // Agrega a la lista temporal
                actualizarPanelAdjuntos(); // Actualiza la visualización

            } catch (Exception e) {
                // Si hay error al leer el archivo, muestra mensaje de error
                JOptionPane.showMessageDialog(this,
                        "Error al leer el archivo: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Método confirmarSalida
     *
     * Verifica si hay contenido sin enviar antes de cerrar el diálogo.
     * Si hay contenido, pide confirmación al usuario.
     */
    private void confirmarSalida() {
        String mensaje = txtMensajeArea.getText().trim(); // Obtiene el texto del área de entrada
        // Verifica si hay contenido (mensaje o archivos adjuntos)
        boolean hayContenido = !mensaje.isEmpty() || !archivosTemporales.isEmpty();

        // Si hay contenido sin enviar, pide confirmación
        if (hayContenido) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea salir?\n\nSe perderá el mensaje y los adjuntos no enviados.",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            // Si el usuario confirma, cierra el diálogo
            if (opcion == JOptionPane.YES_OPTION) {
                dispose(); // Cierra el diálogo
            }
        } else {
            // Si no hay contenido, cierra directamente
            dispose();
        }
    }

    /**
     * Método enviarMensaje
     *
     * Envía el mensaje escrito junto con los archivos adjuntos al grupo.
     * Valida que haya contenido antes de enviar y limpia los campos después.
     */
    private void enviarMensaje() {
        String contenido = txtMensajeArea.getText().trim(); // Obtiene el contenido del mensaje

        // Valida que haya contenido o archivos adjuntos
        if (contenido.isEmpty() && archivosTemporales.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Escribe un mensaje o adjunta un archivo",
                    "Mensaje vacío",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método sin enviar
        }

        try {
            // Crea un nuevo mensaje grupal
            MensajeGrupo mensaje = new MensajeGrupo(
                    grupo.getNombre(), // Nombre del grupo
                    empresa.getUsuarioActual().getEmail(), // Email del remitente
                    empresa.getUsuarioActual().getNombreCompleto(), // Nombre del remitente
                    contenido // Contenido del mensaje
            );

            // Agrega todos los archivos adjuntos temporales al mensaje
            for (ArchivoAdjunto adj : archivosTemporales) {
                mensaje.agregarArchivoAdjunto(adj);
            }

            // Envía el mensaje a través de la empresa
            empresa.enviarMensajeGrupal(grupo.getNombre(), mensaje);

            // Limpia el área de texto
            txtMensajeArea.setText("");
            // Limpia la lista de archivos temporales
            archivosTemporales.clear();
            // Actualiza el panel de adjuntos (ahora vacío)
            actualizarPanelAdjuntos();

            // Recarga los mensajes para mostrar el nuevo mensaje enviado
            cargarMensajes();

        } catch (Exception e) {
            // Si hay error al enviar, muestra mensaje de error
            JOptionPane.showMessageDialog(this,
                    "Error al enviar mensaje: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método modificarGrupo
     *
     * Abre el diálogo de modificación del grupo (solo para administradores).
     * Si se realizan cambios, actualiza la información del grupo en el chat.
     */
    private void modificarGrupo() {
        // Obtiene la lista de contactos disponibles
        ArrayList<Contacto> contactosDisponibles = empresa.listarContactos();

        // Crea y muestra el diálogo de modificación de grupo
        DialogoGrupo dialogo = new DialogoGrupo(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Ventana padre
                empresa, // Referencia a la empresa
                grupo, // Grupo a modificar
                contactosDisponibles); // Contactos disponibles
        dialogo.setVisible(true); // Muestra el diálogo (modal)

        // Si se guardaron cambios
        if (dialogo.isGuardado()) {
            // Actualiza la referencia al grupo (puede haber cambiado)
            grupo = empresa.buscarGrupoPorNombre(grupo.getNombre());
            // Actualiza el título del diálogo con el nuevo nombre
            setTitle("Chat Grupal: " + grupo.getNombre());
            // Recarga los mensajes
            cargarMensajes();
        }
    }

    /**
     * Método verMiembros
     *
     * Abre el diálogo que muestra la lista de miembros del grupo.
     */
    private void verMiembros() {
        // Crea y muestra el diálogo de miembros
        DialogoMiembrosGrupo dialogo = new DialogoMiembrosGrupo(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Ventana padre
                empresa, // Referencia a la empresa
                grupo); // Grupo del cual mostrar miembros
        dialogo.setVisible(true); // Muestra el diálogo (modal)
    }

    /**
     * Método verAdministradores
     *
     * Abre el diálogo de gestión de administradores del grupo.
     */
    private void verAdministradores() {
        // Crea y muestra el diálogo de gestión de administradores
        DialogoGestionAdmins dialogo = new DialogoGestionAdmins(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Ventana padre
                empresa, // Referencia a la empresa
                grupo); // Grupo del cual gestionar administradores
        dialogo.setVisible(true); // Muestra el diálogo (modal)
    }

    /**
     * Método salirDelGrupo
     *
     * Permite a un miembro normal salir del grupo.
     * Solicita confirmación, registra un mensaje de sistema y actualiza la interfaz.
     */
    private void salirDelGrupo() {
        // Muestra diálogo de confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea salir del grupo:\n\n" + grupo.getNombre() + "?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        // Si el usuario confirma
        if (confirmacion == JOptionPane.YES_OPTION) {
            String emailUsuarioActual = empresa.getUsuarioActual().getEmail(); // Email del usuario

            // Busca el contacto del miembro en el grupo
            Contacto miembro = null;
            for (Contacto c : grupo.getMiembros()) {
                if (c.getEmail().equalsIgnoreCase(emailUsuarioActual)) {
                    miembro = c;
                    break; // Sale del bucle al encontrarlo
                }
            }

            // Intenta eliminar al miembro del grupo
            if (grupo.eliminarMiembroPorEmail(emailUsuarioActual)) {
                // Si encuentra al miembro, agrega mensaje de sistema
                if (miembro != null) {
                    grupo.agregarMensajeSistemaMiembroSalio(
                            emailUsuarioActual, // Email del que sale
                            miembro.getNombre() != null && !miembro.getNombre().isEmpty()
                                    ? miembro.getNombre() // Usa el nombre si existe
                                    : empresa.getUsuarioActual().getNombreCompleto() // O el nombre completo
                    );
                }

                // Guarda los cambios automáticamente
                empresa.guardarDatosAutomaticamente();

                // Muestra mensaje de éxito
                JOptionPane.showMessageDialog(this,
                        "Has salido del grupo exitosamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                // Si hay referencia al panel de grupos, lo refresca
                if (panelGrupos != null) {
                    panelGrupos.refrescar();
                }
                dispose(); // Cierra el diálogo
            } else {
                // Si no se pudo salir, muestra mensaje de error
                JOptionPane.showMessageDialog(this,
                        "No se pudo salir del grupo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Método eliminarGrupo
     *
     * Permite a un administrador eliminar completamente el grupo.
     * Solicita confirmación y actualiza la interfaz.
     */
    private void eliminarGrupo() {
        // Muestra diálogo de confirmación con advertencia
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea ELIMINAR el grupo:\n\n" + grupo.getNombre() + "?\n\n" +
                        "Esta acción es IRREVERSIBLE.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        // Si el usuario confirma
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Intenta eliminar el grupo
                if (empresa.eliminarGrupo(grupo.getNombre())) {
                    // Muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            "Grupo eliminado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Si hay referencia al panel de grupos, lo refresca
                    if (panelGrupos != null) {
                        panelGrupos.refrescar();
                    }
                    dispose(); // Cierra el diálogo
                }
            } catch (ValidationException e) {
                // Si hay error de validación, muestra el mensaje
                JOptionPane.showMessageDialog(this,
                        e.getMensaje(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}