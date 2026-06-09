<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Erreur serveur | Lycée Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        body {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
        }
        .error-container {
            background: white;
            border-radius: 16px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 60px 40px;
            text-align: center;
            max-width: 500px;
            animation: slideIn 0.6s ease-out;
        }
        @keyframes slideIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .error-icon {
            font-size: 80px;
            margin-bottom: 20px;
            opacity: 0.8;
        }
        .error-code {
            font-size: 72px;
            font-weight: 700;
            color: #f5576c;
            margin: 0;
            line-height: 1;
        }
        .error-title {
            font-size: 24px;
            font-weight: 600;
            color: #1a1a1a;
            margin: 20px 0 10px;
        }
        .error-message {
            font-size: 16px;
            color: #666;
            margin: 0 0 40px;
            line-height: 1.6;
        }
        .error-actions {
            display: flex;
            gap: 12px;
            justify-content: center;
        }
        .btn {
            padding: 12px 24px;
            border-radius: 8px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-primary {
            background: #f5576c;
            color: white;
        }
        .btn-primary:hover {
            background: #d63a4a;
            box-shadow: 0 8px 16px rgba(245, 87, 108, 0.4);
        }
        .btn-secondary {
            background: #f0f0f0;
            color: #1a1a1a;
        }
        .btn-secondary:hover {
            background: #e0e0e0;
        }
        .error-details {
            margin-top: 40px;
            padding-top: 30px;
            border-top: 1px solid #eee;
            font-size: 12px;
            color: #999;
            text-align: left;
            background: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
        }
        .error-details pre {
            margin: 10px 0;
            overflow-x: auto;
            font-size: 11px;
            color: #666;
        }
        .support-link {
            margin-top: 20px;
            font-size: 13px;
        }
        .support-link a {
            color: #f5576c;
            text-decoration: none;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1 class="error-code">500</h1>
        <h2 class="error-title">Erreur serveur interne</h2>
        <p class="error-message">
            Une erreur inattendue s'est produite sur le serveur. 
            Notre équipe technique a été alertée et travaille à la résolution.
        </p>
        
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/app/dashboard" class="btn btn-primary">
                Retour au tableau de bord
            </a>
            <a href="javascript:history.back()" class="btn btn-secondary">
                Retour en arrière
            </a>
        </div>

        <%-- Affiche les détails de l'erreur en mode debug/développement --%>
        <c:if test="${not empty exception}">
            <div class="error-details">
                <p><strong>Détails de l'erreur :</strong></p>
                <pre><%= exception.getClass().getName() + ": " + exception.getMessage() %></pre>
                <p><strong>Trace :</strong></p>
                <pre><%
                    java.io.StringWriter sw = new java.io.StringWriter();
                    exception.printStackTrace(new java.io.PrintWriter(sw));
                    String trace = sw.toString();
                    out.print(trace.substring(0, Math.min(500, trace.length())));
                    if (trace.length() > 500) out.print("...");
                %></pre>
            </div>
        </c:if>

        <div class="support-link">
            <p>Besoin d'aide ? <a href="mailto:support@lycee.local">Contactez le support</a></p>
        </div>
    </div>

    <%-- Importe le tag core pour <c:if> --%>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
</body>
</html>
