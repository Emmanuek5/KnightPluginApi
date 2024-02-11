package com.obsidian.knight_api.listeners;

import com.obsidian.knight_api.KnightPluginApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.sql.SQLException;

public class PluginEvents implements Listener {

    @EventHandler
    public void onPluginDisabled(PluginDisableEvent event) throws SQLException {
        if (event.getPlugin().getName().equals("KnightPluginApi")) {

        }
    }
}
