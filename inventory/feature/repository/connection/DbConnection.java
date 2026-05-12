package inventory.feature.repository.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestion de la connexion à la base de données (Singleton).
 * Attributs en lecture seule (get uniquement).
 */
public class DbConnection {

    // ── Attributs de configuration ────────────────────────────────────────────
    private final String driver;
    private final String dbName;
    private final String username;
    private final String password;

    // URL complète construite à partir des attributs
    private static final String HOST = "localhost";
    private static final String PORT = "5432";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static DbConnection instance;
    private Connection connexion;

    // ── Constructeur privé ────────────────────────────────────────────────────
    private DbConnection() throws Exception {
        this.driver   = "org.postgresql.Driver";
        this.dbName   = "inventory";
        this.username = "postgres";
        this.password = "fanomezantsoa";

        String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + this.dbName;
        Class.forName(this.driver);
        this.connexion = DriverManager.getConnection(url, this.username, this.password);
        System.out.println("[DbConnection] Connexion établie à la base: " + this.dbName);
    }

    // ── Accès Singleton ───────────────────────────────────────────────────────
    /**
     * Retourne l'instance unique de DbConnection.
     * Recrée la connexion si elle est fermée ou nulle.
     */
    public static DbConnection getInstance() throws Exception {
        if (instance == null || instance.connexion == null || instance.connexion.isClosed()) {
            instance = new DbConnection();
        }
        return instance;
    }

    // ── Getters (lecture seule) ───────────────────────────────────────────────
    public String getDriver()   { return driver;   }
    public String getDbName()   { return dbName;   }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public Connection getConnexion() { return connexion; }

    // ── Fermeture ─────────────────────────────────────────────────────────────
    /**
     * Ferme la connexion et réinitialise le Singleton.
     */
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
