package com.lycee.model;

import java.io.Serializable;

public class Classe implements Serializable {
    private Long id;
    private String niveau;
    private String serie;
    private int effectifMax;
    private String profPrincipal;
    private String sallePrincipale;
    private String anneeScolaire;
    private int effectifActuel;

     public Classe() {
        // Constructeur par défaut requis pour la sérialisation
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNiveau() {
        return this.niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getSerie() {
        return this.serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getEffectifMax() {
        return this.effectifMax;
    }

    public void setEffectifMax(int effectifMax) {
        this.effectifMax = effectifMax;
    }

    public String getProfPrincipal() {
        return this.profPrincipal;
    }

    public void setProfPrincipal(String profPrincipal) {
        this.profPrincipal = profPrincipal;
    }

    public String getSallePrincipale() {
        return this.sallePrincipale;
    }

    public void setSallePrincipale(String sallePrincipale) {
        this.sallePrincipale = sallePrincipale;
    }

    public String getAnneeScolaire() {
        return this.anneeScolaire;
    }

    public void setAnneeScolaire(String anneeScolaire) {
        this.anneeScolaire = anneeScolaire;
    }

    public int getEffectifActuel() {
        return effectifActuel;
    }

    public void setEffectifActuel(int effectifActuel) {
        this.effectifActuel = effectifActuel;
    }

    public String getLibelle(){
        return niveau + " " + serie + " (" + anneeScolaire + ")";
    }

}
