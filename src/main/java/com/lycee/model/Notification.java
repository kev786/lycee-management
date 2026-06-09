package com.lycee.model;

import java.time.LocalDateTime;

public class Notification {

    private Long id;
    private String roleCible;
    private String message;
    private String lien;
    private String type;
    private boolean lue;
    private LocalDateTime dateCreation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoleCible() { return roleCible; }
    public void setRoleCible(String roleCible) { this.roleCible = roleCible; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isLue() { return lue; }
    public void setLue(boolean lue) { this.lue = lue; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
