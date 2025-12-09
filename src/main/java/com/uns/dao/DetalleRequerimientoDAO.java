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

    /**
     * Finds all DetalleRequerimiento items that are:
     * 1. From an APROBADO or EN_ATENCION Requerimiento
     * 2. Have cantidadAtendida < cantidadSolicitada (pending to be purchased)
     */
    public List<DetalleRequerimiento> findPendingItems() {
        EntityManager em = JPAFactory.getEntityManager();
        List<DetalleRequerimiento> lista = new ArrayList<>();
        try {
            // Usar OR en lugar de IN(:list) para compatibilidad con EclipseLink
            lista = em.createQuery(
                "SELECT d FROM DetalleRequerimiento d " +
                "JOIN FETCH d.requerimiento r " +
                "JOIN FETCH d.material m " +
                "JOIN FETCH m.unidad " +
                "JOIN FETCH m.grupo " +
                "JOIN FETCH r.proyecto " +
                "WHERE (r.estado = :estado1 OR r.estado = :estado2) " +
                "AND (d.cantidadAtendida IS NULL OR d.cantidadAtendida < d.cantidadSolicitada) " +
                "ORDER BY r.fechaSolicitud, d.material.nombre", 
                DetalleRequerimiento.class)
                .setParameter("estado1", com.uns.enums.EstadoRequerimiento.APROBADO)
                .setParameter("estado2", com.uns.enums.EstadoRequerimiento.EN_ATENCION)
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error en DetalleRequerimientoDAO.findPendingItems: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return lista;
    }

    public List<DetalleRequerimiento> findByRequerimientoConMaterial(Long idRequerimiento) {
        EntityManager em = JPAFactory.getEntityManager();
        List<DetalleRequerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT d FROM DetalleRequerimiento d " +
                "JOIN FETCH d.material m " +
                "JOIN FETCH m.unidad " +
                "JOIN FETCH m.grupo " +
                "JOIN FETCH d.requerimiento " +
                "WHERE d.requerimiento.id = :idReq", 
                DetalleRequerimiento.class)
                .setParameter("idReq", idRequerimiento)
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error en DetalleRequerimientoDAO.findByRequerimientoConMaterial: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return lista;
    }
}
