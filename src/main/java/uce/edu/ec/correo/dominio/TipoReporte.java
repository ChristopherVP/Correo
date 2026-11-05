package uce.edu.ec.correo.dominio;

public enum TipoReporte {
    MI_TRABAJO("Mi trabajo (solicitudes atendidas y acciones sobre cuentas)", false),
    GENERAL("Resumen general de todo el sistema", true),
    DESEMPENO_COLABORADORES("Desempeño de cada colaborador", true);

    private final String descripcion;
    private final boolean soloPrincipal;

    TipoReporte(String descripcion, boolean soloPrincipal) {
        this.descripcion = descripcion;
        this.soloPrincipal = soloPrincipal;
    }

    public boolean isSoloPrincipal() {
        return soloPrincipal;
    }

    public boolean estaPermitidoPara(Administrador administrador) {
        return !soloPrincipal || administrador.esPrincipal();
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
