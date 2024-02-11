package com.obsidian.knight_api.models.bans;

public class BanResponse {

    private int id;
    private boolean success;
    private String message;

    public BanResponse(int id, boolean success, String message) {
        this.id = id;
        this.success = success;
        this.message = message;

    }

    public int getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
