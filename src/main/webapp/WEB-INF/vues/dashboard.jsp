<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Tableau de Bord" />
    <jsp:param name="active" value="dashboard" />
</jsp:include>

<div class="page-header">
    <div>
        <h2 class="page-title">Vue d'ensemble</h2>
        <p class="page-subtitle">Trimestre ${trimestreCourant} — ${nbClasses} classes, ${nbEleves} élèves inscrits</p>
    </div>
    <div style="display:flex; gap:12px;">
        <c:if test="${role == 'Admin' || role == 'Censeur'}">
            <div class="dropdown" style="position:relative;">
                <button class="btn btn-ghost btn-sm dropdown-trigger" style="text-decoration:none;">
                    <span class="material-symbols-outlined">file_download</span>
                    Exporter
                </button>
                <div class="dropdown-menu" style="display:none; position:absolute; top:100%; right:0; min-width:180px; background:var(--bg-card); border:1px solid var(--outline-variant); border-radius:12px; box-shadow:var(--shadow-lg); z-index:50; margin-top:4px;">
                    <a href="${pageContext.request.contextPath}/app/eleves/export-csv" class="dropdown-item" style="display:flex; align-items:center; gap:8px; padding:10px 16px; text-decoration:none; color:var(--on-surface);">
                        <span class="material-symbols-outlined" style="font-size:18px;">table</span> CSV
                    </a>
                    <a href="${pageContext.request.contextPath}/app/excel/eleves" class="dropdown-item" style="display:flex; align-items:center; gap:8px; padding:10px 16px; text-decoration:none; color:var(--on-surface);">
                        <span class="material-symbols-outlined" style="font-size:18px;">grid_on</span> Excel (.xlsx)
                    </a>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/app/notes/nouveau" class="btn btn-primary btn-sm" style="text-decoration:none;">
                <span class="material-symbols-outlined">add</span>
                Nouvelle saisie
            </a>
        </c:if>
    </div>
</div>

