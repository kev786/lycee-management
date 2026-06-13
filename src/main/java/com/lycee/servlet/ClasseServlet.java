package com.lycee.servlet;

import com.lycee.dao.ClasseDAO;
import com.lycee.dao.impl.ClasseDAOImpl;
import com.lycee.model.Classe;
import com.lycee.util.Constants;
import com.lycee.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasseServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Logger LOG = LoggerFactory.getLogger(ClasseServlet.class);
    private final transient ClasseDAO classeDAO = new ClasseDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null) path = "/";
            path = path.toLowerCase();

            LOG.info("DO_GET ClasseServlet - path='{}'", path);

            if (path.equals("/") || path.isEmpty()) {
                lister(req, resp);
            } else if (path.contains("nouveau")) {
                showForm(req, resp, null);
            } else if (path.contains("modifier")) {
                handleModifier(req, resp, path);
            } else if (path.contains("supprimer")) {
                handleSupprimer(req, resp, path);
            } else {
                LOG.warn("DO_GET Route non reconnue: '{}'", path);
                lister(req, resp); // Par défaut, on liste au lieu de 404
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doGet classes", e);
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


    private void handleModifier(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            String[] parts = path.split("/");
            long id = Long.parseLong(parts[parts.length - 1]);
            showForm(req, resp, classeDAO.findById(id));
        } catch (NumberFormatException | java.sql.SQLException e) {
            throw new ServletException("Erreur lors de la modification de la classe", e);
        }
    }

    private void handleSupprimer(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            String[] parts = path.split("/");
            long id = Long.parseLong(parts[parts.length - 1]);
            classeDAO.delete(id);
            resp.sendRedirect(req.getContextPath() + "/app/classes?msg=" + Constants.MSG_SUPPRIME);
        } catch (NumberFormatException | java.sql.SQLException e) {
            throw new ServletException("Erreur lors de la suppression de la classe", e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null) path = "/";
            path = path.toLowerCase();
            
            LOG.info("DO_POST ClasseServlet - path='{}'", path);

            if (path.contains("nouveau")) {
                LOG.info(">>> APPEL SAUVEGARDER (NOUVEAU)");
                sauvegarder(req, resp, null);
            } else if (path.contains("modifier")) {
                LOG.info(">>> APPEL SAUVEGARDER (MODIFIER)");
                handlePostModifier(req, resp, path);
            } else {
                LOG.warn(">>> AUCUNE ROUTE CORRESPONDANTE POUR: '{}'", path);
                resp.sendError(404, "Chemin non reconnu: " + path);
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doPost classes", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handlePostModifier(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            String[] parts = path.split("/");
            long id = Long.parseLong(parts[parts.length - 1]);
            sauvegarder(req, resp, id);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }


    private void lister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String search  = ValidationUtil.param(req, Constants.PARAM_SEARCH);
            String niveau  = ValidationUtil.param(req, Constants.PARAM_NIVEAU);
            String serie   = ValidationUtil.param(req, Constants.PARAM_SERIE);
            
            int page       = getPage(req);
            int size       = getSize(req);
            int total      = classeDAO.countAll(search, niveau, serie);
            int totalPages = (int) Math.ceil((double) total / size);
            
            req.setAttribute("classes",    classeDAO.findAllPaginated(page, size, search, niveau, serie));
            req.setAttribute("stats",      classeDAO.getGlobalStats());
            req.setAttribute("search",     search);
            req.setAttribute("niveau",     niveau);
            req.setAttribute("serie",      serie);
            req.setAttribute(Constants.PARAM_PAGE,       page);
            req.setAttribute(Constants.PARAM_SIZE,       size);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("total",      total);
            req.getRequestDispatcher("/WEB-INF/vues/classe/liste.jsp").forward(req, resp);
        } catch (java.sql.SQLException e) {
            throw new ServletException("Erreur lors du listing des classes", e);
        }
    }

    private int getPage(HttpServletRequest req) {
        try {
            String p = req.getParameter(Constants.PARAM_PAGE);
            return (p == null) ? 1 : Math.max(1, Integer.parseInt(p));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private int getSize(HttpServletRequest req) {
        try {
            String s = req.getParameter(Constants.PARAM_SIZE);
            return (s == null) ? DEFAULT_PAGE_SIZE : Math.max(5, Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE_SIZE;
        }
    }


    private void showForm(HttpServletRequest req, HttpServletResponse resp, Classe c)
            throws ServletException, IOException {
        req.setAttribute("classe", c);
        req.getRequestDispatcher("/WEB-INF/vues/classe/form.jsp").forward(req, resp);
    }

    private void sauvegarder(HttpServletRequest req, HttpServletResponse resp, Long id)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String niveau = ValidationUtil.param(req, Constants.PARAM_NIVEAU);
        String serie  = ValidationUtil.param(req, Constants.PARAM_SERIE);
        String annee  = ValidationUtil.param(req, Constants.PARAM_ANNEE_SCOLAIRE);

        LOG.info("SAUVEGARDER - niveau: {}, serie: {}, annee: {}", niveau, serie, annee);

        if (ValidationUtil.isEmpty(niveau)) errors.put(Constants.PARAM_NIVEAU, Constants.VAL_OBLIGATOIRE);
        if (ValidationUtil.isEmpty(serie))  errors.put(Constants.PARAM_SERIE,  Constants.VAL_OBLIGATOIRE);
        if (ValidationUtil.isEmpty(annee))  errors.put(Constants.PARAM_ANNEE_SCOLAIRE, Constants.VAL_OBLIGATOIRE);

        if (!errors.isEmpty()) {
            LOG.warn("Validation échouée: {}", errors);
            req.setAttribute("errors", errors);
            req.setAttribute("classe", buildFromReq(req, id));
            req.getRequestDispatcher("/WEB-INF/vues/classe/form.jsp").forward(req, resp);
            return;
        }
        
        LOG.info("Validation réussie, tentative d'enregistrement (id={})", id);

        try {
            Classe c = buildFromReq(req, id);
            LOG.info("DAO CREATE/UPDATE - Objet: {}", c);
            if (id == null) {
                LOG.info("Exécution de classeDAO.create...");
                classeDAO.create(c);
            } else {
                LOG.info("Exécution de classeDAO.update (id={})...", id);
                classeDAO.update(c);
            }
            
            LOG.info("DAO terminé avec succès. Préparation de la redirection...");
            String redirectUrl = req.getContextPath() + "/app/classes?msg=" + (id == null ? Constants.MSG_CREE : Constants.MSG_MODIFIE);
            LOG.info("Tentative de redirection vers: {}", redirectUrl);
            resp.sendRedirect(redirectUrl);
            LOG.info("Redirection envoyée.");
        } catch (Exception e) {
            LOG.error("ERREUR FATALE lors de l'enregistrement", e);
            throw new ServletException("Erreur lors de l'enregistrement de la classe", e);
        }
    }


    private Classe buildFromReq(HttpServletRequest req, Long id) {
        Classe c = new Classe();
        c.setId(id);
        c.setNiveau(ValidationUtil.param(req, Constants.PARAM_NIVEAU));
        c.setSerie(ValidationUtil.param(req, Constants.PARAM_SERIE));
        c.setProfPrincipal(ValidationUtil.param(req, "profPrincipal"));
        c.setSallePrincipale(ValidationUtil.param(req, "sallePrincipale"));
        c.setAnneeScolaire(ValidationUtil.param(req, Constants.PARAM_ANNEE_SCOLAIRE));
        try {
            String effectif = ValidationUtil.param(req, "effectifMax");
            c.setEffectifMax(effectif == null ? 60 : Integer.parseInt(effectif));
        } catch (NumberFormatException ignored) {
            c.setEffectifMax(60);
        }
        return c;
    }

}
