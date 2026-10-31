package uce.edu.ec.correo.util;

import uce.edu.ec.correo.dominio.ArchivoAdjunto;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Clase utilitaria para crear iconos personalizados usando Graphics2D.
 * Proporciona métodos estáticos para generar iconos vectoriales para la interfaz de usuario.
 */
public class IconoUtils {

    /**
     * Crea el icono principal de la aplicación EcuMail.
     * Dibuja un sobre de correo con detalles decorativos.
     * @return Image con el icono de 128x128 píxeles
     */
    public static Image crearIconoEcuMail() {
        // Crea una imagen BufferedImage con canal alpha (transparencia)
        BufferedImage icon = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        // Obtiene el contexto gráfico para dibujar
        Graphics2D g = icon.createGraphics();

        // Activa el antialiasing para bordes suaves
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Activa renderizado de alta calidad
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // Activa antialiasing para texto
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Establece color azul para el fondo
        g.setColor(new Color(0, 82, 184));
        // Dibuja un rectángulo redondeado como fondo del icono
        g.fillRoundRect(4, 4, 120, 120, 30, 30);

        // Establece color negro semi-transparente para sombra
        g.setColor(new Color(0, 0, 0, 30));
        // Dibuja sombra del sobre
        g.fillRoundRect(26, 42, 80, 56, 8, 8);

        // Establece color blanco para el sobre
        g.setColor(Color.WHITE);
        // Dibuja el rectángulo del sobre
        g.fillRoundRect(24, 40, 80, 56, 8, 8);

        // Establece color lavanda claro para el interior del sobre
        g.setColor(new Color(230, 230, 250));
        // Define las coordenadas X de un triángulo (la solapa del sobre)
        int[] xPoints = {24, 64, 104};
        // Define las coordenadas Y del triángulo
        int[] yPoints = {40, 72, 40};
        // Dibuja el triángulo relleno (solapa interior)
        g.fillPolygon(xPoints, yPoints, 3);

        // Establece color azul para las líneas del sobre
        g.setColor(new Color(0, 82, 184));
        // Crea un trazo grueso con bordes redondeados para las líneas del sobre
        g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Dibuja línea diagonal izquierda del sobre (de arriba-izquierda al centro)
        g.drawLine(24, 40, 64, 72);
        // Dibuja línea diagonal derecha del sobre (de arriba-derecha al centro)
        g.drawLine(104, 40, 64, 72);

        // Cambia a trazo más delgado para el borde del sobre
        g.setStroke(new BasicStroke(3.0f));
        // Dibuja el borde del rectángulo del sobre
        g.drawRoundRect(24, 40, 80, 56, 8, 8);

        // Establece color gris claro para líneas de texto decorativas
        g.setColor(new Color(200, 200, 220));
        // Trazo delgado para las líneas de texto
        g.setStroke(new BasicStroke(2.0f));
        // Dibuja primera línea de texto horizontal (más corta)
        g.drawLine(36, 80, 76, 80);
        // Dibuja segunda línea de texto horizontal (más larga)
        g.drawLine(36, 86, 92, 86);

        // Establece color rojo para la notificación
        g.setColor(new Color(220, 53, 69));
        // Dibuja un círculo rojo pequeño (indicador de notificación)
        g.fillOval(88, 48, 10, 10);

        // Libera los recursos del contexto gráfico
        g.dispose();
        // Retorna la imagen creada
        return icon;
    }

    /**
     * Método helper privado que crea un ImageIcon usando un Dibujador.
     * @param size Tamaño del icono en píxeles (ancho y alto)
     * @param d Interfaz funcional que define cómo dibujar el icono
     * @return ImageIcon con el dibujo resultante
     */
    private static ImageIcon crearIcono(int size, Dibujador d) {
        // Crea una imagen con canal alpha del tamaño especificado
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        // Obtiene el contexto gráfico
        Graphics2D g = img.createGraphics();

        // Activa antialiasing para bordes suaves
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Activa renderizado de alta calidad
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Llama al método dibujar de la interfaz Dibujador
        d.dibujar(g, size);
        // Libera recursos
        g.dispose();

        // Retorna el ImageIcon creado
        return new ImageIcon(img);
    }

    /**
     * Interfaz funcional para definir cómo se dibuja un icono.
     */
    private interface Dibujador {
        /**
         * Método que dibuja en el contexto gráfico.
         * @param g Contexto gráfico Graphics2D
         * @param s Tamaño del icono
         */
        void dibujar(Graphics2D g, int s);
    }

    // ========== ICONOS DE VISIBILIDAD DE CONTRASEÑA (MEJORADOS) ==========

    /**
     * ICONO DE OJO VISIBLE - Ojo abierto para mostrar contraseña.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon del ojo abierto
     */
    public static ImageIcon iconoOjoVisible(int size) {
        return crearIcono(size, (g, s) -> {
            // Convierte Graphics a Graphics2D para tener más control
            Graphics2D g2d = (Graphics2D) g;
            // Activa antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Establece color gris oscuro para el ojo
            g2d.setColor(new Color(60, 60, 60));
            // Crea un trazo con bordes redondeados
            g2d.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula dimensiones de la forma del ojo
            int eyeWidth = (int)(s * 0.75);    // 75% del tamaño total
            int eyeHeight = (int)(s * 0.45);   // 45% del tamaño total
            int eyeX = (int)(s * 0.125);       // Posición X (12.5% del tamaño)
            int eyeY = (int)(s * 0.28);        // Posición Y (28% del tamaño)

            // Dibuja la parte superior del ojo (arco de 0° a 180°)
            g2d.drawArc(eyeX, eyeY, eyeWidth, eyeHeight, 0, 180);

            // Dibuja la parte inferior del ojo (arco de 180° a 360°)
            g2d.drawArc(eyeX, eyeY, eyeWidth, eyeHeight, 180, 180);

            // Calcula el tamaño del iris (35% del tamaño total)
            int irisSize = (int)(s * 0.35);
            // Centra el iris horizontalmente
            int irisX = s/2 - irisSize/2;
            // Centra el iris verticalmente
            int irisY = s/2 - irisSize/2;
            // Dibuja el iris como un círculo relleno
            g2d.fillOval(irisX, irisY, irisSize, irisSize);

            // Cambia a color blanco para el reflejo de luz
            g2d.setColor(Color.WHITE);
            // Tamaño de la pupila/reflejo (12% del tamaño total)
            int pupilaSize = (int)(s * 0.12);
            // Posiciona el reflejo ligeramente arriba y a la derecha del centro
            int pupilaX = (int)(s/2 - pupilaSize/2 + s * 0.04);
            int pupilaY = (int)(s/2 - pupilaSize/2 - s * 0.04);
            // Dibuja el reflejo de luz como círculo blanco
            g2d.fillOval(pupilaX, pupilaY, pupilaSize, pupilaSize);
        });
    }

