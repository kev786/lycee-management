package com.lycee.servlet;

import com.lycee.dao.UtilisateurDAO;
import com.lycee.dao.impl.UtilisateurDAOImpl;
import com.lycee.model.Utilisateur;
import com.lycee.util.AuthUtil;
import com.lycee.util.Constants;
import com.lycee.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/app/utilisateurs/*")
public class UtilisateurServlet extends HttpServlet {

    private static final String FORM_JSP = "/WEB-INF/vues/utilisateur/form.jsp";
    private static final String LIST_JSP = "/WEB-INF/vues/utilisateur/liste.jsp";
    private static final Logger LOG = LoggerFactory.getLogger(UtilisateurServlet.class);

    private final transient UtilisateurDAO utilisateurDAO = new UtilisateurDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isAdmin(AuthUtil.getRole(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                lister(req, resp);
            } else if (Constants.ROUTE_NOUVEAU.equals(path)) {
                showForm(req, resp, null);
            } else if (path.startsWith(Constants.ROUTE_MODIFIER)) {
                long id = Long.parseLong(path.substring(Constants.ROUTE_MODIFIER.length()));
                showForm(req, resp, utilisateurDAO.findById(id));
            } else if (path.startsWith(Constants.ROUTE_SUPPRIMER)) {
                long id = Long.parseLong(path.substring(Constants.ROUTE_SUPPRIMER.length()));
                supprimer(req, resp, id);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur doGet utilisateurs", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.isAdmin(AuthUtil.getRole(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            String path = req.getPathInfo();
            if (path == null) path = "/";
            if (path.contains("nouveau")) {
                sauvegarder(req, resp, null);
            } else if (path.contains("modifier")) {
                String[] parts = path.split("/");
                long id = Long.parseLong(parts[parts.length - 1]);
                sauvegarder(req, resp, id);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur doPost utilisateurs", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void lister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, java.sql.SQLException {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        req.setAttribute("utilisateurs", utilisateurs);
        req.getRequestDispatcher(LIST_JSP).forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Utilisateur u)
            throws ServletException, IOException {
        req.setAttribute("utilisateur", u);
        req.getRequestDispatcher(FORM_JSP).forward(req, resp);
    }

    private void supprimer(HttpServletRequest req, HttpServletResponse resp, long id)
            throws IOException, java.sql.SQLException {
        HttpSession session = req.getSession(false);
        Utilisateur current = session != null ? (Utilisateur) session.getAttribute("utilisateur") : null;
        if (current != null && current.getId() != null && current.getId() == id) {
            resp.sendRedirect(req.getContextPath() + "/app/utilisateurs?error=self_delete");
            return;
        }
        utilisateurDAO.delete(id);
        resp.sendRedirect(req.getContextPath() + "/app/utilisateurs?msg=" + Constants.MSG_SUPPRIME);
    }

    private void sauvegarder(HttpServletRequest req, HttpServletResponse resp, Long id)
            throws ServletException, IOException, java.sql.SQLException {

        String login = ValidationUtil.param(req, "login");
        String motPasse = ValidationUtil.param(req, "motPasse");
        String role = ValidationUtil.param(req, "role");

        Map<String, String> errors = new HashMap<>();
        if (ValidationUtil.isEmpty(login)) errors.put("login", "Login obligatoire");
        if (ValidationUtil.isEmpty(role)) errors.put("role", "Rôle obligatoire");
        if (id == null && ValidationUtil.isEmpty(motPasse)) errors.put("motPasse", "Mot de passe obligatoire");
        if (!ValidationUtil.isEmpty(login) && utilisateurDAO.existsLogin(login.trim(), id)) {
            errors.put("login", "Ce login est déjà utilisé");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            Utilisateur u = new Utilisateur();
            u.setId(id);
            u.setLogin(login);
            u.setRole(role);
            req.setAttribute("utilisateur", u);
            req.getRequestDispatcher(FORM_JSP).forward(req, resp);
            return;
        }

        Utilisateur u = id != null ? utilisateurDAO.findById(id) : new Utilisateur();
        if (u == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        u.setLogin(login.trim());
        u.setRole(role);
        if (!ValidationUtil.isEmpty(motPasse)) {
            u.setPasswordHache(BCrypt.hashpw(motPasse, BCrypt.gensalt(12)));
        } else if (id == null) {
            u.setPasswordHache(BCrypt.hashpw(motPasse, BCrypt.gensalt(12)));
        }

        if (id == null) utilisateurDAO.create(u);
        else utilisateurDAO.update(u);

        resp.sendRedirect(req.getContextPath() + "/app/utilisateurs?msg=" + (id == null ? Constants.MSG_CREE : Constants.MSG_MODIFIE));
    }
}
