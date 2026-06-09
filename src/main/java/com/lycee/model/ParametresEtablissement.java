package com.lycee.model;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.lycee.util.Constants;

public class ParametresEtablissement implements Serializable {

    public static final String CLE_NOM           = "nom_etablissement";
    public static final String CLE_ANNEE         = "annee_scolaire";
    public static final String CLE_ENTETE        = "entete_pdf";
    public static final String CLE_DELEGATION    = "delegation";
    public static final String CLE_REPUBLIQUE    = "republique";
    public static final String CLE_MINISTERE     = "ministere";
    public static final String CLE_VILLE        = "ville";
    public static final String CLE_LOGO          = "logo_filename";
    public static final String CLE_FILIGRANE     = "filigrane_logo";

    private String nomEtablissement = "Lycée de Démonstration";
    private String anneeScolaire = "2024-2025";
    private String entetePdf;
    private String delegation = "DÉLÉGATION RÉGIONALE DU CENTRE";
    private String republique = "RÉPUBLIQUE DU CAMEROUN";
    private String ministere = "MINISTÈRE DES ENSEIGNEMENTS SECONDAIRES";
    private String ville = "Yaoundé";
    private String logoFilename;
    private boolean filigraneLogo;

    public static ParametresEtablissement fromMap(Map<String, String> map) {
        ParametresEtablissement p = new ParametresEtablissement();
        if (map == null || map.isEmpty()) return p;
        p.setNomEtablissement(map.getOrDefault(CLE_NOM, p.nomEtablissement));
        p.setAnneeScolaire(map.getOrDefault(CLE_ANNEE, p.anneeScolaire));
        p.setEntetePdf(map.get(CLE_ENTETE));
        p.setDelegation(map.getOrDefault(CLE_DELEGATION, p.delegation));
        p.setRepublique(map.getOrDefault(CLE_REPUBLIQUE, p.republique));
        p.setMinistere(map.getOrDefault(CLE_MINISTERE, p.ministere));
        p.setVille(map.getOrDefault(CLE_VILLE, p.ville));
        p.setLogoFilename(map.get(CLE_LOGO));
        p.setFiligraneLogo("true".equalsIgnoreCase(map.get(CLE_FILIGRANE)));
        return p;
    }

    public Path resolveLogoPath() {
        if (logoFilename == null || logoFilename.isBlank()) return null;
        Path path = Paths.get(Constants.UPLOAD_DIR_ASSETS, logoFilename).normalize();
        if (!path.startsWith(Paths.get(Constants.UPLOAD_DIR_ASSETS)) || !Files.isRegularFile(path)) {
            return null;
        }
        return path;
    }

    public String getNomEtablissement() { return nomEtablissement; }
    public void setNomEtablissement(String nomEtablissement) { this.nomEtablissement = nomEtablissement; }
    public String getAnneeScolaire() { return anneeScolaire; }
    public void setAnneeScolaire(String anneeScolaire) { this.anneeScolaire = anneeScolaire; }
    public String getEntetePdf() { return entetePdf; }
    public void setEntetePdf(String entetePdf) { this.entetePdf = entetePdf; }
    public String getDelegation() { return delegation; }
    public void setDelegation(String delegation) { this.delegation = delegation; }
    public String getRepublique() { return republique; }
    public void setRepublique(String republique) { this.republique = republique; }
    public String getMinistere() { return ministere; }
    public void setMinistere(String ministere) { this.ministere = ministere; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getLogoFilename() { return logoFilename; }
    public void setLogoFilename(String logoFilename) { this.logoFilename = logoFilename; }
    public boolean isFiligraneLogo() { return filigraneLogo; }
    public void setFiligraneLogo(boolean filigraneLogo) { this.filigraneLogo = filigraneLogo; }
}
