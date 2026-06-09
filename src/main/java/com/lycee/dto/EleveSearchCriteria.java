package com.lycee.dto;

/**
 * Objet de regroupement des critères de recherche pour les élèves.
 * Permet de réduire le nombre de paramètres dans les méthodes du DAO.
 */
public class EleveSearchCriteria {
    private String search;
    private Long classeId;
    private String serie;
    private String niveau;
    private String sexe;
    private String salle;

    public EleveSearchCriteria() {}

    public EleveSearchCriteria(String search, Long classeId, String serie, String niveau, String sexe, String salle) {
        this.search = search;
        this.classeId = classeId;
        this.serie = serie;
        this.niveau = niveau;
        this.sexe = sexe;
        this.salle = salle;
    }

    // Getters and Setters
    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public Long getClasseId() { return classeId; }
    public void setClasseId(Long classeId) { this.classeId = classeId; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
}
