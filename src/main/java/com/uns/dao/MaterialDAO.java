package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.Material;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    public void create(Material material) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(material);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    // Método para borrar (Requerido para el botón eliminar de la tabla)
    public void delete(Long id) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            Material m = em.find(Material.class, id);
            if (m != null) {
                em.remove(m);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Material> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Material> lista = new ArrayList<>();
        try {
            // Unimos tablas para no tener problemas de carga perezosa
            lista = em.createQuery("SELECT m FROM Material m JOIN FETCH m.grupo JOIN FETCH m.unidad", Material.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return lista;
    }
}