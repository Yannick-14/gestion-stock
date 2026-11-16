package inventory.models;

import inventory.feature.annotation.FormIgnore;

public class Role {

    @FormIgnore
    private int id;
    private String nameRole;

    public Role() {}

    public Role(String nameRole) {
        this.nameRole = nameRole;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNameRole() { return nameRole; }
    public void setNameRole(String role) { this.nameRole = role; }
}
