package com.lycee.servlet;

import com.lycee.dao.*;
import com.lycee.dao.impl.*;
import com.lycee.util.DateUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@WebServlet("/api/charts/*")
public class ChartDataServlet extends HttpServlet {

    private final transient NoteDAO noteDAO = new NoteDAOImpl();
    private final transient AbsenceDAO absenceDAO = new AbsenceDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"not found\"}");
                return;
            }

            String json;
            switch (pathInfo) {
                case "/moyennes-par-classe":
                    int trimestre = parseInt(req.getParameter("trimestre"), DateUtil.getTrimestreCourant());
                    json = buildMoyennesJson(noteDAO.getMoyennesParClasse(trimestre));
                    break;
                case "/absences-par-mois":
                    json = buildAbsencesJson(absenceDAO.getHeuresParMois(5));
                    break;
                case "/repartition-decision":
                    int t = parseInt(req.getParameter("trimestre"), DateUtil.getTrimestreCourant());
                    json = buildDecisionJson(noteDAO.getRepartitionDecisions(t));
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    json = "{\"error\":\"unknown endpoint\"}";
            }
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                resp.getWriter().write("[]");
            } catch (IOException ignored) {}
        }
    }

    private int parseInt(String s, int def) {
        if (s == null) return def;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    private String buildMoyennesJson(List<Map<String, Object>> data) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            if (i > 0) json.append(",");
            Map<String, Object> row = data.get(i);
            String classe = row.get("niveau") + " " + row.get("serie");
            double moyenne = ((Number) row.get("moyenneClasse")).doubleValue();
            json.append("{\"classe\":\"").append(escape(classe))
                .append("\",\"moyenne\":").append(String.format(Locale.US, "%.2f", moyenne)).append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String buildAbsencesJson(List<Map<String, Object>> data) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            if (i > 0) json.append(",");
            Map<String, Object> row = data.get(i);
            int moisNum = ((Number) row.get("mois")).intValue();
            String mois = Month.of(moisNum).getDisplayName(TextStyle.SHORT, Locale.FRENCH);
            mois = mois.substring(0, 1).toUpperCase() + mois.substring(1);
            int heures = ((Number) row.get("heures")).intValue();
            json.append("{\"mois\":\"").append(escape(mois))
                .append("\",\"heures\":").append(heures).append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String buildDecisionJson(List<Map<String, Object>> data) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < data.size(); i++) {
            if (i > 0) json.append(",");
            Map<String, Object> row = data.get(i);
            json.append("{\"decision\":\"").append(escape((String) row.get("decision")))
                .append("\",\"count\":").append(row.get("count")).append("}");
        }
        json.append("]");
        return json.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
