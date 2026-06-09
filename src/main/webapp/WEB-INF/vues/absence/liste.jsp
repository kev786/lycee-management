<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Gestion des Absences" />
    <jsp:param name="active" value="absences" />
</jsp:include>

<div style="display:flex; flex-direction:column; gap:24px;">
    <div class="page-header">
        <div>
            <h2 class="page-title">Suivi des absences</h2>
            <p class="page-subtitle">Trimestre ${trimestreCourant} — saisie et alertes d'assiduité</p>
        </div>
        <a href="${pageContext.request.contextPath}/app/absences/nouveau" class="btn btn-primary btn-sm" style="text-decoration:none;">
            <span class="material-symbols-outlined">add</span> Nouvelle absence
        </a>
    </div>

    <div style="display:grid; grid-template-columns: repeat(4, 1fr); gap:16px;">
        <div class="stat-card">
            <span class="stat-label-mini">Ce mois</span>
            <h3 style="font-size:28px; font-weight:700; color:var(--primary);">${statMois}</h3>
        </div>
        <div class="stat-card">
            <span class="stat-label-mini">Injustifiées (T${trimestreCourant})</span>
            <h3 style="font-size:28px; font-weight:700; color:var(--error);">${statInjustifiees}</h3>
        </div>
        <div class="stat-card">
            <span class="stat-label-mini">Justifiées (T${trimestreCourant})</span>
            <h3 style="font-size:28px; font-weight:700; color:#15803d;">${statJustifiees}</h3>
        </div>
        <div class="stat-card">
            <span class="stat-label-mini">Élèves ≥ 8h injust.</span>
            <h3 style="font-size:28px; font-weight:700; color:var(--warning);">${statSeuilCritique}</h3>
        </div>
    </div>

    <div class="card" style="padding:20px;">
        <form action="${pageContext.request.contextPath}/app/absences/" method="get"
              style="display:grid; grid-template-columns: 2fr 1fr 1fr 1fr auto; gap:12px; align-items:end;">
            <div class="input-group">
                <label class="label" for="search-input">Recherche</label>
                <input id="search-input" class="input-field" type="search" name="q" value="${search}" placeholder="Nom élève ou matière..."/>
            </div>
            <div class="input-group">
                <label class="label" for="abs-niveau-filter">Niveau</label>
                <select id="abs-niveau-filter" name="niveau" class="input-field">
                    <option value="">Tous</option>
                    <option value="6iem" ${niveau == '6iem' ? 'selected' : ''}>6ième</option>
                    <option value="5iem" ${niveau == '5iem' ? 'selected' : ''}>5ième</option>
                    <option value="4iem" ${niveau == '4iem' ? 'selected' : ''}>4ième</option>
                    <option value="3iem" ${niveau == '3iem' ? 'selected' : ''}>3ième</option>
                    <option value="2nde" ${niveau == '2nde' ? 'selected' : ''}>Seconde</option>
                    <option value="1ere" ${niveau == '1ere' ? 'selected' : ''}>Première</option>
                    <option value="Tle"  ${niveau == 'Tle'  ? 'selected' : ''}>Terminale</option>
                </select>
            </div>
            <div class="input-group">
                <label class="label" for="abs-classe-filter">Classe</label>
                <select id="abs-classe-filter" name="classeId" class="input-field">
                    <option value="">Toutes</option>
                    <c:forEach var="c" items="${classes}">
                        <option value="${c.id}" ${classeId == c.id ? 'selected' : ''}>${c.libelle}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="input-group" id="abs-serie-group">
                <label class="label" for="abs-serie-filter">Série</label>
                <select id="abs-serie-filter" name="serie" class="input-field">
                    <option value="">Toutes</option>
                    <option value="A" ${serie == 'A' ? 'selected' : ''}>Série A</option>
                    <option value="C" ${serie == 'C' ? 'selected' : ''}>Série C</option>
                    <option value="D" ${serie == 'D' ? 'selected' : ''}>Série D</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary"><span class="material-symbols-outlined">filter_list</span> Filtrer</button>
        </form>
    </div>

    <div style="display:grid; grid-template-columns: 1fr 2fr; gap:24px; align-items:start;">
        <div style="display:flex; flex-direction:column; gap:20px;">
            <div class="card" style="padding:24px;">
                <h4 style="font-size:16px; font-weight:700; color:var(--primary); margin-bottom:16px;">Saisie rapide</h4>
                <form action="${pageContext.request.contextPath}/app/absences/nouveau" method="post" style="display:flex; flex-direction:column; gap:12px;">
                    <select name="eleveId" class="input-field" required>
                        <option value="">Choisir un élève...</option>
                        <c:forEach var="e" items="${eleves}">
                            <option value="${e.id}">${e.nom} ${e.prenom}
                                <c:if test="${not empty e.classe}"> (${e.classe.libelle})</c:if>
                            </option>
                        </c:forEach>
                    </select>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
                        <input name="dateAbsence" type="date" class="input-field" required value="<%= java.time.LocalDate.now() %>"/>
                        <input name="dureeHeures" type="number" class="input-field" value="1" min="1" max="8"/>
                    </div>
                    <input name="matiere" type="text" class="input-field" placeholder="Matière" required/>
                    <label style="font-size:13px; display:flex; align-items:center; gap:8px;">
                        <input type="checkbox" name="justifiee" value="on"/> Absence justifiée
                    </label>
                    <button type="submit" class="btn btn-primary" style="justify-content:center;">
                        <span class="material-symbols-outlined">save</span> Enregistrer
                    </button>
                </form>
            </div>

            <c:if test="${not empty statsMatiere}">
                <div class="card" style="padding:0; overflow:hidden;">
                    <div style="padding:16px 20px; border-bottom:1px solid var(--outline-variant); font-weight:700; color:var(--primary);">
                        Récapitulatif par matière
                    </div>
                    <table class="data-table">
                        <thead><tr><th>Matière</th><th>Just.</th><th>Injust.</th><th>Total</th></tr></thead>
                        <tbody>
                            <c:forEach var="stat" items="${statsMatiere}">
                                <tr>
                                    <td>${stat.matiere}</td>
                                    <td>${stat.justifiees}h</td>
                                    <td style="color:var(--error);">${stat.injustifiees}h</td>
                                    <td style="font-weight:700;">${stat.total}h</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <c:if test="${statSeuilCritique > 0}">
                <div class="flash-alert flash-warning" role="alert">
                    <span class="material-symbols-outlined">warning</span>
                    <span><strong>${statSeuilCritique} élève(s)</strong> ont atteint le seuil de 8 heures d'absences injustifiées ce trimestre.</span>
                </div>
            </c:if>
        </div>

        <div class="card" style="padding:0; overflow:hidden;">
            <div style="padding:16px 20px; border-bottom:1px solid var(--outline-variant); display:flex; justify-content:space-between; align-items:center;">
                <h4 style="font-size:18px; font-weight:700; color:var(--primary);">Liste des absences</h4>
                <span style="font-size:12px; color:var(--on-surface-variant);">${total} enregistrement(s)</span>
            </div>
            <div class="overflow-x-auto">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Élève</th>
                            <th>Date</th>
                            <th>Matière</th>
                            <th style="text-align:center;">Statut</th>
                            <th style="text-align:right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="a" items="${absences}">
                            <tr class="hover-row">
                                <td style="padding:12px 20px;">
                                    <div style="display:flex; align-items:center; gap:12px;">
                                        <div style="width:36px; height:36px; border-radius:50%; background:var(--surface-variant); display:flex; align-items:center; justify-content:center; font-weight:700; font-size:12px; color:var(--primary);">
                                            ${a.initiales}
                                        </div>
                                        <div>
                                            <p style="font-weight:600;">${a.eleveNom} ${a.elevePrenom}</p>
                                            <p style="font-size:11px; color:var(--on-surface-variant);">${a.classeLibelle}</p>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <p style="font-weight:600;">${a.dateAbsence}</p>
                                    <p style="font-size:11px; color:var(--on-surface-variant);">${a.dureeHeures}h</p>
                                </td>
                                <td>${a.matiere}</td>
                                <td style="text-align:center;">
                                    <span class="status-badge ${a.justifiee ? 'status-justified' : 'status-unjustified'}">
                                        ${a.justifiee ? 'Justifiée' : 'Injustifiée'}
                                    </span>
                                </td>
                                <td style="text-align:right; padding-right:16px;">
                                    <div class="action-menu">
                                        <button type="button" class="action-menu-trigger material-symbols-outlined" aria-label="Actions">more_vert</button>
                                        <div class="action-menu-dropdown">
                                            <a href="${pageContext.request.contextPath}/app/absences/modifier/${a.id}">
                                                <span class="material-symbols-outlined">edit</span> Modifier
                                            </a>
                                            <a href="${pageContext.request.contextPath}/app/absences/supprimer/${a.id}"
                                               onclick="return confirm('Supprimer cette absence ?')">
                                                <span class="material-symbols-outlined">delete</span> Supprimer
                                            </a>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty absences}">
                            <tr>
                                <td colspan="5" style="text-align:center; padding:40px; color:var(--on-surface-variant);">
                                    Aucune absence enregistrée pour ces critères.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <c:set var="paginationLabel" value="absences" />
            <jsp:include page="/WEB-INF/vues/layout/pagination.jsp"/>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    LyceeAdmin.initCascadeFilters({
        niveauId: 'abs-niveau-filter',
        serieId: 'abs-serie-filter',
        salleId: null,
        serieGroupId: 'abs-serie-group',
        currentNiveau: '${niveau}',
        currentSerie: '${serie}',
        currentSalle: ''
    });
});
</script>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
