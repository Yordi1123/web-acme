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

    public List<Requerimiento> findByUsuarioConDetalles(Long idUsuario) {
        EntityManager em = JPAFactory.getEntityManager();
        List<Requerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT DISTINCT r FROM Requerimiento r " +
                "LEFT JOIN FETCH r.detalles d " +
                "LEFT JOIN FETCH d.material m " +
                "LEFT JOIN FETCH m.unidad " +
                "LEFT JOIN FETCH m.grupo " +
                "LEFT JOIN FETCH r.proyecto " +
                "LEFT JOIN FETCH r.centroCosto " +
                "WHERE r.usuarioSolicitante.id = :idUsuario ORDER BY r.fechaSolicitud DESC", 
                Requerimiento.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
            
            // Cargar relaciones opcionales manualmente para evitar MultipleBagFetchException
            for (Requerimiento r : lista) {
                if (r.getAreaNegocio() != null) {
                    r.getAreaNegocio().getPrefijo(); // force load
                }
                if (r.getJefeAprobador() != null) {
                    r.getJefeAprobador().getNombreCompleto(); // force load
                }
                if (r.getUsuarioSolicitante() != null) {
                    r.getUsuarioSolicitante().getNombreCompleto(); // force load
                }
            }
        } catch (Exception e) {
            System.out.println("Error en RequerimientoDAO.findByUsuarioConDetalles: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return lista;
    }

    /**
     * Encuentra requerimientos PENDIENTES asignados a un jefe específico.
     */
    public List<Requerimiento> findPendientesByJefe(Long idJefe) {
        EntityManager em = JPAFactory.getEntityManager();
        List<Requerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT DISTINCT r FROM Requerimiento r " +
                "LEFT JOIN FETCH r.detalles d " +
                "LEFT JOIN FETCH d.material m " +
                "LEFT JOIN FETCH m.unidad " +
                "LEFT JOIN FETCH r.usuarioSolicitante " +
                "LEFT JOIN FETCH r.proyecto " +
                "WHERE r.estado = :estado AND r.jefeAprobador.id = :idJefe " +
                "ORDER BY r.fechaSolicitud DESC", 
                Requerimiento.class)
                .setParameter("estado", com.uns.enums.EstadoRequerimiento.PENDIENTE)
                .setParameter("idJefe", idJefe)
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error en RequerimientoDAO.findPendientesByJefe: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return lista;
    }

    /**
     * Encuentra requerimientos aprobados por un jefe específico (para seguimiento).
     * Incluye estados: APROBADO, EN_ATENCION, ATENDIDO_TOTAL
     */
    public List<Requerimiento> findAprobadosByJefe(Long idJefe) {
        EntityManager em = JPAFactory.getEntityManager();
        List<Requerimiento> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT DISTINCT r FROM Requerimiento r " +
                "LEFT JOIN FETCH r.detalles d " +
                "LEFT JOIN FETCH d.material m " +
                "LEFT JOIN FETCH m.unidad " +
                "LEFT JOIN FETCH r.usuarioSolicitante " +
                "LEFT JOIN FETCH r.proyecto " +
                "WHERE r.jefeAprobador.id = :idJefe " +
                "AND r.estado IN (:estados) " +
                "ORDER BY r.fechaSolicitud DESC", 
                Requerimiento.class)
                .setParameter("idJefe", idJefe)
                .setParameter("estados", java.util.Arrays.asList(
                    com.uns.enums.EstadoRequerimiento.APROBADO,
                    com.uns.enums.EstadoRequerimiento.EN_ATENCION,
                    com.uns.enums.EstadoRequerimiento.ATENDIDO_TOTAL))
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error en RequerimientoDAO.findAprobadosByJefe: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return lista;
    }
}
