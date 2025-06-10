package com.example.quiz.models;

public class User {
    private String userId;
    private String email;
    private String role;
    private String displayName;
    private int score;
    private int gamesPlayed;

    // Required empty constructor for Firestore
    public User() {}

    public User(String userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.score = 0;
        this.gamesPlayed = 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }
} 