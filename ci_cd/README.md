# 🔄 CI/CD — Gestion-Stock

Ce dossier centralise tout ce qui concerne l'intégration et le déploiement continus du projet.

## 📁 Structure

## 📁 Structure

```
ci-cd/
├── tests/                      # Sources des tests unitaires
│   └── inventory/
│       └── models/
│           └── ArticleTest.java
└── README.md                   # Ce fichier

inventory/lib/                  # JARs de dépendances (prod + test)
├── postgresql-42.3.7.jar
└── junit-platform-console-standalone-1.10.2.jar

.github/
└── workflows/
    └── ci.yml                  # Pipeline GitHub Actions principal
```

## 🚀 Pipelines GitHub Actions

| Workflow | Déclencheur | Description |
|----------|-------------|-------------|
| `ci.yml` | Push / PR sur `main` & `develop` | Compile, teste, construit le JAR |

## ▶️ Lancer les tests en local

### Pré-requis
- Java 21+
- Le JAR JUnit est dans `inventory/lib/` (déjà versionné avec le projet)

### Compilation des sources
```bash
# Depuis la racine du projet
javac -cp "inventory/lib/postgresql-42.3.7.jar" \
      -d out \
      $(find inventory -name "*.java" -not -path "*/lib/*")
```

### Compilation des tests
```bash
javac -cp "out:inventory/lib/junit-platform-console-standalone-1.10.2.jar:inventory/lib/postgresql-42.3.7.jar" \
      -d out \
      ci-cd/tests/inventory/models/ArticleTest.java
```

### Exécution des tests
```bash
java -jar inventory/lib/junit-platform-console-standalone-1.10.2.jar \
     --class-path "out:inventory/lib/postgresql-42.3.7.jar" \
     --scan-class-path
```

> **Windows** : remplacer `:` par `;` dans les classpaths.

## ✅ Tests couverts

### `ArticleTest` (12 tests)

| Catégorie | Test |
|-----------|------|
| Constructeurs | Constructeur vide → valeurs par défaut |
| Constructeurs | Constructeur paramétré → nameArticle & method initialisés |
| Constructeurs | Constructeur paramétré → `createdAt` auto-initialisé |
| Getters/Setters | `setId` / `getId` |
| Getters/Setters | `setNameArticle` / `getNameArticle` |
| Getters/Setters | `setStockManagementMethod` / `getStockManagementMethod` |
| Getters/Setters | `setCreatedAt` / `getCreatedAt` |
| toString | Retourne le nom de l'article |
| toString | Retourne `null` si nom non défini |
| Métier | Changement de méthode de stock |
| Métier | Indépendance entre deux articles |
| Métier | Accepte nom vide & méthode `null` |
