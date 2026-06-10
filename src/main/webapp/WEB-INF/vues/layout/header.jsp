<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="ctx" content="${pageContext.request.contextPath}">
    <title>${param.title} | <c:out value="${not empty etablissement.etablissement ? etablissement.etablissement : 'Lycée Admin'}"/></title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4/dist/chart.umd.min.js" defer></script>
    <script src="${pageContext.request.contextPath}/static/js/app.js" defer></script>
</head>
<body>
    <div class="app-layout">
        <div class="sidebar-overlay" id="sidebar-overlay" hidden></div>

        <aside class="sidebar" id="sidebar">
            <div class="sidebar-brand">
                <div class="sidebar-brand-icon">
                    <c:choose>
                        <c:when test="${not empty etablissement.logoFilename}">
                            <img src="${pageContext.request.contextPath}/assets/${etablissement.logoFilename}"
                                 alt="Logo" style="max-height:32px; max-width:32px; border-radius:6px;"/>
                        </c:when>
                        <c:otherwise>
                            <span class="material-symbols-outlined">school</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="sidebar-brand-text">
                    <span class="sidebar-brand-title"><c:out value="${not empty etablissement.etablissement ? etablissement.etablissement : 'Lycée Admin'}"/></span>
                    <span class="sidebar-brand-sub">Gestion scolaire</span>
                </div>
            </div>

            <nav class="sidebar-nav">
                <p class="nav-section-label">Général</p>
                <a href="${pageContext.request.contextPath}/app/dashboard"
                   class="nav-link ${param.active == 'dashboard' ? 'active' : ''}">
                    <span class="material-symbols-outlined nav-icon">dashboard</span>
                    <span class="nav-label">Tableau de bord</span>
                </a>

                <p class="nav-section-label">Scolarité</p>
                <a href="${pageContext.request.contextPath}/app/eleves/"
                   class="nav-link ${param.active == 'eleves' ? 'active' : ''}">
                    <span class="material-symbols-outlined nav-icon">group</span>
                    <span class="nav-label">Élèves</span>
                </a>
                <a href="${pageContext.request.contextPath}/app/classes/"
                   class="nav-link ${param.active == 'classes' ? 'active' : ''}">
                    <span class="material-symbols-outlined nav-icon">class</span>
                    <span class="nav-label">Classes</span>
                </a>
                <c:if test="${role == 'Admin' || role == 'Censeur'}">
                    <a href="${pageContext.request.contextPath}/app/notes/"
                       class="nav-link ${param.active == 'notes' ? 'active' : ''}">
                        <span class="material-symbols-outlined nav-icon">grading</span>
                        <span class="nav-label">Notes</span>
                    </a>
                </c:if>
                <a href="${pageContext.request.contextPath}/app/absences/"
                   class="nav-link ${param.active == 'absences' ? 'active' : ''}">
                    <span class="material-symbols-outlined nav-icon">event_busy</span>
                    <span class="nav-label">Absences</span>
                </a>
                <c:if test="${role == 'Admin' || role == 'Censeur'}">
                    <a href="${pageContext.request.contextPath}/app/documents"
                       class="nav-link ${param.active == 'documents' ? 'active' : ''}">
                        <span class="material-symbols-outlined nav-icon">description</span>
                        <span class="nav-label">Documents PDF</span>
                    </a>
                </c:if>

                <c:if test="${role == 'Admin'}">
                    <p class="nav-section-label">Administration</p>
                    <a href="${pageContext.request.contextPath}/app/utilisateurs/"
                       class="nav-link ${param.active == 'utilisateurs' ? 'active' : ''}">
                        <span class="material-symbols-outlined nav-icon">manage_accounts</span>
                        <span class="nav-label">Utilisateurs</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/app/parametres/"
                       class="nav-link ${param.active == 'settings' ? 'active' : ''}">
                        <span class="material-symbols-outlined nav-icon">settings</span>
                        <span class="nav-label">Paramètres</span>
                    </a>
                </c:if>
            </nav>

            <div class="sidebar-footer">
                <div class="sidebar-user">
                    <div class="sidebar-user-avatar">
                        ${loginNom != null ? loginNom.substring(0,1).toUpperCase() : 'A'}
                    </div>
                    <div class="sidebar-user-info">
                        <p class="sidebar-user-name">${loginNom}</p>
                        <span class="role-badge role-badge--${role}">${role}</span>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/logout" class="nav-link nav-link--logout">
                    <span class="material-symbols-outlined nav-icon">logout</span>
                    <span class="nav-label">Déconnexion</span>
                </a>
            </div>
        </aside>

        <main class="main-content">
            <header class="app-topbar">
                <div class="topbar-left">
                    <button type="button" class="sidebar-toggle" id="sidebar-toggle" aria-label="Menu">
                        <span class="material-symbols-outlined">menu</span>
                    </button>
                    <div class="topbar-titles">
                        <h1 class="topbar-page-title">${param.title}</h1>
                        <p class="topbar-page-sub">Année scolaire en cours</p>
                    </div>
                </div>

                <div class="topbar-center">
                    <c:if test="${param.active == 'eleves'}">
                        <form class="topbar-search" action="${pageContext.request.contextPath}/app/eleves" method="get">
                            <span class="material-symbols-outlined topbar-search-icon">search</span>
                            <input name="q" value="${param.q}" class="input-field topbar-search-input"
                                   placeholder="Rechercher un élève, matricule…" type="search"/>
                        </form>
                    </c:if>
                </div>

                <div class="topbar-right">
                    <button type="button" class="topbar-icon-btn" id="dark-toggle" aria-label="Mode sombre" title="Mode sombre">
                        <span class="material-symbols-outlined dark-icon-light">dark_mode</span>
                        <span class="material-symbols-outlined dark-icon-dark" style="display:none;">light_mode</span>
                    </button>
                    <div class="notif-panel" id="notif-panel">
                        <button type="button" class="topbar-icon-btn" id="notif-toggle" aria-label="Notifications">
                            <span class="material-symbols-outlined">notifications</span>
                            <span class="notif-badge" id="notif-badge" style="display:none;">0</span>
                        </button>
                        <div class="notif-dropdown" id="notif-dropdown" hidden>
                            <div class="notif-dropdown-header">
                                <span>Notifications</span>
                                <button type="button" class="notif-mark-all" id="notif-mark-all">Tout marquer lu</button>
                            </div>
                            <ul class="notif-list" id="notif-list"></ul>
                        </div>
                    </div>
                    <c:if test="${param.active == 'eleves'}">
                        <a href="${pageContext.request.contextPath}/app/eleves/nouveau" class="btn btn-primary btn-sm topbar-action">
                            <span class="material-symbols-outlined">add</span>
                            Nouvel élève
                        </a>
                    </c:if>
                </div>
            </header>

            <div class="page-content animate-fade">
                <jsp:include page="/WEB-INF/vues/layout/flash.jsp"/>
