package com.lycee.filter;

import java.io.IOException;
import java.util.Set;

import com.lycee.util.Constants;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Filtre d'authentification et d'autorisation par rôle.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/app/*"})
public class AuthFilter implements Filter {

    /** Réservé à l'Admin */
    private static final Set<String> ADMIN_PATHS = Set.of(
        "/app/utilisateurs",
        "/app/parametres",
        "/app/assets"
    );

    /** Admin et Censeur uniquement (bloqué pour Surveillant) */
    private static final Set<String> CENSEUR_PATHS = Set.of(
        "/app/notes",
        "/app/pdf",
        "/app/documents",
        "/app/eleves/export-csv"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI().substring(req.getContextPath().length());

        if (session == null || session.getAttribute("utilisateur") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");

        if (Constants.ROLE_ADMIN.equals(role)) {
            chain.doFilter(request, response);
            return;
        }

        if (matchesAny(uri, ADMIN_PATHS)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (Constants.ROLE_CENSEUR.equals(role)) {
            chain.doFilter(request, response);
            return;
        }

        if (matchesAny(uri, CENSEUR_PATHS)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean matchesAny(String uri, Set<String> paths) {
        for (String path : paths) {
            if (uri.equals(path) || uri.startsWith(path + "/")) {
                return true;
            }
        }
        return false;
    }
}
