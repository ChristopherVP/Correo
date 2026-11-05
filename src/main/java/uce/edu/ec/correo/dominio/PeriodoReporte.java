package uce.edu.ec.correo.dominio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public enum PeriodoReporte {
    DIARIO("Diario"),
    QUINCENAL("Quincenal (1-15 y 16-fin de mes)"),
    MENSUAL("Mensual"),
    ANUAL("Anual");

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale ESPANOL = Locale.forLanguageTag("es");

    private final String descripcion;

    PeriodoReporte(String descripcion) {
        this.descripcion = descripcion;
    }

    public RangoFechas rango(LocalDate referencia) {
        return switch (this) {
            case DIARIO -> new RangoFechas(referencia.atStartOfDay(), referencia.plusDays(1).atStartOfDay(),
                    referencia.format(FORMATO));
            case QUINCENAL -> rangoQuincenal(referencia);
            case MENSUAL -> {
                LocalDate inicio = referencia.withDayOfMonth(1);
                String mes = referencia.getMonth().getDisplayName(TextStyle.FULL, ESPANOL);
                yield new RangoFechas(inicio.atStartOfDay(), inicio.plusMonths(1).atStartOfDay(),
                        mes.substring(0, 1).toUpperCase() + mes.substring(1) + " " + referencia.getYear());
            }
            case ANUAL -> {
                LocalDate inicio = referencia.withDayOfYear(1);
                yield new RangoFechas(inicio.atStartOfDay(), inicio.plusYears(1).atStartOfDay(),
                        "Año " + referencia.getYear());
            }
        };
    }

    private static RangoFechas rangoQuincenal(LocalDate referencia) {
        LocalDate primeroDeMes = referencia.withDayOfMonth(1);
        LocalDate inicio;
        LocalDate fin;
        if (referencia.getDayOfMonth() <= 15) {
            inicio = primeroDeMes;
            fin = primeroDeMes.plusDays(15);
        } else {
            inicio = primeroDeMes.plusDays(15);
            fin = primeroDeMes.plusMonths(1);
        }
        return new RangoFechas(inicio.atStartOfDay(), fin.atStartOfDay(),
                inicio.format(FORMATO) + " al " + fin.minusDays(1).format(FORMATO));
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
