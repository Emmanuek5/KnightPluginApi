package com.obsidian.knight_api.utils.database.sql;

import com.obsidian.knight_api.KnightPluginApi;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SqlPoolManager {
    private static int POOL_SIZE = 10;
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private final String url;
    private final String username;
    private final String password;
    private final BlockingQueue<Connection> connectionPool;

    public SqlPoolManager(String u_url, String u_username, String u_password) {
        url = u_url;
        username = u_username;
        password = u_password;

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            handleException("Error loading JDBC driver", e);
        }

        connectionPool = new LinkedBlockingQueue<>(POOL_SIZE);
        initializePool();
    }

    public SqlPoolManager(String u_url, String u_username, String u_password, int poolSize) {
        url = u_url;
        username = u_username;
        password = u_password;
        POOL_SIZE = poolSize;

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            handleException("Error loading JDBC driver", e);
        }

        connectionPool = new LinkedBlockingQueue<>(POOL_SIZE);
        initializePool();
    }

    private void initializePool() {
        try {
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection connection = DriverManager.getConnection(url, username, password);
                connectionPool.offer(connection);
            }
        } catch (SQLException e) {
            handleException("Error initializing SQL connection pool", e);
        }
    }

    public Connection getConnection() throws InterruptedException {
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            handleException("Error getting SQL connection", e);
            throw e;
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null && !connectionPool.offer(connection)) {
            try {
                connection.close();
            } catch (SQLException e) {
                handleException("Error releasing SQL connection", e);
            }
        }
    }

    public void closeConnections() {
        for (Connection connection : connectionPool) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    handleException("Error closing SQL connection", e);
                }
            }
        }
    }

    private void handleException(String message, Exception e) {
        // Log the exception for debugging
        e.printStackTrace();
        KnightPluginApi.sendMessage(ChatColor.RED + message + ": " + e.getMessage());
    }
}
