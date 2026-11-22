package inventory.feature.applicationService;

import inventory.models.Users;

public class Session {
    private Users currentUser;
    private void setCurrentUser(Users user) {
        this.currentUser = user;
    }

    public void login(Users user) {
        setCurrentUser(user);
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return this.getCurrentUser() != null;
    }

    public void logout() {
        currentUser = null;
    }

    public int getUserId() {
        return this.getCurrentUser() != null ? this.getCurrentUser().getId() : -1;
    }
}
