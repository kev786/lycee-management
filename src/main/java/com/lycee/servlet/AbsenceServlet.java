package com.lycee.servlet;

import com.lycee.dao.*;
import com.lycee.dao.impl.*;
import com.lycee.model.*;
import com.lycee.service.NotificationService;
import com.lycee.service.SmsService;
import com.lycee.util.Constants;
import com.lycee.util.DateUtil;
import com.lycee.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsenceServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 15;
    private static final int SEUIL_SMS  = 10;
    private static final String ATTR_ELEVES = "eleves";
    private static final Logger LOG = LoggerFactory.getLogger(AbsenceServlet.class);


    private static final int SEUIL_CRITIQUE_HEURES = 8;

    private final transient AbsenceDAO absenceDAO = new AbsenceDAOImpl();
    private final transient EleveDAO   eleveDAO   = new EleveDAOImpl();
    private final transient ClasseDAO  classeDAO  = new ClasseDAOImpl();
    private final transient SmsService smsService = new SmsService();
    private final transient NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                lister(req, resp);
            } else if (Constants.ROUTE_NOUVEAU.equals(path)) {
                showForm(req, resp, null);
            } else if (path.startsWith(Constants.ROUTE_MODIFIER)) {
                handleModifier(req, resp, path);
            } else if (path.startsWith(Constants.ROUTE_SUPPRIMER)) {
                handleSupprimer(req, resp, path);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doGet absences", e);
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
            long id = Long.parseLong(path.substring(Constants.ROUTE_MODIFIER.length()));
            showForm(req, resp, absenceDAO.findById(id));
        } catch (NumberFormatException | java.sql.SQLException e) {
            throw new ServletException("Erreur lors de la modification de l'absence", e);
        }
    }

    private void handleSupprimer(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            long id = Long.parseLong(path.substring(Constants.ROUTE_SUPPRIMER.length()));
            absenceDAO.delete(id);
            resp.sendRedirect(req.getContextPath() + "/app/absences?msg=" + Constants.MSG_SUPPRIME);
        } catch (NumberFormatException | java.sql.SQLException e) {
            throw new ServletException("Erreur lors de la suppression de l'absence", e);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (Constants.ROUTE_NOUVEAU.equals(path)) {
                sauvegarder(req, resp, null);
            } else if (path != null && path.startsWith(Constants.ROUTE_MODIFIER)) {
                handlePostModifier(req, resp, path);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doPost absences", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private void handlePostModifier(HttpServletRequest req, HttpServletResponse resp, String path)
            throws ServletException, IOException {
        try {
            long id = Long.parseLong(path.substring(Constants.ROUTE_MODIFIER.length()));
            sauvegarder(req, resp, id);
        } catch (NumberFormatException _) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }



    private void lister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String search = ValidationUtil.param(req, Constants.PARAM_SEARCH);
            int page = getPage(req);
            int size = getSize(req);
            String niveau  = ValidationUtil.param(req, "niveau");
            String serie   = ValidationUtil.param(req, "serie");
            String salle   = ValidationUtil.param(req, "salle");
            String cidStr  = ValidationUtil.param(req, "classeId");
            Long classeId  = (cidStr != null && !cidStr.isBlank()) ? Long.parseLong(cidStr) : null;

            int total = absenceDAO.countAll(search, classeId, serie, niveau, salle);
            int pages = (int) Math.ceil((double) total / size);
            List<Absence> list = absenceDAO.findAllPaginated(page, size, search, classeId, serie, niveau, salle);
            req.setAttribute("absences",   list);
            req.setAttribute("search",     search);
            req.setAttribute("niveau",     niveau);
            req.setAttribute("serie",      serie);
            req.setAttribute("salle",      salle);
            req.setAttribute("classeId",   classeId);
            req.setAttribute(Constants.PARAM_PAGE,       page);
            req.setAttribute(Constants.PARAM_SIZE,       size);
            req.setAttribute("totalPages", pages);
            req.setAttribute("total",      total);
            req.setAttribute(ATTR_ELEVES,  eleveDAO.findAll());
            req.setAttribute("classes",    classeDAO.findAll());

            int trimestre = DateUtil.getTrimestreCourant();
            req.setAttribute("trimestreCourant", trimestre);
            req.setAttribute("statMois",         absenceDAO.countTotalMoisCourant());
            req.setAttribute("statInjustifiees", absenceDAO.countInjustifieesTrimestre(trimestre));
            req.setAttribute("statJustifiees",   absenceDAO.countJustifieesTrimestre(trimestre));
            req.setAttribute("statSeuilCritique", absenceDAO.countElevesSeuilInjustifie(SEUIL_CRITIQUE_HEURES, trimestre));

            if (search != null && !search.isBlank() && !list.isEmpty()) {
                 req.setAttribute("statsMatiere", absenceDAO.getAbsencesParMatiere(list.get(0).getEleveId()));
            }
            req.getRequestDispatcher("/WEB-INF/vues/absence/liste.jsp").forward(req, resp);
        } catch (java.sql.SQLException e) {
            throw new ServletException("Erreur lors du listing des absences", e);
        }
    }

    private int getPage(HttpServletRequest req) {
        try {
            String p = req.getParameter(Constants.PARAM_PAGE);
            return (p == null) ? 1 : Math.max(1, Integer.parseInt(p));
        } catch (NumberFormatException _) {
            return 1;
        }
    }

    private int getSize(HttpServletRequest req) {
        try {
            String s = req.getParameter(Constants.PARAM_SIZE);
            return (s == null) ? DEFAULT_PAGE_SIZE : Math.max(5, Integer.parseInt(s));
        } catch (NumberFormatException _) {
            return DEFAULT_PAGE_SIZE;
        }
    }


    private void showForm(HttpServletRequest req, HttpServletResponse resp, Absence a)
            throws ServletException, IOException {
        try {
            req.setAttribute("absence", a);
            req.setAttribute(ATTR_ELEVES,  eleveDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/vues/absence/form.jsp").forward(req, resp);
        } catch (java.sql.SQLException e) {

            throw new ServletException("Erreur lors de l'affichage du formulaire d'absence", e);
        }
    }

    private void sauvegarder(HttpServletRequest req, HttpServletResponse resp, Long id)
            throws ServletException, IOException {
        Map<String, String> errors = new HashMap<>();

        String eleveIdStr  = ValidationUtil.param(req, Constants.PARAM_ELEVE_ID);
        String dateStr     = ValidationUtil.param(req, Constants.PARAM_DATE_ABSENCE);
        String dureeStr    = ValidationUtil.param(req, "dureeHeures");
        String matiere     = ValidationUtil.param(req, Constants.PARAM_MATIERE);
        String justStr     = req.getParameter("justifiee");
        String motif       = ValidationUtil.param(req, "motif");

        if (ValidationUtil.isEmpty(eleveIdStr)) errors.put(Constants.PARAM_ELEVE_ID, "Élève obligatoire");
        if (ValidationUtil.isEmpty(dateStr))    errors.put(Constants.PARAM_DATE_ABSENCE, "Date obligatoire");

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute(ATTR_ELEVES, getElevesSafe());
            req.getRequestDispatcher("/WEB-INF/vues/absence/form.jsp").forward(req, resp);
            return;
        }


        try {
            Absence a = buildAbsence(id, eleveIdStr, dateStr, dureeStr, matiere, justStr, motif);

            if (id == null) absenceDAO.create(a);
            else absenceDAO.update(a);

            verifierAlerteSms(a);

            resp.sendRedirect(req.getContextPath() + "/app/absences?msg=" + (id == null ? Constants.MSG_CREE : Constants.MSG_MODIFIE));
        } catch (java.sql.SQLException | java.time.format.DateTimeParseException e) {
            throw new ServletException("Erreur lors de l'enregistrement de l'absence", e);
        }
    }

    private List<Eleve> getElevesSafe() {
        try {
            return eleveDAO.findAll();
        } catch (java.sql.SQLException _) {
            return Collections.emptyList();
        }
    }

    private Absence buildAbsence(Long id, String eleveIdStr, String dateStr, String dureeStr, 
                                 String matiere, String justStr, String motif) {
        Absence a = new Absence();
        a.setId(id);
        a.setEleveId(Long.parseLong(eleveIdStr));
        a.setDateAbsence(LocalDate.parse(dateStr));
        try {
            a.setDureeHeures(Integer.parseInt(dureeStr));
        } catch (NumberFormatException _) {
            a.setDureeHeures(1);
        }
        a.setMatiere(matiere);
        a.setJustifiee("on".equals(justStr) || "true".equals(justStr));
        a.setMotif(motif);
        return a;
    }

    private void verifierAlerteSms(Absence a) throws java.sql.SQLException {
        if (!a.isJustifiee()) {
            Eleve eleve = eleveDAO.findById(a.getEleveId());
            if (eleve != null && eleve.getTelParent() != null) {
                int totalInjustifiees = absenceDAO.countAbsencesInjustifieesParEleve(
                    a.getEleveId(), DateUtil.getTrimestreCourant());
                if (totalInjustifiees >= SEUIL_SMS) {
                    smsService.envoyerSmsAlerte(eleve.getTelParent(), eleve.getPrenom(), totalInjustifiees);
                    notificationService.notifier(
                        Constants.ROLE_CENSEUR,
                        "Alerte absence : " + eleve.getNomComplet() + " — " + totalInjustifiees + " absences injustifiées (T"
                            + DateUtil.getTrimestreCourant() + ")",
                        "/app/absences?q=" + eleve.getNom(),
                        "warning");
                    notificationService.notifier(
                        Constants.ROLE_ADMIN,
                        "Alerte absence : " + eleve.getNomComplet() + " — seuil SMS atteint",
                        "/app/absences?q=" + eleve.getNom(),
                        "warning");
                }
            }
        }
    }
}
