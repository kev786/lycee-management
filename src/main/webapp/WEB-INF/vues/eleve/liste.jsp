<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Gestion des Élèves" />
    <jsp:param name="active" value="eleves" />
</jsp:include>

<div class="page-header">
    <div>
        <h2 class="page-title">Liste des élèves</h2>
        <p class="page-subtitle">Recherche, filtres et gestion des dossiers scolaires</p>
    </div>
    <c:if test="${role == 'Admin' || role == 'Censeur'}">
        <a href="${pageContext.request.contextPath}/app/eleves/export-csv" class="btn btn-ghost btn-sm" style="text-decoration:none;">
            <span class="material-symbols-outlined">download</span>
            Export CSV
        </a>
    </c:if>
</div>

<!-- Filtres et Recherche -->
<form id="filter-form" action="${pageContext.request.contextPath}/app/eleves/" method="get" style="margin-bottom:32px; display:grid; grid-template-columns: 1.5fr 1fr 1fr 1fr 0.8fr 1fr 0.4fr; gap:12px; align-items: flex-end;">
    <div class="input-group">
        <label class="label" for="search-input" style="font-size:11px; margin-bottom:4px;">Recherche libre</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">search</span>
            <input id="search-input" class="input-field" type="search" name="q" value="${search}" placeholder="Nom, matricule..." style="margin-top:0;">
        </div>
    </div>

    <div class="input-group">
        <label class="label" for="niveau-filter" style="font-size:11px; margin-bottom:4px;">Niveau</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">stairs</span>
            <select id="niveau-filter" name="niveau" class="input-field" style="margin-top:0; appearance:none;" onchange="updateEleveFilters(this.value)">
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
    </div>
    
    <div class="input-group">
        <label class="label" for="classe-filter" style="font-size:11px; margin-bottom:4px;">Classe</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">door_open</span>
            <select id="classe-filter" name="classeId" class="input-field" style="margin-top:0; appearance:none;">
                <option value="">Toutes</option>
                <c:forEach var="c" items="${classes}">
                    <option value="${c.id}" data-niveau="${c.niveau}" ${classeId == c.id ? 'selected' : ''}>${c.libelle}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="input-group">
        <label class="label" for="serie-filter" style="font-size:11px; margin-bottom:4px;">Spécialité (Série)</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">science</span>
            <select id="serie-filter" name="serie" class="input-field" style="margin-top:0; appearance:none;">
                <option value="">Toutes</option>
                <option value="Général" ${serie == 'Général' ? 'selected' : ''}>Général (6e/5e)</option>
                <option value="A" ${serie == 'A' ? 'selected' : ''}>Série A</option>
                <option value="C" ${serie == 'C' ? 'selected' : ''}>Série C</option>
                <option value="D" ${serie == 'D' ? 'selected' : ''}>Série D</option>
                <option value="ALL" ${serie == 'ALL' ? 'selected' : ''}>Allemand (ALL)</option>
                <option value="Esp" ${serie == 'Esp' ? 'selected' : ''}>Espagnol (ESP)</option>
                <option value="Chs" ${serie == 'Chs' ? 'selected' : ''}>Chinois (CHS)</option>
                <option value="TI" ${serie == 'TI' ? 'selected' : ''}>Série TI</option>
            </select>
        </div>
    </div>

    <div class="input-group">
        <label class="label" for="salle-filter" style="font-size:11px; margin-bottom:4px;">Salle</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">meeting_room</span>
            <select id="salle-filter" name="salle" class="input-field" style="margin-top:0; appearance:none;" disabled>
                <option value="">Toutes les salles</option>
            </select>
        </div>
    </div>

    <div class="input-group">
        <label class="label" for="sexe-filter" style="font-size:11px; margin-bottom:4px;">Genre</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">person</span>
            <select id="sexe-filter" name="sexe" class="input-field" style="margin-top:0; appearance:none;">
                <option value="">Tous</option>
                <option value="M" ${sexe == 'M' ? 'selected' : ''}>Garçons</option>
                <option value="F" ${sexe == 'F' ? 'selected' : ''}>Filles</option>
            </select>
        </div>
    </div>

    <div class="input-group">
        <label class="label" for="filter-btn-eleve" style="font-size:11px; margin-bottom:4px; opacity:0;">&nbsp;</label>
        <button id="filter-btn-eleve" type="submit" class="btn btn-primary" style="height:46px; width:100%; justify-content:center; padding:0;">
            <span class="material-symbols-outlined">filter_list</span>
        </button>
    </div>
