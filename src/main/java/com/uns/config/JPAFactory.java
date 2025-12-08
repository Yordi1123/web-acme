package com.uns.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAFactory {
    // Debe coincidir con el name="bdjpa" que pusimos en persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "bdjpa";
    private static EntityManagerFactory factory;

    // Método Singleton para crear la fábrica una sola vez
    public static EntityManagerFactory getFactory() {
        if (factory == null) {
            try {
                factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            } catch (Exception e) {
                System.out.println("Error al iniciar JPA Factory: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return factory;
    }

    // Método para obtener la conexión (EntityManager)
    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }
    
    // Método para cerrar (opcional, buena práctica)
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}