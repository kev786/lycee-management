package com.lycee.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public final class AuthUtil {

    private AuthUtil() {}

    public static boolean isAdmin(String role) {
        return Constants.ROLE_ADMIN.equals(role);
    }

    public static boolean isCenseur(String role) {
        return Constants.ROLE_CENSEUR.equals(role);
    }

    public static boolean isProfesseur(String role) {
        return Constants.ROLE_PROFESSEUR.equals(role);
    }

    public static boolean isAdminOrCenseur(String role) {
        return isAdmin(role) || isCenseur(role);
    }

    public static boolean isAdminOrCenseurOrProfesseur(String role) {
        return isAdmin(role) || isCenseur(role) || isProfesseur(role);
    }

    public static String getRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null ? (String) session.getAttribute("role") : null;
    }

    /** Refuse l'accès aux fonctionnalités réservées Admin/Censeur (PDF, notes, export…). */
    public static boolean denyUnlessAdminOrCenseur(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isAdminOrCenseur(getRole(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return true;
        }
        return false;
    }

    public static void denyUnlessAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(getRole(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
