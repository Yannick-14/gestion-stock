package inventory.models;

import java.sql.Timestamp;

public class Agence {
    private int id;
    private String nameAgence, location, description;

    private Users ownerUser;
    private Timestamp createdAt; 
}
