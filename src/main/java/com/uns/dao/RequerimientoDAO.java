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

    public List<Requerimiento> findByUsuario(Long idUsuario) {
        EntityManager em = JPAFactory.getEntityManager();
        List<Requerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT r FROM Requerimiento r WHERE r.usuarioSolicitante.id = :idUsuario ORDER BY r.fechaSolicitud DESC", 
                Requerimiento.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error en RequerimientoDAO.findByUsuario: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public List<Requerimiento> findPendientes() {
        EntityManager em = JPAFactory.getEntityManager();
        List<Requerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT DISTINCT r FROM Requerimiento r " +
                "LEFT JOIN FETCH r.detalles d " +
                "LEFT JOIN FETCH d.material m " +
                "LEFT JOIN FETCH m.unidad " +
                "WHERE r.estado = :estado ORDER BY r.fechaSolicitud DESC", 
                Requerimiento.class)
                .setParameter("estado", com.uns.enums.EstadoRequerimiento.PENDIENTE)
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error en RequerimientoDAO.findPendientes: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }

    public void delete(Requerimiento requerimiento) {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            em.getTransaction().begin();
            Requerimiento managed = em.find(Requerimiento.class, requerimiento.getId());
            if (managed != null) {
                // Eliminar detalles primero
                em.createQuery("DELETE FROM DetalleRequerimiento d WHERE d.requerimiento.id = :id")
                    .setParameter("id", requerimiento.getId())
                    .executeUpdate();
                em.remove(managed);
            }
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
