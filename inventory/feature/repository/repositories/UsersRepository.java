package inventory.feature.repository.repositories;

import inventory.feature.repository.dao.GenericMethodDAO;
import inventory.models.Users;

import java.util.List;

public class UsersRepository extends GenericMethodDAO<Users> {

    public Users signup(Users user) throws Exception {
        System.out.println("user: "+ user);
        // email/password already validated & hashed by constructors — just check uniqueness
        List<Users> existing = findWithFilter(user,
            "WHERE email = '" + escape(user.getEmail()) + "'");
        if (existing == null) {
            throw new Exception("Erreur d'accès à la base de données.");
        }
        if (!existing.isEmpty()) {
            throw new Exception("Un compte existe déjà avec cet email.");
        }
        System.out.println("validateUser: "+ user);
        return insert(user);
    }

    public Users login(Users user) throws Exception {
        // email/password already validated & hashed by Users(mail, pwd) constructor
        List<Users> result = findWithFilter(user,
            "WHERE email = '" + escape(user.getEmail()) + "'"
            + " AND password = '" + escape(user.getPassword()) + "'");
        if (result == null) {
            throw new Exception("Erreur d'accès à la base de données.");
        }
        if (result.isEmpty()) {
            throw new Exception("Email ou mot de passe incorrect.");
        }

        return result.get(0);
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("'", "''");
    }
}
