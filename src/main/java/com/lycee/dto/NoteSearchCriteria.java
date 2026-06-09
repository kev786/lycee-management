package com.lycee.dto;

public class NoteSearchCriteria {
    private String search;
    private String niveau;
    private String serie;
    private String salle;
    private String matiere;
    private Integer trimestre;

    public NoteSearchCriteria() {}

    public NoteSearchCriteria(String search, String niveau, String serie, String salle, String matiere, Integer trimestre) {
        this.search = search;
        this.niveau = niveau;
        this.serie = serie;
        this.salle = salle;
        this.matiere = matiere;
        this.trimestre = trimestre;
    }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    
    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    
    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }
    
    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }
    
    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }
    
    public Integer getTrimestre() { return trimestre; }
    public void setTrimestre(Integer trimestre) { this.trimestre = trimestre; }
}
