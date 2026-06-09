package com.lycee.dao.impl;

import com.lycee.dao.ParametreDAO;
import com.lycee.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ParametreDAOImpl implements ParametreDAO {

    @Override
    public Map<String, String> findAll() throws SQLException {
        String sql = "SELECT cle, valeur FROM parametre";
        Map<String, String> map = new HashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("cle"), rs.getString("valeur"));
            }
        }
        return map;
    }

    @Override
    public String get(String cle) throws SQLException {
        String sql = "SELECT valeur FROM parametre WHERE cle = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cle);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("valeur") : null;
            }
        }
    }

    @Override
    public void set(String cle, String valeur) throws SQLException {
        String sql = "INSERT INTO parametre (cle, valeur) VALUES (?, ?) "
                   + "ON DUPLICATE KEY UPDATE valeur = VALUES(valeur)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cle);
            ps.setString(2, valeur);
            ps.executeUpdate();
        }
    }

    @Override
    public void saveAll(Map<String, String> parametres) throws SQLException {
        if (parametres == null || parametres.isEmpty()) return;
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                String sql = "INSERT INTO parametre (cle, valeur) VALUES (?, ?) "
                           + "ON DUPLICATE KEY UPDATE valeur = VALUES(valeur)";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    for (Map.Entry<String, String> e : parametres.entrySet()) {
                        ps.setString(1, e.getKey());
                        ps.setString(2, e.getValue());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
