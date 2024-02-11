package com.obsidian.knight_api.commands;

import com.obsidian.knight_api.commands.executors.PluginHeapExecutor;
import com.obsidian.knight_api.models.CommandCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class PluginHeapCommand implements CommandCreator {
    @Override
    public String getName() {
        return "pluginheap";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Displays the heap usage of the plugin";
    }

    @Override
    public String getUsage() {
        return "/pluginheap";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ph");
    }

    @Override
    public CommandExecutor getExecutor() {
        return new PluginHeapExecutor();
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
