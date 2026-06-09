package com.lycee.servlet;

import com.lycee.model.ParametresEtablissement;
import com.lycee.service.ParametreService;
import com.lycee.util.Constants;
import com.lycee.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/app/parametres/*")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 5 * 1024 * 1024)
public class ParametreServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreServlet.class);
    private final transient ParametreService parametreService = new ParametreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || "/".equals(path)) {
                chargerPage(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            LOG.error("Erreur GET parametres", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ParametresEtablissement existant = parametreService.charger();
            ParametresEtablissement p = new ParametresEtablissement();
            p.setEtablissement(ValidationUtil.param(req, "etablissement"));
            p.setAnneeScolaire(ValidationUtil.param(req, "anneeScolaire"));
            p.setDevise(ValidationUtil.param(req, "devise"));
            p.setVille(ValidationUtil.param(req, "ville"));
            p.setTelephone(ValidationUtil.param(req, "telephone"));
            p.setEmail(ValidationUtil.param(req, "email"));
            p.setSiteWeb(ValidationUtil.param(req, "siteWeb"));
            p.setRepublique(ValidationUtil.param(req, "republique"));
            p.setMinistere(ValidationUtil.param(req, "ministere"));
            p.setDelegation(ValidationUtil.param(req, "delegation"));
            p.setEntetePdf(ValidationUtil.param(req, "entetePdf"));
            p.setFiligraneLogo("on".equals(req.getParameter("filigraneLogo")));

            String logoFilename = traiterLogo(req, existant.getLogoFilename());
            p.setLogoFilename(logoFilename);

            parametreService.enregistrer(p);
            resp.sendRedirect(req.getContextPath() + "/app/parametres?msg=enregistre");
        } catch (Exception e) {
            req.setAttribute("erreur", "Erreur lors de l'enregistrement des paramètres.");
            try {
                chargerPage(req, resp);
            } catch (Exception ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private void chargerPage(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        boolean twilioOk = System.getenv("TWILIO_ACCOUNT_SID") != null
            && System.getenv("TWILIO_AUTH_TOKEN") != null
            && System.getenv("TWILIO_PHONE_FROM") != null;
        req.setAttribute("twilioConfigure", twilioOk);
        req.setAttribute("etablissement", parametreService.charger());
        req.getRequestDispatcher("/WEB-INF/vues/parametre/liste.jsp").forward(req, resp);
    }

    private String traiterLogo(HttpServletRequest req, String logoActuel) throws IOException, ServletException {
        Part logoPart = req.getPart("logo");
        if (logoPart == null || logoPart.getSize() == 0) {
            return logoActuel;
        }
        String origName = Paths.get(logoPart.getSubmittedFileName()).getFileName().toString();
        String ext = origName.contains(".") ? origName.substring(origName.lastIndexOf('.')).toLowerCase() : "";
        if (!".png.jpg.jpeg.gif.webp".contains(ext)) {
            throw new ServletException("Format de logo non autorisé");
        }
        Files.createDirectories(Paths.get(Constants.UPLOAD_DIR_ASSETS));
        String filename = "logo_" + UUID.randomUUID() + ext;
        logoPart.write(Constants.UPLOAD_DIR_ASSETS + File.separator + filename);
        return filename;
    }
}
