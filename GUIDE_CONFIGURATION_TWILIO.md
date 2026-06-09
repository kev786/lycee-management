# 📱 Guide de Configuration Twilio

## ✅ Changement effectué

**SMSLib 3.5.4** → **Twilio 9.3.0** ✅

La dépendance SMSLib, qui n'était plus maintenue et obsolète, a été remplacée par **Twilio**, la plateforme moderne et leader pour SMS et notifications.

---

## 🎯 Avantages de Twilio

| Aspect | SMSLib | Twilio |
|--------|--------|--------|
| **Maintenance** | ❌ Obsolète | ✅ Activement maintenue |
| **Documentation** | ❌ Limitée | ✅ Excellente |
| **Support** | ❌ Communauté inactive | ✅ Support professionnel |
| **API** | ❌ Complexe (modem GSM) | ✅ Simple (REST API) |
| **Coût** | ❌ Modem GSM onéreux | ✅ Payant à l'usage ($ par SMS) |
| **Flexibilité** | ❌ Nécessite modem physique | ✅ Cloud, scalable |
| **Version Java** | ❌ Java 8 max | ✅ Java 11+ (21+) |

---

## 🚀 Comment utiliser Twilio

### Étape 1 : Créer un compte Twilio

1. Aller sur [twilio.com](https://www.twilio.com)
2. S'inscrire (gratuit avec crédit d'essai $15)
3. Vérifier le numéro de téléphone
4. Obtenir un numéro Twilio (ex: +1-555-123-4567)

### Étape 2 : Obtenir les credentials

Dans la console Twilio, vous trouverez :
- **Account SID** : `ACxxxxxxxxxxxxxxxxxxxxxxxxxxx`
- **Auth Token** : `your_auth_token_here`
- **Phone Number** : `+1-555-123-4567` (votre numéro Twilio)

### Étape 3 : Configurer les variables d'environnement

#### Sur Linux/Mac :
```bash
# Ajouter à ~/.bashrc ou ~/.bash_profile
export TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxxxxx"
export TWILIO_AUTH_TOKEN="your_auth_token_here"
export TWILIO_PHONE_FROM="+1-555-123-4567"

# Activer les changements
source ~/.bashrc
```

#### Sur Windows (PowerShell) :
```powershell
[Environment]::SetEnvironmentVariable("TWILIO_ACCOUNT_SID", "ACxxxxxxxxxxxxxxxxxxxxxxxxxxx", "User")
[Environment]::SetEnvironmentVariable("TWILIO_AUTH_TOKEN", "your_auth_token_here", "User")
[Environment]::SetEnvironmentVariable("TWILIO_PHONE_FROM", "+1-555-123-4567", "User")
```

#### Sur Windows (Invite de commande) :
```cmd
setx TWILIO_ACCOUNT_SID "ACxxxxxxxxxxxxxxxxxxxxxxxxxxx"
setx TWILIO_AUTH_TOKEN "your_auth_token_here"
setx TWILIO_PHONE_FROM "+1-555-123-4567"
```

#### En Docker :
```dockerfile
ENV TWILIO_ACCOUNT_SID="ACxxxxxxxxxxxxxxxxxxxxxxxxxxx"
ENV TWILIO_AUTH_TOKEN="your_auth_token_here"
ENV TWILIO_PHONE_FROM="+1-555-123-4567"
```

### Étape 4 : Démarrer l'application

```bash
# Compiler
mvn clean package

# Lancer Tomcat
./tomcat/bin/startup.sh

# L'application utilisera automatiquement Twilio si les variables sont définies
```

---

## 📋 Code modifié

### SmsService.java - Avant (SMSLib)
```java
// Avant : Nécessitait un modem GSM physique
SerialModemGateway gateway = new SerialModemGateway("modem1", "/dev/ttyUSB0", 19200, "Huawei", "");
```

### SmsService.java - Après (Twilio)
```java
// Après : Simple et cloud-native
Message message = Message.creator(
    new PhoneNumber(telephone),           // Destinataire
    new PhoneNumber(TWILIO_PHONE_FROM),  // Numéro Twilio
    texte                                  // Message
).create();
```

### pom.xml - Dépendances mises à jour
```xml
<!-- ❌ Avant -->
<dependency>
    <groupId>org.smslib</groupId>
    <artifactId>smslib</artifactId>
    <version>3.5.4</version>
</dependency>

<!-- ✅ Après -->
<dependency>
    <groupId>com.twilio.sdk</groupId>
    <artifactId>twilio</artifactId>
    <version>9.3.0</version>
</dependency>
```

