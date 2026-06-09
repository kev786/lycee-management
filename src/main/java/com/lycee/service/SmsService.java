package com.lycee.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service SMS via Twilio API (remplace SMSLib, incompatible avec Java 25 / Jakarta EE 11).
 * Même interface métier que le cahier des charges : bulletin trimestriel et alerte absences.
 * Configuration : TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_PHONE_FROM.
 * Sans credentials, les messages sont simulés dans les logs.
 */
public class SmsService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsService.class);
    
    // À configurer : identifiants Twilio
    private static final String TWILIO_ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String TWILIO_AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    private static final String TWILIO_PHONE_FROM = System.getenv("TWILIO_PHONE_FROM"); // Ex: +1234567890
    
    static {
        // Initialiser Twilio si les credentials sont disponibles
        if (TWILIO_ACCOUNT_SID != null && TWILIO_AUTH_TOKEN != null) {
            Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
            LOG.info("Twilio SDK initialisé avec succès");
        } else {
            LOG.warn("Variables TWILIO_ACCOUNT_SID ou TWILIO_AUTH_TOKEN non configurées. SMS seront loggués uniquement.");
        }
    }

    /**
     * Envoie le SMS de bulletin trimestriel.
     * Texte : "Bulletin du T[x] de [prenom] disponible. moy : [note]/20. rang : [rang]/[effectif]"
     */
    public void envoyerSmsBulletin(String telephone, String prenom,
                                    int trimestre, String moyenne, int rang, int effectif) {
        String message = String.format(
            "Bulletin du T%d de %s disponible. moy : %s/20. rang : %d/%d",
            trimestre, prenom, moyenne, rang, effectif);
        envoyerSms(telephone, message);
    }

    /**
     * Envoie une alerte SMS d'absences injustifiées.
     * Texte : "[prenom] a accumulé [N] absences injustifiées ce trimestre"
     */
    public void envoyerSmsAlerte(String telephone, String prenom, int nbAbsences) {
        String message = String.format(
            "%s a accumulé %d absences injustifiées ce trimestre", prenom, nbAbsences);
        envoyerSms(telephone, message);
    }

    /**
     * Méthode centrale d'envoi SMS via Twilio.
     * 
     * Configuration Twilio requise :
     * - TWILIO_ACCOUNT_SID : Identifiant du compte Twilio
     * - TWILIO_AUTH_TOKEN : Jeton d'authentification Twilio
     * - TWILIO_PHONE_FROM : Numéro de téléphone Twilio (ex: +1234567890)
     * 
     * Pour les tests sans API Twilio, le message est simplement loggué.
     */
    private void envoyerSms(String telephone, String texte) {
        if (telephone == null || telephone.isBlank()) {
            LOG.warn("Numéro de téléphone manquant, SMS non envoyé.");
            return;
        }
        
        if (texte == null || texte.isBlank()) {
            LOG.warn("Texte du SMS vide, SMS non envoyé.");
            return;
        }

        // Si les credentials Twilio ne sont pas configurés, simuler l'envoi
        if (TWILIO_ACCOUNT_SID == null || TWILIO_AUTH_TOKEN == null || TWILIO_PHONE_FROM == null) {
            LOG.info("[SMS SIMULATION] À : {} | Texte : {}", telephone, texte);
            LOG.warn("Twilio non configuré. Pour envoyer réellement, définissez les variables d'environnement :");
            LOG.warn("  export TWILIO_ACCOUNT_SID=votre_sid");
            LOG.warn("  export TWILIO_AUTH_TOKEN=votre_token");
            LOG.warn("  export TWILIO_PHONE_FROM=+1234567890");
            return;
        }

        try {
            // Envoyer via Twilio API
            Message message = Message.creator(
                    new PhoneNumber(telephone),      // To number
                    new PhoneNumber(TWILIO_PHONE_FROM), // From number (Twilio)
                    texte)                             // SMS body
                .create();
            
            LOG.info("SMS envoyé avec succès via Twilio. SID: {}", message.getSid());
        } catch (Exception e) {
            LOG.error("Erreur lors de l'envoi du SMS via Twilio : {}", e.getMessage(), e);
        }
    }
}
