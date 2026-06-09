#!/bin/bash
set -e

echo "=== Configuration du projet Lycée Management ==="
echo ""

# 1. Vérifier MySQL
echo "[1] Vérification MySQL..."
if mysql -u root -proot -e "SELECT 1" > /dev/null 2>&1; then
    echo "  ✅ MySQL accessible"
else
    echo "  ⚠️  MySQL non accessible. Vérifiez db.properties"
fi

# 2. Créer la base de données
echo "[2] Création de la base de données..."
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS lycee_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" 2>/dev/null && echo "  ✅ Base créée"

# 3. Importer le schéma
echo "[3] Import du schéma..."
if [ -f src/main/resources/schema.sql ]; then
    mysql -u root -proot lycee_db < src/main/resources/schema.sql 2>/dev/null && echo "  ✅ Schéma importé"
else
    echo "  ⚠️  schema.sql non trouvé dans src/main/resources/"
fi

# 4. Créer les répertoires de stockage
echo "[4] Création des répertoires de stockage..."
STORAGE_DIR=$(grep "^storage.dir" src/main/resources/db.properties 2>/dev/null | cut -d= -f2 || echo "$HOME/lycee_storage")
mkdir -p "$STORAGE_DIR/photos" "$STORAGE_DIR/assets"
echo "  ✅ Répertoires créés : $STORAGE_DIR"

# 5. Copier setenv.sh pour Tomcat
echo "[5] Configuration Tomcat..."
if [ -d /opt/apache-tomcat-11.0.21/bin ]; then
    if [ ! -f /opt/apache-tomcat-11.0.21/bin/setenv.sh ]; then
        cp setenv.sh.example /opt/apache-tomcat-11.0.21/bin/setenv.sh 2>/dev/null && echo "  ✅ setenv.sh copié" || echo "  ⚠️  setenv.sh.example non trouvé"
    else
        echo "  ⏭️  setenv.sh existe déjà"
    fi
fi

# 6. Build
echo "[6] Build Maven..."
mvn clean package -q -DskipTests && echo "  ✅ Build réussi"

echo ""
echo "=== Configuration terminée ==="
echo "URL : http://localhost:8080/lycee/login"
echo "Admin par défaut : admin / Password1!"
