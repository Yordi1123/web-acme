package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.Proyecto;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {

    public List<Proyecto> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Proyecto> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT p FROM Proyecto p", Proyecto.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en ProyectoDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public void create(Proyecto proyecto) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(proyecto);
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

    public void update(Proyecto proyecto) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(proyecto);
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
}
