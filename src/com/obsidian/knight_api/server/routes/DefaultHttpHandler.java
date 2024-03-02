package com.obsidian.knight_api.server.routes;


import com.obsidian.knight_api.KnightPluginApi;
import com.obsidian.knight_api.models.http.HttpMethod;
import com.obsidian.knight_api.models.http.RouteCreator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class DefaultHttpHandler implements RouteCreator, HttpHandler {
    @Override
    public String getRoute() {
        return "/";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        ArrayList routes = KnightPluginApi.webServerHandler.routes;
        String response = "Available routes:\n";
        for (int i = 0; i < routes.size(); i++) {
            response += routes.get(i) + "\n";
        }
        sendResponse(exchange, response, 200);

    }
    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void handle(HttpExchange exchange) throws IOException {
        handleRequest(exchange);
    }
}
