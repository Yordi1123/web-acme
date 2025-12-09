package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.DetalleRequerimiento;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class DetalleRequerimientoDAO {

    public void create(DetalleRequerimiento detalle) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(detalle);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }

    public List<DetalleRequerimiento> findByRequerimiento(Long idRequerimiento) {
        EntityManager em = JPAFactory.getEntityManager();
        List<DetalleRequerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT d FROM DetalleRequerimiento d WHERE d.requerimiento.id = :idReq", DetalleRequerimiento.class)
                      .setParameter("idReq", idRequerimiento)
                      .getResultList();
        } catch (Exception e) {
            System.out.println("Error en DetalleRequerimientoDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }
}
