package uce.edu.ec.correo.util;

import uce.edu.ec.correo.dominio.ArchivoAdjunto;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;

/**
 * Clase utilitaria para visualizar y descargar archivos adjuntos.
 * Proporciona métodos para mostrar imágenes en un diálogo y guardar archivos en el sistema.
 */
public class VisorDocumentos {

    /**
     * Método principal que decide cómo visualizar un archivo según su tipo.
     * @param parent Componente padre para centrar los diálogos
     * @param archivo Objeto ArchivoAdjunto que contiene la información del archivo
     */
    public static void visualizar(Component parent, ArchivoAdjunto archivo) {
        // Switch que determina la acción según el tipo de archivo
        switch (archivo.getTipo()) {
            case IMAGEN:
                // Si es imagen, la muestra en un diálogo visual
                visualizarImagen(parent, archivo);
                break;
            case VIDEO:
            case DOCUMENTO:
            case OTRO:
                // Para videos, documentos y otros tipos, descarga el archivo
                descargarArchivo(parent, archivo);
                break;
        }
    }

    /**
     * Muestra una imagen en un diálogo modal con opciones de guardar y cerrar.
     * @param parent Componente padre para centrar el diálogo
     * @param archivo ArchivoAdjunto que contiene la imagen a visualizar
     */
    private static void visualizarImagen(Component parent, ArchivoAdjunto archivo) {
        // Crea un diálogo modal con el título que incluye el nombre del archivo
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent),
                "Imagen: " + archivo.getNombreArchivo(), true);
        // Establece el layout BorderLayout para organizar los componentes
        dialogo.setLayout(new BorderLayout());

        try {
            // Crea un ImageIcon a partir del contenido binario del archivo
            ImageIcon iconoOriginal = new ImageIcon(archivo.getContenido());
            // Obtiene la imagen del icono para poder escalarla
            Image imagenOriginal = iconoOriginal.getImage();

            // Obtiene las dimensiones originales de la imagen
            int anchoOriginal = iconoOriginal.getIconWidth();
            int altoOriginal = iconoOriginal.getIconHeight();

            // Define los límites máximos para mostrar la imagen
            int anchoMax = 900;
            int altoMax = 700;

            // Calcula la escala necesaria para ajustar la imagen al ancho máximo
            double escalaAncho = (double) anchoMax / anchoOriginal;
            // Calcula la escala necesaria para ajustar la imagen al alto máximo
            double escalaAlto = (double) altoMax / altoOriginal;
            // Toma la escala menor para mantener la proporción
            double escala = Math.min(escalaAncho, escalaAlto);
            // Limita la escala a 1.0 para no agrandar imágenes pequeñas
            escala = Math.min(escala, 1.0);

            // Calcula las nuevas dimensiones aplicando la escala
            int nuevoAncho = (int) (anchoOriginal * escala);
            int nuevoAlto = (int) (altoOriginal * escala);

            // Escala la imagen usando algoritmo de suavizado para mejor calidad
            Image imagenEscalada = imagenOriginal.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
            // Crea un nuevo icono con la imagen escalada
            ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);

            // Crea un JLabel que contendrá la imagen
            JLabel lblImagen = new JLabel(iconoEscalado);
            // Centra la imagen horizontalmente en el label
            lblImagen.setHorizontalAlignment(JLabel.CENTER);

            // Envuelve el label en un JScrollPane para permitir desplazamiento si es necesario
            JScrollPane scrollPane = new JScrollPane(lblImagen);
            // Añade el scroll pane al centro del diálogo
            dialogo.add(scrollPane, BorderLayout.CENTER);

            // ========== Panel de información superior ==========
            // Crea un panel para mostrar información del archivo
            JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            // Establece un color de fondo azul claro
            panelInfo.setBackground(new Color(240, 248, 255));
            // Añade un borde vacío para espaciado interno
            panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Crea un label para el icono del tipo de archivo
            JLabel lblIcono = new JLabel();
            // Establece el icono de imagen con tamaño 24x24
            lblIcono.setIcon(IconoUtils.iconoImagen(24));

            // Construye la cadena de información del archivo (nombre, tamaño y dimensiones)
            String info = archivo.getNombreArchivo() +
                    " | " + archivo.getTamanoFormateado() +
                    " | " + anchoOriginal + " x " + altoOriginal + " px";

            // Crea un label para mostrar la información
            JLabel lblInfo = new JLabel(info);
            // Establece la fuente en Arial, negrita, tamaño 12
            lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
            // Establece el color del texto
            lblInfo.setForeground(new Color(60, 60, 60));

            // Añade el icono y la información al panel
            panelInfo.add(lblIcono);
            panelInfo.add(lblInfo);

            // Añade el panel de información en la parte superior del diálogo
            dialogo.add(panelInfo, BorderLayout.NORTH);

            // ========== Panel de botones inferior ==========
            // Crea un panel para los botones de acción
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
            // Establece fondo blanco
            panelBotones.setBackground(Color.WHITE);

            // ========== Botón Guardar ==========
            // Crea el botón guardar con texto e icono
            JButton btnGuardar = new JButton("Guardar Imagen", IconoUtils.iconoAdjuntar(16));
            // Establece la fuente
            btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
            // Establece el color del texto
            btnGuardar.setForeground(Color.BLACK);
            // Desactiva el borde de enfoque
            btnGuardar.setFocusPainted(false);
            // Establece el tamaño preferido del botón
            btnGuardar.setPreferredSize(new Dimension(160, 38));
            // Posiciona el texto a la derecha del icono
            btnGuardar.setHorizontalTextPosition(SwingConstants.RIGHT);
            // Establece el espacio entre icono y texto
            btnGuardar.setIconTextGap(8);
            // Establece un borde compuesto (línea verde + espacio interno)
            btnGuardar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(40, 167, 69), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            // Añade el listener que ejecuta guardarArchivo cuando se hace clic
            btnGuardar.addActionListener(e -> guardarArchivo(dialogo, archivo));
            // Añade el botón al panel
            panelBotones.add(btnGuardar);

            // ========== Botón Cerrar ==========
            // Crea el botón cerrar con texto e icono
            JButton btnCerrar = new JButton("Cerrar", IconoUtils.iconoCerrar(16));
            // Establece la fuente
            btnCerrar.setFont(new Font("Arial", Font.BOLD, 13));
            // Establece el color del texto
            btnCerrar.setForeground(Color.BLACK);
            // Desactiva el borde de enfoque
            btnCerrar.setFocusPainted(false);
            // Establece el tamaño preferido del botón
            btnCerrar.setPreferredSize(new Dimension(120, 38));
            // Posiciona el texto a la derecha del icono
            btnCerrar.setHorizontalTextPosition(SwingConstants.RIGHT);
            // Establece el espacio entre icono y texto
            btnCerrar.setIconTextGap(8);
            // Establece un borde compuesto (línea gris + espacio interno)
            btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(108, 117, 125), 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            // Añade el listener que cierra el diálogo cuando se hace clic
            btnCerrar.addActionListener(e -> dialogo.dispose());
            // Añade el botón al panel
            panelBotones.add(btnCerrar);

            // Añade el panel de botones en la parte inferior del diálogo
            dialogo.add(panelBotones, BorderLayout.SOUTH);

            // Establece el tamaño del diálogo (limitado a los máximos definidos)
            dialogo.setSize(Math.min(nuevoAncho + 50, 950), Math.min(nuevoAlto + 150, 820));
            // Centra el diálogo respecto al componente padre
            dialogo.setLocationRelativeTo(parent);
            // Hace visible el diálogo (bloquea la ejecución por ser modal)
            dialogo.setVisible(true);

        } catch (Exception e) {
            // Muestra un mensaje de error si falla la visualización
            JOptionPane.showMessageDialog(parent,
                    "Error al visualizar la imagen: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Permite al usuario descargar un archivo a su sistema de archivos.
     * @param parent Componente padre para centrar los diálogos
     * @param archivo ArchivoAdjunto que se va a descargar
     */
    private static void descargarArchivo(Component parent, ArchivoAdjunto archivo) {
        // Crea un selector de archivos para elegir dónde guardar
        JFileChooser fileChooser = new JFileChooser();
        // Establece el título del diálogo
        fileChooser.setDialogTitle("Guardar Archivo - " + archivo.getNombreArchivo());
        // Establece el nombre de archivo sugerido
        fileChooser.setSelectedFile(new File(archivo.getNombreArchivo()));

        // Verifica si el padre es un JDialog para configurar apropiadamente
        if (parent instanceof JDialog) {
            JDialog parentDialog = (JDialog) parent;
            // Configura el tipo de diálogo como guardar
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        }

        // Muestra el diálogo de guardar y captura la respuesta del usuario
        int resultado = fileChooser.showSaveDialog(parent);
        // Si el usuario aprobó la operación (hizo clic en Guardar)
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Obtiene el archivo destino seleccionado
            File archivoDestino = fileChooser.getSelectedFile();

            // Try-with-resources: crea FileOutputStream que se cerrará automáticamente
            try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                // Escribe el contenido del archivo adjunto en el archivo destino
                fos.write(archivo.getContenido());

                // Verifica si Desktop está soportado en este sistema
                if (Desktop.isDesktopSupported()) {
                    // ========== Panel de éxito con opción de abrir ==========
                    // Crea panel principal con layout BorderLayout
                    JPanel panel = new JPanel(new BorderLayout(10, 10));
                    // Añade un borde vacío para espaciado
                    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    // ========== Panel superior con icono y mensaje ==========
                    JPanel panelMensaje = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    // Crea label con icono del tipo de archivo (32x32)
                    JLabel lblIconoExito = new JLabel(IconoUtils.iconoParaArchivo(archivo, 32));
                    // Crea label con mensaje de éxito
                    JLabel lblMensaje = new JLabel("Archivo guardado exitosamente");
                    // Establece fuente en negrita
                    lblMensaje.setFont(new Font("Arial", Font.BOLD, 14));
                    // Establece color verde para indicar éxito
                    lblMensaje.setForeground(new Color(40, 167, 69));
                    // Añade los componentes al panel de mensaje
                    panelMensaje.add(lblIconoExito);
                    panelMensaje.add(lblMensaje);

                    // ========== Panel central con información de ubicación ==========
                    JPanel panelInfo = new JPanel(new BorderLayout(5, 5));
                    // Crea label para indicar la ubicación
                    JLabel lblUbicacion = new JLabel("Ubicación:");
                    lblUbicacion.setFont(new Font("Arial", Font.PLAIN, 12));
                    // Crea área de texto para mostrar la ruta completa del archivo
                    JTextArea txtRuta = new JTextArea(archivoDestino.getAbsolutePath());
                    // Hace que el área de texto no sea editable
                    txtRuta.setEditable(false);
                    // Activa el ajuste de línea
                    txtRuta.setLineWrap(true);
                    // Ajusta por palabras completas
                    txtRuta.setWrapStyleWord(true);
                    // Establece color de fondo gris claro
                    txtRuta.setBackground(new Color(245, 245, 245));
                    // Usa fuente monoespaciada para la ruta
                    txtRuta.setFont(new Font("Monospaced", Font.PLAIN, 11));
                    // Establece borde compuesto (línea + espaciado)
                    txtRuta.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                    // Añade los componentes al panel de información
                    panelInfo.add(lblUbicacion, BorderLayout.NORTH);
                    panelInfo.add(txtRuta, BorderLayout.CENTER);

                    // ========== Panel inferior con pregunta ==========
                    JLabel lblPregunta = new JLabel("¿Desea abrir el archivo ahora?");
                    lblPregunta.setFont(new Font("Arial", Font.PLAIN, 12));

                    // Organiza los paneles en el panel principal
                    panel.add(panelMensaje, BorderLayout.NORTH);
                    panel.add(panelInfo, BorderLayout.CENTER);
                    panel.add(lblPregunta, BorderLayout.SOUTH);

                    // Muestra diálogo de confirmación con botones Sí/No
                    int opcion = JOptionPane.showConfirmDialog(parent,
                            panel,
                            "Descarga Exitosa",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    // Si el usuario eligió Sí
                    if (opcion == JOptionPane.YES_OPTION) {
                        try {
                            // Intenta abrir el archivo con la aplicación predeterminada del sistema
                            Desktop.getDesktop().open(archivoDestino);
                        } catch (Exception ex) {
                            // Si falla, muestra mensaje informativo con la ubicación
                            JOptionPane.showMessageDialog(parent,
                                    "No se pudo abrir el archivo automáticamente.\n" +
                                            "Ábralo manualmente desde:\n" + archivoDestino.getAbsolutePath(),
                                    "Información",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } else {
                    // ========== Panel simple para sistemas sin Desktop ==========
                    JPanel panel = new JPanel(new BorderLayout(10, 10));
                    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    // Panel con icono y mensaje de éxito
                    JPanel panelMensaje = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    JLabel lblIconoExito = new JLabel(IconoUtils.iconoParaArchivo(archivo, 32));
                    JLabel lblMensaje = new JLabel("Archivo guardado exitosamente");
                    lblMensaje.setFont(new Font("Arial", Font.BOLD, 14));
                    lblMensaje.setForeground(new Color(40, 167, 69));
                    panelMensaje.add(lblIconoExito);
                    panelMensaje.add(lblMensaje);

                    // Panel con información de ubicación
                    JPanel panelInfo = new JPanel(new BorderLayout(5, 5));
                    JLabel lblUbicacion = new JLabel("Ubicación:");
                    lblUbicacion.setFont(new Font("Arial", Font.PLAIN, 12));
                    JTextArea txtRuta = new JTextArea(archivoDestino.getAbsolutePath());
                    txtRuta.setEditable(false);
                    txtRuta.setLineWrap(true);
                    txtRuta.setWrapStyleWord(true);
                    txtRuta.setBackground(new Color(245, 245, 245));
                    txtRuta.setFont(new Font("Monospaced", Font.PLAIN, 11));
                    txtRuta.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                    panelInfo.add(lblUbicacion, BorderLayout.NORTH);
                    panelInfo.add(txtRuta, BorderLayout.CENTER);

                    // Organiza los paneles
                    panel.add(panelMensaje, BorderLayout.NORTH);
                    panel.add(panelInfo, BorderLayout.CENTER);

                    // Muestra mensaje de información simple
                    JOptionPane.showMessageDialog(parent,
                            panel,
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception e) {
                // ========== Panel de error ==========
                JPanel panelError = new JPanel(new BorderLayout(10, 10));
                panelError.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Panel con icono y mensaje de error
                JPanel panelMensaje = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                // Usa icono de eliminar (X roja) para indicar error
                JLabel lblIconoError = new JLabel(IconoUtils.iconoEliminar(32));
                JLabel lblMensaje = new JLabel("Error al guardar el archivo");
                lblMensaje.setFont(new Font("Arial", Font.BOLD, 14));
                // Color rojo para indicar error
                lblMensaje.setForeground(new Color(220, 53, 69));
                panelMensaje.add(lblIconoError);
                panelMensaje.add(lblMensaje);

                // Área de texto con el mensaje de error detallado
                JTextArea txtError = new JTextArea(e.getMessage());
                txtError.setEditable(false);
                txtError.setLineWrap(true);
                txtError.setWrapStyleWord(true);
                // Fondo rosa claro para el error
                txtError.setBackground(new Color(255, 240, 240));
                txtError.setFont(new Font("Arial", Font.PLAIN, 11));
                // Borde rojo alrededor del mensaje de error
                txtError.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 53, 69)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));

                // Organiza los componentes del panel de error
                panelError.add(panelMensaje, BorderLayout.NORTH);
                panelError.add(txtError, BorderLayout.CENTER);

                // Muestra el diálogo de error
                JOptionPane.showMessageDialog(parent,
                        panelError,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Guarda un archivo en el sistema de archivos local (usado desde el visor de imágenes).
     * @param parent Componente padre para centrar los diálogos
     * @param archivo ArchivoAdjunto que se va a guardar
     */
    private static void guardarArchivo(Component parent, ArchivoAdjunto archivo) {
        // Crea un selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        // Establece el título del diálogo
        fileChooser.setDialogTitle("Guardar Archivo");
        // Establece el nombre de archivo sugerido
        fileChooser.setSelectedFile(new File(archivo.getNombreArchivo()));

        // Muestra el diálogo de guardar
        int resultado = fileChooser.showSaveDialog(parent);
        // Si el usuario aprobó
        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Obtiene el archivo destino
            File archivoDestino = fileChooser.getSelectedFile();

            // Try-with-resources para manejar el FileOutputStream
            try (FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                // Escribe el contenido en el archivo
                fos.write(archivo.getContenido());

                // ========== Panel de éxito ==========
                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Panel con icono y mensaje
                JPanel panelMensaje = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                JLabel lblIconoExito = new JLabel(IconoUtils.iconoParaArchivo(archivo, 32));
                JLabel lblMensaje = new JLabel("Archivo guardado exitosamente");
                lblMensaje.setFont(new Font("Arial", Font.BOLD, 14));
                lblMensaje.setForeground(new Color(40, 167, 69));
                panelMensaje.add(lblIconoExito);
                panelMensaje.add(lblMensaje);

                // Panel con información de ubicación
                JPanel panelInfo = new JPanel(new BorderLayout(5, 5));
                JLabel lblUbicacion = new JLabel("Ubicación:");
                lblUbicacion.setFont(new Font("Arial", Font.PLAIN, 12));
                JTextArea txtRuta = new JTextArea(archivoDestino.getAbsolutePath());
                txtRuta.setEditable(false);
                txtRuta.setLineWrap(true);
                txtRuta.setWrapStyleWord(true);
                txtRuta.setBackground(new Color(245, 245, 245));
                txtRuta.setFont(new Font("Monospaced", Font.PLAIN, 11));
                txtRuta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                panelInfo.add(lblUbicacion, BorderLayout.NORTH);
                panelInfo.add(txtRuta, BorderLayout.CENTER);

                // Organiza los paneles
                panel.add(panelMensaje, BorderLayout.NORTH);
                panel.add(panelInfo, BorderLayout.CENTER);

                // Muestra mensaje de éxito
                JOptionPane.showMessageDialog(parent,
                        panel,
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                // ========== Panel de error ==========
                JPanel panelError = new JPanel(new BorderLayout(10, 10));
                panelError.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                // Panel con icono y mensaje de error
                JPanel panelMensaje = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                JLabel lblIconoError = new JLabel(IconoUtils.iconoEliminar(32));
                JLabel lblMensaje = new JLabel("Error al guardar el archivo");
                lblMensaje.setFont(new Font("Arial", Font.BOLD, 14));
                lblMensaje.setForeground(new Color(220, 53, 69));
                panelMensaje.add(lblIconoError);
                panelMensaje.add(lblMensaje);

                // Área de texto con el error
                JTextArea txtError = new JTextArea(e.getMessage());
                txtError.setEditable(false);
                txtError.setLineWrap(true);
                txtError.setWrapStyleWord(true);
                txtError.setBackground(new Color(255, 240, 240));
                txtError.setFont(new Font("Arial", Font.PLAIN, 11));
                txtError.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 53, 69)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));

                // Organiza los componentes del error
                panelError.add(panelMensaje, BorderLayout.NORTH);
                panelError.add(txtError, BorderLayout.CENTER);

                // Muestra el diálogo de error
                JOptionPane.showMessageDialog(parent,
                        panelError,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}