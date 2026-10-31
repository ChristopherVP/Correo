package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Clase DialogoGestionAdmins
 *
 * Esta clase representa un diálogo modal para gestionar los administradores de un grupo.
 * Permite a los administradores actuales:
 * - Ver la lista de todos los miembros del grupo con su rol (Creador, Administrador, Miembro)
 * - Promover miembros regulares a administradores
 * - Quitar el rol de administrador a administradores existentes (excepto al creador)
 *
 * Solo los administradores del grupo tienen acceso a las funciones de modificación.
 *
 * Extiende de JDialog para crear una ventana modal independiente.
 */
public class DialogoGestionAdmins extends JDialog {
    // Referencia a la empresa de correo que contiene toda la información del sistema
    private EmpresaCorreo empresa;

    // Grupo del cual se gestionarán los administradores
    private Grupo grupo;

    // Bandera que indica si el usuario actual es administrador del grupo
    private boolean esAdministrador;

    // Componentes de la interfaz gráfica
    private JTable tablaMiembros; // Tabla que muestra los miembros y sus roles
    private DefaultTableModel modeloMiembros; // Modelo de datos para la tabla
    private JButton btnHacerAdmin; // Botón para hacer administrador a un miembro
    private JButton btnQuitarAdmin; // Botón para quitar rol de administrador
    private JButton btnCerrar; // Botón para cerrar el diálogo

    /**
     * Constructor de DialogoGestionAdmins
     *
     * Inicializa el diálogo con la información del grupo y verifica si el usuario
     * actual tiene permisos de administrador.
     *
     * @param parent Ventana padre (JFrame principal)
     * @param empresa Referencia a la empresa de correo
     * @param grupo Grupo del cual se gestionarán los administradores
     */
    public DialogoGestionAdmins(JFrame parent, EmpresaCorreo empresa, Grupo grupo) {
        // Llama al constructor de JDialog con título dinámico que incluye el nombre del grupo
        super(parent, "Gestión de Administradores: " + grupo.getNombre(), true);

        this.empresa = empresa; // Guarda la referencia a la empresa
        this.grupo = grupo; // Guarda la referencia al grupo

        // Verifica si el usuario actual es administrador del grupo
        this.esAdministrador = grupo.esAdministrador(empresa.getUsuarioActual().getEmail());

        inicializarComponentes(); // Crea la interfaz gráfica
        cargarMiembros(); // Carga los miembros en la tabla

        setSize(700, 500); // Establece el tamaño del diálogo
        setLocationRelativeTo(parent); // Centra el diálogo respecto a la ventana padre
    }

