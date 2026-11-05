package uce.edu.ec.correo.util;

import uce.edu.ec.correo.dominio.HojaReporte;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ExcelWriter {

    private static final Pattern ENTERO = Pattern.compile("^(0|[1-9][0-9]{0,8})$");
    private static final int ANCHO_MAXIMO = 60;
    private static final int ANCHO_MINIMO = 10;

    private ExcelWriter() {
    }

    public static void escribir(List<HojaReporte> hojas, Path destino) throws IOException {
        if (hojas == null || hojas.isEmpty()) {
            throw new IllegalArgumentException("No hay hojas para exportar");
        }
        try (OutputStream salida = Files.newOutputStream(destino);
             ZipOutputStream zip = new ZipOutputStream(salida, StandardCharsets.UTF_8)) {
            agregar(zip, "[Content_Types].xml", tiposDeContenido(hojas.size()));
            agregar(zip, "_rels/.rels", relacionesRaiz());
            agregar(zip, "xl/workbook.xml", libro(hojas));
            agregar(zip, "xl/_rels/workbook.xml.rels", relacionesLibro(hojas.size()));
            agregar(zip, "xl/styles.xml", estilos());
            for (int i = 0; i < hojas.size(); i++) {
                agregar(zip, "xl/worksheets/sheet" + (i + 1) + ".xml", hoja(hojas.get(i)));
            }
        }
    }

    private static void agregar(ZipOutputStream zip, String nombre, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String tiposDeContenido(int cantidadHojas) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>");
        for (int i = 1; i <= cantidadHojas; i++) {
            xml.append("<Override PartName=\"/xl/worksheets/sheet").append(i)
                    .append(".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");
        }
        return xml.append("</Types>").toString();
    }

    private static String relacionesRaiz() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
    }

    private static String libro(List<HojaReporte> hojas) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets>");
        Set<String> usados = new HashSet<>();
        for (int i = 0; i < hojas.size(); i++) {
            xml.append("<sheet name=\"").append(escapar(nombreHojaUnico(hojas.get(i).nombre(), usados)))
                    .append("\" sheetId=\"").append(i + 1).append("\" r:id=\"rId").append(i + 1).append("\"/>");
        }
        return xml.append("</sheets></workbook>").toString();
    }

    private static String nombreHojaUnico(String nombre, Set<String> usados) {
        String base = nombre.replaceAll("[\\\\/*?:\\[\\]]", " ").trim();
        if (base.isEmpty()) {
            base = "Hoja";
        }
        if (base.length() > 31) {
            base = base.substring(0, 31);
        }
        String candidato = base;
        int contador = 2;
        while (!usados.add(candidato.toLowerCase())) {
            String sufijo = " (" + contador++ + ")";
            candidato = base.substring(0, Math.min(base.length(), 31 - sufijo.length())) + sufijo;
        }
        return candidato;
    }

    private static String relacionesLibro(int cantidadHojas) {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">");
        for (int i = 1; i <= cantidadHojas; i++) {
            xml.append("<Relationship Id=\"rId").append(i)
                    .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet")
                    .append(i).append(".xml\"/>");
        }
        xml.append("<Relationship Id=\"rId").append(cantidadHojas + 1)
                .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>");
        return xml.append("</Relationships>").toString();
    }

    private static String estilos() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<fonts count=\"3\">"
                + "<font><sz val=\"11\"/><name val=\"Calibri\"/></font>"
                + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font>"
                + "<font><b/><sz val=\"14\"/><color rgb=\"FF0066CC\"/><name val=\"Calibri\"/></font>"
                + "</fonts>"
                + "<fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill>"
                + "<fill><patternFill patternType=\"gray125\"/></fill>"
                + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFDCE9F7\"/><bgColor indexed=\"64\"/></patternFill></fill></fills>"
                + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
                + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                + "<cellXfs count=\"3\">"
                + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
                + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\"/>"
                + "<xf numFmtId=\"0\" fontId=\"2\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>"
                + "</cellXfs>"
                + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                + "</styleSheet>";
    }

    private static String hoja(HojaReporte hoja) {
        int columnas = hoja.columnas().size();
        int[] anchos = new int[columnas];
        for (int c = 0; c < columnas; c++) {
            anchos[c] = Math.max(ANCHO_MINIMO, hoja.columnas().get(c).length() + 2);
        }
        for (List<String> fila : hoja.filas()) {
            for (int c = 0; c < columnas && c < fila.size(); c++) {
                anchos[c] = Math.max(anchos[c], Math.min(ANCHO_MAXIMO, fila.get(c).length() + 2));
            }
        }

        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><cols>");
        for (int c = 0; c < columnas; c++) {
            xml.append("<col min=\"").append(c + 1).append("\" max=\"").append(c + 1)
                    .append("\" width=\"").append(Math.min(anchos[c], ANCHO_MAXIMO)).append("\" customWidth=\"1\"/>");
        }
        xml.append("</cols><sheetData>");
        xml.append("<row r=\"1\">").append(celdaTexto(1, 1, hoja.titulo(), 2)).append("</row>");
        xml.append("<row r=\"3\">");
        for (int c = 0; c < columnas; c++) {
            xml.append(celdaTexto(3, c + 1, hoja.columnas().get(c), 1));
        }
        xml.append("</row>");
        int numeroFila = 4;
        for (List<String> fila : hoja.filas()) {
            xml.append("<row r=\"").append(numeroFila).append("\">");
            for (int c = 0; c < columnas && c < fila.size(); c++) {
                String valor = fila.get(c);
                if (ENTERO.matcher(valor).matches()) {
                    xml.append("<c r=\"").append(referencia(numeroFila, c + 1)).append("\"><v>")
                            .append(valor).append("</v></c>");
                } else {
                    xml.append(celdaTexto(numeroFila, c + 1, valor, 0));
                }
            }
            xml.append("</row>");
            numeroFila++;
        }
        return xml.append("</sheetData></worksheet>").toString();
    }

    private static String celdaTexto(int fila, int columna, String texto, int estilo) {
        return "<c r=\"" + referencia(fila, columna) + "\" t=\"inlineStr\" s=\"" + estilo
                + "\"><is><t xml:space=\"preserve\">" + escapar(texto) + "</t></is></c>";
    }

    private static String referencia(int fila, int columna) {
        StringBuilder letras = new StringBuilder();
        int restante = columna;
        while (restante > 0) {
            int resto = (restante - 1) % 26;
            letras.insert(0, (char) ('A' + resto));
            restante = (restante - 1) / 26;
        }
        return letras.toString() + fila;
    }

    private static String escapar(String texto) {
        StringBuilder resultado = new StringBuilder(texto == null ? 0 : texto.length());
        if (texto == null) {
            return "";
        }
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '&' -> resultado.append("&amp;");
                case '<' -> resultado.append("&lt;");
                case '>' -> resultado.append("&gt;");
                case '"' -> resultado.append("&quot;");
                default -> {
                    boolean valido = c == '\t' || c == '\n' || c == '\r' || c >= 0x20;
                    if (valido) {
                        resultado.append(c);
                    }
                }
            }
        }
        return resultado.toString();
    }
}
