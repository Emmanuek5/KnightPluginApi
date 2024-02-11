package com.obsidian.knight_api.utils;

import org.bukkit.ChatColor;

public class ColourConvert {
    public static String convertColors(String input) {
        if (input == null) {
            return null;
        }

        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
