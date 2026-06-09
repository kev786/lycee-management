package com.lycee.dao;

import com.lycee.model.ParametresEtablissement;
import java.sql.SQLException;

public interface ParametreDAO {
    ParametresEtablissement find() throws SQLException;
    void save(ParametresEtablissement p) throws SQLException;
}
