package com.obsidian.knight_api.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.obsidian.knight_api.KnightPluginApi;
import com.obsidian.knight_api.events.AccountLinkedEvent;
import com.obsidian.knight_api.managers.FileManager;
import com.obsidian.knight_api.managers.PlayerDataManager;
import com.obsidian.knight_api.managers.WebsiteLinkManager;
import com.obsidian.knight_api.models.link.LinkResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import static com.obsidian.knight_api.KnightPluginApi.sendMessage;

public class AccountLinkedCheckerJobs {
    private Map<UUID, Long> pending;
    private Map<UUID, Long> linked;
    private final Timer timer;
    private final WebsiteLinkManager websiteLinkManager;
    private final PlayerDataManager playerDataManager;
    private FileManager fileManager;

    public AccountLinkedCheckerJobs(WebsiteLinkManager websiteLinkManager, PlayerDataManager playerDataManager) {
        this.websiteLinkManager = websiteLinkManager;
        this.playerDataManager = playerDataManager;
        this.pending = Collections.synchronizedMap(new HashMap<>());
        this.linked = Collections.synchronizedMap(new HashMap<>());
        this.timer = new Timer();
    }

    public AccountLinkedCheckerJobs(WebsiteLinkManager websiteLinkManager, PlayerDataManager playerDataManager , FileManager fileManager) {
        this.websiteLinkManager = websiteLinkManager;
        this.playerDataManager = playerDataManager;
        this.pending = Collections.synchronizedMap(new HashMap<>());
        this.linked = Collections.synchronizedMap(new HashMap<>());
        this.timer = new Timer();
        this.fileManager = fileManager;
    }


    public void addJob(UUID uuid) {
        pending.put(uuid, System.currentTimeMillis());
    }

    public void removeJob(UUID uuid) {
        pending.remove(uuid);
        linked.put(uuid, System.currentTimeMillis());
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkJobs();
            }
        }, 0, 5000);
    }

    public void stop() {
        sendMessage("Stopped Account Linked Checker Jobs");
        timer.cancel();
    }

    public void saveJobs() {
        if (fileManager != null) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            if (!fileManager.folderExists("info")) {
                fileManager.createFolder("info");
            }
            if (!fileManager.getFile("info/jobs.yml").exists()) {
                try {
                    fileManager.getFile("info/jobs.yml").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!fileManager.getFile("info/linked.yml").exists()) {
                try {
                    fileManager.getFile("info/linked.yml").createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // convert map to yaml string and write to file
                Yaml yaml = new Yaml(options);
                FileWriter writer = new FileWriter(fileManager.getFile("info/jobs.yml"));
                FileWriter writer2 = new FileWriter(fileManager.getFile("info/linked.yml"));
                yaml.dump(linked, writer);
                writer.close(); // Close the first writer

                yaml.dump(pending, writer2);
                writer2.close(); // Close the second writer
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void loadJobs() {
        if (fileManager != null) {
            try {
                File jobsFile = fileManager.getFile("info/jobs.yml");
                File linkedFile = fileManager.getFile("info/linked.yml");

                if (!jobsFile.exists() || !linkedFile.exists()) {
                    return; // Either file doesn't exist, nothing to load
                }

                FileReader reader = new FileReader(jobsFile);
                FileReader reader2 = new FileReader(linkedFile);

                Yaml yaml = new Yaml();
                linked = new HashMap<>(yaml.load(reader));
                pending = new HashMap<>(yaml.load(reader2));

                reader.close();
                reader2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void checkJobs() {
        int pendingSize = pending.size();
        int linkedSize = linked.size();


            synchronized (pending) {
                Iterator<Map.Entry<UUID, Long>> iterator = pending.entrySet().iterator();
                while (iterator.hasNext()) {
                    try {
                        Map.Entry<UUID, Long> entry = iterator.next();
                        UUID uuid = entry.getKey();
                        long timestamp = entry.getValue();
                        LinkResponse linkResponse = websiteLinkManager.hasLinkedAccount(uuid);
                        if (linkResponse.isSuccess()) {
                            handleSuccessfulLink(uuid, linkResponse.getUser_id());
                            iterator.remove();
                            linked.put(uuid, timestamp);
                            pendingSize--;
                            linkedSize++;
                        } else {
                            // If not successful, check if it's been pending for too long
                            if (System.currentTimeMillis() - timestamp > 60000) { // Assuming a timeout of 60 seconds
                                iterator.remove();
                                pendingSize--;
                            }
                        }
                    } catch (Exception e) {
                       continue;
                    }
                }
            }
    }

    private void handleSuccessfulLink(UUID uuid, String userId) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            AccountLinkedEvent accountLinked = new AccountLinkedEvent(player, true, userId);
            Bukkit.getScheduler().runTask(KnightPluginApi.getPlugin(), () -> {
                Bukkit.getPluginManager().callEvent(accountLinked);

                if (!accountLinked.isCancelled()) {
                    playerDataManager.setWebUserId(uuid, userId);
                    playerDataManager.setHasLinked(uuid, true);
                    linked.put(uuid, System.currentTimeMillis());
                }
            });
        }
    }

    public Map<UUID, Long> getPending() {
        return new HashMap<>(pending); // Return a copy to avoid external modifications
    }

    public Map<UUID, Long> getLinked() {
        return new HashMap<>(linked); // Return a copy to avoid external modifications
    }
}
