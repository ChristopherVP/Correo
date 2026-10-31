package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.Usuario;
import uce.edu.ec.correo.util.IconoUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel principal que contiene el menú de navegación, barra superior con información del usuario
 * y el área de contenido donde se muestran los diferentes paneles (Correos, Contactos, Grupos, etc.).
 */
public class PanelMenuPrincipal extends JPanel {
    // ========== REFERENCIAS A COMPONENTES PRINCIPALES ==========

    // Referencia a la ventana principal
    private VentanaPrincipal ventanaPrincipal;
    // Referencia a la empresa para acceder a datos y operaciones
    private EmpresaCorreo empresa;

    // ========== COMPONENTES DE LA INTERFAZ ==========

    // Label que muestra el mensaje de bienvenida con el nombre del usuario
    private JLabel lblBienvenida;
    // Panel que contiene todos los paneles secundarios (usa CardLayout)
    private JPanel panelContenido;
    // Layout de tarjetas para cambiar entre paneles
    private CardLayout cardLayoutContenido;

    // Paneles secundarios para cada sección
    private PanelCorreos panelCorreos;           // Panel de gestión de correos
    private PanelContactos panelContactos;       // Panel de gestión de contactos
    private PanelGrupos panelGrupos;            // Panel de gestión de grupos
    private PanelInformacionUsuario panelInfoUsuario;  // Panel de información personal

    // Botones de la barra superior
    private JButton btnUsuario;           // Botón que abre el menú de usuario
    private JPopupMenu menuUsuario;       // Menú desplegable del usuario
    private JButton btnMenuHamburguesa;   // Botón que abre/cierra el menú lateral

    // ========== VARIABLES PARA EL MENÚ EXPANDIBLE ==========

    // Panel que contiene todo el menú lateral expandible
    private JPanel panelMenuExpandido;
    // Scroll pane que envuelve el menú expandible
    private JScrollPane scrollMenuExpandido;
    // Indica si el menú lateral está visible o no
    private boolean menuVisible = false;
    // Indica si la sección de correos está expandida
    private boolean menuCorreosExpandido = false;
    // Indica si la sección de contactos está expandida
    private boolean menuContactosExpandido = false;
    // Indica si la sección de grupos está expandida
    private boolean menuGruposExpandido = false;

    /**
     * Constructor del panel de menú principal.
     * @param ventanaPrincipal Referencia a la ventana principal
     * @param empresa Instancia de EmpresaCorreo
     */
    public PanelMenuPrincipal(VentanaPrincipal ventanaPrincipal, EmpresaCorreo empresa) {
        // Almacena la referencia a la ventana principal
        this.ventanaPrincipal = ventanaPrincipal;
        // Almacena la referencia a la empresa
        this.empresa = empresa;
        // Inicializa todos los componentes gráficos
        inicializarComponentes();
    }

