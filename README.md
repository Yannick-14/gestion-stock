> Alternative actuelle pour lancer le projet avec Java 21 et le path postgres.jar manuellement mais il existe autre alternative
### 1. Compilation (Java 21)
Compilation du projet
La compilation génère les classes dans le dossier `inventory/dist`.
```powershell
# Commande à lancer depuis : y:\MY PROJECT\Gestion-stock
javac -cp "inventory/lib/postgresql-42.3.7.jar" -d inventory/dist (Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })
```

### 2. Execution et migration des donnees vers la base de donnee
```powershell
# Commande à lancer depuis : y:\MY PROJECT\Gestion-stock
& 'C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe' -cp "inventory/dist;inventory/lib/postgresql-42.3.7.jar" inventory.Main
```

### 3. Lancement de l'application

```powershell
# Commande à lancer depuis : y:\MY PROJECT\Gestion-stock
$CP = "C:\Users\Yannick Fano\AppData\Roaming\Antigravity\User\workspaceStorage\f8664198ba297dfcc0d90a18af3cd007\redhat.java\jdt_ws\Gestion-stock_d82e2b56\bin"
$LIB = "Y:\MY PROJECT\Gestion-stock\inventory\lib\postgresql-42.3.7.jar"

& 'C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe' -cp "$CP;$LIB" inventory.Application
```
