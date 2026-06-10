<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>403 - Accès refusé | Lycée Admin</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
    <style>
        * { margin:0; padding:0; box-sizing:border-box; }
        body {
            font-family: 'Plus Jakarta Sans', system-ui, sans-serif;
            min-height: 100vh; min-height: 100dvh;
            display: flex; align-items: center; justify-content: center;
            background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
            padding: 20px;
        }
        .card {
            background: white; border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0,0,0,0.2);
            padding: 60px 48px; text-align: center;
            max-width: 520px; width: 100%;
            animation: fadeUp .5s ease-out;
        }
        @keyframes fadeUp { from { opacity:0; transform:translateY(24px); } to { opacity:1; transform:translateY(0); } }
        .code { font-size: 96px; font-weight: 800; color: #dc2626; line-height: 1; }
        .icon { font-size: 56px; margin-bottom: 8px; }
        h1 { font-size: 24px; font-weight: 700; color: #1a1a2e; margin: 16px 0 8px; }
        p { font-size: 15px; color: #6b7280; line-height: 1.7; margin-bottom: 32px; }
        .actions { display: flex; gap: 12px; justify-content: center; flex-wrap: wrap; }
        .btn { padding: 12px 28px; border-radius: 12px; font-weight: 600; font-size: 14px; text-decoration: none; transition: all .2s; border: none; cursor: pointer; display: inline-flex; align-items: center; gap: 8px; }
        .btn-primary { background: #dc2626; color: white; }
        .btn-primary:hover { background: #b91c1c; box-shadow: 0 8px 24px rgba(220,38,38,0.35); transform: translateY(-1px); }
        .btn-ghost { background: #f3f4f6; color: #374151; }
        .btn-ghost:hover { background: #e5e7eb; }
    </style>
</head>
<body>
    <div class="card">
        <div class="icon">🚫</div>
        <div class="code">403</div>
        <h1>Accès refusé</h1>
        <p>Votre rôle ne vous autorise pas à accéder à cette ressource.<br>Contactez l'administrateur si vous pensez qu'il s'agit d'une erreur.</p>
        <div class="actions">
            <a href="${pageContext.request.contextPath}/app/dashboard" class="btn btn-primary">
                <span>→</span> Tableau de bord
            </a>
            <a href="javascript:history.back()" class="btn btn-ghost">← Retour</a>
        </div>
    </div>
</body>
</html>
