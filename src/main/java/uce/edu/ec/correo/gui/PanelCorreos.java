package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.ValidationException;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase que representa el panel de gestión de correos electrónicos.
 * Permite visualizar, buscar y gestionar los correos del usuario.
 * Incluye filtros por tipo (enviados, recibidos, borradores, etc.) y
 * funcionalidad para marcar correos como destacados.
 */
public class PanelCorreos extends JPanel {
    // Referencia a la empresa para acceder a los correos
    private EmpresaCorreo empresa;

    // Componentes de la interfaz de usuario
    private JTable tablaTodos; // Tabla que muestra los correos
    private DefaultTableModel modeloTodos; // Modelo de datos de la tabla
    private JTextField txtBuscar; // Campo de texto para búsqueda
    private JButton btnBuscar; // Botón para ejecutar la búsqueda

    // Variable que almacena el filtro actual aplicado a la tabla
    private String filtroActual = "TODOS";

    /**
     * Constructor del panel de correos.
     * @param empresa Referencia a la empresa de correo
     */
    public PanelCorreos(EmpresaCorreo empresa) {
        // Guarda la referencia a la empresa
        this.empresa = empresa;
        // Inicializa todos los componentes visuales
        inicializarComponentes();
    }

    /**
     * Método que inicializa y configura todos los componentes visuales del panel.
     * Crea el panel de búsqueda y la tabla de correos con columna de estrella.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado de 10 píxeles
        setLayout(new BorderLayout(10, 10));
        // Agrega márgenes internos de 10 píxeles en todos los lados
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== PANEL SUPERIOR =====
        // Crea el panel superior con BorderLayout
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        // ===== PANEL DE BÚSQUEDA =====
        // Crea el panel de búsqueda con FlowLayout alineado a la izquierda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // Agrega un borde con título descriptivo
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar correo por Asunto o Remitente"));

        // Etiqueta "Buscar:"
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBuscar.setForeground(Color.BLACK);
        panelBusqueda.add(lblBuscar);

        // Campo de texto para búsqueda
        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        txtBuscar.setToolTipText("Ingrese asunto o remitente del correo"); // Texto de ayuda
        // Permite buscar al presionar Enter
        txtBuscar.addActionListener(e -> buscarCorreo());
        panelBusqueda.add(txtBuscar);

        // Botón de búsqueda con ícono
        btnBuscar = new JButton(IconoUtils.iconoBuscar(20));
        btnBuscar.setBackground(Color.WHITE);
        btnBuscar.setFocusPainted(false); // Sin borde de foco
        btnBuscar.setBorderPainted(true); // Con borde
        btnBuscar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1)); // Borde gris
        btnBuscar.setPreferredSize(new Dimension(40, 28));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de manita
        btnBuscar.setToolTipText("Buscar correo");
        // Asigna la acción de búsqueda
        btnBuscar.addActionListener(e -> buscarCorreo());
        panelBusqueda.add(btnBuscar);

        // Agrega el panel de búsqueda al panel superior
        panelSuperior.add(panelBusqueda, BorderLayout.NORTH);
        // Agrega el panel superior al panel principal
        add(panelSuperior, BorderLayout.NORTH);

        // ===== TABLA DE CORREOS =====
        // Define las columnas de la tabla (incluye columna vacía para estrella)
        String[] columnas = {"Asunto", "De/Para", "Fecha", "Estado", "  "};
        // Crea el modelo de tabla con las columnas definidas
        modeloTodos = new DefaultTableModel(columnas, 0) {
            /**
             * Define qué celdas son editables.
             * Solo la columna 4 (estrella) es editable.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Solo la columna de estrella es editable
            }

            /**
             * Define el tipo de datos de cada columna.
             * La columna 4 es Boolean (checkbox), las demás son String.
             */
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? Boolean.class : String.class;
            }
        };

        // Crea la tabla con el modelo
        tablaTodos = new JTable(modeloTodos);
        // Configura las propiedades de la tabla
        configurarTabla(tablaTodos);
        // Agrega funcionalidad de doble clic para ver detalles
        tablaTodos.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Detecta doble clic en la tabla.
             */
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Si se hizo doble clic
                if (e.getClickCount() == 2) {
                    // Abre el diálogo de detalle del correo
                    verDetalle();
                }
            }
        });

        // Envuelve la tabla en un JScrollPane para permitir desplazamiento
        JScrollPane scrollPane = new JScrollPane(tablaTodos);
        // Siempre muestra la barra de desplazamiento vertical
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // Agrega el scrollPane al centro del panel
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Método que configura las propiedades visuales y funcionales de la tabla.
     * Define el ancho de columnas, renderer personalizado para estrella y listener para cambios.
     * @param tabla La tabla a configurar
     */
    private void configurarTabla(JTable tabla) {
        // Establece la fuente de la tabla
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        // Establece la altura de las filas
        tabla.setRowHeight(25);
        // Configura la fuente del encabezado
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        // Establece el color de fondo del encabezado
        tabla.getTableHeader().setBackground(new Color(240, 240, 240));
        // Permite seleccionar solo una fila a la vez
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ===== CONFIGURAR ANCHO DE COLUMNAS =====
        tabla.getColumnModel().getColumn(0).setPreferredWidth(300); // Asunto
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200); // De/Para
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150); // Fecha
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120); // Estado
        tabla.getColumnModel().getColumn(4).setPreferredWidth(40);  // Estrella
        tabla.getColumnModel().getColumn(4).setMaxWidth(50);        // Límite máximo para estrella

        // ===== RENDERER PERSONALIZADO PARA LA COLUMNA DE ESTRELLA =====
        tabla.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            /**
             * Método que renderiza la columna de estrella con íconos personalizados.
             * Muestra una estrella llena si está destacado, vacía si no lo está.
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                // Crea una etiqueta para mostrar el ícono
                JLabel label = new JLabel();
                // Centra el contenido
                label.setHorizontalAlignment(JLabel.CENTER);

                // Determina si el correo está destacado (value es Boolean)
                boolean destacado = value != null && (Boolean) value;
                // Establece el ícono de estrella (llena o vacía)
                label.setIcon(IconoUtils.iconoEstrella(20, destacado));

                // Si la fila está seleccionada
                if (isSelected) {
                    // Usa el color de selección de la tabla
                    label.setBackground(table.getSelectionBackground());
                    label.setOpaque(true); // Hace opaco para que se vea el color
                } else {
                    // Usa el color normal de fondo
                    label.setBackground(table.getBackground());
                    label.setOpaque(true);
                }

                return label;
            }
        });

        // ===== AGREGAR LISTENER PARA CAMBIOS EN LA ESTRELLA =====
        tabla.getModel().addTableModelListener(e -> {
            // Si el cambio fue en la columna de estrella
            if (e.getColumn() == 4) {
                // Obtiene la fila modificada
                int row = e.getFirstRow();
                // Obtiene el nuevo valor de destacado
                boolean destacado = (Boolean) tabla.getValueAt(row, 4);
                // Obtiene el asunto del correo de la primera columna
                String asunto = (String) tabla.getValueAt(row, 0);

                // Busca el correo en la empresa usando el asunto
                Correo correo = empresa.buscarCorreoPorAsunto(asunto);
                if (correo != null) {
                    // Actualiza el estado de destacado del correo
                    correo.setDestacado(destacado);
                    // Guarda los cambios automáticamente
                    empresa.guardarDatosAutomaticamente();
                    // Redibuja la tabla para actualizar el icono
                    tabla.repaint();
                }
            }
        });
    }

    /**
     * Método público que refresca el contenido de la tabla.
     * Si hay texto en el campo de búsqueda, aplica el filtro; de lo contrario,
     * muestra los correos según el filtro actual.
     */
    public void refrescar() {
        // Obtiene el texto actual del campo de búsqueda
        String textoBusqueda = txtBuscar.getText().trim();

        // Si hay criterio de búsqueda
        if (!textoBusqueda.isEmpty()) {
            // Aplica el filtro de búsqueda
            buscarCorreo();
        } else {
            // Muestra correos según el filtro actual
            cargarCorreosSegunFiltro();
        }
    }

    /**
     * Método que cambia el filtro a "TODOS" y refresca la tabla.
     */
    public void mostrarTodos() {
        filtroActual = "TODOS";
        txtBuscar.setText(""); // Limpia el campo de búsqueda
        refrescar();
    }

    /**
     * Método que cambia el filtro a "ENVIADOS" y refresca la tabla.
     */
    public void mostrarEnviados() {
        filtroActual = "ENVIADOS";
        txtBuscar.setText("");
        refrescar();
    }

    /**
     * Método que cambia el filtro a "RECIBIDOS" y refresca la tabla.
     */
    public void mostrarRecibidos() {
        filtroActual = "RECIBIDOS";
        txtBuscar.setText("");
        refrescar();
    }

    /**
     * Método que cambia el filtro a "BORRADORES" y refresca la tabla.
     */
    public void mostrarBorradores() {
        filtroActual = "BORRADORES";
        txtBuscar.setText("");
        refrescar();
    }

    /**
     * Método que cambia el filtro a "REENVIADOS" y refresca la tabla.
     */
    public void mostrarReenviados() {
        filtroActual = "REENVIADOS";
        txtBuscar.setText("");
        refrescar();
    }

    /**
     * Método que cambia el filtro a "ELIMINADOS" y refresca la tabla.
     */
    public void mostrarEliminados() {
        filtroActual = "ELIMINADOS";
        txtBuscar.setText("");
        refrescar();
    }

    /**
     * Método que cambia el filtro a "DESTACADOS" y refresca la tabla.
     */
    public void mostrarDestacados() {
        filtroActual = "DESTACADOS";
        txtBuscar.setText("");
        refrescar();
    }

    /**
     * Método privado que carga los correos en la tabla según el filtro actual.
     * Aplica diferentes criterios de filtrado según el valor de filtroActual.
     */
    private void cargarCorreosSegunFiltro() {
        // Limpia todas las filas de la tabla
        modeloTodos.setRowCount(0);
        // Obtiene el email del usuario actual
        String emailUsuario = empresa.getUsuarioActual().getEmail();
        // Lista que contendrá los correos a mostrar
        ArrayList<Correo> correos = new ArrayList<>();

        // ===== APLICAR FILTRO SEGÚN EL TIPO SELECCIONADO =====
        switch (filtroActual) {
            case "TODOS":
                // Obtiene todos los correos activos (no eliminados)
                correos = empresa.listarTodosLosCorreosActivos();
                break;
            case "ENVIADOS":
                // Obtiene solo los correos enviados por el usuario
                correos = empresa.listarCorreosEnviados();
                break;
            case "RECIBIDOS":
                // Obtiene solo los correos recibidos por el usuario
                correos = empresa.listarCorreosRecibidos();
                break;
            case "BORRADORES":
                // Obtiene solo los borradores del usuario
                correos = empresa.listarCorreosBorradores();
                break;
            case "REENVIADOS":
                // Obtiene solo los correos reenviados
                correos = empresa.listarCorreosReenviados();
                break;
            case "ELIMINADOS":
                // Obtiene solo los correos eliminados
                correos = empresa.listarCorreosEliminados();
                break;
            case "DESTACADOS":
                // Obtiene todos los correos activos y filtra los destacados
                correos = empresa.listarTodosLosCorreosActivos();
                ArrayList<Correo> destacados = new ArrayList<>();
                for (Correo c : correos) {
                    // Solo agrega los correos marcados como destacados
                    if (c.isDestacado()) {
                        destacados.add(c);
                    }
                }
                correos = destacados;
                break;
        }

        // ===== AGREGAR CORREOS A LA TABLA =====
        for (Correo c : correos) {
            String dePara;
            // Determina si mostrar "Para:" o "De:" según el remitente
            if (c.getRemitente().equalsIgnoreCase(emailUsuario)) {
                // Si el usuario es el remitente, muestra "Para: X destinatario(s)"
                dePara = "Para: " + c.getTotalDestinatarios() + " destinatario(s)";
            } else {
                // Si el usuario es destinatario, muestra "De: email_remitente"
                dePara = "De: " + c.getRemitente();
            }

            // Agrega una fila a la tabla con los datos del correo
            modeloTodos.addRow(new Object[]{
                    c.getAsunto(),                      // Columna 0: Asunto
                    dePara,                             // Columna 1: De/Para
                    c.getFechaEnvioFormateada(),       // Columna 2: Fecha
                    c.getEstado().getDescripcion(),    // Columna 3: Estado
                    c.isDestacado()                    // Columna 4: Destacado (Boolean)
            });
        }
    }

    /**
     * Método público que abre el diálogo para crear un nuevo correo.
     * Refresca la tabla si se guardó el correo exitosamente.
     */
    public void abrirNuevoCorreo() {
        // Crea el diálogo de nuevo correo
        DialogoNuevoCorreo dialogo = new DialogoNuevoCorreo(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre
                empresa);
        // Muestra el diálogo
        dialogo.setVisible(true);
        // Si se guardó el correo exitosamente
        if (dialogo.isGuardado()) {
            // Refresca la tabla para mostrar el nuevo correo
            refrescar();
        }
    }

    /**
     * Método privado que busca correos según el criterio ingresado.
     * Filtra por asunto o remitente del correo.
     */
    private void buscarCorreo() {
        // Obtiene y limpia el criterio de búsqueda
        String criterio = txtBuscar.getText().trim();

        // Si el criterio está vacío
        if (criterio.isEmpty()) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Ingrese un asunto o remitente para buscar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Limpia todas las filas de la tabla
        modeloTodos.setRowCount(0);

        // Obtiene todos los correos activos
        ArrayList<Correo> todosLosCorreos = empresa.listarTodosLosCorreosActivos();
        // Obtiene los correos eliminados
        ArrayList<Correo> eliminados = empresa.listarCorreosEliminados();
        // Combina ambas listas para buscar en todos los correos
        todosLosCorreos.addAll(eliminados);

        // Obtiene el email del usuario actual
        String emailUsuario = empresa.getUsuarioActual().getEmail();
        // Bandera para controlar si se encontró al menos un resultado
        boolean encontrado = false;

        // Convierte el criterio a minúsculas para búsqueda insensible a mayúsculas
        String criterioBusqueda = criterio.toLowerCase();
        // Itera sobre cada correo
        for (Correo c : todosLosCorreos) {
            // Verifica si el asunto contiene el criterio
            boolean coincideAsunto = c.getAsunto().toLowerCase().contains(criterioBusqueda);
            // Verifica si el remitente contiene el criterio
            boolean coincideRemitente = c.getRemitente().toLowerCase().contains(criterioBusqueda);

            // Si coincide en asunto o remitente
            if (coincideAsunto || coincideRemitente) {
                // Agrega el correo a la tabla
                agregarCorreoATabla(c, emailUsuario);
                // Marca que se encontró al menos un resultado
                encontrado = true;
            }
        }

        // Si no se encontraron resultados
        if (!encontrado) {
            // Muestra mensaje informativo
            JOptionPane.showMessageDialog(this,
                    "No se encontraron correos con el criterio: " + criterio,
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            // Limpia el campo de búsqueda
            txtBuscar.setText("");
            // Recarga los correos según el filtro actual
            cargarCorreosSegunFiltro();
        }
    }

    /**
     * Método privado auxiliar que agrega un correo a la tabla.
     * Determina si mostrar "Para:" o "De:" según el remitente.
     * @param c El correo a agregar
     * @param emailUsuario El email del usuario actual
     */
    private void agregarCorreoATabla(Correo c, String emailUsuario) {
        String dePara;
        // Determina si mostrar "Para:" o "De:" según el remitente
        if (c.getRemitente().equalsIgnoreCase(emailUsuario)) {
            // Si el usuario es el remitente
            dePara = "Para: " + c.getTotalDestinatarios() + " destinatario(s)";
        } else {
            // Si el usuario es destinatario
            dePara = "De: " + c.getRemitente();
        }

        // Agrega una fila a la tabla con los datos del correo
        modeloTodos.addRow(new Object[]{
                c.getAsunto(),
                dePara,
                c.getFechaEnvioFormateada(),
                c.getEstado().getDescripcion(),
                c.isDestacado()
        });
    }

    /**
     * Método privado que abre el diálogo de detalle del correo seleccionado.
     * Permite ver toda la información del correo y realizar acciones sobre él.
     */
    private void verDetalle() {
        // Obtiene el índice de la fila seleccionada
        int filaSeleccionada = tablaTodos.getSelectedRow();

        // Si no hay ninguna fila seleccionada
        if (filaSeleccionada == -1) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Seleccione un correo para ver sus detalles",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Obtiene el asunto del correo de la primera columna de la fila seleccionada
        String asunto = (String) modeloTodos.getValueAt(filaSeleccionada, 0);
        // Busca el correo completo en la empresa usando el asunto
        Correo correo = empresa.buscarCorreoPorAsunto(asunto);

        // Si se encontró el correo
        if (correo != null) {
            // Crea el diálogo de detalle del correo
            DialogoDetalleCorreo dialogo = new DialogoDetalleCorreo(
                    (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre
                    empresa,
                    correo,
                    this); // Pasa referencia a este panel para refrescar después
            // Muestra el diálogo
            dialogo.setVisible(true);
            // Refresca la tabla después de cerrar el diálogo (puede haber cambios)
            refrescar();
        } else {
            // Si no se pudo encontrar el correo, muestra error
            JOptionPane.showMessageDialog(this,
                    "No se pudo encontrar el correo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}