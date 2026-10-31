package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.*;
import uce.edu.ec.correo.util.ValidationException;
import javax.swing.*;
import java.awt.*;

/**
 * Clase DialogoDetalleContacto
 *
 * Esta clase representa un diálogo modal que muestra los detalles completos
 * de un contacto y permite realizar diversas acciones sobre él:
 * - Ver información del contacto (email y nombre)
 * - Modificar el nombre del contacto
 * - Enviar un correo al contacto
 * - Eliminar el contacto
 *
 * Extiende de JDialog para crear una ventana modal independiente.
 */
public class DialogoDetalleContacto extends JDialog {
    // Referencia a la empresa de correo que contiene toda la información del sistema
    private EmpresaCorreo empresa;

    // Contacto del cual se muestran los detalles
    private Contacto contacto;

    // Email original del contacto (para identificarlo si cambia)
    private String emailOriginal;

    // Bandera que indica si se realizaron modificaciones (para notificar a la ventana padre)
    private boolean modificado = false;

    // Componentes de la interfaz gráfica para mostrar información
    private JLabel lblEmail; // Label que muestra el email del contacto
    private JLabel lblNombre; // Label que muestra el nombre del contacto

    // Botones de acción
    private JButton btnModificarNombre; // Botón para modificar el nombre
    private JButton btnEnviarCorreo; // Botón para enviar un correo
    private JButton btnEliminar; // Botón para eliminar el contacto
    private JButton btnCerrar; // Botón para cerrar el diálogo

    /**
     * Constructor de DialogoDetalleContacto
     *
     * Inicializa el diálogo con la información del contacto.
     *
     * @param parent Ventana padre (JFrame principal)
     * @param empresa Referencia a la empresa de correo
     * @param contacto Contacto del cual se mostrarán los detalles
     */
    public DialogoDetalleContacto(JFrame parent, EmpresaCorreo empresa, Contacto contacto) {
        // Llama al constructor de JDialog con título fijo y modo modal
        super(parent, "Detalle del Contacto", true);

        this.empresa = empresa; // Guarda la referencia a la empresa
        this.contacto = contacto; // Guarda la referencia al contacto
        this.emailOriginal = contacto.getEmail(); // Guarda el email original

        inicializarComponentes(); // Crea la interfaz gráfica
        cargarDatos(); // Carga los datos del contacto en los labels

        setSize(600, 500); // Establece el tamaño del diálogo
        setLocationRelativeTo(parent); // Centra el diálogo respecto a la ventana padre
        setResizable(false); // No permite redimensionar la ventana
    }

