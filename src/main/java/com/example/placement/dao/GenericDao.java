package com.example.placement.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * GENERIC INTERFACE demonstrating OOP ABSTRACTION and POLYMORPHISM.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key identifier type
 */
public interface GenericDao<T, ID> {
    List<T> findAll() throws SQLException;
    T findById(ID id) throws SQLException;
    T save(T entity) throws SQLException;
    boolean delete(ID id) throws SQLException;
}
