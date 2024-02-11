package com.obsidian.knight_api.utils;

import com.obsidian.knight_api.managers.ConfigManager;
import org.bukkit.entity.Player;

public class ChatDecoder {
    public static String decode(Player player , ConfigManager configManager, String... args) {
        String message = String.join(" ", args);
        String[] params = new String[1];
        params[0] = "%USERNAME%";
        params[1] = "%SUFFIX%";
        params[2] = "%PREFIX%";

        String[] replacements = new String[1];
        replacements[0] = player.getName();
        replacements[1] = configManager.getString("chat.suffix");
        replacements[2] = configManager.getString("chat.prefix");

       message = ColourConvert.convertColors(message);

        for (int i = 0; i < params.length; i++) {
            message = message.replace(params[i], replacements[i]);
        }
        return message;
    }

}
