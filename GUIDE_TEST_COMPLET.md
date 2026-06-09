# 🧪 GUIDE DE TEST - Système de Gestion de Lycée

**Date** : 2 juin 2026  
**Version** : 1.0.0  
**Statut** : ✅ Prêt pour tests

---

## 📋 Vue d'ensemble des tests

Le projet a 3 domaines critiques à tester :

1. **Gestion des absences** (DAO + SMS)
2. **Gestion des photos** (Upload + Stockage)
3. **Système de notifications** (SMS via Twilio)

---

## 🚀 Prérequis

```bash
# 1. MySQL démarré et configuré
mysql -u root -p lycee_db -e "SELECT COUNT(*) FROM eleve;"

# 2. Répertoire upload créé
sudo mkdir -p /opt/lycee/photos
sudo chmod 777 /opt/lycee/photos

# 3. Tomcat 11 prêt
cd /opt/apache-tomcat-11.0.21/
./bin/startup.sh

# 4. WAR déployé
cp target/lycee.war ./webapps/lycee.war
```

---

## 🔐 Authentification

### Credentials de test (depuis schema.sql)

| Rôle | Login | Mot de passe | Accès |
|------|-------|-------------|-------|
| **Admin** | `admin` | `hachéBCrypt` | Tous les modules |
| **Censeur** | `censeur` | `hachéBCrypt` | Notes + PDF + SMS |
| **Surveillant** | `surveillant` | `hachéBCrypt` | Absences + Appels |

```
http://localhost:8080/lycee
→ Page login
→ Entrer identifiants
→ Redirection vers /app/dashboard
```

---

## ✅ TEST 1 : Gestion des absences

### Cas de test 1.1 : Affichage liste absences

**Étapes** :
1. Login avec rôle "Surveillant"
2. Naviguer vers `/app/absences`
3. Vérifier affichage 15 items/page
4. Vérifier colonnes : Élève, Date, Durée, Matière, Justifiée

**Résultat attendu** :
```
✅ Liste affichée
✅ Données de démonstration visibles
✅ Pagination fonctionnelle
✅ Filtres visibles
```

---

### Cas de test 1.2 : Ajout absence

**Étapes** :
1. Cliquer "Nouvelle absence"
2. Sélectionner élève (ex: "Alice Nkomo")
3. Sélectionner date (ex: 2026-06-01)
4. Durée : 2 heures
5. Matière : "Mathématiques"
6. Justifiée : **Non**
7. Motif : "Maladie"
8. Cliquer "Enregistrer"

**Résultat attendu** :
```
✅ Formulaire validé
✅ Absence enregistrée en BD
✅ Redirection vers liste (msg=cree)
✅ Nouvelle absence visible
```

---

### Cas de test 1.3 : SMS alerte absences

**Prérequis** :
- Twilio configuré OU mode test (SMS loggés)

**Étapes** :
1. Ajouter 5 absences pour même élève
2. Chaque absence : 2 heures, NON justifiée
3. Total = 10 heures = seuil SMS

**Résultat attendu** :

**Mode test (sans Twilio)** :
```
Logs Tomcat :
[SMS SIMULATION] À : +237699001001 | Alice a accumulé 10 absences injustifiées ce trimestre
```

**Mode production (avec Twilio)** :
```
Logs Tomcat :
SMS envoyé via Twilio. SID: SM1234567890abcdef
```

---

### Cas de test 1.4 : Requêtes SQL avancées

**Vérifier les statistiques d'absences** :

```bash
# Via les logs ou interface admin
mysql -u root -p lycee_db << EOF
-- Taux d'absentéisme par classe
SELECT c.niveau, c.serie, 
       COUNT(a.id) AS nb_absences,
       SUM(a.duree_heures) AS heures_abs
FROM classe c
LEFT JOIN eleve e ON e.classe_id=c.id
LEFT JOIN absence a ON a.eleve_id=e.id
GROUP BY c.id
ORDER BY heures_abs DESC;
EOF
```

**Résultat attendu** :
```
✅ Classes triées par volume absences
✅ GROUP BY fonctionne
✅ SUM() agrège correctement
```

---

## 📸 TEST 2 : Gestion des photos

### Cas de test 2.1 : Upload photo valide

