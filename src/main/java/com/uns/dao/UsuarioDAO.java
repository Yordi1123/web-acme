package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Usuario> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en UsuarioDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public void create(Usuario usuario) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void update(Usuario usuario) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Usuario login(String username, String password) {
        EntityManager em = JPAFactory.getEntityManager();
        Usuario usuario = null;
        try {
            usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :user AND u.password = :pass AND u.estado = 'Activo'", Usuario.class)
                    .setParameter("user", username)
                    .setParameter("pass", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return usuario;
    }
}