<c:set var="garconsPct" value="${nbEleves > 0 ? nbGarcons * 100 / nbEleves : 0}" />
<style>
    .gender-donut {
        width: 50px; height: 50px; border-radius: 50%;
        background: conic-gradient(#2563eb 0% ${garconsPct}%, #db2777 ${garconsPct}% 100%);
        display: flex; align-items: center; justify-content: center;
    }
</style>

<div class="stats-grid">
    <div class="stat-card" style="border-top-color: var(--accent);">
        <div class="stat-header">
            <div class="stat-icon-box" style="background: var(--grad-accent); color: white;">
                <span class="material-symbols-outlined">groups</span>
            </div>
            <span class="stat-label-mini">Effectif total</span>
        </div>
        <div style="display:flex; align-items:center; justify-content:space-between; margin-top:12px;">
            <div>
                <h3 style="font-size:32px; font-weight:800; color:var(--primary);">${nbEleves}</h3>
                <p style="font-size:12px; color:var(--on-surface-variant);">Élèves inscrits</p>
            </div>
            <div class="gender-donut"><div style="width:30px;height:30px;background:white;border-radius:50%;"></div></div>
        </div>
        <div style="margin-top:20px; display:flex; gap:12px;">
            <div style="flex:1; background:var(--surface); padding:8px; border-radius:8px; border:1px solid rgba(37,99,235,0.1);">
                <p style="font-size:10px; color:#2563eb; font-weight:700; text-transform:uppercase;">Garçons</p>
                <p style="font-size:16px; font-weight:800; color:var(--primary);">${nbGarcons}</p>
            </div>
            <div style="flex:1; background:var(--surface); padding:8px; border-radius:8px; border:1px solid rgba(219,39,119,0.1);">
                <p style="font-size:10px; color:#db2777; font-weight:700; text-transform:uppercase;">Filles</p>
                <p style="font-size:16px; font-weight:800; color:var(--primary);">${nbFilles}</p>
            </div>
        </div>
    </div>

    <div class="stat-card" style="border-top-color: var(--info);">
        <div class="stat-header">
            <div class="stat-icon-box" style="background: var(--grad-primary); color: white;">
                <span class="material-symbols-outlined">auto_graph</span>
            </div>
            <span class="stat-label-mini">Performance</span>
        </div>
        <h3 style="font-size:28px; font-weight:800; color:var(--primary);">${moyGlobale} / 20</h3>
        <p style="font-size:12px; color:var(--on-surface-variant); margin-top:4px;">Moyenne générale établissement</p>
        <div class="trend-indicator" style="color:var(--on-surface-variant); border-top-style: dashed;">
            <span class="material-symbols-outlined" style="font-size:16px;">info</span>
            T${trimestreCourant} — toutes matières confondues
        </div>
    </div>

    <div class="stat-card" style="border-top-color: var(--warning);">
        <div class="stat-header">
            <div class="stat-icon-box" style="background: linear-gradient(135deg, var(--warning) 0%, #d97706 100%); color: white;">
                <span class="material-symbols-outlined">person_off</span>
            </div>
            <span class="stat-label-mini">Présence</span>
        </div>
        <h3 style="font-size:28px; font-weight:800; color:var(--primary);">${tauxAbsenteeisme}%</h3>
        <p style="font-size:12px; color:var(--on-surface-variant); margin-top:4px;">Taux d'absentéisme global</p>
        <div style="margin-top:16px;">
            <div style="width:100%; height:6px; background:var(--outline-variant); border-radius:10px; overflow:hidden;">
                <div style="width:${tauxAbsBarWidth}%; height:100%; background:var(--warning);"></div>
            </div>
            <p style="font-size:10px; color:var(--on-surface-variant); margin-top:8px;">
                ${nbAbsences} absence(s) enregistrée(s) — ${totalAbsH}h injustifiées
            </p>
        </div>
    </div>

    <div class="stat-card" style="border-top-color: var(--error);">
        <div class="stat-header">
            <div class="stat-icon-box" style="background: linear-gradient(135deg, var(--error) 0%, #b91c1c 100%); color: white;">
                <span class="material-symbols-outlined">dangerous</span>
            </div>
            <span class="stat-label-mini">Alertes</span>
        </div>
        <h3 style="font-size:28px; font-weight:800; color:var(--error);">${tauxEchecPire}%</h3>
        <p style="font-size:12px; color:var(--on-surface-variant); margin-top:4px;">Taux d'échec — ${pireMatiere}</p>
        <div class="trend-indicator" style="color:var(--error); border-top-style: dashed;">
            <span class="material-symbols-outlined" style="font-size:16px;">priority_high</span>
            Matière la plus critique (T${trimestreCourant})
        </div>
    </div>
</div>

<div style="display:grid; grid-template-columns: 2fr 1fr; gap:24px;">
    <c:if test="${role == 'Admin' || role == 'Censeur'}">
    <div class="card" style="padding:0; overflow:hidden;">
        <div style="padding:20px 24px; border-bottom:1px solid var(--outline-variant); display:flex; justify-content:space-between; align-items:center;">
            <div>
                <h3 style="font-size:18px; font-weight:700; color:var(--primary);">Dernières notes</h3>
                <p style="font-size:11px; color:var(--on-surface-variant);">Dernières saisies en base</p>
            </div>
            <a href="${pageContext.request.contextPath}/app/notes/" class="btn btn-ghost btn-sm" style="text-decoration:none;">Voir tout</a>
        </div>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Élève</th>
                    <th>Matière</th>
                    <th style="text-align:right;">Note</th>
                    <th style="text-align:center;">Statut</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="n" items="${dernieresNotes}">
                    <tr>
                        <td>
                            <div style="display:flex; align-items:center; gap:12px;">
                                <div style="width:36px;height:36px;border-radius:10px;background:var(--grad-primary);display:flex;align-items:center;justify-content:center;font-weight:700;color:white;font-size:11px;">
                                    ${n.initiales}
                                </div>
                                <div>
                                    <p style="font-weight:700; font-size:14px;">${n.nom} ${n.prenom}</p>
                                    <p style="font-size:10px; color:var(--on-surface-variant);">${n.classeLibelle}</p>
                                </div>
                            </div>
                        </td>
                        <td style="font-size:13px; color:var(--on-surface-variant);">${n.matiere}</td>
                        <td style="text-align:right;">
                            <span style="font-weight:800; color:var(--primary);">${n.noteAff}</span><span style="font-size:11px;color:var(--outline);">/20</span>
                        </td>
                        <td style="text-align:center;">
                            <span class="status-badge status-${n.statut}">${n.statutLabel}</span>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty dernieresNotes}">
                    <tr><td colspan="4" style="text-align:center;padding:32px;color:var(--on-surface-variant);">Aucune note enregistrée.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
    </c:if>

    <c:if test="${role == 'Surveillant'}">
    <div class="card" style="padding:24px;">
        <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:12px;">Accès rapide</h3>
        <p style="font-size:13px; color:var(--on-surface-variant); margin-bottom:20px;">En tant que surveillant, vous gérez principalement les absences et consultez les effectifs.</p>
        <div style="display:flex; flex-direction:column; gap:10px;">
            <a href="${pageContext.request.contextPath}/app/absences/" class="btn btn-primary" style="text-decoration:none; justify-content:center;">Gérer les absences</a>
            <a href="${pageContext.request.contextPath}/app/eleves/" class="btn btn-ghost" style="text-decoration:none; justify-content:center;">Consulter les élèves</a>
            <a href="${pageContext.request.contextPath}/app/classes/" class="btn btn-ghost" style="text-decoration:none; justify-content:center;">Consulter les classes</a>
        </div>
    </div>
    </c:if>

    <div class="card" style="border-top:4px solid var(--primary);">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
            <h3 style="font-size:18px; font-weight:700; color:var(--primary);">Absences mensuelles</h3>
            <span style="font-size:10px; font-weight:700; color:var(--primary); letter-spacing:0.05em; padding:4px 10px; border:1px solid var(--outline-variant); border-radius:20px;">5 MOIS</span>
        </div>
        <canvas id="absencesChart" style="height:160px; width:100%;"></canvas>
        <div style="margin-top:20px; background:var(--grad-primary); border-radius:12px; padding:16px; color:white;">
            <div style="display:flex; justify-content:space-between; align-items:center;">
                <p style="font-size:11px; font-weight:600; opacity:0.85;">Heures injustifiées (total)</p>
                <span style="font-size:18px; font-weight:800;">${totalAbsH} h</span>
            </div>
        </div>
    </div>
