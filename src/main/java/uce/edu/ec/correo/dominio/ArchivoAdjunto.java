package uce.edu.ec.correo.dominio;

import java.io.Serializable;

/**
 * Clase que representa un archivo adjunto en un correo electrónico o mensaje de grupo.
 * Implementa Serializable para permitir la persistencia de objetos.
 */
public class ArchivoAdjunto implements Serializable {
    // Versión de serialización para mantener compatibilidad
    private static final long serialVersionUID = 1L;

    // Nombre del archivo adjunto (ej: "documento.pdf")
    private String nombreArchivo;

    // Contenido del archivo en bytes (datos binarios)
    private byte[] contenido;

    // Tamaño del archivo en bytes
    private long tamano;

    // Tipo de archivo clasificado (IMAGEN, VIDEO, DOCUMENTO, OTRO)
    private TipoArchivo tipo;

    /**
     * Enumeración que define los tipos de archivos soportados
     */
    public enum TipoArchivo {
        IMAGEN,     // Archivos de imagen (.jpg, .png, etc.)
        VIDEO,      // Archivos de video (.mp4, .avi, etc.)
        DOCUMENTO,  // Archivos de documentos (.pdf, .doc, etc.)
        OTRO        // Cualquier otro tipo de archivo
    }

    /**
     * Constructor que inicializa un archivo adjunto.
     *
     * @param nombreArchivo Nombre del archivo con su extensión
     * @param contenido Array de bytes con el contenido del archivo
     * @param tamano Tamaño del archivo en bytes
     */
    public ArchivoAdjunto(String nombreArchivo, byte[] contenido, long tamano) {
        // Asigna el nombre del archivo al atributo
        this.nombreArchivo = nombreArchivo;

        // Asigna el contenido binario al atributo
        this.contenido = contenido;

        // Asigna el tamaño al atributo
        this.tamano = tamano;

        // Determina automáticamente el tipo de archivo según su extensión
        this.tipo = determinarTipo(nombreArchivo);
    }

    /**
     * Método privado que determina el tipo de archivo basándose en su extensión.
     *
     * @param nombre Nombre del archivo a analizar
     * @return El tipo de archivo correspondiente (IMAGEN, VIDEO, DOCUMENTO, OTRO)
     */
    private TipoArchivo determinarTipo(String nombre) {
        // Convierte el nombre a minúsculas para comparación insensible a mayúsculas
        String ext = nombre.toLowerCase();

        // Verifica si es una imagen comparando extensiones comunes
        if (ext.endsWith(".jpg") || ext.endsWith(".jpeg") || ext.endsWith(".png") ||
                ext.endsWith(".gif") || ext.endsWith(".bmp")) {
            return TipoArchivo.IMAGEN;
        }
        // Verifica si es un video comparando extensiones comunes
        else if (ext.endsWith(".mp4") || ext.endsWith(".avi") || ext.endsWith(".mov") ||
                ext.endsWith(".wmv") || ext.endsWith(".flv")) {
            return TipoArchivo.VIDEO;
        }
        // Verifica si es un documento comparando extensiones comunes
        else if (ext.endsWith(".pdf") || ext.endsWith(".doc") || ext.endsWith(".docx") ||
                ext.endsWith(".txt") || ext.endsWith(".xls") || ext.endsWith(".xlsx")) {
            return TipoArchivo.DOCUMENTO;
        }
        // Si no coincide con ninguno, lo clasifica como OTRO
        else {
            return TipoArchivo.OTRO;
        }
    }

    /**
     * Obtiene el nombre del archivo.
     *
     * @return Nombre del archivo con su extensión
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Obtiene el contenido binario del archivo.
     *
     * @return Array de bytes con el contenido del archivo
     */
    public byte[] getContenido() {
        return contenido;
    }

    /**
     * Obtiene el tipo de archivo clasificado.
     *
     * @return Tipo de archivo (IMAGEN, VIDEO, DOCUMENTO, OTRO)
     */
    public TipoArchivo getTipo() {
        return tipo;
    }

    /**
     * Formatea el tamaño del archivo a una representación legible para humanos.
     * Convierte bytes a KB, MB, GB, etc. según sea necesario.
     *
     * @return String con el tamaño formateado (ej: "1.5 MB", "234 KB")
     */
    public String getTamanoFormateado() {
        // Si el tamaño es menor a 1024 bytes, muestra en bytes
        if (tamano < 1024) return tamano + " B";

        // Calcula el exponente para determinar la unidad (KB=1, MB=2, GB=3, etc.)
        int exp = (int) (Math.log(tamano) / Math.log(1024));

        // Obtiene el prefijo correspondiente (K, M, G, T, P, E)
        String pre = "KMGTPE".charAt(exp - 1) + "";

        // Formatea el número dividiendo por la potencia correspondiente de 1024
        // y muestra con un decimal de precisión más el prefijo y "B"
        return String.format("%.1f %sB", tamano / Math.pow(1024, exp), pre);
    }
}