<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Accès refusé | Lycée Admin</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        body { min-height:100vh; display:flex; align-items:center; justify-content:center; background:var(--bg-main); margin:0; }
        .box { background:white; border-radius:16px; padding:48px; max-width:480px; text-align:center; box-shadow:var(--shadow-lg); }
        .code { font-size:64px; font-weight:800; color:var(--error); }
    </style>
</head>
<body>
    <div class="box">
        <div class="code">403</div>
        <h1 style="color:var(--primary); margin:16px 0 8px;">Accès refusé</h1>
        <p style="color:var(--on-surface-variant); line-height:1.6;">
            Votre rôle ne vous autorise pas à accéder à cette ressource.
            Contactez l'administrateur si vous pensez qu'il s'agit d'une erreur.
        </p>
        <div style="margin-top:32px; display:flex; gap:12px; justify-content:center;">
            <a href="${pageContext.request.contextPath}/app/dashboard" class="btn btn-primary" style="text-decoration:none;">Tableau de bord</a>
            <a href="javascript:history.back()" class="btn" style="text-decoration:none;">Retour</a>
        </div>
    </div>
</body>
</html>
