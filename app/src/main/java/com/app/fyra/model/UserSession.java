package com.app.fyra.model;

public class UserSession {
    private static UserSession instance;
    private AppUser currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(AppUser user) {
        this.currentUser = user;
    }

    public AppUser getUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void clearSession() {
        currentUser = null;
    }
}

