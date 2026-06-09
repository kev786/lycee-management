package com.lycee.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Absence implements Serializable {
    private Long id;
    private Long eleveId;
    private Eleve eleve;
    private LocalDate dateAbsence;
    private int dureeHeures;
    private String matiere;
    private boolean justifiee;
    private String motif;
    private String eleveNom;
    private String elevePrenom;
    private String classeLibelle;

    public Absence() {
        // Constructeur par défaut requis pour la sérialisation
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEleveId() {
        return this.eleveId;
    }

    public void setEleveId(Long eleveId) {
        this.eleveId = eleveId;
    }

    public Eleve getEleve() {
        return this.eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    public LocalDate getDateAbsence() {
        return this.dateAbsence;
    }

    public void setDateAbsence(LocalDate dateAbsence) {
        this.dateAbsence = dateAbsence;
    }

    public int getDureeHeures() {
        return this.dureeHeures;
    }

    public void setDureeHeures(int dureeHeures) {
        this.dureeHeures = dureeHeures;
    }

    public String getMatiere() {
        return this.matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public boolean isJustifiee() {
        return this.justifiee;
    }

    public boolean getJustifiee() {
        return this.justifiee;
    }

    public void setJustifiee(boolean justifiee) {
        this.justifiee = justifiee;
    }

    public String getMotif() {
        return this.motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getEleveNom() { return eleveNom; }
    public void setEleveNom(String eleveNom) { this.eleveNom = eleveNom; }
    public String getElevePrenom() { return elevePrenom; }
    public void setElevePrenom(String elevePrenom) { this.elevePrenom = elevePrenom; }
    public String getClasseLibelle() { return classeLibelle; }
    public void setClasseLibelle(String classeLibelle) { this.classeLibelle = classeLibelle; }

    public String getInitiales() {
        String n = eleveNom != null && !eleveNom.isEmpty() ? eleveNom.substring(0, 1) : "?";
        String p = elevePrenom != null && !elevePrenom.isEmpty() ? elevePrenom.substring(0, 1) : "?";
        return n + p;
    }
}