    /**
     * ICONO DE OJO OCULTO - Ojo tachado para ocultar contraseña.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon del ojo tachado
     */
    public static ImageIcon iconoOjoOculto(int size) {
        return crearIcono(size, (g, s) -> {
            // Convierte a Graphics2D
            Graphics2D g2d = (Graphics2D) g;
            // Activa antialiasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Color gris oscuro
            g2d.setColor(new Color(60, 60, 60));
            // Trazo con bordes redondeados
            g2d.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula dimensiones del ojo (igual que ojo visible)
            int eyeWidth = (int)(s * 0.75);
            int eyeHeight = (int)(s * 0.45);
            int eyeX = (int)(s * 0.125);
            int eyeY = (int)(s * 0.28);

            // Dibuja parte superior del ojo
            g2d.drawArc(eyeX, eyeY, eyeWidth, eyeHeight, 0, 180);

            // Dibuja parte inferior del ojo
            g2d.drawArc(eyeX, eyeY, eyeWidth, eyeHeight, 180, 180);

            // Dibuja el iris
            int irisSize = (int)(s * 0.35);
            int irisX = s/2 - irisSize/2;
            int irisY = s/2 - irisSize/2;
            g2d.fillOval(irisX, irisY, irisSize, irisSize);

            // Dibuja el reflejo blanco
            g2d.setColor(Color.WHITE);
            int pupilaSize = (int)(s * 0.12);
            int pupilaX = (int)(s/2 - pupilaSize/2 + s * 0.04);
            int pupilaY = (int)(s/2 - pupilaSize/2 - s * 0.04);
            g2d.fillOval(pupilaX, pupilaY, pupilaSize, pupilaSize);

            // Dibuja la línea diagonal que tacha el ojo
            g2d.setColor(new Color(60, 60, 60));
            // Trazo más grueso para la línea diagonal
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Margen desde los bordes
            int lineMargin = (int)(s * 0.15);
            // Dibuja línea diagonal de arriba-derecha a abajo-izquierda
            g2d.drawLine(s - lineMargin, lineMargin, lineMargin, s - lineMargin);
        });
    }

    // ========== MÉTODOS PARA ARCHIVOS ADJUNTOS ==========

    /**
     * Obtiene el icono apropiado según el tipo de archivo adjunto.
     * @param archivo Objeto ArchivoAdjunto
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon correspondiente al tipo de archivo
     */
    public static ImageIcon iconoParaArchivo(ArchivoAdjunto archivo, int size) {
        // Si el archivo es null, retorna icono genérico
        if (archivo == null) {
            return iconoArchivoGenerico(size);
        }

        // Switch según el tipo de archivo
        switch (archivo.getTipo()) {
            case IMAGEN:
                // Para imágenes, retorna icono de imagen
                return iconoImagen(size);
            case VIDEO:
                // Para videos, retorna icono de video
                return iconoVideo(size);
            case DOCUMENTO:
                // Para documentos, determina el icono según la extensión
                return iconoParaDocumento(archivo.getNombreArchivo(), size);
            default:
                // Para cualquier otro tipo, retorna icono genérico
                return iconoArchivoGenerico(size);
        }
    }

    /**
     * ICONO DE IMAGEN - Marco con montañas y sol.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando una imagen
     */
    public static ImageIcon iconoImagen(int size) {
        return crearIcono(size, (g, s) -> {
            // Color azul para el marco
            g.setColor(new Color(52, 152, 219));
            // Trazo con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibuja el marco exterior rectangular redondeado
            g.drawRoundRect(3, 3, s - 6, s - 6, 6, 6);

            // Color amarillo para el sol
            g.setColor(new Color(241, 196, 15));
            // Dibuja el sol como círculo relleno en la esquina superior derecha
            g.fillOval((int)(s * 0.6), (int)(s * 0.15), (int)(s * 0.25), (int)(s * 0.25));

            // Color verde para la primera montaña
            g.setColor(new Color(46, 204, 113));
            // Define coordenadas X del triángulo de la primera montaña
            int[] xMountain1 = {(int)(s * 0.1), (int)(s * 0.4), (int)(s * 0.7)};
            // Define coordenadas Y del triángulo de la primera montaña
            int[] yMountain1 = {(int)(s * 0.75), (int)(s * 0.35), (int)(s * 0.75)};
            // Dibuja la primera montaña como triángulo relleno
            g.fillPolygon(xMountain1, yMountain1, 3);

            // Color verde más oscuro para la segunda montaña
            g.setColor(new Color(39, 174, 96));
            // Define coordenadas X del triángulo de la segunda montaña
            int[] xMountain2 = {(int)(s * 0.35), (int)(s * 0.6), (int)(s * 0.85)};
            // Define coordenadas Y del triángulo de la segunda montaña
            int[] yMountain2 = {(int)(s * 0.75), (int)(s * 0.45), (int)(s * 0.75)};
            // Dibuja la segunda montaña superpuesta
            g.fillPolygon(xMountain2, yMountain2, 3);
        });
    }

