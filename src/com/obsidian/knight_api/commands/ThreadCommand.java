package com.obsidian.knight_api.commands;

import com.obsidian.knight_api.commands.executors.ThreadsExecutor;
import com.obsidian.knight_api.models.CommandCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class ThreadCommand implements CommandCreator {
    @Override
    public String getName() {
        return "open-threads";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return "/open-threads";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("threads");
    }

    @Override
    public CommandExecutor getExecutor() {
        return new ThreadsExecutor();
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
