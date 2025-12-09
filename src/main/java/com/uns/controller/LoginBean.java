package com.uns.controller;

import com.uns.dao.UsuarioDAO;
import com.uns.entities.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;
    private Usuario usuarioLogueado;
    private UsuarioDAO usuarioDAO;

    public LoginBean() {
        usuarioDAO = new UsuarioDAO();
    }

    public String login() {
        Usuario usuario = usuarioDAO.login(username, password);
        if (usuario != null) {
            usuarioLogueado = usuario;
            // Redirigir según rol (lógica simple por ahora)
            return "dashboard?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario o contraseña incorrectos"));
            return null;
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return usuarioLogueado != null;
    }

    // --- Getters y Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    public void setUsuarioLogueado(Usuario usuarioLogueado) { this.usuarioLogueado = usuarioLogueado; }
}
