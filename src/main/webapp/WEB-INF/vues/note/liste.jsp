<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Saisie des Notes" />
    <jsp:param name="active" value="notes" />
</jsp:include>

<!-- Header Actions Section -->
<div style="display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:32px;">
    <div>
        <h2 style="font-size:32px; font-weight:700; color:var(--primary); letter-spacing:-0.02em;">Saisie des Notes</h2>
        <p style="color:var(--on-surface-variant); font-size:14px; margin-top:4px;">Enregistrez les évaluations périodiques et gérez les performances académiques.</p>
    </div>
    <a href="${pageContext.request.contextPath}/app/notes/nouveau" class="btn btn-primary" style="text-decoration:none; font-size:12px;">
        <span class="material-symbols-outlined" style="font-size:18px;">add</span>
        Nouvelle Note
    </a>
</div>

<!-- Filtres et Recherche -->
<div class="card" style="padding:24px; background:var(--grad-surface); margin-bottom: 24px;">
    <form id="filter-form" action="${pageContext.request.contextPath}/app/notes/" method="get" style="display:grid; grid-template-columns: 1.5fr 1fr 1fr 0.8fr 1fr 1fr 0.4fr; gap:12px; align-items: flex-end;">
        <div class="input-group">
            <label class="label" for="search-input" style="font-size:11px; margin-bottom:4px;">Recherche Élève</label>
            <div class="input-wrapper">
                <span class="material-symbols-outlined input-icon">search</span>
                <input id="search-input" class="input-field" type="search" name="q" value="${search}" placeholder="Nom ou prénom..." style="margin-top:0;">
            </div>
        </div>
        
        <div class="input-group">
            <label class="label" for="note-niveau-filter" style="font-size:11px; margin-bottom:4px;">Niveau</label>
            <div class="input-wrapper">
                <span class="material-symbols-outlined input-icon">stairs</span>
                <select id="note-niveau-filter" name="niveau" class="input-field" style="margin-top:0; appearance:none;">
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

        <div id="note-serie-group" class="input-group">
            <label class="label" for="note-serie-filter" style="font-size:11px; margin-bottom:4px;">Série</label>
            <div class="input-wrapper">
                <span class="material-symbols-outlined input-icon">science</span>
                <select id="note-serie-filter" name="serie" class="input-field" style="margin-top:0; appearance:none;">
                    <option value="">Toutes</option>
                    <option value="ALL" ${serie == 'ALL' ? 'selected' : ''}>Allemand</option>
                    <option value="ESP" ${serie == 'ESP' ? 'selected' : ''}>Espagnol</option>
                    <option value="CHS" ${serie == 'CHS' ? 'selected' : ''}>Chinois</option>
                    <option value="A"   ${serie == 'A'   ? 'selected' : ''}>Série A</option>
                    <option value="C"   ${serie == 'C'   ? 'selected' : ''}>Série C</option>
                    <option value="D"   ${serie == 'D'   ? 'selected' : ''}>Série D</option>
                </select>
            </div>
        </div>

        <div class="input-group">
            <label class="label" for="note-salle-filter" style="font-size:11px; margin-bottom:4px;">Salle</label>
            <div class="input-wrapper">
                <span class="material-symbols-outlined input-icon">meeting_room</span>
                <select id="note-salle-filter" name="salle" class="input-field" style="margin-top:0; appearance:none;" disabled>
                    <option value="">Toutes</option>
                </select>
            </div>
        </div>

        <div class="input-group">
            <label class="label" for="note-matiere-filter" style="font-size:11px; margin-bottom:4px;">Matière</label>
            <div class="input-wrapper">
                <span class="material-symbols-outlined input-icon">book</span>
                <input id="note-matiere-filter" name="matiere" class="input-field" type="text" value="${matiere}" placeholder="Matière..." style="margin-top:0;">
            </div>
        </div>
        
        <div class="input-group">
            <label class="label" for="note-trimestre-filter" style="font-size:11px; margin-bottom:4px;">Trimestre</label>
            <div class="input-wrapper">
                <span class="material-symbols-outlined input-icon">calendar_month</span>
                <select id="note-trimestre-filter" name="trimestre" class="input-field" style="margin-top:0; appearance:none;">
                    <option value="">Tous</option>
                    <option value="1" ${trimestre == 1 ? 'selected' : ''}>1er Trimestre</option>
                    <option value="2" ${trimestre == 2 ? 'selected' : ''}>2ème Trimestre</option>
                    <option value="3" ${trimestre == 3 ? 'selected' : ''}>3ème Trimestre</option>
                </select>
            </div>
        </div>

        <div class="input-group">
            <label class="label" for="filter-btn" style="font-size:11px; margin-bottom:4px; opacity:0;">&nbsp;</label>
            <button id="filter-btn" type="submit" class="btn btn-primary" style="height:46px; width:100%; justify-content:center; padding:0;">
                <span class="material-symbols-outlined">filter_list</span>
            </button>
        </div>
    </form>
    <script>
    document.addEventListener('DOMContentLoaded', () => {
        LyceeAdmin.initCascadeFilters({
            niveauId:     'note-niveau-filter',
            serieId:      'note-serie-filter',
            salleId:      'note-salle-filter',
            serieGroupId: 'note-serie-group',
            currentNiveau: '${niveau}',
            currentSerie:  '${serie}',
            currentSalle:  '${salle}'
        });
    });
    </script>
