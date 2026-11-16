# 📦 Gestion de Stock

Application de gestion de stock avec interface Swing (Java 21) et base de données PostgreSQL.

---

## Pré-requis

| Outil | Version | Vérification |
|-------|---------|--------------|
| Java (JDK) | 21+ | `java -version` |
| PostgreSQL | 12+ | `psql --version` |

> **Maven n'est pas nécessaire** — le projet inclut un wrapper (`mvnw`) qui le télécharge automatiquement.

---

## Installation

### 1. Cloner le projet

```bash
git clone https://github.com/Yannick-14/gestion-stock.git
cd gestion-stock
```

### 2. Créer la base de données PostgreSQL

Connectez-vous à PostgreSQL et créez la base :

```sql
CREATE DATABASE inventory;
```

### 3. Compiler le projet

```powershell
.\mvnw.cmd compile
```

```bash
./mvnw compile
```

### 4. Migrer le schéma de la base de données

Crée les tables et insère les données de référence (types de mouvement, méthodes de gestion) :

```powershell
.\mvnw.cmd exec:java -Dexec.mainClass="inventory.MigrationMain"
```

Ou avec `java` directement :

```powershell
java -cp "target\classes;inventory\lib\postgresql-42.3.7.jar" inventory.MigrationMain
```

```bash
java -cp "target/classes:inventory/lib/postgresql-42.3.7.jar" inventory.MigrationMain
```

### 5. (Optionnel) Insérer des données de test

```powershell
java -cp "target\classes;inventory\lib\postgresql-42.3.7.jar" inventory.TestSeedMain
```

```bash
java -cp "target/classes:inventory/lib/postgresql-42.3.7.jar" inventory.TestSeedMain
```

---

## Lancement de l'application

```powershell
java -cp "target\classes;inventory\lib\postgresql-42.3.7.jar" inventory.Application
```

```bash
java -cp "target/classes:inventory/lib/postgresql-42.3.7.jar" inventory.Application
```

---

## Construire et lancer le JAR

```powershell
.\mvnw.cmd package -DskipTests
java -jar target\gestion-stock-1.0.0.jar
```

```bash
./mvnw package -DskipTests
java -jar target/gestion-stock-1.0.0.jar
```

---

## Lancer les tests

Les tests unitaires ne nécessitent **aucune base de données** (POJOs purs).

```powershell
.\mvnw.cmd test
```

```bash
./mvnw test
```

> Voir [`ci_cd/README.md`](ci_cd/README.md) pour plus de détails sur les tests et le CI/CD.

---

## Utilitaires de base de données

| Commande | Description |
|----------|-------------|
| `java -cp "target\classes;inventory\lib\postgresql-42.3.7.jar" inventory.MigrationMain` | Créer/mettre à jour les tables + données de référence |
| `java -cp "target\classes;inventory\lib\postgresql-42.3.7.jar" inventory.TestSeedMain` | Insérer des articles et mouvements de test |
| `java -cp "target\classes;inventory\lib\postgresql-42.3.7.jar" inventory.CleanupMain` | Vider toutes les tables |

> **Linux/macOS** : remplacer `\` par `/` et `;` par `:` dans les classpaths.

---

## Structure du projet

```
gestion-stock/
├── inventory/                  # Sources Java (package inventory.*)
│   ├── models/                 # Entités (Article, StockMovement, etc.)
│   ├── feature/
│   │   ├── annotation/         # @FormIgnore
│   │   ├── applicationService/ # GenerateTable (migration)
│   │   ├── repository/         # Connexion BDD + DAO générique
│   │   └── stock/              # StockManager
│   ├── ui/                     # Interface Swing
│   ├── lib/                    # Dépendances JAR
│   ├── Application.java        # Point d'entrée principal
│   ├── MigrationMain.java      # Migration du schéma
│   ├── TestSeedMain.java       # Données de test
│   └── CleanupMain.java        # Nettoyage des tables
├── ci_cd/                      # Tests unitaires + documentation CI/CD
├── .github/workflows/          # Pipelines GitHub Actions
├── pom.xml                     # Configuration Maven
├── mvnw / mvnw.cmd             # Maven Wrapper
└── manifest.txt                # Manifest pour le JAR
```
