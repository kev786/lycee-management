package com.lycee.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class NoteEleve implements Serializable {
    private Long id;
    private Long eleveId;
    private Eleve eleve;
    private String matiere;
    private int coefficient;
    private BigDecimal notesValeur;
    private int trimestre;
    private String profSaisie;


    public NoteEleve() {
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

    public String getMatiere() {
        return this.matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public int getCoefficient() {
        return this.coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public BigDecimal getNotesValeur() {
        return this.notesValeur;
    }

    public void setNotesValeur(BigDecimal notesValeur) {
        this.notesValeur = notesValeur;
    }

    public int getTrimestre() {
        return this.trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    public String getProfSaisie() {
        return this.profSaisie;
    }

    public void setProfSaisie(String profSaisie) {
        this.profSaisie = profSaisie;
    }

}
