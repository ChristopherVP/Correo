package uce.edu.ec.correo.dominio;

import java.util.List;

public record HojaReporte(String nombre, String titulo, List<String> columnas, List<List<String>> filas) {
}
