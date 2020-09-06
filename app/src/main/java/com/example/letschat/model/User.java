package com.example.letschat.model;

public class User {
    private String id;
    private String username;
    private String user_email;
    private String imageUrl;
    private String status;
    private String search;

    public User() {
    }

    public User(String id, String username, String user_email, String imageUrl, String phone_number, String about, String birth_date, String status, String search) {
        this.id = id;
        this.username = username;
        this.user_email = user_email;
        this.imageUrl = imageUrl;
        this.status = status;
        this.search = search;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
