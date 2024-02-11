package com.obsidian.knight_api.listeners;

import com.obsidian.knight_api.KnightPluginApi;
import com.obsidian.knight_api.events.AccountLinkedEvent;
import com.obsidian.knight_api.managers.ConfigManager;
import com.obsidian.knight_api.managers.PlayerDataManager;
import com.obsidian.knight_api.managers.ThreadManager;
import com.obsidian.knight_api.models.PlayerData;
import com.obsidian.knight_api.utils.ChatDecoder;
import com.obsidian.knight_api.utils.ColourConvert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerEvents implements Listener {

    PlayerDataManager playerDataManager;
    ConfigManager configManager;

    public PlayerEvents(PlayerDataManager playerDataManager,ConfigManager configManager) {
        this.playerDataManager = playerDataManager;
        this.configManager = configManager;
    }

    public void startPlaytimeTracking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Iterate through online players and increment playtime
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (playerDataManager.hasPlayerData(onlinePlayer)) {
                        playerDataManager.incrementPlaytime(onlinePlayer, 1);
                    }
                }
            }
        }.runTaskTimer(KnightPluginApi.getPlugin(), 20L, 20L); // Run every 1 second (20 ticks = 1 second)
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // Use ThreadManager to run the task in another thread
        ThreadManager.createThread("PlayerMoveThread-" + player.getName(), () -> {
            if (playerDataManager.hasPlayerData(player)) {
                // Increment playtime for each movement event (you can adjust the value based on your needs)
                String location = "x:" + player.getLocation().getBlockX() + " y:" + player.getLocation().getBlockY() + " z:" + player.getLocation().getBlockZ() + " world:" + player.getLocation().getWorld().getName();
                playerDataManager.setLastKnownLocation(player, location);
                playerDataManager.setPlayerInventory(player, player.getInventory().getContents());
            }
        });
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        playerDataManager.setPlayerLastInventory(player, player.getInventory().getContents());
        ItemStack[] savedItems = new ItemStack[player.getInventory().getSize()];
        playerDataManager.setPlayerLastInventory(player, player.getInventory().getContents());
        playerDataManager.addPlayerDeath(player);
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            playerDataManager.addPlayerKill(killer);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException, InterruptedException {
        Player player = event.getPlayer();
        if (playerDataManager.hasPlayerData(player)){
            playerDataManager.getPlayerData(player).setLastIp(player.getAddress().getAddress().getHostAddress());
            if (configManager.getBoolean("chat.enabled")) {
                String joinMessage = ChatDecoder.decode(player,configManager, configManager.getString("chat.join_message"));
                event.setJoinMessage(joinMessage);
            }
        }else {
            PlayerData newPlayerData = new PlayerData(player.getUniqueId(), player.getName(), 0, configManager.getString("default-player-power"), configManager.getString("default-player-class"), true);
            playerDataManager.setPlayerData(player, newPlayerData);
            playerDataManager.savePlayerData();
            playerDataManager.getPlayerData(player).setLastIp(player.getAddress().getAddress().getHostAddress());
            if (configManager.getBoolean("chat.enabled")) {
                String joinMessage = ChatDecoder.decode( player,configManager,configManager.getString("chat.welcome_message"));
                event.setJoinMessage(joinMessage);
            }
        }
    }

    @EventHandler
    public void  onAccountLinked(AccountLinkedEvent event){
        Player player = event.getPlayer();
            playerDataManager.setWebUserId(player.getUniqueId(), event.getWebUserId());
            playerDataManager.setHasLinked(player.getUniqueId(), true);
        player.sendMessage(ChatColor.GREEN+"Your account has been linked successfully!");
    }
}
