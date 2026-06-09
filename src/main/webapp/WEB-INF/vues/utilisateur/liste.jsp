<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Utilisateurs" />
    <jsp:param name="active" value="utilisateurs" />
</jsp:include>

<div style="display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:24px;">
    <div>
        <h2 style="font-size:28px; font-weight:700; color:var(--primary);">Gestion des utilisateurs</h2>
        <p style="font-size:14px; color:var(--on-surface-variant);">Comptes Admin, Censeur et Surveillant</p>
    </div>
    <a href="${pageContext.request.contextPath}/app/utilisateurs/nouveau" class="btn btn-primary" style="text-decoration:none; font-size:12px;">
        <span class="material-symbols-outlined" style="font-size:18px;">person_add</span>
        Nouvel utilisateur
    </a>
</div>

<c:if test="${param.error == 'self_delete'}"><div style="margin-bottom:16px; padding:12px; background:var(--error-container); color:var(--error); border-radius:8px;">Vous ne pouvez pas supprimer votre propre compte.</div></c:if>

<div class="card" style="padding:0; overflow:hidden;">
    <table class="data-table">
        <thead>
            <tr>
                <th>Login</th>
                <th>Rôle</th>
                <th style="text-align:right;">Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="u" items="${utilisateurs}">
                <tr class="hover-row">
                    <td style="font-weight:600;">${u.login}</td>
                    <td><span style="padding:4px 8px; background:var(--surface-container-high); font-size:11px; font-weight:700; border-radius:4px;">${u.role}</span></td>
                    <td style="text-align:right;">
                        <a href="${pageContext.request.contextPath}/app/utilisateurs/modifier/${u.id}" class="btn" style="font-size:11px; padding:6px 12px; text-decoration:none;">Modifier</a>
                        <a href="${pageContext.request.contextPath}/app/utilisateurs/supprimer/${u.id}"
                           onclick="return confirm('Supprimer cet utilisateur ?');"
                           class="btn" style="font-size:11px; padding:6px 12px; color:var(--error); text-decoration:none;">Supprimer</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