**Étapes** :
1. Naviguer `/app/eleves/nouveau`
2. Remplir formulaire élève
3. Sélectionner photo (JPEG ou PNG, < 2 MB)
4. Cliquer "Enregistrer"

**Résultat attendu** :
```
✅ Photo acceptée
✅ Fichier écrit dans /opt/lycee/photos/{uuid}.jpg
✅ Enregistrement en BD : photo_filename={uuid}.jpg
✅ Redirection avec msg=cree
```

---

### Cas de test 2.2 : Rejet photo invalide (type)

**Étapes** :
1. Sélectionner fichier BMP ou GIF
2. Soumettre formulaire

**Résultat attendu** :
```
❌ Photo rejetée
❌ Message : "Format accepté : JPEG, PNG"
❌ Formulaire réaffché avec erreur
```

---

### Cas de test 2.3 : Rejet photo invalide (taille)

**Étapes** :
1. Créer fichier JPEG > 2 MB
2. Soumettre formulaire

**Résultat attendu** :
```
❌ Photo rejetée
❌ Message : "Taille max : 2 MB"
❌ Formulaire réaffché avec erreur
```

---

### Cas de test 2.4 : Vérifier stockage fichiers

```bash
# Vérifier les fichiers uploadés
ls -la /opt/lycee/photos/
# Résultat : fichiers {uuid}.jpg ou {uuid}.png

# Vérifier les métadonnées en BD
mysql -u root -p lycee_db -e "SELECT id, matricule, photo_filename FROM eleve WHERE photo_filename IS NOT NULL LIMIT 5;"
```

**Résultat attendu** :
```
✅ Fichiers physiques présents
✅ Noms uniques (UUID)
✅ BD contient références cohérentes
```

---

## 📱 TEST 3 : Système de notifications SMS

### Cas de test 3.1 : SMS Bulletin (crétion de notes)

**Mode test** :

**Étapes** :
1. Naviguer `/app/notes/nouveau`
2. Saisir notes pour élève (T1, Math=15, Français=16)
3. Soumettre

**Résultat attendu - Logs** :
```
[SMS SIMULATION] À : +237699001001 | Bulletin du T1 de Alice disponible. moy : 15.5/20. rang : 0/0
```

---

### Cas de test 3.2 : SMS Alerte (accumulation absences)

**Mode test** :

**Étapes** :
1. Ajouter 10h absences injustifiées pour élève
2. Dernière absence déclenchera SMS alerte

**Résultat attendu - Logs** :
```
[SMS SIMULATION] À : +237699001001 | Alice a accumulé 10 absences injustifiées ce trimestre
```

---

### Cas de test 3.3 : Configuration Twilio (production)

**Prérequis** :
- Compte Twilio actif
- Credentials configurés

**Étapes** :
```bash
# Définir variables d'environnement
export TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxx"
export TWILIO_AUTH_TOKEN="your_auth_token"
export TWILIO_PHONE_FROM="+1-555-123-4567"

# Redémarrer Tomcat
./tomcat/bin/shutdown.sh
./tomcat/bin/startup.sh

# Ajouter une absence
# SMS réel envoyé via Twilio API
```

**Résultat attendu** :
```
✅ Logs : "SMS envoyé via Twilio. SID: SM..."
✅ SMS reçu sur téléphone parent
```

---

## 🔐 TEST 4 : Sécurité

### Cas de test 4.1 : JDBC Injection

**Étapes** :
1. Formulaire recherche élève
2. Entrer : `' OR '1'='1`

**Résultat attendu** :
```
✅ PreparedStatement échappe le texte
✅ Recherche retourne aucun résultat
❌ Pas d'injection SQL (sécurisé)
```

---

### Cas de test 4.2 : Authentification

**Étapes** :
1. Accéder `/app/eleves` sans login
2. Redirection automatique vers `/login`

**Résultat attendu** :
```
✅ Redirection vers login
✅ Session vérifiée
```

---

### Cas de test 4.3 : Autorisation par rôle

**Étapes** :
1. Login avec "Surveillant"
2. Essayer accéder `/app/utilisateurs`

**Résultat attendu** :
```
❌ Erreur 403 Forbidden
✅ Rôle vérifiée dans AuthFilter
```

---

