package com.obsidian.knight_api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AccountLinkedEvent extends Event implements Cancellable {
    private final Player player;
    private static final HandlerList handlers = new HandlerList();
    private  final boolean success;
    private final String webUserId;
    private boolean cancelled;

    public AccountLinkedEvent(Player player, boolean success, String webUserId) {
        this.player = player;
        this.success = success;
        this.webUserId = webUserId;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getWebUserId() {
        return webUserId;
    }



    @NotNull
    @Override
     public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;

    }
}
