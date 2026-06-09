<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="flashCode" value="${not empty param.flash ? param.flash : param.msg}" />
<c:if test="${not empty flashCode}">
    <c:set var="flashType" value="${flashCode == 'access_refuse' || flashCode == 'self_delete' ? 'error' : (flashCode == 'supprime' ? 'warning' : 'success')}" />
    <div class="flash-alert flash-${flashType}" data-flash role="alert">
        <span class="material-symbols-outlined">
            <c:choose>
                <c:when test="${flashType == 'error'}">block</c:when>
                <c:when test="${flashType == 'warning'}">info</c:when>
                <c:otherwise>check_circle</c:otherwise>
            </c:choose>
        </span>
        <span>
            <c:choose>
                <c:when test="${flashCode == 'cree'}">Enregistrement créé avec succès.</c:when>
                <c:when test="${flashCode == 'modifie'}">Modification enregistrée avec succès.</c:when>
                <c:when test="${flashCode == 'supprime'}">Suppression effectuée.</c:when>
                <c:when test="${flashCode == 'enregistre'}">Paramètres enregistrés avec succès.</c:when>
                <c:when test="${flashCode == 'access_refuse'}">Accès refusé : votre rôle ne permet pas cette action.</c:when>
                <c:when test="${flashCode == 'self_delete'}">Vous ne pouvez pas supprimer votre propre compte.</c:when>
                <c:when test="${flashCode == 'bulletins_ok'}">Génération des bulletins lancée.</c:when>
                <c:otherwise>Opération réussie.</c:otherwise>
            </c:choose>
        </span>
        <button type="button" class="flash-close material-symbols-outlined" onclick="this.parentElement.remove()">close</button>
    </div>
</c:if>
<c:if test="${not empty erreur}">
    <div class="flash-alert flash-error" role="alert">
        <span class="material-symbols-outlined">error</span>
        <span>${erreur}</span>
    </div>
</c:if>
