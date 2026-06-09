package com.lycee.dao;

import com.lycee.model.Eleve;
import java.sql.SQLException;
import java.util.List;

public interface EleveDAO {
    void create(Eleve eleve) throws SQLException;
    Eleve findById(Long id) throws SQLException;
    Eleve findByMatricule(String matricule) throws SQLException;
    List<Eleve> findAll() throws SQLException;
    List<Eleve> findAllPaginated(int page, int pageSize, com.lycee.dto.EleveSearchCriteria criteria) throws SQLException;
    List<Eleve> findByClasse(Long classeId) throws SQLException;
    int countAll(com.lycee.dto.EleveSearchCriteria criteria) throws SQLException;
    void update(Eleve eleve) throws SQLException;
    void delete(Long id) throws SQLException;
    boolean existsMatricule(String matricule, Long excludeId) throws SQLException;
}
