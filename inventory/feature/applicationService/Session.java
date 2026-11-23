package inventory.feature.applicationService;

import inventory.models.Users;

public class Session {
    private static Users currentUser;
    private static void setCurrentUser(Users user) {
        currentUser = user;
    }

    public void login(Users user) {
        setCurrentUser(user);
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
