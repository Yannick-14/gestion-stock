package inventory.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires Utils - User")
class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Test de verification des mails")
    void testCheckMailValueSet() {
        String[] mailArray = new String[3];
        mailArray[0] = "testinvalide.test";
        mailArray[1] = "testinvalide@.test";
        mailArray[2] = "testinvalide@test.com";

        String[] results = new String[mailArray.length];

        for(int i = 0; i < mailArray.length; i++) {
            try {
                results[i] = user.checkMailValueSet(mailArray[i]);
                System.out.println("Test de util users: "+ results[i]);
            } catch (Exception e) {
                System.err.println("Test de util users echec: "+ e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Test de hash de mot de passe")
    void testHashPassword() {
        String pwd = "123456789";
        try {
            System.out.println("Pwd hash: "+ user.hashPassword(pwd));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
