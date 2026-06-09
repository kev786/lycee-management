package com.lycee.service;

import com.lycee.dao.ParametreDAO;
import com.lycee.dao.impl.ParametreDAOImpl;
import com.lycee.model.ParametresEtablissement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class ParametreService {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreService.class);
    private final ParametreDAO parametreDAO = new ParametreDAOImpl();

    public ParametresEtablissement charger() {
        try {
            ParametresEtablissement p = parametreDAO.find();
            if (p.getEtablissement() == null || p.getEtablissement().isBlank()) {
                return new ParametresEtablissement();
            }
            return p;
        } catch (SQLException e) {
            LOG.warn("Table parametre inaccessible, utilisation des valeurs par défaut : {}", e.getMessage());
            return new ParametresEtablissement();
        }
    }

    public void enregistrer(ParametresEtablissement p) throws SQLException {
        parametreDAO.save(p);
    }
}