    /**
     * Inicializa y configura todos los componentes del panel.
     */
    private void inicializarComponentes() {
        // Establece BorderLayout con espaciado de 10 píxeles
        setLayout(new BorderLayout(10, 10));

        // ========== PANEL SUPERIOR (BARRA DE NAVEGACIÓN) ==========

        // Crea panel superior con BorderLayout
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        // Color azul característico de la aplicación
        panelSuperior.setBackground(new Color(0, 102, 204));
        // Añade márgenes internos (15 arriba/abajo, 20 izquierda/derecha)
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ========== PANEL IZQUIERDO (MENÚ HAMBURGUESA) ==========

        // Crea panel para el lado izquierdo con FlowLayout
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        // Mismo color de fondo que el panel superior
        panelIzquierdo.setBackground(new Color(0, 102, 204));

        // Crea botón de menú hamburguesa
        btnMenuHamburguesa = new JButton();
        // Establece icono de menú (3 líneas horizontales)
        btnMenuHamburguesa.setIcon(IconoUtils.iconoMenu(24));
        // Color de fondo azul más oscuro
        btnMenuHamburguesa.setBackground(new Color(0, 82, 184));
        // Desactiva el borde de enfoque
        btnMenuHamburguesa.setFocusPainted(false);
        // Oculta el borde del botón
        btnMenuHamburguesa.setBorderPainted(false);
        // Establece tamaño preferido de 50x50 píxeles
        btnMenuHamburguesa.setPreferredSize(new Dimension(50, 50));
        // Cambia el cursor a mano al pasar sobre el botón
        btnMenuHamburguesa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Tooltip que aparece al pasar el mouse
        btnMenuHamburguesa.setToolTipText("Menú");
        // Añade listener que ejecuta toggleMenuHamburguesa() al hacer clic
        btnMenuHamburguesa.addActionListener(e -> toggleMenuHamburguesa());
        // Añade el botón al panel izquierdo
        panelIzquierdo.add(btnMenuHamburguesa);

        // Añade el panel izquierdo al oeste del panel superior
        panelSuperior.add(panelIzquierdo, BorderLayout.WEST);

        // ========== BIENVENIDA EN EL CENTRO ==========

        // Crea label para el mensaje de bienvenida
        lblBienvenida = new JLabel();
        // Establece fuente en negrita tamaño 16
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        // Color blanco para contraste con el fondo azul
        lblBienvenida.setForeground(Color.WHITE);
        // Centra el texto horizontalmente
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);
        // Añade el label al centro del panel superior
        panelSuperior.add(lblBienvenida, BorderLayout.CENTER);

        // ========== BOTÓN DE USUARIO A LA DERECHA ==========

        // Crea panel para el lado derecho
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        // Mismo color de fondo
        panelDerecho.setBackground(new Color(0, 102, 204));

        // Crea botón de usuario
        btnUsuario = new JButton();
        // Establece icono de usuario (silueta)
        btnUsuario.setIcon(IconoUtils.iconoUsuario(28));
        // Color de fondo azul oscuro
        btnUsuario.setBackground(new Color(0, 82, 184));
        // Desactiva el borde de enfoque
        btnUsuario.setFocusPainted(false);
        // Oculta el borde del botón
        btnUsuario.setBorderPainted(false);
        // Tamaño de 50x50 píxeles
        btnUsuario.setPreferredSize(new Dimension(50, 50));
        // Cursor de mano
        btnUsuario.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Tooltip explicativo
        btnUsuario.setToolTipText("Menú de usuario");

        // ========== MENÚ DESPLEGABLE DE USUARIO ==========

        // Crea menú popup (desplegable)
        menuUsuario = new JPopupMenu();
        // Añade borde gris claro
        menuUsuario.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // ========== OPCIÓN "MI INFORMACIÓN" ==========

        // Crea ítem de menú "Mi Información"
        JMenuItem itemMiInfo = new JMenuItem("Mi Información");
        // Establece fuente
        itemMiInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        // Añade listener que ejecuta irAMiInformacion()
        itemMiInfo.addActionListener(e -> irAMiInformacion());
        // Añade el ítem al menú
        menuUsuario.add(itemMiInfo);

        // Añade separador visual
        menuUsuario.addSeparator();

        // ========== OPCIÓN "CERRAR SESIÓN" ==========

        // Crea ítem de menú "Cerrar Sesión"
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        // Establece fuente
        itemCerrarSesion.setFont(new Font("Arial", Font.PLAIN, 14));
        // Color rojo para indicar acción de cierre
        itemCerrarSesion.setForeground(new Color(220, 53, 69));
        // Añade listener que ejecuta cerrarSesion()
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        // Añade el ítem al menú
        menuUsuario.add(itemCerrarSesion);

        // Configura el botón para mostrar el menú al hacer clic
        btnUsuario.addActionListener(e -> menuUsuario.show(btnUsuario, 0, btnUsuario.getHeight()));

        // Añade el botón al panel derecho
        panelDerecho.add(btnUsuario);
        // Añade el panel derecho al este del panel superior
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

        // Añade el panel superior al norte del panel principal
        add(panelSuperior, BorderLayout.NORTH);

