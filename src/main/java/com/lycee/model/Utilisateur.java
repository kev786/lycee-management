package com.lycee.model;

import java.io.Serializable;

public class Utilisateur implements Serializable {
    private Long id;
    private String login;
    private String passwordHache;
    private String role;


    public Utilisateur() {
        // Constructeur par défaut requis pour la sérialisation
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHache() {
        return this.passwordHache;
    }

    public void setPasswordHache(String passwordHache) {
        this.passwordHache = passwordHache;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
