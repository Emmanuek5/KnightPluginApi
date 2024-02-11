package com.obsidian.knight_api.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoard {
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreBoard(Player player, String objectiveName) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(objectiveName, "dummy", "Title");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setScore(String entry, int score) {
        Score playerScore = objective.getScore(entry);
        playerScore.setScore(score);
    }

    public void updateScore(String entry, int newScore) {
        setScore(entry, newScore);
        player.setScoreboard(scoreboard);
    }

    public void removeScore(String entry) {
        scoreboard.resetScores(entry);
        player.setScoreboard(scoreboard);
    }

    public void setTitle(String title) {
        objective.setDisplayName(title);
    }

    public void display() {
        player.setScoreboard(scoreboard);
    }

    public void clear() {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
