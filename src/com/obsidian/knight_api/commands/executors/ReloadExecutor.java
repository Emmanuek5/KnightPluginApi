package com.obsidian.knight_api.commands.executors;

import com.obsidian.knight_api.KnightPluginApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        KnightPluginApi.configManager.reloadConfig();
        commandSender.sendMessage("Config reloaded");
        return true;
    }
}
