<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%-- Attributs requis : page, totalPages, total, size, paginationLabel (ex: "élèves") --%>
<c:set var="size" value="${not empty size ? size : 10}" />
<c:set var="page" value="${not empty page ? page : 1}" />
<c:set var="totalPages" value="${not empty totalPages ? totalPages : 1}" />
<c:set var="start" value="${(page - 1) * size + 1}" />
<c:set var="end" value="${page * size}" />
<c:if test="${end > total}"><c:set var="end" value="${total}" /></c:if>
<c:if test="${total == 0}"><c:set var="start" value="0" /><c:set var="end" value="0" /></c:if>

<div style="padding:16px 24px; background:var(--bg-main); border-top:1px solid var(--outline-variant); display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px;">
    <div style="display:flex; align-items:center; gap:16px; flex-wrap:wrap;">
        <p style="font-size:13px; color:var(--on-surface-variant);">
            Affichage de <span style="font-weight:700; color:var(--on-surface);">${start} à ${end}</span> sur ${total} ${paginationLabel}
        </p>
        <div style="display:flex; align-items:center; gap:8px;">
            <label for="pageSize" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Éléments par page:</label>
            <select id="pageSize" class="input-field" style="padding:4px 8px; font-size:12px; margin:0; width:auto; border-radius:6px; min-height:auto;" onchange="LyceeAdmin.changePageSize(this.value);">
                <c:forEach var="s" items="5,10,15,20,25">
                    <option value="${s}" ${size == s ? 'selected' : ''}>${s}</option>
                </c:forEach>
            </select>
        </div>
    </div>
    <div style="display:flex; gap:4px; align-items:center; flex-wrap:wrap;">
        <button type="button" class="btn-page material-symbols-outlined"
                ${page > 1 ? '' : 'disabled'}
                data-page="${page - 1}">chevron_left</button>

        <c:forEach begin="1" end="${totalPages}" var="p">
            <button type="button" class="btn-page-num ${p == page ? 'active' : ''}"
                    data-page="${p}">${p}</button>
        </c:forEach>

        <button type="button" class="btn-page material-symbols-outlined"
                ${page < totalPages ? '' : 'disabled'}
                data-page="${page + 1}">chevron_right</button>
    </div>
</div>
