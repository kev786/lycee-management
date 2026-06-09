<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Paramètres Système" />
    <jsp:param name="active" value="settings" />
</jsp:include>

<div style="margin-bottom:24px;">
    <h2 style="font-size:32px; font-weight:700; color:var(--primary);">Configuration de l'établissement</h2>
    <p style="color:var(--on-surface-variant); font-size:14px; margin-top:4px;">
        Ces paramètres alimentent les bulletins, convocations et tableaux d'honneur PDF.
    </p>
</div>

<c:if test="${not empty erreur}">
    <div class="alert alert-error" style="margin-bottom:16px;">${erreur}</div>
</c:if>
<c:if test="${param.msg eq 'enregistre'}">
    <div class="alert alert-success" style="margin-bottom:16px;">Paramètres enregistrés avec succès.</div>
</c:if>

<form action="${pageContext.request.contextPath}/app/parametres" method="post" enctype="multipart/form-data" style="display:flex; flex-direction:column; gap:24px;">

    <div class="card" style="padding:24px;">
        <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:20px;">Informations générales</h3>
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:20px;">
            <div class="input-group">
                <label class="label" for="etablissement">Nom de l'établissement</label>
                <input class="input-field" id="etablissement" name="etablissement" type="text"
                       value="${etablissement.etablissement}" required/>
            </div>
            <div class="input-group">
                <label class="label" for="anneeScolaire">Année scolaire</label>
                <input class="input-field" id="anneeScolaire" name="anneeScolaire" type="text"
                       value="${etablissement.anneeScolaire}" required/>
            </div>
            <div class="input-group">
                <label class="label" for="devise">Devise</label>
                <input class="input-field" id="devise" name="devise" type="text" value="${etablissement.devise}"/>
            </div>
            <div class="input-group">
                <label class="label" for="ville">Ville</label>
                <input class="input-field" id="ville" name="ville" type="text" value="${etablissement.ville}"/>
            </div>
            <div class="input-group">
                <label class="label" for="telephone">Téléphone</label>
                <input class="input-field" id="telephone" name="telephone" type="text" value="${etablissement.telephone}"/>
            </div>
            <div class="input-group">
                <label class="label" for="email">Email</label>
                <input class="input-field" id="email" name="email" type="email" value="${etablissement.email}"/>
            </div>
            <div class="input-group">
                <label class="label" for="siteWeb">Site web</label>
                <input class="input-field" id="siteWeb" name="siteWeb" type="text" value="${etablissement.siteWeb}"/>
            </div>
        </div>
    </div>

    <div class="card" style="padding:24px;">
        <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:20px;">En-tête PDF</h3>
        <div class="input-group" style="margin-bottom:16px;">
            <label class="label" for="entetePdf">En-tête multiligne</label>
            <textarea class="input-field" id="entetePdf" name="entetePdf" rows="5" style="font-family:monospace; font-size:12px;">${etablissement.entetePdf}</textarea>
        </div>
        <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:16px;">
            <div class="input-group">
                <label class="label" for="republique">République</label>
                <input class="input-field" id="republique" name="republique" type="text" value="${etablissement.republique}"/>
            </div>
            <div class="input-group">
                <label class="label" for="ministere">Ministère</label>
                <input class="input-field" id="ministere" name="ministere" type="text" value="${etablissement.ministere}"/>
            </div>
            <div class="input-group">
                <label class="label" for="delegation">Délégation</label>
                <input class="input-field" id="delegation" name="delegation" type="text" value="${etablissement.delegation}"/>
            </div>
        </div>
    </div>

    <div style="display:grid; grid-template-columns:1fr 1fr; gap:24px;">
        <div class="card" style="padding:24px;">
            <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:16px;">Logo</h3>
            <c:if test="${not empty etablissement.logoFilename}">
                <img src="${pageContext.request.contextPath}/assets/${etablissement.logoFilename}"
                     alt="Logo" style="max-height:80px; margin-bottom:12px;"/>
            </c:if>
            <input class="input-field" name="logo" type="file" accept="image/*"/>
            <label style="display:flex; align-items:center; gap:8px; margin-top:12px; font-size:13px;">
                <input type="checkbox" name="filigraneLogo" ${etablissement.filigraneLogo ? 'checked' : ''}/>
                Filigrane sur les PDF
            </label>
        </div>
        <div class="card" style="padding:24px;">
            <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:16px;">SMS Twilio</h3>
            <c:choose>
                <c:when test="${twilioConfigure}"><span class="badge-ok">Configuré</span></c:when>
                <c:otherwise><span class="badge-warn">Simulation (logs)</span></c:otherwise>
            </c:choose>
            <p style="font-size:12px; color:var(--on-surface-variant); margin-top:8px;">
                Configuré via les variables d'environnement du serveur.
            </p>
        </div>
    </div>

    <div style="display:flex; justify-content:flex-end; gap:12px;">
        <a href="${pageContext.request.contextPath}/app/utilisateurs" class="btn" style="text-decoration:none;">Utilisateurs</a>
        <button type="submit" class="btn btn-primary"><span class="material-symbols-outlined">save</span> Enregistrer</button>
    </div>
</form>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
