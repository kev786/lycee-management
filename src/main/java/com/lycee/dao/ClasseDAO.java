package com.lycee.dao;

import com.lycee.model.Classe;
import java.sql.SQLException;
import java.util.List;

public interface ClasseDAO {
    void create(Classe classe) throws SQLException;
    Classe findById(Long id) throws SQLException;
    List<Classe> findAll() throws SQLException;
    List<Classe> findAllPaginated(int page, int pageSize, String search, String niveau, String serie) throws SQLException;
    int countAll(String search, String niveau, String serie) throws SQLException;
    void update(Classe classe) throws SQLException;
    void delete(Long id) throws SQLException;
    java.util.Map<String, Object> getGlobalStats() throws SQLException;
    /** Retourne les salles distinctes pour un niveau (et une série optionnelle) */
    List<String> getSallesByNiveauSerie(String niveau, String serie) throws SQLException;
}
