package com.obsidian.knight_api.managers;

import com.google.gson.Gson;
import com.obsidian.knight_api.models.bans.BanData;
import com.obsidian.knight_api.models.bans.BanRequest;
import com.obsidian.knight_api.models.bans.BanResponse;
import com.obsidian.knight_api.utils.HttpRequest;

import java.io.IOException;

public class BanManager {
    private final BanData banData;
    private String url;

    public BanManager(BanData banData, String url, String url1) {
        this.banData = banData;
        this.url = url1;
    }

   public BanResponse createBan() throws IOException {
       try {
           if (!url.endsWith("/")) {
               url += "/";
           }
           String r_url = url + banData.getUuid();

           BanRequest banRequest = new BanRequest(banData);
           Gson gson = new Gson();
           String json = gson.toJson(banRequest);

           // Send POST request to the API
           String response = HttpRequest.sendJsonPostRequest(r_url, json);

           // Parse the JSON response using Gson
           Gson gson1 = new Gson();
           BanResponse banResponse = gson1.fromJson(response, BanResponse.class);

           return banResponse;
       }catch (IOException e) {
           return new BanResponse(0, false, "Error making API request");
       }
    }

    public BanData getBanData() {
        return banData;
    }
}
