package inventory.feature.repository.dao;

import java.util.*;

// import javax.servlet.http.HttpSession;

public class GenericMethodDAO<O> extends GenericMethodCRUD
{
    public GenericMethodDAO(){}
    public void insertion(String table,Object objetInserer)
    {
        try
        {
            this.setNomTable(table);
            this.insererObjet(objetInserer);
            System.out.println("Succes insertion dans "+table);
        } catch (Exception e) {
            System.out.println("Echec d'insertion");
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public String recuperationDernierInsertion(String table, Object objetInserer)
    {
        String lastInsertId ="";
        try {
            this.setNomTable(table);
            lastInsertId = this.insererObjetAndGetId(objetInserer);
            System.out.println("Reussite de Id dernier= "+lastInsertId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return lastInsertId;
    }

    public List<O> recuperationEntier(String table, O classes) throws Exception {
        this.setNomTable(table);
        List<O> resultaList = null;
        try {
            resultaList = this.getAllDataObject(classes);
            if (resultaList == null) {
                throw new Exception("Aucune donner dans "+table);
            } else {
                System.out.println("Recuperation bien fait dans "+table);
            }
        } catch (Exception e) {
            System.out.println("Erreur de recuperation");
            System.err.println(e.getMessage());
        }
        return resultaList;
    }

    public void miseAjour(String table,Object classe , String requeteUpdate) throws Exception
    {
        this.setNomTable(table);
        try {
            super.updateObjet(classe, requeteUpdate);
            System.out.println("MISE A JOUR TERMINE dans "+table);
        } catch (Exception e) {
            // e.getMessage();
            e.printStackTrace();
            System.out.println("MISE A JOUR EN ECHEC dans "+table);
        }
    }

    public List<O> recuperationAvecCondition(String table,O objet,String requeteConditionSelect) throws Exception
    {
        this.setNomTable(table);
        List<O> resultaList = null;
        try
        {
            resultaList= super.selectObjetRequete(objet, requeteConditionSelect);
            System.out.println("SUCCES DE RECUPERATION DANS "+table);
        } catch (Exception e) {
            System.err.println("ECHEC DE RECUPERATION DANS= "+table);
            System.err.println(e.getMessage());
        }
        return resultaList;
    }


    public List<Map<String, Object>> requeteUsageFonction(String table,String requete,String conditionFaculte) throws Exception
    {
        List<Map<String, Object>> resultats=null;
        try {
            resultats = super.selectDataAvecConditionCalcul(requete, conditionFaculte);
        } catch (Exception e) {
            throw new Exception("ECHEC DE REQUETE");
        }
        return resultats;
    }

    public void suppression(String table,Object objet,String requete) throws Exception
    {
        try {
            this.setNomTable(table);
            this.delete(objet, requete);
            System.out.println("Suppression reussie.");
        } catch (Exception e) {
            throw new Exception("Echec de suppression: "+e.getMessage());
        }
    }
}
