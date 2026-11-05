package uce.edu.ec.correo.util;

import uce.edu.ec.correo.dominio.HojaReporte;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PdfWriter {

    private static final float ANCHO_PAGINA = 842f;
    private static final float ALTO_PAGINA = 595f;
    private static final float MARGEN = 30f;
    private static final float TAMANO = 8f;
    private static final float INTERLINEA = 10.5f;
    private static final float ANCHO_CARACTER = 4.6f;
    private static final int CARACTERES_MINIMOS = 6;
    private static final int CARACTERES_MAXIMOS = 50;
    private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");

    private final List<StringBuilder> paginas = new ArrayList<>();
    private StringBuilder paginaActual;
    private float y;

    private PdfWriter() {
    }

    public static void escribir(List<HojaReporte> hojas, Path destino) throws IOException {
        if (hojas == null || hojas.isEmpty()) {
            throw new IllegalArgumentException("No hay hojas para exportar");
        }
        PdfWriter escritor = new PdfWriter();
        for (HojaReporte hoja : hojas) {
            escritor.dibujarHoja(hoja);
        }
        Files.write(destino, escritor.ensamblar());
    }

    private void nuevaPagina() {
        paginaActual = new StringBuilder();
        paginas.add(paginaActual);
        y = ALTO_PAGINA - MARGEN;
    }

    private void dibujarHoja(HojaReporte hoja) {
        nuevaPagina();
        texto(MARGEN, y - 12, "F2", 12f, hoja.titulo());
        y -= 20;
        texto(MARGEN, y - 9, "F1", 9f, "Sección: " + hoja.nombre());
        y -= 22;

        int[] caracteres = calcularColumnas(hoja);
        float[] posiciones = new float[caracteres.length + 1];
        for (int c = 0; c < caracteres.length; c++) {
            posiciones[c + 1] = posiciones[c] + caracteres[c] * ANCHO_CARACTER;
        }

        dibujarFila(hoja.columnas(), caracteres, posiciones, true);
        if (hoja.filas().isEmpty()) {
            texto(MARGEN, y - 10, "F1", TAMANO, "Sin registros en este periodo.");
            y -= INTERLINEA;
            return;
        }
        for (List<String> fila : hoja.filas()) {
            float altura = alturaFila(fila, caracteres);
            if (y - altura < MARGEN + 15) {
                nuevaPagina();
                dibujarFila(hoja.columnas(), caracteres, posiciones, true);
            }
            dibujarFila(fila, caracteres, posiciones, false);
        }
    }

    private int[] calcularColumnas(HojaReporte hoja) {
        int columnas = hoja.columnas().size();
        int[] deseado = new int[columnas];
        for (int c = 0; c < columnas; c++) {
            int maximo = hoja.columnas().get(c).length();
            for (List<String> fila : hoja.filas()) {
                if (c < fila.size()) {
                    maximo = Math.max(maximo, fila.get(c).length());
                }
            }
            deseado[c] = Math.max(CARACTERES_MINIMOS, Math.min(CARACTERES_MAXIMOS, maximo + 1));
        }
        int capacidad = (int) ((ANCHO_PAGINA - 2 * MARGEN) / ANCHO_CARACTER);
        int total = 0;
        for (int valor : deseado) {
            total += valor;
        }
        int[] resultado = new int[columnas];
        for (int c = 0; c < columnas; c++) {
            resultado[c] = Math.max(CARACTERES_MINIMOS, deseado[c] * capacidad / total);
        }
        return resultado;
    }

    private float alturaFila(List<String> fila, int[] caracteres) {
        int lineas = 1;
        for (int c = 0; c < caracteres.length; c++) {
            String valor = c < fila.size() ? fila.get(c) : "";
            lineas = Math.max(lineas, envolver(valor, caracteres[c] - 1).size());
        }
        return lineas * INTERLINEA + 5;
    }

    private void dibujarFila(List<String> fila, int[] caracteres, float[] posiciones, boolean encabezado) {
        float altura = alturaFila(fila, caracteres);
        float base = y - altura;
        if (encabezado) {
            paginaActual.append("0.86 0.91 0.97 rg ")
                    .append(num(MARGEN)).append(' ').append(num(base)).append(' ')
                    .append(num(posiciones[caracteres.length])).append(' ').append(num(altura))
                    .append(" re f 0 g\n");
        }
        for (int c = 0; c < caracteres.length; c++) {
            String valor = c < fila.size() ? fila.get(c) : "";
            float yTexto = y - 3 - TAMANO;
            for (String linea : envolver(valor, caracteres[c] - 1)) {
                texto(MARGEN + posiciones[c] + 2, yTexto, encabezado ? "F2" : "F1", TAMANO, linea);
                yTexto -= INTERLINEA;
            }
        }
        paginaActual.append("0.8 G 0.5 w ")
                .append(num(MARGEN)).append(' ').append(num(base)).append(" m ")
                .append(num(MARGEN + posiciones[caracteres.length])).append(' ').append(num(base))
                .append(" l S 0 G\n");
        y = base;
    }

    private static List<String> envolver(String texto, int ancho) {
        List<String> lineas = new ArrayList<>();
        String limpio = texto == null ? "" : texto.replaceAll("[\\r\\n\\t]+", " ").trim();
        StringBuilder actual = new StringBuilder();
        for (String palabra : limpio.split(" ")) {
            String restante = palabra;
            while (restante.length() > ancho) {
                if (actual.length() > 0) {
                    lineas.add(actual.toString());
                    actual.setLength(0);
                }
                lineas.add(restante.substring(0, ancho));
                restante = restante.substring(ancho);
            }
            if (actual.length() == 0) {
                actual.append(restante);
            } else if (actual.length() + 1 + restante.length() <= ancho) {
                actual.append(' ').append(restante);
            } else {
                lineas.add(actual.toString());
                actual.setLength(0);
                actual.append(restante);
            }
        }
        lineas.add(actual.toString());
        return lineas;
    }

    private void texto(float x, float posicionY, String fuente, float tamano, String contenido) {
        paginaActual.append("BT /").append(fuente).append(' ').append(num(tamano)).append(" Tf ")
                .append(num(x)).append(' ').append(num(posicionY)).append(" Td (")
                .append(escapar(contenido)).append(") Tj ET\n");
    }

    private static String escapar(String contenido) {
        String bytes = new String(contenido.getBytes(WINDOWS_1252), StandardCharsets.ISO_8859_1);
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < bytes.length(); i++) {
            char c = bytes.charAt(i);
            if (c == '\\' || c == '(' || c == ')') {
                resultado.append('\\').append(c);
            } else if (c >= 0x20) {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    private static String num(float valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }

    private byte[] ensamblar() throws IOException {
        int total = paginas.size();
        for (int i = 0; i < total; i++) {
            StringBuilder pagina = paginas.get(i);
            String pie = "Página " + (i + 1) + " de " + total;
            pagina.append("BT /F1 8 Tf ").append(num(ANCHO_PAGINA / 2 - 25)).append(' ').append(num(15))
                    .append(" Td (").append(escapar(pie)).append(") Tj ET\n");
        }

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        List<Integer> posiciones = new ArrayList<>();
        escribir(salida, "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");

        objeto(salida, posiciones, "<< /Type /Catalog /Pages 2 0 R >>");
        StringBuilder hijos = new StringBuilder();
        for (int i = 0; i < total; i++) {
            hijos.append(5 + 2 * i).append(" 0 R ");
        }
        objeto(salida, posiciones, "<< /Type /Pages /Kids [" + hijos + "] /Count " + total + " >>");
        objeto(salida, posiciones,
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");
        objeto(salida, posiciones,
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");
        for (int i = 0; i < total; i++) {
            objeto(salida, posiciones, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) ANCHO_PAGINA + " "
                    + (int) ALTO_PAGINA + "] /Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents "
                    + (6 + 2 * i) + " 0 R >>");
            String contenido = paginas.get(i).toString();
            objeto(salida, posiciones, "<< /Length " + contenido.length() + " >>\nstream\n" + contenido
                    + "endstream");
        }

        int inicioXref = salida.size();
        StringBuilder xref = new StringBuilder("xref\n0 " + (posiciones.size() + 1) + "\n0000000000 65535 f \n");
        for (int posicion : posiciones) {
            xref.append(String.format(Locale.ROOT, "%010d 00000 n \n", posicion));
        }
        xref.append("trailer\n<< /Size ").append(posiciones.size() + 1).append(" /Root 1 0 R >>\nstartxref\n")
                .append(inicioXref).append("\n%%EOF\n");
        escribir(salida, xref.toString());
        return salida.toByteArray();
    }

    private static void objeto(ByteArrayOutputStream salida, List<Integer> posiciones, String cuerpo)
            throws IOException {
        posiciones.add(salida.size());
        escribir(salida, (posiciones.size()) + " 0 obj\n" + cuerpo + "\nendobj\n");
    }

    private static void escribir(ByteArrayOutputStream salida, String texto) throws IOException {
        salida.write(texto.getBytes(StandardCharsets.ISO_8859_1));
    }
}
