package com.lycee.dao.impl;

import com.lycee.dao.ParametreDAO;
import com.lycee.model.ParametresEtablissement;
import com.lycee.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParametreDAOImpl implements ParametreDAO {

    private static final String COLS = "etablissement, annee_scolaire, logo_filename, devise, "
        + "ville, telephone, email, site_web, republique, ministere, delegation, entete_pdf, filigrane_logo";

    @Override
    public ParametresEtablissement find() throws SQLException {
        String sql = "SELECT " + COLS + " FROM parametre WHERE id = 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return new ParametresEtablissement();
    }

    @Override
    public void save(ParametresEtablissement p) throws SQLException {
        String sql = "INSERT INTO parametre (id, " + COLS + ") VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "etablissement = VALUES(etablissement), annee_scolaire = VALUES(annee_scolaire), "
            + "logo_filename = VALUES(logo_filename), devise = VALUES(devise), "
            + "ville = VALUES(ville), telephone = VALUES(telephone), email = VALUES(email), "
            + "site_web = VALUES(site_web), republique = VALUES(republique), "
            + "ministere = VALUES(ministere), delegation = VALUES(delegation), "
            + "entete_pdf = VALUES(entete_pdf), filigrane_logo = VALUES(filigrane_logo)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getEtablissement());
            ps.setString(2, p.getAnneeScolaire());
            ps.setString(3, p.getLogoFilename() != null ? p.getLogoFilename() : "");
            ps.setString(4, p.getDevise());
            ps.setString(5, p.getVille());
            ps.setString(6, p.getTelephone());
            ps.setString(7, p.getEmail());
            ps.setString(8, p.getSiteWeb());
            ps.setString(9, p.getRepublique());
            ps.setString(10, p.getMinistere());
            ps.setString(11, p.getDelegation());
            ps.setString(12, p.getEntetePdf());
            ps.setBoolean(13, p.isFiligraneLogo());
            ps.executeUpdate();
        }
    }

    private ParametresEtablissement mapRow(ResultSet rs) throws SQLException {
        ParametresEtablissement p = new ParametresEtablissement();
        p.setEtablissement(rs.getString("etablissement"));
        p.setAnneeScolaire(rs.getString("annee_scolaire"));
        p.setLogoFilename(rs.getString("logo_filename"));
        p.setDevise(rs.getString("devise"));
        p.setVille(rs.getString("ville"));
        p.setTelephone(rs.getString("telephone"));
        p.setEmail(rs.getString("email"));
        p.setSiteWeb(rs.getString("site_web"));
        p.setRepublique(rs.getString("republique"));
        p.setMinistere(rs.getString("ministere"));
        p.setDelegation(rs.getString("delegation"));
        p.setEntetePdf(rs.getString("entete_pdf"));
        p.setFiligraneLogo(rs.getBoolean("filigrane_logo"));
        return p;
    }
}
