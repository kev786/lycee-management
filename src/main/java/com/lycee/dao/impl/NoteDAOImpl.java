package com.lycee.dao.impl;

import com.lycee.dao.NoteDAO;
import com.lycee.model.NoteEleve;
import com.lycee.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class NoteDAOImpl implements NoteDAO {

    private static final String COL_MATIERE = "matiere";
    private static final String COL_MOYENNE = "moyenne";
    private static final String COL_PRENOM  = "prenom";


    private NoteEleve map(ResultSet rs) throws SQLException {
        NoteEleve n = new NoteEleve();
        n.setId(rs.getLong("id"));
        n.setEleveId(rs.getLong("eleve_id"));
        n.setMatiere(rs.getString(COL_MATIERE));
        n.setCoefficient(rs.getInt("coefficient"));
        n.setNotesValeur(rs.getBigDecimal("notes_valeur"));
        n.setTrimestre(rs.getInt("trimestre"));
        n.setProfSaisie(rs.getString("prof_saisie"));
        return n;
    }

    @Override
    public void create(NoteEleve n) throws SQLException {
        String sql = "INSERT INTO note_eleve (eleve_id,matiere,coefficient,notes_valeur,trimestre,prof_saisie) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, n.getEleveId());
            ps.setString(2, n.getMatiere());
            ps.setInt(3, n.getCoefficient());
            ps.setBigDecimal(4, n.getNotesValeur());
            ps.setInt(5, n.getTrimestre());
            ps.setString(6, n.getProfSaisie());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) n.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public NoteEleve findById(Long id) throws SQLException {
        String sql = "SELECT id, eleve_id, matiere, coefficient, notes_valeur, trimestre, prof_saisie FROM note_eleve WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<NoteEleve> findByEleve(Long eleveId) throws SQLException {
        String sql = "SELECT id, eleve_id, matiere, coefficient, notes_valeur, trimestre, prof_saisie FROM note_eleve WHERE eleve_id=? ORDER BY trimestre, matiere";
        List<NoteEleve> list = new ArrayList<>();
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
    public List<NoteEleve> findByEleveAndTrimestre(Long eleveId, int trimestre) throws SQLException {
        String sql = "SELECT id, eleve_id, matiere, coefficient, notes_valeur, trimestre, prof_saisie FROM note_eleve WHERE eleve_id=? AND trimestre=? ORDER BY matiere";
        List<NoteEleve> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            ps.setInt(2, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private void buildSearchQuery(StringBuilder sql, List<Object> params, com.lycee.dto.NoteSearchCriteria criteria) {
        if (criteria == null) return;
        
        if (criteria.getSearch() != null && !criteria.getSearch().isBlank()) {
            String like = "%" + criteria.getSearch() + "%";
            sql.append(" AND (e.nom LIKE ? OR e.prenom LIKE ? OR n.matiere LIKE ?) ");
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (criteria.getNiveau() != null && !criteria.getNiveau().isBlank()) {
            sql.append(" AND e.classe_id IN (SELECT id FROM classe WHERE niveau = ?) ");
            params.add(criteria.getNiveau());
        }
        if (criteria.getSerie() != null && !criteria.getSerie().isBlank()) {
            sql.append(" AND e.classe_id IN (SELECT id FROM classe WHERE serie = ?) ");
            params.add(criteria.getSerie());
        }
        if (criteria.getSalle() != null && !criteria.getSalle().isBlank()) {
            sql.append(" AND e.classe_id IN (SELECT id FROM classe WHERE salle_principale = ?) ");
            params.add(criteria.getSalle());
        }
        if (criteria.getMatiere() != null && !criteria.getMatiere().isBlank()) {
            sql.append(" AND n.matiere = ? ");
            params.add(criteria.getMatiere());
        }
        if (criteria.getTrimestre() != null) {
            sql.append(" AND n.trimestre = ? ");
            params.add(criteria.getTrimestre());
        }
    }

    @Override
    public List<NoteEleve> findAllPaginated(int page, int pageSize, com.lycee.dto.NoteSearchCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT n.id, n.eleve_id, n.matiere, n.coefficient, n.notes_valeur, n.trimestre, n.prof_saisie, " +
            "e.nom, e.prenom, e.photo_filename " +
            "FROM note_eleve n JOIN eleve e ON n.eleve_id=e.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        buildSearchQuery(sql, params, criteria);
        
        sql.append(" ORDER BY e.nom, n.trimestre LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        List<NoteEleve> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NoteEleve n = map(rs);
                    com.lycee.model.Eleve e = new com.lycee.model.Eleve();
                    e.setId(n.getEleveId());
                    e.setNom(rs.getString("nom"));
                    e.setPrenom(rs.getString(COL_PRENOM));
                    e.setPhotoFilename(rs.getString("photo_filename"));
                    n.setEleve(e);
                    list.add(n);
                }
            }
        }
        return list;
    }

    @Override
    public int countAll(com.lycee.dto.NoteSearchCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM note_eleve n JOIN eleve e ON n.eleve_id=e.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        buildSearchQuery(sql, params, criteria);

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
    public void update(NoteEleve n) throws SQLException {
        String sql = "UPDATE note_eleve SET eleve_id=?,matiere=?,coefficient=?,notes_valeur=?,trimestre=?,prof_saisie=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, n.getEleveId());
            ps.setString(2, n.getMatiere());
            ps.setInt(3, n.getCoefficient());
            ps.setBigDecimal(4, n.getNotesValeur());
            ps.setInt(5, n.getTrimestre());
            ps.setString(6, n.getProfSaisie());
            ps.setLong(7, n.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM note_eleve WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void saveBulk(List<NoteEleve> notes) throws SQLException {
        if (notes == null || notes.isEmpty()) return;
        String sql = "INSERT INTO note_eleve (eleve_id, matiere, coefficient, notes_valeur, trimestre, prof_saisie) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (NoteEleve n : notes) {
                    ps.setLong(1, n.getEleveId());
                    ps.setString(2, n.getMatiere());
                    ps.setInt(3, n.getCoefficient());
                    ps.setBigDecimal(4, n.getNotesValeur());
                    ps.setInt(5, n.getTrimestre());
                    ps.setString(6, n.getProfSaisie());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    @Override
    public BigDecimal getMoyenneEleve(Long eleveId, int trimestre) throws SQLException {
        // Moyenne pondérée : SUM(note * coef) / SUM(coef)
        String sql = "SELECT SUM(notes_valeur * coefficient) / SUM(coefficient) AS " + COL_MOYENNE + " " +
                     "FROM note_eleve WHERE eleve_id=? AND trimestre=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            ps.setInt(2, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal m = rs.getBigDecimal(COL_MOYENNE);
                    return m != null ? m.setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getMoyenneAnnuelle(Long eleveId) throws SQLException {
        // Moyenne des moyennes de chaque trimestre disponible
        String sql =
            "SELECT AVG(sub.moy) AS moyenne_annuelle FROM (" +
            "  SELECT SUM(notes_valeur * coefficient) / SUM(coefficient) AS moy" +
            "  FROM note_eleve WHERE eleve_id = ?" +
            "  GROUP BY trimestre" +
            ") sub";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal m = rs.getBigDecimal("moyenne_annuelle");
                    return m != null ? m.setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public int countTrimestresWithNotes(Long eleveId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT trimestre) FROM note_eleve WHERE eleve_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public int getSommeCoefficients(Long eleveId, int trimestre) throws SQLException {
        String sql =
            "SELECT SUM(coefficient) FROM note_eleve WHERE eleve_id = ? AND trimestre = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, eleveId);
            ps.setInt(2, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public BigDecimal getMoyenneClasse(Long classeId, int trimestre) throws SQLException {
        String sql =
            "SELECT AVG(sub.moy) AS moyenne_classe FROM (" +
            "  SELECT SUM(n.notes_valeur * n.coefficient) / SUM(n.coefficient) AS moy " +
            "  FROM note_eleve n JOIN eleve e ON n.eleve_id = e.id " +
            "  WHERE e.classe_id = ? AND n.trimestre = ? " +
            "  GROUP BY n.eleve_id" +
            ") sub";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            ps.setInt(2, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal m = rs.getBigDecimal("moyenne_classe");
                    return m != null ? m.setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public int getRangEleve(Long eleveId, Long classeId, int trimestre) throws SQLException {
        String sql =
            "SELECT rang FROM (" +
            "  SELECT n.eleve_id, " +
            "    RANK() OVER (ORDER BY SUM(n.notes_valeur * n.coefficient) / SUM(n.coefficient) DESC) AS rang" +
            "  FROM note_eleve n JOIN eleve e ON n.eleve_id = e.id" +
            "  WHERE e.classe_id = ? AND n.trimestre = ?" +
            "  GROUP BY n.eleve_id" +
            ") rankings WHERE eleve_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            ps.setInt(2, trimestre);
            ps.setLong(3, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("rang") : 0;
            }
        }
    }

    @Override
    public int getRangAnnuel(Long eleveId, Long classeId) throws SQLException {
        String sql =
            "SELECT rang FROM (" +
            "  SELECT eleve_id," +
            "    RANK() OVER (ORDER BY AVG(moy) DESC) AS rang" +
            "  FROM (" +
            "    SELECT n.eleve_id, n.trimestre," +
            "      SUM(n.notes_valeur * n.coefficient) / SUM(n.coefficient) AS moy" +
            "    FROM note_eleve n JOIN eleve e ON n.eleve_id = e.id" +
            "    WHERE e.classe_id = ?" +
            "    GROUP BY n.eleve_id, n.trimestre" +
            "  ) sub GROUP BY eleve_id" +
            ") rankings WHERE eleve_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            ps.setLong(2, eleveId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("rang") : 0;
            }
        }
    }

    @Override
    public BigDecimal getMoyenneClasseAnnuelle(Long classeId) throws SQLException {
        String sql =
            "SELECT AVG(ann.moy) FROM (" +
            "  SELECT AVG(sub.moy) AS moy FROM (" +
            "    SELECT n.eleve_id, SUM(n.notes_valeur * n.coefficient) / SUM(n.coefficient) AS moy" +
            "    FROM note_eleve n JOIN eleve e ON n.eleve_id = e.id" +
            "    WHERE e.classe_id = ?" +
            "    GROUP BY n.eleve_id, n.trimestre" +
            "  ) sub GROUP BY sub.eleve_id" +
            ") ann";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal m = rs.getBigDecimal(1);
                    return m != null ? m.setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<Map<String, Object>> getMoyennesParClasse(int trimestre) throws SQLException {
        String sql =
            "SELECT c.id AS classe_id, c.niveau, c.serie, c.annee_scolaire, " +
            "  AVG(sub.moy) AS moyenne_classe, " +
            "  MIN(sub.moy) AS min_moy, MAX(sub.moy) AS max_moy, COUNT(sub.eleve_id) AS nb_eleves " +
            "FROM (" +
            "  SELECT n.eleve_id, e.classe_id, " +
            "    SUM(n.notes_valeur * n.coefficient) / SUM(n.coefficient) AS moy " +
            "  FROM note_eleve n JOIN eleve e ON n.eleve_id=e.id " +
            "  WHERE n.trimestre=? GROUP BY n.eleve_id, e.classe_id" +
            ") sub JOIN classe c ON sub.classe_id=c.id " +
            "GROUP BY c.id, c.niveau, c.serie, c.annee_scolaire ORDER BY c.niveau, c.serie";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("classeId", rs.getLong("classe_id"));
                    row.put("niveau", rs.getString("niveau"));
                    row.put("serie", rs.getString("serie"));
                    row.put("anneeScolaire", rs.getString("annee_scolaire"));
                    row.put("moyenneClasse", rs.getBigDecimal("moyenne_classe"));
                    row.put("minMoy", rs.getBigDecimal("min_moy"));
                    row.put("maxMoy", rs.getBigDecimal("max_moy"));
                    row.put("nbEleves", rs.getInt("nb_eleves"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTauxEchecParMatiere(int trimestre) throws SQLException {
        // Taux d'échec = nb élèves < 10 / total élèves ayant une note dans cette matière
        String sql =
            "SELECT " + COL_MATIERE + ", " +
            "  COUNT(*) AS total, " +
            "  SUM(CASE WHEN notes_valeur < 10 THEN 1 ELSE 0 END) AS nb_echec, " +
            "  AVG(notes_valeur) AS " + COL_MOYENNE + ", " +
            "  ROUND(SUM(CASE WHEN notes_valeur < 10 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS taux_echec " +
            "FROM note_eleve WHERE trimestre=? " +
            "GROUP BY " + COL_MATIERE + " ORDER BY taux_echec DESC";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put(COL_MATIERE, rs.getString(COL_MATIERE));
                    row.put("total", rs.getInt("total"));
                    row.put("nbEchec", rs.getInt("nb_echec"));
                    row.put(COL_MOYENNE, rs.getBigDecimal(COL_MOYENNE));
                    row.put("tauxEchec", rs.getDouble("taux_echec"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTop10ParClasse(Long classeId, int trimestre) throws SQLException {
        String sql =
            "SELECT e.id, e.prenom, e.nom, e.matricule, " +
            "  SUM(n.notes_valeur * n.coefficient) / SUM(n.coefficient) AS moyenne, " +
            "  RANK() OVER (ORDER BY SUM(n.notes_valeur * n.coefficient) / SUM(n.coefficient) DESC) AS rang " +
            "FROM note_eleve n JOIN eleve e ON n.eleve_id=e.id " +
            "WHERE e.classe_id=? AND n.trimestre=? " +
            "GROUP BY e.id, e.prenom, e.nom, e.matricule " +
            "ORDER BY moyenne DESC LIMIT 10";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, classeId);
            ps.setInt(2, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put(COL_PRENOM, rs.getString(COL_PRENOM));
                    row.put("nom", rs.getString("nom"));
                    row.put("matricule", rs.getString("matricule"));
                    row.put(COL_MOYENNE, rs.getBigDecimal(COL_MOYENNE));
                    row.put("rang", rs.getInt("rang"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    @Override
    public double getMoyenneGlobale() throws SQLException {
        String sql = "SELECT SUM(notes_valeur * coefficient) / SUM(coefficient) FROM note_eleve";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    @Override
    public List<Map<String, Object>> getRepartitionDecisions(int trimestre) throws SQLException {
        String sql =
            "SELECT decision, COUNT(*) AS count FROM (" +
            "  SELECT eleve_id," +
            "    CASE WHEN SUM(notes_valeur * coefficient) / SUM(coefficient) >= 10 THEN 'Admis' ELSE 'Échec' END AS decision" +
            "  FROM note_eleve WHERE trimestre=? GROUP BY eleve_id" +
            ") sub GROUP BY decision";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, trimestre);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("decision", rs.getString("decision"));
                    row.put("count", rs.getInt("count"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> findRecentes(int limit) throws SQLException {
        String sql =
            "SELECT n.id, n.matiere, n.notes_valeur, n.trimestre, " +
            "e.nom, e.prenom, c.niveau, c.serie " +
            "FROM note_eleve n " +
            "JOIN eleve e ON n.eleve_id = e.id " +
            "LEFT JOIN classe c ON e.classe_id = c.id " +
            "ORDER BY n.id DESC LIMIT ?";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put(COL_MATIERE, rs.getString(COL_MATIERE));
                    row.put("note", rs.getBigDecimal("notes_valeur"));
                    row.put("trimestre", rs.getInt("trimestre"));
                    row.put("nom", rs.getString("nom"));
                    row.put(COL_PRENOM, rs.getString(COL_PRENOM));
                    String niveau = rs.getString("niveau");
                    String serie = rs.getString("serie");
                    row.put("classeLibelle", niveau != null ? niveau + " " + serie : "—");
                    result.add(row);
                }
            }
        }
        return result;
    }
}
