package com.obsidian.knight_api.server.routes;

import com.obsidian.knight_api.models.http.HttpMethod;
import com.obsidian.knight_api.models.http.RouteCreator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.obsidian.knight_api.KnightPluginApi.playerDataManager;

public class PlayerHandler implements RouteCreator, HttpHandler {
    @Override
    public String getRoute() {
        return "/api/player";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    /**
     * Handles the HTTP request and returns player information.
     *
     * @param  exchange  the HTTP exchange object
     * @throws IOException  if an I/O error occurs
     */
    public void handleRequest(HttpExchange exchange) throws IOException {
        // Get the player from the request path
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length >= 4) {
            UUID playerUUID = UUID.fromString(pathParts[3]);
            OfflinePlayer player2 = Bukkit.getOfflinePlayer(playerUUID);
            if (player2.getName() == null) {
                sendResponse(exchange, "Player not found", 404);
                return;
            }
            try {
                OfflinePlayer player = playerDataManager.getOfflinePlayer(player2.getName());
                ArrayList<String> playerInfoList = new ArrayList<>();
                playerInfoList.add("Name: " + player.getName());
                playerInfoList.add("UUID: " + player.getUniqueId());
                playerInfoList.add("Linked: " + playerDataManager.hasLinked(player.getUniqueId()));
                playerInfoList.add("WebUserId: " + playerDataManager.getWebUserId(player.getUniqueId()));
                playerInfoList.add("First played: " + player.getFirstPlayed());
                playerInfoList.add("Last played: " + player.getLastPlayed());
                playerInfoList.add("Playtime: " + playerDataManager.getPlaytime(player.getUniqueId()));
                playerInfoList.add("Last played: " + player.getLastPlayed());
                playerInfoList.add("online: " + player.isOnline());
                playerInfoList.add("banned: " + player.isBanned());
                playerInfoList.add("whitelisted: " + player.isWhitelisted());
                playerInfoList.add("op: " + player.isOp());
                playerInfoList.add("class: " + playerDataManager.getPlayerClass(player.getUniqueId()));
                playerInfoList.add("inventory: " + Arrays.toString(playerDataManager.getPlayerInventory(player.getUniqueId())));
                String[] powerSots = playerDataManager.getPowerSlots(player.getUniqueId());
                for (int i = 0; i < powerSots.length; i++) {
                    playerInfoList.add("powerslot-" + i + ": " + powerSots[i]);
                }
                playerInfoList.add("lastInventory: " + Arrays.toString(playerDataManager.getPlayerLastInventory(player.getUniqueId())));
                String response = String.join("\n", playerInfoList);
                sendResponse(exchange, response, 200); // OK
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String response = "Invalid request";
            sendResponse(exchange, response, 400); // Bad Request
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        try {
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } catch (IOException e) {
            // Log or handle the exception
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handleRequest(exchange);
    }
}
