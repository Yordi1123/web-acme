package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.AreaNegocio;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class AreaNegocioDAO {

    public List<AreaNegocio> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<AreaNegocio> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT a FROM AreaNegocio a LEFT JOIN FETCH a.jefe WHERE a.estado = 'Activo' ORDER BY a.prefijo", 
                AreaNegocio.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en AreaNegocioDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public AreaNegocio findById(Long id) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            return em.find(AreaNegocio.class, id);
        } finally {
            em.close();
        }
    }
}
