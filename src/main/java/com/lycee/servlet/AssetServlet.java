package com.lycee.servlet;

import com.lycee.util.AuthUtil;
import com.lycee.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Sert les assets institutionnels (logo) stockés hors webapp. */
@WebServlet("/app/assets/*")
public class AssetServlet extends HttpServlet {

    private static final String SAFE_FILENAME = "^[a-zA-Z0-9\\-]+\\.(png|jpg|jpeg|gif|webp)$";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!AuthUtil.isAdmin(AuthUtil.getRole(req))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String filename = pathInfo.substring(1);
        if (!filename.matches(SAFE_FILENAME)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Path file = Paths.get(Constants.UPLOAD_DIR_ASSETS, filename).normalize();
        if (!file.startsWith(Paths.get(Constants.UPLOAD_DIR_ASSETS)) || !Files.isRegularFile(file)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        resp.setContentType(switch (ext) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        });
        resp.setHeader("Cache-Control", "private, max-age=300");
        Files.copy(file, resp.getOutputStream());
    }
}
