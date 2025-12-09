package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.OrdenCompra;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraDAO {

    public List<OrdenCompra> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<OrdenCompra> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT o FROM OrdenCompra o", OrdenCompra.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en OrdenCompraDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public void create(OrdenCompra orden) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(orden);
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

    public void update(OrdenCompra orden) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(orden);
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
