# Documentation du Projet — Lycée Management System

## Table des matières
1. [Présentation générale](#1-présentation-générale)
2. [Architecture MVC](#2-architecture-mvc)
3. [Jakarta EE et les Servlets](#3-jakarta-ee-et-les-servlets)
4. [JSP (Jakarta Server Pages)](#4-jsp-jakarta-server-pages)
5. [DAO (Data Access Object)](#5-dao-data-access-object)
6. [DTO (Data Transfer Object)](#6-dto-data-transfer-object)
7. [Modèle (Model / Entity)](#7-modèle-model--entity)
8. [Service Layer](#8-service-layer)
9. [Filtres (Filters)](#9-filtres-filters)
10. [Pages d'erreur personnalisées](#10-pages-derreur-personnalisées)
11. [Mode sombre](#11-mode-sombre)
12. [Dashboard enrichi + Chart.js](#12-dashboard-enrichi--chartjs)
13. [Export Excel (Apache POI)](#13-export-excel-apache-poi)
14. [Génération PDF (iText 7)](#14-génération-pdf-itext-7)
15. [Notifications SMS (Twilio)](#15-notifications-sms-twilio)
16. [Déploiement du projet](#16-déploiement-du-projet)
17. [Configuration effectuée](#17-configuration-effectuée)
18. [Dépendances (pom.xml)](#18-dépendances-pomxml)
19. [Base de données](#19-base-de-données)
20. [Schéma architectural complet](#20-schéma-architectural-complet)

---

## 1. Présentation générale

**Lycée Management System** est une application web de gestion d'établissement d'enseignement secondaire (lycée). Elle permet de gérer :

- **Élèves** : inscription, photo, matricule, classe, parents
- **Classes** : 18 niveaux (6e → Tle) avec séries (ALL, ESP, CHS, A, C, D, TI)
- **Notes** : saisie par matière, coefficient et trimestre, avec calcul automatique des moyennes
- **Absences** : enregistrement, justification, suivi par trimestre et annuel
- **Utilisateurs** : 3 rôles (Admin, Censeur, Surveillant)
- **Documents PDF** : bulletins trimestriels, bulletins annuels, convocations, tableaux d'honneur
- **Export Excel** : liste des élèves, bulletins individuels
- **Dashboard** : statistiques avec graphiques Chart.js, absentéisme, performances

Technologies utilisées :
- **Langage** : Java 25
- **Framework Web** : Jakarta EE 11 (Servlets 6.1, JSP, JSTL 3.0)
- **Base de données** : MySQL 8.4 avec JDBC (pas d'ORM)
- **Serveur** : Apache Tomcat 11.0.21
- **Build** : Maven 3.9 (WAR)
- **PDF** : iText 7 Core (AGPL)
- **SMS** : Twilio SDK 9.3.0
- **Excel** : Apache POI 5.2.5
- **Graphiques** : Chart.js 4.x (CDN)
- **Mot de passe** : JBCrypt 0.4
- **Logs** : SLF4J + Logback

---

## 2. Architecture MVC

L'application suit le pattern **MVC (Modèle-Vue-Contrôleur)** :

```
[Client Browser]
       |
       v
   AuthFilter (vérifie session et rôle)
       |
       v
  Servlet (Contrôleur) ──→ DAO (Accès DB) ──→ MySQL
       |                      |
       |                      v
       |                  Modèle (POJO)
       |
       v
   JSP (Vue)
```

### Détail du flux MVC

| Couche | Rôle | Exemples |
|---|---|---|
| **Modèle** | POJO représentant les entités | `Eleve`, `NoteEleve`, `Absence`, `Classe`, `Utilisateur` |
| **Vue** | JSP dans `/WEB-INF/vues/` | `liste.jsp`, `form.jsp`, `dashboard.jsp` |
| **Contrôleur** | Servlets (`@WebServlet`) | `EleveServlet`, `NoteServlet`, `PdfServlet` |
| **DAO** | Interface + Implémentation JDBC | `EleveDAO` / `EleveDAOImpl` |
| **Service** | Logique métier | `PdfService`, `SmsService`, `ExcelService` |
| **DTO** | Objets de transfert pour les filtres | `EleveSearchCriteria`, `NoteSearchCriteria` |
| **Utilitaire** | Helpers | `PdfLayoutHelper`, `DateUtil`, `AuthUtil` |

---

## 3. Jakarta EE et les Servlets

L'application utilise **Jakarta EE 11** (ex Java EE) avec l'API Servlet 6.1.

### Servlet de base

```java
@WebServlet("/app/eleves/*")
public class EleveServlet extends HttpServlet {
    // doGet() pour l'affichage, doPost() pour les soumissions
}
```

### Tous les servlets

| Servlet | Mapping | Fonction |
|---|---|---|
| `LoginServlet` | `/login` | Authentification + logout |
| `DashboardServlet` | `/app/dashboard` | Vue d'ensemble + stats |
| `EleveServlet` | `/app/eleves/*` | CRUD élèves |
| `ClasseServlet` | `/app/classes/*` | CRUD classes |
| `NoteServlet` | `/app/notes/*` | CRUD notes (individuel + masse) |
| `AbsenceServlet` | `/app/absences/*` | CRUD absences |
| `DocumentsServlet` | `/app/documents` | Interface de génération de documents |
| `PdfServlet` | `/app/pdf/*` | Génération des PDF (bulletins, convocations) |
| `ExcelServlet` | `/app/excel/*` | Export Excel (élèves, bulletins) |
| `ChartDataServlet` | `/api/charts/*` | API JSON pour les graphiques |
| `UtilisateurServlet` | `/app/utilisateurs/*` | CRUD utilisateurs |
| `ParametreServlet` | `/app/parametres/*` | Paramètres établissement |
| `NotificationServlet` | `/api/notifications/*` | API notifications |
| `PhotoServlet` | `/app/photo/*` | Upload photo élève |
| `SallesApiServlet` | `/api/salles/*` | API salles pour filtres |
| `AssetServlet` | `/assets/*` | Fichiers statiques publics (logo) |

---

## 4. JSP (Jakarta Server Pages)

Les vues JSP sont situées dans `/WEB-INF/vues/` :

```
WEB-INF/vues/
├── login.jsp                 # Page de connexion
├── dashboard.jsp             # Tableau de bord
├── motDePasseOublie.jsp      # Mot de passe oublié
├── index.jsp                 # Redirection vers login
├── layout/
│   ├── header.jsp            # En-tête + sidebar + topbar
│   ├── footer.jsp            # Pied de page
│   └── flash.jsp             # Messages flash
├── eleve/
│   ├── liste.jsp             # Liste des élèves
│   └── form.jsp              # Formulaire élève
├── classe/
│   ├── liste.jsp             # Liste des classes
│   └── form.jsp              # Formulaire classe
├── note/
│   ├── liste.jsp             # Liste des notes
│   ├── form.jsp              # Édition note individuelle
│   └── saisie_masse.jsp      # Saisie groupée par classe
├── absence/
│   ├── liste.jsp             # Liste des absences
│   └── form.jsp              # Formulaire absence
├── documents/
│   └── index.jsp             # Interface de génération PDF + Excel
├── utilisateur/
│   ├── liste.jsp             # Liste des utilisateurs
│   └── form.jsp              # Formulaire utilisateur
├── parametre/
│   └── liste.jsp             # Paramètres établissement
└── erreur/
    ├── 404.jsp               # Page non trouvée
    ├── 403.jsp               # Accès refusé
    └── 500.jsp               # Erreur serveur
```

### Principe d'inclusion

```jsp
<jsp:include page="/WEB-INF/vues/layout/header.jsp">
    <jsp:param name="title" value="Liste des Élèves" />
    <jsp:param name="active" value="eleves" />
</jsp:include>
<!-- Contenu de la page -->
<jsp:include page="/WEB-INF/vues/layout/footer.jsp" />
```

### Pourquoi les JSP sont dans WEB-INF ?

Les pages JSP sont placées dans `/WEB-INF/` pour des raisons de sécurité :
- **Empêche l'accès direct** : un utilisateur ne peut pas accéder à `http://serveur/WEB-INF/vues/eleve/liste.jsp`
- **Passe obligatoirement par le servlet** : tout accès se fait via `/app/eleves/`
- **Séparation des responsabilités** : le contrôleur prépare les données, la vue les affiche

---

## 5. DAO (Data Access Object)

### Principe

Le pattern DAO sépare l'accès aux données de la logique métier. Chaque entité a :
- Une **interface** (`NoteDAO.java`) qui définit les contrats
- Une **implémentation** (`NoteDAOImpl.java`) avec les requêtes SQL concrètes

Toutes les connexions utilisent `DBConnection.getConnection()` (pool de connexions ou connexion directe via `db.properties`).

### Liste des DAO

| Interface | Implémentation | Entité |
|---|---|---|
| `EleveDAO` | `EleveDAOImpl` | Élèves |
| `ClasseDAO` | `ClasseDAOImpl` | Classes |
| `NoteDAO` | `NoteDAOImpl` | Notes |
| `AbsenceDAO` | `AbsenceDAOImpl` | Absences |
| `UtilisateurDAO` | `UtilisateurDAOImpl` | Utilisateurs |
| `ParametreDAO` | `ParametreDAOImpl` | Paramètres |
| `NotificationDAO` | `NotificationDAOImpl` | Notifications |

### Bonnes pratiques appliquées

```java
@Override
public List<NoteEleve> findByEleveAndTrimestre(Long eleveId, int trimestre) throws SQLException {
    String sql = "SELECT id, eleve_id, matiere, coefficient, notes_valeur, trimestre, prof_saisie "
               + "FROM note_eleve WHERE eleve_id=? AND trimestre=? ORDER BY matiere";
    List<NoteEleve> list = new ArrayList<>();
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setLong(1, eleveId);
        ps.setInt(2, trimestre);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
    }
    return list;
}
```

Points clés :
- **`try-with-resources`** partout (fermeture automatique)
- **`PreparedStatement`** (prévention injection SQL)
- **Pas de `Statement`**, pas de concaténation manuelle dans les requêtes

---

## 6. DTO (Data Transfer Object)

Les DTO sont utilisés pour transporter les critères de recherche entre les servlets et les DAO.

| Classe | Rôle |
|---|---|
| `EleveSearchCriteria` | Filtres pour la recherche d'élèves (nom, classe, sexe, salle, etc.) |
| `NoteSearchCriteria` | Filtres pour la recherche de notes (matière, trimestre, classe, etc.) |

Placement : `com.lycee.dto`

---

## 7. Modèle (Model / Entity)

Tous les modèles sont dans `com.lycee.model` :

| Classe | Champs principaux |
|---|---|
| `Eleve` | `id, matricule, nom, prenom, dateNaissance, sexe, classeId, telParent, emailParent, photoFilename, salle` |
| `Classe` | `id, niveau, serie, salle, anneeScolaire` |
| `NoteEleve` | `id, eleveId, matiere, coefficient, notesValeur, trimestre, profSaisie` |
| `Absence` | `id, eleveId, dateAbsence, dureeHeures, justifiee, motif` |
| `Utilisateur` | `id, login, passwordHache, role, nomComplet` |
| `ParametresEtablissement` | `id, etablissement, anneeScolaire, logoFilename, devise, ville, telephone, email, siteWeb, republique, ministere, delegation, entetePdf, filigraneLogo` |
| `Notification` | `id, type, message, destinataireId, lu, dateCreation` |

---

## 8. Service Layer

Les services contiennent la logique métier complexe.

| Service | Rôle |
|---|---|
| `PdfService` | Génération de tous les PDF (bulletins, convocations, tableaux d'honneur) |
| `ExcelService` | Export Excel (élèves, bulletins) via Apache POI |
| `SmsService` | Envoi de notifications SMS via Twilio (ou simulation) |
| `ParametreService` | Chargement et cache des paramètres établissement |
| `NotificationService` | Gestion des notifications en base |

---

## 9. Filtres (Filters)

### AuthFilter

L'`AuthFilter` (`@WebFilter("/app/*")`) intercepte toutes les requêtes vers `/app/*` :

1. Récupère l'utilisateur depuis la session
2. Si non connecté : redirige vers `/login`
3. Si connecté : vérifie le rôle via `AuthUtil.denyUnless*()`
4. Vérifie la validité de la session (timeout 30 min)

```
Requête → /app/eleves/
    ↓
AuthFilter (vérifie session)
    ↓
  ┌─ Session vide → redirect /login
  │
  └─ Session OK → EleveServlet
        ↓
      AuthUtil.denyUnlessAdminOrCenseur(req, resp)
```

### AuthUtil

Méthodes utilitaires pour le contrôle d'accès basé sur les rôles :
- `denyUnlessAdminOrCenseur(req, resp)` → 403 si rôle incorrect
- `denyUnlessAdmin(req, resp)` → 403 si pas Admin
- `denyUnlessCenseur(req, resp)` → 403 si pas Censeur
- `denyUnlessSurveillant(req, resp)` → 403 si pas Surveillant

---

## 10. Pages d'erreur personnalisées

### Configuration (web.xml)

```xml
<error-page>
    <error-code>400</error-code>
    <location>/WEB-INF/vues/erreur/404.jsp</location>
</error-page>
<error-page>
    <error-code>404</error-code>
    <location>/WEB-INF/vues/erreur/404.jsp</location>
</error-page>
<error-page>
    <error-code>403</error-code>
    <location>/WEB-INF/vues/erreur/403.jsp</location>
</error-page>
<error-page>
    <error-code>500</error-code>
    <location>/WEB-INF/vues/erreur/500.jsp</location>
</error-page>
<error-page>
    <exception-type>java.lang.Throwable</exception-type>
    <location>/WEB-INF/vues/erreur/500.jsp</location>
</error-page>
```

Les pages d'erreur sont conçues avec :
- Design responsive et animations
- Dégradés de couleurs distinctifs par code
- Boutons de navigation (retour au dashboard, historique)
- Détails techniques pour l'erreur 500 (en mode dev)
- Lien de support

---

## 11. Mode sombre

### Principe
- Attribut HTML `data-theme="dark"` sur `<html>`
- Variables CSS personnalisées dans `[data-theme="dark"]` (45+ variables redéfinies)
- Overrides spécifiques pour sidebar, topbar, tableaux, formulaires, badges

### Activation
```js
// Dans app.js - initDarkMode()
const saved = localStorage.getItem('theme');
if (saved === 'dark') apply(true);
else if (saved === 'light') apply(false);
else if (matchMedia('(prefers-color-scheme: dark)').matches) apply(true);
```

### Interface
- Bouton toggle dans le topbar (icône `dark_mode` / `light_mode`)
- Persistance via `localStorage`
- Détection automatique du thème système

---

## 12. Dashboard enrichi + Chart.js

### Endpoint API

`ChartDataServlet` (`@WebServlet("/api/charts/*")`) expose 3 endpoints JSON :

| Endpoint | Données |
|---|---|
| `GET /api/charts/moyennes-par-classe?trimestre=X` | Moyenne par classe |
| `GET /api/charts/absences-par-mois` | Heures d'absence par mois |
| `GET /api/charts/repartition-decision?trimestre=X` | Répartition Admis/Échec |

### Graphiques affichés

| Graphique | Type | Position |
|---|---|---|
| Moyennes par classe | Barre (chart.js) | Après les stats |
| Répartition des décisions | Doughnut (chart.js) | Après les stats |
| Absences mensuelles | Ligne (chart.js) | Remplace l'ancien graph CSS |

### Données dashboard

Le `DashboardServlet` charge :
- Effectifs (total, garçons, filles)
- Moyenne générale établissement
- Taux d'absentéisme
- Matière la plus critique
- 6 dernières notes saisies
- Absentéisme par classe

---

## 13. Export Excel (Apache POI)

### Dépendance

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

### Service

`ExcelService` (com.lycee.service) :

| Méthode | Description |
|---|---|
| `exportEleves(List<Eleve> eleves)` | Génère XLSX avec 8 colonnes (Matricule, Nom, Prénom, Date Naiss., Sexe, Classe, Tél. Parent, Email Parent) |
| `exportBulletin(Eleve, BigDecimal, int, int, int, BigDecimal, List<NoteEleve>)` | Génère XLSX avec en-tête élève, tableau des notes et résumé (moyenne, rang) |

### Endpoints

| URL | Export |
|---|---|
| `GET /app/excel/eleves` | Tous les élèves en XLSX |
| `GET /app/excel/bulletin?eleveId=X&trimestre=Y` | Bulletin individuel en XLSX |

### Interface
- Dropdown "Exporter" dans le dashboard (CSV + Excel)
- Carte dédiée dans la page Documents PDF

---

## 14. Génération PDF (iText 7)

### Dépendances

iText 7 Core (kernel + layout) version 7.2.5.

### Services

`PdfService` génère 4 types de documents :

| Méthode | Description |
|---|---|
| `genererBulletin(...)` | Bulletin trimestriel : identité, tableau des notes, moyenne, rang, appréciation, absences, décision |
| `genererBulletinAnnuel(...)` | Bulletin annuel : récapitulatif des 3 trimestres, moyenne annuelle, rang annuel, décision |
| `genererConvocation(...)` | Convocation parent : motif, date, QR code |
| `genererTableauHonneur(...)` | Tableau d'honneur : top 10 élèves |

### Endpoints PdfServlet

| URL | Fonction |
|---|---|
| `GET /app/pdf/bulletin/?eleveId=X&trimestre=Y` | Bulletin trimestriel individuel |
| `GET /app/pdf/bulletin-classe?classeId=X&trimestre=Y` | ZIP bulletins trimestriels d'une classe |
| `GET /app/pdf/bulletin-annuel?eleveId=X` | Bulletin annuel individuel |
| `GET /app/pdf/bulletin-annuel-classe?classeId=X` | ZIP bulletins annuels d'une classe |
| `GET /app/pdf/tableau-honneur?classeId=X&trimestre=Y` | Tableau d'honneur |
| `GET /app/pdf/convocation/?eleveId=X&motif=...&date=...` | Convocation parent |

### Layout PDF

`PdfLayoutHelper` fournit :
- En-tête institutionnel (Ministère, Délégation, Établissement, devise)
- Logo dynamique (filigrane optionnel)
- Tableaux stylisés avec couleurs alternées
- Formatage des notes, appréciations, décisions
- Signatures (Censeur)
- Watermark "BULLETIN ANNUEL" ou "BULLETIN TRIMESTRIEL"

---

## 15. Notifications SMS (Twilio)

### Configuration

Via `setenv.sh` ou variables d'environnement :
```bash
TWILIO_ACCOUNT_SID=votre_sid
TWILIO_AUTH_TOKEN=votre_token
TWILIO_PHONE_NUMBER=+1234567890
```

### Service

`SmsService` utilise le SDK Twilio 9.3.0 :
- `envoyerSmsBulletin(telephone, prenom, trimestre, moyenne, rang, effectif)`
- `envoyerSmsRappelAbsence(telephone, prenom, nbAbsences)`
- Simulation en mode dev (log au lieu d'envoyer)

---

## 16. Déploiement du projet

### Structure du WAR

```
lycee.war
├── index.jsp
├── assets/               # Fichiers publics (logo)
├── static/
│   ├── css/
│   │   ├── style.css     # Design system global + dark mode
│   │   └── login.css     # Styles login responsive
│   └── js/
│       └── app.js        # Core JS (pagination, sidebar, dark mode, charts, dropdowns)
├── WEB-INF/
│   ├── web.xml           # Configuration déploiement + error pages
│   ├── classes/
│   │   └── com/lycee/    # Classes compilées
│   ├── lib/              # Dépendances JAR
│   ├── db.properties     # Connexion MySQL (hors WEB-INF via classpath)
│   └── vues/             # JSP
```

### Déploiement manuel

```bash
# 1. Build
mvn clean package -DskipTests

# 2. Stop Tomcat
/opt/apache-tomcat-11.0.21/bin/shutdown.sh

# 3. Déployer
rm -rf /opt/apache-tomcat-11.0.21/webapps/lycee*
cp target/lycee.war /opt/apache-tomcat-11.0.21/webapps/

# 4. Start Tomcat
/opt/apache-tomcat-11.0.21/bin/startup.sh
```

### Script de déploiement (redeploy.sh)

Le script `redeploy.sh` automatise ces étapes (Linux/Mac). Pour Windows, voir `redeploy.bat`.

### Sessions

- Durée : 30 minutes
- Attributs session : `utilisateur`, `role`, `loginNom`
- Invalidation automatique après inactivité

---

## 17. Configuration effectuée

### Fichiers de configuration

| Fichier | Rôle |
|---|---|
| `db.properties` | Connexion MySQL (URL, utilisateur, mot de passe) |
| `setenv.sh` / `setenv.bat` | Variables d'environnement (Twilio, DB) |
| `src/main/resources/db.properties` | Fichier versionné avec valeurs par défaut |
| `.gitignore` | Exclut : `target/`, `.vscode/`, `*.bat`, `setenv.*`, `db.properties` (runtime) |

### Paramètres établissement

Table `parametre` avec colonnes fixes :
`etablissement, annee_scolaire, logo_filename, devise, ville, telephone, email, site_web, republique, ministere, delegation, entete_pdf, filigrane_logo`

---

## 18. Dépendances (pom.xml)

| Dépendance | Version | Usage |
|---|---|---|
| Jakarta EE API | 11.0.0 | Jakarta Servlet, JSP, EL |
| Jakarta Servlet API | 6.1.0 | Servlets |
| JSTL API + Impl | 3.0.0 / 3.0.1 | Tags JSTL (`c:forEach`, `c:if`) |
| MySQL Connector | 8.4.0 | Connexion MySQL |
| iText 7 Core | 7.2.5 | Génération PDF |
| JBCrypt | 0.4 | Hash mots de passe |
| Twilio SDK | 9.3.0 | SMS |
| Apache POI | 5.2.5 | Export Excel |
| Commons FileUpload | 1.5 | Upload photo |
| Commons IO | 2.15.1 | Utilitaires IO |
| SLF4J API | 2.0.9 | Logging |
| Logback Classic | 1.4.14 | Implémentation logs |

---

## 19. Base de données

### Tables

| Table | Rôle | Colonnes principales |
|---|---|---|
| `classe` | Classes | id, niveau, serie, salle, annee_scolaire |
| `eleve` | Élèves | id, matricule, nom, prenom, date_naissance, sexe, classe_id, tel_parent, email_parent, photo_filename, salle |
| `note_eleve` | Notes | id, eleve_id, matiere, coefficient, notes_valeur, trimestre, prof_saisie |
| `absence` | Absences | id, eleve_id, date_absence, duree_heures, justifiee, motif |
| `utilisateurs` | Utilisateurs | id, login, password_hache, role, nom_complet |
| `parametre` | Paramètres | id + 13 colonnes de configuration |
| `notification` | Notifications | id, type, message, destinataire_id, lu, date_creation |

### Relations

```
classe 1──N eleve
eleve  1──N note_eleve
eleve  1──N absence
```

### Requêtes notables

**Moyenne pondérée d'un élève :**
```sql
SELECT SUM(notes_valeur * coefficient) / SUM(coefficient) AS moyenne
FROM note_eleve WHERE eleve_id=? AND trimestre=?
```

**Rang d'un élève (window function) :**
```sql
SELECT rang FROM (
    SELECT eleve_id, RANK() OVER (ORDER BY SUM(notes_valeur*coefficient)/SUM(coefficient) DESC) AS rang
    FROM note_eleve WHERE trimestre=? GROUP BY eleve_id
) sub WHERE eleve_id=?
```

**Moyenne annuelle (moyenne des trimestres) :**
```sql
SELECT AVG(sub.moy) AS moyenne_annuelle FROM (
    SELECT SUM(notes_valeur * coefficient) / SUM(coefficient) AS moy
    FROM note_eleve WHERE eleve_id=? GROUP BY trimestre
) sub
```

**Répartition des décisions :**
```sql
SELECT decision, COUNT(*) AS count FROM (
    SELECT eleve_id,
        CASE WHEN SUM(notes_valeur * coefficient) / SUM(coefficient) >= 10
             THEN 'Admis' ELSE 'Échec' END AS decision
    FROM note_eleve WHERE trimestre=? GROUP BY eleve_id
) sub GROUP BY decision
```

---

## 20. Schéma architectural complet

```
                   ┌─────────────────────────────────────┐
                   │          Navigateur Client           │
                   │    (responsive, dark mode ready)     │
                   └────────────────┬────────────────────┘
                                    │ HTTP(S)
                                    ▼
                   ┌─────────────────────────────────────┐
                   │           Apache Tomcat 11           │
                   │          (Servlet Container)          │
                   └────────────────┬────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │           AuthFilter            │
                    │        @WebFilter("/app/*")     │
                    │   Vérifie session + rôle        │
                    └───────────────┬───────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │         Dispatcher             │
                    │   Router vers les servlets     │
                    └──────┬──────┬──────┬──────┬───┘
                           │      │      │      │
              ┌────────────┘      │      │      └──────────┐
              ▼                   ▼      ▼                  ▼
      ┌─────────────┐   ┌─────────────┐   ┌──────────────┐   ┌──────────────┐
      │ EleveServlet│   │ NoteServlet │   │ PdfServlet   │   │ ExcelServlet │
      │ ClasseServlet│  │AbsenceServlet│  │ ...          │   │ ...          │
      └──────┬──────┘   └──────┬──────┘   └──────┬───────┘   └──────┬───────┘
             │                 │                  │                  │
             ▼                 ▼                  ▼                  ▼
      ┌───────────────────────────────────────────────────────────────────┐
      │                       DAO Layer (JDBC)                            │
      │  EleveDAOImpl │ NoteDAOImpl │ AbsenceDAOImpl │ ClasseDAOImpl ...  │
      └─────────────────────────────┬─────────────────────────────────────┘
                                    │
                                    ▼
      ┌───────────────────────────────────────────────────────────────────┐
      │                       MySQL 8.4 Database                          │
      │  tables: eleve, classe, note_eleve, absence, utilisateurs,        │
      │          parametre, notification                                  │
      └───────────────────────────────────────────────────────────────────┘
```

### Flux de données pour la génération PDF

```
Utilisateur → Documents (JSP) → PdfServlet → NoteDAO / AbsenceDAO / EleveDAO
                                                         ↓
                                                    Données brutes
                                                         ↓
                                                    PdfService (iText 7)
                                                         ↓
                                                    byte[] PDF
                                                         ↓
                                                    Response → Download
```

### Flux de données pour les graphiques dashboard

```
Dashboard → ChartDataServlet → NoteDAO / AbsenceDAO
                                        ↓
                                    JSON (via StringBuilder)
                                        ↓
                                    Chart.js (côté client)
                                        ↓
                                    Canvases rendus
```

### Contrôle d'accès par rôle

| Ressource | Admin | Censeur | Surveillant |
|---|---|---|---|
| Dashboard | ✅ | ✅ | ✅ |
| Élèves (liste) | ✅ | ✅ | ✅ |
| Élèves (CRUD) | ✅ | ✅ | ❌ |
| Notes | ✅ | ✅ | ❌ |
| Absences | ✅ | ✅ | ✅ |
| Documents PDF | ✅ | ✅ | ❌ |
| Utilisateurs | ✅ | ❌ | ❌ |
| Paramètres | ✅ | ❌ | ❌ |
| Export Excel | ✅ | ✅ | ❌ |

