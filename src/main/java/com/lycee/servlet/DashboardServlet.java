package com.lycee.servlet;

import com.lycee.dao.*;
import com.lycee.dao.impl.*;
import com.lycee.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardServlet.class);
    private final transient EleveDAO   eleveDAO   = new EleveDAOImpl();
    private final transient ClasseDAO  classeDAO  = new ClasseDAOImpl();
    private final transient AbsenceDAO absenceDAO = new AbsenceDAOImpl();
    private final transient NoteDAO    noteDAO    = new NoteDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int trimestre = DateUtil.getTrimestreCourant();

            int nbEleves   = eleveDAO.countAll(new com.lycee.dto.EleveSearchCriteria("", null, null, null, null, null));
            int nbGarcons  = eleveDAO.countAll(new com.lycee.dto.EleveSearchCriteria("", null, null, null, "M", null));
            int nbFilles   = eleveDAO.countAll(new com.lycee.dto.EleveSearchCriteria("", null, null, null, "F", null));
            int nbClasses  = classeDAO.countAll("", null, null);
            int nbAbsences = absenceDAO.countAll("", null, null, null, null);
            double moyGlobale = noteDAO.getMoyenneGlobale();
            int totalAbsH = absenceDAO.getTotalHeuresInjustifieesGlobale();
            double tauxAbsGlobal = absenceDAO.getTauxAbsenteeismeGlobal();

            List<Map<String, Object>> tauxEchec = noteDAO.getTauxEchecParMatiere(trimestre);
            String pireMatiere = "—";
            double tauxEchecPire = 0.0;
            if (!tauxEchec.isEmpty()) {
                pireMatiere = (String) tauxEchec.get(0).get("matiere");
                tauxEchecPire = ((Number) tauxEchec.get(0).get("tauxEchec")).doubleValue();
            }

            List<Map<String, Object>> absMois = absenceDAO.getHeuresParMois(5);
            List<Map<String, Object>> chartAbsences = buildChartData(absMois);
            int maxHeuresChart = chartAbsences.stream()
                .mapToInt(m -> ((Number) m.get("heures")).intValue())
                .max().orElse(1);

            req.setAttribute("nbEleves", nbEleves);
            req.setAttribute("nbGarcons", nbGarcons);
            req.setAttribute("nbFilles", nbFilles);
            req.setAttribute("nbClasses", nbClasses);
            req.setAttribute("nbAbsences", nbAbsences);
            req.setAttribute("moyGlobale", String.format("%.2f", moyGlobale));
            req.setAttribute("totalAbsH", totalAbsH);
            req.setAttribute("tauxAbsenteeisme", String.format("%.1f", tauxAbsGlobal));
            req.setAttribute("tauxAbsBarWidth", Math.min(100, Math.round(tauxAbsGlobal)));
            req.setAttribute("pireMatiere", pireMatiere);
            req.setAttribute("tauxEchecPire", String.format("%.1f", tauxEchecPire));
            req.setAttribute("trimestreCourant", trimestre);
            req.setAttribute("dernieresNotes", enrichirNotes(noteDAO.findRecentes(6)));
            req.setAttribute("chartAbsences", chartAbsences);
            req.setAttribute("maxHeuresChart", maxHeuresChart);
            req.setAttribute("absenteismeParClasse", absenceDAO.getTauxAbsenteismeParClasse());

            req.getRequestDispatcher("/WEB-INF/vues/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            LOG.error("Erreur lors du chargement du tableau de bord", e);
            sendErrorSafe(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private List<Map<String, Object>> enrichirNotes(List<Map<String, Object>> notes) {
        for (Map<String, Object> n : notes) {
            double v = 0;
            Object noteObj = n.get("note");
            if (noteObj instanceof BigDecimal bd) {
                v = bd.doubleValue();
            } else if (noteObj instanceof Number num) {
                v = num.doubleValue();
            }
            n.put("noteVal", v);
            n.put("noteAff", String.format("%.1f", v));
            if (v >= 14) {
                n.put("statut", "excellent");
                n.put("statutLabel", "Excellent");
            } else if (v >= 10) {
                n.put("statut", "moyen");
                n.put("statutLabel", "Admis");
            } else {
                n.put("statut", "critique");
                n.put("statutLabel", "Critique");
            }
            String nom = (String) n.get("nom");
            String prenom = (String) n.get("prenom");
            String iniNom = (nom != null && !nom.isEmpty()) ? nom.substring(0, 1) : "?";
            String iniPrenom = (prenom != null && !prenom.isEmpty()) ? prenom.substring(0, 1) : "?";
            n.put("initiales", iniNom + iniPrenom);
        }
        return notes;
    }

    private List<Map<String, Object>> buildChartData(List<Map<String, Object>> raw) {
        List<Map<String, Object>> chart = new ArrayList<>();
        Locale fr = Locale.FRENCH;
        for (Map<String, Object> row : raw) {
            int mois = ((Number) row.get("mois")).intValue();
            String label = Month.of(mois).getDisplayName(TextStyle.SHORT, fr);
            label = label.substring(0, 1).toUpperCase() + label.substring(1);
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", label);
            int heures = ((Number) row.get("heures")).intValue();
            point.put("heures", heures);
            chart.add(point);
        }
        return chart;
    }

    private void sendErrorSafe(HttpServletResponse resp, int code) {
        try {
            resp.sendError(code);
        } catch (IOException e) {
            LOG.error("Impossible d'envoyer l'erreur {}", code, e);
        }
    }
}
