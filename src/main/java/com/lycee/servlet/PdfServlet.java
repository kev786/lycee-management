package com.lycee.servlet;

import com.lycee.dao.*;
import com.lycee.dao.impl.*;
import com.lycee.model.*;
import com.lycee.service.ParametreService;
import com.lycee.service.PdfService;
import com.lycee.service.SmsService;
import com.lycee.util.AuthUtil;
import com.lycee.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/app/pdf/*")
public class PdfServlet extends HttpServlet {

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String PDF_EXTENSION = ".pdf";
    private static final Logger LOG = LoggerFactory.getLogger(PdfServlet.class);

    private final transient PdfService pdfService = new PdfService();
    private final transient SmsService smsService = new SmsService();
    private final transient ParametreService parametreService = new ParametreService();
    private final transient EleveDAO   eleveDAO   = new EleveDAOImpl();
    private final transient NoteDAO    noteDAO    = new NoteDAOImpl();
    private final transient ClasseDAO  classeDAO  = new ClasseDAOImpl();
    private final transient AbsenceDAO absenceDAO = new AbsenceDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (AuthUtil.denyUnlessAdminOrCenseur(req, resp)) return;

        try {
            String path = req.getPathInfo();
            ParametresEtablissement params = parametreService.charger();

            if (path != null && path.startsWith("/bulletin/")) {
                genererBulletin(resp, params,
                    Long.parseLong(req.getParameter("eleveId")),
                    Integer.parseInt(req.getParameter("trimestre")));
            } else if (path != null && path.startsWith("/convocation/")) {
                genererConvocation(resp, params,
                    Long.parseLong(req.getParameter("eleveId")),
                    req.getParameter("motif"), req.getParameter("date"));
            } else if (path != null && path.startsWith("/tableau-honneur")) {
                genererTableauHonneur(resp, params,
                    Long.parseLong(req.getParameter("classeId")),
                    Integer.parseInt(req.getParameter("trimestre")));
            } else if (path != null && path.startsWith("/bulletin-classe")) {
                genererBulletinsClasse(resp, params,
                    Long.parseLong(req.getParameter("classeId")),
                    Integer.parseInt(req.getParameter("trimestre")));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            LOG.error("Paramètre invalide dans la requête PDF", e);
            sendErrorSafe(resp, HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            LOG.error("Erreur génération PDF", e);
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

    private void genererBulletin(HttpServletResponse resp, ParametresEtablissement params,
                                   long eleveId, int trimestre) throws Exception {
        Eleve eleve = eleveDAO.findById(eleveId);
        if (eleve == null) {
            sendErrorSafe(resp, 404);
            return;
        }

        List<NoteEleve> notes = noteDAO.findByEleveAndTrimestre(eleveId, trimestre);
        BigDecimal moy = noteDAO.getMoyenneEleve(eleveId, trimestre);
        int rang = noteDAO.getRangEleve(eleveId, eleve.getClasseId(), trimestre);
        int effectif = eleveDAO.findByClasse(eleve.getClasseId()).size();
        BigDecimal moyenneClasse = noteDAO.getMoyenneClasse(eleve.getClasseId(), trimestre);
        int nbAbs = absenceDAO.countAbsencesInjustifieesParEleve(eleveId, trimestre);
        int heuresAbs = absenceDAO.sumHeuresInjustifieesParEleve(eleveId, trimestre);

        byte[] pdf = pdfService.genererBulletin(
            params, eleve, notes, moy, rang, effectif, trimestre, moyenneClasse, nbAbs, heuresAbs);

        resp.setContentType(CONTENT_TYPE_PDF);
        resp.setHeader(CONTENT_DISPOSITION,
            "attachment; filename=\"bulletin_" + eleve.getMatricule() + "_T" + trimestre + PDF_EXTENSION);
        resp.getOutputStream().write(pdf);

        if (eleve.getTelParent() != null && !eleve.getTelParent().isBlank()) {
            smsService.envoyerSmsBulletin(
                eleve.getTelParent(), eleve.getPrenom(), trimestre,
                moy != null ? moy.toString() : "0", rang, effectif);
        }
    }

    private void genererConvocation(HttpServletResponse resp, ParametresEtablissement params,
                                     long eleveId, String motif, String date) throws Exception {
        Eleve eleve = eleveDAO.findById(eleveId);
        if (eleve == null) {
            sendErrorSafe(resp, 404);
            return;
        }

        int nbAbsences = absenceDAO.countAbsencesInjustifieesParEleve(
            eleveId, DateUtil.getTrimestreCourant());

        byte[] pdf = pdfService.genererConvocation(params, eleve, motif, date, nbAbsences);
        resp.setContentType(CONTENT_TYPE_PDF);
        resp.setHeader(CONTENT_DISPOSITION,
            "attachment; filename=\"convocation_" + eleve.getMatricule() + PDF_EXTENSION);
        resp.getOutputStream().write(pdf);
    }

    private void genererTableauHonneur(HttpServletResponse resp, ParametresEtablissement params,
                                        long classeId, int trimestre) throws Exception {
        Classe classe = classeDAO.findById(classeId);
        if (classe == null) {
            sendErrorSafe(resp, 404);
            return;
        }

        List<Map<String, Object>> top10 = noteDAO.getTop10ParClasse(classeId, trimestre);
        byte[] pdf = pdfService.genererTableauHonneur(
            params, classe.getLibelle(), trimestre, top10);
        resp.setContentType(CONTENT_TYPE_PDF);
        resp.setHeader(CONTENT_DISPOSITION,
            "attachment; filename=\"tableau_honneur_" + classeId + "_T" + trimestre + PDF_EXTENSION);
        resp.getOutputStream().write(pdf);
    }

    private void genererBulletinsClasse(HttpServletResponse resp, ParametresEtablissement params,
                                         long classeId, int trimestre) throws Exception {
        Classe classe = classeDAO.findById(classeId);
        if (classe == null) {
            sendErrorSafe(resp, 404);
            return;
        }
        List<Eleve> eleves = eleveDAO.findByClasse(classeId);
        if (eleves.isEmpty()) {
            sendErrorSafe(resp, 404);
            return;
        }

        ByteArrayOutputStream zipBos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(zipBos)) {
            for (Eleve eleve : eleves) {
                List<NoteEleve> notes = noteDAO.findByEleveAndTrimestre(eleve.getId(), trimestre);
                BigDecimal moy = noteDAO.getMoyenneEleve(eleve.getId(), trimestre);
                int rang = noteDAO.getRangEleve(eleve.getId(), classeId, trimestre);
                BigDecimal moyenneClasse = noteDAO.getMoyenneClasse(classeId, trimestre);
                int nbAbs = absenceDAO.countAbsencesInjustifieesParEleve(eleve.getId(), trimestre);
                int heuresAbs = absenceDAO.sumHeuresInjustifieesParEleve(eleve.getId(), trimestre);

                byte[] pdf = pdfService.genererBulletin(
                    params, eleve, notes, moy, rang, eleves.size(), trimestre,
                    moyenneClasse, nbAbs, heuresAbs);

                String entryName = "bulletin_" + eleve.getMatricule() + "_T" + trimestre + PDF_EXTENSION;
                zos.putNextEntry(new ZipEntry(entryName));
                zos.write(pdf);
                zos.closeEntry();
            }
        }

        resp.setContentType("application/zip");
        resp.setHeader(CONTENT_DISPOSITION,
            "attachment; filename=\"bulletins_" + classe.getLibelle().replace(' ', '_') + "_T" + trimestre + ".zip\"");
        resp.getOutputStream().write(zipBos.toByteArray());
    }
}
