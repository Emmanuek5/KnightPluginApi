package com.obsidian.knight_api.utils.database.sql;

import com.obsidian.knight_api.managers.PlayerDataManager;
import com.obsidian.knight_api.managers.ThreadManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static com.obsidian.knight_api.KnightPluginApi.*;

public class Messenger {

    private final SqlManager sqlManager;
    private final BlockingQueue<MessageTask> messageQueue;
    private final ScheduledExecutorService scheduler;
    private final Semaphore messageProcessingSemaphore;
    private volatile boolean isRunning = false;
    private final Set<UUID> queuedUsers = new HashSet<>();

    public Messenger(SqlManager sqlManager, PlayerDataManager playerDataManager) throws SQLException {
        this.sqlManager =  sqlManager;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.messageProcessingSemaphore = new Semaphore(1); // Allow only one message processing at a time
        // Start a scheduler to periodically check and process messages
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::tryProcessMessages, 0, 10, TimeUnit.SECONDS);
    }

    public void startMessagingService() {
        sendMessage("Starting messaging service");
    }

    public void stopMessagingService() {
        sendMessage("Stopping messaging service");
        if (scheduler != null) {
            scheduler.shutdown();
        }
        sendMessage("Stopped messaging service");
    }

    private void tryProcessMessages() {
        // Acquire the semaphore only if it's available
        if (messageProcessingSemaphore.tryAcquire()) {
            processMessages();
            // Release the semaphore when the processing is done
            messageProcessingSemaphore.release();
        }
    }

    private void processMessages() {

    }

    private void updateMessageStatus() {

    }

    public boolean isRunning() {
        return isRunning;
    }

    // Method to create a new message when saving data to the database
    public void createMessage(String messageContent, UUID playerUUID) {
        try {
            String serverId = configManager.getString("plugin_id");
            // Insert a new message into the 'messages' table with the provided server_id
            sqlManager.insertData("messages", new String[]{"content", "status", "server_id", "player_uuid"},
                    new Object[]{messageContent, "new", serverId, playerUUID.toString()});
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Define a simple class to represent a message task
    private static class MessageTask {
        private final String serverId;
        private final UUID playerUUID;

        public MessageTask(String serverId, UUID playerUUID) {
            this.serverId = serverId;
            this.playerUUID = playerUUID;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }
        public String getServerId() {
            return serverId;
        }
    }
}
