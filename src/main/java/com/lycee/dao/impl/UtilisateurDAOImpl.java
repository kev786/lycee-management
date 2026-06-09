package com.lycee.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.lycee.dao.UtilisateurDAO;
import com.lycee.model.Utilisateur;
import com.lycee.util.DBConnection;

public class UtilisateurDAOImpl implements UtilisateurDAO {

    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setId(rs.getLong("id"));
        u.setLogin(rs.getString("login"));
        u.setPasswordHache(rs.getString("password_hache"));
        u.setRole(rs.getString("role"));
        return u;
    }

    @Override
    public void create(Utilisateur u) throws SQLException {
        String sql = "INSERT INTO utilisateurs (login, password_hache, role) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getLogin());
            ps.setString(2, u.getPasswordHache());
            ps.setString(3, u.getRole());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public Utilisateur findByLogin(String login) throws SQLException {
        String sql = "SELECT id, login, password_hache, role FROM utilisateurs WHERE login = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public Utilisateur findById(Long id) throws SQLException {
        String sql = "SELECT id, login, password_hache, role FROM utilisateurs WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Utilisateur> findAll() throws SQLException {
        String sql = "SELECT id, login, password_hache, role FROM utilisateurs ORDER BY role, login";
        List<Utilisateur> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public void update(Utilisateur u) throws SQLException {
        String sql = "UPDATE utilisateurs SET login=?, password_hache=?, role=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());
            ps.setString(2, u.getPasswordHache());
            ps.setString(3, u.getRole());
            ps.setLong(4, u.getId());
            ps.executeUpdate();
        }
    }

     @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM utilisateurs WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsLogin(String login, Long excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE login=? AND id != ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setLong(2, excludeId == null ? -1L : excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
