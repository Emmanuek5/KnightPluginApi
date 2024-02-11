package com.obsidian.knight_api.models;

public class UserData {

    private final String id;
    private final String username;
    private final String email;
    private final int points;
    private final String rank;

    public UserData(String id, String username, String email, int points, String rank) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.points = points;
        this.rank = rank;
    }




    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getPoints() {
        return points;
    }

    public String getRank() {
        return rank;
    }
}
