package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.ValidationException;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Diálogo modal para crear o modificar grupos de contactos.
 * Permite agregar/quitar miembros y gestionar información del grupo.
 */
public class DialogoGrupo extends JDialog {
    // ========== ATRIBUTOS DEL DIÁLOGO ==========

    // Referencia a la empresa para acceder a datos y operaciones
    private EmpresaCorreo empresa;
    // Grupo que se está editando (null si se está creando uno nuevo)
    private Grupo grupo;
    // Nombre original del grupo (para detectar cambios)
    private String nombreOriginal;
    // Descripción original del grupo (para detectar cambios)
    private String descripcionOriginal;
    // Lista de contactos disponibles para agregar al grupo
    private ArrayList<Contacto> contactosDisponibles;
    // Indica si se guardaron los cambios exitosamente
    private boolean guardado = false;
    // Indica si el usuario actual es administrador del grupo
    private boolean esAdministrador = true;
    // Lista temporal de miembros durante la edición
    private ArrayList<Contacto> miembrosTemporales;
    // Lista original de miembros (para detectar cambios)
    private ArrayList<Contacto> miembrosOriginales;

    // ========== COMPONENTES DE LA INTERFAZ ==========

    // Campo de texto para el nombre del grupo
    private JTextField txtNombre;
    // Campo de texto para la descripción del grupo
    private JTextField txtDescripcion;
    // Tabla para mostrar los miembros del grupo
    private JTable tablaMiembros;
    // Modelo de datos para la tabla de miembros
    private DefaultTableModel modeloMiembros;
    // Botón para agregar un nuevo miembro al grupo
    private JButton btnAgregarMiembro;
    // Botón para quitar un miembro del grupo
    private JButton btnQuitarMiembro;
    // Botón para guardar los cambios
    private JButton btnGuardar;
    // Botón para cancelar y cerrar el diálogo
    private JButton btnCancelar;

    /**
     * Constructor del diálogo de grupo.
     * @param parent Ventana padre
     * @param empresa Instancia de EmpresaCorreo
     * @param grupo Grupo a editar (null para crear nuevo)
     * @param contactosDisponibles Lista de contactos disponibles para agregar
     */
    public DialogoGrupo(JFrame parent, EmpresaCorreo empresa, Grupo grupo,
                        ArrayList<Contacto> contactosDisponibles) {
        // Llama al constructor de JDialog con título dinámico según si es crear o modificar
        super(parent, grupo == null ? "Crear Grupo" : "Modificar Grupo", true);
        // Almacena la referencia a la empresa
        this.empresa = empresa;
        // Almacena el grupo (puede ser null si es creación)
        this.grupo = grupo;
        // Guarda el nombre original (null si es nuevo grupo)
        this.nombreOriginal = grupo != null ? grupo.getNombre() : null;
        // Guarda la descripción original (null si es nuevo grupo)
        this.descripcionOriginal = grupo != null ? grupo.getDescripcion() : null;
        // Almacena la lista de contactos disponibles
        this.contactosDisponibles = contactosDisponibles;
        // Inicializa la lista temporal de miembros vacía
        this.miembrosTemporales = new ArrayList<>();
        // Inicializa la lista original de miembros vacía
        this.miembrosOriginales = new ArrayList<>();

        // Si existe un grupo (modo edición)
        if (grupo != null) {
            // Verifica si el usuario actual es administrador del grupo
            this.esAdministrador = grupo.esAdministrador(empresa.getUsuarioActual().getEmail());
        }

        // Inicializa los componentes gráficos
        inicializarComponentes();
        // Carga los datos del grupo (si existe)
        cargarDatos();
        // Establece el tamaño del diálogo
        setSize(600, 500);
        // Centra el diálogo respecto a la ventana padre
        setLocationRelativeTo(parent);
    }

