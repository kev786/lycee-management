package com.lycee.servlet;

import com.lycee.dao.ClasseDAO;
import com.lycee.dao.impl.ClasseDAOImpl;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Endpoint JSON pour récupérer les salles disponibles selon niveau+serie.
 * GET /api/salles?niveau=Tle&serie=C  → ["Salle A", "Salle B"]
 */
public class SallesApiServlet extends HttpServlet {

    private final transient ClasseDAO classeDAO = new ClasseDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String niveau = req.getParameter("niveau");
        String serie  = req.getParameter("serie");

        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");

        try {
            java.io.PrintWriter out = resp.getWriter();
            if (niveau == null || niveau.isBlank()) {
                out.write("[]");
                return;
            }

            List<String> salles = classeDAO.getSallesByNiveauSerie(niveau, serie);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < salles.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(salles.get(i).replace("\"", "\\\"")).append("\"");
            }
            json.append("]");
            out.write(json.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                resp.getWriter().write("[]");
            } catch (IOException ignored) {
                // Ignorer
            }
        }
    }
}
