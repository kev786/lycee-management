package com.lycee.servlet;

import com.lycee.dao.*;
import com.lycee.dao.impl.*;
import com.lycee.model.*;
import com.lycee.util.Constants;
import com.lycee.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoteServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 15;
    private static final String ATTR_ELEVES = "eleves";
    private static final String PARAM_SALLE = "salle";
    private static final Logger LOG = LoggerFactory.getLogger(NoteServlet.class);


    private final transient NoteDAO    noteDAO   = new NoteDAOImpl();
    private final transient EleveDAO   eleveDAO  = new EleveDAOImpl();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                lister(req, resp);
            } else if (Constants.ROUTE_NOUVEAU.equals(path)) {
                showSaisieMasseForm(req, resp);
            } else if (path.startsWith(Constants.ROUTE_MODIFIER)) {
                handleModifier(req, resp, path);
            } else if (path.startsWith(Constants.ROUTE_SUPPRIMER)) {
                handleSupprimer(req, resp, path);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doGet notes", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleModifier(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            long id = Long.parseLong(path.substring(Constants.ROUTE_MODIFIER.length()));
            showForm(req, resp, noteDAO.findById(id));
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Erreur lors de la modification de la note", e);
        }
    }

    private void handleSupprimer(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            long id = Long.parseLong(path.substring(Constants.ROUTE_SUPPRIMER.length()));
            noteDAO.delete(id);
            resp.sendRedirect(req.getContextPath() + "/app/notes?msg=" + Constants.MSG_SUPPRIME);
        } catch (NumberFormatException | SQLException e) {
            throw new ServletException("Erreur lors de la suppression de la note", e);
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
            String path = req.getPathInfo();
            if (Constants.ROUTE_NOUVEAU.equals(path)) {
                sauvegarderMasse(req, resp);
            } else if (path != null && path.startsWith(Constants.ROUTE_MODIFIER)) {
                handlePostModifier(req, resp, path);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doPost notes", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handlePostModifier(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            long id = Long.parseLong(path.substring(Constants.ROUTE_MODIFIER.length()));
            sauvegarder(req, resp, id);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }



    private void lister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String search = ValidationUtil.param(req, Constants.PARAM_SEARCH);
            String niveau = req.getParameter("niveau");
            String serie  = req.getParameter("serie");
            String salle  = req.getParameter(PARAM_SALLE);
            String matiere = req.getParameter("matiere");
            String trimStr = req.getParameter("trimestre");
            Integer trimestre = trimStr != null && !trimStr.isBlank() ? Integer.parseInt(trimStr) : null;

            com.lycee.dto.NoteSearchCriteria criteria = new com.lycee.dto.NoteSearchCriteria(search, niveau, serie, salle, matiere, trimestre);

            int page = getPage(req);
            int size = getSize(req);
            int total  = noteDAO.countAll(criteria);
            int pages  = (int) Math.ceil((double) total / size);
            
            req.setAttribute("notes",      noteDAO.findAllPaginated(page, size, criteria));
            req.setAttribute("search",     search);
            req.setAttribute("niveau",     niveau);
            req.setAttribute("serie",      serie);
            req.setAttribute(PARAM_SALLE,      salle);
            req.setAttribute("matiere",    matiere);
            req.setAttribute("trimestre",  trimestre);
            req.setAttribute(Constants.PARAM_PAGE,       page);
            req.setAttribute(Constants.PARAM_SIZE,       size);
            req.setAttribute("totalPages", pages);
            req.setAttribute("total",      total);
            req.getRequestDispatcher("/WEB-INF/vues/note/liste.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Erreur lors du listing des notes", e);
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


    private void showForm(HttpServletRequest req, HttpServletResponse resp, NoteEleve note)
            throws ServletException, IOException {
        try {
            req.setAttribute("note",   note);
            req.setAttribute(ATTR_ELEVES, eleveDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/vues/note/form.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException(e); }

    }

    private void sauvegarder(HttpServletRequest req, HttpServletResponse resp, Long id)
            throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();

        String eleveIdStr   = ValidationUtil.param(req, Constants.PARAM_ELEVE_ID);
        String matiere      = ValidationUtil.param(req, Constants.PARAM_MATIERE);
        String coefStr      = ValidationUtil.param(req, "coefficient");
        String noteStr      = ValidationUtil.param(req, "notesValeur");
        String trimStr      = ValidationUtil.param(req, Constants.PARAM_TRIMESTRE);
        String prof         = ValidationUtil.param(req, "profSaisie");

        if (ValidationUtil.isEmpty(eleveIdStr)) errors.put(Constants.PARAM_ELEVE_ID, "Élève obligatoire");
        if (ValidationUtil.isEmpty(matiere))    errors.put(Constants.PARAM_MATIERE, "Matière obligatoire");
        if (!ValidationUtil.isValidNote(noteStr)) errors.put("notesValeur", "Note invalide (0-20)");


        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            try {
                req.setAttribute(ATTR_ELEVES, eleveDAO.findAll());
            } catch (SQLException e) {
                req.setAttribute(ATTR_ELEVES, Collections.emptyList());
            }
            req.getRequestDispatcher("/WEB-INF/vues/note/form.jsp").forward(req, resp);

            return;
        }

        try {
            NoteEleve n = new NoteEleve();
            n.setId(id);
            n.setEleveId(Long.parseLong(eleveIdStr));
            n.setMatiere(matiere);
            n.setCoefficient(parseInt(coefStr, 1));
            n.setNotesValeur(new BigDecimal(noteStr));
            n.setTrimestre(parseInt(trimStr, 1));
            n.setProfSaisie(prof);

            if (id == null) noteDAO.create(n);
            else noteDAO.update(n);

            resp.sendRedirect(req.getContextPath() + "/app/notes?msg=" + (id == null ? "cree" : "modifie"));
        } catch (java.sql.SQLException e) {
            throw new ServletException("Erreur lors de l'enregistrement de la note", e);
        }
    }

    private void showSaisieMasseForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String niveau = req.getParameter(Constants.PARAM_NIVEAU);
            String serie  = req.getParameter(Constants.PARAM_SERIE);
            String salle  = req.getParameter(PARAM_SALLE);
            
            if (niveau != null && !niveau.isEmpty()) {
                // On récupère les élèves filtrés par niveau, série et salle (taille max 100 pour la saisie)
                com.lycee.dto.EleveSearchCriteria criteria = new com.lycee.dto.EleveSearchCriteria(null, null, serie, niveau, null, salle);
                req.setAttribute(ATTR_ELEVES, eleveDAO.findAllPaginated(1, 100, criteria));
                req.setAttribute(PARAM_SALLE, salle);
            }
            req.getRequestDispatcher("/WEB-INF/vues/note/saisie_masse.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void sauvegarderMasse(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String matiere = req.getParameter(Constants.PARAM_MATIERE);
        String prof    = req.getParameter("profSaisie");
        String trimStr = req.getParameter(Constants.PARAM_TRIMESTRE);
        String coefStr = req.getParameter("coefficient");
        int trimestre  = parseInt(trimStr, 1);
        int coef       = parseInt(coefStr, 1);
        
        String[] eleveIds = req.getParameterValues("eleveIds");
        List<NoteEleve> notes = new ArrayList<>();
        
        if (eleveIds != null) {
            for (String eid : eleveIds) {
                String val = req.getParameter("note_" + eid);
                if (val != null && !val.isEmpty()) {
                    try {
                        NoteEleve n = new NoteEleve();
                        n.setEleveId(Long.parseLong(eid));
                        n.setMatiere(matiere);
                        n.setCoefficient(coef);
                        n.setNotesValeur(new BigDecimal(val));
                        n.setTrimestre(trimestre);
                        n.setProfSaisie(prof);
                        notes.add(n);
                    } catch (Exception e) { /* Ignorer les notes invalides */ }
                }
            }
        }
        
        try {
            noteDAO.saveBulk(notes);
            resp.sendRedirect(req.getContextPath() + "/app/notes?msg=cree");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private int parseInt(String s, int def) {
        try {
            return (s == null || s.isEmpty()) ? def : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
