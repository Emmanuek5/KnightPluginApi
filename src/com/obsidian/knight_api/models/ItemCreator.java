package com.obsidian.knight_api.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public interface ItemCreator {

    String getItemName();

    ItemStack getItem();

    void createItem();
    ShapedRecipe getRecipe();
}
