package com.obsidian.knight_api.models.http;


import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface RouteCreator {

    String getRoute(); // Returns the route URL
    HttpMethod getMethod(); // Returns the HTTP method (GET, POST, etc.)
    void handleRequest(HttpExchange exchange) throws IOException, InterruptedException; // Handles the incoming request
}
