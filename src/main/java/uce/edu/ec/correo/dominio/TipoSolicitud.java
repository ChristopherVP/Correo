package uce.edu.ec.correo.dominio;

public enum TipoSolicitud {
    QUEJA("Queja"),
    REPORTE_TECNICO("Reporte técnico"),
    RECOMENDACION("Recomendación de mejora");

    private final String descripcion;

    TipoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
