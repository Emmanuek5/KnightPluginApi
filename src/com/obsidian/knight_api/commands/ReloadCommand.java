package com.obsidian.knight_api.commands;

import com.obsidian.knight_api.commands.executors.ReloadExecutor;
import com.obsidian.knight_api.models.CommandCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class ReloadCommand implements CommandCreator {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin";
    }

    @Override
    public String getUsage() {
        return "/reload";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public CommandExecutor getExecutor() {
        return new ReloadExecutor();
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
