package com.uns.enums;

public enum EstadoOrden {
    GENERADA("Generada"),
    ENVIADA("Enviada"),
    ANULADA("Anulada");

    private final String label;

    EstadoOrden(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
