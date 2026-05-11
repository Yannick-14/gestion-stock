# Dossier ApplicationService

>> classe GenerateTable.java

## attributs

    -Tableau classe
    -Objet classe

## fonctions

    -checkStateTable()
    -updateStateTable()

    -createTable()
        ** creer les tables par rapport à nos classes
        ** verifier si les tables existent deja et si il y a une mise à jour par rapport à nos classes pour les tables

## Dossier repository

>> classe DbConnection

    -Attributs --get only and private
    Driver (String), dbName(String), password (String)

    -Fonctions
    Constructeur de connection

>> classe GenericMethodCRUD
## fonctions
-findAllData(Object O) <!-- recuperer les donnees d'un objet -->
-read(id) <!-- recuperer une donnée par rapport à son id -->
-findDataWithRequest(Object O, Object paramsQuery) <!-- recuperer les donnees avec des conditions de filtres -->
-insertData(Object O) <!-- inserer les donnees d'un objet -->
-update(id, Object params) <!-- mettre a jour les donnees d'un objet -->
-delete(id) <!-- Supprimer une ligne -->

## Dossier utils

## classe StockStatus

# dossier models

<!-- gettersFormulaire:
    -State (visible ou non)
    -DefaultValue (valeur par defaut à afficher si il y a)
    -Placeholder () -->

>> classe stockManagementMethod <!-- liste des methodes d'insertion de stock: cump, lifo, fifo -->

    - ATTRIBUTS:
    .id(INT) , nameMethod (String)

>> classe typeStockMovement <!-- liste des methodes de mouvement de stock: entreee, sortie -->

    - ATTRIBUTS:
    .id(INT) , nameType (String)

>> classe article <!-- liste de nos produits -->

    - ATTRIBUTS:
    .id(INT), nameArticle (String), stockManagementMethod (Object --classe methodManagement), createdAt (DateTime)

>> classe stockMovement <!-- liste de nos mouvements de stock -->

    - ATTRIBUTS:
    .id(INT), article (Object --classe article),typeStockMovement (Object --classe type), createdAt (DateTime), quantity (number), unitPrice (double)