    /**
     * ICONO DE VIDEO - Cámara de video.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando un video
     */
    public static ImageIcon iconoVideo(int size) {
        return crearIcono(size, (g, s) -> {
            // Color rojo para la cámara
            g.setColor(new Color(231, 76, 60));
            // Trazo con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula dimensiones del cuerpo de la cámara
            int bodyWidth = (int)(s * 0.5);    // 50% del tamaño
            int bodyHeight = (int)(s * 0.4);   // 40% del tamaño
            int bodyX = (int)(s * 0.15);       // Posición X
            int bodyY = (int)(s * 0.3);        // Posición Y
            // Dibuja el cuerpo de la cámara como rectángulo redondeado relleno
            g.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 8, 8);

            // Color rojo más oscuro para el lente
            g.setColor(new Color(192, 57, 43));
            // Define coordenadas X del triángulo del lente
            int[] xLens = {bodyX + bodyWidth, (int)(s * 0.85), bodyX + bodyWidth};
            // Define coordenadas Y del triángulo del lente (apuntando a la derecha)
            int[] yLens = {bodyY, s / 2, bodyY + bodyHeight};
            // Dibuja el lente como triángulo relleno
            g.fillPolygon(xLens, yLens, 3);

            // Color blanco para el botón de grabación
            g.setColor(Color.WHITE);
            // Dibuja botón de grabación como círculo blanco pequeño
            g.fillOval((int)(s * 0.25), (int)(s * 0.42), (int)(s * 0.15), (int)(s * 0.15));
        });
    }

    /**
     * ICONO DE PDF - Documento con "PDF".
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando un archivo PDF
     */
    public static ImageIcon iconoPDF(int size) {
        return crearIcono(size, (g, s) -> {
            // Color rojo para el documento PDF
            g.setColor(new Color(231, 76, 60));

            // Calcula dimensiones del documento
            int docWidth = (int)(s * 0.6);     // 60% del tamaño
            int docHeight = (int)(s * 0.75);   // 75% del tamaño
            int docX = (int)(s * 0.2);         // Posición X (20%)
            int docY = (int)(s * 0.15);        // Posición Y (15%)

            // Dibuja el rectángulo del documento relleno
            g.fillRoundRect(docX, docY, docWidth, docHeight, 6, 6);

            // Color rojo más oscuro para la esquina doblada
            g.setColor(new Color(192, 57, 43));
            // Define coordenadas X del triángulo de la esquina doblada
            int[] xCorner = {docX + docWidth - 12, docX + docWidth, docX + docWidth};
            // Define coordenadas Y del triángulo de la esquina doblada
            int[] yCorner = {docY, docY, docY + 12};
            // Dibuja la esquina doblada como triángulo relleno
            g.fillPolygon(xCorner, yCorner, 3);

            // Color blanco para el texto
            g.setColor(Color.WHITE);
            // Establece fuente grande y negrita para "PDF"
            g.setFont(new Font("Arial", Font.BOLD, (int)(s * 0.2)));
            // Texto a dibujar
            String text = "PDF";
            // Obtiene métricas de la fuente para centrar el texto
            FontMetrics fm = g.getFontMetrics();
            // Calcula ancho del texto
            int textWidth = fm.stringWidth(text);
            // Calcula posición X centrada
            int textX = docX + (docWidth - textWidth) / 2;
            // Calcula posición Y centrada verticalmente
            int textY = docY + docHeight / 2 + fm.getAscent() / 2;
            // Dibuja el texto "PDF"
            g.drawString(text, textX, textY);
        });
    }

    /**
     * ICONO DE WORD - Documento con "W".
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando un archivo Word
     */
    public static ImageIcon iconoWord(int size) {
        return crearIcono(size, (g, s) -> {
            // Color azul para documento Word
            g.setColor(new Color(41, 128, 185));

            // Calcula dimensiones del documento
            int docWidth = (int)(s * 0.6);
            int docHeight = (int)(s * 0.75);
            int docX = (int)(s * 0.2);
            int docY = (int)(s * 0.15);

            // Dibuja el documento
            g.fillRoundRect(docX, docY, docWidth, docHeight, 6, 6);

            // Color azul más oscuro para esquina doblada
            g.setColor(new Color(31, 97, 141));
            // Coordenadas del triángulo
            int[] xCorner = {docX + docWidth - 12, docX + docWidth, docX + docWidth};
            int[] yCorner = {docY, docY, docY + 12};
            // Dibuja esquina doblada
            g.fillPolygon(xCorner, yCorner, 3);

            // Color blanco para texto
            g.setColor(Color.WHITE);
            // Fuente más grande para la "W"
            g.setFont(new Font("Arial", Font.BOLD, (int)(s * 0.35)));
            String text = "W";
            // Obtiene métricas de fuente
            FontMetrics fm = g.getFontMetrics();
            // Calcula ancho del texto
            int textWidth = fm.stringWidth(text);
            // Centra horizontalmente
            int textX = docX + (docWidth - textWidth) / 2;
            // Centra verticalmente
            int textY = docY + docHeight / 2 + fm.getAscent() / 2;
            // Dibuja "W"
            g.drawString(text, textX, textY);
        });
    }

    /**
     * ICONO DE EXCEL - Documento con "X".
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando un archivo Excel
     */
    public static ImageIcon iconoExcel(int size) {
        return crearIcono(size, (g, s) -> {
            // Color verde para Excel
            g.setColor(new Color(39, 174, 96));

            // Calcula dimensiones del documento
            int docWidth = (int)(s * 0.6);
            int docHeight = (int)(s * 0.75);
            int docX = (int)(s * 0.2);
            int docY = (int)(s * 0.15);

            // Dibuja el documento
            g.fillRoundRect(docX, docY, docWidth, docHeight, 6, 6);

            // Color verde más oscuro para esquina
            g.setColor(new Color(30, 132, 73));
            // Coordenadas del triángulo
            int[] xCorner = {docX + docWidth - 12, docX + docWidth, docX + docWidth};
            int[] yCorner = {docY, docY, docY + 12};
            // Dibuja esquina doblada
            g.fillPolygon(xCorner, yCorner, 3);

            // Color blanco para texto
            g.setColor(Color.WHITE);
            // Fuente grande para la "X"
            g.setFont(new Font("Arial", Font.BOLD, (int)(s * 0.35)));
            String text = "X";
            // Obtiene métricas
            FontMetrics fm = g.getFontMetrics();
            // Calcula ancho
            int textWidth = fm.stringWidth(text);
            // Centra horizontalmente
            int textX = docX + (docWidth - textWidth) / 2;
            // Centra verticalmente
            int textY = docY + docHeight / 2 + fm.getAscent() / 2;
            // Dibuja "X"
            g.drawString(text, textX, textY);
        });
    }

    /**
     * ICONO DE TEXTO - Documento con líneas.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando un archivo de texto
     */
    public static ImageIcon iconoTexto(int size) {
        return crearIcono(size, (g, s) -> {
            // Color gris para documento de texto
            g.setColor(new Color(149, 165, 166));

            // Calcula dimensiones del documento
            int docWidth = (int)(s * 0.6);
            int docHeight = (int)(s * 0.75);
            int docX = (int)(s * 0.2);
            int docY = (int)(s * 0.15);

            // Dibuja el documento
            g.fillRoundRect(docX, docY, docWidth, docHeight, 6, 6);

            // Color gris más oscuro para esquina
            g.setColor(new Color(127, 140, 141));
            // Coordenadas del triángulo
            int[] xCorner = {docX + docWidth - 12, docX + docWidth, docX + docWidth};
            int[] yCorner = {docY, docY, docY + 12};
            // Dibuja esquina doblada
            g.fillPolygon(xCorner, yCorner, 3);

            // Color blanco para las líneas de texto
            g.setColor(Color.WHITE);
            // Trazo con bordes redondeados
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula posiciones X de inicio y fin de las líneas
            int lineX1 = docX + 8;              // Margen izquierdo
            int lineX2 = docX + docWidth - 8;   // Margen derecho

            // Dibuja primera línea de texto (35% de altura)
            g.drawLine(lineX1, (int)(docY + docHeight * 0.35), lineX2, (int)(docY + docHeight * 0.35));
            // Dibuja segunda línea de texto (50% de altura)
            g.drawLine(lineX1, (int)(docY + docHeight * 0.5), lineX2, (int)(docY + docHeight * 0.5));
            // Dibuja tercera línea más corta (65% de altura, termina antes)
            g.drawLine(lineX1, (int)(docY + docHeight * 0.65), (int)(lineX2 - docWidth * 0.2), (int)(docY + docHeight * 0.65));
        });
    }

    /**
     * ICONO DE ARCHIVO GENÉRICO - Clip de papel.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon genérico para archivos
     */
    public static ImageIcon iconoArchivoGenerico(int size) {
        // Retorna el icono de clip (adjuntar)
        return iconoAdjuntar(size);
    }

    /**
     * Determina el icono apropiado para un documento basado en su extensión.
     * @param nombreArchivo Nombre del archivo con extensión
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon correspondiente al tipo de documento
     */
    public static ImageIcon iconoParaDocumento(String nombreArchivo, int size) {
        // Convierte el nombre a minúsculas para comparación
        String ext = nombreArchivo.toLowerCase();

        // Verifica si termina en .pdf
        if (ext.endsWith(".pdf")) {
            return iconoPDF(size);
            // Verifica si es documento Word (.doc o .docx)
        } else if (ext.endsWith(".doc") || ext.endsWith(".docx")) {
            return iconoWord(size);
            // Verifica si es documento Excel (.xls o .xlsx)
        } else if (ext.endsWith(".xls") || ext.endsWith(".xlsx")) {
            return iconoExcel(size);
            // Verifica si es archivo de texto (.txt)
        } else if (ext.endsWith(".txt")) {
            return iconoTexto(size);
        }

        // Si no coincide con ninguna extensión conocida, retorna icono genérico
        return iconoArchivoGenerico(size);
    }

    /**
     * ICONO DE MENÚ - Tres líneas horizontales (hamburguesa).
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando un menú
     */
    public static ImageIcon iconoMenu(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro para las líneas
            g.setColor(Color.BLACK);
            // Trazo mediano
            g.setStroke(new BasicStroke(2.5f));
            // Posición Y inicial (primera línea en 1/4 del tamaño)
            int y = s / 4;
            // Dibuja 3 líneas horizontales espaciadas uniformemente
            for (int i = 0; i < 3; i++) {
                // Dibuja línea desde margen izquierdo hasta margen derecho
                g.drawLine(4, y, s - 4, y);
                // Incrementa Y para la siguiente línea (1/4 del tamaño)
                y += s / 4;
            }
        });
    }

    /**
     * ICONO DE USUARIO - Silueta de persona (cabeza y hombros).
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando un usuario
     */
    public static ImageIcon iconoUsuario(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));
            // Dibuja la cabeza como círculo (1/3 del ancho, centrado horizontalmente)
            g.drawOval(s / 3, 4, s / 3, s / 3);
            // Dibuja los hombros como arco (semicírculo superior de 0° a 180°)
            g.drawArc(s / 6, s / 2, s * 2 / 3, s / 2, 0, 180);
        });
    }

    /**
     * ICONO DE CORREO - Sobre de correo con líneas de apertura.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando correo electrónico
     */
    public static ImageIcon iconoCorreo(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja el rectángulo del sobre (redondeado)
            g.drawRoundRect(3, s / 4, s - 6, s / 2, 6, 6);

            // Dibuja línea diagonal izquierda (desde esquina superior izquierda al centro)
            g.drawLine(3, s / 4, s / 2, s / 2);
            // Dibuja línea diagonal derecha (desde esquina superior derecha al centro)
            g.drawLine(s - 3, s / 4, s / 2, s / 2);
        });
    }

    /**
     * ICONO DE REDACTAR - Lápiz o pluma.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando redacción/escritura
     */
    public static ImageIcon iconoRedactar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja línea diagonal del lápiz (de abajo-izquierda a arriba-derecha)
            g.drawLine(s / 4, s * 3 / 4, s * 3 / 4, s / 4);

            // Define coordenadas X del triángulo de la punta del lápiz
            int[] xPunta = {s * 3 / 4 - 2, s * 3 / 4 + 2, s * 3 / 4};
            // Define coordenadas Y del triángulo de la punta
            int[] yPunta = {s / 4 - 2, s / 4 - 2, s / 4 - 6};
            // Dibuja la punta del lápiz como triángulo relleno
            g.fillPolygon(xPunta, yPunta, 3);
        });
    }

    /**
     * ICONO DE LISTA - Viñetas con líneas.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando una lista
     */
    public static ImageIcon iconoLista(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja 3 elementos de lista
            for (int i = 0; i < 3; i++) {
                // Calcula posición Y de cada elemento (espaciados en 1/3 del tamaño)
                int y = 8 + i * (s / 3);
                // Dibuja viñeta como círculo relleno pequeño
                g.fillOval(6, y - 2, 4, 4);
                // Dibuja línea de texto junto a la viñeta
                g.drawLine(s / 4, y, s - 8, y);
            }
        });
    }

    /**
     * ICONO DE ENVIADOS - Flecha apuntando hacia arriba-derecha.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando mensajes enviados
     */
    public static ImageIcon iconoEnviados(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Define coordenadas X de la línea de la flecha
            int[] x = {6, s - 10, s - 10};
            // Define coordenadas Y de la línea de la flecha
            int[] y = {s / 2, s / 4, s * 3 / 4};
            // Dibuja la línea de la flecha (polilínea de 3 puntos)
            g.drawPolyline(x, y, 3);

            // Define coordenadas X del triángulo de la punta
            int[] xPunta = {s - 10, s - 4, s - 10};
            // Define coordenadas Y del triángulo de la punta
            int[] yPunta = {s / 4, s / 2, s * 3 / 4};
            // Dibuja la punta de la flecha como triángulo relleno
            g.fillPolygon(xPunta, yPunta, 3);
        });
    }

    /**
     * ICONO DE RECIBIDOS - Flecha apuntando hacia abajo.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando mensajes recibidos
     */
    public static ImageIcon iconoRecibidos(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja línea vertical de la flecha (de arriba hacia abajo)
            g.drawLine(s / 2, 6, s / 2, s - 10);

            // Define coordenadas X del triángulo de la punta (apuntando hacia abajo)
            int[] xPunta = {s / 4, s / 2, s * 3 / 4};
            // Define coordenadas Y del triángulo de la punta
            int[] yPunta = {s - 10, s - 4, s - 10};
            // Dibuja la punta de la flecha como triángulo relleno
            g.fillPolygon(xPunta, yPunta, 3);
        });
    }

    /**
     * ICONO DE BORRADORES - Documento con líneas de texto.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando borradores
     */
    public static ImageIcon iconoBorradores(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja el contorno del documento como una serie de líneas conectadas
            // Borde superior
            g.drawLine(6, 4, s - 10, 4);
            // Esquina superior derecha doblada - vertical
            g.drawLine(s - 10, 4, s - 10, 10);
            // Esquina superior derecha doblada - horizontal
            g.drawLine(s - 10, 10, s - 4, 10);
            // Borde derecho
            g.drawLine(s - 4, 10, s - 4, s - 4);
            // Borde inferior
            g.drawLine(s - 4, s - 4, 6, s - 4);
            // Borde izquierdo (completa el rectángulo)
            g.drawLine(6, s - 4, 6, 4);

            // Dibuja líneas de texto dentro del documento
            // Primera línea completa
            g.drawLine(10, s / 3, s - 8, s / 3);
            // Segunda línea completa
            g.drawLine(10, s / 2, s - 8, s / 2);
            // Tercera línea incompleta (más corta)
            g.drawLine(10, s * 2 / 3, s / 2, s * 2 / 3);
        });
    }

    /**
     * ICONO DE REENVIAR - Flecha circular (reenvío).
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando reenviar
     */
    public static ImageIcon iconoReenviar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja arco circular (de 45° a 315°, total 270°)
            g.drawArc(6, 6, s - 12, s - 12, 45, 270);

            // Define coordenadas X del triángulo de la punta de flecha
            int[] xPunta = {s - 6, s - 12, s - 12};
            // Define coordenadas Y del triángulo de la punta
            int[] yPunta = {s / 2, s / 2 - 6, s / 2 + 6};
            // Dibuja la punta de flecha como triángulo relleno
            g.fillPolygon(xPunta, yPunta, 3);
        });
    }

    /**
     * ICONO DE ELIMINAR - Papelera de reciclaje.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando eliminar
     */
    public static ImageIcon iconoEliminar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color rojo para indicar eliminación
            g.setColor(new Color(220, 53, 69));
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja la parte superior de la papelera (línea horizontal)
            g.drawLine(s / 4, s / 4, s * 3 / 4, s / 4);
            // Dibuja la tapa de la papelera (arco semicircular de 0° a 180°)
            g.drawArc(s / 3, s / 6, s / 3, s / 6, 0, 180);
            // Dibuja lado izquierdo de la papelera (línea inclinada)
            g.drawLine(s / 3, s / 4, s / 4, s * 3 / 4);
            // Dibuja lado derecho de la papelera (línea inclinada)
            g.drawLine(s * 2 / 3, s / 4, s * 3 / 4, s * 3 / 4);
            // Dibuja fondo de la papelera (línea horizontal inferior)
            g.drawLine(s / 4, s * 3 / 4, s * 3 / 4, s * 3 / 4);
            // Dibuja línea vertical central (detalle de la papelera)
            g.drawLine(s / 2, s / 3, s / 2, s * 2 / 3);
        });
    }

    /**
     * ICONO DE CONTACTOS - Silueta de persona individual.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando contactos
     */
    public static ImageIcon iconoContactos(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja la cabeza como círculo
            g.drawOval(s / 3, 6, s / 3, s / 3);
            // Dibuja los hombros como arco (semicírculo superior)
            g.drawArc(s / 6, s / 2, s * 2 / 3, s / 2, 0, 180);
        });
    }

    /**
     * ICONO DE GRUPOS - Tres siluetas de personas.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando grupos
     */
    public static ImageIcon iconoGrupos(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo más delgado para dibujar múltiples personas
            g.setStroke(new BasicStroke(2));

            // Offset para espaciar las personas
            int offset = s / 5;

            // ========== Primera persona (izquierda) ==========
            // Dibuja cabeza
            g.drawOval(4, s / 3, offset, offset);
            // Dibuja hombros
            g.drawArc(2, s / 2 + offset / 2, offset + 4, offset, 0, 180);

            // ========== Segunda persona (centro, más arriba) ==========
            // Dibuja cabeza centrada
            g.drawOval(s / 2 - offset / 2, s / 4, offset, offset);
            // Dibuja hombros
            g.drawArc(s / 2 - offset / 2 - 2, s / 2 + offset / 4, offset + 4, offset, 0, 180);

            // ========== Tercera persona (derecha) ==========
            // Dibuja cabeza
            g.drawOval(s - offset - 4, s / 3, offset, offset);
            // Dibuja hombros
            g.drawArc(s - offset - 6, s / 2 + offset / 2, offset + 4, offset, 0, 180);
        });
    }

    /**
     * ICONO DE AGREGAR - Signo más (+).
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando agregar
     */
    public static ImageIcon iconoAgregar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color verde para indicar agregar/añadir
            g.setColor(new Color(40, 167, 69));
            // Trazo grueso
            g.setStroke(new BasicStroke(3));

            // Dibuja línea vertical del signo más
            g.drawLine(s / 2, 4, s / 2, s - 4);
            // Dibuja línea horizontal del signo más
            g.drawLine(4, s / 2, s - 4, s / 2);
        });
    }

    /**
     * ICONO DE CHAT - Globo de conversación.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando chat
     */
    public static ImageIcon iconoChat(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f));

            // Dibuja el cuerpo del globo de chat (rectángulo redondeado)
            g.drawRoundRect(4, 4, s - 8, s * 2 / 3, 10, 10);

            // Define coordenadas X de la cola del globo
            int[] xCola = {s / 3, s / 2, s / 4};
            // Define coordenadas Y de la cola del globo
            int[] yCola = {s * 2 / 3 + 4, s - 4, s - 4};
            // Dibuja la cola del globo como polilínea
            g.drawPolyline(xCola, yCola, 3);
        });
    }

    /**
     * ICONO DE BUSCAR - Lupa.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando búsqueda
     */
    public static ImageIcon iconoBuscar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color gris oscuro
            g.setColor(new Color(60, 60, 60));
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula el diámetro del círculo de la lupa (55% del tamaño)
            int diameter = (int)(s * 0.55);
            // Calcula offset desde el borde
            int offset = (int)(s * 0.08);
            // Dibuja el círculo de la lupa
            g.drawOval(offset, offset, diameter, diameter);

            // Calcula punto de inicio del mango (esquina inferior derecha del círculo)
            int mangoStartX = (int)(offset + diameter * 0.75);
            int mangoStartY = (int)(offset + diameter * 0.75);
            // Calcula punto final del mango (esquina inferior derecha del icono)
            int mangoEndX = (int)(s * 0.88);
            int mangoEndY = (int)(s * 0.88);

            // Trazo más grueso para el mango
            g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Dibuja el mango de la lupa
            g.drawLine(mangoStartX, mangoStartY, mangoEndX, mangoEndY);
        });
    }

    /**
     * ICONO DE CLIP ADJUNTAR - Diseño simple y reconocible.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando adjuntar archivo
     */
    public static ImageIcon iconoAdjuntar(int size) {
        // Crea imagen con canal alpha para transparencia
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // ===== CONFIGURACIÓN DE CALIDAD DE RENDERIZADO =====
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // ===== CONFIGURACIÓN DE COLOR Y TRAZO =====
        g2d.setColor(Color.BLACK);
        float strokeWidth = size * 0.12f;
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // ===== ROTACIÓN PARA ASPECTO INCLINADO =====
        g2d.rotate(Math.toRadians(-45), size / 2.0, size / 2.0);

        // ===== DIMENSIONES DEL CLIP =====
        float clipWidth = size * 0.65f;   // Ancho del clip
        float clipHeight = size * 0.40f;  // Alto del clip (más ancho que alto)
        float x = (size - clipWidth) / 2;
        float y = (size - clipHeight) / 2;

        // Radio para las esquinas redondeadas (forma ovalada)
        float cornerRadius = clipHeight / 2;

        // ===== DIBUJAR ÓVALO EXTERIOR =====
        RoundRectangle2D outerClip = new RoundRectangle2D.Float(
                x, y,
                clipWidth, clipHeight,
                clipHeight, clipHeight  // Radio igual al alto para forma ovalada
        );
        g2d.draw(outerClip);

        // ===== DIBUJAR ÓVALO INTERIOR =====
        // Margen entre el óvalo exterior e interior
        float innerMargin = strokeWidth * 1.5f;

        RoundRectangle2D innerClip = new RoundRectangle2D.Float(
                x + innerMargin,
                y + innerMargin,
                clipWidth - innerMargin * 2,
                clipHeight - innerMargin * 2,
                clipHeight - innerMargin * 2,
                clipHeight - innerMargin * 2
        );
        g2d.draw(innerClip);

        // Liberar recursos
        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * ICONO DE ENVIAR - Flecha de envío (avión de papel).
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando enviar
     */
    public static ImageIcon iconoEnviar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Define coordenadas X del triángulo (avión de papel)
            int[] xPoints = {(int)(s*0.15), (int)(s*0.85), (int)(s*0.15)};
            // Define coordenadas Y del triángulo
            int[] yPoints = {(int)(s*0.15), s/2, (int)(s*0.85)};

            // Dibuja el triángulo relleno (cuerpo del avión)
            g.fillPolygon(xPoints, yPoints, 3);

            // Cambia a color blanco para la línea central
            g.setColor(Color.WHITE);
            // Trazo más delgado para la línea central
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Dibuja línea horizontal central (detalle del avión)
            g.drawLine((int)(s*0.15), s/2, (int)(s*0.6), s/2);
        });
    }

    /**
     * ICONO DE ELIMINAR X - X grande para cerrar/eliminar.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando cerrar/eliminar
     */
    public static ImageIcon iconoEliminarX(int size) {
        return crearIcono(size, (g, s) -> {
            // Color rojo para indicar eliminación
            g.setColor(new Color(220, 53, 69));
            // Trazo grueso con bordes redondeados
            g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula margen desde los bordes (25% del tamaño)
            int margen = (int)(s * 0.25);
            // Dibuja diagonal de arriba-izquierda a abajo-derecha
            g.drawLine(margen, margen, s - margen, s - margen);
            // Dibuja diagonal de arriba-derecha a abajo-izquierda
            g.drawLine(s - margen, margen, margen, s - margen);
        });
    }

    /**
     * ICONO DE ESTRELLA - Estrella de 5 puntas.
     * @param size Tamaño del icono en píxeles
     * @param llena true para estrella rellena, false para contorno
     * @return ImageIcon de estrella
     */
    public static ImageIcon iconoEstrella(int size, boolean llena) {
        return crearIcono(size, (g, s) -> {
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Centro de la estrella
            int cx = s / 2;
            int cy = s / 2;
            // Radio exterior (puntas de la estrella)
            int outerRadius = s / 2 - 3;
            // Radio interior (valles entre las puntas)
            int innerRadius = s / 5;

            // Arrays para coordenadas de los 10 puntos (5 puntas + 5 valles)
            int[] xPoints = new int[10];
            int[] yPoints = new int[10];

            // Genera los 10 puntos de la estrella
            for (int i = 0; i < 10; i++) {
                // Calcula ángulo del punto (empieza en PI/2 = 90°, hacia arriba)
                double angle = Math.PI / 2 + (2 * Math.PI * i / 10);
                // Alterna entre radio exterior (puntas) e interior (valles)
                int radius = (i % 2 == 0) ? outerRadius : innerRadius;
                // Calcula coordenada X del punto
                xPoints[i] = (int) (cx + radius * Math.cos(angle));
                // Calcula coordenada Y del punto (resta porque Y crece hacia abajo)
                yPoints[i] = (int) (cy - radius * Math.sin(angle));
            }

            // Si la estrella debe estar llena
            if (llena) {
                // Color dorado para estrella rellena
                g.setColor(new Color(255, 215, 0));
                // Dibuja polígono relleno
                g.fillPolygon(xPoints, yPoints, 10);
                // Color dorado más oscuro para el borde
                g.setColor(new Color(218, 165, 32));
                // Dibuja el contorno
                g.drawPolygon(xPoints, yPoints, 10);
            } else {
                // Color gris para estrella vacía
                g.setColor(new Color(200, 200, 200));
                // Dibuja solo el contorno
                g.drawPolygon(xPoints, yPoints, 10);
            }
        });
    }

    /**
     * ICONO DE DESTACADOS - Estrella rellena.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon de estrella dorada
     */
    public static ImageIcon iconoDestacados(int size) {
        // Retorna estrella llena
        return iconoEstrella(size, true);
    }

    /**
     * ICONO DE INFORMACIÓN PERSONAL - Tarjeta con datos de usuario.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando información personal
     */
    public static ImageIcon iconoInformacionPersonal(int size) {
        return crearIcono(size, (g, s) -> {
            // Activa antialiasing
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Color azul para la tarjeta
            g.setColor(new Color(0, 102, 204));
            // Dibuja rectángulo relleno de la tarjeta
            g.fillRoundRect(2, (int)(s * 0.25), s - 4, (int)(s * 0.6), 8, 8);

            // Color azul más oscuro para el borde
            g.setColor(new Color(0, 82, 164));
            // Trazo mediano
            g.setStroke(new BasicStroke(2.0f));
            // Dibuja el borde de la tarjeta
            g.drawRoundRect(2, (int)(s * 0.25), s - 4, (int)(s * 0.6), 8, 8);

            // Color blanco para la silueta de persona
            g.setColor(Color.WHITE);
            // Calcula tamaño de la cabeza
            int headSize = (int)(s * 0.25);
            int headX = (int)(s * 0.15);
            int headY = (int)(s * 0.32);
            // Dibuja la cabeza como círculo relleno
            g.fillOval(headX, headY, headSize, headSize);

            // Calcula dimensiones del cuerpo
            int bodyWidth = (int)(s * 0.32);
            int bodyHeight = (int)(s * 0.2);
            int bodyX = (int)(s * 0.09);
            int bodyY = (int)(s * 0.50);
            // Dibuja el cuerpo como arco (semicírculo superior)
            g.fillArc(bodyX, bodyY, bodyWidth, bodyHeight, 0, 180);

            // Color azul claro para las líneas de texto
            g.setColor(new Color(200, 220, 255));
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Posición X de inicio de las líneas
            int lineStartX = (int)(s * 0.50);
            // Posición X de fin de las líneas
            int lineEndX = s - 8;

            // Dibuja 4 líneas horizontales representando texto
            g.drawLine(lineStartX, (int)(s * 0.38), lineEndX, (int)(s * 0.38));
            g.drawLine(lineStartX, (int)(s * 0.50), lineEndX, (int)(s * 0.50));
            g.drawLine(lineStartX, (int)(s * 0.62), lineEndX, (int)(s * 0.62));
            // Última línea más corta
            g.drawLine(lineStartX, (int)(s * 0.74), (int)(s * 0.75), (int)(s * 0.74));
        });
    }

    /**
     * ICONO DE CAMBIAR PASSWORD - Candado.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando cambio de contraseña
     */
    public static ImageIcon iconoCambiarPassword(int size) {
        return crearIcono(size, (g, s) -> {
            // Activa antialiasing
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula dimensiones del cuerpo del candado
            int lockWidth = (int)(s * 0.5);
            int lockHeight = (int)(s * 0.35);
            int lockX = (int)(s * 0.25);
            int lockY = (int)(s * 0.5);
            // Dibuja el cuerpo del candado relleno
            g.fillRoundRect(lockX, lockY, lockWidth, lockHeight, 6, 6);

            // Calcula dimensiones del arco del candado
            int arcWidth = (int)(s * 0.35);
            int arcHeight = (int)(s * 0.4);
            int arcX = (int)(s * 0.325);
            int arcY = (int)(s * 0.15);
            // Trazo más grueso para el arco
            g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Dibuja el arco superior (semicírculo de 0° a 180°)
            g.drawArc(arcX, arcY, arcWidth, arcHeight, 0, 180);

            // Color blanco para el agujero de llave
            g.setColor(Color.WHITE);
            // Tamaño del círculo del agujero
            int keyHoleSize = (int)(s * 0.1);
            // Dibuja círculo del agujero de llave
            g.fillOval(s/2 - keyHoleSize/2, (int)(s * 0.58), keyHoleSize, keyHoleSize);

            // Dibuja rectángulo vertical del agujero de llave
            g.fillRect(s/2 - 2, (int)(s * 0.65), 4, (int)(s * 0.12));
        });
    }

    /**
     * ICONO DE ELIMINAR CUENTA - Usuario con X.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando eliminación de cuenta
     */
    public static ImageIcon iconoEliminarCuenta(int size) {
        return crearIcono(size, (g, s) -> {
            // Activa antialiasing
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Calcula dimensiones de la cabeza
            int headSize = (int)(s * 0.3);
            int headX = (int)(s * 0.35);
            int headY = (int)(s * 0.12);
            // Dibuja la cabeza como círculo
            g.drawOval(headX, headY, headSize, headSize);

            // Calcula dimensiones del cuerpo
            int bodyWidth = (int)(s * 0.5);
            int bodyHeight = (int)(s * 0.35);
            int bodyX = (int)(s * 0.25);
            int bodyY = (int)(s * 0.35);
            // Dibuja el cuerpo como arco
            g.drawArc(bodyX, bodyY, bodyWidth, bodyHeight, 0, 180);

            // Trazo más grueso para la X
            g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Tamaño de la X
            int xSize = (int)(s * 0.3);
            // Posición de la X
            int xOffset = (int)(s * 0.6);
            int yOffset = (int)(s * 0.6);

            // Dibuja diagonal de la X (arriba-izquierda a abajo-derecha)
            g.drawLine(xOffset, yOffset, xOffset + xSize, yOffset + xSize);
            // Dibuja diagonal de la X (arriba-derecha a abajo-izquierda)
            g.drawLine(xOffset + xSize, yOffset, xOffset, yOffset + xSize);
        });
    }

    /**
     * ICONO DE REGRESAR - Flecha curva hacia atrás.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando regresar/volver
     */
    public static ImageIcon iconoRegresar(int size) {
        return crearIcono(size, (g, s) -> {
            // Activa antialiasing
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo grueso con bordes redondeados
            g.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Posición Y de la flecha (centro vertical)
            int arrowY = s / 2;
            // Posición X de la punta de la flecha
            int arrowTipX = (int)(s * 0.15);
            // Posición X de la base de la flecha
            int arrowBaseX = (int)(s * 0.4);

            // Dibuja línea horizontal principal de la flecha
            g.drawLine(arrowTipX, arrowY, (int)(s * 0.85), arrowY);

            // Dibuja línea superior de la punta (hacia arriba)
            g.drawLine(arrowTipX, arrowY, arrowBaseX, (int)(s * 0.3));

            // Dibuja línea inferior de la punta (hacia abajo)
            g.drawLine(arrowTipX, arrowY, arrowBaseX, (int)(s * 0.7));

            // Dibuja arco curvo decorativo (cuarto de círculo)
            g.drawArc((int)(s * 0.3), (int)(s * 0.35), (int)(s * 0.55), (int)(s * 0.5), 270, 90);
        });
    }

    /**
     * ICONO DE ARCHIVOS CHAT - Carpeta con documento.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando archivos compartidos en chat
     */
    public static ImageIcon iconoArchivosChat(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibuja el contorno de la carpeta como polilínea
            // Borde izquierdo inferior
            g.drawLine(4, s/3, s/3, s/3);
            // Pestaña de la carpeta - diagonal
            g.drawLine(s/3, s/3, s/3 + 6, s/4);
            // Borde superior
            g.drawLine(s/3 + 6, s/4, s - 4, s/4);
            // Borde derecho
            g.drawLine(s - 4, s/4, s - 4, s - 4);
            // Borde inferior
            g.drawLine(s - 4, s - 4, 4, s - 4);
            // Borde izquierdo (completa la carpeta)
            g.drawLine(4, s - 4, 4, s/3);

            // Trazo más delgado para las líneas de documento
            g.setStroke(new BasicStroke(1.5f));
            // Dibuja primera línea de documento
            g.drawLine(s/4, s/2, s - s/4, s/2);
            // Dibuja segunda línea de documento
            g.drawLine(s/4, s*2/3, s - s/4, s*2/3);
        });
    }

    /**
     * ICONO DE ACTUALIZAR - Flecha circular de actualización.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando actualizar/refrescar
     */
    public static ImageIcon iconoActualizar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibuja arco circular (de 45° a 315°, total 270°)
            g.drawArc(s/6, s/6, s*2/3, s*2/3, 45, 270);

            // Define coordenadas X del triángulo de la punta
            int[] xPunta = {s/2 + s/4, s/2 + s/4 - 6, s/2 + s/4 + 6};
            // Define coordenadas Y del triángulo de la punta
            int[] yPunta = {s/6 + 3, s/6 - 3, s/6 - 3};
            // Dibuja la punta de flecha como triángulo relleno
            g.fillPolygon(xPunta, yPunta, 3);
        });
    }

    /**
     * ICONO DE EDITAR - Lápiz pequeño.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando editar
     */
    public static ImageIcon iconoEditar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibuja línea diagonal del lápiz
            g.drawLine(s/4, s*3/4, s*3/4, s/4);

            // Define coordenadas X de la punta del lápiz
            int[] xPunta = {s*3/4 - 2, s*3/4 + 2, s*3/4};
            // Define coordenadas Y de la punta del lápiz
            int[] yPunta = {s/4 - 2, s/4 - 2, s/4 - 6};
            // Dibuja la punta como triángulo relleno
            g.fillPolygon(xPunta, yPunta, 3);

            // Dibuja pequeña línea base del lápiz (donde toca el papel)
            g.drawLine(s/6, s*5/6, s/4, s*3/4);
        });
    }

    /**
     * ICONO DE ADMIN - Gráfico/estadísticas.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando administración
     */
    public static ImageIcon iconoAdmin(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Define coordenadas X de la polilínea (gráfico de líneas)
            int[] xPoints = {s/6, s/4, s/2, s*3/4, s*5/6, s*5/6, s/6};
            // Define coordenadas Y de la polilínea (forma de gráfico)
            int[] yPoints = {s*2/3, s/3, s/2, s/3, s*2/3, s*3/4, s*3/4};
            // Dibuja el gráfico como polilínea
            g.drawPolyline(xPoints, yPoints, 7);

            // Dibuja puntos de datos como círculos rellenos
            g.fillOval(s/4 - 2, s/3 - 2, 4, 4);      // Punto 1
            g.fillOval(s/2 - 2, s/2 - 2, 4, 4);      // Punto 2
            g.fillOval(s*3/4 - 2, s/3 - 2, 4, 4);    // Punto 3
        });
    }

    /**
     * ICONO DE SALIR - Puerta con flecha.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando cerrar sesión/salir
     */
    public static ImageIcon iconoSalir(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo mediano con bordes redondeados
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibuja la puerta como rectángulo
            g.drawRect(4, s/6, s/2, s*2/3);
            // Dibuja la perilla de la puerta como círculo relleno
            g.fillOval(s/3, s/2 - 2, 4, 4);

            // Dibuja flecha horizontal hacia la derecha (saliendo)
            g.drawLine(s/2 + 4, s/2, s - 6, s/2);
            // Define coordenadas X de la punta de flecha
            int[] xPunta = {s - 6, s - 12, s - 12};
            // Define coordenadas Y de la punta de flecha
            int[] yPunta = {s/2, s/2 - 6, s/2 + 6};
            // Dibuja la punta de flecha como triángulo relleno
            g.fillPolygon(xPunta, yPunta, 3);
        });
    }

    /**
     * ICONO DE CERRAR - Círculo con X.
     * @param size Tamaño del icono en píxeles
     * @return ImageIcon representando cerrar
     */
    public static ImageIcon iconoCerrar(int size) {
        return crearIcono(size, (g, s) -> {
            // Color negro
            g.setColor(Color.BLACK);
            // Trazo más delgado para el círculo
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Dibuja el círculo exterior
            g.drawOval(4, 4, s - 8, s - 8);

            // Trazo más grueso para la X
            g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Calcula margen para la X
            int margen = s/4;
            // Dibuja diagonal de la X (arriba-izquierda a abajo-derecha)
            g.drawLine(margen, margen, s - margen, s - margen);
            // Dibuja diagonal de la X (arriba-derecha a abajo-izquierda)
            g.drawLine(s - margen, margen, margen, s - margen);
        });
    }
}