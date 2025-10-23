# Template pour GEST_EMP Serveur de gestion de calendrier avec Javalin

Ce projet a été converti pour utiliser Maven.

Commandes utiles:
- mvn clean test
- mvn package

Prérequis:
- Java 21 (modifiable dans le pom.xml via maven.compiler.source/target)


## How to run

- Compiler et tester:
  - ./mvnw clean test (Windows: mvnw.cmd clean test)
- Packager:
  - ./mvnw package
- Exécuter l’application:
  - java -jar target/gest-emp-server-template-1.0.0-SNAPSHOT.jar
  - ou démarrer la classe Main depuis l’IDE: tn.iset.m2glnt.server.Main
  - changer le port (par défaut 8080):
    - ./mvnw -Pexec exec:java -Dserver.port=9090
    - ou définir l’ENV PORT=9090

L’application démarre un serveur Javalin sur le port 8080 avec un endpoint:
- GET /timeslots/{dateInterval}
  - {dateInterval} = "YYYY-MM-DD&YYYY-MM-DD"
  - Exemple: curl http://localhost:8080/timeslots/2024-01-01&2024-01-31

## Maven exec (optionnel)

Vous pouvez lancer la classe Main via Maven:

```bash
./mvnw -q exec:java -Dexec.mainClass=fr.univ_amu.m1info.server.Main
```

Ce profil nécessite le plugin exec configuré dans le pom.xml.