        // ========== PANEL DE CONTENIDO CON CARDLAYOUT ==========

        // Crea CardLayout para cambiar entre paneles
        cardLayoutContenido = new CardLayout();
        // Crea panel que contendrá todos los subpaneles
        panelContenido = new JPanel(cardLayoutContenido);

        // Crea instancias de todos los paneles secundarios
        panelCorreos = new PanelCorreos(empresa);
        panelContactos = new PanelContactos(empresa);
        panelGrupos = new PanelGrupos(empresa, panelContactos);
        panelInfoUsuario = new PanelInformacionUsuario(empresa);
        // Establece referencias necesarias para el panel de información
        panelInfoUsuario.setVentanaPrincipal(ventanaPrincipal);
        panelInfoUsuario.setPanelMenuPrincipal(this);

        // Añade cada panel con su identificador único
        panelContenido.add(panelCorreos, "CORREOS");
        panelContenido.add(panelContactos, "CONTACTOS");
        panelContenido.add(panelGrupos, "GRUPOS");
        panelContenido.add(panelInfoUsuario, "MI_INFO");

        // Añade el panel de contenido al centro del panel principal
        add(panelContenido, BorderLayout.CENTER);

        // Muestra el panel de correos por defecto
        cardLayoutContenido.show(panelContenido, "CORREOS");
    }

    /**
     * Alterna la visibilidad del menú hamburguesa (mostrar/ocultar).
     */
    private void toggleMenuHamburguesa() {
        // Si el menú está visible
        if (menuVisible) {
            // Oculta el menú
            ocultarMenu();
        } else {
            // Muestra el menú
            mostrarMenuExpandible();
        }
    }

    /**
     * Muestra el menú lateral expandible con todas las opciones.
     */
    private void mostrarMenuExpandible() {
        // Si ya existe un scroll de menú, lo elimina
        if (scrollMenuExpandido != null) {
            remove(scrollMenuExpandido);
        }

        // Crea nuevo panel para el menú expandido
        panelMenuExpandido = new JPanel();
        // Establece layout vertical (componentes uno debajo de otro)
        panelMenuExpandido.setLayout(new BoxLayout(panelMenuExpandido, BoxLayout.Y_AXIS));
        // Color de fondo gris muy claro
        panelMenuExpandido.setBackground(new Color(250, 250, 250));
        // Añade borde: línea gris + márgenes internos
        panelMenuExpandido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Resetea los estados de expansión de todas las secciones
        menuCorreosExpandido = false;
        menuContactosExpandido = false;
        menuGruposExpandido = false;

        // Crea las tres secciones principales del menú
        crearSeccionCorreos();
        crearSeccionContactos();
        crearSeccionGrupos();

        // Envuelve el panel en un scroll pane
        scrollMenuExpandido = new JScrollPane(panelMenuExpandido);
        // Sin borde
        scrollMenuExpandido.setBorder(null);
        // Ancho preferido de 280 píxeles, altura flexible
        scrollMenuExpandido.setPreferredSize(new Dimension(280, 0));
        // Muestra barra de scroll vertical solo cuando sea necesario
        scrollMenuExpandido.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Nunca muestra barra de scroll horizontal
        scrollMenuExpandido.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Incremento de desplazamiento de 16 píxeles
        scrollMenuExpandido.getVerticalScrollBar().setUnitIncrement(16);

        // Añade el scroll pane al oeste del panel principal
        add(scrollMenuExpandido, BorderLayout.WEST);
        // Marca el menú como visible
        menuVisible = true;
        // Recalcula el layout
        revalidate();
        // Repinta el componente
        repaint();
    }

    /**
     * Crea la sección de CORREOS del menú lateral.
     */
    private void crearSeccionCorreos() {
        // ========== BOTÓN PRINCIPAL CORREOS ==========

        // Crea botón principal con texto e icono
        JButton btnCorreos = crearBotonPrincipal(" Correos", IconoUtils.iconoCorreo(18));
        // Añade listener que expande/contrae la sección
        btnCorreos.addActionListener(e -> toggleSeccionCorreos());
        // Añade el botón al panel del menú
        panelMenuExpandido.add(btnCorreos);

        // ========== PANEL DE OPCIONES (INICIALMENTE OCULTO) ==========

        // Crea panel para las opciones de correos
        JPanel panelOpcionesCorreos = new JPanel();
        // Layout vertical
        panelOpcionesCorreos.setLayout(new BoxLayout(panelOpcionesCorreos, BoxLayout.Y_AXIS));
        // Color de fondo ligeramente diferente
        panelOpcionesCorreos.setBackground(new Color(245, 245, 245));
        // Inicialmente invisible
        panelOpcionesCorreos.setVisible(false);
        // Establece nombre para identificar el panel
        panelOpcionesCorreos.setName("opcionesCorreos");

        // ========== OPCIÓN: REDACTAR ==========

        // Añade botón "Redactar" que abre el diálogo de nuevo correo
        panelOpcionesCorreos.add(crearBotonOpcion(" Redactar", IconoUtils.iconoRedactar(16), () -> {
            // Muestra el panel de correos
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Abre el diálogo de nuevo correo
            panelCorreos.abrirNuevoCorreo();
        }));

        // ========== OPCIÓN: TODOS LOS CORREOS ==========

        panelOpcionesCorreos.add(crearBotonOpcion(" Todos los Correos", IconoUtils.iconoLista(16), () -> {
            // Muestra el panel de correos
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Muestra todos los correos
            panelCorreos.mostrarTodos();
        }));

        // Añade espacio vertical de 5 píxeles
        panelOpcionesCorreos.add(Box.createVerticalStrut(5));

        // ========== OPCIÓN: DESTACADOS ==========

        panelOpcionesCorreos.add(crearBotonOpcion(" Destacados", IconoUtils.iconoDestacados(16), () -> {
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Muestra solo correos destacados (estrella)
            panelCorreos.mostrarDestacados();
        }));

        // Añade espacio vertical
        panelOpcionesCorreos.add(Box.createVerticalStrut(5));

        // ========== OPCIÓN: ENVIADOS ==========

        panelOpcionesCorreos.add(crearBotonOpcion(" Enviados", IconoUtils.iconoEnviados(16), () -> {
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Muestra correos enviados
            panelCorreos.mostrarEnviados();
        }));

        // ========== OPCIÓN: RECIBIDOS ==========

        panelOpcionesCorreos.add(crearBotonOpcion(" Recibidos", IconoUtils.iconoRecibidos(16), () -> {
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Muestra correos recibidos
            panelCorreos.mostrarRecibidos();
        }));

        // ========== OPCIÓN: BORRADORES ==========

        panelOpcionesCorreos.add(crearBotonOpcion(" Borradores", IconoUtils.iconoBorradores(16), () -> {
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Muestra borradores (correos no enviados)
            panelCorreos.mostrarBorradores();
        }));

        // ========== OPCIÓN: REENVIADOS ==========

        panelOpcionesCorreos.add(crearBotonOpcion(" Reenviados", IconoUtils.iconoReenviar(16), () -> {
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Muestra correos reenviados
            panelCorreos.mostrarReenviados();
        }));

        // ========== OPCIÓN: ELIMINADOS ==========

        panelOpcionesCorreos.add(crearBotonOpcion(" Eliminados", IconoUtils.iconoEliminar(16), () -> {
            cardLayoutContenido.show(panelContenido, "CORREOS");
            // Muestra correos eliminados (papelera)
            panelCorreos.mostrarEliminados();
        }));

        // Añade el panel de opciones al menú expandido
        panelMenuExpandido.add(panelOpcionesCorreos);
    }

    /**
     * Crea la sección de CONTACTOS del menú lateral.
     */
    private void crearSeccionContactos() {
        // ========== BOTÓN PRINCIPAL CONTACTOS ==========

        // Crea botón principal con texto e icono
        JButton btnContactos = crearBotonPrincipal(" Contactos", IconoUtils.iconoContactos(18));
        // Añade listener que expande/contrae la sección
        btnContactos.addActionListener(e -> toggleSeccionContactos());
        // Añade el botón al panel del menú
        panelMenuExpandido.add(btnContactos);

        // ========== PANEL DE OPCIONES (INICIALMENTE OCULTO) ==========

        // Crea panel para las opciones de contactos
        JPanel panelOpcionesContactos = new JPanel();
        // Layout vertical
        panelOpcionesContactos.setLayout(new BoxLayout(panelOpcionesContactos, BoxLayout.Y_AXIS));
        // Color de fondo
        panelOpcionesContactos.setBackground(new Color(245, 245, 245));
        // Inicialmente invisible
        panelOpcionesContactos.setVisible(false);
        // Nombre para identificar el panel
        panelOpcionesContactos.setName("opcionesContactos");

        // ========== OPCIÓN: AGREGAR CONTACTO ==========

        panelOpcionesContactos.add(crearBotonOpcion(" Agregar Contacto", IconoUtils.iconoAgregar(16), () -> {
            // Muestra el panel de contactos
            cardLayoutContenido.show(panelContenido, "CONTACTOS");
            // Abre el diálogo para agregar contacto
            panelContactos.abrirAgregarContacto();
        }));

        // ========== OPCIÓN: VER TODOS LOS CONTACTOS ==========

        panelOpcionesContactos.add(crearBotonOpcion(" Ver Todos los Contactos", IconoUtils.iconoOjoVisible(16), () -> {
            // Muestra el panel de contactos
            cardLayoutContenido.show(panelContenido, "CONTACTOS");
            // Refresca la lista de contactos
            panelContactos.refrescar();
        }));

        // Añade el panel de opciones al menú expandido
        panelMenuExpandido.add(panelOpcionesContactos);
    }

    /**
     * Crea la sección de GRUPOS del menú lateral.
     */
    private void crearSeccionGrupos() {
        // ========== BOTÓN PRINCIPAL GRUPOS ==========

        // Crea botón principal con texto e icono
        JButton btnGrupos = crearBotonPrincipal(" Grupos", IconoUtils.iconoGrupos(18));
        // Añade listener que expande/contrae la sección
        btnGrupos.addActionListener(e -> toggleSeccionGrupos());
        // Añade el botón al panel del menú
        panelMenuExpandido.add(btnGrupos);

        // ========== PANEL DE OPCIONES (INICIALMENTE OCULTO) ==========

        // Crea panel para las opciones de grupos
        JPanel panelOpcionesGrupos = new JPanel();
        // Layout vertical
        panelOpcionesGrupos.setLayout(new BoxLayout(panelOpcionesGrupos, BoxLayout.Y_AXIS));
        // Color de fondo
        panelOpcionesGrupos.setBackground(new Color(245, 245, 245));
        // Inicialmente invisible
        panelOpcionesGrupos.setVisible(false);
        // Nombre para identificar el panel
        panelOpcionesGrupos.setName("opcionesGrupos");

        // ========== OPCIÓN: CREAR GRUPO ==========

        panelOpcionesGrupos.add(crearBotonOpcion(" Crear Grupo", IconoUtils.iconoAgregar(16), () -> {
            // Muestra el panel de grupos
            cardLayoutContenido.show(panelContenido, "GRUPOS");
            // Abre el diálogo para crear grupo
            panelGrupos.abrirCrearGrupo();
        }));

        // ========== OPCIÓN: CHATS GRUPALES ==========

        panelOpcionesGrupos.add(crearBotonOpcion(" Chats Grupales", IconoUtils.iconoChat(16), () -> {
            // Muestra el panel de grupos
            cardLayoutContenido.show(panelContenido, "GRUPOS");
            // Refresca la lista de grupos
            panelGrupos.refrescar();
        }));

        // Añade el panel de opciones al menú expandido
        panelMenuExpandido.add(panelOpcionesGrupos);
    }

    /**
     * Crea un botón principal para las secciones del menú (Correos, Contactos, Grupos).
     * @param texto Texto del botón
     * @param icono Icono del botón
     * @return Botón configurado
     */
    private JButton crearBotonPrincipal(String texto, ImageIcon icono) {
        // Crea botón con texto e icono
        JButton btn = new JButton(texto, icono);
        // Fuente en negrita tamaño 14
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        // Fondo gris claro
        btn.setBackground(new Color(240, 240, 240));
        // Texto negro
        btn.setForeground(Color.BLACK);
        // Desactiva borde de enfoque
        btn.setFocusPainted(false);
        // Alineación a la izquierda
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Ancho máximo (se expande), altura fija de 45 píxeles
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        // Borde: línea inferior gris + márgenes internos
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        // Alineación del contenido a la izquierda
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        // Espacio de 10 píxeles entre icono y texto
        btn.setIconTextGap(10);
        // Cursor de mano
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Añade efecto hover (cambio de color al pasar el mouse)
        btn.addMouseListener(new MouseAdapter() {
            /**
             * Se ejecuta cuando el mouse entra en el área del botón.
             */
            public void mouseEntered(MouseEvent e) {
                // Cambia a gris más oscuro
                btn.setBackground(new Color(220, 220, 220));
            }

            /**
             * Se ejecuta cuando el mouse sale del área del botón.
             */
            public void mouseExited(MouseEvent e) {
                // Vuelve al color original
                btn.setBackground(new Color(240, 240, 240));
            }
        });

        // Retorna el botón configurado
        return btn;
    }

    /**
     * Crea un botón de opción para las subsecciones del menú.
     * @param texto Texto del botón
     * @param icono Icono del botón
     * @param accion Acción a ejecutar al hacer clic
     * @return Botón configurado
     */
    private JButton crearBotonOpcion(String texto, ImageIcon icono, Runnable accion) {
        // Crea botón con texto e icono
        JButton btn = new JButton(texto, icono);
        // Fuente normal tamaño 13
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        // Fondo gris muy claro
        btn.setBackground(new Color(245, 245, 245));
        // Texto negro
        btn.setForeground(Color.BLACK);
        // Desactiva borde de enfoque
        btn.setFocusPainted(false);
        // Alineación a la izquierda
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Ancho máximo, altura de 38 píxeles
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        // Márgenes internos (30 píxeles a la izquierda para indentar)
        btn.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 15));
        // Alineación del contenido a la izquierda
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        // Espacio de 8 píxeles entre icono y texto
        btn.setIconTextGap(8);
        // Cursor de mano
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Añade efecto hover
        btn.addMouseListener(new MouseAdapter() {
            /**
             * Se ejecuta cuando el mouse entra en el botón.
             */
            public void mouseEntered(MouseEvent e) {
                // Cambia a gris más oscuro
                btn.setBackground(new Color(230, 230, 230));
            }

            /**
             * Se ejecuta cuando el mouse sale del botón.
             */
            public void mouseExited(MouseEvent e) {
                // Vuelve al color original
                btn.setBackground(new Color(245, 245, 245));
            }
        });

        // Añade listener que ejecuta la acción proporcionada
        btn.addActionListener(e -> accion.run());

        // Retorna el botón configurado
        return btn;
    }

    /**
     * Alterna la expansión/contracción de la sección de correos.
     */
    private void toggleSeccionCorreos() {
        // Invierte el estado de expansión
        menuCorreosExpandido = !menuCorreosExpandido;
        // Actualiza la visibilidad del panel de opciones
        actualizarVisibilidadOpciones("opcionesCorreos", menuCorreosExpandido);
    }

    /**
     * Alterna la expansión/contracción de la sección de contactos.
     */
    private void toggleSeccionContactos() {
        // Invierte el estado de expansión
        menuContactosExpandido = !menuContactosExpandido;
        // Actualiza la visibilidad del panel de opciones
        actualizarVisibilidadOpciones("opcionesContactos", menuContactosExpandido);
    }

    /**
     * Alterna la expansión/contracción de la sección de grupos.
     */
    private void toggleSeccionGrupos() {
        // Invierte el estado de expansión
        menuGruposExpandido = !menuGruposExpandido;
        // Actualiza la visibilidad del panel de opciones
        actualizarVisibilidadOpciones("opcionesGrupos", menuGruposExpandido);
    }

    /**
     * Actualiza la visibilidad de un panel de opciones específico.
     * @param nombre Nombre del panel a mostrar/ocultar
     * @param visible true para mostrar, false para ocultar
     */
    private void actualizarVisibilidadOpciones(String nombre, boolean visible) {
        // Itera sobre todos los componentes del menú expandido
        for (Component comp : panelMenuExpandido.getComponents()) {
            // Si es un JPanel y tiene el nombre buscado
            if (comp instanceof JPanel && nombre.equals(comp.getName())) {
                // Establece la visibilidad
                comp.setVisible(visible);
                // Recalcula el layout del menú
                panelMenuExpandido.revalidate();
                // Repinta el menú
                panelMenuExpandido.repaint();
                // Sale del bucle
                break;
            }
        }
    }

    /**
     * Oculta el menú lateral expandible.
     */
    private void ocultarMenu() {
        // Si existe el scroll del menú
        if (scrollMenuExpandido != null) {
            // Elimina el scroll del panel
            remove(scrollMenuExpandido);
            // Limpia la referencia
            scrollMenuExpandido = null;
            // Marca el menú como no visible
            menuVisible = false;
            // Recalcula el layout
            revalidate();
            // Repinta el panel
            repaint();
        }
    }

    /**
     * Actualiza el mensaje de bienvenida con el nombre del usuario actual.
     */
    public void actualizarBienvenida() {
        // Si hay un usuario logueado
        if (empresa.hayUsuarioLogueado()) {
            // Obtiene el usuario actual
            Usuario usuario = empresa.getUsuarioActual();
            // Establece el texto de bienvenida con el nombre completo
            lblBienvenida.setText("Bienvenido/a: " + usuario.getNombreCompleto());
            // Refresca el panel de correos
            panelCorreos.refrescar();
            // Muestra el panel de correos
            cardLayoutContenido.show(panelContenido, "CORREOS");
        }
    }

    /**
     * Navega al panel de información del usuario.
     */
    private void irAMiInformacion() {
        // Muestra el panel de información del usuario
        cardLayoutContenido.show(panelContenido, "MI_INFO");
        // Actualiza la información mostrada
        panelInfoUsuario.actualizarInformacion();
        // Desactiva el botón de menú hamburguesa
        btnMenuHamburguesa.setEnabled(false);
        // Desactiva el botón de usuario
        btnUsuario.setEnabled(false);
        // Oculta el menú lateral si está visible
        ocultarMenu();
    }

    /**
     * Regresa al menú principal desde la sección de información de usuario.
     */
    public void volverAlMenuPrincipal() {
        // Muestra el panel de correos
        cardLayoutContenido.show(panelContenido, "CORREOS");
        // Refresca el panel de correos
        panelCorreos.refrescar();
        // Reactiva el botón de menú hamburguesa
        btnMenuHamburguesa.setEnabled(true);
        // Reactiva el botón de usuario
        btnUsuario.setEnabled(true);
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    private void cerrarSesion() {
        // Muestra diálogo de confirmación
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION);

        // Si el usuario confirmó
        if (opcion == JOptionPane.YES_OPTION) {
            // Cierra la sesión en la empresa
            empresa.cerrarSesion();
            // Oculta el menú lateral
            ocultarMenu();
            // Muestra el panel de login
            ventanaPrincipal.mostrarPanel("LOGIN");
        }
    }

    /**
     * Sobreescribe setVisible para actualizar la bienvenida cuando se muestra el panel.
     */
    @Override
    public void setVisible(boolean visible) {
        // Llama al método padre
        super.setVisible(visible);
        // Si se está haciendo visible
        if (visible) {
            // Actualiza el mensaje de bienvenida
            actualizarBienvenida();
        }
    }
}