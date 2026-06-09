package com.lycee.servlet;

import com.lycee.dao.UtilisateurDAO;
import com.lycee.dao.impl.UtilisateurDAOImpl;
import com.lycee.model.Utilisateur;
import com.lycee.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/logout", "/mot-de-passe-oublie"})
public class LoginServlet extends HttpServlet {

    private static final String VUE_LOGIN = "/WEB-INF/vues/login.jsp";
    private static final String VUE_OUBLI = "/WEB-INF/vues/motDePasseOublie.jsp";
    private static final String ATTR_ERREUR = "erreur";

    private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);
    private final transient UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String uri = req.getRequestURI();
            if (uri.endsWith("/logout")) {
                HttpSession session = req.getSession(false);
                if (session != null) session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            if (uri.endsWith("/mot-de-passe-oublie")) {
                req.getRequestDispatcher(VUE_OUBLI).forward(req, resp);
                return;
            }

            // Déjà connecté ?
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("utilisateur") != null) {
                resp.sendRedirect(req.getContextPath() + "/app/dashboard");
                return;
            }

            req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
        } catch (Exception e) {
            LOG.error("Erreur dans doGet login", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorSafe(HttpServletResponse resp, int code) {
        try {
            resp.sendError(code);
        } catch (IOException e) {
            LOG.error("Impossible d'envoyer l'erreur {}", code, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String uri = req.getRequestURI();

            if (uri.endsWith("/mot-de-passe-oublie")) {
                handleMotDePasseOublie(req, resp);
                return;
            }

            handleLogin(req, resp);
        } catch (Exception e) {
            LOG.error("Erreur critique dans doPost login", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleMotDePasseOublie(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String login = req.getParameter("login");
        if (login == null || login.isBlank()) {
            req.setAttribute(ATTR_ERREUR, "Veuillez saisir votre login.");
            req.getRequestDispatcher(VUE_OUBLI).forward(req, resp);
            return;
        }
        try {
            Utilisateur u = utilisateurDAO.findByLogin(login.trim());
            if (u != null) {
                req.setAttribute("succes", "Votre demande a été prise en compte. Contactez l'administrateur pour réinitialiser votre mot de passe.");
            } else {
                req.setAttribute(ATTR_ERREUR, "Aucun compte trouvé avec ce login.");
            }
        } catch (java.sql.SQLException e) {
            LOG.error("Erreur SQL lors de l'oubli de mot de passe", e);
            req.setAttribute(ATTR_ERREUR, "Erreur serveur. Veuillez réessayer plus tard.");
        }
        req.getRequestDispatcher(VUE_OUBLI).forward(req, resp);
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String login    = req.getParameter("login");
        String motPasse = req.getParameter("motPasse");
        String roleForm = req.getParameter("role");

        if (login == null || motPasse == null || login.isBlank() || motPasse.isBlank()) {
            req.setAttribute(ATTR_ERREUR, "Veuillez remplir tous les champs.");
            req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
            return;
        }

        try {
            Utilisateur u = utilisateurDAO.findByLogin(login.trim());
            String roleAttendu = mapRoleFormulaire(roleForm);
            if (roleAttendu == null) {
                req.setAttribute(ATTR_ERREUR, "Rôle invalide.");
                req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
                return;
            }
            if (u != null && !roleAttendu.equals(u.getRole())) {
                req.setAttribute(ATTR_ERREUR, "Le rôle sélectionné ne correspond pas à votre compte.");
                req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
                return;
            }
            if (u != null && org.mindrot.jbcrypt.BCrypt.checkpw(motPasse, u.getPasswordHache())) {
                HttpSession session = req.getSession(true);
                session.setAttribute("utilisateur", u);
                session.setAttribute("role", u.getRole());
                session.setAttribute("loginNom", u.getLogin());
                session.setMaxInactiveInterval(30 * 60); // 30 min
                resp.sendRedirect(req.getContextPath() + "/app/dashboard");
            } else {
                req.setAttribute(ATTR_ERREUR, "Identifiants incorrects.");
                req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
            }
        } catch (java.sql.SQLException e) {
            LOG.error("Erreur SQL lors du login", e);
            req.setAttribute(ATTR_ERREUR, "Erreur serveur. Veuillez réessayer plus tard.");
            req.getRequestDispatcher(VUE_LOGIN).forward(req, resp);
        }
    }

    private String mapRoleFormulaire(String roleForm) {
        if (roleForm == null || roleForm.isBlank()) return null;
        return switch (roleForm.toLowerCase()) {
            case "admin" -> Constants.ROLE_ADMIN;
            case "censeur" -> Constants.ROLE_CENSEUR;
            case "surveillant" -> Constants.ROLE_SURVEILLANT;
            default -> null;
        };
    }
}
