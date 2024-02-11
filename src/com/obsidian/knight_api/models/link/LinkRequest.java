package com.obsidian.knight_api.models.link;

import java.util.UUID;

public class LinkRequest {

    private UUID player;
    private String user_id;
    private boolean linked;

    public LinkRequest(UUID player, String user_id, boolean linked) {
        this.player = player;
        this.user_id = user_id;
        this.linked = linked;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getUser_id() {
        return user_id;
    }

    public boolean isLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
