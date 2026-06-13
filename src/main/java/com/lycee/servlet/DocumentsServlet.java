package com.lycee.servlet;

import com.lycee.dao.ClasseDAO;
import com.lycee.dao.impl.ClasseDAOImpl;
import com.lycee.dao.EleveDAO;
import com.lycee.dao.impl.EleveDAOImpl;
import com.lycee.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class DocumentsServlet extends HttpServlet {

    private final transient ClasseDAO classeDAO = new ClasseDAOImpl();
    private final transient EleveDAO  eleveDAO  = new EleveDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AuthUtil.denyUnlessAdminOrCenseur(req, resp)) return;
        try {
            req.setAttribute("classes", classeDAO.findAll());
            req.setAttribute("eleves",  eleveDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/vues/documents/index.jsp").forward(req, resp);
        } catch (SQLException | ServletException | IOException e) {
            try {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors du chargement des documents");
            } catch (IOException ignored) {
                // Ignore
            }
        }
    }
}
