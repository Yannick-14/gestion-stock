package inventory.feature.repository.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private String driver;
    private String dbName;
    private String username;
    private String password;


    private static String HOST = "localhost";
    private static String PORT = "5432";

    private static DbConnection instance;
    private Connection connexion;

    private DbConnection() throws Exception {
        this.driver = "org.postgresql.Driver";
        this.dbName = "inventory";
        this.username = "postgres";
        this.password = "fanomezantsoa";

        String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + this.dbName;
        Class.forName(this.driver);
        this.connexion = DriverManager.getConnection(url, this.username, this.password);
        System.out.println("[DbConnection] Connexion établie à la base: " + this.dbName);
    }

    // ── Accès Singleton ───────────────────────────────────────────────────────
    public static DbConnection getInstance() throws Exception {
        if (instance == null || instance.connexion == null || instance.connexion.isClosed()) {
            instance = new DbConnection();
        }
        return instance;
    }

    public String getDriver() { return driver; }
    public String getDbName() { return dbName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public Connection getConnexion() { return connexion; }

    public void close() {
        try {
            if (connexion != null && !connexion.isClosed()) {
                connexion.close();
                System.out.println("[DbConnection] Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("[DbConnection] Erreur lors de la fermeture: " + e.getMessage());
        } finally {
            instance = null;
        }
    }
}
