package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Clase que representa la pantalla de bienvenida (splash screen) de la aplicación.
 * Se muestra durante 3 segundos mientras se cargan los componentes principales.
 * Esta pantalla proporciona una introducción visual atractiva a EcuMail.
 */
public class SplashScreen extends JDialog {

    /**
     * Constructor de la pantalla de splash.
     * @param owner El Frame propietario de este diálogo (la ventana principal)
     */
    public SplashScreen(Frame owner) {
        // Llama al constructor de JDialog pasando el Frame propietario
        super(owner);

        // ===== ESTABLECER ÍCONO INMEDIATAMENTE =====
        // Establece el ícono de la ventana usando la utilidad IconoUtils
        setIconImage(IconoUtils.crearIconoEcuMail());

        // ===== CONFIGURAR COMO JDIALOG SIN DECORACIONES =====
        // Remueve la barra de título y los bordes del diálogo
        setUndecorated(true);
        // Configura como modal para bloquear la interacción con otras ventanas
        setModal(true);

        // ===== PANEL PRINCIPAL =====
        // Crea el panel de contenido con GridBagLayout para centrar elementos
        JPanel contenido = new JPanel(new GridBagLayout());
        // Establece el color de fondo azul corporativo
        contenido.setBackground(new Color(0, 102, 204));
        // Agrega un borde azul oscuro de 3 píxeles
        contenido.setBorder(BorderFactory.createLineBorder(new Color(0, 82, 184), 3));

        // ===== CONFIGURAR RESTRICCIONES DE LAYOUT =====
        // Crea las restricciones para posicionar elementos
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Ocupa todo el ancho
        gbc.anchor = GridBagConstraints.CENTER; // Centra los componentes
        gbc.insets = new Insets(30, 40, 10, 40); // Márgenes: arriba=30, lados=40, abajo=10

        // ===== ÍCONO DE CORREO =====
        // Crea una etiqueta con el ícono de EcuMail
        JLabel lblIcono = new JLabel(new ImageIcon(IconoUtils.crearIconoEcuMail()));
        // Agrega el ícono al panel con las restricciones configuradas
        contenido.add(lblIcono, gbc);

        // ===== NOMBRE DE LA EMPRESA =====
        // Modifica los márgenes para el título
        gbc.insets = new Insets(20, 40, 5, 40); // Arriba=20, abajo=5
        // Crea la etiqueta del nombre de la aplicación
        JLabel lblNombre = new JLabel("EcuMail");
        // Establece la fuente grande y en negrita (Arial, Bold, 48pts)
        lblNombre.setFont(new Font("Arial", Font.BOLD, 48));
        // Establece el color del texto en blanco
        lblNombre.setForeground(Color.WHITE);
        // Agrega el nombre al panel
        contenido.add(lblNombre, gbc);

        // ===== MENSAJE DE BIENVENIDA =====
        // Modifica los márgenes para el mensaje
        gbc.insets = new Insets(5, 40, 20, 40); // Arriba=5, abajo=20
        // Crea la etiqueta de bienvenida
        JLabel lblBienvenida = new JLabel("¡Bienvenido/a!");
        // Establece la fuente mediana sin negrita (Arial, Plain, 20pts)
        lblBienvenida.setFont(new Font("Arial", Font.PLAIN, 20));
        // Establece el color del texto en lavanda claro
        lblBienvenida.setForeground(new Color(230, 230, 250));
        // Agrega el mensaje al panel
        contenido.add(lblBienvenida, gbc);

        // ===== BARRA DE PROGRESO =====
        // Modifica los márgenes para la barra de progreso
        gbc.insets = new Insets(10, 40, 30, 40); // Arriba=10, abajo=30
        // Permite que el componente se expanda horizontalmente
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Crea la barra de progreso
        JProgressBar progressBar = new JProgressBar();
        // Establece en modo indeterminado (animación continua sin porcentaje)
        progressBar.setIndeterminate(true);
        // Establece el tamaño preferido (300 píxeles de ancho, 20 de alto)
        progressBar.setPreferredSize(new Dimension(300, 20));
        // Establece el color de fondo de la barra en blanco
        progressBar.setBackground(Color.WHITE);
        // Establece el color de la animación en verde
        progressBar.setForeground(new Color(40, 167, 69));
        // Agrega la barra de progreso al panel
        contenido.add(progressBar, gbc);

        // ===== CONFIGURAR EL JDIALOG =====
        // Establece el panel creado como contenido del diálogo
        setContentPane(contenido);
        // Define el tamaño de la ventana (500 píxeles de ancho, 450 de alto)
        setSize(500, 450);
        // Centra la ventana respecto al Frame propietario
        setLocationRelativeTo(owner);
        // Mantiene la ventana siempre visible sobre otras ventanas
        setAlwaysOnTop(true);
    }
}