    /**
     * Método inicializarComponentes
     *
     * Crea y configura todos los componentes de la interfaz gráfica del diálogo.
     * Incluye panel de información, tabla de miembros y botones de acción.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL SUPERIOR (Información del Grupo) ==========
        // Panel con información del grupo (3 filas)
        JPanel panelInfo = new JPanel(new GridLayout(3, 1, 5, 5));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15)); // Padding
        panelInfo.setBackground(new Color(240, 248, 255)); // Fondo azul claro

        // Título "Gestión de Administradores"
        JLabel lblTitulo = new JLabel("Gestión de Administradores");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18)); // Fuente grande y en negrita
        lblTitulo.setForeground(new Color(0, 102, 204)); // Color azul
        panelInfo.add(lblTitulo); // Agrega el título

        // Label con el nombre del grupo
        JLabel lblGrupo = new JLabel("Grupo: " + grupo.getNombre());
        lblGrupo.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        panelInfo.add(lblGrupo); // Agrega el label

        // Label con información del creador y cantidad de administradores
        JLabel lblInfo = new JLabel("Creador: " + grupo.getEmailAdministrador() +
                " | Administradores: " + grupo.getCantidadAdministradores());
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 13)); // Fuente normal
        lblInfo.setForeground(Color.GRAY); // Color gris
        panelInfo.add(lblInfo); // Agrega el label

        add(panelInfo, BorderLayout.NORTH); // Agrega el panel al norte del diálogo

        // ========== TABLA DE MIEMBROS ==========
        // Define las columnas de la tabla
        String[] columnas = {"Email", "Nombre", "Rol"};

        // Crea el modelo de tabla con las columnas y 0 filas iniciales
        modeloMiembros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que las celdas no sean editables
            }
        };

        // Crea la tabla con el modelo
        tablaMiembros = new JTable(modeloMiembros);
        tablaMiembros.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente de la tabla
        tablaMiembros.setRowHeight(25); // Altura de las filas
        tablaMiembros.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12)); // Fuente del encabezado
        tablaMiembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable

        // Crea un scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaMiembros);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15)); // Márgenes
        add(scrollPane, BorderLayout.CENTER); // Agrega al centro del diálogo

        // ========== PANEL DE BOTONES ==========
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

        // Solo muestra los botones de acción si el usuario es administrador
        if (esAdministrador) {
            // Botón "Hacer Administrador"
            btnHacerAdmin = new JButton("Hacer Administrador", IconoUtils.iconoAgregar(18));
            btnHacerAdmin.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
            btnHacerAdmin.setBackground(Color.WHITE); // Fondo blanco
            btnHacerAdmin.setForeground(Color.BLACK); // Texto negro
            btnHacerAdmin.setFocusPainted(false); // Sin borde de foco
            btnHacerAdmin.setPreferredSize(new Dimension(220, 40)); // Tamaño del botón
            btnHacerAdmin.addActionListener(e -> hacerAdministrador()); // Acción al hacer clic
            panelBotones.add(btnHacerAdmin); // Agrega el botón

            // Botón "Quitar Administrador"
            btnQuitarAdmin = new JButton("Quitar Administrador", IconoUtils.iconoEliminar(18));
            btnQuitarAdmin.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
            btnQuitarAdmin.setBackground(Color.WHITE); // Fondo blanco
            btnQuitarAdmin.setForeground(Color.BLACK); // Texto negro
            btnQuitarAdmin.setFocusPainted(false); // Sin borde de foco
            btnQuitarAdmin.setPreferredSize(new Dimension(220, 40)); // Tamaño del botón
            btnQuitarAdmin.addActionListener(e -> quitarAdministrador()); // Acción al hacer clic
            panelBotones.add(btnQuitarAdmin); // Agrega el botón
        }

        // Botón "Cerrar" (siempre visible)
        btnCerrar = new JButton("Cerrar", IconoUtils.iconoCerrar(18));
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        btnCerrar.setBackground(Color.WHITE); // Fondo blanco
        btnCerrar.setForeground(Color.BLACK); // Texto negro
        btnCerrar.setFocusPainted(false); // Sin borde de foco
        btnCerrar.setPreferredSize(new Dimension(120, 40)); // Tamaño del botón
        btnCerrar.addActionListener(e -> dispose()); // Acción: cerrar el diálogo
        panelBotones.add(btnCerrar); // Agrega el botón

        add(panelBotones, BorderLayout.SOUTH); // Agrega el panel de botones al sur del diálogo
    }

    /**
     * Método cargarMiembros
     *
     * Carga todos los miembros del grupo en la tabla, mostrando su email,
     * nombre y rol (Creador, Administrador o Miembro).
     */
    private void cargarMiembros() {
        modeloMiembros.setRowCount(0); // Limpia todas las filas existentes de la tabla

        // Obtiene las listas de miembros y administradores del grupo
        ArrayList<Contacto> miembros = grupo.getMiembros();
        ArrayList<String> administradores = grupo.getAdministradores();

        // Itera sobre cada miembro del grupo
        for (Contacto c : miembros) {
            String rol; // Variable para el rol que se mostrará

            // Determina el rol del miembro
            if (c.getEmail().equalsIgnoreCase(grupo.getEmailAdministrador())) {
                // Si es el creador del grupo, tiene un rol permanente especial
                rol = "Creador (Admin permanente)";
            } else if (administradores.stream().anyMatch(a -> a.equalsIgnoreCase(c.getEmail()))) {
                // Si está en la lista de administradores, es administrador
                rol = "Administrador";
            } else {
                // Si no es ninguno de los anteriores, es miembro regular
                rol = "Miembro";
            }

            // Agrega una fila a la tabla con los datos del miembro
            modeloMiembros.addRow(new Object[]{
                    c.getEmail(), // Columna 0: Email
                    c.getNombre(), // Columna 1: Nombre
                    rol // Columna 2: Rol
            });
        }
    }

