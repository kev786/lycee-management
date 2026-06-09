package com.lycee.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitaire de connexion JDBC.
 * Les informations de connexion sont chargées depuis db.properties.
 */
public class DBConnection {
    
    private static final Logger LOG = LoggerFactory.getLogger(DBConnection.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (input == null) {
                LOG.error("Fichier db.properties introuvable dans le classpath");
            } else {
                props.load(input);
            }
        } catch (Exception e) {
            LOG.error("Erreur d'initialisation de la connexion DB", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        String url  = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");
        
        if (url == null || user == null) {
            throw new SQLException("Configuration de base de données incomplète dans db.properties");
        }
        
        return DriverManager.getConnection(url, user, pass);
    }
}
