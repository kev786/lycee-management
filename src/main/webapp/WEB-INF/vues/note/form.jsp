<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="${note.id == null ? 'Nouvelle Note' : 'Modifier Note'}" />
    <jsp:param name="active" value="notes" />
</jsp:include>

<div style="max-width:700px; margin:0 auto;">
    <!-- Breadcrumb -->
    <div style="margin-bottom:32px;">
        <div style="display:flex; align-items:center; gap:8px; color:var(--on-surface-variant); font-size:12px; font-weight:600; margin-bottom:8px; text-transform:uppercase; letter-spacing:0.05em;">
            <a href="${pageContext.request.contextPath}/app/notes/" style="text-decoration:none; color:inherit;">Notes</a>
            <span class="material-symbols-outlined" style="font-size:16px;">chevron_right</span>
            <span style="color:var(--primary);">${note.id == null ? 'Nouvelle Note' : 'Modification'}</span>
        </div>
        <h2 style="font-size:32px; font-weight:700; color:var(--primary); letter-spacing:-0.02em;">
            ${note.id == null ? 'Saisir une Note' : 'Modifier la Note'}
        </h2>
        <p style="color:var(--on-surface-variant); font-size:14px;">Renseignez les informations de l'évaluation pour l'enregistrer dans le bulletin.</p>
    </div>

    <form action="${pageContext.request.contextPath}/app/notes/${note.id == null ? 'nouveau' : 'modifier/'.concat(note.id)}" method="post">
        <div class="card" style="border-top:4px solid var(--primary); display:flex; flex-direction:column; gap:24px;">

            <!-- Élève -->
            <div style="display:flex; flex-direction:column; gap:8px;">
                <label for="eleveId" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Élève <span style="color:var(--error);">*</span></label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">person</span>
                    <select id="eleveId" name="eleveId" class="input-field ${not empty errors.eleveId ? 'border-error' : ''}">
                        <option value="">Sélectionner un élève...</option>
                        <c:forEach var="e" items="${eleves}">
                            <option value="${e.id}" ${note.eleveId == e.id ? 'selected' : ''}>${e.labelComplet}</option>
                        </c:forEach>
                    </select>
                </div>
                <c:if test="${not empty errors.eleveId}">
                    <span style="color:var(--error); font-size:11px; font-weight:600;">${errors.eleveId}</span>
                </c:if>
            </div>

            <!-- Matière + Coefficient -->
            <div style="display:grid; grid-template-columns:2fr 1fr; gap:16px;">
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="matiere" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Matière <span style="color:var(--error);">*</span></label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">book</span>
                        <input id="matiere" name="matiere" type="text" class="input-field ${not empty errors.matiere ? 'border-error' : ''}"
                               value="${note.matiere}" placeholder="Ex: Mathématiques">
                    </div>
                    <c:if test="${not empty errors.matiere}">
                        <span style="color:var(--error); font-size:11px; font-weight:600;">${errors.matiere}</span>
                    </c:if>
                </div>
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="coefficient" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Coefficient</label>
                    <input id="coefficient" name="coefficient" type="number" class="input-field"
                           value="${not empty note.coefficient ? note.coefficient : 1}" min="1" max="10" style="padding-left:16px;">
                </div>
            </div>

            <!-- Note + Trimestre -->
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="notesValeur" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Note (sur 20) <span style="color:var(--error);">*</span></label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">grade</span>
                        <input id="notesValeur" name="notesValeur" type="number" class="input-field ${not empty errors.notesValeur ? 'border-error' : ''}"
                               value="${note.notesValeur}" step="0.25" min="0" max="20" placeholder="0.00 - 20.00">
                    </div>
                    <c:if test="${not empty errors.notesValeur}">
                        <span style="color:var(--error); font-size:11px; font-weight:600;">${errors.notesValeur}</span>
                    </c:if>
                </div>
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="trimestre" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Trimestre</label>
                    <select id="trimestre" name="trimestre" class="input-field" style="padding-left:16px;">
                        <option value="1" ${note.trimestre == 1 ? 'selected' : ''}>1er Trimestre</option>
                        <option value="2" ${note.trimestre == 2 ? 'selected' : ''}>2ème Trimestre</option>
                        <option value="3" ${note.trimestre == 3 ? 'selected' : ''}>3ème Trimestre</option>
                    </select>
                </div>
            </div>

            <!-- Professeur -->
            <div style="display:flex; flex-direction:column; gap:8px;">
                <label for="profSaisie" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Professeur saisissant</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">school</span>
                    <input id="profSaisie" name="profSaisie" type="text" class="input-field"
                           value="${note.profSaisie}" placeholder="Nom du professeur (optionnel)">
                </div>
            </div>

            <!-- Actions -->
            <div style="display:flex; justify-content:flex-end; gap:16px; padding-top:16px; border-top:1px solid var(--outline-variant);">
                <a href="${pageContext.request.contextPath}/app/notes/" class="btn"
                   style="text-decoration:none; background:var(--bg-main); border:1px solid var(--outline-variant); color:var(--on-surface-variant);">Annuler</a>
                <button type="submit" class="btn btn-primary" style="padding:12px 32px;">
                    <span class="material-symbols-outlined">save</span>
                    ${note.id == null ? 'Enregistrer la note' : 'Mettre à jour'}
                </button>
            </div>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
