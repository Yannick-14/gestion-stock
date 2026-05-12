package inventory.feature.repository.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbConnection {
    private Connection connexion;
    private PreparedStatement preparedStatement;
    private ResultSet result;

    private static final String URL = "jdbc:postgresql://localhost:5432/inventory?charset=UTF-8";
    private static final String USER="postgres";
    private static final String PASSWORD="fanomezantsoa";

    public PreparedStatement getPrepaStatement()
    {
    	return this.preparedStatement;
    }

    public void setPrepaStatement(PreparedStatement stateQuery)
    {
    	this.preparedStatement = stateQuery;
    }

    public Connection getConnexion()
    {
    	return this.connexion;
    }

    public void close_serv() throws Exception {
        try {
            if (this.preparedStatement != null) {
                this.preparedStatement.close();
            }
        } catch (Exception e) {
            // Gérer l'exception de fermeture de la déclaration
            e.printStackTrace();
        }
        try {
            if (this.connexion != null) {
                this.connexion.close();
            }
        } catch (Exception e) {
            // Gérer l'exception de fermeture de la connexion
            e.printStackTrace();
        }
        System.out.println("CLOSE REUSSIE");
    }

    public DbConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        this.connexion = DriverManager.getConnection(URL,USER,PASSWORD);
        System.out.println("Bien connecter");
    }
}
