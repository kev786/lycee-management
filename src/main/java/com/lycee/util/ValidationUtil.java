package com.lycee.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ValidationUtil {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return phone.matches("^\\+?[0-9]{8,15}$");
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim()
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }

    public static String param(HttpServletRequest req, String name) {
        String val = req.getParameter(name);
        return val == null ? "" : val.trim();
    }

    /** Valide un score de note : entre 0 et 20 */
    public static boolean isValidNote(String val) {
        try {
            double d = Double.parseDouble(val);
            return d >= 0 && d <= 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Map<String, String> buildErrors() {
        return new HashMap<>();
    }
}
