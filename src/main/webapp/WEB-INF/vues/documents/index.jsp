<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Génération de Documents PDF" />
    <jsp:param name="active" value="documents" />
</jsp:include>

<div class="page-header">
    <div>
        <h2 class="page-title">Documents PDF</h2>
        <p class="page-subtitle">Bulletins, convocations et tableaux d'honneur</p>
    </div>
</div>

<div style="display:grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap:24px;">

    <!-- Carte Bulletin -->
    <div class="card" style="padding:24px;">
        <div style="display:flex; align-items:center; gap:12px; margin-bottom:20px;">
            <div style="width:40px; height:40px; border-radius:10px; background:rgba(21,101,192,0.1); color:var(--primary); display:flex; align-items:center; justify-content:center;">
                <span class="material-symbols-outlined">assignment</span>
            </div>
            <h3 style="font-size:18px; font-weight:700; color:var(--on-surface);">Bulletin Trimestriel</h3>
        </div>
        <form action="${pageContext.request.contextPath}/app/pdf/bulletin/" method="get" target="_blank">
            <div class="input-group">
                <label class="label" for="bulletin-eleve">Élève</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">person</span>
                    <select id="bulletin-eleve" name="eleveId" class="input-field" required>
                        <option value="">Sélectionner un élève...</option>
                        <c:forEach var="e" items="${eleves}">
                            <option value="${e.id}">${e.nom} ${e.prenom} (${e.matricule})</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="input-group">
                <label class="label" for="bulletin-trimestre">Trimestre</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">calendar_month</span>
                    <select id="bulletin-trimestre" name="trimestre" class="input-field" required>
                        <option value="1">1er Trimestre</option>
                        <option value="2">2ème Trimestre</option>
                        <option value="3">3ème Trimestre</option>
                    </select>
                </div>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%; justify-content:center;">
                <span class="material-symbols-outlined">download</span> Générer le Bulletin
            </button>
        </form>
    </div>

    <!-- Carte Bulletins par classe -->
    <div class="card" style="padding:24px;">
        <div style="display:flex; align-items:center; gap:12px; margin-bottom:20px;">
            <div style="width:40px; height:40px; border-radius:10px; background:rgba(99,102,241,0.1); color:var(--accent); display:flex; align-items:center; justify-content:center;">
                <span class="material-symbols-outlined">folder_zip</span>
            </div>
            <h3 style="font-size:18px; font-weight:700; color:var(--on-surface);">Bulletins par Classe (ZIP)</h3>
        </div>
        <form action="${pageContext.request.contextPath}/app/pdf/bulletin-classe" method="get">
            <div class="input-group">
                <label class="label" for="bulk-classe">Classe</label>
                <select id="bulk-classe" name="classeId" class="input-field" required>
                    <option value="">Sélectionner une classe...</option>
                    <c:forEach var="c" items="${classes}">
                        <option value="${c.id}">${c.libelle}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="input-group">
                <label class="label" for="bulk-trimestre">Trimestre</label>
                <select id="bulk-trimestre" name="trimestre" class="input-field" required>
                    <option value="1">1er Trimestre</option>
                    <option value="2">2ème Trimestre</option>
                    <option value="3">3ème Trimestre</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="width:100%; justify-content:center;">
                <span class="material-symbols-outlined">download</span> Télécharger tous les bulletins
            </button>
        </form>
    </div>

    <!-- Carte Tableau d'honneur -->
    <div class="card" style="padding:24px;">
        <div style="display:flex; align-items:center; gap:12px; margin-bottom:20px;">
            <div style="width:40px; height:40px; border-radius:10px; background:rgba(22,163,74,0.1); color:var(--success); display:flex; align-items:center; justify-content:center;">
                <span class="material-symbols-outlined">military_tech</span>
            </div>
            <h3 style="font-size:18px; font-weight:700; color:var(--on-surface);">Tableau d'Honneur</h3>
        </div>
        <form action="${pageContext.request.contextPath}/app/pdf/tableau-honneur" method="get" target="_blank">
            <div class="input-group">
                <label class="label" for="honneur-classe">Classe</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">meeting_room</span>
                    <select id="honneur-classe" name="classeId" class="input-field" required>
                        <option value="">Sélectionner une classe...</option>
                        <c:forEach var="c" items="${classes}">
                            <option value="${c.id}">${c.libelle}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="input-group">
                <label class="label" for="honneur-trimestre">Trimestre</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">calendar_month</span>
                    <select id="honneur-trimestre" name="trimestre" class="input-field" required>
                        <option value="1">1er Trimestre</option>
                        <option value="2">2ème Trimestre</option>
                        <option value="3">3ème Trimestre</option>
                    </select>
                </div>
            </div>
            <button type="submit" class="btn" style="width:100%; justify-content:center; background:var(--success); color:white;">
                <span class="material-symbols-outlined">download</span> Générer le Top 10
            </button>
        </form>
    </div>

    <!-- Carte Convocation -->
    <div class="card" style="padding:24px;">
        <div style="display:flex; align-items:center; gap:12px; margin-bottom:20px;">
            <div style="width:40px; height:40px; border-radius:10px; background:rgba(220,38,38,0.1); color:var(--error); display:flex; align-items:center; justify-content:center;">
                <span class="material-symbols-outlined">warning</span>
            </div>
            <h3 style="font-size:18px; font-weight:700; color:var(--on-surface);">Convocation Parent</h3>
        </div>
        <form action="${pageContext.request.contextPath}/app/pdf/convocation/" method="get" target="_blank">
            <div class="input-group">
                <label class="label" for="convo-eleve">Élève</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">person</span>
                    <select id="convo-eleve" name="eleveId" class="input-field" required>
                        <option value="">Sélectionner un élève...</option>
                        <c:forEach var="e" items="${eleves}">
                            <option value="${e.id}">${e.nom} ${e.prenom} (${e.matricule})</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="input-group">
                <label class="label" for="convo-motif">Motif (ex: Absences)</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">edit_note</span>
                    <input type="text" id="convo-motif" name="motif" class="input-field" value="Absences répétées non justifiées" required>
                </div>
            </div>
            <div class="input-group">
                <label class="label" for="convo-date">Date de Rendez-vous</label>
                <div class="input-wrapper">
                    <span class="material-symbols-outlined input-icon">event</span>
                    <input type="datetime-local" id="convo-date" name="date" class="input-field" required>
                </div>
            </div>
            <button type="submit" class="btn" style="width:100%; justify-content:center; background:var(--error); color:white;">
                <span class="material-symbols-outlined">download</span> Générer la Convocation
            </button>
        </form>
    </div>

</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
