package inventory.feature.repository.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inventory.feature.repository.connection.DbConnection;

public class GenericMethodCRUD
{
    protected DbConnection connexion;
    protected String nomTable;
    public GenericMethodCRUD(){}
    

    protected DbConnection manaoConnexion()
    {
        DbConnection conn=null;
        try {
            conn=new DbConnection();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.connexion=conn;
    }
    

    protected String getNomTable() {
        return nomTable;
    }

    protected void setNomTable(String table) throws Exception {
        if (table == null || table.isEmpty()) {
            throw new Exception("Indiquer la table ou vous vouller travailler");
        }
        this.nomTable = table;
    }




    protected void insererObjet(Object obj) throws Exception {
        Class<?> objClass = obj.getClass();
        Field[] champs = objClass.getDeclaredFields();

        StringBuilder requete = new StringBuilder("INSERT INTO " + getNomTable() + " (");
        for (int i = 0; i < champs.length; i++) {
            requete.append(champs[i].getName()).append(",");
        }
        requete.deleteCharAt(requete.length() - 1);
        requete.append(") VALUES (");
        for (int i = 0; i < champs.length; i++) {
            requete.append("?,");

        }
        requete.deleteCharAt(requete.length() - 1).append(")");

        try (Connection conn = this.manaoConnexion().getConnexion();
            PreparedStatement ps = conn.prepareStatement(requete.toString())) {

            for (int i = 0; i < champs.length; i++) {
                champs[i].setAccessible(true);
                ps.setObject(i + 1, champs[i].get(obj));
            }
            ps.executeUpdate();
        }
        this.manaoConnexion().close_serv();
    }




    protected String insererObjetAndGetId(Object obj) throws Exception
    {
        Class<?> objClass = obj.getClass();
        Field[] champs = objClass.getDeclaredFields();

        StringBuilder requete = new StringBuilder("INSERT INTO " + getNomTable() + " (");
        for (int i = 0; i < champs.length; i++) {
            requete.append(champs[i].getName()).append(",");
        }
        requete.deleteCharAt(requete.length() - 1);
        requete.append(") VALUES (");
        for (int i = 0; i < champs.length; i++) {
            requete.append("?,");
        }
        requete.deleteCharAt(requete.length() - 1).append(")");

        String lastInsertId = "";
        try (Connection conn = this.manaoConnexion().getConnexion();
            PreparedStatement ps = conn.prepareStatement(requete.toString(), Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < champs.length; i++) {
                champs[i].setAccessible(true);
                ps.setObject(i + 1, champs[i].get(obj));
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    lastInsertId = rs.getString(1);
                    System.out.println("Dans insererObjetAndGetId: "+lastInsertId);
                }
            }
        }
        this.manaoConnexion().close_serv();
        return lastInsertId;
    }




    protected <T> List<T> getAllDataObject(T object) throws Exception {
        Class<T> objClass = (Class<T>) object.getClass();
        String requete = "SELECT * FROM " + getNomTable();
        List<T> resultats = new ArrayList<>();

        try (Connection conn = this.manaoConnexion().getConnexion();
            PreparedStatement ps = conn.prepareStatement(requete);
            ResultSet rs = ps.executeQuery()) {

            Field[] champs = objClass.getDeclaredFields();

            while (rs.next()) {
                T objet = objClass.getDeclaredConstructor().newInstance();
                for (Field champ : champs) {
                    champ.setAccessible(true);
                    champ.set(objet, rs.getObject(champ.getName()));
                }
                resultats.add(objet);
            }
        }
        this.manaoConnexion().close_serv();
        return resultats;
    }




    protected <T> List<T> selectObjetRequete(T object, String whereClause) throws Exception {
        Class<T> objClass = (Class<T>) object.getClass();
        String query = "SELECT * FROM " + getNomTable();
        
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            query += " " + whereClause;
        }
        
        List<T> resultats = new ArrayList<>();
    
        try (Connection conn = this.manaoConnexion().getConnexion();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
    
            Field[] champs = objClass.getDeclaredFields();
    
            while (rs.next()) {
                T objet = objClass.getDeclaredConstructor().newInstance();
                for (Field champ : champs) {
                    champ.setAccessible(true);
                    champ.set(objet, rs.getObject(champ.getName()));
                }
                resultats.add(objet);
            }
        }
        this.manaoConnexion().close_serv();
        return resultats;
    }





    protected List<Map<String, Object>> selectDataAvecConditionCalcul(String requete, String whereClause) throws Exception
    {
        String query = "SELECT " + requete + " FROM " + getNomTable();
        
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            query += " WHERE " + whereClause;
        }
        
        List<Map<String, Object>> resultats = new ArrayList<>();
    
        try (Connection conn = this.manaoConnexion().getConnexion();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
    
            while (rs.next()) {
                Map<String, Object> ligne = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnLabel);
                    ligne.put(columnLabel, value);
                }
    
                resultats.add(ligne);
            }
        }
        this.manaoConnexion().close_serv();
        return resultats;
    }
    


    

    

    public void delete(Object obj, String requete) throws Exception {
        StringBuilder query = new StringBuilder("DELETE FROM " + getNomTable());
    
        if (requete != null && !requete.trim().isEmpty()) {
            query.append(" ").append(requete);
        }
    
        try (Connection conn = this.manaoConnexion().getConnexion();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
    
            Class<?> objClass = obj.getClass();
            Field[] champs = objClass.getDeclaredFields();
    
            int paramIndex = 1;
            for (Field champ : champs) {
                champ.setAccessible(true);
                String fieldName = champ.getName();
    
                if (requete.contains(fieldName + " = ?")) {
                    ps.setObject(paramIndex++, champ.get(obj));
                }
            }
    
            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected + " effacer.");
            this.manaoConnexion().close_serv();
        }
    }
    

    public void updateObjet(Object obj, String whereClause) throws Exception {
        // Start building the UPDATE query
        StringBuilder query = new StringBuilder("UPDATE " + getNomTable() + " SET ");
        Class<?> objClass = obj.getClass();
        Field[] champs = objClass.getDeclaredFields();
    
        // To keep track of parameters for the PreparedStatement
        int paramIndex = 1;
        boolean firstField = true;
    
        // Use a StringBuilder to dynamically build the query
        for (int i = 0; i < champs.length; i++) {
            Field champ = champs[i];
            champ.setAccessible(true);
    
            // Ignore the first column (primary key)
            if (i == 0) {
                continue; // Skip the first field (e.g., idOlona)
            }
    
            // Only update fields that are set in the object
            Object value = champ.get(obj);
            if (value != null) {
                if (!firstField) {
                    query.append(", ");
                }
                query.append(champ.getName()).append(" = ?");
                firstField = false;
            }
        }
    
        // Append the WHERE clause
        query.append(" ").append(whereClause);
    
        // Now prepare the statement with the complete query
        try (Connection conn = this.manaoConnexion().getConnexion();
            PreparedStatement ps = conn.prepareStatement(query.toString())) {
    
            // Set parameters for fields that are not null
            for (int i = 0; i < champs.length; i++) {
                Field champ = champs[i];
                champ.setAccessible(true);
    
                // Ignore the first column (primary key)
                if (i == 0) {
                    continue; // Skip the first field
                }
    
                Object value = champ.get(obj);
                if (value != null) {
                    ps.setObject(paramIndex++, value);
                }
            }
    
            // Execute the update
            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected + " row(s) updated.");
        }
    }           
}