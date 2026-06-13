package com.lycee.servlet;

import com.lycee.dao.ClasseDAO;
import com.lycee.dao.EleveDAO;
import com.lycee.dao.impl.ClasseDAOImpl;
import com.lycee.dao.impl.EleveDAOImpl;
import com.lycee.model.Eleve;
import com.lycee.util.AuthUtil;
import com.lycee.util.Constants;
import com.lycee.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB seuil mémoire
    maxFileSize       = 2 * 1024 * 1024,  // 2 MB max par fichier
    maxRequestSize    = 5 * 1024 * 1024   // 5 MB max requête
)
public class EleveServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String ATTR_ELEVE = "eleve";
    private static final String ATTR_CLASSES = "classes";
    private static final String FORM_JSP = "/WEB-INF/vues/eleve/form.jsp";
    private static final Logger LOG = LoggerFactory.getLogger(EleveServlet.class);

    private final transient EleveDAO  eleveDAO  = new EleveDAOImpl();
    private final transient ClasseDAO classeDAO = new ClasseDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || "/".equals(pathInfo)) {
                lister(req, resp);
            } else if (pathInfo.startsWith(Constants.ROUTE_NOUVEAU)) {
                showForm(req, resp, null);
            } else if (pathInfo.startsWith(Constants.ROUTE_MODIFIER)) {
                handleModifier(req, resp, pathInfo);
            } else if (pathInfo.startsWith(Constants.ROUTE_SUPPRIMER)) {
                handleSupprimer(req, resp, pathInfo);
            } else if ("/export-csv".equals(pathInfo)) {
                if (AuthUtil.denyUnlessAdminOrCenseur(req, resp)) return;
                exportCsv(resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doGet élèves", e);
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


    private void handleModifier(HttpServletRequest req, HttpServletResponse resp, String pathInfo)
            throws ServletException, IOException {
        try {
            String[] parts = pathInfo.split("/");
            long id = Long.parseLong(parts[parts.length - 1]);
            Eleve e = eleveDAO.findById(id);
            showForm(req, resp, e);
        } catch (NumberFormatException | java.sql.SQLException ex) {
            throw new ServletException("Erreur lors de la modification de l'élève", ex);
        }
    }

    private void handleSupprimer(HttpServletRequest req, HttpServletResponse resp, String pathInfo)
            throws ServletException, IOException {
        try {
            String[] parts = pathInfo.split("/");
            long id = Long.parseLong(parts[parts.length - 1]);
            eleveDAO.delete(id);
            resp.sendRedirect(req.getContextPath() + "/app/eleves?msg=" + Constants.MSG_SUPPRIME);
        } catch (NumberFormatException | java.sql.SQLException ex) {
            throw new ServletException("Erreur lors de la suppression de l'élève", ex);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null) pathInfo = "/";
            
            LOG.info("doPost EleveServlet - path: '{}'", pathInfo);

            if (pathInfo.contains("nouveau")) {
                LOG.info("Branche 'nouveau' détectée (Eleve)");
                sauvegarder(req, resp, null);
            } else if (pathInfo.contains("modifier")) {
                LOG.info("Branche 'modifier' détectée (Eleve)");
                handlePostModifier(req, resp, pathInfo);
            } else {
                LOG.warn("Branche 'else' détectée (Eleve) - Route non trouvée: '{}'", pathInfo);
                resp.sendError(404, "Route non trouvée: " + pathInfo);
            }
        } catch (Exception e) {
            LOG.error("Erreur dans doPost élèves", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    private void handlePostModifier(HttpServletRequest req, HttpServletResponse resp, String pathInfo)
            throws ServletException, IOException {
        try {
            String[] parts = pathInfo.split("/");
            long id = Long.parseLong(parts[parts.length - 1]);
            sauvegarder(req, resp, id);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    private void lister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String search = ValidationUtil.param(req, Constants.PARAM_SEARCH);
            String cidStr = ValidationUtil.param(req, Constants.PARAM_CLASSE_ID);
            String serie  = ValidationUtil.param(req, "serie");
            String niveau = ValidationUtil.param(req, "niveau");
            String sexe   = ValidationUtil.param(req, "sexe");
            String salle  = ValidationUtil.param(req, "salle");
            
            Long classeId = (cidStr != null && !cidStr.isBlank()) ? Long.parseLong(cidStr) : null;
            
            int page = getPage(req);
            int size = getSize(req);

            com.lycee.dto.EleveSearchCriteria criteria = new com.lycee.dto.EleveSearchCriteria(search, classeId, serie, niveau, sexe, salle);
            int total = eleveDAO.countAll(criteria);
            int totalPages = (int) Math.ceil((double) total / size);

            List<Eleve> eleves = eleveDAO.findAllPaginated(page, size, criteria);
            int nbGarcons = eleveDAO.countAll(new com.lycee.dto.EleveSearchCriteria(null, null, null, null, "M", salle));
            int nbFilles  = eleveDAO.countAll(new com.lycee.dto.EleveSearchCriteria(null, null, null, null, "F", salle));
            
            req.setAttribute("eleves",     eleves);
            req.setAttribute("nbGarcons",  nbGarcons);
            req.setAttribute("nbFilles",   nbFilles);
            req.setAttribute("salle",      salle);
            req.setAttribute(ATTR_CLASSES,    classeDAO.findAll()); // Pour le filtre dropdown
            req.setAttribute("total",      total);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("page",       page);
            req.setAttribute("size",       size);
            req.setAttribute("search",     search);
            req.setAttribute("classeId",   classeId);
            req.setAttribute("serie",      serie);
            req.setAttribute("niveau",     niveau);
            req.setAttribute("sexe",       sexe);
            req.getRequestDispatcher("/WEB-INF/vues/eleve/liste.jsp").forward(req, resp);
        } catch (java.sql.SQLException e) {
            throw new ServletException("Erreur lors du listing des élèves", e);
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

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Eleve eleve)
            throws ServletException, IOException {
        try {
            req.setAttribute(ATTR_ELEVE,   eleve);
            req.setAttribute(ATTR_CLASSES, classeDAO.findAll());
            req.getRequestDispatcher(FORM_JSP).forward(req, resp);
        } catch (Exception e) { throw new ServletException(e); }
    }

    private void sauvegarder(HttpServletRequest req, HttpServletResponse resp, Long id)
            throws ServletException, IOException {

        Map<String, String> errors = validateRequest(req, id);

        if (!errors.isEmpty()) {
            req.setAttribute("errors",  errors);
            req.setAttribute(ATTR_CLASSES, getClassesSafe());
            req.setAttribute(ATTR_ELEVE,   buildEleveFromRequest(req, id));
            req.getRequestDispatcher(FORM_JSP).forward(req, resp);
            return;
        }

        try {
            String photoFilename = handleUpload(req, errors);
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                req.setAttribute(ATTR_CLASSES, getClassesSafe());
                req.setAttribute(ATTR_ELEVE, buildEleveFromRequest(req, id));
                req.getRequestDispatcher(FORM_JSP).forward(req, resp);
                return;
            }

            Eleve e = buildEleveFromRequest(req, id);
            if (photoFilename != null) e.setPhotoFilename(photoFilename);
            else if (id != null) {
                Eleve existing = eleveDAO.findById(id);
                if (existing != null) e.setPhotoFilename(existing.getPhotoFilename());
            }

            if (id == null) eleveDAO.create(e);
            else eleveDAO.update(e);

            resp.sendRedirect(req.getContextPath() + "/app/eleves?msg=" + (id == null ? Constants.MSG_CREE : Constants.MSG_MODIFIE));
        } catch (java.sql.SQLException | java.time.format.DateTimeParseException ex) {
            throw new ServletException("Erreur lors de l'enregistrement de l'élève", ex);
        }
    }

    private Map<String, String> validateRequest(HttpServletRequest req, Long id) throws ServletException {
        Map<String, String> errors = new HashMap<>();
        String matricule   = ValidationUtil.param(req, Constants.PARAM_MATRICULE);
        String nom         = ValidationUtil.param(req, "nom");
        String prenom      = ValidationUtil.param(req, Constants.PARAM_PRENOM);
        String classeIdStr = ValidationUtil.param(req, Constants.PARAM_CLASSE_ID);
        String telParent   = ValidationUtil.param(req, Constants.PARAM_TEL_PARENT);
        String emailParent = ValidationUtil.param(req, Constants.PARAM_EMAIL_PARENT);

        if (ValidationUtil.isEmpty(matricule)) errors.put(Constants.PARAM_MATRICULE, "Matricule obligatoire");
        if (ValidationUtil.isEmpty(nom))       errors.put("nom", "Nom obligatoire");
        if (ValidationUtil.isEmpty(prenom))    errors.put(Constants.PARAM_PRENOM, "Prénom obligatoire");
        if (ValidationUtil.isEmpty(classeIdStr)) errors.put(Constants.PARAM_CLASSE_ID, "Classe obligatoire");
        
        if (!ValidationUtil.isEmpty(emailParent) && !ValidationUtil.isValidEmail(emailParent))
            errors.put(Constants.PARAM_EMAIL_PARENT, "Email invalide");
        if (!ValidationUtil.isEmpty(telParent) && !ValidationUtil.isValidPhone(telParent))
            errors.put(Constants.PARAM_TEL_PARENT, "Téléphone invalide");

        try {
            if (!ValidationUtil.isEmpty(matricule) && eleveDAO.existsMatricule(matricule, id)) {
                errors.put(Constants.PARAM_MATRICULE, "Ce matricule est déjà utilisé");
            }
        } catch (java.sql.SQLException ex) {
            throw new ServletException(ex);
        }
        return errors;
    }

    private String handleUpload(HttpServletRequest req, Map<String, String> errors) throws IOException, ServletException {
        Part photoPart = req.getPart(Constants.PARAM_PHOTO);
        if (photoPart != null && photoPart.getSize() > 0) {
            String origName  = Paths.get(photoPart.getSubmittedFileName()).getFileName().toString();
            String ext       = origName.contains(".") ? origName.substring(origName.lastIndexOf('.')).toLowerCase() : "";
            if (!".jpg.jpeg.png.gif.webp".contains(ext)) {
                errors.put(Constants.PARAM_PHOTO, "Type de fichier non autorisé");
                return null;
            }
            if (photoPart.getSize() > 2 * 1024 * 1024) {
                errors.put(Constants.PARAM_PHOTO, "Taille max 2 MB");
                return null;
            }
            Files.createDirectories(Paths.get(Constants.UPLOAD_DIR_PHOTOS));
            String photoFilename = UUID.randomUUID() + ext;
            photoPart.write(Constants.UPLOAD_DIR_PHOTOS + File.separator + photoFilename);
            return photoFilename;
        }
        return null;
    }

    private List<com.lycee.model.Classe> getClassesSafe() {
        try {
            return classeDAO.findAll();
        } catch (java.sql.SQLException e) {
            return Collections.emptyList();
        }
    }

    private Eleve buildEleveFromRequest(HttpServletRequest req, Long id) {
        Eleve e = new Eleve();
        e.setId(id);
        e.setMatricule(ValidationUtil.param(req, Constants.PARAM_MATRICULE));
        e.setNom(ValidationUtil.param(req, "nom"));
        e.setPrenom(ValidationUtil.param(req, Constants.PARAM_PRENOM));
        String dateNaissStr = ValidationUtil.param(req, "dateNaissance");
        if (!ValidationUtil.isEmpty(dateNaissStr)) {
            try { e.setDateNaissance(LocalDate.parse(dateNaissStr)); } catch (java.time.format.DateTimeParseException ignored) { /* champ optionnel */ }
        }
        try {
            String cid = ValidationUtil.param(req, Constants.PARAM_CLASSE_ID);
            if (cid != null && !cid.isEmpty()) e.setClasseId(Long.parseLong(cid));
        } catch (NumberFormatException ignored) {
            // champ optionnel
        }
        e.setNomParent(ValidationUtil.param(req, "nomParent"));
        e.setTelParent(ValidationUtil.param(req, Constants.PARAM_TEL_PARENT));
        e.setEmailParent(ValidationUtil.param(req, Constants.PARAM_EMAIL_PARENT));
        e.setSexe(ValidationUtil.param(req, "sexe"));
        return e;
    }

    private void exportCsv(HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"eleves.csv\"");
        try (PrintWriter w = resp.getWriter()) {
            w.println("Matricule,Nom,Prénom,Date Naissance,Classe,Nom Parent,Tél Parent,Email Parent");
            List<Eleve> tous = eleveDAO.findAll();
            for (Eleve e : tous) {
                w.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    csv(e.getMatricule()), csv(e.getNom()), csv(e.getPrenom()),
                    e.getDateNaissance() != null ? e.getDateNaissance().toString() : "",
                    e.getClasse() != null ? csv(e.getClasse().getLibelle()) : "",
                    csv(e.getNomParent()), csv(e.getTelParent()), csv(e.getEmailParent()));
            }
        } catch (java.sql.SQLException ex) {
            throw new ServletException("Erreur lors de l'export CSV", ex);
        }
    }


    private String csv(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

}
