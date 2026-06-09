package com.lycee.dao;

import java.sql.SQLException;
import java.util.Map;

public interface ParametreDAO {
    Map<String, String> findAll() throws SQLException;
    String get(String cle) throws SQLException;
    void set(String cle, String valeur) throws SQLException;
    void saveAll(Map<String, String> parametres) throws SQLException;
}
