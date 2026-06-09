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
10. [Déploiement du projet](#10-déploiement-du-projet)
11. [Pourquoi les JSP sont dans WEB-INF](#11-pourquoi-les-jsp-sont-dans-web-inf)
12. [Configuration effectuée](#12-configuration-effectuée)
13. [Dépendances (pom.xml)](#13-dépendances-pomxml)
14. [Base de données](#14-base-de-données)
15. [Schéma architectural complet](#15-schéma-architectural-complet)

---

## 1. Présentation générale

**Lycée Management System** est une application web de gestion d'établissement d'enseignement secondaire (lycée). Elle permet de gérer :

- **Élèves** : inscription, photo, matricule, classe, parents
- **Classes** : 18 niveaux (6e → Tle) avec séries (ALL, ESP, CHS, A, C, D, TI)
- **Notes** : saisie par matière et trimestre, bulletins PDF
- **Absences** : enregistrement, justification, suivi
- **Utilisateurs** : 3 rôles (Admin, Censeur, Surveillant)
- **Paramètres** : configuration de l'établissement (nom, logo, devise, etc.)
- **Notifications** : alertes internes, envoi SMS via Twilio
- **Documents PDF** : bulletins, convocations, tableaux d'honneur

**Stack technique :**
- Java 25
- Jakarta EE 11 (Servlets 6.1, JSP, JSTL 3.0)
- Apache Tomcat 11.0.21
- MySQL 8.4
- Maven 3.9 (packaging WAR)
- CSS : Thème maison + Tailwind CDN + Material Symbols

---

## 2. Architecture MVC

Le projet suit le pattern **Modèle-Vue-Contrôleur (MVC)** :

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   MODÈLE     │◄───►│  CONTROLEUR  │────►│    VUE       │
│  (Entity)    │     │  (Servlet)   │     │  (JSP)       │
│  + DAO       │     │              │     │              │
│  + Service   │     │              │     │              │
└──────────────┘     └──────────────┘     └──────────────┘
```

### Rôles de chaque couche :

| Couche | Rôle | Exemple |
|---|---|---|
| **Modèle** | Données métier + accès base | `Eleve.java`, `EleveDAO.java` |
| **Vue** | Affichage HTML dynamique | `liste.jsp`, `form.jsp` |
| **Contrôleur** | Réception requête, logique, redirection | `EleveServlet.java` |

### Exemple de flux MVC (création d'un élève) :

1. Le navigateur envoie `GET /app/eleves/nouveau`
2. **Contrôleur** `EleveServlet.doGet()` traite la requête
3. Il charge la liste des classes via **Modèle** (`ClasseDAO.findAll()`)
4. Il stocke le résultat dans la requête : `req.setAttribute("classes", list)`
5. Il forwarde vers la **Vue** : `requestDispatcher("/WEB-INF/vues/eleve/form.jsp")`
6. La JSP génère le HTML avec les données reçues
7. L'utilisateur remplit le formulaire et POST
8. **Contrôleur** `EleveServlet.doPost()` valide les données
9. Il appelle **Modèle** `EleveDAO.create(eleve)` pour insérer en base
10. Il redirige (`sendRedirect`) vers la liste

---

## 3. Jakarta EE et les Servlets

### Qu'est-ce que Jakarta EE ?

Jakarta EE (anciennement Java EE / J2EE) est un ensemble de spécifications pour le développement d'applications d'entreprise en Java. L'application utilise Jakarta EE 11.

### Qu'est-ce qu'un Servlet ?

Un **Servlet** est une classe Java côté serveur qui reçoit des requêtes HTTP et produit des réponses. C'est l'équivalent Java d'un script PHP ou d'une route Express.js.

**Cycle de vie d'un Servlet :**
1. **init()** — appelé une fois lors du chargement
2. **service()** — appelé pour chaque requête (dispatch vers doGet/doPost)
3. **destroy()** — appelé lors du retrait du serveur

**Annotations principales :**
```java
@WebServlet("/app/eleves/*")        // Déclare l'URL pattern
@MultipartConfig(maxFileSize = ...) // Pour l'upload de fichiers

// Méthodes à surcharger :
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
protected void doPost(HttpServletRequest req, HttpServletResponse resp)
```

**Exemple concret :**
```java
@WebServlet("/app/eleves/*")
public class EleveServlet extends HttpServlet {

    private final transient EleveDAO eleveDAO = new EleveDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo(); // "/nouveau", "/modifier/5", etc.
        if ("/nouveau".equals(path)) {
            // Afficher formulaire de création
            List<Classe> classes = classeDAO.findAll();
            req.setAttribute("classes", classes);
            req.getRequestDispatcher("/WEB-INF/vues/eleve/form.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String nom = req.getParameter("nom"); // Récupère champ formulaire
        eleveDAO.create(new Eleve(nom, ...));
        resp.sendRedirect(req.getContextPath() + "/app/eleves");
    }
}
```

### Points importants sur les Servlets :

- **req.getParameter("nom")** — lit un champ de formulaire HTTP
- **req.setAttribute("cle", valeur)** — transmet des données à la JSP
- **req.getRequestDispatcher("/WEB-INF/...").forward(req, resp)** — forwarde vers une JSP (côté serveur)
- **resp.sendRedirect(url)** — redirige le navigateur (nouvelle requête HTTP)
- **req.getSession()** — récupère/crée une session HTTP
- **@WebServlet** — annotation qui remplace le `web.xml` pour déclarer les servlets (sauf pages d'erreur)

### Les filtres (Filters)

Un **Filter** intercepte les requêtes avant qu'elles n'atteignent le Servlet :

```java
@WebFilter(urlPatterns = {"/app/*"})
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
        // Vérifier si l'utilisateur est connecté
        if (session.getAttribute("utilisateur") == null) {
            resp.sendRedirect("/login");
            return;
        }
        // Continuer la chaîne
        chain.doFilter(req, resp);
    }
}
```

**Filtres du projet :**
- `AuthFilter` — Vérifie authentification et droits d'accès par rôle
- `SecurityHeadersFilter` — Ajoute les en-têtes HTTP de sécurité (X-Frame-Options, etc.)

---

## 4. JSP (Jakarta Server Pages)

### Qu'est-ce qu'une JSP ?

Une JSP est un fichier HTML contenant du code Java embarqué via des **tags** et des **expressions EL** (Expression Language). Elle permet de générer du HTML dynamique côté serveur.

### Syntaxe JSP :

```jsp
<%-- Commentaire JSP (invisible dans le HTML) --%>

<%@ page contentType="text/html;charset=UTF-8" %>  <%-- Directive page --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>   <%-- Import JSTL --%>

<%-- Affichage d'une variable avec EL (Expression Language) --%>
<p>${eleve.prenom} ${eleve.nom}</p>

<%-- Condition --%>
<c:if test="${not empty eleves}">
    <p>Il y a des élèves</p>
</c:if>

<%-- Boucle --%>
<c:forEach var="e" items="${eleves}">
    <tr><td>${e.matricule}</td><td>${e.prenom}</td></tr>
</c:forEach>

<%-- Lien --%>
<a href="${pageContext.request.contextPath}/app/eleves/modifier/${e.id}">Modifier</a>
```

### Comment les données arrivent dans une JSP ?

C'est le Servlet qui prépare les données avant de forwarder :

```java
// Dans le Servlet :
List<Eleve> eleves = eleveDAO.findAll();
req.setAttribute("eleves", eleves);    // ← rend "eleves" accessible dans la JSP
req.getRequestDispatcher("/WEB-INF/vues/eleve/liste.jsp").forward(req, resp);

// Dans la JSP, on accède à la liste via ${eleves}
```

### Structure des vues du projet :

```
WEB-INF/vues/
├── absence/          liste.jsp, form.jsp
├── classe/           liste.jsp, form.jsp
├── documents/        index.jsp
├── eleve/            liste.jsp, form.jsp, detail.jsp
├── erreur/           403.jsp, 404.jsp, 500.jsp
├── layout/           header.jsp, footer.jsp, pagination.jsp, flash.jsp
├── note/             liste.jsp, form.jsp, saisie-masse.jsp
├── parametre/        liste.jsp
├── utilisateur/      liste.jsp, form.jsp
├── dashboard.jsp
├── login.jsp
└── motDePasseOublie.jsp
```

---

## 5. DAO (Data Access Object)

### Définition

Le pattern **DAO** sépare la logique d'accès aux données du reste de l'application. Chaque entité a son propre DAO qui encapsule les requêtes SQL.

### Structure

```java
// Interface (contrat)
public interface EleveDAO {
    Eleve findById(Long id) throws SQLException;
    List<Eleve> findAll() throws SQLException;
    List<Eleve> findAllPaginated(int page, int size, EleveSearchCriteria c) throws SQLException;
    void create(Eleve e) throws SQLException;
    void update(Eleve e) throws SQLException;
    void delete(Long id) throws SQLException;
}

// Implémentation concrète
public class EleveDAOImpl implements EleveDAO {

    @Override
    public Eleve findById(Long id) throws SQLException {
        String sql = "SELECT * FROM eleve WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }
}
```

### Règles appliquées dans le projet :

- **PreparedStatement** obligatoire (protection contre les injections SQL)
- **try-with-resources** partout (fermeture automatique des connexions)
- `Connection` obtenue via `DBConnection.getConnection()` (singleton)
- Pas de `connection.close()` manuel — le try-with-resources s'en charge

### Tous les DAO du projet :

| DAO | Interface | Implémentation |
|---|---|---|
| EleveDAO | `eleve` → CRUD + pagination + recherche | `EleveDAOImpl` |
| ClasseDAO | `classe` → CRUD + pagination | `ClasseDAOImpl` |
| NoteDAO | `note_eleve` → CRUD + pagination | `NoteDAOImpl` |
| AbsenceDAO | `absence` → CRUD + pagination | `AbsenceDAOImpl` |
| UtilisateurDAO | `utilisateurs` → CRUD | `UtilisateurDAOImpl` |
| ParametreDAO | `parametre` → find + save | `ParametreDAOImpl` |
| NotificationDAO | `notification` → CRUD | `NotificationDAOImpl` |

---

## 6. DTO (Data Transfer Object)

### Définition

Un **DTO** est un objet simple qui transporte des données entre les couches, sans logique métier. Il sert à encapsuler des critères de recherche ou des résultats composites.

### Exemple dans le projet

```java
public class EleveSearchCriteria {
    private String q;          // recherche textuelle
    private Long classeId;     // filtre par classe
    private String niveau;     // filtre par niveau
    private String serie;      // filtre par série
    // Getters + setters...
}
```

Ce DTO est utilisé par `EleveServlet` pour passer les critères de filtrage à `EleveDAOImpl`, au lieu d'avoir 6 paramètres séparés dans la méthode.

**DTO du projet :**
- `EleveSearchCriteria` — critères de recherche/filtre des élèves
- `NoteSearchCriteria` — critères de recherche/filtre des notes

---

## 7. Modèle (Model / Entity)

### Définition

Une **Entity** est une classe Java qui représente une table de la base de données. Chaque champ = une colonne.

```java
public class Eleve {
    private Long id;
    private String matricule;
    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private Long classeId;       // FK vers classe
    private String telParent;
    private String emailParent;
    private String photoFilename;
    // Getters + setters...
}
```

**Entités du projet :**
| Classe | Table | Usage |
|---|---|---|
| `Eleve` | `eleve` | Données personnelles + parents |
| `Classe` | `classe` | Niveau, série, salle, professeur principal |
| `NoteEleve` | `note_eleve` | Notes par matière/trimestre |
| `Absence` | `absence` | Absences avec motif et justification |
| `Utilisateur` | `utilisateurs` | Comptes (login, hash, rôle) |
| `ParametresEtablissement` | `parametre` | Config de l'établissement |
| `Notification` | `notification` | Alertes internes |

---

## 8. Service Layer

La couche **Service** encapsule la logique métier et sert d'intermédiaire entre les Servlets et les DAOs.

```java
public class ParametreService {
    private final ParametreDAO parametreDAO = new ParametreDAOImpl();

    public ParametresEtablissement charger() {
        try {
            return parametreDAO.find();
        } catch (SQLException e) {
            LOG.warn("Erreur, valeurs par défaut utilisées");
            return new ParametresEtablissement();
        }
    }
}
```

**Services du projet :**
- `ParametreService` — gestion des paramètres établissement
- `PdfService` — génération des PDF (bulletins, convocations, tableaux d'honneur)
- `SmsService` — envoi de SMS via Twilio (simulation si non configuré)
- `NotificationService` — gestion des notifications internes

---

## 9. Filtres (Filters)

### AuthFilter

Intercepte toutes les requêtes `/app/*` :

1. Vérifie si l'utilisateur est connecté (session)
2. Vérifie les droits d'accès selon le rôle :
   - **Admin** : tout accès
   - **Censeur** : pas d'accès à `/app/utilisateurs`, `/app/parametres`
   - **Surveillant** : accès limité à `/app/eleves`, `/app/absences`, `/app/dashboard`
3. Si non connecté → redirection vers `/login`
4. Si pas les droits → HTTP 403

### SecurityHeadersFilter

Intercepte **toutes** les requêtes (`/*`) et ajoute les en-têtes de sécurité :
- `X-Frame-Options: DENY` (pas d'iframe)
- `X-Content-Type-Options: nosniff`
- `X-XSS-Protection: 1; mode=block`
- `Referrer-Policy: strict-origin-when-cross-origin`
- `Cache-Control: no-store`

---

## 10. Déploiement du projet

### Prérequis

- Java 25 (JDK)
- Apache Tomcat 11.0.21
- MySQL 8.4
- Maven 3.9+
- Git

### Étapes de déploiement

#### 1. Cloner le projet
```bash
git clone <url-du-repo>
cd lycee-management
```

#### 2. Créer la base de données
```sql
CREATE DATABASE lycee_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. Initialiser les tables et données
```bash
mysql -u root -proot lycee_db < schema.sql
```

#### 4. Configurer db.properties
Le fichier `src/main/resources/db.properties` contient :
```properties
db.url=jdbc:mysql://localhost:3306/lycee_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Africa/Douala
db.user=root
db.password=root
storage.dir=/home/kevin/lycee_storage
```

#### 5. Construire le WAR
```bash
mvn clean package -DskipTests
```
→ produit `target/lycee.war`

#### 6. Déployer sur Tomcat

Copier le WAR dans le répertoire webapps de Tomcat :
```bash
cp target/lycee.war /opt/apache-tomcat-11.0.21/webapps/
```

#### 7. Démarrer Tomcat
```bash
/opt/apache-tomcat-11.0.21/bin/startup.sh
```

#### 8. Accéder à l'application
```
http://localhost:8080/lycee/login
```

### Script de redéploiement automatique

```bash
./redeploy.sh
```
Ce script exécute : `mvn clean package` → arrête Tomcat → copie le WAR → démarre Tomcat.

---

## 11. Pourquoi les JSP sont dans WEB-INF

Les fichiers JSP sont placés dans `/WEB-INF/vues/` pour des **raisons de sécurité** :

| Sans WEB-INF | Avec WEB-INF |
|---|---|
| `http://site/eleve/liste.jsp` → accessible directement | `http://site/WEB-INF/vues/eleve/liste.jsp` → **HTTP 404** |
| Un visiteur peut contourner le contrôleur et accéder aux vues sans authentification | Les JSP sont uniquement accessibles via un **forward** interne du serveur (`req.getRequestDispatcher(...)`) |
| Les données dynamiques (`${eleves}`) seraient vides car non préparées par le Servlet | Le Servlet prépare les données AVANT de forwarder vers la JSP |

**Règle :** Jamais de `sendRedirect` vers une JSP. Toujours un `forward` depuis un Servlet.

---

## 12. Configuration effectuée

### web.xml (Jakarta EE 6.0)

```xml
<web-app version="6.0" ...>
    <display-name>Lycee Management System</display-name>
    <!-- Pages d'erreur personnalisées -->
    <error-page>
        <error-code>404</error-code>
        <location>/WEB-INF/vues/erreur/404.jsp</location>
    </error-page>
</web-app>
```

Le `web.xml` a été migré de Servlet 2.3 (D TD) vers Jakarta EE 6.0, ce qui a corrigé l'Expression Language (EL) qui était désactivé par défaut dans les anciennes versions.

### Schéma MySQL (schema.sql)

- 7 tables : `classe`, `eleve`, `note_eleve`, `absence`, `utilisateurs`, `parametre`, `notification`
- Contraintes : clés étrangères avec `ON DELETE CASCADE`, `CHECK` contraintes
- Données de démo : 18 classes, 265 élèves, 216 notes (Tle C), 3 utilisateurs

### Stockage fichiers

- Photos élèves : `{storage.dir}/photos/`
- Assets (logo) : `{storage.dir}/assets/`
- Configurable via `storage.dir` dans `db.properties`

### Sécurité

- Mots de passe hashés en BCrypt (JBCrypt 0.4)
- Filtre d'authentification par rôle (AuthFilter)
- En-têtes HTTP de sécurité (SecurityHeadersFilter)
- PreparedStatement anti-injection SQL
- Upload de photos sécurisé (UUID + validation extension)

### SMS Twilio

- SDK Twilio 9.3.0 configuré
- Credentials lus depuis les variables d'environnement (`TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_PHONE_FROM`)
- Fallback : simulation dans les logs si non configuré

### Session HTTP

- Durée : 30 minutes d'inactivité
- Attributs de session : `utilisateur`, `role`, `loginNom`

---

## 13. Dépendances (pom.xml)

| Dépendance | Version | Usage |
|---|---|---|
| **Jakarta EE API** | 11.0.0 | Spécifications Jakarta EE (Servlet, JSP, etc.) |
| **Servlet API** | 6.1.0 | API des Servlets (Tomcat 11) |
| **JSTL API** | 3.0.0 | Tags JSP standard (boucles, conditions) |
| **JSTL Impl (Glassfish)** | 3.0.1 | Implémentation des tags JSTL |
| **MySQL Connector** | 8.4.0 | Pilote JDBC pour MySQL |
| **iText 7 Core** | 7.2.5 | Génération de PDF (bulletins, convocations) |
| **iText 7 Kernel** | 7.2.5 | Noyau iText PDF |
| **iText 7 Layout** | 7.2.5 | Mise en page PDF |
| **JBCrypt** | 0.4 | Hachage BCrypt des mots de passe |
| **Twilio SDK** | 9.3.0 | Envoi de SMS (notifications parents) |
| **Commons FileUpload** | 1.5 | Upload de fichiers (photos élèves, logo) |
| **Commons IO** | 2.15.1 | Utilitaires d'entrée/sortie |
| **SLF4J API** | 2.0.9 | Interface de logging |
| **Logback Classic** | 1.4.14 | Implémentation de logging |

### Plugins Maven

| Plugin | Version | Usage |
|---|---|---|
| `maven-compiler-plugin` | 3.11.0 | Compilation Java 25 |
| `maven-war-plugin` | 3.4.0 | Packaging WAR (pas de web.xml requis) |

---

## 14. Base de données

### Modèle conceptuel

```
┌──────────────┐       ┌──────────────────┐
│   classe     │       │    eleve         │
├──────────────┤       ├──────────────────┤
│ id (PK)      │◄──────│ id (PK)          │
│ niveau       │ 1,N   │ matricule        │
│ serie        │       │ prenom           │
│ nom_complet  │       │ nom              │
│ salle        │       │ date_naissance   │
│ prof_principal│      │ sexe             │
│ effectif_max │       │ classe_id (FK)───┘
│ annee_scolaire│      │ tel_parent       │
└──────────────┘       │ email_parent     │
                       │ photo_filename   │
         ┌─────────────┴──────────────┐
         │                            │
┌────────▼──────┐          ┌─────────▼───────┐
│  note_eleve   │          │    absence      │
├───────────────┤          ├─────────────────┤
│ id (PK)       │          │ id (PK)         │
│ eleve_id (FK) │          │ eleve_id (FK)   │
│ matiere       │          │ date_absence    │
│ coefficient   │          │ duree_heures    │
│ notes_valeur  │          │ matiere         │
│ trimestre     │          │ justifiee       │
│ prof_saisie   │          │ motif           │
└───────────────┘          └─────────────────┘

┌────────────────┐      ┌──────────────────┐
│  utilisateurs  │      │   parametre      │
├────────────────┤      ├──────────────────┤
│ id (PK)        │      │ id (PK) = 1      │
│ login (UNIQUE) │      │ etablissement    │
│ password_hache │      │ logo_filename    │
│ role (ENUM)    │      │ devise           │
└────────────────┘      │ ville            │
                        │ telephone        │
┌────────────────┐      │ email            │
│  notification  │      │ ...              │
├────────────────┤      └──────────────────┘
│ id (PK)        │
│ role_cible     │
│ message        │
│ lien           │
│ lue            │
│ date_creation  │
└────────────────┘
```

---

## 15. Schéma architectural complet

```
                    ┌──────────┐
                    │ Browser  │
                    └────┬─────┘
                         │ HTTP
                    ┌────▼─────┐
                    │  Tomcat  │
                    │  11.0.21 │
                    └────┬─────┘
                         │
              ┌──────────┼──────────┐
              │          │          │
         ┌────▼───┐ ┌───▼────┐ ┌───▼────┐
         │Filters │ │Servlets│ │ Assets │
         │Auth    │ │ /app/* │ │ /assets│
         │Security│ │ /login │ │   /*   │
         └────┬───┘ └───┬────┘ └────────┘
              │         │
              │   ┌─────▼──────────┐
              │   │   Service      │
              │   │  Layer         │
              │   │ PdfService     │
              │   │ SmsService     │
              │   │ ParametreSvc   │
              │   └─────┬──────────┘
              │         │
              │   ┌─────▼──────────┐
              │   │   DAO Layer    │
              │   │ (Interface +   │
              │   │  Impl)         │
              │   └─────┬──────────┘
              │         │ JDBC
              │   ┌─────▼──────────┐
              │   │   MySQL 8.4    │
              │   │   lycee_db     │
              │   └────────────────┘
              │
         ┌────▼──────────────────────┐
         │     Vues JSP (WEB-INF)    │
         │  header.jsp → footer.jsp  │
         │  eleve/liste.jsp          │
         │  eleve/form.jsp           │
         │  login.jsp                │
         │  ...                      │
         └───────────────────────────┘
```

### Flux d'une requête typique

```
1. Navigateur → GET http://localhost:8080/lycee/app/eleves
2. Tomcat intercepte → vérifie le contexte "/lycee"
3. SecurityHeadersFilter s'exécute (ajoute en-têtes HTTP)
4. AuthFilter s'exécute (vérifie session, droits)
5. EleveServlet.doGet() est appelé
6. EleveServlet appelle EleveDAO.findAllPaginated(...)
7. EleveDAO construit une requête SQL avec PreparedStatement
8. MySQL exécute la requête, retourne les résultats
9. EleveDAO transforme ResultSet en objets Eleve
10. EleveServlet stocke la liste dans req.setAttribute("eleves", ...)
11. EleveServlet forwarde vers /WEB-INF/vues/eleve/liste.jsp
12. La JSP génère le HTML avec JSTL + EL
13. Tomcat renvoie la réponse HTTP au navigateur
```

---

## Prompt pour Gemini (pour générer un document similaire)

```
Tu es développeur Java spécialisé Jakarta EE. Rédige un document
technique complet pour un projet web Java nommé "Lycée Management System".

L'application utilise :
- Java 25, Jakarta EE 11 (Servlets 6.1, JSTL 3.0)
- Apache Tomcat 11.0.21
- MySQL 8.4
- Maven 3.9 (packaging WAR)

Architecture : MVC (Modèle-Vue-Contrôleur)
- Contrôleur : Servlets avec annotations @WebServlet
- Vue : JSP dans /WEB-INF/vues/ avec JSTL + EL (Expression Language)
- Modèle : Classes POJO + DAO (interface + impl) + Service Layer
- Filtres : AuthFilter (authentification par rôle) + SecurityHeadersFilter
- DTO : EleveSearchCriteria, NoteSearchCriteria (critères de recherche)

Fonctionnalités :
- CRUD élèves, classes, notes, absences, utilisateurs
- Pagination + recherche
- Upload photo élève et logo établissement
- Génération PDF (bulletins, convocations, tableaux d'honneur) via iText 7
- Envoi SMS via Twilio SDK 9.3.0
- Notifications internes
- 3 rôles : Admin, Censeur, Surveillant

Sécurité :
- PreparedStatement anti-injection SQL (try-with-resources)
- BCrypt pour mots de passe (JBCrypt 0.4)
- Filtre d'authentification et autorisation par rôle
- En-têtes HTTP de sécurité (X-Frame-Options, etc.)
- JSP dans WEB-INF (pas d'accès direct)
- Sessions HTTP (30 min d'inactivité)

Base de données (MySQL) :
- 7 tables : classe, eleve, note_eleve, absence, utilisateurs, parametre, notification
- Données de démo : 18 classes, 265 élèves, 216 notes, 3 utilisateurs
- Schema.sql avec CREATE TABLE + INSERT + contraintes

Configuration :
- web.xml Jakarta EE 6.0 (pages d'erreur 404/403/500)
- db.properties pour connexion DB et stockage fichiers
- Variables d'environnement Twilio
- Aucun framework Spring — Jakarta EE pure
- CSS : thème maison + Material Symbols + Tailwind (CDN)

Le document doit expliquer clairement :
1. Qu'est-ce que Jakarta EE et quel est son rôle
2. Qu'est-ce qu'un Servlet, son cycle de vie (@WebServlet, doGet/doPost)
3. Qu'est-ce qu'une JSP, syntaxe EL/JSTL, différence avec HTML statique
4. Le pattern MVC appliqué au projet
5. Le rôle et l'utilité des DAO (avec exemple de code)
6. Le rôle et l'utilité des DTO
7. Le rôle des filtres (Filters)
8. Pourquoi les JSP sont dans WEB-INF (sécurité)
9. Comment déployer (WAR → Tomcat → MySQL)
10. Toutes les dépendances Maven et leur utilité
11. Les configuration clés (web.xml, db.properties, variables env)
12. Schéma de la base de données
13. Exemple de flux complet (requête → servlet → DAO → JSP → réponse)

Écris en français, dans un style clair et pédagogique destiné à un
étudiant en informatique qui doit présenter le projet devant un jury.
Utilise des schémas textuels, des tableaux et des extraits de code
pour illustrer chaque concept.
```

---

*Document généré le 09/06/2026 — Lycée Management System v1.0.0*