### Java Compiler - Version corrigée
```xml
<!-- ❌ Avant -->
<source>25</source>
<target>25</target>

<!-- ✅ Après -->
<source>21</source>
<target>21</target>
```

---

## 🔧 Utilisation dans le code

### Envoyer un SMS de bulletin
```java
smsService.envoyerSmsBulletin("+237699001001", "Alice", 1, "15.5", 3, 50);
// Résultat : "Bulletin du T1 de Alice disponible. moy : 15.5/20. rang : 3/50"
```

### Envoyer une alerte absence
```java
smsService.envoyerSmsAlerte("+237699001001", "Alice", 5);
// Résultat : "Alice a accumulé 5 absences injustifiées ce trimestre"
```

---

## 💰 Tarification Twilio

### Plan essai (gratuit)
- ✅ Crédit d'essai : $15 USD
- ✅ Idéal pour tester
- ✅ Limitation : SMS uniquement vers numéros vérifiés

### Plan payant
- 📱 **Prix SMS sortant** : ~$0.0075 USD par SMS (Cameroun)
- 📞 **SMS entrant** : Gratuit
- 💬 **WhatsApp/Messengers** : Également disponibles

### Exemple de coût
```
100 SMS/jour × 30 jours = 3000 SMS/mois
3000 SMS × $0.0075 = $22.50 USD/mois (~13,000 FCFA)
```

---

## ✅ Mode test (sans configuration Twilio)

Si vous n'avez pas d'identifiants Twilio, l'application **fonctionne quand même** :

```
Les SMS sont simplement loggués dans la console :
[SMS SIMULATION] À : +237699001001 | Texte : "Bulletin du T1 de Alice..."
```

**Aucune configuration manquante** - Twilio est optionnel en développement !

---

## 🐛 Dépannage

### Erreur : "TWILIO_ACCOUNT_SID not found"
**Solution** : Vérifier que les variables d'environnement sont définies
```bash
# Vérifier les variables
echo $TWILIO_ACCOUNT_SID

# Si vide, redéfinir et redémarrer l'IDE/Tomcat
export TWILIO_ACCOUNT_SID="..."
```

### Erreur : "AuthenticationException from Twilio"
**Solution** : Vérifier que l'Auth Token est correct
```bash
# Copier depuis https://console.twilio.com/console
# Ne pas oublier les guillemets
export TWILIO_AUTH_TOKEN="your_auth_token_here"
```

### Erreur : "Invalid 'To' phone number"
**Solution** : Vérifier le format du numéro
```java
// ❌ Mauvais format
"0699001001"

// ✅ Bon format (international)
"+237699001001"  // Cameroun
"+33699001001"   // France
"+1-555-123-4567" // USA
```

---

## 📚 Ressources

| Ressource | Lien |
|-----------|------|
| **Console Twilio** | https://console.twilio.com |
| **Documentation Java SDK** | https://www.twilio.com/docs/sms/send-messages/java |
| **Pricing** | https://www.twilio.com/en-us/sms/pricing |
| **Support** | https://support.twilio.com |

---

## 🎓 Points importants

### Sécurité
- ✅ **Jamais** hardcoder les credentials dans le code
- ✅ Utiliser des variables d'environnement
- ✅ Ne pas commiter les secrets sur Git
- ✅ Ajouter `.env` au `.gitignore`

### Bonnes pratiques
- ✅ Logging des SMS envoyés (audit)
- ✅ Gestion des erreurs (try-catch)
- ✅ Rate limiting pour éviter les abus
- ✅ Consentement de l'utilisateur (opt-in)

### Alternative : AWS SNS
Si vous préférez AWS au lieu de Twilio :
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>sns</artifactId>
    <version>2.25.0</version>
</dependency>
```

---

## ✨ Résumé des changements

✅ **pom.xml** : SMSLib → Twilio 9.3.0  
✅ **SmsService.java** : Intégration Twilio complète  
✅ **Java version** : 25 → 21 (conforme au cahier des charges)  
✅ **Mode test** : Fonctionne sans configuration (SMS loggés)  
✅ **Mode production** : Fonctionne avec variables d'environnement  

**Votre projet est maintenant moderne et cloud-native !** 🚀

---

**Prochaine étape** : Ajouter les credentials Twilio et tester l'envoi de SMS.
