package com.lycee.dao;

import com.lycee.model.NoteEleve;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface NoteDAO {
    void create(NoteEleve note) throws SQLException;
    NoteEleve findById(Long id) throws SQLException;
    List<NoteEleve> findByEleve(Long eleveId) throws SQLException;
    List<NoteEleve> findByEleveAndTrimestre(Long eleveId, int trimestre) throws SQLException;
    List<NoteEleve> findAllPaginated(int page, int pageSize, com.lycee.dto.NoteSearchCriteria criteria) throws SQLException;
    int countAll(com.lycee.dto.NoteSearchCriteria criteria) throws SQLException;
    void update(NoteEleve note) throws SQLException;
    void delete(Long id) throws SQLException;
    void saveBulk(List<NoteEleve> notes) throws SQLException;

    /** Moyenne pondérée d'un élève pour un trimestre */
    BigDecimal getMoyenneEleve(Long eleveId, int trimestre) throws SQLException;

    /** Moyenne pondérée de la classe pour un trimestre */
    BigDecimal getMoyenneClasse(Long classeId, int trimestre) throws SQLException;

    /** Rang d'un élève dans sa classe pour un trimestre */
    int getRangEleve(Long eleveId, Long classeId, int trimestre) throws SQLException;

    /** Moyenne par classe et trimestre */
    List<Map<String, Object>> getMoyennesParClasse(int trimestre) throws SQLException;

    /** Matière avec le plus fort taux d'échec */
    List<Map<String, Object>> getTauxEchecParMatiere(int trimestre) throws SQLException;

    /** Top 10 élèves par classe et trimestre */
    List<Map<String, Object>> getTop10ParClasse(Long classeId, int trimestre) throws SQLException;

    /** Moyenne générale de tous les élèves */
    double getMoyenneGlobale() throws SQLException;

    /** Dernières notes saisies (pour le tableau de bord) */
    List<Map<String, Object>> findRecentes(int limit) throws SQLException;
}