</div>

<div class="charts-grid" style="display:grid; grid-template-columns:1fr 1fr; gap:24px; margin-top:24px;">
    <div class="card" style="padding:20px 24px;">
        <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:16px;">Moyennes par classe</h3>
        <canvas id="moyennesChart" style="height:250px; width:100%;"></canvas>
    </div>
    <div class="card" style="padding:20px 24px;">
        <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:16px;">Répartition des décisions</h3>
        <canvas id="repartitionChart" style="height:250px; width:100%;"></canvas>
    </div>
</div>

<c:if test="${not empty absenteismeParClasse}">
<div class="card" style="margin-top:24px; padding:0; overflow:hidden;">
    <div style="padding:20px 24px; border-bottom:1px solid var(--outline-variant);">
        <h3 style="font-size:18px; font-weight:700; color:var(--primary);">Absentéisme par classe</h3>
    </div>
    <table class="data-table">
        <thead>
            <tr>
                <th>Classe</th>
                <th style="text-align:center;">Absences</th>
                <th style="text-align:center;">Heures</th>
                <th style="text-align:right;">Taux</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="cl" items="${absenteismeParClasse}">
                <tr>
                    <td style="font-weight:600;">${cl.niveau} ${cl.serie}</td>
                    <td style="text-align:center;">${cl.nbAbsences}</td>
                    <td style="text-align:center;">${cl.heuresAbs}h</td>
                    <td style="text-align:right; font-weight:700;
                        <c:choose>
                            <c:when test="${cl.taux > 8}">color:var(--error);</c:when>
                            <c:otherwise>color:var(--primary);</c:otherwise>
                        </c:choose>">${cl.taux}%</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</c:if>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
