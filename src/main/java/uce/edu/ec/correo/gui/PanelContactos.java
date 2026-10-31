package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase que representa el panel de gestión de contactos.
 * Permite visualizar, buscar y gestionar los contactos del usuario.
 * Incluye funcionalidad para ver detalles de contactos mediante doble clic.
 */
public class PanelContactos extends JPanel {
    // Referencia a la empresa para acceder a los contactos
    private EmpresaCorreo empresa;

    // Componentes de la interfaz de usuario
    private JTable tablaContactos; // Tabla que muestra los contactos
    private DefaultTableModel modeloTabla; // Modelo de datos de la tabla
    private JTextField txtBuscar; // Campo de texto para búsqueda
    private JButton btnBuscar; // Botón para ejecutar la búsqueda

    /**
     * Constructor del panel de contactos.
     * @param empresa Referencia a la empresa de correo
     */
    public PanelContactos(EmpresaCorreo empresa) {
        // Guarda la referencia a la empresa
        this.empresa = empresa;
        // Inicializa todos los componentes visuales
        inicializarComponentes();
    }

    /**
     * Método que inicializa y configura todos los componentes visuales del panel.
     * Crea el panel de búsqueda y la tabla de contactos.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado de 10 píxeles
        setLayout(new BorderLayout(10, 10));
        // Agrega márgenes internos de 10 píxeles en todos los lados
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== PANEL SUPERIOR SOLO CON BÚSQUEDA =====
        // Crea el panel superior con BorderLayout
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        // ===== PANEL DE BÚSQUEDA =====
        // Crea el panel de búsqueda con FlowLayout alineado a la izquierda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // Agrega un borde con título descriptivo
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar por correo o nombre"));

        // Etiqueta "Buscar:"
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBuscar.setForeground(Color.BLACK);
        panelBusqueda.add(lblBuscar);

        // Campo de texto para búsqueda
        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        txtBuscar.setToolTipText("Ingrese email o nombre del contacto"); // Texto de ayuda
        // Permite buscar al presionar Enter
        txtBuscar.addActionListener(e -> buscarContacto());
        panelBusqueda.add(txtBuscar);

        // Botón de búsqueda con ícono
        btnBuscar = new JButton(IconoUtils.iconoBuscar(20));
        btnBuscar.setToolTipText("Buscar contacto");
        btnBuscar.setBackground(Color.WHITE);
        btnBuscar.setFocusPainted(false); // Sin borde de foco
        btnBuscar.setBorderPainted(true); // Con borde
        btnBuscar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1)); // Borde gris
        btnBuscar.setPreferredSize(new Dimension(40, 28));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de manita
        // Asigna la acción de búsqueda
        btnBuscar.addActionListener(e -> buscarContacto());
        panelBusqueda.add(btnBuscar);

        // Agrega el panel de búsqueda al panel superior
        panelSuperior.add(panelBusqueda, BorderLayout.NORTH);
        // Agrega el panel superior al panel principal
        add(panelSuperior, BorderLayout.NORTH);

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

        // Agrega funcionalidad de doble clic para ver detalles
        tablaContactos.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Detecta doble clic en la tabla.
             */
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Si se hizo doble clic
                if (e.getClickCount() == 2) {
                    // Abre el diálogo de detalle del contacto
                    verDetalleContacto();
                }
            }
        });

        // Envuelve la tabla en un JScrollPane para permitir desplazamiento
        JScrollPane scrollPane = new JScrollPane(tablaContactos);
        // Agrega el scrollPane al centro del panel
        add(scrollPane, BorderLayout.CENTER);

        // Carga inicialmente todos los contactos
        refrescar();
    }

    /**
     * Método público que refresca el contenido de la tabla.
     * Si hay texto en el campo de búsqueda, aplica el filtro; de lo contrario, muestra todos.
     */
    public void refrescar() {
        // Obtiene el texto actual del campo de búsqueda
        String textoBusqueda = txtBuscar.getText().trim();

        // Si hay criterio de búsqueda
        if (!textoBusqueda.isEmpty()) {
            // Aplica el filtro de búsqueda
            buscarContacto();
        } else {
            // Muestra todos los contactos
            cargarTodosLosContactos();
        }
    }

    /**
     * Método privado que carga todos los contactos en la tabla sin filtros.
     */
    private void cargarTodosLosContactos() {
        // Limpia todas las filas de la tabla
        modeloTabla.setRowCount(0);
        // Obtiene la lista completa de contactos de la empresa
        ArrayList<Contacto> contactos = empresa.listarContactos();

        // Itera sobre cada contacto
        for (Contacto c : contactos) {
            // Agrega una fila con el email y nombre del contacto
            modeloTabla.addRow(new Object[]{
                    c.getEmail(),
                    c.getNombre()
            });
        }
    }

    /**
     * Método público que abre el diálogo para agregar un nuevo contacto.
     * Refresca la tabla si se guardó un contacto exitosamente.
     */
    public void abrirAgregarContacto() {
        // Crea el diálogo de contacto sin contacto existente (modo creación)
        DialogoContacto dialogo = new DialogoContacto(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre
                empresa,
                null); // null indica que es un nuevo contacto
        // Muestra el diálogo
        dialogo.setVisible(true);

        // Si se guardó el contacto exitosamente
        if (dialogo.isGuardado()) {
            // Refresca la tabla para mostrar el nuevo contacto
            refrescar();
        }
    }

    /**
     * Método privado que busca contactos según el criterio ingresado.
     * Filtra por email o nombre del contacto.
     */
    private void buscarContacto() {
        // Obtiene y limpia el criterio de búsqueda
        String criterio = txtBuscar.getText().trim();

        // Si el criterio está vacío
        if (criterio.isEmpty()) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Ingrese un correo o nombre para buscar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Limpia todas las filas de la tabla
        modeloTabla.setRowCount(0);
        // Obtiene todos los contactos
        ArrayList<Contacto> contactos = empresa.listarContactos();
        // Bandera para controlar si se encontró al menos un resultado
        boolean encontrado = false;

        // Convierte el criterio a minúsculas para búsqueda insensible a mayúsculas
        String criterioBusqueda = criterio.toLowerCase();

        // Itera sobre cada contacto
        for (Contacto c : contactos) {
            // Verifica si el email contiene el criterio
            boolean coincideEmail = c.getEmail().toLowerCase().contains(criterioBusqueda);
            // Verifica si el nombre contiene el criterio (si existe nombre)
            boolean coincideNombre = c.getNombre() != null &&
                    c.getNombre().toLowerCase().contains(criterioBusqueda);

            // Si coincide en email o nombre
            if (coincideEmail || coincideNombre) {
                // Agrega el contacto a la tabla
                modeloTabla.addRow(new Object[]{
                        c.getEmail(),
                        c.getNombre()
                });
                // Marca que se encontró al menos un resultado
                encontrado = true;
            }
        }

        // Si no se encontraron resultados
        if (!encontrado) {
            // Muestra mensaje informativo
            JOptionPane.showMessageDialog(this,
                    "No se encontraron contactos con el criterio: " + criterio,
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            // Limpia el campo de búsqueda
            txtBuscar.setText("");
            // Recarga todos los contactos
            cargarTodosLosContactos();
        }
    }

    /**
     * Método privado que abre el diálogo de detalle del contacto seleccionado.
     * Permite ver y editar la información completa del contacto.
     */
    private void verDetalleContacto() {
        // Obtiene el índice de la fila seleccionada
        int filaSeleccionada = tablaContactos.getSelectedRow();

        // Si no hay ninguna fila seleccionada
        if (filaSeleccionada == -1) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Seleccione un contacto para ver sus detalles",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Obtiene el email del contacto de la primera columna de la fila seleccionada
        String email = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        // Busca el contacto completo en la empresa usando el email
        Contacto contacto = empresa.buscarContactoPorEmail(email);

        // Si se encontró el contacto
        if (contacto != null) {
            // Crea el diálogo de detalle del contacto
            DialogoDetalleContacto dialogo = new DialogoDetalleContacto(
                    (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre
                    empresa,
                    contacto);
            // Muestra el diálogo
            dialogo.setVisible(true);

            // Si se modificó el contacto
            if (dialogo.isModificado()) {
                // Refresca la tabla para mostrar los cambios
                refrescar();
            }
        } else {
            // Si no se pudo encontrar el contacto, muestra error
            JOptionPane.showMessageDialog(this,
                    "No se pudo encontrar el contacto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}