<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="${absence.id == null ? 'Signaler une Absence' : 'Modifier Absence'}" />
    <jsp:param name="active" value="absences" />
</jsp:include>

<div style="max-width:700px; margin:0 auto;">
    <!-- Breadcrumb -->
    <div style="margin-bottom:32px;">
        <div style="display:flex; align-items:center; gap:8px; color:var(--on-surface-variant); font-size:12px; font-weight:600; margin-bottom:8px; text-transform:uppercase; letter-spacing:0.05em;">
            <a href="${pageContext.request.contextPath}/app/absences/" style="text-decoration:none; color:inherit;">Absences</a>
            <span class="material-symbols-outlined" style="font-size:16px;">chevron_right</span>
            <span style="color:var(--primary);">${absence.id == null ? 'Nouvelle Absence' : 'Modification'}</span>
        </div>
        <h2 style="font-size:32px; font-weight:700; color:var(--primary); letter-spacing:-0.02em;">
            ${absence.id == null ? 'Signaler une Absence' : 'Modifier l\'Absence'}
        </h2>
        <p style="color:var(--on-surface-variant); font-size:14px;">Enregistrez les informations de l'absence pour le suivi de l'assiduité scolaire.</p>
    </div>

    <form action="${pageContext.request.contextPath}/app/absences/${absence.id == null ? 'nouveau' : 'modifier/'.concat(absence.id)}" method="post">
        <div class="card" style="border-top:4px solid var(--primary); display:flex; flex-direction:column; gap:24px;">

            <!-- Élève -->
            <div style="display:flex; flex-direction:column; gap:8px;">
                <label for="eleveId" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Élève <span style="color:var(--error);">*</span></label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">person</span>
                    <select id="eleveId" name="eleveId" class="input-field ${not empty errors.eleveId ? 'border-error' : ''}">
                        <option value="">Sélectionner un élève...</option>
                        <c:forEach var="e" items="${eleves}">
                            <option value="${e.id}" ${absence.eleveId == e.id ? 'selected' : ''}>${e.labelComplet}</option>
                        </c:forEach>
                    </select>
                </div>
                <c:if test="${not empty errors.eleveId}">
                    <span style="color:var(--error); font-size:11px; font-weight:600;">${errors.eleveId}</span>
                </c:if>
            </div>

            <!-- Date + Durée -->
            <div style="display:grid; grid-template-columns:2fr 1fr; gap:16px;">
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="dateAbsence" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Date d'absence <span style="color:var(--error);">*</span></label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">calendar_today</span>
                        <input id="dateAbsence" name="dateAbsence" type="date"
                               class="input-field ${not empty errors.dateAbsence ? 'border-error' : ''}"
                               value="${not empty absence.dateAbsence ? absence.dateAbsence : ''}">
                    </div>
                    <c:if test="${not empty errors.dateAbsence}">
                        <span style="color:var(--error); font-size:11px; font-weight:600;">${errors.dateAbsence}</span>
                    </c:if>
                </div>
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="dureeHeures" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Durée (heures)</label>
                    <input id="dureeHeures" name="dureeHeures" type="number"
                           class="input-field" style="padding-left:16px;"
                           value="${not empty absence.dureeHeures ? absence.dureeHeures : 1}" min="1" max="8">
                </div>
            </div>

            <!-- Matière -->
            <div style="display:flex; flex-direction:column; gap:8px;">
                <label for="matiere" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Matière concernée</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">book</span>
                    <input id="matiere" name="matiere" type="text" class="input-field"
                           value="${absence.matiere}" placeholder="Ex: Mathématiques (optionnel)">
                </div>
            </div>

            <!-- Justifiée -->
            <div style="display:flex; flex-direction:column; gap:8px;">
                <span style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Absence justifiée ?</span>
                <div style="display:flex; gap:16px;">
                    <label for="justif-non" style="flex:1; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; border:1px solid var(--outline-variant); border-radius:10px; cursor:pointer; font-size:13px; font-weight:600; color:var(--on-surface);">
                        <input id="justif-non" type="radio" name="justifiee" value="false" ${not absence.justifiee ? 'checked' : ''}> Non justifiée
                    </label>
                    <label for="justif-oui" style="flex:1; display:flex; align-items:center; justify-content:center; gap:8px; padding:12px; border:1px solid var(--outline-variant); border-radius:10px; cursor:pointer; font-size:13px; font-weight:600; color:var(--on-surface);">
                        <input id="justif-oui" type="radio" name="justifiee" value="on" ${absence.justifiee ? 'checked' : ''}> Justifiée
                    </label>
                </div>
            </div>

            <!-- Motif -->
            <div style="display:flex; flex-direction:column; gap:8px;">
                <label for="motif" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Motif (si justifiée)</label>
                <textarea id="motif" name="motif" class="input-field" rows="3"
                          style="resize:vertical; padding:12px 16px; height:auto; font-family:inherit;"
                          placeholder="Indiquez le motif si l'absence est justifiée...">${absence.motif}</textarea>
            </div>

            <!-- Actions -->
            <div style="display:flex; justify-content:flex-end; gap:16px; padding-top:16px; border-top:1px solid var(--outline-variant);">
                <a href="${pageContext.request.contextPath}/app/absences/" class="btn"
                   style="text-decoration:none; background:var(--bg-main); border:1px solid var(--outline-variant); color:var(--on-surface-variant);">Annuler</a>
                <button type="submit" class="btn btn-primary" style="padding:12px 32px;">
                    <span class="material-symbols-outlined">save</span>
                    ${absence.id == null ? 'Enregistrer l\'absence' : 'Mettre à jour'}
                </button>
            </div>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
