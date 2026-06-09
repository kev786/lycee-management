package com.lycee.util;

public final class Constants {
    
    private Constants() {}

    // Routes
    public static final String ROUTE_MODIFIER = "/modifier/";
    public static final String ROUTE_SUPPRIMER = "/supprimer/";
    public static final String ROUTE_NOUVEAU  = "/nouveau";
    public static final String ROUTE_SAISIE_MASSE = "/saisie-masse";

    // Parameters
    public static final String PARAM_ID       = "id";
    public static final String PARAM_PAGE     = "page";
    public static final String PARAM_SIZE     = "size";
    public static final String PARAM_SEARCH   = "q";
    public static final String PARAM_MSG      = "msg";

    // Common literals
    public static final String MSG_CREE       = "cree";
    public static final String MSG_MODIFIE    = "modifie";
    public static final String MSG_SUPPRIME   = "supprime";

    // Common Parameters
    public static final String PARAM_NIVEAU         = "niveau";
    public static final String PARAM_SERIE          = "serie";
    public static final String PARAM_ANNEE_SCOLAIRE = "anneeScolaire";
    public static final String PARAM_MATRICULE      = "matricule";
    public static final String PARAM_PRENOM         = "prenom";
    public static final String PARAM_CLASSE_ID      = "classeId";
    public static final String PARAM_TEL_PARENT     = "telParent";
    public static final String PARAM_EMAIL_PARENT    = "emailParent";
    public static final String PARAM_PHOTO          = "photo";
    public static final String PARAM_ELEVE_ID       = "eleveId";
    public static final String PARAM_MATIERE        = "matiere";
    public static final String PARAM_DATE_ABSENCE   = "dateAbsence";
    public static final String PARAM_TRIMESTRE      = "trimestre";


    // Validation messages
    public static final String VAL_OBLIGATOIRE      = "Obligatoire";

    // Stockage fichiers (hors webapp)
    public static final String UPLOAD_DIR_PHOTOS    = "/opt/lycee/photos";
    public static final String UPLOAD_DIR_ASSETS    = "/opt/lycee/assets";
    public static final String PARAM_LOGO           = "logo";

    // Rôles
    public static final String ROLE_ADMIN           = "Admin";
    public static final String ROLE_CENSEUR         = "Censeur";
    public static final String ROLE_SURVEILLANT     = "Surveillant";
}
