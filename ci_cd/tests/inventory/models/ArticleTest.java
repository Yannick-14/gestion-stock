package inventory.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

/**
 * Tests unitaires pour la classe Article.
 * Couvre : constructeurs, getters/setters, toString, et validation métier.
 */
@DisplayName("Tests unitaires - Article")
class ArticleTest {

    private StockManagementMethod methodFIFO;
    private StockManagementMethod methodLIFO;

    @BeforeEach
    void setUp() {
        methodFIFO = new StockManagementMethod("FIFO");
        methodFIFO.setId(1);

        methodLIFO = new StockManagementMethod("LIFO");
        methodLIFO.setId(2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Constructeurs
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Constructeur vide : valeurs par défaut")
    void testDefaultConstructor() {
        Article article = new Article();
        assertEquals(0, article.getId(), "id doit valoir 0 par défaut");
        assertNull(article.getNameArticle(), "nameArticle doit être null par défaut");
        assertNull(article.getStockManagementMethod(), "stockManagementMethod doit être null par défaut");
        assertNull(article.getCreatedAt(), "createdAt doit être null par défaut");
    }

    @Test
    @DisplayName("Constructeur paramétré : initialise nameArticle et stockManagementMethod")
    void testParameterizedConstructor() {
        Article article = new Article("Cahier A4", methodFIFO);

        assertEquals("Cahier A4", article.getNameArticle());
        assertEquals(methodFIFO, article.getStockManagementMethod());
    }

    @Test
    @DisplayName("Constructeur paramétré : createdAt est initialisé automatiquement")
    void testParameterizedConstructorSetsCreatedAt() {
        long before = System.currentTimeMillis();
        Article article = new Article("Stylo", methodFIFO);
        long after = System.currentTimeMillis();

        assertNotNull(article.getCreatedAt(), "createdAt ne doit pas être null après construction");
        assertTrue(article.getCreatedAt().getTime() >= before,
                "createdAt doit être >= timestamp avant construction");
        assertTrue(article.getCreatedAt().getTime() <= after,
                "createdAt doit être <= timestamp après construction");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Getters / Setters
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("setId / getId : lecture-écriture de l'identifiant")
    void testSetGetId() {
        Article article = new Article();
        article.setId(42);
        assertEquals(42, article.getId());
    }

    @Test
    @DisplayName("setNameArticle / getNameArticle : lecture-écriture du nom")
    void testSetGetNameArticle() {
        Article article = new Article();
        article.setNameArticle("Ramette papier");
        assertEquals("Ramette papier", article.getNameArticle());
    }

    @Test
    @DisplayName("setStockManagementMethod / getStockManagementMethod : lecture-écriture de la méthode")
    void testSetGetStockManagementMethod() {
        Article article = new Article();
        article.setStockManagementMethod(methodLIFO);
        assertEquals(methodLIFO, article.getStockManagementMethod());
        assertEquals("LIFO", article.getStockManagementMethod().getNameMethod());
    }

    @Test
    @DisplayName("setCreatedAt / getCreatedAt : lecture-écriture du timestamp")
    void testSetGetCreatedAt() {
        Article article = new Article();
        Timestamp ts = new Timestamp(1_700_000_000_000L);
        article.setCreatedAt(ts);
        assertEquals(ts, article.getCreatedAt());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // toString
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("toString : retourne le nom de l'article")
    void testToString() {
        Article article = new Article("Classeur", methodFIFO);
        assertEquals("Classeur", article.toString());
    }

    @Test
    @DisplayName("toString : retourne null si nameArticle n'est pas défini")
    void testToStringWhenNameIsNull() {
        Article article = new Article();
        assertNull(article.toString(),
                "toString doit retourner null quand nameArticle est null");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Règles métier
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Un article peut changer de méthode de gestion de stock")
    void testChangeStockManagementMethod() {
        Article article = new Article("Enveloppe", methodFIFO);
        assertEquals("FIFO", article.getStockManagementMethod().getNameMethod());

        article.setStockManagementMethod(methodLIFO);
        assertEquals("LIFO", article.getStockManagementMethod().getNameMethod());
    }

    @Test
    @DisplayName("Deux articles avec le même nom sont indépendants (pas de référence partagée)")
    void testArticlesAreIndependent() {
        Article a1 = new Article("Post-it", methodFIFO);
        Article a2 = new Article("Post-it", methodFIFO);
        a1.setId(1);
        a2.setId(2);

        assertNotEquals(a1.getId(), a2.getId(),
                "Deux articles distincts doivent avoir des ids différents");
        assertEquals(a1.getNameArticle(), a2.getNameArticle(),
                "Les noms peuvent être identiques");
    }

    @Test
    @DisplayName("setNameArticle accepte un nom vide (validation à faire au niveau service)")
    void testSetEmptyNameArticle() {
        Article article = new Article();
        article.setNameArticle("");
        assertEquals("", article.getNameArticle());
    }

    @Test
    @DisplayName("setStockManagementMethod accepte null (cas désassociation)")
    void testSetNullStockManagementMethod() {
        Article article = new Article("Agenda", methodFIFO);
        article.setStockManagementMethod(null);
        assertNull(article.getStockManagementMethod());
    }
}
