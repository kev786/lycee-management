package com.lycee.service;

import com.lycee.dao.NotificationDAO;
import com.lycee.dao.impl.NotificationDAOImpl;
import com.lycee.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    public void notifier(String roleCible, String message, String lien, String type) {
        try {
            Notification n = new Notification();
            n.setRoleCible(roleCible);
            n.setMessage(message);
            n.setLien(lien);
            n.setType(type != null ? type : "info");
            notificationDAO.create(n);
        } catch (SQLException e) {
            LOG.warn("Impossible de créer la notification : {}", e.getMessage());
        }
    }

    public List<Notification> listerPourRole(String role, int limit) {
        try {
            return notificationDAO.findForRole(role, limit);
        } catch (SQLException e) {
            LOG.warn("Lecture notifications impossible : {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public int compterNonLues(String role) {
        try {
            return notificationDAO.countUnreadForRole(role);
        } catch (SQLException e) {
            return 0;
        }
    }

    public void marquerLue(Long id) {
        try {
            notificationDAO.markAsRead(id);
        } catch (SQLException e) {
            LOG.warn("Marquage notification impossible : {}", e.getMessage());
        }
    }

    public void toutMarquerLu(String role) {
        try {
            notificationDAO.markAllReadForRole(role);
        } catch (SQLException e) {
            LOG.warn("Marquage notifications impossible : {}", e.getMessage());
        }
    }
}
