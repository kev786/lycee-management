package com.lycee.dao.impl;

import com.lycee.dao.ClasseDAO;
import com.lycee.model.Classe;
import com.lycee.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasseDAOImpl implements ClasseDAO {

    private static final String AND_SERIE = " AND serie = ? ";

    private Classe map(ResultSet rs) throws SQLException {
        Classe c = new Classe();
        c.setId(rs.getLong("id"));
        c.setNiveau(rs.getString("niveau"));
        c.setSerie(rs.getString("serie"));
        c.setEffectifMax(rs.getInt("effectif_max"));
        c.setProfPrincipal(rs.getString("prof_principal"));
        c.setSallePrincipale(rs.getString("salle_principale"));
        c.setAnneeScolaire(rs.getString("annee_scolaire"));
        try {
            c.setEffectifActuel(rs.getInt("effectif_actuel"));
        } catch (SQLException ignored) {
            // Dans certains cas le champ peut manquer si la requête n'est pas adaptée
        }
        return c;
    }

    @Override
    public void create(Classe c) throws SQLException {
        String sql = "INSERT INTO classe (niveau, serie, effectif_max, prof_principal, salle_principale, annee_scolaire) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNiveau());
            ps.setString(2, c.getSerie());
            ps.setInt(3, c.getEffectifMax());
            ps.setString(4, c.getProfPrincipal());
            ps.setString(5, c.getSallePrincipale());
            ps.setString(6, c.getAnneeScolaire());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public Classe findById(Long id) throws SQLException {
        String sql = "SELECT id, niveau, serie, effectif_max, prof_principal, salle_principale, annee_scolaire FROM classe WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Classe> findAll() throws SQLException {
        String sql = "SELECT *, (SELECT COUNT(*) FROM eleve e WHERE e.classe_id = c.id) as effectif_actuel FROM classe c ORDER BY niveau, serie";
        List<Classe> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Classe> findAllPaginated(int page, int pageSize, String search, String niveau, String serie) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT c.*, (SELECT COUNT(*) FROM eleve e WHERE e.classe_id = c.id) as effectif_actuel FROM classe c WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        
        if (search != null && !search.isBlank()) {
            sql.append(" AND (niveau LIKE ? OR serie LIKE ? OR prof_principal LIKE ? OR annee_scolaire LIKE ?) ");
            String like = "%" + search + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }
        if (niveau != null && !niveau.isBlank()) {
            sql.append(" AND niveau = ? ");
            params.add(niveau);
        }
        if (serie != null && !serie.isBlank()) {
            sql.append(AND_SERIE);
            params.add(serie);
        }
        
        sql.append(" ORDER BY niveau, serie LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<Classe> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public int countAll(String search, String niveau, String serie) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM classe WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        
        if (search != null && !search.isBlank()) {
            sql.append(" AND (niveau LIKE ? OR serie LIKE ? OR prof_principal LIKE ? OR annee_scolaire LIKE ?) ");
            String like = "%" + search + "%";
            params.add(like); params.add(like); params.add(like); params.add(like);
        }
        if (niveau != null && !niveau.isBlank()) {
            sql.append(" AND niveau = ? ");
            params.add(niveau);
        }
        if (serie != null && !serie.isBlank()) {
            sql.append(AND_SERIE);
            params.add(serie);
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public void update(Classe c) throws SQLException {
        String sql = "UPDATE classe SET niveau=?, serie=?, effectif_max=?, prof_principal=?, salle_principale=?, annee_scolaire=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNiveau());
            ps.setString(2, c.getSerie());
            ps.setInt(3, c.getEffectifMax());
            ps.setString(4, c.getProfPrincipal());
            ps.setString(5, c.getSallePrincipale());
            ps.setString(6, c.getAnneeScolaire());
            ps.setLong(7, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM classe WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public java.util.Map<String, Object> getGlobalStats() throws SQLException {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        String sqlMain = "SELECT " +
                         "(SELECT COUNT(*) FROM classe) as total_classes, " +
                         "(SELECT COUNT(*) FROM eleve) as total_eleves, " +
                         "(SELECT SUM(effectif_max) FROM classe) as total_capacite, " +
                         "(SELECT COUNT(DISTINCT classe_id) FROM eleve) as salles_occupees";
        
        try (Connection con = DBConnection.getConnection()) {
            // 1. Stats globales
            try (PreparedStatement ps = con.prepareStatement(sqlMain);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalClasses", rs.getInt("total_classes"));
                    stats.put("totalEleves", rs.getInt("total_eleves"));
                    stats.put("totalCapacite", rs.getInt("total_capacite"));
                    stats.put("sallesOccupees", rs.getInt("salles_occupees"));
                }
            }
            
            // 2. Répartition par série
            final String COL_SERIE = "serie";
            String sqlSeries = "SELECT c." + COL_SERIE + ", COUNT(e.id) as count " +
                               "FROM classe c " +
                               "LEFT JOIN eleve e ON c.id = e.classe_id " +
                               "GROUP BY c." + COL_SERIE + " ORDER BY count DESC LIMIT 5";
            
            java.util.List<java.util.Map<String, Object>> seriesDist = new java.util.ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sqlSeries);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> item = new java.util.HashMap<>();
                    item.put(COL_SERIE, rs.getString(COL_SERIE));
                    item.put("count", rs.getInt("count"));
                    seriesDist.add(item);
                }
            }
            stats.put("seriesDist", seriesDist);
        }
        return stats;
    }

    @Override
    public List<String> getSallesByNiveauSerie(String niveau, String serie) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT salle_principale FROM classe WHERE niveau = ? AND salle_principale IS NOT NULL AND salle_principale != '' ");
        List<Object> params = new ArrayList<>();
        params.add(niveau);
        if (serie != null && !serie.isBlank()) {
            sql.append(AND_SERIE);
            params.add(serie);
        }
        sql.append(" ORDER BY salle_principale");

        List<String> salles = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) salles.add(rs.getString("salle_principale"));
            }
        }
        return salles;
    }
}
