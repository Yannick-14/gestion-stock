> Alternative avec JAVAC/JAVA (Version 21)

## Commande à lancer depuis : y:\MY PROJECT\Gestion-stock
## Compilation
```powershell
javac -cp "inventory/lib/postgresql-42.3.7.jar" -d inventory/dist (Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })
```

## Gestion de la Base de Données

### 1. Migration (Schéma et données de base)
```powershell
java -cp "inventory/dist;inventory/lib/postgresql-42.3.7.jar" inventory.MigrationMain
```

### 2. Insertion de données de test (Articles et Mouvements)
```powershell
java -cp "inventory/dist;inventory/lib/postgresql-42.3.7.jar" inventory.TestSeedMain
```

### 3. Nettoyage (Vider les tables)
```powershell
java -cp "inventory/dist;inventory/lib/postgresql-42.3.7.jar" inventory.CleanupMain
```

## Execution avec java depuis : y:\MY PROJECT\Gestion-stock
```powershell
java -cp "inventory/dist;inventory/lib/postgresql-42.3.7.jar" inventory.Application
```

## Execution avec JAR depuis : y:\MY PROJECT\Gestion-stock
```powershell
jar cvfm gestion-stock.jar manifest.txt -C inventory/dist .
java -jar gestion-stock.jar
```


<!-- README POSTGRES -->
## connect DB
```
\c inventory
```
## list tables
```
\dt
```