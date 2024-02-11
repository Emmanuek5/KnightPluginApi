package com.obsidian.knight_api;

import com.obsidian.knight_api.events.BanEvent;

import com.obsidian.knight_api.managers.ConfigManager;
import com.obsidian.knight_api.managers.FileManager;
import com.obsidian.knight_api.managers.PlayerDataManager;
import com.obsidian.knight_api.models.CommandCreator;
import com.obsidian.knight_api.models.ItemCreator;
import com.obsidian.knight_api.utils.database.sql.Messenger;
import com.obsidian.knight_api.utils.database.sql.SqlManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Hook {

    private final JavaPlugin plugin;
    private FileManager fileManager;
    private  ConfigManager configManager;

    private PlayerDataManager playerDataManager;
    private List<CommandCreator>  commands;
    private List<ItemCreator> items = new ArrayList<>();//<ItemStack>
    private Messenger messenger;
    private SqlManager sqlManager;
    public Hook(JavaPlugin plugin) {
        KnightPluginApi.sendMessage(ChatColor.BLUE+"New Plugin Adapter created: " + plugin.getName());
        this.commands = new ArrayList<>();
        this.items = new ArrayList<>();
        this.plugin = plugin;
        this.fileManager = new FileManager(plugin);
        this.configManager = new ConfigManager(plugin, fileManager);
        this.playerDataManager = KnightPluginApi.playerDataManager;
        this.messenger = playerDataManager.messenger;
        this.sqlManager = KnightPluginApi.sqlManager;
    }


    public FileManager getFileManager() {
        return fileManager;
    }
    /**
     * Sends a message to the console using the Knight Plugin API.
     *
     * @param  message  the message to be sent
     */
    public  void sendMessage(String message) {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE+"[KNIGHT PLUGIN API - "+plugin.getName()+"] "+message);
    }

    /**
     * Bans a player with a specified reason and expiration date.
     *
     * @param  player   the player to be banned
     * @param  reason   the reason for the ban
     * @param  expires  the date when the ban expires
     * @return          none
     */
    public void banPlayer(Player player, String reason, Date expires) {
       BanEvent event = new BanEvent(player, reason, new Date(), expires);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            player.kickPlayer(event.getKickMessage());
        }else {
            Date d_duration = event.getBanDuration();
            Duration d = Duration.ofMillis(d_duration.getTime() - new Date().getTime());
            playerDataManager.setBanned(player, true);

            player.ban(event.getReason(), d,event.getSecondaryMessage());
        }
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public SqlManager getSqlManager() {
        return KnightPluginApi.sqlManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return KnightPluginApi.playerDataManager;
    }

    public List<ItemCreator> getItems() {
        return items;
    }

    public List<CommandCreator> getCommands() {
        return commands;
    }

    public void addItem(ItemCreator itemCreator) {
        items.add( itemCreator);
    }
    public ItemStack getItem(String name) {
        for (ItemCreator item : items) {
            if (item.getItemName().equalsIgnoreCase(name)) {
                return item.getItem();
            }
        }
        return null;
    }

    public void registerItems() {
        for (ItemCreator item : items) {
            sendMessage(ChatColor.GOLD + "Registering item: " + item.getItemName() + " for plugin: " + plugin.getName());
            item.createItem();
            if (item.getRecipe() == null) {
                sendMessage(ChatColor.RED + "Recipe not found for item: " + item.getItemName());
                continue;
            }
            Bukkit.getServer().addRecipe(item.getRecipe());
        }
    }
    public void addCommand(CommandCreator command) {
        commands.add(command);
    }

    public void registerCommands() {
        for (CommandCreator command : commands) {
            String commandName = command.getName();
            org.bukkit.command.PluginCommand pluginCommand = plugin.getCommand(commandName);

            if (pluginCommand == null) {
                sendMessage( ChatColor.RED + "Command /" + commandName + " not found for plugin: " + plugin.getName());
               sendMessage(ChatColor.RED + "Place the command in the plugin.yml file");
               sendMessage(ChatColor.RED + "Skipping command /" + commandName + " for plugin: " + plugin.getName());
                continue;
            }

           sendMessage("Registering command /" + commandName + " for plugin: " + plugin.getName());
            pluginCommand.setExecutor(command.getExecutor());
            pluginCommand.setTabCompleter(command.getTabCompleter());
        }
    }

}
