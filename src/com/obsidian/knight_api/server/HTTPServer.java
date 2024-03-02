package com.obsidian.knight_api.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import static com.obsidian.knight_api.KnightPluginApi.sendMessage;

public class HTTPServer {
    private HttpServer server;

    public HTTPServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void start() {
        server.start();
        sendMessage("HTTP server started on port " + server.getAddress().getPort());
    }

    public HttpServer getServer() {
        return server;
    }
}
