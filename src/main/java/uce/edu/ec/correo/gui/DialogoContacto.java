package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.IconoUtils;
import uce.edu.ec.correo.util.ValidationException;
import javax.swing.*;
import java.awt.*;

/**
 * Clase DialogoContacto
 *
 * Esta clase representa un diálogo modal para agregar o modificar contactos.
 * Permite al usuario ingresar o editar el email y nombre de un contacto,
 * validando que no existan duplicados y que todos los campos sean obligatorios.
 *
 * Extiende de JDialog para crear una ventana modal independiente.
 */
public class DialogoContacto extends JDialog {
    // Referencia a la empresa de correo que contiene toda la información del sistema
    private EmpresaCorreo empresa;

    // Contacto que se está editando (null si es un contacto nuevo)
    private Contacto contacto;

    // Email original del contacto (para detectar si cambió al modificar)
    private String emailOriginal;

    // Nombre original del contacto (para detectar cambios)
    private String nombreOriginal;

    // Bandera que indica si se guardó exitosamente el contacto
    private boolean guardado = false;

    // Componentes de la interfaz gráfica
    private JTextField txtEmail; // Campo de texto para el email
    private JTextField txtNombre; // Campo de texto para el nombre
    private JButton btnGuardar; // Botón para guardar el contacto
    private JButton btnCancelar; // Botón para cancelar y cerrar el diálogo

    /**
     * Constructor de DialogoContacto
     *
     * Inicializa el diálogo para agregar o modificar un contacto.
     * El título cambia según si es creación o modificación.
     *
     * @param parent Ventana padre (JFrame principal)
     * @param empresa Referencia a la empresa de correo
     * @param contacto Contacto a modificar (null para crear uno nuevo)
     */
    public DialogoContacto(JFrame parent, EmpresaCorreo empresa, Contacto contacto) {
        // Llama al constructor de JDialog con título dinámico según el modo
        // Si contacto es null: "Agregar Contacto", sino: "Modificar Contacto"
        super(parent, contacto == null ? "Agregar Contacto" : "Modificar Contacto", true);

        this.empresa = empresa; // Guarda la referencia a la empresa
        this.contacto = contacto; // Guarda la referencia al contacto (puede ser null)

        // Si hay contacto, guarda el email original; si no, es null
        this.emailOriginal = contacto != null ? contacto.getEmail() : null;

        // Si hay contacto, guarda el nombre original; si no, es null
        this.nombreOriginal = contacto != null ? contacto.getNombre() : null;

        inicializarComponentes(); // Crea la interfaz gráfica
        cargarDatos(); // Carga los datos si es modificación

        setSize(450, 250); // Establece el tamaño del diálogo
        setLocationRelativeTo(parent); // Centra el diálogo respecto a la ventana padre
    }

