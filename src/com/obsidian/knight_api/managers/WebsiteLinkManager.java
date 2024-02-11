package com.obsidian.knight_api.managers;

import com.google.gson.Gson;
import com.obsidian.knight_api.models.PasswordResetResponse;
import com.obsidian.knight_api.models.UserData;
import com.obsidian.knight_api.models.link.LinkResponse;
import com.obsidian.knight_api.utils.HttpRequest;
import org.bukkit.entity.Player;


import java.io.IOException;
import java.util.UUID;

public class WebsiteLinkManager {
    public String link ;
    public String responseUrl;

    public UUID playerUUID;

    public WebsiteLinkManager(String url, String response) {
        link = url;
        responseUrl = response;
    }

    public WebsiteLinkManager(UUID playerUUID, String url) {
        this.playerUUID = playerUUID;
        link = url;
    }


    public LinkResponse linkAccount(UUID playerUUID) {
        link = link +"/create/"+ playerUUID.toString();

        // Make an HTTP request to link the account
        String url = link;
        String postData = "{\"response_url\":\"" + responseUrl + "\"}";

        try {
            String response = HttpRequest.sendJsonPostRequest(url, postData);

            // Parse the JSON response using Gson
            Gson gson = new Gson();
            LinkResponse linkResponse = gson.fromJson(response, LinkResponse.class);

            // Now you can access the parsed data
            String message = linkResponse.getMessage();
            boolean success = linkResponse.isSuccess();

            // Do something with the parsed data

            return linkResponse; // Return the response object

        } catch (IOException e) {

            return new LinkResponse("Error making API request", false);
        }
    }

    public LinkResponse hasLinkedAccount(UUID playerUUID) {

        String url = link + "/"+ playerUUID.toString();

        try {
            String response = HttpRequest.sendGetRequest(url);

            // Parse the JSON response using Gson
            Gson gson = new Gson();
            LinkResponse linkResponse = gson.fromJson(response, LinkResponse.class);

            // Now you can access the parsed data
            String message = linkResponse.getMessage();
            boolean success = linkResponse.isSuccess();

            // Do something with the parsed data
            return linkResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 public UserData getUserData(Player player) {
     UUID playerUUID = player.getUniqueId();
     String url = link + "/user/"+ playerUUID.toString();
     try {
         String response = HttpRequest.sendGetRequest(url);
         Gson gson = new Gson();
        UserData userData = gson.fromJson(response, UserData.class);
         return userData;
     } catch (IOException e) {
         e.printStackTrace();
         return new UserData("", "Error making API request", "Error making API request", 0, "Error making API request");
     }
 }

 public PasswordResetResponse getPasswordResetLink(Player player) {
     String url = link + "/password-reset/" + player.getUniqueId().toString();
     try {
         String response = HttpRequest.sendGetRequest(url);
         Gson gson = new Gson();
         PasswordResetResponse passwordResetResponse = gson.fromJson(response, PasswordResetResponse.class);
         return passwordResetResponse;
     } catch (IOException e) {
         e.printStackTrace();
       return new PasswordResetResponse("Error making API request", false);
     }
 }

 public boolean verifyPasswordResetCode(UUID player, String code) {
     String url = link + "/password-reset/validate/" + player.toString() + "/" + code;
     try {
         String response = HttpRequest.sendGetRequest(url);
         Gson gson = new Gson();
         PasswordResetResponse passwordResetResponse = gson.fromJson(response, PasswordResetResponse.class);
         return passwordResetResponse.isSuccess();
     } catch (IOException e) {
         e.printStackTrace();
         return false;
     }
 }
}
