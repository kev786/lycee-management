<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion | Lycée Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/login.css">
</head>
<body class="auth-body">
    <!-- Background Layer -->
    <div class="bg-pattern"></div>
    <div class="bg-overlay"></div>

    <main style="position: relative; z-index: 20; width: 100%; max-width: 480px; padding: 0 24px;">
        <!-- Login Card -->
        <div class="login-card">
            <!-- Logo and Header -->
            <div class="text-center mb-xl">
                <div class="logo-circle">
                    <c:choose>
                        <c:when test="${not empty etablissement.logoFilename}">
                            <img src="${pageContext.request.contextPath}/assets/${etablissement.logoFilename}"
                                 alt="Logo" style="max-height:40px; max-width:40px;"/>
                        </c:when>
                        <c:otherwise>
                            <span class="material-symbols-outlined" style="font-size: 40px;">school</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <h1 style="font-size: 1.5rem; font-weight: 800; color: var(--primary); letter-spacing: -0.02em;">
                    <c:out value="${not empty etablissement.etablissement ? etablissement.etablissement : 'Lycée Admin'}"/>
                </h1>
                <p style="font-size: 0.875rem; color: var(--on-surface-variant); margin-top: 4px; font-weight: 500;">Portail institutionnel de gestion</p>
                <c:if test="${not empty etablissement.devise}">
                    <p style="font-size: 0.75rem; color: var(--on-surface-variant); margin-top: 2px; font-style: italic;">
                        <c:out value="${etablissement.devise}"/>
                    </p>
                </c:if>
            </div>

            <c:if test="${not empty erreur}">
                <div style="padding: 12px; background: var(--error-container); border-radius: 8px; color: var(--error); font-size: 13px; margin-bottom: 20px; border: 1px solid rgba(186, 26, 26, 0.2); display: flex; gap: 8px; align-items: center;">
                    <span class="material-symbols-outlined" style="font-size: 18px;">error</span>
                    ${erreur}
                </div>
            </c:if>

            <!-- Login Form -->
            <form action="${pageContext.request.contextPath}/login" method="post" style="display: flex; flex-direction: column; gap: 24px;">
                <!-- Login Field -->
                <div class="input-group">
                    <label class="label" for="login">Identifiant</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">person</span>
                        <input class="input-field" id="login" name="login" placeholder="admin.lycee" type="text" required/>
                    </div>
                </div>

                <!-- Password Field -->
                <div class="input-group" style="margin-bottom: 16px;">
                    <label class="label" for="motPasse">Mot de passe</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">lock</span>
                        <input class="input-field" id="motPasse" name="motPasse" placeholder="••••••••" type="password" required/>
                    </div>
                    <div style="display: flex; justify-content: flex-end; margin-top: 8px;">
                        <a href="${pageContext.request.contextPath}/mot-de-passe-oublie" style="font-size: 12px; color: var(--primary); font-weight: 600; text-decoration: none;">Mot de passe oublié ?</a>
                    </div>
                </div>

                <!-- Role Selector -->
                <div class="input-group">
                    <label class="label" for="role">Rôle</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">badge</span>
                        <select class="input-field" id="role" name="role" style="appearance: none;">
                            <option value="admin">Administrateur</option>
                            <option value="censeur">Censeur</option>
                            <option value="professeur">Professeur</option>
                            <option value="surveillant">Surveillant</option>
                        </select>
                        <span class="material-symbols-outlined" style="position: absolute; right: 16px; top: 50%; transform: translateY(-50%); color: var(--outline); pointer-events: none;">expand_more</span>
                    </div>
                </div>

                <!-- Submit Button -->
                <button class="btn btn-primary mt-xl" style="width: 100%; padding: 16px; border-radius: 0.75rem;" type="submit">
                    <span style="font-weight: 600;">Se connecter</span>
                    <span class="material-symbols-outlined">login</span>
                </button>
            </form>
        </div>

        <!-- Footer Help -->
        <footer class="footer-auth">
            <p>© 2026 Lycée Admin. Tous droits réservés.</p>
            <div class="footer-links">
                <a href="#">
                    <span class="material-symbols-outlined" style="font-size: 16px;">help_outline</span>
                    Aide & Assistance
                </a>
                <span style="opacity: 0.4;">•</span>
                <a href="#">
                    <span class="material-symbols-outlined" style="font-size: 16px;">policy</span>
                    Confidentialité
                </a>
            </div>
        </footer>
    </main>

    <!-- Subtle Decorative Elements -->
    <div class="decoration-blur-1"></div>
    <div class="decoration-blur-2"></div>
</body>
</html>
