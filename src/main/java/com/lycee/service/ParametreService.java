package com.lycee.service;

import com.lycee.dao.ParametreDAO;
import com.lycee.dao.impl.ParametreDAOImpl;
import com.lycee.model.ParametresEtablissement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ParametreService {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreService.class);
    private final ParametreDAO parametreDAO = new ParametreDAOImpl();

    public ParametresEtablissement charger() {
        try {
            Map<String, String> map = parametreDAO.findAll();
            if (map.isEmpty()) return new ParametresEtablissement();
            return ParametresEtablissement.fromMap(map);
        } catch (SQLException e) {
            LOG.warn("Table parametre inaccessible, utilisation des valeurs par défaut : {}", e.getMessage());
            return new ParametresEtablissement();
        }
    }

    public void enregistrer(ParametresEtablissement p) throws SQLException {
        Map<String, String> map = new HashMap<>();
        map.put(ParametresEtablissement.CLE_NOM, p.getNomEtablissement());
        map.put(ParametresEtablissement.CLE_ANNEE, p.getAnneeScolaire());
        map.put(ParametresEtablissement.CLE_ENTETE, p.getEntetePdf() != null ? p.getEntetePdf() : "");
        map.put(ParametresEtablissement.CLE_DELEGATION, p.getDelegation());
        map.put(ParametresEtablissement.CLE_REPUBLIQUE, p.getRepublique());
        map.put(ParametresEtablissement.CLE_MINISTERE, p.getMinistere());
        map.put(ParametresEtablissement.CLE_VILLE, p.getVille());
        map.put(ParametresEtablissement.CLE_LOGO, p.getLogoFilename() != null ? p.getLogoFilename() : "");
        map.put(ParametresEtablissement.CLE_FILIGRANE, p.isFiligraneLogo() ? "true" : "false");
        parametreDAO.saveAll(map);
    }
}
