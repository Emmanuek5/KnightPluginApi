package com.obsidian.knight_api.events;

import com.obsidian.knight_api.models.bans.BanData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BanCreated extends Event {

    private final BanData banData;
    private UUID uuid;

    private static final HandlerList handlers = new HandlerList();

    public BanCreated(BanData banData, BanData banData1) {
        super();
        this.banData = banData1;
        this.uuid = banData.getUuid();
    }

    public BanData getBanData() {
        return banData;
    }

    public UUID getUuid() {
        return uuid;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
