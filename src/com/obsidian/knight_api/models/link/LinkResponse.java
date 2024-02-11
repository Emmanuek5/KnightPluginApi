package com.obsidian.knight_api.models.link;

public class LinkResponse {
    private String message;
    private boolean success;
    private String url;
    private String user_id;

    // Default constructor
    public LinkResponse() {
    }

    // Constructor with custom message, success, and url values
    public LinkResponse(String message, boolean success, String url) {
        this.message = message;
        this.success = success;
        this.url = url;
    }

    public LinkResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.url = url;
    }
    public LinkResponse(String message, boolean success, String url, String user_id) {
        this.message = message;
        this.success = success;
        this.url = url;
        this.user_id = user_id;
    }




    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public String getUser_id() {
        return user_id;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
