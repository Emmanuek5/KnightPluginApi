package com.obsidian.knight_api.listeners;

import com.mysql.cj.protocol.Message;
import com.obsidian.knight_api.events.PlayerDataUpdateEvent;
import com.obsidian.knight_api.managers.PlayerDataManager;
import com.obsidian.knight_api.managers.ThreadManager;
import com.obsidian.knight_api.models.PlayerData;
import com.obsidian.knight_api.utils.database.sql.Messenger;
import com.obsidian.knight_api.utils.database.sql.SqlManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static com.obsidian.knight_api.KnightPluginApi.configManager;
import static com.obsidian.knight_api.KnightPluginApi.sendMessage;

public class PlayerDataUpdateListener implements Listener {

    private final PlayerDataManager playerDataManager;
    private final BlockingQueue<PlayerUpdateTask> updateQueue;
    private final ScheduledExecutorService scheduler;
    private final Semaphore updateSemaphore;  // Add a Semaphore


    private final Set<UUID> queuedPlayerUUIDs;  // Remove 'static'


    public PlayerDataUpdateListener(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
        this.updateQueue = new LinkedBlockingQueue<>();
        this.queuedPlayerUUIDs = new HashSet<>();
        // Start a scheduler to periodically check and insert updates
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::tryProcessUpdateQueue, 0, 30, TimeUnit.SECONDS);
        this.updateSemaphore = new Semaphore(5); //Permit 5 concurrent updates
    }

    @EventHandler
    public void onPlayerDataUpdate(PlayerDataUpdateEvent event) {
        try {
            if (!playerDataManager.isLoading() || playerDataManager.useSql || !playerDataManager.messenger.isRunning()) {
                queueUpdate(event.getPlayerUUID(), event.getUpdatedPlayerData());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void queueUpdate(UUID playerUUID, PlayerData updatedPlayerData) throws InterruptedException {
       if (!playerDataManager.isLoading() || playerDataManager.useSql) {
           // Check if player is already queued
           if (queuedPlayerUUIDs.contains(playerUUID)) {
               updateQueue.removeIf(task -> task.getPlayerUUID().equals(playerUUID));
               PlayerUpdateTask updateTask = new PlayerUpdateTask(playerUUID, updatedPlayerData);
               updateQueue.put(updateTask);
               return;
           }
           // If the player UUID is not in the Set, add it to the Set and the queue
           queuedPlayerUUIDs.add(playerUUID);
           PlayerUpdateTask updateTask = new PlayerUpdateTask(playerUUID, updatedPlayerData);
           updateQueue.put(updateTask);
       }
    }

    private void tryProcessUpdateQueue() {
        // Acquire the semaphore only if it's available
        if (updateSemaphore.tryAcquire()) {
            processUpdateQueue();
            // Release the semaphore when the processing is done
            updateSemaphore.release();
        }
    }

    private void processUpdateQueue() {
            while (!updateQueue.isEmpty()) {
                PlayerUpdateTask updateTask = updateQueue.poll();
                    ThreadManager.createThread("PlayerDataUpdate", () ->{
                        try {
                            SqlManager sqlManager =  new SqlManager("jdbc:mysql://" + configManager.getString("sql.host") + ":" + configManager.getString("sql.port") + "/" + configManager.getString("sql.database"), configManager.getString("sql.username"), configManager.getString("sql.password"));
                            Messenger messenger = new Messenger(sqlManager,playerDataManager);
                            playerDataManager.updatePlayerData(updateTask.getPlayerUUID(), updateTask.getUpdatedPlayerData(), sqlManager,messenger);
                            sqlManager.closeConnection();
                        } catch (SQLException | InterruptedException e) {
                           e.printStackTrace();
                        }
                    });
                    queuedPlayerUUIDs.remove(updateTask.getPlayerUUID());
                   updateQueue.removeIf(task -> task.getPlayerUUID().equals(updateTask.getPlayerUUID()));
            }
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    // Define a simple class to represent an update task
    private static class PlayerUpdateTask {
        private final UUID playerUUID;
        private final PlayerData updatedPlayerData;

        public PlayerUpdateTask(UUID playerUUID, PlayerData updatedPlayerData) {
            this.playerUUID = playerUUID;
            this.updatedPlayerData = updatedPlayerData;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public PlayerData getUpdatedPlayerData() {
            return updatedPlayerData;
        }
    }
}