</div>


<!-- Data Table -->
<div class="card" style="padding:0; overflow:hidden;">
    <div class="overflow-x-auto">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Élève</th>
                    <th>Matière</th>
                    <th style="text-align:center;">Coef.</th>
                    <th style="text-align:center;">Note / 20</th>
                    <th style="text-align:center;">Trimestre</th>
                    <th>Professeur</th>
                    <th style="text-align:right; padding-right:24px;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty notes}">
                        <tr>
                            <td colspan="7" style="text-align:center; padding:48px; color:var(--on-surface-variant);">
                                <span class="material-symbols-outlined" style="font-size:48px; display:block; margin-bottom:8px; opacity:0.4;">grade</span>
                                Aucune note enregistrée.
                                <a href="${pageContext.request.contextPath}/app/notes/nouveau" style="color:var(--primary); font-weight:600;">Saisir la première note</a>
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="n" items="${notes}">
                            <c:set var="isCritique" value="${n.notesValeur != null and n.notesValeur.doubleValue() < 10}" />
                            <tr class="hover-row">
                                <td style="padding:16px 24px;">
                                    <div style="display:flex; align-items:center; gap:12px;">
                                        <div style="width:36px; height:36px; border-radius:10px; background:var(--grad-primary); display:flex; align-items:center; justify-content:center; color:white; font-weight:700; font-size:12px;">
                                            <c:choose>
                                                <c:when test="${not empty n.eleve}">${n.eleve.nom.substring(0,1)}${n.eleve.prenom.substring(0,1)}</c:when>
                                                <c:otherwise>--</c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <p style="font-weight:700; color:var(--on-surface); font-size:14px;">
                                                <c:choose>
                                                    <c:when test="${not empty n.eleve}">${n.eleve.nom} ${n.eleve.prenom}</c:when>
                                                    <c:otherwise>Élève #${n.eleveId}</c:otherwise>
                                                </c:choose>
                                            </p>
                                            <p style="font-size:10px; color:var(--on-surface-variant);">
                                                <c:if test="${not empty n.eleve and not empty n.eleve.classe}">${n.eleve.classe.libelle}</c:if>
                                            </p>
                                        </div>
                                    </div>
                                </td>
                                <td style="font-size:13px; font-weight:600; color:var(--on-surface);">${n.matiere}</td>
                                <td style="text-align:center; font-size:13px; color:var(--on-surface-variant);">${n.coefficient}</td>
                                <td style="text-align:center;">
                                    <span class="${isCritique ? 'text-error' : 'text-primary-color'}" style="font-size:18px; font-weight:800;">${n.notesValeur}</span>
                                    <span style="font-size:11px; color:var(--outline);">/20</span>
                                    <c:if test="${isCritique}">
                                        <span class="status-badge status-critique" style="display:block; margin-top:4px;">Insuffisant</span>
                                    </c:if>
                                </td>
                                <td style="text-align:center;">
                                    <span style="padding:4px 12px; background:var(--surface-variant); border-radius:20px; font-size:11px; font-weight:700; color:var(--primary);">T${n.trimestre}</span>
                                </td>
                                <td style="font-size:12px; color:var(--on-surface-variant);">${not empty n.profSaisie ? n.profSaisie : '—'}</td>
                                <td style="text-align:right; padding-right:24px;">
                                    <div style="display:flex; justify-content:flex-end; gap:8px;">
                                        <a href="${pageContext.request.contextPath}/app/notes/modifier/${n.id}"
                                           class="material-symbols-outlined"
                                           style="padding:8px; color:var(--on-surface-variant); text-decoration:none;" title="Modifier">edit</a>
                                        <a href="${pageContext.request.contextPath}/app/notes/supprimer/${n.id}"
                                           onclick="return confirm('Confirmer la suppression de cette note ?')"
                                           class="material-symbols-outlined"
                                           style="padding:8px; color:var(--error); text-decoration:none;" title="Supprimer">delete</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <c:set var="paginationLabel" value="notes" />
    <jsp:include page="/WEB-INF/vues/layout/pagination.jsp"/>
</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
