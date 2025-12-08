package com.uns.dao;

import java.util.List;

// <T> significa que esta interfaz funciona para cualquier Entidad (Unidad, Grupo, Material)
public interface DAO<T> {
    void create(T entity);
    void update(T entity);
    void delete(T entity);
    T findById(Object id);
    List<T> findAll();
}