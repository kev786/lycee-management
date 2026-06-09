<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Gestion des Classes" />
    <jsp:param name="active" value="classes" />
</jsp:include>
<style>
    .pct-badge { font-size: 11px; font-weight: 700; }
    .pct-critical { color: var(--error) !important; }
    .pct-normal { color: var(--on-surface-variant); }
    .progress-bar-fill { height: 100%; transition: width 0.3s ease; }
    .progress-critical { background: var(--error) !important; }
    .progress-normal { background: var(--primary); }
</style>

<!-- Page Header -->
<div style="display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:32px;">
    <div>
        <h2 style="font-size:32px; font-weight:700; color:var(--primary); letter-spacing:-0.02em;">Gestion des Classes</h2>
        <p style="color:var(--on-surface-variant); font-size:14px; margin-top:4px;">Suivez l'organisation pédagogique et les effectifs en temps réel.</p>
    </div>
    <a href="${pageContext.request.contextPath}/app/classes/nouveau" class="btn btn-primary" style="text-decoration:none; padding:12px 24px;">
        <span class="material-symbols-outlined" style="font-size:20px;">add_circle</span>
        Nouvelle Classe
    </a>
</div>

<!-- Filtres et Recherche -->
<form action="${pageContext.request.contextPath}/app/classes/" method="get" style="margin-bottom:32px; display:grid; grid-template-columns: 2fr 1.5fr 1.5fr 0.5fr; gap:16px; align-items: flex-end;">
    <div class="input-group">
        <label class="label" for="search-input" style="font-size:11px; margin-bottom:4px;">Recherche libre</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">search</span>
            <input id="search-input" class="input-field" type="search" name="q" value="${search}" placeholder="Niveau, prof, année..." style="margin-top:0;">
        </div>
    </div>
    
    <div class="input-group">
        <label class="label" for="niveau-filter" style="font-size:11px; margin-bottom:4px;">Niveau Précis</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">stairs</span>
            <select id="niveau-filter" name="niveau" class="input-field" style="margin-top:0; appearance:none;" onchange="LyceeAdmin.updateClasseFilters(this.value)">
                <option value="">Tous les niveaux</option>
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
        <label class="label" for="serie-filter" style="font-size:11px; margin-bottom:4px;">Spécialité (Série)</label>
        <div class="input-wrapper">
            <span class="material-symbols-outlined input-icon">science</span>
            <select id="serie-filter" name="serie" class="input-field" style="margin-top:0; appearance:none;">
                <option value="">Toutes les séries</option>
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
        <label class="label" for="filter-btn-classe" style="font-size:11px; margin-bottom:4px; opacity:0;">&nbsp;</label>
        <button id="filter-btn-classe" type="submit" class="btn btn-primary" style="height:46px; width:100%; justify-content:center; padding:0;">
            <span class="material-symbols-outlined">filter_list</span>
        </button>
    </div>
</form>




