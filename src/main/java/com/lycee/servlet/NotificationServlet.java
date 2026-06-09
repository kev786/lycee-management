package com.lycee.servlet;

import com.lycee.model.Notification;
import com.lycee.service.NotificationService;
import com.lycee.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/app/notifications")
public class NotificationServlet extends HttpServlet {

    private final transient NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String role = AuthUtil.getRole(req);
        if (role == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        List<Notification> list = notificationService.listerPourRole(role, 15);
        int unread = notificationService.compterNonLues(role);

        StringBuilder json = new StringBuilder("{\"unread\":").append(unread).append(",\"items\":[");
        boolean first = true;
        for (Notification n : list) {
            if (!first) json.append(',');
            first = false;
            json.append("{\"id\":").append(n.getId())
                .append(",\"message\":").append(jsonStr(n.getMessage()))
                .append(",\"lien\":").append(jsonStr(n.getLien()))
                .append(",\"type\":").append(jsonStr(n.getType()))
                .append(",\"lue\":").append(n.isLue())
                .append(",\"date\":").append(jsonStr(
                    n.getDateCreation() != null ? n.getDateCreation().toString() : ""))
                .append('}');
        }
        json.append("]}");
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String role = AuthUtil.getRole(req);
        if (role == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String action = req.getParameter("action");
        if ("readAll".equals(action)) {
            notificationService.toutMarquerLu(role);
        } else {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                try {
                    notificationService.marquerLue(Long.parseLong(idStr));
                } catch (NumberFormatException ignored) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private static String jsonStr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}
