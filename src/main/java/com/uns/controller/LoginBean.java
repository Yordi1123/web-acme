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

    public String iniciarSesion() {
        Usuario usuario = usuarioDAO.login(username, password);
        if (usuario != null) {
            usuarioLogueado = usuario;
            
            // Lógica de Redirección según Rol
            switch (usuario.getCargo()) {
                case ENCARGADO_OBRA:
                    return "/encargado/dashboard.xhtml?faces-redirect=true";
                case JEFE_AREA:
                    return "/jefe/dashboard.xhtml?faces-redirect=true";
                case EMPLEADO_COMPRAS:
                    return "/compras/dashboard.xhtml?faces-redirect=true";
                case ADMINISTRADOR:
                    return "/admin/dashboard.xhtml?faces-redirect=true";
                default:
                     return "/index.xhtml?faces-redirect=true";
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario o contraseña incorrectos"));
            return null;
        }
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return usuarioLogueado != null;
    }
    
    public void verificarSesion() {
        try {
             if (usuarioLogueado == null) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(
                         FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/login.xhtml");
             }
        } catch (Exception e) {
            // log error
        }
    }

    // --- Getters y Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    public void setUsuarioLogueado(Usuario usuarioLogueado) { this.usuarioLogueado = usuarioLogueado; }
}
