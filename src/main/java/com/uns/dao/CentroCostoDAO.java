package com.uns.dao;

import com.uns.config.JPAFactory;
import com.uns.entities.CentroCosto;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class CentroCostoDAO {

    public List<CentroCosto> findAll() {
        EntityManager em = JPAFactory.getEntityManager();
        List<CentroCosto> lista = new ArrayList<>();
        try {
            lista = em.createQuery("SELECT c FROM CentroCosto c", CentroCosto.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error en CentroCostoDAO: " + e.getMessage());
        } finally {
            em.close();
        }
        return lista;
    }
}
