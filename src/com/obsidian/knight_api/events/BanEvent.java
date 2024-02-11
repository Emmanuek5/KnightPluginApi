package com.obsidian.knight_api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class BanEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String reason;
    private final Date date;
    private final Date expires;

    private boolean cancelled;
    private String secondaryMessage;


    public BanEvent(Player player, String reason, Date date, Date expires) {
        super();
        this.player = player;
        this.reason = reason;
        this.date = date;
        this.expires = expires;
        this.secondaryMessage = "";
    }

    public BanEvent(Player player, String reason, Date date, Date expires, String secondaryMessage) {
        super();
        this.player = player;
        this.reason = reason;
        this.date = date;
        this.expires = expires;
        this.secondaryMessage = secondaryMessage;
    }
    public Player getPlayer() {
        return player;
    }

    public String getSecondaryMessage() {
        return secondaryMessage;
    }

    public String getReason() {
        return reason;
    }

    public String getKickMessage() {
        return reason;
    }

    public Date getDate() {
        return date;
    }

    public Date getExpiry() {
        return expires;
    }

    public Date getBanDuration() {
        if (expires == null) {
            return null;
        }
        return new Date(expires.getTime() - date.getTime());
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    @NotNull
    @Override
    public HandlerList getHandlers() {
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
