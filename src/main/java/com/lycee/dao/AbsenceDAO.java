package com.lycee.dao;

import com.lycee.model.Absence;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AbsenceDAO {
    void create(Absence absence) throws SQLException;
    Absence findById(Long id) throws SQLException;
    List<Absence> findByEleve(Long eleveId) throws SQLException;
    List<Absence> findAllPaginated(int page, int pageSize, String search, Long classeId, String serie, String niveau, String salle) throws SQLException;
    int countAll(String search, Long classeId, String serie, String niveau, String salle) throws SQLException;
    void update(Absence absence) throws SQLException;
    void delete(Long id) throws SQLException;

    /** Total heures d'absences injustifiées par élève et trimestre */
    int countAbsencesInjustifieesParEleve(Long eleveId, int trimestre) throws SQLException;

    /** Total heures d'absences injustifiées par élève (tous trimestres) */
    int countAbsencesInjustifieesAnnuel(Long eleveId) throws SQLException;

    /** Total heures d'absences injustifiées par élève (tous trimestres) */
    int sumHeuresInjustifieesAnnuel(Long eleveId) throws SQLException;

    /** Taux d'absentéisme par classe */
    List<Map<String, Object>> getTauxAbsenteismeParClasse() throws SQLException;

    /** Total des heures d'absences injustifiées dans tout le lycée */
    int getTotalHeuresInjustifieesGlobale() throws SQLException;

    /** Récupère les absences groupées par matière pour un élève */
    List<Map<String, Object>> getAbsencesParMatiere(Long eleveId) throws SQLException;

    int countInjustifieesTrimestre(int trimestre) throws SQLException;
    int countJustifieesTrimestre(int trimestre) throws SQLException;
    int countTotalMoisCourant() throws SQLException;
    int countElevesSeuilInjustifie(int seuil, int trimestre) throws SQLException;
    int sumHeuresInjustifieesParEleve(Long eleveId, int trimestre) throws SQLException;

    /** Heures d'absences par mois (N derniers mois) */
    List<Map<String, Object>> getHeuresParMois(int nbMois) throws SQLException;

    /** Taux d'absentéisme global (heures / effectif) */
    double getTauxAbsenteeismeGlobal() throws SQLException;
}
