package com.lycee.model;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.lycee.util.Constants;

public class ParametresEtablissement implements Serializable {

    private String etablissement  = "Lycée de Démonstration";
    private String anneeScolaire  = "2025-2026";
    private String logoFilename;
    private String devise         = "VÉRITÉ - TRAVAIL - SOLIDARITÉ";
    private String ville          = "Yaoundé";
    private String telephone;
    private String email;
    private String siteWeb;
    private String republique     = "RÉPUBLIQUE DU CAMEROUN";
    private String ministere      = "MINISTÈRE DES ENSEIGNEMENTS SECONDAIRES";
    private String delegation     = "DÉLÉGATION RÉGIONALE DU CENTRE";
    private String entetePdf;
    private boolean filigraneLogo;

    public Path resolveLogoPath() {
        if (logoFilename == null || logoFilename.isBlank()) return null;
        Path path = Paths.get(Constants.UPLOAD_DIR_ASSETS, logoFilename).normalize();
        if (!path.startsWith(Paths.get(Constants.UPLOAD_DIR_ASSETS)) || !Files.isRegularFile(path)) {
            return null;
        }
        return path;
    }

    public String getEtablissement() { return etablissement; }
    public void setEtablissement(String etablissement) { this.etablissement = etablissement; }
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    public String getLogoFilename() { return logoFilename; }
    public void setLogoFilename(String logoFilename) { this.logoFilename = logoFilename; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSiteWeb() { return siteWeb; }
    public void setSiteWeb(String siteWeb) { this.siteWeb = siteWeb; }
    public String getRepublique() { return republique; }
    public void setRepublique(String republique) { this.republique = republique; }
    public String getMinistere() { return ministere; }
    public void setMinistere(String ministere) { this.ministere = ministere; }
    public String getDelegation() { return delegation; }
    public void setDelegation(String delegation) { this.delegation = delegation; }
    public String getEntetePdf() { return entetePdf; }
    public void setEntetePdf(String entetePdf) { this.entetePdf = entetePdf; }
    public boolean isFiligraneLogo() { return filigraneLogo; }
    public void setFiligraneLogo(boolean filigraneLogo) { this.filigraneLogo = filigraneLogo; }

    // Alias compatibilité ascendante
    public String getNomEtablissement() { return etablissement; }
    public void setNomEtablissement(String nom) { this.etablissement = nom; }
}