    /**
     * Método hacerAdministrador
     *
     * Promueve a un miembro regular a administrador del grupo.
     * Realiza múltiples validaciones:
     * - Verifica que el usuario actual sea administrador
     * - Verifica que haya una fila seleccionada
     * - Verifica que el miembro seleccionado no sea ya administrador
     * - Solicita confirmación antes de realizar el cambio
     * - Registra un mensaje de sistema en el chat del grupo
     */
    private void hacerAdministrador() {
        // ========== VALIDACIÓN: Solo administradores ==========
        if (!esAdministrador) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Solo los administradores pueden realizar esta acción",
                    "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // ========== VALIDACIÓN: Debe haber una selección ==========
        int filaSeleccionada = tablaMiembros.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un miembro para hacer administrador",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Obtiene los datos del miembro seleccionado
        String emailSeleccionado = (String) modeloMiembros.getValueAt(filaSeleccionada, 0);
        String nombreSeleccionado = (String) modeloMiembros.getValueAt(filaSeleccionada, 1);

        // ========== VALIDACIÓN: No es ya administrador ==========
        if (grupo.esAdministrador(emailSeleccionado)) {
            JOptionPane.showMessageDialog(this,
                    nombreSeleccionado + " ya es administrador",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return; // Sale del método
        }

        // ========== CONFIRMACIÓN ==========
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea hacer administrador a:\n\n" +
                        nombreSeleccionado + "\n" + emailSeleccionado + "?",
                "Confirmar promoción a administrador",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        // Si el usuario confirma
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Agrega al miembro como administrador
            grupo.agregarAdministrador(emailSeleccionado);

            // Registra un mensaje de sistema en el chat del grupo
            grupo.agregarMensajeSistemaAdminAgregado(
                    empresa.getUsuarioActual().getEmail(), // Email del que hizo la acción
                    empresa.getUsuarioActual().getNombreCompleto(), // Nombre del que hizo la acción
                    emailSeleccionado, // Email del nuevo administrador
                    nombreSeleccionado // Nombre del nuevo administrador
            );

            // Guarda los cambios automáticamente
            empresa.guardarDatosAutomaticamente();

            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    "✓ " + nombreSeleccionado + " ahora es administrador",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarMiembros(); // Recarga la tabla para mostrar el cambio
        }
    }

    /**
     * Método quitarAdministrador
     *
     * Quita el rol de administrador a un administrador existente.
     * Realiza múltiples validaciones:
     * - Verifica que el usuario actual sea administrador
     * - Verifica que haya una fila seleccionada
     * - Verifica que no se intente quitar el rol al creador del grupo
     * - Verifica que el miembro seleccionado sea realmente administrador
     * - Solicita confirmación antes de realizar el cambio
     * - Registra un mensaje de sistema en el chat del grupo
     */
    private void quitarAdministrador() {
        // ========== VALIDACIÓN: Solo administradores ==========
        if (!esAdministrador) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ Solo los administradores pueden realizar esta acción",
                    "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // ========== VALIDACIÓN: Debe haber una selección ==========
        int filaSeleccionada = tablaMiembros.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un administrador para quitar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // Obtiene los datos del miembro seleccionado
        String emailSeleccionado = (String) modeloMiembros.getValueAt(filaSeleccionada, 0);
        String nombreSeleccionado = (String) modeloMiembros.getValueAt(filaSeleccionada, 1);

        // ========== VALIDACIÓN: No se puede quitar al creador ==========
        if (emailSeleccionado.equalsIgnoreCase(grupo.getEmailAdministrador())) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ El CREADOR del grupo siempre será administrador.\n\n" +
                            "No se puede quitar este rol al creador del grupo.",
                    "Operación no permitida",
                    JOptionPane.WARNING_MESSAGE);
            return; // Sale del método
        }

        // ========== VALIDACIÓN: Debe ser administrador ==========
        if (!grupo.esAdministrador(emailSeleccionado)) {
            JOptionPane.showMessageDialog(this,
                    nombreSeleccionado + " no es administrador",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return; // Sale del método
        }

        // ========== CONFIRMACIÓN ==========
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea quitar a " + nombreSeleccionado + " como administrador?",
                "Confirmar remoción de administrador",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        // Si el usuario confirma
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Quita el rol de administrador
            grupo.quitarAdministrador(emailSeleccionado);

            // Registra un mensaje de sistema en el chat del grupo
            grupo.agregarMensajeSistemaAdminQuitado(
                    empresa.getUsuarioActual().getEmail(), // Email del que hizo la acción
                    empresa.getUsuarioActual().getNombreCompleto(), // Nombre del que hizo la acción
                    emailSeleccionado, // Email del que dejó de ser administrador
                    nombreSeleccionado // Nombre del que dejó de ser administrador
            );

            // Guarda los cambios automáticamente
            empresa.guardarDatosAutomaticamente();

            // Muestra mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    "✓ " + nombreSeleccionado + " ya no es administrador",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarMiembros(); // Recarga la tabla para mostrar el cambio
        }
    }
}