package com.obsidian.knight_api.commands.executors;

import com.obsidian.knight_api.KnightPluginApi;
import com.obsidian.knight_api.events.AccountLinkedEvent;
import com.obsidian.knight_api.managers.WebsiteLinkManager;
import com.obsidian.knight_api.models.link.LinkResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WebLinkExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
      if (!(commandSender instanceof Player)) {
          commandSender.sendMessage(ChatColor.RED+ "You must be a player to use this command.");
        return false;
      }
     try {
         Player player = (Player) commandSender;
         FileConfiguration  config =KnightPluginApi.getPlugin().getConfig();
         WebsiteLinkManager websiteLinkManager = new WebsiteLinkManager(config.getString("web.link_url"),"");
         if (config.getBoolean("web.enabled")) {
             LinkResponse response = websiteLinkManager.linkAccount(player.getUniqueId());
             if (response.isSuccess()) {
                 KnightPluginApi.accountLinkedCheckerJobs.addJob(player.getUniqueId());
                 player.sendMessage(ChatColor.GREEN+response.getMessage());
                 player.sendMessage(ChatColor.GREEN+response.getUrl());
             }else {
                 player.sendMessage(ChatColor.RED+response.getMessage());
                 if (response.getUrl()!= null){
                     KnightPluginApi.accountLinkedCheckerJobs.addJob(player.getUniqueId());
                     player.sendMessage(response.getUrl());
                 }
                 if (response.getUser_id()!= null){
                     Bukkit.getPluginManager().callEvent(new AccountLinkedEvent(player,true,response.getUser_id()));
                 }
             }
         }else {
             player.sendMessage(ChatColor.RED+"Web link is not enabled.");
         }
     } catch (Exception e) {
         e.printStackTrace();
     }
      return true;
    }
}
