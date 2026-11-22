package inventory.models;

import inventory.feature.annotation.FormIgnore;
import java.sql.Timestamp;
import inventory.utils.Time;
import inventory.utils.User;

public class Users {
    @FormIgnore
    private int id;
    private String name, email, password;
    @FormIgnore
    private Timestamp createdAt;

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String nameUser) { this.name = nameUser; }

    public String getEmail() { return email; }
    public void setEmail(String mail) throws Exception {
        User userUtils = new User();
        if (mail == null || mail.trim().isEmpty()) {
            throw new Exception("L'email est obligatoire.");
        }
        // Délègue la validation regex à User.checkMailValueSet()
        this.email = userUtils.checkMailValueSet(mail.trim());
    }

    public String getPassword() { return password; }
    public void setPassword(String pwd) throws Exception {
        User userUtils = new User();
        if (pwd == null || pwd.isEmpty()) {
            throw new Exception("Le mot de passe est obligatoire.");
        }
        if (pwd.length() < 6) {
            throw new Exception("Le mot de passe doit contenir au moins 6 caractères.");
        }
        this.password = userUtils.hashPassword(pwd);
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Users() {}

    public Users(String name, String mail, String pwd) throws Exception {
        try {
            this.setName(name);
            this.setEmail(mail);
            this.setPassword(pwd);
            this.setCreatedAt(new Time().getCurrentDate());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Users(String mail, String pwd) throws Exception {
        try {
            this.setEmail(mail);
            this.setPassword(pwd);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
