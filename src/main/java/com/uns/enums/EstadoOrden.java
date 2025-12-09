package com.uns.enums;

public enum EstadoOrden {
    BORRADOR("Borrador"),           // Orden en proceso de creación
    GENERADA("Generada"),           // Orden lista, pendiente de aprobación
    APROBADA("Aprobada"),           // Visto Bueno del Jefe, lista para enviar
    ENVIADA("Enviada"),             // Enviada al proveedor
    ANULADA("Anulada");             // Anulada (con justificación)

    private final String label;

    EstadoOrden(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
