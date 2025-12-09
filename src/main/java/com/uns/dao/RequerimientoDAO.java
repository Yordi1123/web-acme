package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.Requerimiento;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class RequerimientoDAO {

    public List<Requerimiento> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Requerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT r FROM Requerimiento r", Requerimiento.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en RequerimientoDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public void create(Requerimiento requerimiento) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(requerimiento);
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

    public void update(Requerimiento requerimiento) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(requerimiento);
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
