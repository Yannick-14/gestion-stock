import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class GenerateTable {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/fleur";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "fanomezantsoa";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Générez des classes pour les tables
            // generateClassFromTable(connection, "categorie", "table", "table");
            // generateClassFromTable(connection, "fleurs", "table", "table");
            // generateClassFromTable(connection, "prix", "table", "table");
            // generateClassFromTable(connection, "administrateur", "table", "table");
            // generateClassFromTable(connection, "panier", "table", "table");
            // generateClassFromTable(connection, "commande", "table", "table");
            // generateClassFromTable(connection, "client", "table", "table");
            // generateClassFromTable(connection, "promotion", "table", "table");
            // generateClassFromTable(connection, "promotionfleur", "table", "table");
            // generateClassFromTable(connection, "detailscommande", "table", "table");
            // generateClassFromTable(connection, "paiementcommande", "table", "table");
            // generateClassFromTable(connection, "prixoffre", "table", "table");
            // generateClassFromTable(connection, "remise", "table", "table");
            
            // Générez des classes pour les vues
            // generateClassFromView(connection, "fleurinfo", "view", "view");
            // generateClassFromView(connection, "promotioninfo", "view", "view");
            // generateClassFromView(connection, "mespaniers", "view", "view");
            generateClassFromView(connection, "listevente", "view", "view");
            // generateClassFromView(connection, "historiqueprix", "view", "view");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateClassFromTable(Connection connection, String tableName, String outputDir, String packageName)
            throws SQLException, IOException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, tableName, null);
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

        // Récupérer la clé primaire
        String primaryKeyColumn = null;
        if (primaryKeys.next()) {
            primaryKeyColumn = primaryKeys.getString("COLUMN_NAME");
        }

        // Stocker les colonnes et leurs types
        Map<String, String> columnData = new HashMap<>();
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = mapSQLTypeToJavaType(columns.getString("TYPE_NAME"));
            columnData.put(columnName, columnType);
        }

        // Générer la classe
        StringBuilder classCode = new StringBuilder();
        String className = capitalize(tableName.toLowerCase());  // Convertir tout en minuscules avant de capitaliser

        // Ajouter le package
        classCode.append("package ").append(packageName).append(";\n\n");

        // Importations nécessaires
        classCode.append("import java.util.*;\n");
        classCode.append("import java.sql.*;\n\n");

        // Définir la classe
        classCode.append("public class ").append(className).append(" {\n\n");

        // Ajouter les attributs
        for (Map.Entry<String, String> entry : columnData.entrySet()) {
            classCode.append("    private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";\n");
        }

        if (primaryKeyColumn != null) {
            // Constructeur par défaut
            classCode.append("\n    public ").append(className).append("() {\n    }\n\n");

            // Constructeur avec tous les attributs sauf la clé primaire
            classCode.append("    public ").append(className).append("(");
            boolean first = true;
            for (Map.Entry<String, String> entry : columnData.entrySet()) {
                if (!entry.getKey().equals(primaryKeyColumn)) {
                    if (!first) {
                        classCode.append(", ");
                    }
                    classCode.append(entry.getValue()).append(" ").append(entry.getKey());
                    first = false;
                }
            }
            classCode.append(") {\n");
            for (Map.Entry<String, String> entry : columnData.entrySet()) {
                if (!entry.getKey().equals(primaryKeyColumn)) {
                    classCode.append("        this.set").append(capitalize(entry.getKey()))
                            .append("(").append(entry.getKey()).append(");\n");
                }
            }
            classCode.append("    }\n\n");

            // Getters et Setters
            for (Map.Entry<String, String> entry : columnData.entrySet()) {
                String attributeName = entry.getKey();
                String attributeType = entry.getValue();
                classCode.append("    public ").append(attributeType).append(" get").append(capitalize(attributeName))
                        .append("() {\n        return ").append(attributeName).append(";\n    }\n\n");
                classCode.append("    public void set").append(capitalize(attributeName)).append("(").append(attributeType)
                        .append(" ").append(attributeName).append(") {\n        this.").append(attributeName).append(" = ")
                        .append(attributeName).append(";\n    }\n\n");
            }
        } else {
            // Si pas de clé primaire
            for (Map.Entry<String, String> entry : columnData.entrySet()) {
                String attributeName = entry.getKey();
                String attributeType = entry.getValue();
                classCode.append("    public ").append(attributeType).append(" get").append(capitalize(attributeName))
                        .append("() {\n        return ").append(attributeName).append(";\n    }\n\n");
                classCode.append("    public void set").append(capitalize(attributeName)).append("(").append(attributeType)
                        .append(" ").append(attributeName).append(") {\n        this.").append(attributeName).append(" = ")
                        .append(attributeName).append(";\n    }\n\n");
            }
        }

        classCode.append("}");

        // Écrire le code dans un fichier
        String filePath = outputDir + "/" + className + ".java";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(classCode.toString());
        }

        System.out.println("Classe générée avec succès : " + filePath);
    }


    public static void generateClassFromView(Connection connection, String viewName, String outputDir, String packageName)
            throws SQLException, IOException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, viewName, null);

        // Stocker les colonnes et leurs types
        Map<String, String> columnData = new HashMap<>();
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = mapSQLTypeToJavaType(columns.getString("TYPE_NAME"));
            columnData.put(columnName, columnType);
        }

        // Générer la classe
        StringBuilder classCode = new StringBuilder();
        String className = capitalize(viewName.toLowerCase());  // Convertir tout en minuscules avant de capitaliser

        // Ajouter le package
        classCode.append("package ").append(packageName).append(";\n\n");

        // Importations nécessaires
        classCode.append("import java.util.*;\n");
        classCode.append("import java.sql.*;\n\n");

        // Définir la classe
        classCode.append("public class ").append(className).append(" {\n\n");

        // Ajouter les attributs
        for (Map.Entry<String, String> entry : columnData.entrySet()) {
            classCode.append("    private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";\n");
        }

        // Getters
        for (Map.Entry<String, String> entry : columnData.entrySet()) {
            String attributeName = entry.getKey();
            String attributeType = entry.getValue();
            classCode.append("    public ").append(attributeType).append(" get").append(capitalize(attributeName))
                    .append("() {\n        return ").append(attributeName).append(";\n    }\n\n");
        }

        classCode.append("}");

        // Écrire le code dans un fichier
        String filePath = outputDir + "/" + className + ".java";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(classCode.toString());
        }

        System.out.println("Classe générée avec succès : " + filePath);
    }

    private static String mapSQLTypeToJavaType(String sqlType) {
        switch (sqlType.toLowerCase()) {
            case "varchar":
            case "text":
                return "String";
            case "int":
            case "integer": // Add proper case for PostgreSQL
            case "serial": 
            case "numeric":
                return "int";
            case "double precision":
                return "double";
            case "boolean":
                return "boolean";
            case "date":
                return "java.util.Date";
            case "timestamp":
                return "java.sql.Timestamp";
            case "bytea":
            case "blob":
                return "byte[]";
            default:
                return "double";
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
