package com.app.fyra.model;

public class CreateUser {

    private int id;
    private String email;
    private String password;
    private String profilePhoto;

    public CreateUser() {}

    public CreateUser(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public CreateUser(int id, String email, String password, String profilePhoto) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.profilePhoto = profilePhoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