    /**
     * Método inicializarComponentes
     *
     * Crea y configura todos los componentes de la interfaz gráfica del diálogo.
     * Incluye campos de texto para email y nombre, y botones de acción.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL CENTRAL (Formulario) ==========
        // Crea el panel central con GridBagLayout para mejor control de posicionamiento
        JPanel panelCentral = new JPanel(new GridBagLayout());
        // Agrega un borde vacío (padding) alrededor del panel
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crea el objeto GridBagConstraints para controlar la posición de componentes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Los componentes se expanden horizontalmente
        gbc.anchor = GridBagConstraints.WEST; // Alineación hacia la izquierda

        // ========== CAMPO EMAIL ==========
        gbc.gridx = 0; // Columna 0 (primera columna)
        gbc.gridy = 0; // Fila 0 (primera fila)
        JLabel lblEmail = new JLabel("Email: *"); // Label con asterisco (obligatorio)
        lblEmail.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        panelCentral.add(lblEmail, gbc); // Agrega el label al panel

        gbc.gridx = 1; // Columna 1 (segunda columna)
        txtEmail = new JTextField(20); // Campo de texto con 20 columnas de ancho
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        panelCentral.add(txtEmail, gbc); // Agrega el campo al panel

        // ========== CAMPO NOMBRE ==========
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 1; // Fila 1 (segunda fila)
        JLabel lblNombre = new JLabel("Nombre: *"); // Label con asterisco (obligatorio)
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14)); // Fuente en negrita
        panelCentral.add(lblNombre, gbc); // Agrega el label al panel

        gbc.gridx = 1; // Columna 1
        txtNombre = new JTextField(20); // Campo de texto con 20 columnas de ancho
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        panelCentral.add(txtNombre, gbc); // Agrega el campo al panel

        // ========== NOTA INFORMATIVA ==========
        gbc.gridx = 0; // Empieza en columna 0
        gbc.gridy = 2; // Fila 2 (tercera fila)
        gbc.gridwidth = 2; // Ocupa 2 columnas (abarca todo el ancho)
        gbc.insets = new Insets(15, 8, 8, 8); // Mayor espaciado superior
        JLabel lblNota = new JLabel("* Todos los campos son obligatorios"); // Mensaje informativo
        lblNota.setFont(new Font("Arial", Font.ITALIC, 12)); // Fuente en cursiva y más pequeña
        lblNota.setForeground(Color.GRAY); // Color gris
        lblNota.setHorizontalAlignment(SwingConstants.CENTER); // Centrado
        panelCentral.add(lblNota, gbc); // Agrega la nota al panel

        add(panelCentral, BorderLayout.CENTER); // Agrega el panel central al centro del diálogo

        // ========== PANEL DE BOTONES ==========
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Botón Guardar con icono
        btnGuardar = new JButton("Guardar", IconoUtils.iconoEnviar(16));
        btnGuardar.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        btnGuardar.setForeground(Color.BLACK); // Texto negro
        btnGuardar.setFocusPainted(false); // Sin borde de foco
        btnGuardar.setPreferredSize(new Dimension(120, 35)); // Tamaño del botón
        btnGuardar.setHorizontalTextPosition(SwingConstants.RIGHT); // Texto a la derecha del icono
        btnGuardar.setIconTextGap(8); // Espacio entre icono y texto
        btnGuardar.addActionListener(e -> guardar()); // Acción: llamar al método guardar()
        panelBotones.add(btnGuardar); // Agrega el botón al panel

        // Botón Cancelar con icono
        btnCancelar = new JButton("Cancelar", IconoUtils.iconoCerrar(16));
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        btnCancelar.setForeground(Color.BLACK); // Texto negro
        btnCancelar.setFocusPainted(false); // Sin borde de foco
        btnCancelar.setPreferredSize(new Dimension(120, 35)); // Tamaño del botón
        btnCancelar.setHorizontalTextPosition(SwingConstants.RIGHT); // Texto a la derecha del icono
        btnCancelar.setIconTextGap(8); // Espacio entre icono y texto
        btnCancelar.addActionListener(e -> cerrarDialogo()); // Acción: llamar al método cerrarDialogo()
        panelBotones.add(btnCancelar); // Agrega el botón al panel

        add(panelBotones, BorderLayout.SOUTH); // Agrega el panel de botones al sur del diálogo
    }

    /**
     * Método cargarDatos
     *
     * Carga los datos del contacto en los campos de texto si se está modificando.
     * Si es un contacto nuevo (contacto == null), los campos quedan vacíos.
     */
    private void cargarDatos() {
        // Verifica si hay un contacto para cargar
        if (contacto != null) {
            txtEmail.setText(contacto.getEmail()); // Carga el email en el campo
            txtNombre.setText(contacto.getNombre()); // Carga el nombre en el campo
        }
        // Si contacto es null, los campos permanecen vacíos (para creación)
    }

    /**
     * Método hayModificaciones
     *
     * Verifica si el usuario ha realizado cambios en el formulario.
     * Utilizado para advertir al usuario antes de cerrar sin guardar.
     *
     * @return true si hay cambios sin guardar, false si no hay cambios
     */
    private boolean hayModificaciones() {
        // Si es un contacto nuevo (contacto == null)
        if (contacto == null) {
            // Verifica si hay algo escrito en alguno de los campos
            return !txtEmail.getText().trim().isEmpty() || !txtNombre.getText().trim().isEmpty();
        }

        // Para modificación, obtiene los valores actuales de los campos
        String emailActual = txtEmail.getText().trim();
        String nombreActual = txtNombre.getText().trim();

        // Compara los valores actuales con los originales
        // Retorna true si alguno cambió
        return !emailActual.equals(emailOriginal) || !nombreActual.equals(nombreOriginal);
    }

    /**
     * Método cerrarDialogo
     *
     * Cierra el diálogo, pero antes verifica si hay cambios sin guardar.
     * Si hay modificaciones, pide confirmación al usuario.
     */
    private void cerrarDialogo() {
        // Verifica si hay cambios sin guardar
        if (hayModificaciones()) {
            // Muestra diálogo de confirmación
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "⚠️ Tiene cambios sin guardar\n\n" +
                            "¿Desea salir sin guardar los cambios realizados?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            // Si el usuario confirma (YES), cierra el diálogo
            if (respuesta == JOptionPane.YES_OPTION) {
                dispose(); // Cierra el diálogo
            }
            // Si responde NO, no hace nada (permanece en el diálogo)
        } else {
            // Si no hay cambios, cierra directamente sin preguntar
            dispose();
        }
    }

