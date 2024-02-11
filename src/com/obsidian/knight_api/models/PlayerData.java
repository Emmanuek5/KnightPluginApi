package com.obsidian.knight_api.models;

import com.obsidian.knight_api.KnightPluginApi;
import com.obsidian.knight_api.events.PlayerDataUpdateEvent;
import com.obsidian.knight_api.utils.database.sql.SqlManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;



public class PlayerData implements Serializable {

    private ItemStack[] lastInventory;
    private String lastKnownLocation;
    private ItemStack[] inventory;
    private UUID playerUUID;
    private String playerName;
    private int playerScore;
    private String power; // New field for power
    private String[] powerSlots;
    private String playerClass;
    private String lastIp;
    private String Captcha;
    private int deaths;
    private int kills;
    private long playtime;
    private boolean usePowers;
    private boolean hasLinked ;
    private String webUserId;
    private long lastUpdateTimestamp;
    private boolean banned = false;
    boolean isLoading ;

    public PlayerData(UUID playerUUID, String playerName, int playerScore, String power , String playerClass, boolean usePowers) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.playerScore = playerScore;
        this.power = power; // Default value for power
        this.powerSlots = new String[10];
        this.playerClass = playerClass;
        this.lastIp = "";
        this.Captcha = "";
        this.usePowers = usePowers;
        this.hasLinked = false;
        this.webUserId = "";
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public void updateTimestamp() {
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
        emitUpdateEvent();
    }

    public boolean isBanned() {
        return banned;
    }

    public String getWebUserId() {
        return webUserId;
    }

    public void setWebUserId(String webUserId) {
        this.webUserId = webUserId;
        emitUpdateEvent();
    }

    public boolean hasLinked() {
        return hasLinked;
    }

    public void setHasLinked(boolean hasLinked) {
        this.hasLinked = hasLinked;
        emitUpdateEvent();
    }

    public void incrementPlaytime(long seconds) {
        this.playtime += seconds;
    }

    public long getPlaytime() {
        return this.playtime;
    }
    public void setPlaytime(long playtime) {
        this.playtime = playtime;
        emitUpdateEvent();
    }

    public boolean isUsePowers() {
        return usePowers;
    }
    public void setUsePowers(boolean usePowers) {
        this.usePowers = usePowers;
        emitUpdateEvent();
    }
    public ItemStack[] getLastInventory() {
        return lastInventory;
    }
    public void setLastInventory(ItemStack[] lastInventory) {
        this.lastInventory = lastInventory;
        emitUpdateEvent();
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public String getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(String lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
        emitUpdateEvent();
    }
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        emitUpdateEvent();
    }




    public int getKills() {
        return kills;
    }
    public void setKills(int kills) {
        this.kills = kills;
        emitUpdateEvent();
    }
    public int getDeaths() {
        return deaths;
    }
    public void setDeaths(int deaths) {
        this.deaths = deaths ;
        emitUpdateEvent();
    }




    public void setPowerSlot(int slotIndex, String power) {
        if (slotIndex >= 0 && slotIndex < powerSlots.length) {
            powerSlots[slotIndex] = power;
        }
        emitUpdateEvent();
    }
    public String getLastIp() {
        return lastIp;
    }
    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
        emitUpdateEvent();
    }
    public String getCaptcha() {
        return Captcha;
    }

    public void setCaptcha(String captcha) {
        Captcha = captcha;
    }
    public String[] getPowerSlots() {
        return powerSlots;
    }
    public String getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
        emitUpdateEvent();
    }
    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
        emitUpdateEvent();
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
        emitUpdateEvent();
    }


    public void setPowerSlots(String[] powerSlots) {
        this.powerSlots = powerSlots;
        emitUpdateEvent();
    }

    public void fetchPlayerData(SqlManager sqlManager) throws SQLException, InterruptedException {
        ResultSet resultSet =  sqlManager.executeQuery("SELECT * FROM player_data WHERE uuid = '" + playerUUID + "'");
        if (resultSet.next()) {
            isLoading = true;
            this.kills = resultSet.getInt("kills");
            this.deaths = resultSet.getInt("deaths");
            this.playtime = resultSet.getLong("playtime");
            this.lastKnownLocation = resultSet.getString("last_known_location");
            this.usePowers = resultSet.getBoolean("use_powers");
            this.hasLinked = resultSet.getBoolean("web_has_linked");
            this.webUserId = resultSet.getString("web_user_id");
            this.playerClass = resultSet.getString("class");
            this.banned = resultSet.getBoolean("banned");
            this.Captcha = resultSet.getString("captcha");
            isLoading = false;
        }
    }


    private void emitUpdateEvent() {
     if (!isLoading){
         Bukkit.getScheduler().runTask(KnightPluginApi.getPlugin(), () -> {
             PlayerDataUpdateEvent event = new PlayerDataUpdateEvent(playerUUID, this);
             Bukkit.getPluginManager().callEvent(event);
         });}
    }
}