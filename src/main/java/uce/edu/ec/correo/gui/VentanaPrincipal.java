package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

/**
 * Clase principal de la ventana de la aplicación EcuMail.
 * Esta clase maneja la interfaz gráfica principal y coordina
 * la navegación entre los diferentes paneles de la aplicación.
 */
public class VentanaPrincipal extends JFrame {
    // Constante que define el nombre del archivo donde se guardarán los usuarios registrados
    private static final String ARCHIVO_USUARIOS = "usuarios_registrados.txt";

    // Referencia a la empresa de correo que maneja la lógica de negocio
    private EmpresaCorreo empresa;

    // Layout que permite cambiar entre diferentes paneles (LOGIN, REGISTRO, MENU_PRINCIPAL)
    private CardLayout cardLayout;

    // Panel contenedor principal que alberga todos los demás paneles
    private JPanel contenedorPrincipal;

    // Referencias a los paneles específicos de la aplicación
    private PanelLogin panelLogin;
    private PanelRegistro panelRegistro;
    private PanelMenuPrincipal panelMenuPrincipal;

    /**
     * Constructor de la ventana principal.
     * Inicializa todos los componentes y muestra la pantalla de splash.
     */
    public VentanaPrincipal() {
        // Establece el ícono de la ventana usando la utilidad IconoUtils
        setIconImage(IconoUtils.crearIconoEcuMail());

        // Carga los datos previamente guardados de la empresa (usuarios, correos, etc.)
        empresa = EmpresaCorreo.cargarDatos();

        // Crea y muestra la pantalla de splash (pantalla de bienvenida)
        SplashScreen splash = new SplashScreen(this);

        // Crea un temporizador que espera 3 segundos (3000 ms) antes de mostrar la ventana principal
        Timer timer = new Timer(3000, e -> {
            // Cierra la pantalla de splash
            splash.dispose();
            // Inicializa todos los componentes de la ventana
            inicializarComponentes();
            // Configura las propiedades de la ventana
            configurarVentana();
            // Configura el comportamiento al cerrar la ventana
            configurarCierrePersistencia();
            // Hace visible la ventana principal
            setVisible(true);
        });
        // Configura el timer para que se ejecute solo una vez
        timer.setRepeats(false);
        // Inicia el temporizador
        timer.start();

        // Muestra la pantalla de splash mientras se espera
        splash.setVisible(true);
    }

    /**
     * Método que inicializa todos los componentes de la interfaz gráfica.
     * Crea los paneles (Login, Registro, MenuPrincipal) y los agrega al contenedor.
     */
    private void inicializarComponentes() {
        // Crea un nuevo CardLayout para manejar el cambio entre paneles
        cardLayout = new CardLayout();
        // Crea el panel contenedor con el CardLayout
        contenedorPrincipal = new JPanel(cardLayout);

        // Instancia el panel de login pasándole la referencia a esta ventana y a la empresa
        panelLogin = new PanelLogin(this, empresa);
        // Instancia el panel de registro
        panelRegistro = new PanelRegistro(this, empresa);
        // Instancia el panel del menú principal
        panelMenuPrincipal = new PanelMenuPrincipal(this, empresa);

        // Agrega el panel de login al contenedor con el identificador "LOGIN"
        contenedorPrincipal.add(panelLogin, "LOGIN");
        // Agrega el panel de registro con el identificador "REGISTRO"
        contenedorPrincipal.add(panelRegistro, "REGISTRO");
        // Agrega el panel de menú principal con el identificador "MENU_PRINCIPAL"
        contenedorPrincipal.add(panelMenuPrincipal, "MENU_PRINCIPAL");

        // Agrega el contenedor principal a la ventana
        add(contenedorPrincipal);
    }

    /**
     * Método que configura las propiedades básicas de la ventana.
     * Define título, tamaño, comportamiento de cierre y posición.
     */
    private void configurarVentana() {
        // Establece el título de la ventana
        setTitle("EcuMail");
        // Define el tamaño inicial de la ventana (1000x700 píxeles)
        setSize(1000, 700);
        // Configura para que NO cierre automáticamente al hacer clic en X
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Centra la ventana en la pantalla
        setLocationRelativeTo(null);
        // Establece el tamaño mínimo permitido para la ventana
        setMinimumSize(new Dimension(900, 600));
    }

    /**
     * Método que configura el listener para el evento de cierre de la ventana.
     * Permite guardar datos antes de cerrar la aplicación.
     */
    private void configurarCierrePersistencia() {
        // Agrega un listener que escucha eventos de la ventana
        addWindowListener(new WindowAdapter() {
            /**
             * Método que se ejecuta cuando el usuario intenta cerrar la ventana.
             * @param e Evento de ventana que contiene información del cierre
             */
            @Override
            public void windowClosing(WindowEvent e) {
                // Llama al método que maneja el cierre de la aplicación
                cerrarAplicacion();
            }
        });
    }

