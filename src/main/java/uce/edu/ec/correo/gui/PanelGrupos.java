package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase que representa el panel de gestión de grupos.
 * Permite visualizar, buscar y gestionar los grupos del usuario.
 * Incluye funcionalidad para abrir chats grupales mediante doble clic.
 */
public class PanelGrupos extends JPanel {
    // Referencia a la empresa para acceder a los grupos
    private EmpresaCorreo empresa;
    // Referencia al panel de contactos (puede usarse para operaciones relacionadas)
    private PanelContactos panelContactos;

    // Componentes de la interfaz de usuario
    private JTable tablaGrupos; // Tabla que muestra los grupos
    private DefaultTableModel modeloTabla; // Modelo de datos de la tabla
    private JTextField txtBuscar; // Campo de texto para búsqueda
    private JButton btnBuscar; // Botón para ejecutar la búsqueda

    /**
     * Constructor del panel de grupos.
     * @param empresa Referencia a la empresa de correo
     * @param panelContactos Referencia al panel de contactos
     */
    public PanelGrupos(EmpresaCorreo empresa, PanelContactos panelContactos) {
        // Guarda la referencia a la empresa
        this.empresa = empresa;
        // Guarda la referencia al panel de contactos
        this.panelContactos = panelContactos;
        // Inicializa todos los componentes visuales
        inicializarComponentes();
    }