</form>

<script>
document.addEventListener('DOMContentLoaded', () => {
    // Cascade Niveau → Série → Salle
    LyceeAdmin.initCascadeFilters({
        niveauId:      'niveau-filter',
        serieId:       'serie-filter',
        salleId:       'salle-filter',
        serieGroupId:  'serie-group-eleve',
        currentNiveau: '${niveau}',
        currentSerie:  '${serie}',
        currentSalle:  '${salle}'
    });

    // Filtre aussi le dropdown classe selon le niveau
    const classeSelect = document.getElementById('classe-filter');
    const niveauSel = document.getElementById('niveau-filter');
    const filterClasses = (niveau) => {
        Array.from(classeSelect.options).forEach((opt, i) => {
            if (i === 0) return;
            const show = !niveau || opt.getAttribute('data-niveau') === niveau;
            opt.style.display = show ? 'block' : 'none';
            if (!show && opt.selected) classeSelect.value = '';
        });
    };
    niveauSel.addEventListener('change', () => filterClasses(niveauSel.value));
    filterClasses('${niveau}');
});
</script>

<!-- Bento Stats Grid (Minimal) -->
<div class="stats-grid">
    <div class="stat-card">
        <span class="stat-label-mini">Total Élèves</span>
        <div style="display:flex; align-items:flex-end; justify-content:space-between; margin-top:8px;">
            <h3 style="font-size:32px; font-weight:700; color:var(--primary);">${total != null ? total : eleves.size()}</h3>
            <span style="font-size:11px; color:#15803d; font-weight:700; background:#dcfce7; padding:4px 10px; border-radius:9999px;">+4%</span>
        </div>
    </div>
    <div class="stat-card">
        <span class="stat-label-mini">Garçons</span>
        <div style="display:flex; align-items:flex-end; justify-content:space-between; margin-top:8px;">
            <h3 style="font-size:32px; font-weight:700; color:var(--primary);">${nbGarcons}</h3>
            <span style="font-size:14px; color:var(--on-surface-variant);">${total > 0 ? Math.round(nbGarcons * 100 / total) : 0}%</span>
        </div>
    </div>
    <div class="stat-card">
        <span class="stat-label-mini">Filles</span>
        <div style="display:flex; align-items:flex-end; justify-content:space-between; margin-top:8px;">
            <h3 style="font-size:32px; font-weight:700; color:#c026d3;">${nbFilles}</h3>
            <span style="font-size:14px; color:var(--on-surface-variant);">${total > 0 ? Math.round(nbFilles * 100 / total) : 0}%</span>
        </div>
    </div>
    <div class="stat-card">
        <span class="stat-label-mini">Présence Moy.</span>
        <div style="display:flex; align-items:flex-end; justify-content:space-between; margin-top:8px;">
            <h3 style="font-size:32px; font-weight:700; color:var(--primary);">94.2%</h3>
            <div style="width:100px; height:8px; background:var(--surface-container-high); border-radius:10px; overflow:hidden; margin-bottom:10px;">
                <div style="width:94%; height:100%; background:var(--primary);"></div>
            </div>
        </div>
    </div>
</div>