<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label-mini">Total Classes</span>
            <span class="material-symbols-outlined" style="color:var(--primary); font-size:20px;">school</span>
        </div>
        <h3 style="font-size:32px; font-weight:700; color:var(--primary); margin-top:8px;">${stats.totalClasses}</h3>
        <p style="font-size:11px; color:var(--on-surface-variant); margin-top:4px;">Classes enregistrées</p>
    </div>

    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label-mini">Effectif Global</span>
            <span class="material-symbols-outlined" style="color:var(--primary); font-size:20px;">groups</span>
        </div>
        <h3 style="font-size:32px; font-weight:700; color:var(--primary); margin-top:8px;">
            <fmt:formatNumber value="${stats.totalEleves}" pattern="#,###" />
        </h3>
        <c:set var="globalPct" value="${stats.totalCapacite > 0 ? (stats.totalEleves * 100 / stats.totalCapacite) : 0}" />
        <div style="width:100%; height:6px; background:var(--surface-container); border-radius:10px; margin-top:12px; overflow:hidden;">
            <div id="global-progress-bar" 
                 class="progress-bar-fill"
                 data-width="${globalPct > 100 ? 100 : globalPct}%"
                 style="height:100%; background:var(--primary); width:0; transition:width 0.8s ease;"></div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label-mini">Taux Occupation</span>
            <span class="material-symbols-outlined" style="color:var(--primary); font-size:20px;">analytics</span>
        </div>
        <h3 style="font-size:32px; font-weight:700; color:var(--primary); margin-top:8px;">
            <fmt:formatNumber value="${globalPct}" maxFractionDigits="0"/>%
        </h3>
        <p style="font-size:11px; color:var(--on-surface-variant); margin-top:4px;">Sur ${stats.totalCapacite} places</p>
    </div>

    <div class="stat-card">
        <div class="stat-header">
            <span class="stat-label-mini">Salles Occupées</span>
            <span class="material-symbols-outlined" style="color:var(--primary); font-size:20px;">meeting_room</span>
        </div>
        <h3 style="font-size:32px; font-weight:700; color:var(--primary); margin-top:8px;">${stats.sallesOccupees}/${stats.totalClasses}</h3>
        <p style="font-size:11px; color:var(--on-surface-variant); margin-top:4px;">Classes avec élèves</p>
    </div>
</div>


<!-- Data Table -->
<div class="card" style="padding:0; overflow:hidden;">
    <div class="overflow-x-auto">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Niveau & Série</th>
                    <th>Professeur Principal</th>
                    <th>Salle</th>
                    <th>Effectif / Capacité</th>
                    <th style="text-align:right;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${classes}">
                    <tr class="hover-row">
                        <td style="padding:16px 24px;">
                            <div style="display:flex; align-items:center; gap:16px;">
                                <div style="width:40px; height:40px; border-radius:10px; background:var(--primary-fixed); color:var(--primary); display:flex; align-items:center; justify-content:center; font-weight:700;">
                                    ${c.niveau.substring(0,3)}
                                </div>
                                <div>
                                    <p style="font-size:16px; font-weight:700; color:var(--primary);">${c.libelle}</p>
                                    <p style="font-size:11px; color:var(--on-surface-variant);">${c.serie}</p>
                                </div>
                            </div>
                        </td>
                        <td style="font-size:14px; font-weight:500;">${not empty c.profPrincipal ? c.profPrincipal : 'Non assigné'}</td>
                        <td>
                            <span style="padding:4px 12px; background:var(--surface-container-highest); color:var(--primary); border-radius:9999px; font-size:11px; font-weight:700;">Salle ${c.sallePrincipale}</span>
                        </td>
                        <td>
                            <div style="display:flex; flex-direction:column; gap:4px; width:140px;">
                                <c:set var="pct" value="${c.effectifMax > 0 ? (c.effectifActuel * 100 / c.effectifMax) : 0}" />
                                <div style="display:flex; justify-content:space-between; font-size:11px; font-weight:700;">
                                    <span style="color:var(--on-surface);">${c.effectifActuel} / ${c.effectifMax}</span>
                                    <span class="pct-badge ${pct > 90 ? 'pct-critical' : 'pct-normal'}">
                                        <fmt:formatNumber value="${pct}" maxFractionDigits="0"/>%
                                    </span>
                                </div>
                                <div style="width:100%; height:6px; background:var(--surface-container); border-radius:10px; overflow:hidden;">
                                    <div class="progress-bar-fill ${pct > 90 ? 'progress-critical' : 'progress-normal'}" 
                                         data-width="${pct > 100 ? 100 : pct}%"
                                         style="width:0; transition:width 0.8s ease;"></div>
                                </div>
                            </div>
                        </td>
                        <td style="text-align:right; padding-right:24px;">
                            <div style="display:flex; justify-content:flex-end; gap:8px;">
                                <a href="${pageContext.request.contextPath}/app/classes/modifier/${c.id}" class="material-symbols-outlined" style="padding:8px; color:var(--primary); text-decoration:none;">edit</a>
                                <a href="#" class="material-symbols-outlined" style="padding:8px; color:var(--outline); text-decoration:none;">more_vert</a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    
    <c:set var="paginationLabel" value="classes" />
    <jsp:include page="/WEB-INF/vues/layout/pagination.jsp"/>