    /**
     * Método que maneja el proceso de cierre de la aplicación.
     * Muestra un diálogo de confirmación y guarda los datos antes de salir.
     */
    private void cerrarAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            empresa.guardarDatos();

            boolean guardadoBD = uce.edu.ec.correo.DB.PersistenciaDB.guardarTodo(empresa);
            if (!guardadoBD) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar en la base de datos.\n" +
                                "Los datos quedaron guardados localmente.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            guardarUsuariosEnArchivo();
            System.exit(0);
        }
    }

    /**
     * Método que guarda la lista de usuarios registrados en un archivo de texto.
     * Crea un archivo formateado con la información de todos los usuarios.
     */
    private void guardarUsuariosEnArchivo() {
        // Usa try-with-resources para asegurar que el archivo se cierre automáticamente
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            // Escribe el encabezado del archivo
            writer.println("===========================================");
            writer.println("   USUARIOS REGISTRADOS EN EL SISTEMA");
            writer.println("===========================================");
            writer.println(); // Línea en blanco

            // Itera sobre cada usuario registrado en la empresa
            empresa.listarUsuarios().forEach(usuario -> {
                // Escribe un separador visual
                writer.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                // Escribe el nombre completo del usuario
                writer.println("Nombre Completo:     " + usuario.getNombreCompleto());
                // Escribe el email del usuario
                writer.println("Email:               " + usuario.getEmail());
                // Escribe la contraseña oculta (reemplazada por asteriscos)
                writer.println("Contraseña:          " + generarAsteriscos(usuario.getPassword().length()));
                // Escribe la cédula del usuario
                writer.println("Cédula:              " + usuario.getCedula());
                // Escribe el teléfono (o "No registrado" si no hay teléfono)
                writer.println("Teléfono:            " + (usuario.getTelefono() != null && !usuario.getTelefono().isEmpty()
                        ? usuario.getTelefono() : "No registrado"));
                // Escribe la fecha de nacimiento formateada (o "N/A" si no existe)
                writer.println("Fecha Nacimiento:    " + (usuario.getFechaNacimiento() != null ?
                        usuario.getFechaNacimiento().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"));
                // Escribe el género del usuario
                writer.println("Género:              " + usuario.getGenero().getDescripcion());
                writer.println(); // Línea en blanco entre usuarios
            });

            // Escribe el pie del archivo
            writer.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            writer.println();
            // Escribe el total de usuarios registrados
            writer.println("Total de usuarios registrados: " + empresa.getCantidadUsuarios());
            writer.println();
            // Escribe la fecha y hora de generación del archivo
            writer.println("Archivo generado el: " +
                    java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.println();
            // Nota de seguridad
            writer.println("NOTA: Las contraseñas están ocultas por seguridad.");

        } catch (IOException e) {
            // Si ocurre un error al escribir el archivo, muestra el error en consola
            System.err.println("Error al guardar archivo de usuarios: " + e.getMessage());
        }
    }

    /**
     * Método que genera una cadena de asteriscos para ocultar contraseñas.
     * @param cantidad Número de asteriscos a generar
     * @return String con la cantidad especificada de asteriscos
     */
    private String generarAsteriscos(int cantidad) {
        // Crea un StringBuilder para construir la cadena de forma eficiente
        StringBuilder asteriscos = new StringBuilder();
        // Itera tantas veces como caracteres tenga la contraseña
        for (int i = 0; i < cantidad; i++) {
            // Agrega un asterisco por cada carácter
            asteriscos.append("*");
        }
        // Convierte el StringBuilder a String y lo retorna
        return asteriscos.toString();
    }

    /**
     * Método que cambia la visualización entre diferentes paneles.
     * @param nombrePanel Identificador del panel a mostrar ("LOGIN", "REGISTRO", "MENU_PRINCIPAL")
     */
    public void mostrarPanel(String nombrePanel) {
        // Usa el CardLayout para mostrar el panel especificado
        cardLayout.show(contenedorPrincipal, nombrePanel);

        // Si el panel a mostrar es el menú principal
        if (nombrePanel.equals("MENU_PRINCIPAL")) {
            // Actualiza el mensaje de bienvenida con el nombre del usuario actual
            panelMenuPrincipal.actualizarBienvenida();
        }
    }

    /**
     * Método getter que retorna la referencia al panel de login.
     * @return PanelLogin - Instancia del panel de inicio de sesión
     */
    public PanelLogin getPanelLogin() {
        return panelLogin;
    }
}