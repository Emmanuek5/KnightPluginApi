package com.obsidian.knight_api;

import com.obsidian.knight_api.commands.PluginHeapCommand;
import com.obsidian.knight_api.commands.ReloadCommand;
import com.obsidian.knight_api.commands.ThreadCommand;
import com.obsidian.knight_api.commands.WebLinkCommand;
import com.obsidian.knight_api.listeners.PlayerDataUpdateListener;
import com.obsidian.knight_api.listeners.PlayerEvents;
import com.obsidian.knight_api.managers.*;
import com.obsidian.knight_api.models.CommandCreator;
import com.obsidian.knight_api.models.http.HttpMethod;
import com.obsidian.knight_api.server.HTTPServer;
import com.obsidian.knight_api.server.WebServerHandler;
import com.obsidian.knight_api.server.routes.DefaultHttpHandler;
import com.obsidian.knight_api.server.routes.PlayerHandler;
import com.obsidian.knight_api.utils.AccountLinkedCheckerJobs;
import com.obsidian.knight_api.utils.database.sql.Messenger;
import com.obsidian.knight_api.utils.database.sql.SqlManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KnightPluginApi extends JavaPlugin {
    public static FileManager fileManager;
    public List<CommandCreator>  commands = new ArrayList<>();
    public static SqlManager sqlManager;
    public static PlayerDataManager playerDataManager;
    public static ConfigManager configManager;
    public static UUID randomUUID;
    public static AccountLinkedCheckerJobs accountLinkedCheckerJobs;
    public static HTTPServer httpServer;
    public static WebServerHandler webServerHandler;
    public static PlayerDataUpdateListener playerDataUpdateListener;
    public static   void sendMessage(String... args) {
        getPlugin().getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE+"[KNIGHT PLUGIN API] "+String.join(" ", args));
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("KnightPluginApi");
    }

    public static JavaPlugin getInstance() {
        return  (JavaPlugin) getPlugin();
    }

    public void onEnable() {
        commands.add(new WebLinkCommand());
        commands.add(new PluginHeapCommand());
        commands.add(new ReloadCommand());
        commands.add(new ThreadCommand());
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        copyDefaultConfig();
        registerCommands();
        randomUUID = UUID.randomUUID();
        fileManager = new FileManager(this);
        configManager = new ConfigManager(this, fileManager);
        // Copy default-config.yml if it doesn't exist
        boolean useSql = getConfig().getBoolean("sql.enabled");
        if (getConfig().getBoolean("sql.enabled")) {
            try {

                sqlManager = new SqlManager("jdbc:mysql://" + getConfig().getString("sql.host") + ":" + getConfig().getString("sql.port") + "/" + getConfig().getString("sql.database"), getConfig().getString("sql.username"), getConfig().getString("sql.password"));

            } catch (SQLException e) {
               e.printStackTrace();
               useSql = false;
            }
            createTables();
        }


        boolean finalUseSql1 = useSql;
        try {
            playerDataManager = new PlayerDataManager(dataFolder, configManager, finalUseSql1);
         if (useSql){
             playerDataManager.setSqlOptions(sqlManager);
         }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ThreadManager.createThread("PlayerDataUpdateListener",()->{
                playerDataUpdateListener   = new PlayerDataUpdateListener(playerDataManager);
                registerEvent(playerDataUpdateListener);
            });

        if (getConfig().getBoolean("server.enabled")) {
            try {
                httpServer = new HTTPServer(getConfig().getInt("server.port"));
                webServerHandler = new WebServerHandler(httpServer.getServer());
                webServerHandler.addRoute(new PlayerHandler());
                webServerHandler.handleDefault(HttpMethod.GET,new DefaultHttpHandler());
                webServerHandler.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        WebsiteLinkManager websiteLinkManager = new WebsiteLinkManager(getConfig().getString("web.link_url"),"");
      ThreadManager.createThread("Account_Checker",()->{
          sendMessage("Starting Account Check Job");
          accountLinkedCheckerJobs = new AccountLinkedCheckerJobs(websiteLinkManager, playerDataManager,fileManager);
          accountLinkedCheckerJobs.start();
          accountLinkedCheckerJobs.loadJobs();
      });
        boolean finalUseSql = useSql;
        PlayerEvents playerEvents = new PlayerEvents(playerDataManager,configManager);

        playerEvents.startPlaytimeTracking();
        registerEvent(playerEvents);
        sendMessage(ChatColor.BLUE+"--------------------------------------------");
        sendMessage(ChatColor.GREEN+"KNIGHT PLUGIN API ENABLED");
        sendMessage(ChatColor.GREEN+"Version: " + getDescription().getVersion());
        sendMessage(ChatColor.GREEN+"Website: " + getDescription().getWebsite());
        sendMessage(ChatColor.GREEN+"Author: " + getDescription().getAuthors());
        sendMessage(ChatColor.GREEN+"Threads: " + ThreadManager.getThreads().size());
        sendMessage(ChatColor.BLUE+"--------------------------------------------");
      if (sqlManager != null) {
          sendMessage(ChatColor.YELLOW+"Connection to SQL server established");
          sendMessage(ChatColor.YELLOW+"Database: " + getConfig().getString("sql.database"));
      };
    }

    public void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerCommands() {
        for (CommandCreator command : commands) {
            String name = command.getName();
            if (getCommand(name) == null) {
                sendMessage("Command not found: /" + name);
                sendMessage("Place it in the plugin.yml file and try again");
                continue;
            }
            sendMessage("Registering command: /" + command.getName());
            getCommand(command.getName()).setExecutor(command.getExecutor());
            getCommand(command.getName()).setTabCompleter(command.getTabCompleter());
        }
    }

    private void createTables() {
        // Get table configurations from the config file
        ConfigurationSection tablesSection = getConfig().getConfigurationSection("tables");
        if (tablesSection != null) {
            for (String tableName : tablesSection.getKeys(false)) {
                ConfigurationSection tableSection = tablesSection.getConfigurationSection(tableName);
                if (tableSection != null) {
                    // Get column definitions
                    List<String> columnsList = tableSection.getStringList("columns");
                    List<String> valuesList = tableSection.getStringList("values");

                    if (!columnsList.isEmpty() && columnsList.size() == valuesList.size()) {
                        String[] columns = columnsList.toArray(new String[0]);
                        String[] columnTypes = valuesList.toArray(new String[0]);

                        try {
                            // Use existing createTable method from SqlManager
                            sqlManager.createTable(tableName, columns, columnTypes);
                        } catch (SQLException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Log an error or handle the case where columns and values lists are not valid
                        sendMessage("Invalid configuration for table: " + tableName);
                    }
                }
            }
        }
    }



    private void copyDefaultConfig() {
        // Copy default-config.yml if it doesn't exist
        if (!Files.exists(getDataFolder().toPath().resolve("config.yml"))) {
            // Generate a random hash as the plugin id
            String pluginId = UUID.randomUUID().toString();

            Path configFile = getDataFolder().toPath().resolve("config.yml");
            try (InputStream inputStream = getResource("resources/default-config.yml")) {
                if (inputStream != null) {
                    Files.copy(inputStream, configFile, StandardCopyOption.REPLACE_EXISTING);
                    // Append the plugin id to the copied config
                    appendPluginId(configFile, pluginId);
                    sendMessage("Default config.yml has been copied with plugin id: " + pluginId);
                } else {
                    sendMessage("Failed to copy default config.yml. Resource not found.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // New method to append the plugin id to the copied config
    private void appendPluginId(Path configFile, String pluginId) {
        try {
            // Read the content of the config file
            String content = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);

            // Append the plugin id to the content
            content += "\nplugin_id: " + pluginId;

            // Write the updated content back to the config file
            Files.write(configFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onDisable() {
        sendMessage("Disabling plugin...");
        if (playerDataManager != null) {
            sendMessage("Saving player data...");
          ThreadManager.createThread("Saving player data", ()->{
              try {
                  playerDataManager.savePlayerData();
              } catch (SQLException e) {
                  e.printStackTrace();
              }
          });
        }
        sendMessage("Stopping services...");
        playerDataUpdateListener.stop();
        playerDataManager.messenger.stopMessagingService();
        accountLinkedCheckerJobs.stop();
        accountLinkedCheckerJobs.saveJobs();
        sendMessage("Stopping threads...");
        ThreadManager.waitForThreads();
        sendMessage(ChatColor.RED + "The plugin has been disabled!");
    }

}
