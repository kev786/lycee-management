package com.lycee.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Eleve implements Serializable {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Long classeId;
    private Classe classe;
    private String nomParent;
    private String telParent;
    private String emailParent;
    private String photoFilename;
    private String sexe;

    

    public Eleve() {
        // Constructeur par défaut requis pour la sérialisation
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return this.matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Long getClasseId() {
        return this.classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }

    public Classe getClasse() {
        return this.classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public String getNomParent() {
        return this.nomParent;
    }

    public void setNomParent(String nomParent) {
        this.nomParent = nomParent;
    }

    public String getTelParent() {
        return this.telParent;
    }

    public void setTelParent(String telParent) {
        this.telParent = telParent;
    }

    public String getEmailParent() {
        return this.emailParent;
    }

    public void setEmailParent(String emailParent) {
        this.emailParent = emailParent;
    }

    public String getPhotoFilename() {
        return this.photoFilename;
    }

    public void setPhotoFilename(String photoFilename) {
        this.photoFilename = photoFilename;
    }

    public String getSexe() {
        return this.sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
    
    public String getNomComplet(){
        return prenom + " " + nom;
    }

    public String getLabelComplet(){
        return prenom + " " + nom + " (" + (classe != null ? classe.getLibelle() : "Sans classe") + ")";
    }

}
