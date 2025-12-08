/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.uns.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author yordiLv
 */
public class TestConnection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBA DE CONEXIÓN A LARAGON ---");
        try {
            // Intenta crear la fábrica (esto lee el persistence.xml)
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("bdjpa");
            EntityManager em = emf.createEntityManager();
            
            System.out.println("✅ ¡ÉXITO! Conexión establecida con Laragon.");
            
            // Intenta contar cuántas unidades hay
            long count = (long) em.createQuery("SELECT COUNT(u) FROM Unidad u").getSingleResult();
            System.out.println("📊 Se encontraron " + count + " unidades en la base de datos.");
            
            em.close();
            emf.close();
        } catch (Exception e) {
            System.out.println("❌ ERROR FATAL DE CONEXIÓN:");
            // Esto imprimirá la causa real (contraseña mal, base de datos no existe, etc.)
            e.printStackTrace();
        }
    }

}
