package com.lycee.dao.impl;

import com.lycee.dao.NotificationDAO;
import com.lycee.model.Notification;
import com.lycee.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    @Override
    public void create(Notification n) throws SQLException {
        String sql = "INSERT INTO notification (role_cible, message, lien, type, lue) VALUES (?,?,?,?,FALSE)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getRoleCible());
            ps.setString(2, n.getMessage());
            ps.setString(3, n.getLien());
            ps.setString(4, n.getType() != null ? n.getType() : "info");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) n.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public List<Notification> findForRole(String role, int limit) throws SQLException {
        String sql = "SELECT * FROM notification WHERE (role_cible IS NULL OR role_cible = ?) " +
                     "ORDER BY date_creation DESC LIMIT ?";
        List<Notification> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public int countUnreadForRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notification WHERE lue=FALSE AND (role_cible IS NULL OR role_cible = ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public void markAsRead(Long id) throws SQLException {
        String sql = "UPDATE notification SET lue=TRUE WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void markAllReadForRole(String role) throws SQLException {
        String sql = "UPDATE notification SET lue=TRUE WHERE lue=FALSE AND (role_cible IS NULL OR role_cible = ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.executeUpdate();
        }
    }

    private Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));
        n.setRoleCible(rs.getString("role_cible"));
        n.setMessage(rs.getString("message"));
        n.setLien(rs.getString("lien"));
        n.setType(rs.getString("type"));
        n.setLue(rs.getBoolean("lue"));
        Timestamp ts = rs.getTimestamp("date_creation");
        if (ts != null) n.setDateCreation(ts.toLocalDateTime());
        return n;
    }
}
