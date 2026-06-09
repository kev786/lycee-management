<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="${eleve.id == null ? 'Nouvelle Inscription' : 'Modification Élève'}" />
    <jsp:param name="active" value="eleves" />
</jsp:include>

<div style="max-width:1000px; margin:0 auto;">
    <!-- Breadcrumbs & Header -->
    <div style="margin-bottom:32px;">
        <div style="display:flex; align-items:center; gap:8px; color:var(--on-surface-variant); font-size:12px; font-weight:600; margin-bottom:8px; text-transform:uppercase; letter-spacing:0.05em;">
            <a href="${pageContext.request.contextPath}/app/eleves/" style="text-decoration:none; color:inherit;">Élèves</a>
            <span class="material-symbols-outlined" style="font-size:16px;">chevron_right</span>
            <span style="color:var(--primary);">${eleve.id == null ? 'Nouvelle Inscription' : 'Modification'}</span>
        </div>
        <h2 style="font-size:32px; font-weight:700; color:var(--primary); letter-spacing:-0.02em;">
            ${eleve.id == null ? "Formulaire d'inscription" : "Modifier le dossier"}
        </h2>
        <p style="color:var(--on-surface-variant); font-size:14px;">Veuillez renseigner les informations académiques et personnelles de l'élève pour valider son dossier.</p>
    </div>

    <form action="${pageContext.request.contextPath}/app/eleves/${eleve.id == null ? 'nouveau' : 'modifier/'.concat(eleve.id)}" method="post" enctype="multipart/form-data">
        <div style="display:grid; grid-template-columns: 1fr 2fr; gap:24px;">
            
            <!-- Left Column: Photo Upload -->
            <div style="display:flex; flex-direction:column; gap:24px;">
                <div class="card" style="border-top:4px solid var(--primary); text-align:center;">
                    <h3 style="font-size:18px; font-weight:700; color:var(--primary); text-align:left; margin-bottom:24px;">Photo de l'élève</h3>
                    
                    <div style="position:relative; width:180px; height:180px; margin:0 auto 24px; border-radius:50%; border:4px solid var(--surface-container); overflow:hidden; background:var(--bg-main); display:flex; align-items:center; justify-content:center;">
                        <c:choose>
                            <c:when test="${not empty eleve.photoFilename}">
                                <img src="${pageContext.request.contextPath}/app/photos/${eleve.photoFilename}" alt="Aperçu" style="width:100%; height:100%; object-fit:cover;">
                            </c:when>
                            <c:otherwise>
                                <span class="material-symbols-outlined" style="font-size:64px; color:var(--outline);">person</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <p style="font-size:11px; color:var(--on-surface-variant); margin-bottom:16px;">Format JPG ou PNG, max 2MB. Portrait de face uniquement.</p>
                    
                    <label class="btn" style="display:flex; align-items:center; justify-content:center; gap:8px; background:white; border:1px solid var(--primary); color:var(--primary); cursor:pointer;">
                        <span class="material-symbols-outlined">upload</span>
                        Choisir un fichier
                        <input type="file" name="photo" style="display:none;" onchange="this.parentElement.style.borderColor='var(--on-surface-variant)'">
                    </label>
                    <c:if test="${not empty errors.photo}">
                        <p style="color:var(--error); font-size:11px; margin-top:8px; font-weight:600;">${errors.photo}</p>
                    </c:if>
                </div>

                <div style="background:var(--surface-container-low); padding:16px; border-radius:12px; border:1px solid var(--outline-variant); display:flex; gap:12px;">
                    <span class="material-symbols-outlined" style="color:var(--primary);">info</span>
                    <div>
                        <span style="font-size:10px; font-weight:700; color:var(--primary); text-transform:uppercase; display:block; margin-bottom:4px;">Rappel Administratif</span>
                        <p style="font-size:12px; color:var(--on-surface-variant); line-height:1.4;">Le matricule est généré automatiquement par le système national d'éducation dès validation du formulaire.</p>
                    </div>
                </div>
            </div>

            <!-- Right Column: Details -->
            <div style="display:flex; flex-direction:column; gap:24px;">
                <!-- Identity Section -->
                <div class="card" style="border-top:4px solid var(--primary);">
                    <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:24px; display:flex; align-items:center; gap:12px;">
                        <span class="material-symbols-outlined">person</span>
                        Identité de l'élève
                    </h3>
                    
                    <div style="display:grid; grid-template-columns: 1fr 1fr; gap:20px;">
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label for="matricule" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Matricule</label>
                            <input id="matricule" type="text" name="matricule" value="${eleve.matricule}" class="input-field" style="background:var(--bg-main); font-family:monospace; font-weight:700;" placeholder="Auto-généré ou manuel" ${eleve.id != null ? 'readonly' : ''}>
                            <c:if test="${not empty errors.matricule}">
                                <span style="color:var(--error); font-size:11px;">${errors.matricule}</span>
                            </c:if>
                        </div>

                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label for="classeId" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Classe</label>
                            <select id="classeId" name="classeId" class="input-field" style="padding-left:16px;">
                                <option value="">Sélectionner une classe</option>
                                <c:forEach var="c" items="${classes}">
                                    <option value="${c.id}" ${eleve.classeId == c.id ? 'selected' : ''}>${c.libelle}</option>
                                </c:forEach>
                            </select>
                            <c:if test="${not empty errors.classeId}">
                                <span style="color:var(--error); font-size:11px;">${errors.classeId}</span>
                            </c:if>
                        </div>

                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label for="nom" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Nom de famille</label>
                            <input id="nom" type="text" name="nom" value="${eleve.nom}" class="input-field" style="padding-left:16px;" placeholder="Ex: Dupont">
                            <c:if test="${not empty errors.nom}">
                                <span style="color:var(--error); font-size:11px;">${errors.nom}</span>
                            </c:if>
                        </div>

                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label for="prenom" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Prénom</label>
                            <input id="prenom" type="text" name="prenom" value="${eleve.prenom}" class="input-field" style="padding-left:16px;" placeholder="Ex: Jean-Marc">
                            <c:if test="${not empty errors.prenom}">
                                <span style="color:var(--error); font-size:11px;">${errors.prenom}</span>
                            </c:if>
                        </div>

                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label for="dateNaissance" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Date de Naissance</label>
                            <input id="dateNaissance" type="date" name="dateNaissance" value="${eleve.dateNaissance}" class="input-field" style="padding-left:16px;">
                        </div>

                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label for="sexe" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Sexe</label>
                            <select id="sexe" name="sexe" class="input-field" style="padding-left:16px;" required>
                                <option value="">Choisir...</option>
                                <option value="M" ${eleve.sexe == 'M' ? 'selected' : ''}>Masculin (Garçon)</option>
                                <option value="F" ${eleve.sexe == 'F' ? 'selected' : ''}>Féminin (Fille)</option>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Parent Section -->
                <div class="card" style="border-top:4px solid var(--primary);">
                    <h3 style="font-size:18px; font-weight:700; color:var(--primary); margin-bottom:24px; display:flex; align-items:center; gap:12px;">
                        <span class="material-symbols-outlined">family_restroom</span>
                        Contact Parent / Tuteur
                    </h3>
                    
                    <div style="display:grid; grid-template-columns: 1fr; gap:20px;">
                        <div style="display:flex; flex-direction:column; gap:6px;">
                            <label for="nomParent" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Nom complet du parent</label>
                            <input id="nomParent" type="text" name="nomParent" value="${eleve.nomParent}" class="input-field" style="padding-left:16px;" placeholder="Ex: Marc Dupont">
                        </div>

                        <div style="display:grid; grid-template-columns: 1fr 1fr; gap:20px;">
                             <div style="display:flex; flex-direction:column; gap:6px;">
                                <label for="telParent" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Téléphone</label>
                                <div class="input-wrapper">
                                    <span class="material-symbols-outlined input-icon">phone</span>
                                    <input id="telParent" type="tel" name="telParent" value="${eleve.telParent}" class="input-field" placeholder="+33 6 00 00 00 00">
                                </div>
                                <c:if test="${not empty errors.telParent}">
                                    <span style="color:var(--error); font-size:11px;">${errors.telParent}</span>
                                </c:if>
                            </div>

                             <div style="display:flex; flex-direction:column; gap:6px;">
                                <label for="emailParent" style="font-size:12px; font-weight:600; color:var(--on-surface-variant);">Email</label>
                                <div class="input-wrapper">
                                    <span class="material-symbols-outlined input-icon">mail</span>
                                    <input id="emailParent" type="email" name="emailParent" value="${eleve.emailParent}" class="input-field" placeholder="parent@exemple.com">
                                </div>
                                <c:if test="${not empty errors.emailParent}">
                                    <span style="color:var(--error); font-size:11px;">${errors.emailParent}</span>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div style="display:flex; justify-content:flex-end; gap:16px; padding-top:16px;">
                    <a href="${pageContext.request.contextPath}/app/eleves/" class="btn" style="text-decoration:none; background:var(--bg-main); border:1px solid var(--outline-variant); color:var(--on-surface-variant);">Annuler</a>
                    <button type="submit" class="btn btn-primary" style="padding:12px 32px;">
                        <span class="material-symbols-outlined">save</span>
                        ${eleve.id == null ? 'Enregistrer l\'élève' : 'Mettre à jour'}
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
