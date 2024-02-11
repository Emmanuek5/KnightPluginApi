package com.obsidian.knight_api.commands.executors;

import com.obsidian.knight_api.managers.ThreadManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.obsidian.knight_api.KnightPluginApi.sendMessage;

public class ThreadsExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()){
            commandSender.sendMessage(ChatColor.GREEN + "Open threads: " + ThreadManager.listThreads());
            sendMessage( "Open threads: " + ThreadManager.listThreads());
            return true;
        }else {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
        }
        return false;
    }
}
