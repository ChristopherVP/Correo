package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.Contacto;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase que representa un diálogo para seleccionar un contacto de la lista.
 * Utilizado principalmente para agregar miembros a listas de distribución.
 * Incluye funcionalidad de búsqueda para facilitar la selección.
 */
class DialogoSeleccionarContacto extends JDialog {
    // Lista completa de contactos disponibles para seleccionar
    private ArrayList<Contacto> contactosDisponibles;
    // Lista filtrada de contactos (cambia según el criterio de búsqueda)
    private ArrayList<Contacto> contactosFiltrados;
    // Contacto seleccionado por el usuario (null si no se seleccionó ninguno)
    private Contacto contactoSeleccionado = null;

    // Componentes de la interfaz de usuario
    private JTextField txtBuscar; // Campo de texto para ingresar criterio de búsqueda
    private JButton btnBuscar; // Botón para ejecutar la búsqueda
    private JTable tablaContactos; // Tabla que muestra los contactos
    private DefaultTableModel modeloTabla; // Modelo de datos de la tabla
    private JButton btnAgregar; // Botón para confirmar la selección
    private JButton btnRegresar; // Botón para cancelar y cerrar el diálogo

    /**
     * Constructor del diálogo de selección de contacto.
     * @param parent Ventana padre del diálogo
     * @param contactos Lista de contactos disponibles para seleccionar
     */
    public DialogoSeleccionarContacto(JFrame parent, ArrayList<Contacto> contactos) {
        // Llama al constructor de JDialog con título y modo modal
        super(parent, "Agregar Miembro", true);
        // Guarda la referencia a los contactos disponibles
        this.contactosDisponibles = contactos;
        // Inicializa la lista filtrada con todos los contactos
        this.contactosFiltrados = new ArrayList<>(contactos);

        // Inicializa todos los componentes visuales
        inicializarComponentes();
        // Carga los contactos en la tabla
        cargarContactos();

        // Establece el tamaño del diálogo
        setSize(550, 450);
        // Centra el diálogo respecto a la ventana padre
        setLocationRelativeTo(parent);
    }

