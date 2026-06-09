package com.lycee.dao.impl;

import com.lycee.dao.EleveDAO;
import com.lycee.dto.EleveSearchCriteria;
import com.lycee.model.Classe;
import com.lycee.model.Eleve;
import com.lycee.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EleveDAOImpl implements EleveDAO {

    private Eleve map(ResultSet rs) throws SQLException {
        Eleve e = new Eleve();
        e.setId(rs.getLong("id"));
        e.setMatricule(rs.getString("matricule"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        Date d = rs.getDate("date_naissance");
        if (d != null) e.setDateNaissance(d.toLocalDate());
        e.setClasseId(rs.getLong("classe_id"));
        e.setNomParent(rs.getString("nom_parent"));
        e.setTelParent(rs.getString("tel_parent"));
        e.setEmailParent(rs.getString("email_parent"));
        e.setPhotoFilename(rs.getString("photo_filename"));
        e.setSexe(rs.getString("sexe"));

        // JOIN avec classe si présent
        try {
            String niveau = rs.getString("c_niveau");
            if (niveau != null) {
                Classe c = new Classe();
                c.setId(rs.getLong("classe_id"));
                c.setNiveau(niveau);
                c.setSerie(rs.getString("c_serie"));
                c.setAnneeScolaire(rs.getString("c_annee"));
                e.setClasse(c);
            }
        } catch (SQLException ignored) {
            // Ignoré car les infos de classe sont optionnelles ici
        }

        return e;
    }

    private static final String SELECT_JOIN =
        "SELECT e.id, e.matricule, e.nom, e.prenom, e.date_naissance, e.classe_id, e.nom_parent, e.tel_parent, e.email_parent, e.photo_filename, e.sexe, " +
        "c.niveau AS c_niveau, c.serie AS c_serie, c.annee_scolaire AS c_annee " +
        "FROM eleve e JOIN classe c ON e.classe_id = c.id ";

    @Override
    public void create(Eleve e) throws SQLException {
        String sql = "INSERT INTO eleve (matricule,nom,prenom,date_naissance,classe_id,nom_parent,tel_parent,email_parent,photo_filename,sexe) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getMatricule());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setDate(4, e.getDateNaissance() != null ? Date.valueOf(e.getDateNaissance()) : null);
            ps.setLong(5, e.getClasseId());
            ps.setString(6, e.getNomParent());
            ps.setString(7, e.getTelParent());
            ps.setString(8, e.getEmailParent());
            ps.setString(9, e.getPhotoFilename());
            ps.setString(10, e.getSexe());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public Eleve findById(Long id) throws SQLException {
        String sql = SELECT_JOIN + "WHERE e.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public Eleve findByMatricule(String matricule) throws SQLException {
        String sql = SELECT_JOIN + "WHERE e.matricule = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricule);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Eleve> findAll() throws SQLException {
        String sql = SELECT_JOIN + "ORDER BY e.nom, e.prenom";
        List<Eleve> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public List<Eleve> findAllPaginated(int page, int pageSize, EleveSearchCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_JOIN);
        sql.append(" WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        if (criteria.getSearch() != null && !criteria.getSearch().isBlank()) {
            sql.append(" AND (e.nom LIKE ? OR e.prenom LIKE ? OR e.matricule LIKE ?) ");
            String like = "%" + criteria.getSearch() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (criteria.getClasseId() != null && criteria.getClasseId() > 0) {
            sql.append(" AND e.classe_id = ? ");
            params.add(criteria.getClasseId());
        }
        if (criteria.getSerie() != null && !criteria.getSerie().isBlank()) {
            sql.append(" AND c.serie = ? ");
            params.add(criteria.getSerie());
        }
        if (criteria.getNiveau() != null && !criteria.getNiveau().isBlank()) {
            sql.append(" AND c.niveau = ? ");
            params.add(criteria.getNiveau());
        }
        if (criteria.getSexe() != null && !criteria.getSexe().isBlank()) {
            sql.append(" AND e.sexe = ? ");
            params.add(criteria.getSexe());
        }
        if (criteria.getSalle() != null && !criteria.getSalle().isBlank()) {
            sql.append(" AND c.salle_principale = ? ");
            params.add(criteria.getSalle());
        }
        
        sql.append(" ORDER BY e.nom, e.prenom LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<Eleve> list = new ArrayList<>();
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
    public List<Eleve> findByClasse(Long classeId) throws SQLException {
        String sql = SELECT_JOIN + "WHERE e.classe_id = ? ORDER BY e.nom";
        List<Eleve> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public int countAll(EleveSearchCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM eleve e JOIN classe c ON e.classe_id = c.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        
        if (criteria.getSearch() != null && !criteria.getSearch().isBlank()) {
            sql.append(" AND (e.nom LIKE ? OR e.prenom LIKE ? OR e.matricule LIKE ?) ");
            String like = "%" + criteria.getSearch() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (criteria.getClasseId() != null && criteria.getClasseId() > 0) {
            sql.append(" AND e.classe_id = ? ");
            params.add(criteria.getClasseId());
        }
        if (criteria.getSerie() != null && !criteria.getSerie().isBlank()) {
            sql.append(" AND c.serie = ? ");
            params.add(criteria.getSerie());
        }
        if (criteria.getNiveau() != null && !criteria.getNiveau().isBlank()) {
            sql.append(" AND c.niveau = ? ");
            params.add(criteria.getNiveau());
        }
        if (criteria.getSexe() != null && !criteria.getSexe().isBlank()) {
            sql.append(" AND e.sexe = ? ");
            params.add(criteria.getSexe());
        }
        if (criteria.getSalle() != null && !criteria.getSalle().isBlank()) {
            sql.append(" AND c.salle_principale = ? ");
            params.add(criteria.getSalle());
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
    public void update(Eleve e) throws SQLException {
        String sql = "UPDATE eleve SET matricule=?,nom=?,prenom=?,date_naissance=?,classe_id=?,nom_parent=?,tel_parent=?,email_parent=?,photo_filename=?,sexe=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getMatricule());
            ps.setString(2, e.getNom());
            ps.setString(3, e.getPrenom());
            ps.setDate(4, e.getDateNaissance() != null ? Date.valueOf(e.getDateNaissance()) : null);
            ps.setLong(5, e.getClasseId());
            ps.setString(6, e.getNomParent());
            ps.setString(7, e.getTelParent());
            ps.setString(8, e.getEmailParent());
            ps.setString(9, e.getPhotoFilename());
            ps.setString(10, e.getSexe());
            ps.setLong(11, e.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM eleve WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsMatricule(String matricule, Long excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM eleve WHERE matricule=? AND id != ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matricule);
            ps.setLong(2, excludeId == null ? -1L : excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
