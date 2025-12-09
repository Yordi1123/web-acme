package com.uns.enums;

public enum EstadoRequerimiento {
    PENDIENTE("Pendiente"),
    OBSERVADO("Observado"),
    APROBADO("Aprobado"),
    EN_ATENCION("En Atencion"),
    ATENDIDO_TOTAL("Atendido Total"),
    RECHAZADO("Rechazado");

    private final String label;

    EstadoRequerimiento(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
