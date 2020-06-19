package com.example.fitnessapp.Login;

public class User {
    private String email;

    public User()
    {
        email = null;
    }
    public User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
