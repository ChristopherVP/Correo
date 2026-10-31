package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.Grupo;
import uce.edu.ec.correo.dominio.Contacto;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase DialogoMiembrosGrupo
 *
 * Esta clase representa un diálogo modal que muestra la lista completa
 * de miembros de un grupo. Permite visualizar:
 * - Email de cada miembro
 * - Nombre del miembro
 * - Estado del miembro respecto al usuario actual:
 *   * "✓ Tú" - Si es el usuario actual
 *   * "En contactos" - Si el miembro está en la lista de contactos del usuario
 *   * "No agregado" - Si el miembro no está en los contactos del usuario
 *
 * Es un diálogo de solo lectura, no permite modificar los miembros.
 *
 * Extiende de JDialog para crear una ventana modal independiente.
 */
public class DialogoMiembrosGrupo extends JDialog {
    // Referencia a la empresa de correo que contiene toda la información del sistema
    private EmpresaCorreo empresa;

    // Grupo del cual se mostrarán los miembros
    private Grupo grupo;

    // Componentes de la interfaz gráfica
    private JLabel lblNombreGrupo; // Label que muestra el nombre del grupo
    private JLabel lblCantidadMiembros; // Label que muestra la cantidad de miembros
    private JTable tablaMiembros; // Tabla que muestra la lista de miembros
    private DefaultTableModel modeloTabla; // Modelo de datos para la tabla
    private JButton btnCerrar; // Botón para cerrar el diálogo

    /**
     * Constructor de DialogoMiembrosGrupo
     *
     * Inicializa el diálogo con la información del grupo y carga los miembros.
     *
     * @param parent Ventana padre (JFrame principal)
     * @param empresa Referencia a la empresa de correo
     * @param grupo Grupo del cual se mostrarán los miembros
     */
    public DialogoMiembrosGrupo(JFrame parent, EmpresaCorreo empresa, Grupo grupo) {
        // Llama al constructor de JDialog con título fijo y modo modal
        super(parent, "Miembros del Grupo", true);

        this.empresa = empresa; // Guarda la referencia a la empresa
        this.grupo = grupo; // Guarda la referencia al grupo

        inicializarComponentes(); // Crea la interfaz gráfica
        cargarDatos(); // Carga los datos del grupo y sus miembros

        setSize(650, 450); // Establece el tamaño del diálogo
        setLocationRelativeTo(parent); // Centra el diálogo respecto a la ventana padre
    }

