package com.lycee.dao;

import com.lycee.model.Notification;
import java.sql.SQLException;
import java.util.List;

public interface NotificationDAO {
    void create(Notification n) throws SQLException;
    List<Notification> findForRole(String role, int limit) throws SQLException;
    int countUnreadForRole(String role) throws SQLException;
    void markAsRead(Long id) throws SQLException;
    void markAllReadForRole(String role) throws SQLException;
}
