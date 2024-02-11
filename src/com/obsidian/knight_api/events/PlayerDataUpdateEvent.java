package com.obsidian.knight_api.events;

import com.obsidian.knight_api.models.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerDataUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID playerUUID;
    private final PlayerData updatedPlayerData;

    public PlayerDataUpdateEvent(UUID playerUUID, PlayerData updatedPlayerData) {
        this.playerUUID = playerUUID;
        this.updatedPlayerData = updatedPlayerData;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public PlayerData getUpdatedPlayerData() {
        return updatedPlayerData;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