    /**
     * Método inicializarComponentes
     *
     * Crea y configura todos los componentes de la interfaz gráfica del diálogo.
     * Incluye panel de información superior, tabla de miembros y botón de cierre.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL SUPERIOR (Información del Grupo) ==========
        // Panel con 2 filas de información
        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15)); // Padding
        panelSuperior.setBackground(new Color(240, 248, 255)); // Fondo azul claro

        // Label para el nombre del grupo (se llenará en cargarDatos)
        lblNombreGrupo = new JLabel();
        lblNombreGrupo.setFont(new Font("Arial", Font.BOLD, 16)); // Fuente grande y en negrita
        lblNombreGrupo.setForeground(new Color(0, 102, 204)); // Color azul
        panelSuperior.add(lblNombreGrupo); // Agrega el label

        // Label para la cantidad de miembros (se llenará en cargarDatos)
        lblCantidadMiembros = new JLabel();
        lblCantidadMiembros.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        lblCantidadMiembros.setForeground(new Color(100, 100, 100)); // Color gris oscuro
        panelSuperior.add(lblCantidadMiembros); // Agrega el label

        add(panelSuperior, BorderLayout.NORTH); // Agrega el panel al norte del diálogo

        // ========== TABLA DE MIEMBROS ==========
        // Define las columnas de la tabla
        String[] columnas = {"Email", "Nombre", "Estado"};

        // Crea el modelo de tabla con las columnas y 0 filas iniciales
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que las celdas no sean editables
            }
        };

        // Crea la tabla con el modelo
        tablaMiembros = new JTable(modeloTabla);
        tablaMiembros.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente de la tabla
        tablaMiembros.setRowHeight(28); // Altura de las filas
        tablaMiembros.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); // Fuente del encabezado
        tablaMiembros.getTableHeader().setBackground(new Color(240, 240, 240)); // Fondo del encabezado
        tablaMiembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable

        // ========== AJUSTE DE ANCHOS DE COLUMNAS ==========
        tablaMiembros.getColumnModel().getColumn(0).setPreferredWidth(200);  // Email - 200px
        tablaMiembros.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre - 150px
        tablaMiembros.getColumnModel().getColumn(2).setPreferredWidth(150);  // Estado - 150px

        // Crea un scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaMiembros);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15)); // Márgenes
        add(scrollPane, BorderLayout.CENTER); // Agrega al centro del diálogo

        // ========== PANEL DE BOTONES ==========
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Botón "Cerrar"
        btnCerrar = new JButton("Cerrar", IconoUtils.iconoCerrar(18));
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        btnCerrar.setBackground(Color.WHITE); // Fondo blanco
        btnCerrar.setForeground(Color.BLACK); // Texto negro
        btnCerrar.setFocusPainted(false); // Sin borde de foco
        btnCerrar.setPreferredSize(new Dimension(120, 35)); // Tamaño del botón
        btnCerrar.addActionListener(e -> dispose()); // Acción: cerrar el diálogo
        panelBotones.add(btnCerrar); // Agrega el botón

        add(panelBotones, BorderLayout.SOUTH); // Agrega el panel de botones al sur del diálogo
    }

    /**
     * Método cargarDatos
     *
     * Carga los datos del grupo (nombre y cantidad de miembros) y
     * llena la tabla con la información de cada miembro.
     *
     * Determina el estado de cada miembro respecto al usuario actual:
     * - Si es el propio usuario: "✓ Tú"
     * - Si está en los contactos del usuario: "En contactos"
     * - Si no está en los contactos: "No agregado"
     */
    private void cargarDatos() {
        // Verifica que haya un grupo válido
        if (grupo != null) {
            // Establece el texto del label con el nombre del grupo
            lblNombreGrupo.setText("Grupo: " + grupo.getNombre());

            // Establece el texto del label con la cantidad de miembros
            lblCantidadMiembros.setText("Total de miembros: " + grupo.getCantidadMiembros());

            modeloTabla.setRowCount(0); // Limpia todas las filas existentes de la tabla

            // Obtiene la lista de miembros del grupo
            ArrayList<Contacto> miembros = grupo.getMiembros();

            // ========== CASO: Grupo sin miembros ==========
            if (miembros.isEmpty()) {
                // Crea un panel con mensaje de que no hay miembros
                JPanel panelVacio = new JPanel(new GridBagLayout());
                JLabel lblVacio = new JLabel("Este grupo no tiene miembros");
                lblVacio.setFont(new Font("Arial", Font.ITALIC, 14)); // Fuente en cursiva
                lblVacio.setForeground(Color.GRAY); // Color gris
                panelVacio.add(lblVacio); // Agrega el mensaje
                return; // Sale del método
            }

            // Obtiene el email del usuario actual
            String emailUsuarioActual = empresa.getUsuarioActual().getEmail();

            // ========== ITERA SOBRE CADA MIEMBRO DEL GRUPO ==========
            for (Contacto miembro : miembros) {
                String emailMiembro = miembro.getEmail(); // Email del miembro actual
                String nombreMostrar; // Variable para el nombre a mostrar
                String estado; // Variable para el estado a mostrar

                // ========== CASO 1: Es el usuario actual ==========
                if (emailMiembro.equalsIgnoreCase(emailUsuarioActual)) {
                    nombreMostrar = "Tú"; // Muestra "Tú" en lugar del nombre
                    estado = "✓ Tú"; // Estado con checkmark
                }
                // ========== CASO 2: Es otro usuario ==========
                else {
                    // Busca si el miembro está en la lista de contactos del usuario
                    Contacto contactoEnMisContactos = empresa.buscarContactoPorEmail(emailMiembro);

                    // Si el miembro está en contactos y tiene nombre registrado
                    if (contactoEnMisContactos != null &&
                            contactoEnMisContactos.getNombre() != null &&
                            !contactoEnMisContactos.getNombre().trim().isEmpty()) {
                        nombreMostrar = contactoEnMisContactos.getNombre(); // Muestra el nombre del contacto
                        estado = "En contactos"; // Estado: está en contactos
                    }
                    // Si el miembro NO está en contactos
                    else {
                        nombreMostrar = "—"; // Muestra un guion (sin nombre)
                        estado = "No agregado"; // Estado: no está en contactos
                    }
                }

                // ========== AGREGA UNA FILA A LA TABLA ==========
                modeloTabla.addRow(new Object[]{
                        emailMiembro,    // Columna 0: Email del miembro
                        nombreMostrar,   // Columna 1: Nombre a mostrar
                        estado           // Columna 2: Estado del miembro
                });
            }
        }
    }
}