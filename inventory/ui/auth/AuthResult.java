package inventory.ui.auth;

import inventory.models.Users;

public class AuthResult {
    private final boolean success;
    private final String message;
    private final Users user;
    private final boolean redirect;

    private AuthResult(boolean success, String message, Users user, boolean redirect) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.redirect = redirect;
    }

    public static AuthResult success(String message, Users user, boolean redirect) {
        return new AuthResult(true, message, user, redirect);
    }

    public static AuthResult error(String message) {
        return new AuthResult(false, message, null, false);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Users getUser() { return user; }
    public boolean isRedirect() { return redirect; }
}
