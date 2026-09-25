package com.example.placement.dao;

import com.example.placement.model.User;
import java.sql.SQLException;

/**
 * INTERFACE demonstrating OOP ABSTRACTION for User Authentication.
 */
public interface UserDao extends GenericDao<User, Integer> {
    User findByUsername(String username) throws SQLException;
    User findByEmail(String email) throws SQLException;
}
