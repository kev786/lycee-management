package com.lycee.dao.impl;

import com.lycee.dao.AbsenceDAO;
import com.lycee.model.Absence;
import com.lycee.util.DBConnection;

import java.sql.*;
import java.util.*;

public class AbsenceDAOImpl implements AbsenceDAO {

    private Absence map(ResultSet rs) throws SQLException {
        Absence a = new Absence();
        a.setId(rs.getLong("id"));
        a.setEleveId(rs.getLong("eleve_id"));
        java.sql.Date d = rs.getDate("date_absence");
        if (d != null) a.setDateAbsence(d.toLocalDate());
        a.setDureeHeures(rs.getInt("duree_heures"));
        a.setMatiere(rs.getString("matiere"));
        a.setJustifiee(rs.getBoolean("justifiee"));
        a.setMotif(rs.getString("motif"));
        try {
            a.setEleveNom(rs.getString("e_nom"));
            a.setElevePrenom(rs.getString("e_prenom"));
            String niveau = rs.getString("c_niveau");
            String serie = rs.getString("c_serie");
            if (niveau != null) {
                a.setClasseLibelle(niveau + " " + serie);
            }
        } catch (SQLException ignored) {
            // colonnes JOIN optionnelles
        }
        return a;
    }

    @Override
    public void create(Absence a) throws SQLException {
        String sql = "INSERT INTO absence (eleve_id,date_absence,duree_heures,matiere,justifiee,motif) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getEleveId());
            ps.setDate(2, java.sql.Date.valueOf(a.getDateAbsence()));
            ps.setInt(3, a.getDureeHeures());
            ps.setString(4, a.getMatiere());
            ps.setBoolean(5, a.isJustifiee());
            ps.setString(6, a.getMotif());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public Absence findById(Long id) throws SQLException {
        String sql = "SELECT id, eleve_id, date_absence, duree_heures, matiere, justifiee, motif FROM absence WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Absence> findByEleve(Long eleveId) throws SQLException {
        String sql = "SELECT id, eleve_id, date_absence, duree_heures, matiere, justifiee, motif FROM absence WHERE eleve_id=? ORDER BY date_absence DESC";
        List<Absence> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Absence> findAllPaginated(int page, int pageSize, String search, Long classeId, String serie, String niveau, String salle) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT a.*, e.nom AS e_nom, e.prenom AS e_prenom, c.niveau AS c_niveau, c.serie AS c_serie " +
            "FROM absence a JOIN eleve e ON a.eleve_id=e.id JOIN classe c ON e.classe_id=c.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (e.nom LIKE ? OR e.prenom LIKE ? OR a.matiere LIKE ?) ");
            String like = "%" + search + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (classeId != null && classeId > 0) {
            sql.append(" AND e.classe_id = ? ");
            params.add(classeId);
        }
        if (serie != null && !serie.isBlank()) {
            sql.append(" AND c.serie = ? ");
            params.add(serie);
        }
        if (niveau != null && !niveau.isBlank()) {
            sql.append(" AND c.niveau = ? ");
            params.add(niveau);
        }
        if (salle != null && !salle.isBlank()) {
            sql.append(" AND c.salle_principale = ? ");
            params.add(salle);
        }

        sql.append(" ORDER BY a.date_absence DESC LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<Absence> list = new ArrayList<>();
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
    public int countAll(String search, Long classeId, String serie, String niveau, String salle) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM absence a JOIN eleve e ON a.eleve_id=e.id JOIN classe c ON e.classe_id=c.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (e.nom LIKE ? OR e.prenom LIKE ? OR a.matiere LIKE ?) ");
            String like = "%" + search + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (classeId != null && classeId > 0) {
            sql.append(" AND e.classe_id = ? ");
            params.add(classeId);
        }
        if (serie != null && !serie.isBlank()) {
            sql.append(" AND c.serie = ? ");
            params.add(serie);
        }
        if (niveau != null && !niveau.isBlank()) {
            sql.append(" AND c.niveau = ? ");
            params.add(niveau);
        }
        if (salle != null && !salle.isBlank()) {
            sql.append(" AND c.salle_principale = ? ");
            params.add(salle);
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
    public void update(Absence a) throws SQLException {
        String sql = "UPDATE absence SET eleve_id=?,date_absence=?,duree_heures=?,matiere=?,justifiee=?,motif=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, a.getEleveId());
            ps.setDate(2, java.sql.Date.valueOf(a.getDateAbsence()));
            ps.setInt(3, a.getDureeHeures());
            ps.setString(4, a.getMatiere());
            ps.setBoolean(5, a.isJustifiee());
            ps.setString(6, a.getMotif());
            ps.setLong(7, a.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM absence WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public int countAbsencesInjustifieesParEleve(Long eleveId, int trimestre) throws SQLException {
        // Trimestre 1 : sept-dec (mois 9-12), T2 : jan-mars (mois 1-3), T3 : avr-juin (mois 4-6)
        String sql = "SELECT COUNT(*) AS total FROM absence " +
                     "WHERE eleve_id=? AND justifiee=FALSE " +
                     "AND ((?=1 AND MONTH(date_absence) BETWEEN 9 AND 12) " +
                     "     OR (?=2 AND MONTH(date_absence) BETWEEN 1 AND 3) " +
                     "     OR (?=3 AND MONTH(date_absence) BETWEEN 4 AND 6))";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            ps.setInt(2, trimestre);
            ps.setInt(3, trimestre);
            ps.setInt(4, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        }
    }

    @Override
    public int countAbsencesInjustifieesAnnuel(Long eleveId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence WHERE eleve_id=? AND justifiee=FALSE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public int sumHeuresInjustifieesAnnuel(Long eleveId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(duree_heures), 0) FROM absence WHERE eleve_id=? AND justifiee=FALSE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public List<Map<String, Object>> getTauxAbsenteismeParClasse() throws SQLException {
        // Hypothèse : 30 heures de cours par semaine, 36 semaines = 1080h/an
        int heuresTotales = 1080;
        String sql =
            "SELECT c.id AS classe_id, c.niveau, c.serie, " +
            "  COUNT(a.id) AS nb_absences, " +
            "  COALESCE(SUM(a.duree_heures),0) AS heures_abs, " +
            "  ROUND(COALESCE(SUM(a.duree_heures),0) * 100.0 / (" + heuresTotales + " * COUNT(DISTINCT e.id)), 2) AS taux " +
            "FROM classe c " +
            "LEFT JOIN eleve e ON e.classe_id=c.id " +
            "LEFT JOIN absence a ON a.eleve_id=e.id " +
            "GROUP BY c.id, c.niveau, c.serie ORDER BY taux DESC";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("classeId", rs.getLong("classe_id"));
                row.put("niveau", rs.getString("niveau"));
                row.put("serie", rs.getString("serie"));
                row.put("nbAbsences", rs.getInt("nb_absences"));
                row.put("heuresAbs", rs.getInt("heures_abs"));
                row.put("taux", rs.getDouble("taux"));
                result.add(row);
            }
        }
        return result;
    }
    @Override
    public int getTotalHeuresInjustifieesGlobale() throws SQLException {
        String sql = "SELECT COALESCE(SUM(duree_heures),0) AS total FROM absence WHERE justifiee=FALSE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    @Override
    public List<Map<String, Object>> getAbsencesParMatiere(Long eleveId) throws SQLException {
        String sql = "SELECT matiere, SUM(duree_heures) AS total, " +
                     "SUM(CASE WHEN justifiee=TRUE THEN duree_heures ELSE 0 END) AS justifiees, " +
                     "SUM(CASE WHEN justifiee=FALSE THEN duree_heures ELSE 0 END) AS injustifiees " +
                     "FROM absence WHERE eleve_id=? GROUP BY matiere ORDER BY total DESC";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("matiere", rs.getString("matiere"));
                    row.put("total", rs.getInt("total"));
                    row.put("justifiees", rs.getInt("justifiees"));
                    row.put("injustifiees", rs.getInt("injustifiees"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    private static final String FILTER_TRIMESTRE =
        "((? = 1 AND MONTH(date_absence) BETWEEN 9 AND 12) " +
        " OR (? = 2 AND MONTH(date_absence) BETWEEN 1 AND 3) " +
        " OR (? = 3 AND MONTH(date_absence) BETWEEN 4 AND 6))";

    @Override
    public int countInjustifieesTrimestre(int trimestre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence WHERE justifiee=FALSE AND " + FILTER_TRIMESTRE;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trimestre);
            ps.setInt(2, trimestre);
            ps.setInt(3, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public int countJustifieesTrimestre(int trimestre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence WHERE justifiee=TRUE AND " + FILTER_TRIMESTRE;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trimestre);
            ps.setInt(2, trimestre);
            ps.setInt(3, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public int countTotalMoisCourant() throws SQLException {
        String sql = "SELECT COUNT(*) FROM absence WHERE MONTH(date_absence)=MONTH(CURDATE()) AND YEAR(date_absence)=YEAR(CURDATE())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public int countElevesSeuilInjustifie(int seuil, int trimestre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM (SELECT eleve_id, SUM(duree_heures) AS h FROM absence " +
                     "WHERE justifiee=FALSE AND " + FILTER_TRIMESTRE + " GROUP BY eleve_id HAVING h>=?) t";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trimestre);
            ps.setInt(2, trimestre);
            ps.setInt(3, trimestre);
            ps.setInt(4, seuil);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public List<Map<String, Object>> getHeuresParMois(int nbMois) throws SQLException {
        String sql =
            "SELECT YEAR(date_absence) AS annee, MONTH(date_absence) AS mois, " +
            "COALESCE(SUM(duree_heures), 0) AS heures " +
            "FROM absence WHERE date_absence >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
            "GROUP BY YEAR(date_absence), MONTH(date_absence) " +
            "ORDER BY annee, mois";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nbMois);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("annee", rs.getInt("annee"));
                    row.put("mois", rs.getInt("mois"));
                    row.put("heures", rs.getInt("heures"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    @Override
    public double getTauxAbsenteeismeGlobal() throws SQLException {
        int heuresTotales = 1080;
        String sql =
            "SELECT COUNT(DISTINCT e.id) AS effectif, COALESCE(SUM(a.duree_heures), 0) AS heures " +
            "FROM eleve e LEFT JOIN absence a ON a.eleve_id = e.id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return 0.0;
            int effectif = rs.getInt("effectif");
            int heures = rs.getInt("heures");
            if (effectif == 0) return 0.0;
            return Math.round(heures * 10000.0 / (heuresTotales * effectif)) / 100.0;
        }
    }

    @Override
    public int sumHeuresInjustifieesParEleve(Long eleveId, int trimestre) throws SQLException {
        String sql = "SELECT COALESCE(SUM(duree_heures),0) FROM absence WHERE eleve_id=? AND justifiee=FALSE AND "
                     + FILTER_TRIMESTRE;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            ps.setInt(2, trimestre);
            ps.setInt(3, trimestre);
            ps.setInt(4, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
