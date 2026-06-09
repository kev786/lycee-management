<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="${empty utilisateur.id ? 'Nouvel utilisateur' : 'Modifier utilisateur'}" />
    <jsp:param name="active" value="utilisateurs" />
</jsp:include>

<div style="max-width:560px;">
    <h2 style="font-size:24px; font-weight:700; color:var(--primary); margin-bottom:24px;">
        ${empty utilisateur.id ? 'Nouvel utilisateur' : 'Modifier utilisateur'}
    </h2>

    <form method="post" action="${pageContext.request.contextPath}/app/utilisateurs${empty utilisateur.id ? '/nouveau' : '/modifier/'.concat(utilisateur.id)}" class="card" style="padding:32px; display:flex; flex-direction:column; gap:20px;">
        <div class="input-group">
            <label class="label" for="login">Login</label>
            <input class="input-field" id="login" name="login" type="text" value="${utilisateur.login}" required/>
            <c:if test="${not empty errors.login}"><p style="color:var(--error); font-size:12px;">${errors.login}</p></c:if>
        </div>

        <div class="input-group">
            <label class="label" for="motPasse">Mot de passe ${not empty utilisateur.id ? '(laisser vide pour conserver)' : ''}</label>
            <input class="input-field" id="motPasse" name="motPasse" type="password" ${empty utilisateur.id ? 'required' : ''}/>
            <c:if test="${not empty errors.motPasse}"><p style="color:var(--error); font-size:12px;">${errors.motPasse}</p></c:if>
        </div>

        <div class="input-group">
            <label class="label" for="role">Rôle</label>
            <select class="input-field" id="role" name="role" required>
                <option value="">— Choisir —</option>
                <option value="Admin" ${utilisateur.role == 'Admin' ? 'selected' : ''}>Administrateur</option>
                <option value="Censeur" ${utilisateur.role == 'Censeur' ? 'selected' : ''}>Censeur</option>
                <option value="Surveillant" ${utilisateur.role == 'Surveillant' ? 'selected' : ''}>Surveillant</option>
            </select>
            <c:if test="${not empty errors.role}"><p style="color:var(--error); font-size:12px;">${errors.role}</p></c:if>
        </div>

        <div style="display:flex; gap:12px; margin-top:8px;">
            <button type="submit" class="btn btn-primary">Enregistrer</button>
            <a href="${pageContext.request.contextPath}/app/utilisateurs" class="btn" style="text-decoration:none;">Annuler</a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
