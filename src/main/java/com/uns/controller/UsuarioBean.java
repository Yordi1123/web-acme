package com.uns.controller;

import com.uns.dao.UsuarioDAO;
import com.uns.entities.Usuario;
import com.uns.enums.RolUsuario;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Named
@ViewScoped
public class UsuarioBean implements Serializable {

    private Usuario usuario;
    private List<Usuario> usuarios;
    private UsuarioDAO usuarioDAO;

    @PostConstruct
    public void init() {
        usuarioDAO = new UsuarioDAO();
        usuario = new Usuario();
        usuarios = usuarioDAO.findAll();
    }

    public void guardar() {
        try {
            if (usuario.getId() == null) {
                // Nuevo usuario
                usuario.setEstado("Activo"); // Por defecto
                usuarioDAO.create(usuario);
            } else {
                // Actualizar usuario existente
                usuarioDAO.update(usuario);
            }
            limpiar();
            usuarios = usuarioDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void limpiar() {
        usuario = new Usuario();
    }
    
    public List<RolUsuario> getRoles() {
        return Arrays.asList(RolUsuario.values());
    }

    // --- Getters y Setters ---
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }
}
