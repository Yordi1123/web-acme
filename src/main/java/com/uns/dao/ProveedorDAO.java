package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.Proveedor;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public List<Proveedor> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Proveedor> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en ProveedorDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public void create(Proveedor proveedor) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(proveedor);
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

    public void update(Proveedor proveedor) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(proveedor);
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
    
    public void delete(Proveedor proveedor) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            Proveedor managed = em.find(Proveedor.class, proveedor.getId());
            if (managed != null) {
                em.remove(managed);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw e; // Re-throw to let caller handle
        } finally {
            em.close();
        }
    }
}
