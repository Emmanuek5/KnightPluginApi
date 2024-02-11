package com.obsidian.knight_api.managers;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config;
    private final FileManager fileManager;



    public ConfigManager(Plugin plugin,  FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;

        reloadConfig();
    }

    public void reloadConfig() {
        try {
            File configFile = fileManager.getFile("config.yml");
            Reader reader = new FileReader(configFile);
            config = YamlConfiguration.loadConfiguration(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }



    public ConfigurationSection getConfigurationSection(String path) {
        return config.getConfigurationSection(path);
    }
}