    /**
     * Método que inicializa y configura todos los componentes visuales del diálogo.
     * Crea el panel de búsqueda, la tabla de contactos y los botones de acción.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado de 10 píxeles
        setLayout(new BorderLayout(10, 10));

        // ===== PANEL DE BÚSQUEDA =====
        // Crea el panel de búsqueda con FlowLayout alineado a la izquierda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        // Agrega márgenes internos al panel
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Etiqueta "Buscar:"
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 13));
        panelBusqueda.add(lblBuscar);

        // Campo de texto para búsqueda
        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por correo o nombre"); // Texto de ayuda
        // Permite buscar al presionar Enter
        txtBuscar.addActionListener(e -> buscarContactos());
        panelBusqueda.add(txtBuscar);

        // Botón de búsqueda con ícono
        btnBuscar = new JButton(IconoUtils.iconoBuscar(18));
        btnBuscar.setToolTipText("Buscar contacto");
        btnBuscar.setFocusPainted(false); // Sin borde de foco
        btnBuscar.setBorderPainted(true); // Con borde
        btnBuscar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1)); // Borde gris
        btnBuscar.setPreferredSize(new Dimension(35, 26));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de manita
        // Asigna la acción de búsqueda
        btnBuscar.addActionListener(e -> buscarContactos());
        panelBusqueda.add(btnBuscar);

        // Agrega el panel de búsqueda en la parte superior
        add(panelBusqueda, BorderLayout.NORTH);

        // ===== PANEL CENTRAL CON TABLA =====
        // Crea el panel central con BorderLayout
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        // Agrega márgenes internos
        panelCentral.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // Etiqueta instructiva
        JLabel lblSeleccione = new JLabel("Seleccione el contacto a agregar:");
        lblSeleccione.setFont(new Font("Arial", Font.PLAIN, 13));
        panelCentral.add(lblSeleccione, BorderLayout.NORTH);

        // ===== TABLA DE CONTACTOS =====
        // Define las columnas de la tabla
        String[] columnas = {"Correo", "Nombre"};
        // Crea el modelo de tabla con las columnas definidas
        modeloTabla = new DefaultTableModel(columnas, 0) {
            /**
             * Hace que todas las celdas sean no editables.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna celda es editable
            }
        };

        // Crea la tabla con el modelo
        tablaContactos = new JTable(modeloTabla);
        tablaContactos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaContactos.setRowHeight(25); // Altura de las filas
        tablaContactos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); // Fuente del encabezado
        tablaContactos.getTableHeader().setBackground(new Color(240, 240, 240)); // Color de fondo del encabezado
        tablaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable

        // Configura el ancho de las columnas
        tablaContactos.getColumnModel().getColumn(0).setPreferredWidth(250); // Columna "Correo"
        tablaContactos.getColumnModel().getColumn(1).setPreferredWidth(200); // Columna "Nombre"

        // Agrega funcionalidad de doble clic para seleccionar
        tablaContactos.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Detecta doble clic en la tabla.
             */
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Si se hizo doble clic
                if (e.getClickCount() == 2) {
                    // Confirma la selección automáticamente
                    confirmarSeleccion();
                }
            }
        });

        // Envuelve la tabla en un JScrollPane para permitir desplazamiento
        JScrollPane scrollPane = new JScrollPane(tablaContactos);
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        // Agrega el panel central al diálogo
        add(panelCentral, BorderLayout.CENTER);

        // ===== PANEL DE BOTONES =====
        // Crea el panel de botones con FlowLayout centrado
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // Botón "Agregar" con ícono
        btnAgregar = new JButton("Agregar", IconoUtils.iconoAgregar(18));
        btnAgregar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnAgregar.setFocusPainted(false);
        btnAgregar.setPreferredSize(new Dimension(120, 30));
        btnAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Asigna la acción de confirmar selección
        btnAgregar.addActionListener(e -> confirmarSeleccion());
        panelBotones.add(btnAgregar);

        // Botón "Regresar" con ícono
        btnRegresar = new JButton("Regresar", IconoUtils.iconoRegresar(18));
        btnRegresar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnRegresar.setFocusPainted(false);
        btnRegresar.setPreferredSize(new Dimension(120, 30));
        btnRegresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Asigna la acción de cerrar el diálogo
        btnRegresar.addActionListener(e -> cerrarDialogo());
        panelBotones.add(btnRegresar);

        // Agrega el panel de botones en la parte inferior
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Método que carga los contactos filtrados en la tabla.
     * También controla la habilitación del botón "Agregar" según si hay contactos.
     */
    private void cargarContactos() {
        // Limpia todas las filas de la tabla
        modeloTabla.setRowCount(0);

        // Si no hay contactos filtrados
        if (contactosFiltrados.isEmpty()) {
            // Deshabilita el botón de agregar
            btnAgregar.setEnabled(false);
        } else {
            // Itera sobre cada contacto filtrado
            for (Contacto c : contactosFiltrados) {
                // Agrega una fila con el email y nombre del contacto
                modeloTabla.addRow(new Object[]{
                        c.getEmail(),
                        c.getNombre()
                });
            }
            // Habilita el botón de agregar
            btnAgregar.setEnabled(true);
        }
    }

    /**
     * Método que filtra los contactos según el criterio de búsqueda ingresado.
     * Busca coincidencias en el email o nombre del contacto.
     */
    private void buscarContactos() {
        // Obtiene el criterio de búsqueda, limpia espacios y convierte a minúsculas
        String criterio = txtBuscar.getText().trim().toLowerCase();

        // Si no hay criterio de búsqueda
        if (criterio.isEmpty()) {
            // Muestra todos los contactos
            contactosFiltrados = new ArrayList<>(contactosDisponibles);
            cargarContactos();
            return; // Sale del método
        }

        // Limpia la lista de contactos filtrados
        contactosFiltrados.clear();
        // Itera sobre todos los contactos disponibles
        for (Contacto c : contactosDisponibles) {
            // Verifica si el email contiene el criterio
            boolean coincideEmail = c.getEmail().toLowerCase().contains(criterio);
            // Verifica si el nombre contiene el criterio (si existe nombre)
            boolean coincideNombre = c.getNombre() != null &&
                    c.getNombre().toLowerCase().contains(criterio);

            // Si coincide en email o nombre
            if (coincideEmail || coincideNombre) {
                // Agrega el contacto a la lista filtrada
                contactosFiltrados.add(c);
            }
        }

        // Recarga la tabla con los contactos filtrados
        cargarContactos();

        // Si no se encontraron resultados
        if (contactosFiltrados.isEmpty()) {
            // Muestra mensaje informativo
            JOptionPane.showMessageDialog(this,
                    "No se encontraron contactos con el criterio: " + txtBuscar.getText(),
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Método que cierra el diálogo después de confirmar con el usuario.
     * Pregunta si está seguro de salir sin agregar un miembro.
     */
    private void cerrarDialogo() {
        // Muestra un diálogo de confirmación
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro/a que desea regresar sin agregar un miembro?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        // Si el usuario confirma
        if (respuesta == JOptionPane.YES_OPTION) {
            // Cierra el diálogo
            dispose();
        }
    }

    /**
     * Método que confirma la selección del contacto.
     * Valida que haya una fila seleccionada y guarda el contacto seleccionado.
     */
    private void confirmarSeleccion() {
        // Obtiene el índice de la fila seleccionada
        int filaSeleccionada = tablaContactos.getSelectedRow();

        // Si no hay ninguna fila seleccionada
        if (filaSeleccionada == -1) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Seleccione un contacto de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Verifica que el índice sea válido
        if (filaSeleccionada >= 0 && filaSeleccionada < contactosFiltrados.size()) {
            // Obtiene el contacto de la lista filtrada según el índice
            contactoSeleccionado = contactosFiltrados.get(filaSeleccionada);
            // Cierra el diálogo
            dispose();
        }
    }

    /**
     * Método getter que retorna el contacto seleccionado.
     * @return Contacto seleccionado o null si no se seleccionó ninguno
     */
    public Contacto getContactoSeleccionado() {
        return contactoSeleccionado;
    }
}