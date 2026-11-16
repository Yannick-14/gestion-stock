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

inventory/lib/
├── postgresql-42.3.7.jar
└── junit-platform-console-standalone-1.10.2.jar

.github/
└── workflows/
    └── ci.yml
```

---

## 🚀 Pipeline GitHub Actions

| Workflow | Déclencheur | Description |
|----------|-------------|-------------|
| `ci.yml` | Push / PR sur `main` & `develop` | Compile, teste, construit le JAR |

---

## ▶️ Lancer les tests en local

> **Pré-requis** : Java 21+ (`java -version` pour vérifier).
> Les tests de modèles ne nécessitent **aucune connexion à la base de données**.

Toutes les commandes sont à lancer depuis la **racine du projet**.

---

### 🪟 Windows — PowerShell

**Lancer tous les tests :**

```powershell
New-Item -ItemType Directory -Force -Path out | Out-Null; javac -cp "inventory\lib\postgresql-42.3.7.jar" -d out inventory\models\Article.java inventory\models\StockManagementMethod.java inventory\feature\annotation\FormIgnore.java; Set-Location ci_cd\tests; javac -cp "..\..\out;..\..\inventory\lib\junit-platform-console-standalone-1.10.2.jar" -d "..\..\out" inventory\models\ArticleTest.java; Set-Location ..\.. ; java -jar inventory\lib\junit-platform-console-standalone-1.10.2.jar execute --class-path "out" --select-class inventory.models.ArticleTest --reports-dir ci_cd\test-reports --fail-if-no-tests
```

**Lancer un seul test :**

```powershell
java -jar inventory\lib\junit-platform-console-standalone-1.10.2.jar execute --class-path "out" --select-method "inventory.models.ArticleTest#testDefaultConstructor" --fail-if-no-tests
```

**Nettoyer les fichiers compilés :**

```powershell
Remove-Item -Recurse -Force out
```

---

### 🐧 Linux / macOS — bash

**Lancer tous les tests :**

```bash
mkdir -p out && javac -cp "inventory/lib/postgresql-42.3.7.jar" -d out inventory/models/Article.java inventory/models/StockManagementMethod.java inventory/feature/annotation/FormIgnore.java && cd ci_cd/tests && javac -cp "../../out:../../inventory/lib/junit-platform-console-standalone-1.10.2.jar" -d "../../out" inventory/models/ArticleTest.java && cd ../.. && java -jar inventory/lib/junit-platform-console-standalone-1.10.2.jar execute --class-path "out" --select-class inventory.models.ArticleTest --reports-dir ci_cd/test-reports --fail-if-no-tests
```

**Lancer un seul test :**

```bash
java -jar inventory/lib/junit-platform-console-standalone-1.10.2.jar execute --class-path "out" --select-method "inventory.models.ArticleTest#testDefaultConstructor" --fail-if-no-tests
```

**Nettoyer les fichiers compilés :**

```bash
rm -rf out
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

---

## 📄 À quoi servent les `test-reports/` ?

| Fichier | Rôle |
|---------|------|
| `TEST-junit-jupiter.xml` | ⭐ Rapport principal — résultat de chaque test (nom, durée, succès/échec) |
| `TEST-junit-vintage.xml` | Rapport vide — moteur JUnit 4 (non utilisé ici) |
| `TEST-junit-platform-suite.xml` | Rapport vide — moteur suites (non utilisé ici) |

Ces fichiers sont ignorés par Git et se regénèrent à chaque run.
Sur GitHub Actions, ils sont uploadés en artifact et affichés dans le résumé de la PR.
