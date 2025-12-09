package com.uns.enums;

public enum RolUsuario {
    ENCARGADO("Encargado Obra"),
    JEFE("Jefe Area"),
    LOGISTICA("Logistica");

    private final String label;

    RolUsuario(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
