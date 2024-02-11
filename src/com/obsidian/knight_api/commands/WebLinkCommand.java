package com.obsidian.knight_api.commands;

import com.obsidian.knight_api.commands.executors.WebLinkExecutor;
import com.obsidian.knight_api.models.CommandCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebLinkCommand implements CommandCreator {
    @Override
    public String getName() {
        return "weblink";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Link your Minecraft account to your Knight SMP account.";
    }

    @Override
    public String getUsage() {
        return "/weblink";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("link", "web-link", "wlink","wl");
    }

    @Override
    public CommandExecutor getExecutor() {
        return new WebLinkExecutor();
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
