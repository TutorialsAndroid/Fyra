package com.app.fyra.model;

public class AppUser2 {

    private int id;
    private String email;
    private String profilePhoto;

    public AppUser2() {}

    public AppUser2(int id, String email, String profilePhoto) {
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
