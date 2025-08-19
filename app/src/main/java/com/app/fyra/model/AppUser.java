package com.app.fyra.model;

public class AppUser {
    private int id;
    private String email;
    private String profilePhoto;

    public AppUser() {
    }

    public AppUser(int id, String email, String profilePhoto) {
        this.id = id;
        this.email = email;
        this.profilePhoto = profilePhoto;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}