    /**
     * Método inicializarComponentes
     *
     * Crea y configura todos los componentes de la interfaz gráfica del diálogo.
     * Incluye panel de información y panel de botones con todas las acciones.
     */
    private void inicializarComponentes() {
        // Establece el layout principal con BorderLayout y espaciado
        setLayout(new BorderLayout(15, 15));

        // ========== CONFIGURACIÓN DEL CIERRE DE VENTANA ==========
        // Intercepta el evento de cierre de ventana
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // No cierra automáticamente

        // Agrega un listener para manejar el cierre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmarCierre(); // Llama al método de confirmación
            }
        });

        // ========== PANEL CENTRAL (Información del Contacto) ==========
        // Crea el panel central con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.WHITE); // Fondo blanco
        // Borde compuesto: padding externo + línea gris
        panelCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(30, 35, 30, 35),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));

        // Configura las restricciones para GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Expansión horizontal
        gbc.anchor = GridBagConstraints.WEST; // Alineación a la izquierda

        // ========== TÍTULO DEL DIÁLOGO ==========
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.gridwidth = 2; // Ocupa 2 columnas
        JLabel lblTitulo = new JLabel("Información del Contacto");
        lblTitulo.setIcon(uce.edu.ec.correo.util.IconoUtils.iconoInformacionPersonal(24)); // Icono de info
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22)); // Fuente grande y en negrita
        lblTitulo.setForeground(new Color(0, 102, 204)); // Color azul
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER); // Centrado
        panelCentral.add(lblTitulo, gbc); // Agrega el título

        // ========== SEPARADOR DECORATIVO ==========
        gbc.gridy = 1; // Fila 1
        gbc.insets = new Insets(10, 20, 25, 20); // Mayor espaciado inferior
        JSeparator separador = new JSeparator(); // Línea separadora horizontal
        separador.setForeground(new Color(0, 102, 204)); // Color azul
        panelCentral.add(separador, gbc); // Agrega el separador

        // Restaura el espaciado normal
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridwidth = 1; // Vuelve a 1 columna de ancho

        // ========== CAMPO EMAIL (No editable) ==========
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 2; // Fila 2
        gbc.weightx = 0.3; // 30% del ancho disponible
        JLabel lblEmailLabel = new JLabel("Email:");
        lblEmailLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Fuente en negrita
        panelCentral.add(lblEmailLabel, gbc); // Agrega el label

        gbc.gridx = 1; // Columna 1
        gbc.weightx = 0.7; // 70% del ancho disponible
        lblEmail = new JLabel(); // Label que mostrará el email
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente normal
        lblEmail.setForeground(new Color(50, 50, 50)); // Color gris oscuro
        panelCentral.add(lblEmail, gbc); // Agrega el label

        // ========== CAMPO NOMBRE ==========
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 3; // Fila 3
        gbc.weightx = 0.3; // 30% del ancho disponible
        JLabel lblNombreLabel = new JLabel("Nombre:");
        lblNombreLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Fuente en negrita
        panelCentral.add(lblNombreLabel, gbc); // Agrega el label

        gbc.gridx = 1; // Columna 1
        gbc.weightx = 0.7; // 70% del ancho disponible
        lblNombre = new JLabel(); // Label que mostrará el nombre
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 16)); // Fuente normal
        lblNombre.setForeground(new Color(50, 50, 50)); // Color gris oscuro
        panelCentral.add(lblNombre, gbc); // Agrega el label

        add(panelCentral, BorderLayout.CENTER); // Agrega el panel al centro del diálogo

        // ========== PANEL DE BOTONES ==========
        // Crea el panel de botones con GridBagLayout para organización en cuadrícula
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(new Color(245, 245, 245)); // Fondo gris claro
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 25, 25, 25)); // Padding

        // Configura las restricciones para los botones
        GridBagConstraints gbcBotones = new GridBagConstraints();
        gbcBotones.insets = new Insets(8, 8, 8, 8); // Espaciado entre botones
        gbcBotones.fill = GridBagConstraints.BOTH; // Expansión en ambas direcciones
        gbcBotones.weightx = 0.5; // 50% del ancho disponible
        gbcBotones.weighty = 0.5; // 50% del alto disponible

        // ========== PRIMERA FILA - BOTÓN MODIFICAR NOMBRE (Izquierda) ==========
        gbcBotones.gridx = 0; // Columna 0
        gbcBotones.gridy = 0; // Fila 0
        btnModificarNombre = new JButton("Modificar Nombre", uce.edu.ec.correo.util.IconoUtils.iconoEditar(20));
        btnModificarNombre.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        btnModificarNombre.setBackground(Color.WHITE); // Fondo blanco
        btnModificarNombre.setForeground(Color.BLACK); // Texto negro
        btnModificarNombre.setFocusPainted(false); // Sin borde de foco
        btnModificarNombre.setPreferredSize(new Dimension(240, 45)); // Tamaño del botón
        btnModificarNombre.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano
        // Borde compuesto: línea gris + padding interno
        btnModificarNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        btnModificarNombre.addActionListener(e -> modificarNombre()); // Acción al hacer clic
        panelBotones.add(btnModificarNombre, gbcBotones); // Agrega el botón

        // ========== PRIMERA FILA - BOTÓN ENVIAR CORREO (Derecha) ==========
        gbcBotones.gridx = 1; // Columna 1
        gbcBotones.gridy = 0; // Fila 0
        btnEnviarCorreo = new JButton("Enviar Correo", uce.edu.ec.correo.util.IconoUtils.iconoEnviar(20));
        btnEnviarCorreo.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        btnEnviarCorreo.setBackground(Color.WHITE); // Fondo blanco
        btnEnviarCorreo.setForeground(Color.BLACK); // Texto negro
        btnEnviarCorreo.setFocusPainted(false); // Sin borde de foco
        btnEnviarCorreo.setPreferredSize(new Dimension(240, 45)); // Tamaño del botón
        btnEnviarCorreo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano
        // Borde compuesto: línea gris + padding interno
        btnEnviarCorreo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        btnEnviarCorreo.addActionListener(e -> enviarCorreo()); // Acción al hacer clic
        panelBotones.add(btnEnviarCorreo, gbcBotones); // Agrega el botón

        // ========== SEGUNDA FILA - BOTÓN ELIMINAR CONTACTO (Izquierda) ==========
        gbcBotones.gridx = 0; // Columna 0
        gbcBotones.gridy = 1; // Fila 1
        btnEliminar = new JButton("Eliminar Contacto", uce.edu.ec.correo.util.IconoUtils.iconoEliminarX(20));
        btnEliminar.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        btnEliminar.setBackground(Color.WHITE); // Fondo blanco
        btnEliminar.setForeground(Color.BLACK); // Texto negro
        btnEliminar.setFocusPainted(false); // Sin borde de foco
        btnEliminar.setPreferredSize(new Dimension(240, 45)); // Tamaño del botón
        btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano
        // Borde compuesto: línea gris + padding interno
        btnEliminar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        btnEliminar.addActionListener(e -> eliminarContacto()); // Acción al hacer clic
        panelBotones.add(btnEliminar, gbcBotones); // Agrega el botón

        // ========== SEGUNDA FILA - BOTÓN CERRAR (Derecha) ==========
        gbcBotones.gridx = 1; // Columna 1
        gbcBotones.gridy = 1; // Fila 1
        btnCerrar = new JButton("Cerrar", uce.edu.ec.correo.util.IconoUtils.iconoCerrar(20));
        btnCerrar.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente normal
        btnCerrar.setBackground(Color.WHITE); // Fondo blanco
        btnCerrar.setForeground(Color.BLACK); // Texto negro
        btnCerrar.setFocusPainted(false); // Sin borde de foco
        btnCerrar.setPreferredSize(new Dimension(240, 45)); // Tamaño del botón
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano
        // Borde compuesto: línea gris + padding interno
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        btnCerrar.addActionListener(e -> confirmarCierre()); // Acción al hacer clic
        panelBotones.add(btnCerrar, gbcBotones); // Agrega el botón

        add(panelBotones, BorderLayout.SOUTH); // Agrega el panel de botones al sur del diálogo
    }

    /**
     * Método confirmarCierre
     *
     * Pide confirmación al usuario antes de cerrar el diálogo.
     * Esto proporciona una capa adicional de seguridad para evitar cierres accidentales.
     */
    private void confirmarCierre() {
        // Muestra diálogo de confirmación
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro/a que desea salir?",
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION, // Botones Sí/No
                JOptionPane.QUESTION_MESSAGE); // Icono de pregunta

        // Si el usuario confirma (Sí)
        if (opcion == JOptionPane.YES_OPTION) {
            dispose(); // Cierra el diálogo
        }
        // Si responde No, no hace nada (permanece abierto)
    }

    /**
     * Método cargarDatos
     *
     * Carga los datos del contacto en los labels de la interfaz.
     * Maneja el caso especial de contactos sin nombre.
     */
    private void cargarDatos() {
        lblEmail.setText(contacto.getEmail()); // Muestra el email

        // Para el nombre, verifica si existe y no está vacío
        lblNombre.setText(contacto.getNombre() != null && !contacto.getNombre().isEmpty()
                ? contacto.getNombre() // Si tiene nombre, lo muestra
                : "Sin nombre"); // Si no, muestra "Sin nombre"
    }

    /**
     * Método modificarNombre
     *
     * Permite modificar únicamente el nombre del contacto mediante un diálogo de entrada.
     * El email permanece inmutable.
     */
    private void modificarNombre() {
        // Obtiene el nombre actual (o cadena vacía si es null)
        String nombreActual = contacto.getNombre() != null ? contacto.getNombre() : "";

        // Muestra un diálogo de entrada con el nombre actual como valor inicial
        String nuevoNombre = (String) JOptionPane.showInputDialog(
                this, // Ventana padre
                "Ingrese el nuevo nombre:", // Mensaje
                "Modificar Nombre", // Título
                JOptionPane.PLAIN_MESSAGE, // Tipo de mensaje
                null, // Sin icono personalizado
                null, // Sin opciones predefinidas
                nombreActual); // Valor inicial

        // Si el usuario ingresó algo y no está vacío
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            // Crea un contacto modificado con el nuevo nombre
            Contacto contactoModificado = new Contacto(contacto.getEmail(), nuevoNombre.trim());

            try {
                // Intenta modificar el contacto usando el email original como identificador
                if (empresa.modificarContacto(emailOriginal, contactoModificado)) {
                    // Actualiza el objeto contacto local
                    contacto.setNombre(nuevoNombre.trim());
                    // Actualiza el label en la interfaz
                    lblNombre.setText(nuevoNombre.trim());
                    // Marca que hubo modificaciones
                    modificado = true;
                    // Muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            "Nombre modificado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (ValidationException e) {
                // Si hay error de validación, muestra el mensaje
                JOptionPane.showMessageDialog(this,
                        e.getMensaje(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si el usuario ingresó texto pero solo espacios en blanco
        else if (nuevoNombre != null && nuevoNombre.trim().isEmpty()) {
            // Muestra mensaje de error
            JOptionPane.showMessageDialog(this,
                    "El nombre no puede estar vacío",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        // Si nuevoNombre es null, significa que el usuario canceló
        // En ese caso, no hace nada
    }

    /**
     * Método enviarCorreo
     *
     * Abre el diálogo para enviar un nuevo correo al contacto.
     * El campo "Para" ya viene prellenado con el email del contacto.
     */
    private void enviarCorreo() {
        // Crea una nueva instancia del diálogo de nuevo correo
        DialogoNuevoCorreo dialogo = new DialogoNuevoCorreo(
                (JFrame) SwingUtilities.getWindowAncestor(this), // Obtiene la ventana padre principal
                empresa, // Referencia a la empresa
                contacto.getEmail()); // Email del destinatario prellenado
        dialogo.setVisible(true); // Muestra el diálogo (modal)
    }

    /**
     * Método eliminarContacto
     *
     * Elimina el contacto después de pedir confirmación al usuario.
     * Una vez eliminado, cierra el diálogo y marca que hubo modificaciones.
     */
    private void eliminarContacto() {
        // Muestra diálogo de confirmación con información del contacto
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el contacto:\n\n" +
                        contacto.getNombre() + "\n" + contacto.getEmail() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, // Botones Sí/No
                JOptionPane.WARNING_MESSAGE); // Icono de advertencia

        // Si el usuario confirma la eliminación
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                // Intenta eliminar el contacto por su email
                if (empresa.eliminarContacto(contacto.getEmail())) {
                    // Si se eliminó exitosamente, muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            "Contacto eliminado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Marca que hubo modificaciones
                    modificado = true;
                    // Cierra el diálogo
                    dispose();
                }
            } catch (ValidationException e) {
                // Si hay error al eliminar, muestra el mensaje
                JOptionPane.showMessageDialog(this,
                        e.getMensaje(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si responde No, no hace nada (no elimina)
    }

    /**
     * Método isModificado
     *
     * Retorna si se realizaron modificaciones en el contacto.
     * Útil para que la ventana que llamó a este diálogo sepa si debe actualizar su lista.
     *
     * @return true si se modificó o eliminó el contacto, false si no hubo cambios
     */
    public boolean isModificado() {
        return modificado;
    }
}