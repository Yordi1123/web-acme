package com.uns.enums;

public enum RolUsuario {
    ENCARGADO_OBRA("Encargado de Obra"),
    JEFE_AREA("Jefe de Área"),
    EMPLEADO_COMPRAS("Empleado de Compras"),
    ADMINISTRADOR("Administrador");

    private final String label;

    RolUsuario(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
