package com.obsidian.knight_api.models.bans;

import java.time.Duration;
import java.util.UUID;

public class BanRequest {

    private String username;
    private String reason;
    private int expiry;
    private boolean isPermanent;
    private UUID uuid;

    public BanRequest(String username, String reason, Duration expiry, boolean isPermanent, UUID uuid) {
        this.username = username;
        this.reason = reason;
        // Convert Duration to seconds
        this.expiry = (int) expiry.getSeconds();
        this.isPermanent = isPermanent;
        this.uuid = uuid;
    }

    public  BanRequest(BanData banData) {
        this.username = banData.getUsername();
        this.reason = banData.getReason();
        this.expiry = (int) banData.getExpiry().getSeconds();
        this.isPermanent = banData.isPermanent();
        this.uuid = banData.getUuid();
    }

    // Add getters and setters as needed

    public String getUsername() {
        return username;
    }

    public String getReason() {
        return reason;
    }

    public int getExpiry() {
        return expiry;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public UUID getUuid() {
        return uuid;
    }
}
