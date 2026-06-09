<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mot de passe oublié | Lycée Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body class="auth-body">
    <div class="bg-pattern"></div>
    <div class="bg-overlay"></div>

    <main style="position: relative; z-index: 20; width: 100%; max-width: 480px; padding: 0 24px;">
        <div class="login-card">
            <div class="text-center mb-xl">
                <div class="logo-circle">
                    <span class="material-symbols-outlined" style="font-size: 40px;">lock_reset</span>
                </div>
                <h1 style="font-size: 24px; font-weight: 600; color: var(--primary);">Mot de passe oublié</h1>
                <p style="font-size: 14px; color: var(--on-surface-variant); margin-top: 4px;">Saisissez votre identifiant pour demander une réinitialisation</p>
            </div>

            <c:if test="${not empty erreur}">
                <div style="padding: 12px; background: var(--error-container); border-radius: 8px; color: var(--error); font-size: 13px; margin-bottom: 20px; border: 1px solid rgba(186, 26, 26, 0.2); display: flex; gap: 8px; align-items: center;">
                    <span class="material-symbols-outlined" style="font-size: 18px;">error</span>
                    ${erreur}
                </div>
            </c:if>

            <c:if test="${not empty succes}">
                <div style="padding: 12px; background: #dcfce7; border-radius: 8px; color: #15803d; font-size: 13px; margin-bottom: 20px; border: 1px solid rgba(21, 128, 61, 0.2); display: flex; gap: 8px; align-items: center;">
                    <span class="material-symbols-outlined" style="font-size: 18px;">check_circle</span>
                    ${succes}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/mot-de-passe-oublie" method="post" style="display: flex; flex-direction: column; gap: 24px;">
                <div class="input-group">
                    <label class="label" for="login">Identifiant</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">person</span>
                        <input class="input-field" id="login" name="login" placeholder="admin" type="text" required/>
                    </div>
                </div>

                <button class="btn btn-primary mt-xl" style="width: 100%; padding: 16px; border-radius: 0.75rem;" type="submit">
                    <span style="font-weight: 600;">Envoyer la demande</span>
                    <span class="material-symbols-outlined">send</span>
                </button>
            </form>

            <p style="text-align: center; margin-top: 24px; font-size: 13px;">
                <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600; text-decoration: none;">← Retour à la connexion</a>
            </p>
        </div>
    </main>
</body>
</html>