### Cas de test 4.4 : Headers sécurité

```bash
# Vérifier les en-têtes de sécurité
curl -I http://localhost:8080/lycee/app/dashboard

# Résultat attendu :
# X-Frame-Options: DENY ✅
# X-Content-Type-Options: nosniff ✅
# X-XSS-Protection: 1; mode=block ✅
# Cache-Control: no-store ✅
```

---

## 📊 TEST 5 : Requêtes SQL avancées

### Cas de test 5.1 : Moyenne générale par classe

```java
// NoteDAOImpl.getMoyenneGeneraleParClasse()
// Requête :
SELECT c.id, c.niveau, c.serie,
       AVG((n.notes_valeur * n.coefficient) / 
           (SELECT SUM(coefficient) FROM note_eleve WHERE trimestre = n.trimestre)) AS moyenne
FROM classe c
JOIN eleve e ON e.classe_id = c.id
JOIN note_eleve n ON n.eleve_id = e.id
WHERE n.trimestre = 1
GROUP BY c.id
ORDER BY moyenne DESC;
```

**Résultat attendu** :
```
✅ Classes triées par moyenne
✅ Calculs pondérés corrects
✅ GROUP BY agrège les notes
```

---

### Cas de test 5.2 : Élèves en échec

```java
// NoteDAOImpl.findElevesEnEchec()
// Requête :
SELECT e.id, e.nom, e.prenom,
       AVG(n.notes_valeur) AS moyenne
FROM eleve e
JOIN note_eleve n ON n.eleve_id = e.id
WHERE n.trimestre = 1
GROUP BY e.id
HAVING AVG(n.notes_valeur) < 10
ORDER BY moyenne ASC;
```

**Résultat attendu** :
```
✅ Élèves avec moy < 10 seulement
✅ HAVING filtre les résultats agrégés
✅ Tri ascendant
```

---

## 📋 Checklist de test complet

### Avant déploiement

- [ ] ✅ MySQL connexion OK
- [ ] ✅ Répertoire /opt/lycee/photos créé
- [ ] ✅ WAR compilé (31 MB)
- [ ] ✅ Tomcat 11 démarré

### Après déploiement

- [ ] ✅ Login fonctionne
- [ ] ✅ Absence ajoutée + visible
- [ ] ✅ SMS alerte loggé (mode test)
- [ ] ✅ Photo uploadée + stockée
- [ ] ✅ Authentification OK
- [ ] ✅ Autorisation par rôle OK
- [ ] ✅ Recherche SQL safe (pas injection)
- [ ] ✅ Headers sécurité présents

### Optionnel (Twilio)

- [ ] ✅ SMS réels envoyés (avec Twilio configuré)
- [ ] ✅ SMS reçus sur téléphone

---

## 🐛 Debugging

### Logs Tomcat

```bash
# Affichage en temps réel
tail -f /opt/apache-tomcat-11.0.21/logs/catalina.out

# Rechercher erreurs
grep -i "error\|exception\|sms" /opt/apache-tomcat-11.0.21/logs/catalina.out | tail -50
```

### Logs MySQL

```bash
# Voir les requêtes exécutées
mysql -u root -p lycee_db
mysql> SET SESSION sql_mode='';
mysql> SELECT * FROM absence WHERE eleve_id=1 LIMIT 1;
```

### Debugging SMS

```bash
# Mode test (check logs)
grep "SMS SIMULATION" /opt/apache-tomcat-11.0.21/logs/catalina.out

# Mode Twilio (check SID)
grep "SMS envoyé via Twilio" /opt/apache-tomcat-11.0.21/logs/catalina.out
```

---

## 🎯 Résumé

| Test | Statut | Commandes |
|------|--------|-----------|
| **Compilation** | ✅ OK | `mvn clean package -DskipTests` |
| **Authentification** | À tester | Login form |
| **Absences** | À tester | `/app/absences` |
| **SMS** | À tester (logs) | Ajouter absence |
| **Photos** | À tester | `/app/eleves/nouveau` |
| **Sécurité** | À tester | Injection SQL + CSRF |

---

**Prochaine étape** : Exécuter les tests et ajuster selon les résultats.

**Support** : Consulter `RAPPORT_VERIFICATION_COHERENCE.md` pour détails architecturaux.
