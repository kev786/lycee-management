#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "==> Build..."
mvn clean package -q -DskipTests

echo "==> Arrêt Tomcat..."
/opt/apache-tomcat-11.0.21/bin/shutdown.sh > /dev/null 2>&1 || true
sleep 2

echo "==> Déploiement..."
rm -rf /opt/apache-tomcat-11.0.21/webapps/lycee*
cp target/lycee.war /opt/apache-tomcat-11.0.21/webapps/

echo "==> Démarrage Tomcat..."
/opt/apache-tomcat-11.0.21/bin/startup.sh > /dev/null
sleep 3

echo "✅ Redéploiement terminé — http://localhost:8080/lycee/login"
