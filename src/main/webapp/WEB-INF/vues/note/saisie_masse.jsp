<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/vues/layout/header.jsp" />

<div class="animate-fade">
    <!-- Breadcrumb & Header -->
    <div style="display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:32px;">
        <div>
            <div style="display:flex; align-items:center; gap:8px; color:var(--on-surface-variant); font-size:13px; margin-bottom:8px;">
                <span class="material-symbols-outlined" style="font-size:16px;">home</span>
                <a href="${pageContext.request.contextPath}/app/dashboard" style="color:inherit; text-decoration:none;">Dashboard</a>
                <span class="material-symbols-outlined" style="font-size:16px;">chevron_right</span>
                <a href="${pageContext.request.contextPath}/app/notes" style="color:inherit; text-decoration:none;">Notes</a>
                <span class="material-symbols-outlined" style="font-size:16px;">chevron_right</span>
                <span style="color:var(--primary); font-weight:600;">Saisie en Masse</span>
            </div>
            <h2 style="font-size:28px; font-weight:800; color:var(--primary); letter-spacing:-0.5px;">Saisie Groupée des Notes</h2>
            <p style="color:var(--on-surface-variant); margin-top:4px;">Remplissez les notes pour toute une classe en une seule fois.</p>
        </div>
    </div>

    <!-- Step 1: Selection du Niveau et de la Série - Nouveau Design épuré -->
    <div class="card mb-xl" style="padding:24px; border-radius:24px; box-shadow:0 10px 30px rgba(0,0,0,0.05); background:white; border:1px solid var(--outline-variant);">
        <form method="get" action="${pageContext.request.contextPath}/app/notes/nouveau" style="display:flex; align-items:center; gap:24px; flex-wrap:wrap;">
            
            <div style="flex:1; min-width:250px;">
                <label class="label" for="niveau-select" style="font-weight:700; font-size:11px; text-transform:uppercase; letter-spacing:1px; margin-bottom:8px; display:block; color:var(--on-surface-variant);">Niveau Scolaire</label>
                <div class="input-wrapper" style="height:52px; background:var(--surface-variant); border:none; border-radius:16px;">
                    <span class="material-symbols-outlined input-icon" style="color:var(--primary); font-size:20px;">stairs</span>
                    <select id="niveau-select" name="niveau" class="input-field" style="font-weight:600; color:var(--primary);" onchange="updateSerieOptions(this.value)" required>
                        <option value="">Choisir le niveau...</option>
                        <option value="6iem" ${param.niveau == '6iem' ? 'selected' : ''}>6ième</option>
                        <option value="5iem" ${param.niveau == '5iem' ? 'selected' : ''}>5ième</option>
                        <option value="4iem" ${param.niveau == '4iem' ? 'selected' : ''}>4ième</option>
                        <option value="3iem" ${param.niveau == '3iem' ? 'selected' : ''}>3ième</option>
                        <option value="2nde" ${param.niveau == '2nde' ? 'selected' : ''}>Seconde</option>
                        <option value="1ere" ${param.niveau == '1ere' ? 'selected' : ''}>Première</option>
                        <option value="Tle"  ${param.niveau == 'Tle' ? 'selected' : ''}>Terminale</option>
                    </select>
                </div>
            </div>

            <div id="serie-group" style="flex:1; min-width:180px;">
                <label class="label" for="serie-select" style="font-weight:700; font-size:11px; text-transform:uppercase; letter-spacing:1px; margin-bottom:8px; display:block; color:var(--on-surface-variant);">Série / Option</label>
                <div class="input-wrapper" style="height:52px; background:var(--surface-variant); border:none; border-radius:16px;">
                    <span class="material-symbols-outlined input-icon" style="color:var(--primary); font-size:20px;">category</span>
                    <select id="serie-select" name="serie" class="input-field" style="font-weight:600; color:var(--primary);">
                        <option value="">Toutes les séries</option>
                        <option value="Général" ${param.serie == 'Général' ? 'selected' : ''}>Général</option>
                        <option value="ALL" ${param.serie == 'ALL' ? 'selected' : ''}>Allemand</option>
                        <option value="ESP" ${param.serie == 'ESP' ? 'selected' : ''}>Espagnol</option>
                        <option value="CHS" ${param.serie == 'CHS' ? 'selected' : ''}>Chinois</option>
                        <option value="A" ${param.serie == 'A' ? 'selected' : ''}>Série A</option>
                        <option value="C" ${param.serie == 'C' ? 'selected' : ''}>Série C</option>
                        <option value="D" ${param.serie == 'D' ? 'selected' : ''}>Série D</option>
                        <option value="TI" ${param.serie == 'TI' ? 'selected' : ''}>Série TI</option>
                    </select>
                </div>
            </div>

            <div style="flex:0.5; min-width:120px;">
                <label class="label" for="salle-input" style="font-weight:700; font-size:11px; text-transform:uppercase; letter-spacing:1px; margin-bottom:8px; display:block; color:var(--on-surface-variant);">Salle</label>
                <div class="input-wrapper" style="height:52px; background:var(--surface-variant); border:none; border-radius:16px;">
                    <span class="material-symbols-outlined input-icon" style="color:var(--primary); font-size:20px;">meeting_room</span>
                    <select id="salle-input" name="salle" class="input-field" style="font-weight:600; color:var(--primary); appearance:none;" disabled>
                        <option value="">Toutes les salles</option>
                    </select>
                </div>
            </div>

            <div style="margin-top:22px;">
                <button type="submit" class="btn btn-primary" style="height:52px; padding:0 32px; border-radius:16px; font-weight:800; font-size:14px; box-shadow:var(--shadow-md); transition:all 0.3s cubic-bezier(0.4, 0, 0.2, 1);">
                    <span class="material-symbols-outlined" style="margin-right:10px; font-size:22px;">group</span>
                    Charger les élèves
                </button>
            </div>
        </form>
    </div>

    <script>
    document.addEventListener('DOMContentLoaded', () => {
        LyceeAdmin.initCascadeFilters({
            niveauId:     'niveau-select',
            serieId:      'serie-select',
            salleId:      'salle-input',
            serieGroupId: 'serie-group',
            currentNiveau: '${param.niveau}',
            currentSerie:  '${param.serie}',
            currentSalle:  '${salle}'
        });
    });
    </script>

    <c:if test="${not empty eleves}">
        <form action="${pageContext.request.contextPath}/app/notes/nouveau" method="post">
            <!-- Global Info Card -->
            <div class="card mb-xl" style="display:grid; grid-template-columns: repeat(4, 1fr); gap:20px;">
                <div class="input-group">
                    <label class="label" for="matiere">Matière</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">book</span>
                        <input type="text" id="matiere" name="matiere" class="input-field" placeholder="Nom de la matière" required>
                    </div>
                </div>
                <div class="input-group">
                    <label class="label" for="profSaisie">Nom du Professeur</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">person</span>
                        <input type="text" id="profSaisie" name="profSaisie" class="input-field" placeholder="Nom complet" required>
                    </div>
                </div>
                <div class="input-group">
                    <label class="label" for="trimestre">Trimestre</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">event</span>
                        <select id="trimestre" name="trimestre" class="input-field">
                            <option value="1">1er Trimestre</option>
                            <option value="2">2ème Trimestre</option>
                            <option value="3">3ème Trimestre</option>
                        </select>
                    </div>
                </div>
                <div class="input-group">
                    <label class="label" for="coefficient">Coefficient</label>
                    <div class="input-wrapper">
                        <span class="material-symbols-outlined input-icon">calculate</span>
                        <input type="number" id="coefficient" name="coefficient" class="input-field" value="1" min="1" max="10">
                    </div>
                </div>
            </div>

            <!-- Student List Table -->
            <div class="card" style="padding:0; overflow:hidden;">
                <table class="table" style="margin:0;">
                    <thead>
                        <tr>
                            <th style="padding-left:24px;">Matricule</th>
                            <th>Nom & Prénom</th>
                            <th style="width:200px; text-align:right; padding-right:24px;">Note (0 - 20)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="e" items="${eleves}">
                            <tr>
                                <td style="padding-left:24px;">
                                    <span style="font-family:monospace; background:var(--surface-variant); padding:4px 8px; border-radius:4px; font-size:12px; font-weight:700;">
                                        ${e.matricule}
                                    </span>
                                </td>
                                <td>
                                    <div style="font-weight:700; color:var(--primary);">${e.nom}</div>
                                    <div style="font-size:12px; color:var(--on-surface-variant);">${e.prenom}</div>
                                </td>
                                <td style="padding-right:24px; text-align:right;">
                                    <input type="hidden" name="eleveIds" value="${e.id}">
                                    <div class="input-wrapper" style="display:inline-flex; width:120px; border-radius:10px;">
                                        <input type="number" step="0.25" min="0" max="20" name="note_${e.id}" 
                                               class="input-field" placeholder="--" 
                                               style="text-align:center; font-weight:800; color:var(--primary); font-size:16px;">
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div style="background:var(--surface); padding:24px; display:flex; justify-content:flex-end; gap:16px; border-top:1px solid var(--outline-variant);">
                    <button type="reset" class="btn" style="background:var(--surface-variant); color:var(--on-surface-variant); font-weight:600;">Réinitialiser</button>
                    <button type="submit" class="btn btn-primary" style="padding:0 40px; font-weight:800; height:48px; border-radius:12px; box-shadow:var(--shadow-md);">
                        <span class="material-symbols-outlined" style="font-size:20px; margin-right:8px;">save</span>
                        Enregistrer toutes les notes
                    </button>
                </div>
            </div>
        </form>
    </c:if>
    
    <c:if test="${empty eleves and not empty param.classeId}">
        <div class="card text-center" style="padding:60px 20px;">
            <span class="material-symbols-outlined" style="font-size:64px; color:var(--outline-variant);">group_off</span>
            <h3 style="margin-top:16px; color:var(--primary);">Aucun élève trouvé</h3>
            <p style="color:var(--on-surface-variant);">Assurez-vous que la classe sélectionnée contient des élèves.</p>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
