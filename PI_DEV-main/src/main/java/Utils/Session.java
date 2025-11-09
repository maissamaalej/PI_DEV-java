package Utils;

import Models.User;

public class Session {
    private static Session instance;
    private static User currentUser;

    private Session() {
        // Private constructor to prevent instantiation
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void clearSession() {
        currentUser = null;
    }
}