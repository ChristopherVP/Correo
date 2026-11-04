package uce.edu.ec.correo.dominio;

public enum EstadoSolicitud {
    PENDIENTE("Pendiente"),
    EN_REVISION("En revisión"),
    RESUELTA("Resuelta"),
    RECHAZADA("Rechazada");

    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean estaAbierta() {
        return this == PENDIENTE || this == EN_REVISION;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
