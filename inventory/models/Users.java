package inventory.models;

import inventory.feature.annotation.FormIgnore;
import java.sql.Timestamp;
import inventory.utils.Time;

public class Users {
    @FormIgnore
    private int id;
    private String name, email, password;
    @FormIgnore
    private Timestamp createdAt;

    // Helpers
    Time timeUtils = new Time();

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName( String nameUser ) { this.name = nameUser; }

    public String getEmail() { return email; }
    public void setEmail( String mail ) throws Exception {
        try {
            this.email = mail;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getPassword() { return password; }
    public void setPassword( String pwd ) throws Exception {
        try {
            this.password = pwd;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Users() {}
    public Users(String name, String mail, String pwd) throws Exception {
        try {
            this.setName(name);
            this.setEmail(mail);
            this.setPassword(pwd);
            this.setCreatedAt(timeUtils.getCurrentDate());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Users(String mail, String pwd) throws Exception {
        this.setEmail(mail);
        this.setPassword(pwd);
    }
}