    /**
     * Método guardar
     *
     * Valida y guarda el contacto (nuevo o modificado).
     * Realiza múltiples validaciones:
     * - Campos obligatorios no vacíos
     * - Email no duplicado (al crear o al cambiar email en modificación)
     * - Formato de email válido (mediante ValidationException)
     */
    private void guardar() {
        try {
            // Obtiene y limpia los valores de los campos
            String email = txtEmail.getText().trim();
            String nombre = txtNombre.getText().trim();

            // ========== VALIDACIÓN: Email obligatorio ==========
            if (email.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "⚠️ Email requerido\n\nDebe ingresar un email válido.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtEmail.requestFocus(); // Pone el foco en el campo de email
                return; // Sale del método sin guardar
            }

            // ========== VALIDACIÓN: Nombre obligatorio ==========
            if (nombre.isEmpty()) {
                // Muestra mensaje de advertencia
                JOptionPane.showMessageDialog(this,
                        "⚠️ Nombre requerido\n\nDebe ingresar un nombre para el contacto.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE);
                txtNombre.requestFocus(); // Pone el foco en el campo de nombre
                return; // Sale del método sin guardar
            }

            // ========== MODO: CREAR NUEVO CONTACTO ==========
            if (contacto == null) {
                // Verifica si ya existe un contacto con ese email
                Contacto existente = empresa.buscarContactoPorEmail(email);

                if (existente != null) {
                    // Si existe, muestra mensaje de error
                    JOptionPane.showMessageDialog(this,
                            "⚠️ Este contacto ya existe\n\n" +
                                    "Ya tiene un contacto con el email:\n" + email + "\n\n" +
                                    "Nombre actual: " + existente.getNombre(),
                            "Contacto duplicado",
                            JOptionPane.WARNING_MESSAGE);
                    return; // Sale del método sin guardar
                }

                // Crea el nuevo contacto
                Contacto nuevoContacto = new Contacto(email, nombre);

                // Intenta agregar el contacto a la empresa
                if (empresa.agregarContacto(nuevoContacto)) {
                    // Si se agregó exitosamente, muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            "✓ Contacto agregado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    guardado = true; // Marca que se guardó exitosamente
                    dispose(); // Cierra el diálogo
                }
            }
            // ========== MODO: MODIFICAR CONTACTO EXISTENTE ==========
            else {
                // Verifica si el email cambió
                if (!email.equalsIgnoreCase(emailOriginal)) {
                    // Si cambió, verifica que el nuevo email no exista en otro contacto
                    Contacto existente = empresa.buscarContactoPorEmail(email);

                    if (existente != null) {
                        // Si ya existe otro contacto con ese email, muestra error
                        JOptionPane.showMessageDialog(this,
                                "⚠️ Ya existe otro contacto con ese email\n\n" +
                                        "Email: " + email + "\n" +
                                        "Nombre: " + existente.getNombre(),
                                "Email duplicado",
                                JOptionPane.WARNING_MESSAGE);
                        return; // Sale del método sin guardar
                    }
                }

                // Crea el contacto modificado
                Contacto contactoModificado = new Contacto(email, nombre);

                // Intenta modificar el contacto usando el email original como clave
                if (empresa.modificarContacto(emailOriginal, contactoModificado)) {
                    // Si se modificó exitosamente, muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            "✓ Contacto modificado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    guardado = true; // Marca que se guardó exitosamente
                    dispose(); // Cierra el diálogo
                }
            }

        } catch (ValidationException e) {
            // Captura excepciones de validación (ej: formato de email inválido)
            JOptionPane.showMessageDialog(this,
                    "❌ " + e.getMensaje(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);

            // Si el error es en el campo Email, lo limpia y le da foco
            if ("Email".equals(e.getCampo())) {
                txtEmail.setText(""); // Limpia el campo
                txtEmail.requestFocus(); // Da foco al campo para que el usuario lo corrija
            }
        }
    }

    /**
     * Método isGuardado
     *
     * Retorna si el contacto fue guardado exitosamente.
     * Útil para que la ventana que llamó a este diálogo sepa si debe actualizar su lista.
     *
     * @return true si se guardó el contacto, false si se canceló
     */
    public boolean isGuardado() {
        return guardado;
    }
}