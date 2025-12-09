package com.uns.enums;

public enum RolUsuario {
    ENCARGADO_OBRA("Encargado Obra"),
    JEFE_AREA("Jefe Area"),
    LOGISTICA("Logística"),
    ADMINISTRADOR("Administrador");

    private final String label;

    RolUsuario(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
