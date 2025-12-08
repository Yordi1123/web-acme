package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.Unidad;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class UnidadDAO {
    
    // Método para listar (así probamos si trae datos de la BD)
    public List<Unidad> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Unidad> lista = new ArrayList<>();
        try {
            // Consulta JPQL simple
            lista = em.createQuery("SELECT u FROM Unidad u", Unidad.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en DAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }
    
    // Método simple para crear (opcional por ahora)
    public void create(Unidad unidad) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(unidad);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}