<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="${classe.id == null ? 'Nouvelle Classe' : 'Modification Classe'}" />
    <jsp:param name="active" value="classes" />
</jsp:include>

<div style="max-width:800px; margin:0 auto;">
    <!-- Breadcrumbs & Header -->
    <div style="margin-bottom:32px;">
        <div style="display:flex; align-items:center; gap:8px; color:var(--on-surface-variant); font-size:12px; font-weight:600; margin-bottom:8px; text-transform:uppercase; letter-spacing:0.05em;">
            <a href="${pageContext.request.contextPath}/app/classes/" style="text-decoration:none; color:inherit;">Classes</a>
            <span class="material-symbols-outlined" style="font-size:16px;">chevron_right</span>
            <span style="color:var(--primary);">${classe.id == null ? 'Nouvelle Classe' : 'Modification'}</span>
        </div>
        <h2 style="font-size:32px; font-weight:700; color:var(--primary); letter-spacing:-0.02em;">
            ${classe.id == null ? "Créer une nouvelle classe" : "Modifier la classe"}
        </h2>
        <p style="color:var(--on-surface-variant); font-size:14px;">Définissez les paramètres académiques, l'enseignant responsable et la capacité d'accueil.</p>
    </div>

    <form action="${pageContext.request.contextPath}/app/classes/${classe.id == null ? 'nouveau' : 'modifier/'.concat(classe.id)}" method="post">
        <div class="card" style="border-top:4px solid var(--primary); padding:32px; display:flex; flex-direction:column; gap:24px;">
            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:24px;">
                <!-- Niveau & Série -->
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="niveau" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Niveau / Grade</label>
                    <select id="niveau" name="niveau" class="input-field" style="margin-top:0;" onchange="updateSeriesOptions(this.value)">
                        <option value="">Sélectionner le niveau</option>
                        <option value="6iem" ${classe.niveau == '6iem' ? 'selected' : ''}>6ième</option>
                        <option value="5iem" ${classe.niveau == '5iem' ? 'selected' : ''}>5ième</option>
                        <option value="4iem" ${classe.niveau == '4iem' ? 'selected' : ''}>4ième</option>
                        <option value="3iem" ${classe.niveau == '3iem' ? 'selected' : ''}>3ième</option>
                        <option value="2nde" ${classe.niveau == '2nde' ? 'selected' : ''}>Seconde</option>
                        <option value="1ere" ${classe.niveau == '1ere' ? 'selected' : ''}>Première</option>
                        <option value="Tle"  ${classe.niveau == 'Tle'  ? 'selected' : ''}>Terminale</option>
                    </select>
                    <c:if test="${not empty errors.niveau}">
                        <span style="color:var(--error); font-size:11px; font-weight:600;">${errors.niveau}</span>
                    </c:if>
                </div>

                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="serie" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Série / Spécialité</label>
                    <select id="serie" name="serie" class="input-field" style="margin-top:0;">
                        <option value="">Sélectionner la série</option>
                        <option value="Général" ${classe.serie == 'Général' ? 'selected' : ''}>Général (Aucune)</option>
                        <option value="A" ${classe.serie == 'A' ? 'selected' : ''}>Série A (Littéraire)</option>
                        <option value="C" ${classe.serie == 'C' ? 'selected' : ''}>Série C (Mathématiques)</option>
                        <option value="D" ${classe.serie == 'D' ? 'selected' : ''}>Série D (Sciences Vie)</option>
                        <option value="ALL" ${classe.serie == 'ALL' ? 'selected' : ''}>Allemand (ALL)</option>
                        <option value="Esp" ${classe.serie == 'Esp' ? 'selected' : ''}>Espagnol (ESP)</option>
                        <option value="Chs" ${classe.serie == 'Chs' ? 'selected' : ''}>Chinois (CHS)</option>
                        <option value="TI" ${classe.serie == 'TI' ? 'selected' : ''}>Informatique (TI)</option>
                    </select>
                    <c:if test="${not empty errors.serie}">
                        <span style="color:var(--error); font-size:11px; font-weight:600;">${errors.serie}</span>
                    </c:if>
                </div>

                <!-- Responsable & Salle -->
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="profPrincipal" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Professeur Principal</label>
                    <div style="position:relative;">
                        <span class="material-symbols-outlined" style="position:absolute; left:12px; top:50%; transform:translateY(-50%); color:var(--outline); font-size:20px;">person</span>
                        <input id="profPrincipal" name="profPrincipal" type="text" class="input-field" style="padding-left:40px; margin-top:0;" value="${classe.profPrincipal}" placeholder="Nom de l'enseignant">
                    </div>
                </div>

                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="sallePrincipale" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Salle de classe</label>
                    <div style="position:relative;">
                        <span class="material-symbols-outlined" style="position:absolute; left:12px; top:50%; transform:translateY(-50%); color:var(--outline); font-size:20px;">meeting_room</span>
                        <input id="sallePrincipale" name="sallePrincipale" type="text" class="input-field" style="padding-left:40px; margin-top:0;" value="${classe.sallePrincipale}" placeholder="Ex: Salle B-102">
                    </div>
                </div>

                <!-- Effectif & Année -->
                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="effectifMax" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Capacité maximale (Élèves)</label>
                    <input id="effectifMax" name="effectifMax" type="number" class="input-field" style="margin-top:0;" value="${classe.effectifMax != 0 ? classe.effectifMax : 60}" min="1" max="100">
                </div>

                <div style="display:flex; flex-direction:column; gap:8px;">
                    <label for="anneeScolaire" style="font-size:12px; font-weight:700; color:var(--on-surface-variant);">Année Académique</label>
                    <select id="anneeScolaire" name="anneeScolaire" class="input-field" style="margin-top:0;">
                        <option value="2023-2024" ${classe.anneeScolaire == '2023-2024' ? 'selected' : ''}>2023-2024</option>
                        <option value="2024-2025" ${classe.anneeScolaire == '2024-2025' ? 'selected' : ''}>2024-2025</option>
                        <option value="2025-2026" ${classe.anneeScolaire == '2025-2026' ? 'selected' : ''}>2025-2026</option>
                    </select>
                </div>
            </div>

            <!-- Footer Actions -->
            <div style="display:flex; justify-content:flex-end; gap:16px; margin-top:16px; padding-top:24px; border-top:1px solid var(--outline-variant);">
                <a href="${pageContext.request.contextPath}/app/classes/" class="btn" style="text-decoration:none; background:var(--bg-main); border:1px solid var(--outline-variant); color:var(--on-surface-variant);">Annuler</a>
                <button type="submit" class="btn btn-primary" style="padding:12px 32px;">
                    <span class="material-symbols-outlined">save</span>
                    ${classe.id == null ? 'Créer la classe' : 'Enregistrer les modifications'}
                </button>
            </div>
        </div>
    </form>
</div>

<script>
function updateSeriesOptions(niveau) {
    const serieSelect = document.getElementById('serie');
    const options = serieSelect.options;
    
    for (let i = 1; i < options.length; i++) {
        const val = options[i].value;
        let show = false;
        
        if (niveau === '6iem' || niveau === '5iem') {
            show = (val === 'Général');
        } else if (niveau === '4iem' || niveau === '3iem') {
            show = (['ALL', 'Esp', 'Chs'].includes(val));
        } else if (niveau === '2nde') {
            show = (['A', 'C'].includes(val));
        } else if (niveau === '1ere' || niveau === 'Tle') {
            show = (['A', 'C', 'D', 'TI'].includes(val));
        } else {
            show = true;
        }
        
        options[i].style.display = show ? 'block' : 'none';
        if (!show && options[i].selected) {
            serieSelect.value = "";
        }
    }
}
// Initial run
document.addEventListener('DOMContentLoaded', () => {
    updateSeriesOptions(document.getElementById('niveau').value);
});
</script>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
