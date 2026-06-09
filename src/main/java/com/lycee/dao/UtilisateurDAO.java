package com.lycee.dao;

import java.sql.SQLException;
import java.util.List;

import com.lycee.model.Utilisateur;

public interface UtilisateurDAO {
    void create(Utilisateur u) throws SQLException;
    Utilisateur findByLogin(String login) throws SQLException;
    Utilisateur findById(Long id) throws SQLException;
    List<Utilisateur> findAll() throws SQLException;
    void update(Utilisateur u) throws SQLException;
    void delete(Long id) throws SQLException;
    boolean existsLogin(String login, Long excludeId) throws SQLException;
}