    /**
     * Método que inicializa y configura todos los componentes visuales del panel.
     * Crea el panel de búsqueda y la tabla de grupos con información detallada.
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
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar por nombre o descripción"));

        // Etiqueta "Buscar:"
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBuscar.setForeground(Color.BLACK);
        panelBusqueda.add(lblBuscar);

        // Campo de texto para búsqueda
        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        txtBuscar.setToolTipText("Ingrese nombre o descripción del grupo"); // Texto de ayuda
        // Permite buscar al presionar Enter
        txtBuscar.addActionListener(e -> buscarGrupo());
        panelBusqueda.add(txtBuscar);

        // Botón de búsqueda con ícono
        btnBuscar = new JButton(IconoUtils.iconoBuscar(20));
        btnBuscar.setToolTipText("Buscar grupo");
        btnBuscar.setBackground(Color.WHITE);
        btnBuscar.setFocusPainted(false); // Sin borde de foco
        btnBuscar.setBorderPainted(true); // Con borde
        btnBuscar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1)); // Borde gris
        btnBuscar.setPreferredSize(new Dimension(40, 28));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de manita
        // Asigna la acción de búsqueda
        btnBuscar.addActionListener(e -> buscarGrupo());
        panelBusqueda.add(btnBuscar);

        // Agrega el panel de búsqueda al panel superior
        panelSuperior.add(panelBusqueda, BorderLayout.NORTH);
        // Agrega el panel superior al panel principal
        add(panelSuperior, BorderLayout.NORTH);

        // ===== TABLA DE GRUPOS =====
        // Define las columnas de la tabla
        String[] columnas = {"Nombre", "Descripción", "Miembros", "Rol", "Admins"};
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
        tablaGrupos = new JTable(modeloTabla);
        tablaGrupos.setFont(new Font("Arial", Font.PLAIN, 12));
        tablaGrupos.setRowHeight(25); // Altura de las filas
        tablaGrupos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); // Fuente del encabezado
        tablaGrupos.getTableHeader().setBackground(new Color(240, 240, 240)); // Color de fondo del encabezado
        tablaGrupos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable

        // Configura el ancho de las columnas
        tablaGrupos.getColumnModel().getColumn(0).setPreferredWidth(150); // Nombre
        tablaGrupos.getColumnModel().getColumn(1).setPreferredWidth(200); // Descripción
        tablaGrupos.getColumnModel().getColumn(2).setPreferredWidth(80);  // Miembros
        tablaGrupos.getColumnModel().getColumn(3).setPreferredWidth(100); // Rol
        tablaGrupos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Admins

        // Agrega funcionalidad de doble clic para abrir el chat
        tablaGrupos.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Detecta doble clic en la tabla.
             */
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Si se hizo doble clic
                if (e.getClickCount() == 2) {
                    // Abre el chat del grupo seleccionado
                    abrirChat();
                }
            }
        });

        // Envuelve la tabla en un JScrollPane para permitir desplazamiento
        JScrollPane scrollPane = new JScrollPane(tablaGrupos);
        // Siempre muestra la barra de desplazamiento vertical
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // Muestra la barra horizontal solo cuando sea necesario
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Agrega el scrollPane al centro del panel
        add(scrollPane, BorderLayout.CENTER);

        // Carga inicialmente todos los grupos
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
            buscarGrupo();
        } else {
            // Muestra todos los grupos
            cargarTodosLosGrupos();
        }
    }

    /**
     * Método privado que carga todos los grupos en la tabla sin filtros.
     * Muestra el rol del usuario en cada grupo (Creador o Miembro).
     */
    private void cargarTodosLosGrupos() {
        // Limpia todas las filas de la tabla
        modeloTabla.setRowCount(0);

        // ✅ Verificar que hay un usuario logueado antes de acceder
        // Si no hay usuario logueado, no intenta cargar grupos
        if (!empresa.hayUsuarioLogueado()) {
            return; // Sale del método
        }

        // Obtiene la lista completa de grupos de la empresa
        ArrayList<Grupo> grupos = empresa.listarTodosLosGrupos();
        // Obtiene el email del usuario actual para determinar su rol
        String emailUsuarioActual = empresa.getUsuarioActual().getEmail();

        // Itera sobre cada grupo
        for (Grupo g : grupos) {
            // Determina el rol del usuario en el grupo
            // Si es administrador muestra "Creador", si no, muestra "Miembro"
            String rol = g.esAdministrador(emailUsuarioActual) ? "Creador" : "Miembro";

            // Agrega una fila con la información del grupo
            modeloTabla.addRow(new Object[]{
                    g.getNombre(),                                      // Columna 0: Nombre del grupo
                    g.getDescripcion(),                                 // Columna 1: Descripción
                    g.getCantidadMiembros() + " miembro(s)",           // Columna 2: Cantidad de miembros
                    rol,                                                // Columna 3: Rol del usuario
                    g.getCantidadAdministradores() + " admin(s)"       // Columna 4: Cantidad de administradores
            });
        }
    }

    /**
     * Método público que abre el diálogo para crear un nuevo grupo.
     * Refresca la tabla si se guardó el grupo exitosamente.
     */
    public void abrirCrearGrupo() {
        // ✅ Verificar que hay un usuario logueado
        // Si no hay usuario logueado, muestra error y sale
        if (!empresa.hayUsuarioLogueado()) {
            JOptionPane.showMessageDialog(this,
                    "Debe iniciar sesión primero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return; // Sale del método
        }

        // Obtiene la lista de contactos disponibles para agregar al grupo
        ArrayList<Contacto> contactosDisponibles = empresa.listarContactos();

        // Crea el diálogo de grupo sin grupo existente (modo creación)
        DialogoGrupo dialogo = new DialogoGrupo(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre
                empresa,
                null, // null indica que es un nuevo grupo
                contactosDisponibles);
        // Muestra el diálogo
        dialogo.setVisible(true);

        // Si se guardó el grupo exitosamente
        if (dialogo.isGuardado()) {
            // Refresca la tabla para mostrar el nuevo grupo
            refrescar();
        }
    }

    /**
     * Método privado que busca grupos según el criterio ingresado.
     * Filtra por nombre o descripción del grupo.
     */
    private void buscarGrupo() {
        // Obtiene y limpia el criterio de búsqueda
        String criterio = txtBuscar.getText().trim();

        // Si el criterio está vacío
        if (criterio.isEmpty()) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Ingrese un nombre o descripción para buscar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // ✅ Verificar que hay un usuario logueado
        // Si no hay usuario logueado, muestra error y sale
        if (!empresa.hayUsuarioLogueado()) {
            JOptionPane.showMessageDialog(this,
                    "Debe iniciar sesión primero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return; // Sale del método
        }

        // Limpia todas las filas de la tabla
        modeloTabla.setRowCount(0);
        // Obtiene todos los grupos
        ArrayList<Grupo> grupos = empresa.listarTodosLosGrupos();
        // Obtiene el email del usuario actual
        String emailUsuarioActual = empresa.getUsuarioActual().getEmail();
        // Bandera para controlar si se encontró al menos un resultado
        boolean encontrado = false;

        // Convierte el criterio a minúsculas para búsqueda insensible a mayúsculas
        String criterioBusqueda = criterio.toLowerCase();
        // Itera sobre cada grupo
        for (Grupo g : grupos) {
            // Verifica si el nombre contiene el criterio
            boolean coincideNombre = g.getNombre().toLowerCase().contains(criterioBusqueda);
            // Verifica si la descripción contiene el criterio (si existe descripción)
            boolean coincideDescripcion = g.getDescripcion() != null &&
                    g.getDescripcion().toLowerCase().contains(criterioBusqueda);

            // Si coincide en nombre o descripción
            if (coincideNombre || coincideDescripcion) {
                // Determina el rol del usuario en el grupo
                String rol = g.esAdministrador(emailUsuarioActual) ? "Creador" : "Miembro";
                // Agrega el grupo a la tabla
                modeloTabla.addRow(new Object[]{
                        g.getNombre(),
                        g.getDescripcion(),
                        g.getCantidadMiembros() + " miembro(s)",
                        rol,
                        g.getCantidadAdministradores() + " admin(s)"
                });
                // Marca que se encontró al menos un resultado
                encontrado = true;
            }
        }

        // Si no se encontraron resultados
        if (!encontrado) {
            // Muestra mensaje informativo
            JOptionPane.showMessageDialog(this,
                    "No se encontraron grupos con el criterio: " + criterio,
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            // Limpia el campo de búsqueda
            txtBuscar.setText("");
            // Recarga todos los grupos
            cargarTodosLosGrupos();
        }
    }

    /**
     * Método privado que abre el chat del grupo seleccionado.
     * Permite al usuario participar en la conversación grupal.
     */
    private void abrirChat() {
        // Obtiene el índice de la fila seleccionada
        int filaSeleccionada = tablaGrupos.getSelectedRow();

        // Si no hay ninguna fila seleccionada
        if (filaSeleccionada == -1) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "Seleccione un grupo para abrir el chat",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // ✅ Verificar que hay un usuario logueado
        // Si no hay usuario logueado, muestra error y sale
        if (!empresa.hayUsuarioLogueado()) {
            JOptionPane.showMessageDialog(this,
                    "Debe iniciar sesión primero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return; // Sale del método
        }

        // Obtiene el nombre del grupo de la primera columna de la fila seleccionada
        String nombreGrupo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        // Busca el grupo completo en la empresa usando el nombre
        Grupo grupo = empresa.buscarGrupoPorNombre(nombreGrupo);

        // Si se encontró el grupo
        if (grupo != null) {
            // Crea el diálogo de chat grupal
            DialogoChatGrupal dialogo = new DialogoChatGrupal(
                    (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre
                    empresa,
                    grupo,
                    this); // Pasa referencia a este panel para refrescar después
            // Muestra el diálogo
            dialogo.setVisible(true);
            // Refresca la tabla después de cerrar el chat (puede haber cambios)
            refrescar();
        } else {
            // Si no se pudo encontrar el grupo, muestra error
            JOptionPane.showMessageDialog(this,
                    "No se pudo encontrar el grupo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}