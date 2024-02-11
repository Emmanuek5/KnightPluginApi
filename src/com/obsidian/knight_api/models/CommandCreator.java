package com.obsidian.knight_api.models;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import java.util.List;

public interface CommandCreator {

    String getName();

    String getPermission();

    String getDescription();

    String getUsage();

    List<String> getAliases();

    CommandExecutor getExecutor();

    TabCompleter getTabCompleter();

}