package com.lycee.servlet;

import com.lycee.dao.EleveDAO;
import com.lycee.dao.NoteDAO;
import com.lycee.dao.impl.EleveDAOImpl;
import com.lycee.dao.impl.NoteDAOImpl;
import com.lycee.model.Eleve;
import com.lycee.model.NoteEleve;
import com.lycee.service.ExcelService;
import com.lycee.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

public class ExcelServlet extends HttpServlet {

    private final transient ExcelService excelService = new ExcelService();
    private final transient EleveDAO eleveDAO = new EleveDAOImpl();
    private final transient NoteDAO noteDAO = new NoteDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException {

        if (AuthUtil.denyUnlessAdminOrCenseur(req, resp)) return;

        try {
            String path = req.getPathInfo();
            if (path != null && path.equals("/eleves")) {
                exportEleves(resp);
            } else if (path != null && path.equals("/bulletin")) {
                long eleveId = Long.parseLong(req.getParameter("eleveId"));
                int trimestre = Integer.parseInt(req.getParameter("trimestre"));
                exportBulletin(resp, eleveId, trimestre);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void exportEleves(HttpServletResponse resp) throws Exception {
        List<Eleve> eleves = eleveDAO.findAll();
        byte[] data = excelService.exportEleves(eleves);
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=\"eleves.xlsx\"");
        resp.setContentLength(data.length);
        try (OutputStream out = resp.getOutputStream()) {
            out.write(data);
        }
    }

    private void exportBulletin(HttpServletResponse resp, long eleveId, int trimestre) throws Exception {
        Eleve eleve = eleveDAO.findById(eleveId);
        if (eleve == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        List<NoteEleve> notes = noteDAO.findByEleveAndTrimestre(eleveId, trimestre);
        BigDecimal moyenne = noteDAO.getMoyenneEleve(eleveId, trimestre);
        int rang = noteDAO.getRangEleve(eleveId, eleve.getClasseId(), trimestre);
        int effectif = eleveDAO.findByClasse(eleve.getClasseId()).size();
        BigDecimal moyenneClasse = noteDAO.getMoyenneClasse(eleve.getClasseId(), trimestre);

        byte[] data = excelService.exportBulletin(eleve, moyenne, rang, effectif, trimestre, moyenneClasse, notes);
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition",
            "attachment; filename=\"bulletin_" + eleve.getMatricule() + "_T" + trimestre + ".xlsx\"");
        resp.setContentLength(data.length);
        try (OutputStream out = resp.getOutputStream()) {
            out.write(data);
        }
    }
}