    /**
     * Inicializa y configura todos los componentes gráficos del diálogo.
     */
    private void inicializarComponentes() {
        // Establece BorderLayout con espaciado de 10 píxeles
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL DE DATOS DEL GRUPO ==========

        // Crea panel con GridBagLayout para organizar los campos
        JPanel panelDatos = new JPanel(new GridBagLayout());
        // Añade borde con título "Datos del Grupo"
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos del Grupo"));
        // Crea objeto GridBagConstraints para posicionar componentes
        GridBagConstraints gbc = new GridBagConstraints();
        // Establece márgenes internos de 8 píxeles
        gbc.insets = new Insets(8, 8, 8, 8);
        // Componentes se expanden horizontalmente
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Alineación a la izquierda (oeste)
        gbc.anchor = GridBagConstraints.WEST;

        // ========== CAMPO NOMBRE ==========

        // Posiciona en columna 0, fila 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        // Crea label para el campo nombre (con asterisco de obligatorio)
        JLabel lblNombre = new JLabel("Nombre: *");
        // Establece fuente en negrita
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label al panel
        panelDatos.add(lblNombre, gbc);

        // Posiciona en columna 1, fila 0
        gbc.gridx = 1;
        // Permite que el componente se expanda horizontalmente
        gbc.weightx = 1.0;
        // Crea campo de texto con 20 columnas
        txtNombre = new JTextField(20);
        // Establece fuente normal
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 14));
        // Si no es administrador
        if (!esAdministrador) {
            // Hace el campo no editable
            txtNombre.setEditable(false);
            // Cambia el color de fondo a gris claro
            txtNombre.setBackground(new Color(240, 240, 240));
        }
        // Añade el campo de texto al panel
        panelDatos.add(txtNombre, gbc);

        // ========== CAMPO DESCRIPCIÓN ==========

        // Posiciona en columna 0, fila 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        // Resetea weightx para el label
        gbc.weightx = 0;
        // Crea label para el campo descripción
        JLabel lblDescripcion = new JLabel("Descripción: *");
        // Establece fuente en negrita
        lblDescripcion.setFont(new Font("Arial", Font.BOLD, 14));
        // Añade el label al panel
        panelDatos.add(lblDescripcion, gbc);

        // Posiciona en columna 1, fila 1
        gbc.gridx = 1;
        // Permite expansión horizontal
        gbc.weightx = 1.0;
        // Crea campo de texto para descripción
        txtDescripcion = new JTextField(20);
        // Establece fuente normal
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 14));
        // Si no es administrador
        if (!esAdministrador) {
            // Hace el campo no editable
            txtDescripcion.setEditable(false);
            // Cambia el color de fondo a gris claro
            txtDescripcion.setBackground(new Color(240, 240, 240));
        }
        // Añade el campo al panel
        panelDatos.add(txtDescripcion, gbc);

        // Añade el panel de datos en la parte superior del diálogo
        add(panelDatos, BorderLayout.NORTH);

        // ========== PANEL DE MIEMBROS ==========

        // Crea panel con BorderLayout y espaciado
        JPanel panelMiembros = new JPanel(new BorderLayout(5, 5));
        // Añade borde con título "Miembros del Grupo"
        panelMiembros.setBorder(BorderFactory.createTitledBorder("Miembros del Grupo"));

        // Define las columnas de la tabla
        String[] columnas = {"Email", "Nombre"};
        // Crea modelo de tabla con las columnas definidas
        modeloMiembros = new DefaultTableModel(columnas, 0) {
            /**
             * Hace que ninguna celda sea editable directamente.
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                // Retorna false para todas las celdas
                return false;
            }
        };

        // Crea tabla con el modelo definido
        tablaMiembros = new JTable(modeloMiembros);
        // Establece fuente para el contenido
        tablaMiembros.setFont(new Font("Arial", Font.PLAIN, 12));
        // Establece altura de las filas en 25 píxeles
        tablaMiembros.setRowHeight(25);
        // Establece fuente en negrita para los encabezados
        tablaMiembros.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        // Permite seleccionar solo una fila a la vez
        tablaMiembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Crea scroll pane para la tabla
        JScrollPane scrollMiembros = new JScrollPane(tablaMiembros);
        // Añade la tabla al centro del panel de miembros
        panelMiembros.add(scrollMiembros, BorderLayout.CENTER);

        // Si el usuario es administrador
        if (esAdministrador) {
            // Crea panel para los botones de gestión de miembros
            JPanel panelBotonesMiembros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

            // ========== BOTÓN AGREGAR MIEMBRO ==========

            // Crea botón con texto e icono
            btnAgregarMiembro = new JButton("Agregar Miembro", IconoUtils.iconoAgregar(18));
            // Establece fuente
            btnAgregarMiembro.setFont(new Font("Arial", Font.PLAIN, 12));
            // Fondo blanco
            btnAgregarMiembro.setBackground(Color.WHITE);
            // Texto negro
            btnAgregarMiembro.setForeground(Color.BLACK);
            // Desactiva el borde de enfoque
            btnAgregarMiembro.setFocusPainted(false);
            // Añade listener que ejecuta agregarMiembro() al hacer clic
            btnAgregarMiembro.addActionListener(e -> agregarMiembro());
            // Añade el botón al panel
            panelBotonesMiembros.add(btnAgregarMiembro);

            // ========== BOTÓN QUITAR MIEMBRO ==========

            // Crea botón con texto e icono de eliminar
            btnQuitarMiembro = new JButton("Quitar Miembro", IconoUtils.iconoEliminar(18));
            // Establece fuente
            btnQuitarMiembro.setFont(new Font("Arial", Font.PLAIN, 12));
            // Fondo blanco
            btnQuitarMiembro.setBackground(Color.WHITE);
            // Texto negro
            btnQuitarMiembro.setForeground(Color.BLACK);
            // Desactiva el borde de enfoque
            btnQuitarMiembro.setFocusPainted(false);
            // Añade listener que ejecuta quitarMiembro() al hacer clic
            btnQuitarMiembro.addActionListener(e -> quitarMiembro());
            // Añade el botón al panel
            panelBotonesMiembros.add(btnQuitarMiembro);

            // Añade el panel de botones en la parte inferior del panel de miembros
            panelMiembros.add(panelBotonesMiembros, BorderLayout.SOUTH);
        }

        // Añade el panel de miembros en el centro del diálogo
        add(panelMiembros, BorderLayout.CENTER);

        // ========== PANEL DE BOTONES PRINCIPALES ==========

        // Crea panel con FlowLayout centrado
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Si es administrador
        if (esAdministrador) {
            // ========== BOTÓN GUARDAR ==========

            // Crea botón guardar con icono
            btnGuardar = new JButton("Guardar", IconoUtils.iconoAgregar(18));
            // Establece fuente
            btnGuardar.setFont(new Font("Arial", Font.PLAIN, 14));
            // Fondo blanco
            btnGuardar.setBackground(Color.WHITE);
            // Texto negro
            btnGuardar.setForeground(Color.BLACK);
            // Desactiva el borde de enfoque
            btnGuardar.setFocusPainted(false);
            // Establece tamaño preferido
            btnGuardar.setPreferredSize(new Dimension(140, 35));
            // Añade listener que ejecuta guardar() al hacer clic
            btnGuardar.addActionListener(e -> guardar());
            // Añade el botón al panel
            panelBotones.add(btnGuardar);
        }

        // ========== BOTÓN CANCELAR/CERRAR ==========

        // Crea botón con texto dinámico según rol (Cancelar si es admin, Cerrar si no)
        btnCancelar = new JButton(esAdministrador ? "Cancelar" : "Cerrar", IconoUtils.iconoCerrar(18));
        // Establece fuente
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 14));
        // Fondo blanco
        btnCancelar.setBackground(Color.WHITE);
        // Texto negro
        btnCancelar.setForeground(Color.BLACK);
        // Desactiva el borde de enfoque
        btnCancelar.setFocusPainted(false);
        // Establece tamaño preferido
        btnCancelar.setPreferredSize(new Dimension(140, 35));
        // Añade listener que ejecuta cerrarDialogo() al hacer clic
        btnCancelar.addActionListener(e -> cerrarDialogo());
        // Añade el botón al panel
        panelBotones.add(btnCancelar);

        // Añade el panel de botones en la parte inferior del diálogo
        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Carga los datos del grupo existente en los componentes del formulario.
     */
    private void cargarDatos() {
        // Si existe un grupo (modo edición)
        if (grupo != null) {
            // Carga el nombre en el campo de texto
            txtNombre.setText(grupo.getNombre());
            // Carga la descripción en el campo de texto
            txtDescripcion.setText(grupo.getDescripcion());
            // Itera sobre todos los miembros del grupo
            for (Contacto c : grupo.getMiembros()) {
                // Añade el contacto a la lista temporal
                miembrosTemporales.add(c);
                // Añade el contacto a la lista original (para comparar cambios)
                miembrosOriginales.add(c);
            }
        }
        // Actualiza la tabla de miembros
        cargarMiembros();
    }

    /**
     * Actualiza la tabla de miembros con la lista temporal actual.
     */
    private void cargarMiembros() {
        // Limpia todas las filas de la tabla
        modeloMiembros.setRowCount(0);
        // Itera sobre la lista temporal de miembros
        for (Contacto c : miembrosTemporales) {
            // Añade una fila con el email y nombre del contacto
            modeloMiembros.addRow(new Object[]{
                    c.getEmail(), c.getNombre()
            });
        }
    }

    /**
     * Abre un diálogo para seleccionar y agregar un nuevo miembro al grupo.
     */
    private void agregarMiembro() {
        // Si no hay contactos disponibles
        if (contactosDisponibles.isEmpty()) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this,
                    "No hay contactos disponibles.\nPrimero debe agregar contactos.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            // Sale del método
            return;
        }

        // Crea lista de contactos disponibles (que no están ya en el grupo)
        ArrayList<Contacto> disponibles = new ArrayList<>();
        // Itera sobre todos los contactos disponibles
        for (Contacto c : contactosDisponibles) {
            // Bandera para verificar si ya está en el grupo
            boolean yaEsta = false;
            // Itera sobre los miembros temporales
            for (Contacto miembro : miembrosTemporales) {
                // Si el email coincide (ignora mayúsculas/minúsculas)
                if (miembro.getEmail().equalsIgnoreCase(c.getEmail())) {
                    // Marca que ya está en el grupo
                    yaEsta = true;
                    // Sale del bucle interno
                    break;
                }
            }
            // Si no está en el grupo, lo añade a la lista de disponibles
            if (!yaEsta) disponibles.add(c);
        }

        // Si no quedan contactos disponibles
        if (disponibles.isEmpty()) {
            // Muestra mensaje informativo
            JOptionPane.showMessageDialog(this,
                    "Todos los contactos ya están en el grupo",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            // Sale del método
            return;
        }

        // Crea diálogo de selección de contacto
        DialogoSeleccionarContacto dialogo = new DialogoSeleccionarContacto(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                disponibles);
        // Muestra el diálogo (bloquea hasta que se cierre)
        dialogo.setVisible(true);

        // Obtiene el contacto seleccionado
        Contacto seleccionado = dialogo.getContactoSeleccionado();
        // Si se seleccionó un contacto
        if (seleccionado != null) {
            // Añade el contacto a la lista temporal
            miembrosTemporales.add(seleccionado);
            // Actualiza la tabla de miembros
            cargarMiembros();
        }
    }

    /**
     * Quita un miembro seleccionado del grupo.
     */
    private void quitarMiembro() {
        // Obtiene el índice de la fila seleccionada
        int filaSeleccionada = tablaMiembros.getSelectedRow();
        // Si no hay ninguna fila seleccionada
        if (filaSeleccionada == -1) {
            // Muestra mensaje de advertencia
            JOptionPane.showMessageDialog(this, "Seleccione un miembro para quitar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            // Sale del método
            return;
        }

        // Obtiene el email de la fila seleccionada (columna 0)
        String email = (String) modeloMiembros.getValueAt(filaSeleccionada, 0);
        // Obtiene el nombre de la fila seleccionada (columna 1)
        String nombre = (String) modeloMiembros.getValueAt(filaSeleccionada, 1);

        // Muestra diálogo de confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea quitar a:\n\n" + nombre + "\n" + email + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        // Si el usuario confirmó
        if (confirmacion == JOptionPane.YES_OPTION) {
            // Elimina el contacto de la lista temporal (compara emails ignorando mayúsculas)
            miembrosTemporales.removeIf(c -> c.getEmail().equalsIgnoreCase(email));
            // Actualiza la tabla de miembros
            cargarMiembros();
        }
    }

    /**
     * Verifica si hay modificaciones sin guardar en el formulario.
     * @return true si hay cambios, false si no
     */
    private boolean hayModificaciones() {
        // Obtiene el nombre actual del campo de texto (sin espacios)
        String nombreActual = txtNombre.getText().trim();
        // Obtiene la descripción actual del campo de texto (sin espacios)
        String descripcionActual = txtDescripcion.getText().trim();

        // Si el nombre cambió (compara con el original o cadena vacía si es nuevo)
        if (!nombreActual.equals(nombreOriginal != null ? nombreOriginal : "")) {
            // Hay modificaciones
            return true;
        }

        // Si la descripción cambió
        if (!descripcionActual.equals(descripcionOriginal != null ? descripcionOriginal : "")) {
            // Hay modificaciones
            return true;
        }

        // Si cambió la cantidad de miembros
        if (miembrosTemporales.size() != miembrosOriginales.size()) {
            // Hay modificaciones
            return true;
        }

        // Itera sobre los miembros temporales
        for (Contacto temporal : miembrosTemporales) {
            // Bandera para verificar si el miembro estaba originalmente
            boolean encontrado = false;
            // Itera sobre los miembros originales
            for (Contacto original : miembrosOriginales) {
                // Si los emails coinciden
                if (temporal.getEmail().equalsIgnoreCase(original.getEmail())) {
                    // Marca como encontrado
                    encontrado = true;
                    // Sale del bucle interno
                    break;
                }
            }
            // Si no se encontró el miembro temporal en los originales
            if (!encontrado) {
                // Hay modificaciones
                return true;
            }
        }

        // No hay modificaciones
        return false;
    }

    /**
     * Cierra el diálogo, verificando si hay cambios sin guardar.
     */
    private void cerrarDialogo() {
        // Si es edición, es administrador y hay modificaciones
        if (grupo != null && esAdministrador && hayModificaciones()) {
            // Muestra diálogo de confirmación
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "⚠️ Tiene cambios sin guardar\n\n" +
                            "¿Está seguro que desea salir sin guardar los cambios realizados?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            // Si el usuario confirmó la salida
            if (respuesta == JOptionPane.YES_OPTION) {
                // Cierra el diálogo
                dispose();
            }
            // Si es creación nueva y es administrador
        } else if (grupo == null && esAdministrador) {
            // Obtiene el nombre ingresado
            String nombre = txtNombre.getText().trim();
            // Obtiene la descripción ingresada
            String descripcion = txtDescripcion.getText().trim();

            // Si hay algún dato ingresado
            if (!nombre.isEmpty() || !descripcion.isEmpty() || !miembrosTemporales.isEmpty()) {
                // Muestra diálogo de confirmación
                int respuesta = JOptionPane.showConfirmDialog(this,
                        "⚠️ Tiene información sin guardar\n\n" +
                                "¿Está seguro que desea cancelar la creación del grupo?",
                        "Confirmar cancelación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                // Si el usuario confirmó
                if (respuesta == JOptionPane.YES_OPTION) {
                    // Cierra el diálogo
                    dispose();
                }
            } else {
                // Si no hay datos, cierra directamente
                dispose();
            }
        } else {
            // En otros casos (no admin o sin cambios), cierra directamente
            dispose();
        }
    }

    /**
     * Guarda el grupo (crear nuevo o modificar existente).
     */
    private void guardar() {
        try {
            // Obtiene el nombre del campo de texto (sin espacios)
            String nombre = txtNombre.getText().trim();
            // Obtiene la descripción del campo de texto (sin espacios)
            String descripcion = txtDescripcion.getText().trim();

            // ========== VALIDACIONES ==========

            // Si el nombre está vacío
            if (nombre.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "⚠️ Nombre requerido\n\nDebe ingresar un nombre para el grupo.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                // Pone el foco en el campo nombre
                txtNombre.requestFocus();
                // Sale del método
                return;
            }

            // Si la descripción está vacía
            if (descripcion.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "⚠️ Descripción requerida\n\nDebe ingresar una descripción para el grupo.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                // Pone el foco en el campo descripción
                txtDescripcion.requestFocus();
                // Sale del método
                return;
            }

            // ========== CREAR NUEVO GRUPO ==========

            // Si es modo creación (grupo es null)
            if (grupo == null) {
                // Muestra diálogo de confirmación
                int confirmacion = JOptionPane.showConfirmDialog(this,
                        "¿Está seguro que desea crear el grupo:\n\n" +
                                "Nombre: " + nombre + "\n" +
                                "Descripción: " + descripcion + "\n" +
                                "Miembros: " + (miembrosTemporales.size() + 1),
                        "Confirmar creación de grupo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                // Si el usuario no confirmó
                if (confirmacion != JOptionPane.YES_OPTION) {
                    // Sale del método
                    return;
                }

                // Crea un nuevo grupo con nombre y descripción
                Grupo nuevoGrupo = new Grupo(nombre, descripcion);
                // Crea contacto del creador (usuario actual)
                Contacto creador = new Contacto(empresa.getUsuarioActual().getEmail(),
                        empresa.getUsuarioActual().getNombreCompleto());
                // Añade el creador como primer miembro del grupo
                nuevoGrupo.agregarMiembro(creador);

                // Itera sobre los miembros temporales
                for (Contacto c : miembrosTemporales) {
                    // Si el contacto no es el usuario actual (ya agregado)
                    if (!c.getEmail().equalsIgnoreCase(empresa.getUsuarioActual().getEmail())) {
                        // Añade el contacto al grupo
                        nuevoGrupo.agregarMiembro(c);
                    }
                }

                // Intenta agregar el grupo a la empresa
                if (empresa.agregarGrupo(nuevoGrupo)) {
                    // Añade mensaje del sistema indicando que el grupo fue creado
                    nuevoGrupo.agregarMensajeSistemaGrupoCreado(
                            empresa.getUsuarioActual().getEmail(),
                            empresa.getUsuarioActual().getNombreCompleto()
                    );

                    // Guarda los datos automáticamente
                    empresa.guardarDatosAutomaticamente();

                    // Muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this, "✓ Grupo creado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Marca que se guardó exitosamente
                    guardado = true;
                    // Cierra el diálogo
                    dispose();
                }
                // ========== MODIFICAR GRUPO EXISTENTE ==========
            } else {
                // Muestra diálogo de confirmación
                int confirmacion = JOptionPane.showConfirmDialog(this,
                        "¿Está seguro que desea guardar los cambios realizados en el grupo?",
                        "Confirmar modificación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                // Si el usuario no confirmó
                if (confirmacion != JOptionPane.YES_OPTION) {
                    // Sale del método
                    return;
                }

                // Guarda el nombre antiguo
                String nombreAntiguo = grupo.getNombre();
                // Guarda la descripción antigua
                String descripcionAntigua = grupo.getDescripcion();

                // Si el nombre cambió
                if (!nombreAntiguo.equals(nombre)) {
                    // Añade mensaje del sistema indicando el cambio de nombre
                    grupo.agregarMensajeSistemaNombreCambiado(
                            empresa.getUsuarioActual().getEmail(),
                            empresa.getUsuarioActual().getNombreCompleto(),
                            nombreAntiguo,
                            nombre
                    );
                }

                // Si la descripción cambió
                if (!descripcionAntigua.equals(descripcion)) {
                    // Añade mensaje del sistema indicando el cambio de descripción
                    grupo.agregarMensajeSistemaDescripcionCambiada(
                            empresa.getUsuarioActual().getEmail(),
                            empresa.getUsuarioActual().getNombreCompleto()
                    );
                }

                // Actualiza el nombre del grupo
                grupo.setNombre(nombre);
                // Actualiza la descripción del grupo
                grupo.setDescripcion(descripcion);

                // ========== DETECTAR MIEMBROS AGREGADOS ==========

                // Itera sobre los nuevos miembros
                for (Contacto nuevoMiembro : miembrosTemporales) {
                    // Verifica si el miembro ya existía originalmente usando streams
                    boolean eraOriginal = miembrosOriginales.stream()
                            .anyMatch(c -> c.getEmail().equalsIgnoreCase(nuevoMiembro.getEmail()));

                    // Si no era miembro original (es nuevo)
                    if (!eraOriginal) {
                        // Añade mensaje del sistema indicando que se agregó un miembro
                        grupo.agregarMensajeSistemaMiembroAgregado(
                                empresa.getUsuarioActual().getEmail(),
                                empresa.getUsuarioActual().getNombreCompleto(),
                                nuevoMiembro.getEmail(),
                                nuevoMiembro.getNombre()
                        );
                    }
                }

                // ========== DETECTAR MIEMBROS ELIMINADOS ==========

                // Itera sobre los miembros originales
                for (Contacto miembroOriginal : miembrosOriginales) {
                    // Verifica si el miembro original sigue en el grupo
                    boolean sigueEnGrupo = miembrosTemporales.stream()
                            .anyMatch(c -> c.getEmail().equalsIgnoreCase(miembroOriginal.getEmail()));

                    // Si ya no está en el grupo (fue eliminado)
                    if (!sigueEnGrupo) {
                        // Añade mensaje del sistema indicando que se eliminó un miembro
                        grupo.agregarMensajeSistemaMiembroEliminado(
                                empresa.getUsuarioActual().getEmail(),
                                empresa.getUsuarioActual().getNombreCompleto(),
                                miembroOriginal.getEmail(),
                                miembroOriginal.getNombre()
                        );
                    }
                }

                // ========== ACTUALIZAR LISTA DE MIEMBROS ==========

                // Limpia la lista actual de miembros del grupo
                grupo.getMiembros().clear();

                // Obtiene el email del administrador del grupo
                String emailAdmin = grupo.getEmailAdministrador();
                // Verifica si el administrador fue agregado manualmente
                boolean adminAgregado = miembrosTemporales.stream()
                        .anyMatch(c -> c.getEmail().equalsIgnoreCase(emailAdmin));

                // Si el admin no fue agregado manualmente
                if (!adminAgregado) {
                    // Si el admin es el usuario actual
                    if (emailAdmin.equalsIgnoreCase(empresa.getUsuarioActual().getEmail())) {
                        // Crea contacto del administrador
                        Contacto admin = new Contacto(empresa.getUsuarioActual().getEmail(),
                                empresa.getUsuarioActual().getNombreCompleto());
                        // Añade el administrador al grupo
                        grupo.agregarMiembro(admin);
                    }
                }

                // Itera sobre los miembros temporales
                for (Contacto c : miembrosTemporales) {
                    // Añade cada miembro al grupo
                    grupo.agregarMiembro(c);
                }

                // Intenta modificar el grupo en la empresa
                if (empresa.modificarGrupo(nombreOriginal, grupo)) {
                    // Guarda los datos automáticamente
                    empresa.guardarDatosAutomaticamente();

                    // Muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this, "✓ Grupo modificado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Marca que se guardó exitosamente
                    guardado = true;
                    // Cierra el diálogo
                    dispose();
                }
            }
        } catch (ValidationException e) {
            // Si ocurre una excepción de validación, muestra el mensaje de error
            JOptionPane.showMessageDialog(this, "❌ " + e.getMensaje(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retorna si se guardaron los cambios exitosamente.
     * @return true si se guardó, false si no
     */
    public boolean isGuardado() {
        // Retorna el estado de guardado
        return guardado;
    }
}