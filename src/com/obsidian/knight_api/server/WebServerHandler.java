package com.obsidian.knight_api.server;



import com.obsidian.knight_api.models.handlers.RouteHandler;
import com.obsidian.knight_api.models.http.HttpMethod;
import com.obsidian.knight_api.models.http.RouteCreator;
import com.obsidian.knight_api.server.routes.PlayerHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.obsidian.knight_api.KnightPluginApi.sendMessage;

public class WebServerHandler {
    private final HttpServer server;
    private static List<RouteCreator> routeHandlers;
   public ArrayList<String> routes;
    public WebServerHandler(HttpServer server) {
        this.server =  server;
        routeHandlers = new ArrayList<>();
        routes = new ArrayList<>();
    }


    public void addRoute(RouteCreator routeCreator) {
        routeHandlers.add( routeCreator );
    }

    public void start() {
        server.start();
        registerRoutes();
        sendMessage("Server started on port " + server.getAddress().getPort());
    }

    public void  registerRoutes() {
        for (RouteCreator routeHandler : routeHandlers) {
            routes.add( routeHandler.getRoute());
           server.createContext( routeHandler.getRoute(), (HttpHandler) routeHandler);
        }
    }

    public void handleDefault(HttpMethod method, HttpHandler handler) {
        server.createContext("/", exchange -> {
            if (exchange.getRequestMethod().equalsIgnoreCase(method.name())) {
                handler.handle(exchange);
            } else {
                String response = "Unsupported method: " + exchange.getRequestMethod();
                sendResponse(exchange, response, 405); // Method Not Allowed
            }
        });
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