<!-- Table Section -->
<div class="card" style="padding:0; overflow:hidden;">
    <div class="overflow-x-auto">
        <table class="data-table">
            <thead>
                <tr>
                    <th style="padding:16px 24px;">Matricule</th>
                    <th>Élève</th>
                    <th>Sexe</th>
                    <th>Classe</th>
                    <th>Parent & Contact</th>
                    <th style="text-align:right; padding-right:24px;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="e" items="${eleves}">
                    <tr class="hover-row">
                        <td style="padding:16px 24px; font-weight:700; color:var(--primary); font-family:monospace;">${e.matricule}</td>
                        <td style="padding:16px 24px;">
                            <div style="display:flex; align-items:center; gap:16px;">
                                <div style="width:44px; height:44px; border-radius:50%; background:var(--surface-variant); display:flex; align-items:center; justify-content:center; border:1px solid var(--outline-variant); overflow:hidden;">
                                    <c:choose>
                                        <c:when test="${not empty e.photoFilename}">
                                            <img src="${pageContext.request.contextPath}/app/photos/${e.photoFilename}" alt="Avatar de ${e.nom}" style="width:100%; height:100%; object-fit:cover;">
                                        </c:when>
                                        <c:otherwise>
                                            <span style="font-weight:700; color:var(--primary); font-size:14px;">${e.nom.substring(0,1)}${e.prenom.substring(0,1)}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div>
                                    <p style="font-size:16px; font-weight:700; color:var(--on-surface);">${e.nom} ${e.prenom}</p>
                                    <p style="font-size:11px; color:var(--on-surface-variant);">Né(e) le ${e.dateNaissance}</p>
                                </div>
                            </div>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${e.sexe == 'M'}">
                                    <span class="material-symbols-outlined" style="color:#2563eb; font-size:20px;" title="Garçon">man</span>
                                </c:when>
                                <c:when test="${e.sexe == 'F'}">
                                    <span class="material-symbols-outlined" style="color:#db2777; font-size:20px;" title="Fille">woman</span>
                                </c:when>
                                <c:otherwise>--</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty e.classe}">
                                    <span style="padding:6px 14px; background:var(--surface-container-high); color:var(--primary); border-radius:9999px; font-size:11px; font-weight:700; border:1px solid rgba(0, 32, 69, 0.1);">
                                        ${e.classe.libelle}
                                    </span>
                                </c:when>
                                <c:otherwise><span style="color:var(--outline); font-style:italic;">Non classé</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div>
                                <p style="font-size:14px; font-weight:500;">${e.nomParent}</p>
                                <p style="font-size:11px; color:var(--on-surface-variant);">${e.telParent}</p>
                            </div>
                        </td>
                        <td style="text-align:right; padding-right:24px;">
                            <div class="action-menu">
                                <button type="button" class="action-menu-trigger material-symbols-outlined" aria-label="Actions pour ${e.nom}">more_vert</button>
                                <div class="action-menu-dropdown">
                                    <a href="${pageContext.request.contextPath}/app/eleves/modifier/${e.id}">
                                        <span class="material-symbols-outlined">edit</span> Modifier
                                    </a>
                                    <c:if test="${role == 'Admin' || role == 'Censeur'}">
                                        <span class="action-menu-label">Bulletins PDF</span>
                                        <a href="${pageContext.request.contextPath}/app/pdf/bulletin/?eleveId=${e.id}&trimestre=1" target="_blank">
                                            <span class="material-symbols-outlined">description</span> Bulletin T1
                                        </a>
                                        <a href="${pageContext.request.contextPath}/app/pdf/bulletin/?eleveId=${e.id}&trimestre=2" target="_blank">
                                            <span class="material-symbols-outlined">description</span> Bulletin T2
                                        </a>
                                        <a href="${pageContext.request.contextPath}/app/pdf/bulletin/?eleveId=${e.id}&trimestre=3" target="_blank">
                                            <span class="material-symbols-outlined">description</span> Bulletin T3
                                        </a>
                                        <a href="${pageContext.request.contextPath}/app/pdf/convocation/?eleveId=${e.id}&motif=Absences&date=<%= java.time.LocalDate.now().plusDays(7) %>T09:00" target="_blank">
                                            <span class="material-symbols-outlined">warning</span> Convocation
                                        </a>
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/app/eleves/supprimer/${e.id}"
                                       class="action-menu-danger"
                                       onclick="return confirm('Confirmer la suppression de ${e.nom} ${e.prenom} ?')">
                                        <span class="material-symbols-outlined">delete</span> Supprimer
                                    </a>
                                </div>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <c:set var="paginationLabel" value="élèves" />
    <jsp:include page="/WEB-INF/vues/layout/pagination.jsp"/>
</div>

<c:if test="${role == 'Admin' || role == 'Censeur'}">
<!-- Contextual Export Helper -->
<div style="margin-top:32px; padding:24px; background:var(--surface-container-high); border-radius:12px; border:1px solid rgba(0, 32, 69, 0.1); display:flex; justify-content:space-between; align-items:center;">
    <div style="display:flex; align-items:center; gap:20px;">
        <div style="background:var(--primary); color:white; padding:12px; border-radius:50%; display:flex;">
            <span class="material-symbols-outlined">info</span>
        </div>
        <div>
            <p style="font-size:18px; font-weight:700; color:var(--primary);">Besoin d'un rapport complet ?</p>
            <p style="font-size:14px; color:var(--on-surface-variant);">Vous pouvez filtrer par niveau avant d'exporter pour obtenir une liste segmentée.</p>
        </div>
    </div>
    <a href="${pageContext.request.contextPath}/app/eleves/export-csv" class="btn btn-primary" style="padding:12px 24px; text-decoration:none;">Exporter la vue actuelle</a>
</div>
</c:if>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
