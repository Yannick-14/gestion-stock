# Instructions d'exécution

Pour compiler et lancer la migration de base de données, suivez ces étapes depuis le dossier racine (`Gestion-stock`) :

### 1. Compilation (Java 21)
La compilation génère les classes dans le dossier `inventory/dist`.
```powershell
# Commande à lancer depuis : y:\MY PROJECT\Gestion-stock
javac -cp "inventory/lib/postgresql-42.3.7.jar" -d inventory/dist (Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })
```

### 2. Exécution
Lance le Main du package `inventory` en incluant le driver PostgreSQL.
```powershell
# Commande à lancer depuis : y:\MY PROJECT\Gestion-stock
& 'C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe' -cp "inventory/dist;inventory/lib/postgresql-42.3.7.jar" inventory.Main
```

---
> [!NOTE]
> La structure des fichiers a été réorganisée pour que tout soit centralisé sous le dossier `inventory`. Le `Main.java` se trouve désormais dans `inventory/Main.java` avec le package `inventory`.


# Lancement de application.java

```powershell
# Commande à lancer depuis : y:\MY PROJECT\Gestion-stock
$CP = "C:\Users\Yannick Fano\AppData\Roaming\Antigravity\User\workspaceStorage\f8664198ba297dfcc0d90a18af3cd007\redhat.java\jdt_ws\Gestion-stock_d82e2b56\bin"
$LIB = "Y:\MY PROJECT\Gestion-stock\inventory\lib\postgresql-42.3.7.jar"

& 'C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe' -cp "$CP;$LIB" inventory.Application
```