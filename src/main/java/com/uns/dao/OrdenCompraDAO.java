package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.OrdenCompra;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraDAO {

    public List<OrdenCompra> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<OrdenCompra> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT o FROM OrdenCompra o " +
                "LEFT JOIN FETCH o.proveedor " +
                "LEFT JOIN FETCH o.usuarioCompras " +
                "ORDER BY o.fechaEmision DESC, o.id DESC", 
                OrdenCompra.class).getResultList();
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
    
    /**
     * Genera el próximo número de orden de compra.
     * Formato: OC-YYYY-NNNNN (ej: OC-2024-00001)
     */
    public String getNextNumeroOrden() {
        EntityManager em = JPAFactory.getEntityManager();
        try {
            int year = LocalDate.now().getYear();
            String prefix = "OC-" + year + "-";
            
            // Buscar el mayor numero de este año
            String query = "SELECT MAX(o.numeroOrden) FROM OrdenCompra o WHERE o.numeroOrden LIKE :prefix";
            String maxNumero = em.createQuery(query, String.class)
                .setParameter("prefix", prefix + "%")
                .getSingleResult();
            
            int nextNumber = 1;
            if (maxNumero != null && !maxNumero.isEmpty()) {
                // Extraer el número del formato OC-YYYY-NNNNN
                String numPart = maxNumero.substring(prefix.length());
                try {
                    nextNumber = Integer.parseInt(numPart) + 1;
                } catch (NumberFormatException e) {
                    nextNumber = 1;
                }
            }
            
            return String.format("%s%05d", prefix, nextNumber);
        } catch (Exception e) {
            System.out.println("Error generando número de orden: " + e.getMessage());
            // Fallback
            return "OC-" + LocalDate.now().getYear() + "-" + String.format("%05d", System.currentTimeMillis() % 100000);
        } finally {
            em.close();
        }
    }
    
    /**
     * Encuentra órdenes por estado.
     */
    public List<OrdenCompra> findByEstado(com.uns.enums.EstadoOrden estado) {
        EntityManager em = JPAFactory.getEntityManager();
        List<OrdenCompra> lista = new ArrayList<>();
        try {
            lista = em.createQuery(
                "SELECT o FROM OrdenCompra o " +
                "LEFT JOIN FETCH o.proveedor " +
                "WHERE o.estado = :estado " +
                "ORDER BY o.fechaEmision DESC", 
                OrdenCompra.class)
                .setParameter("estado", estado)
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error en OrdenCompraDAO.findByEstado: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }
}
