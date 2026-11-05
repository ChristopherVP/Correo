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

    public static EstadoSolicitud desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        for (EstadoSolicitud estado : values()) {
            if (estado.name().equalsIgnoreCase(texto.trim()) || estado.descripcion.equalsIgnoreCase(texto.trim())) {
                return estado;
            }
        }
        return null;
    }

    public boolean estaAbierta() {
        return this == PENDIENTE || this == EN_REVISION;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
