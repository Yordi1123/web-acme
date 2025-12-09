package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.Grupo;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    public void create(Grupo grupo) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(grupo);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Grupo> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Grupo> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT g FROM Grupo g", Grupo.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return lista;
    }
}