</div>

<!-- Additional Context Section (Bento Bottom) -->
<div style="display:grid; grid-template-columns: 2fr 1fr; gap:24px; margin-top:32px;">
    <div class="card" style="display:flex; justify-content:space-between; align-items:center;">
        <div style="max-width:400px;">
            <h4 style="font-size:18px; font-weight:700; color:var(--primary);">Répartition des Séries</h4>
            <p style="font-size:13px; color:var(--on-surface-variant); margin-top:8px;">Visualisation rapide de la répartition des élèves par filière d'enseignement.</p>
            
            <div style="display:flex; flex-wrap:wrap; gap:16px; margin-top:24px;">
                <c:forEach var="item" items="${stats.seriesDist}" varStatus="status">
                    <c:set var="itemPct" value="${stats.totalEleves > 0 ? (item.count * 100.0 / stats.totalEleves) : 0}" />
                    <div style="display:flex; align-items:center; gap:8px;">
                        <c:set var="legendColor" value="${status.index == 0 ? 'var(--primary)' : (status.index == 1 ? 'var(--accent)' : (status.index == 2 ? 'var(--info)' : 'var(--on-primary-container)'))}" />
                        <span class="series-legend-dot" data-bg="${legendColor}" style="width:10px; height:10px; border-radius:50%; background: #ccc;"></span>
                        <span style="font-size:11px; font-weight:700;">${item.serie}: <fmt:formatNumber value="${itemPct}" maxFractionDigits="0"/>%</span>
                    </div>
                </c:forEach>
            </div>
        </div>

        <c:set var="gradient" value="" />
        <c:set var="accum" value="0" />
        <c:forEach var="item" items="${stats.seriesDist}" varStatus="status">
            <c:set var="itemPct" value="${stats.totalEleves > 0 ? (item.count * 100.0 / stats.totalEleves) : 0}" />
            <c:set var="color" value="${status.index == 0 ? 'var(--primary)' : (status.index == 1 ? 'var(--accent)' : (status.index == 2 ? 'var(--info)' : 'var(--on-primary-container)'))}" />
            <c:set var="nextAccum" value="${accum + itemPct}" />
            <c:set var="gradient" value="${gradient}${color} ${accum}% ${nextAccum}%${!status.last ? ', ' : ''}" />
            <c:set var="accum" value="${nextAccum}" />
        </c:forEach>
        
        <div id="series-chart" 
             data-gradient="${gradient}"
             style="width:100px; height:100px; border-radius:50%; background: #eee; position:relative; display:flex; align-items:center; justify-content:center;">
             <div style="width:60px; height:60px; background:var(--surface); border-radius:50%;"></div>
        </div>
    </div>
    
    <div class="card" style="background:var(--primary); color:white; display:flex; flex-direction:column; justify-content:space-between;">
        <div>
            <span class="material-symbols-outlined" style="font-size:32px;">info</span>
            <h4 style="font-size:18px; font-weight:700; margin-top:12px;">Note Administrative</h4>
            <p style="font-size:13px; opacity:0.8; margin-top:8px;">La période de réinscription se termine le 15 Septembre. Veillez à mettre à jour les effectifs définitifs.</p>
        </div>
        <a href="#" style="color:var(--primary-fixed); font-weight:700; text-decoration:none; display:flex; align-items:center; gap:8px; margin-top:16px;">
            En savoir plus <span class="material-symbols-outlined" style="font-size:16px;">arrow_forward</span>
        </a>
    </div>
</div>



<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
