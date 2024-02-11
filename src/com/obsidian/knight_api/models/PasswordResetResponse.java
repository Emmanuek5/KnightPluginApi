package com.obsidian.knight_api.models;

public class PasswordResetResponse {

    private final String message;
    private final boolean success;

    private final String url;



    public PasswordResetResponse(String message, boolean success, String url) {
        this.message = message;
        this.success = success;
        this.url = url;
    }

    public PasswordResetResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.url = null;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUrl() {
        return url;
    }


}
