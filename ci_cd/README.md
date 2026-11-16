# 🔄 CI/CD — Gestion-Stock

Ce dossier centralise tout ce qui concerne l'intégration et le déploiement continus du projet.

## 📁 Structure

```
ci_cd/
├── tests/
│   └── inventory/
│       └── models/
│           └── ArticleTest.java
├── test-reports/               # Rapports XML générés (non versionnés)
└── README.md

.github/
└── workflows/
    ├── ci.yml                  # Pipeline CI principal (lint, test, package)
    └── codeql.yml              # Analyse de sécurité CodeQL

pom.xml                         # Configuration Maven
mvnw / mvnw.cmd                 # Maven Wrapper (pas besoin d'installer Maven)
```

---

## 🚀 Pipelines GitHub Actions

| Workflow | Déclencheur | Jobs |
|----------|-------------|------|
| `ci.yml` | Push (toutes branches) / PR vers `main`, `develop` | Lint → Test → Package JAR |
| `codeql.yml` | Push / PR vers `main` + hebdomadaire | Analyse de sécurité |

---

## ▶️ Lancer les tests en local

> **Pré-requis** : Java 21+ uniquement. Maven n'est **pas nécessaire** grâce au wrapper (`mvnw`).
> Les tests ne nécessitent **aucune base de données** — Article est un POJO pur.

**Lancer tous les tests :**

```powershell
.\mvnw.cmd test
```

```bash
./mvnw test
```

**Lancer un seul test :**

```powershell
.\mvnw.cmd test -Dtest="ArticleTest#testDefaultConstructor"
```

```bash
./mvnw test -Dtest="ArticleTest#testDefaultConstructor"
```

**Compiler sans tester :**

```powershell
.\mvnw.cmd compile
```

```bash
./mvnw compile
```

**Construire le JAR :**

```powershell
.\mvnw.cmd package
```

```bash
./mvnw package
```

Le JAR est généré dans `target/gestion-stock-1.0.0.jar`.

**Nettoyer :**

```powershell
.\mvnw.cmd clean
```

```bash
./mvnw clean
```

---

## ✅ Tests couverts

### `ArticleTest` — 13 tests (aucune BDD requise)

| Catégorie | Méthode |
|-----------|---------|
| Constructeur vide → valeurs par défaut | `testDefaultConstructor` |
| Constructeur paramétré → nameArticle & method initialisés | `testParameterizedConstructor` |
| Constructeur paramétré → `createdAt` auto-initialisé | `testParameterizedConstructorSetsCreatedAt` |
| `setId` / `getId` | `testSetGetId` |
| `setNameArticle` / `getNameArticle` | `testSetGetNameArticle` |
| `setStockManagementMethod` / `getStockManagementMethod` | `testSetGetStockManagementMethod` |
| `setCreatedAt` / `getCreatedAt` | `testSetGetCreatedAt` |
| `toString` → retourne le nom | `testToString` |
| `toString` → retourne `null` si nom non défini | `testToStringWhenNameIsNull` |
| Changement de méthode de stock | `testChangeStockManagementMethod` |
| Indépendance entre deux articles | `testArticlesAreIndependent` |
| Accepte un nom vide | `testSetEmptyNameArticle` |
| Accepte méthode `null` (désassociation) | `testSetNullStockManagementMethod` |
