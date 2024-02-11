package com.obsidian.knight_api.commands.executors;

import com.obsidian.knight_api.KnightPluginApi;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class PluginHeapExecutor implements CommandExecutor {
    private final JavaPlugin plugin;

    public PluginHeapExecutor() {
        this.plugin = KnightPluginApi.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 1) {
            String pluginName = strings[0];
            JavaPlugin targetPlugin = getPluginByName(pluginName);

            if (targetPlugin != null) {
                MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                commandSender.sendMessage(ChatColor.GREEN + "The " + pluginName + " heap usage: " + (heapUsage.getUsed() / 1024 / 1024) + "/" + (heapUsage.getMax() / 1024 / 1024) + " MB");
            } else {
                commandSender.sendMessage(ChatColor.RED + "Plugin not found: " + pluginName);
            }
            return true;
        }

        MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        commandSender.sendMessage(ChatColor.GREEN + "The total heap usage: " + (heapUsage.getUsed() / 1024 / 1024) + "/" + (heapUsage.getMax() / 1024 / 1024) + " MB");
        return true;
    }

    private JavaPlugin getPluginByName(String name) {
        return (JavaPlugin) plugin.getServer().getPluginManager().getPlugin(name);
    }
}
