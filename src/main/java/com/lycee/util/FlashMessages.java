package com.lycee.util;

import java.util.Map;

public final class FlashMessages {

    private static final Map<String, String> MESSAGES = Map.of(
        "cree", "Enregistrement créé avec succès.",
        "modifie", "Modification enregistrée avec succès.",
        "supprime", "Suppression effectuée avec succès.",
        "enregistre", "Paramètres enregistrés avec succès.",
        "access_refuse", "Accès refusé : votre rôle ne permet pas cette action.",
        "self_delete", "Vous ne pouvez pas supprimer votre propre compte.",
        "bulletins_ok", "Bulletins de classe générés avec succès."
    );

    private FlashMessages() {}

    public static String resolve(String code) {
        if (code == null || code.isBlank()) return null;
        return MESSAGES.getOrDefault(code, null);
    }

    public static String type(String code) {
        if ("access_refuse".equals(code) || "self_delete".equals(code)) return "error";
        if ("supprime".equals(code)) return "warning";
        return "success";
    }
}
