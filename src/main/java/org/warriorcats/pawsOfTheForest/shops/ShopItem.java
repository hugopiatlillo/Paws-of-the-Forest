package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * A record representing an item available for purchase in the shop system.
 * 
 * This immutable data structure encapsulates all the information needed to
 * represent a purchasable item in the shop, including its base ItemStack,
 * display name, price in Paw Coins, and descriptive lore text.
 * 
 * The record provides utility methods for converting the shop item data
 * into a properly formatted ItemStack suitable for display in the shop
 * interface, complete with pricing information and custom lore.
 * 
 * @param item The base ItemStack that defines the material and properties
 * @param name The display name shown to players in the shop interface
 * @param price The cost of the item in Paw Coins (must be non-negative)
 * @param lore A list of descriptive text lines displayed under the item name
 * 
 * @author WarriorCats Plugin Team
 * @version 1.0
 * @since 1.0
 */
public record ShopItem(ItemStack item, String name, long price, List<String> lore) {

    /**
     * Converts this ShopItem into a properly formatted ItemStack for display.
     * 
     * This method creates a new ItemStack based on the item's material type
     * and applies all the shop-specific formatting including:
     * - Setting the custom display name
     * - Adding the original lore text
     * - Appending pricing information in green text
     * - Adding proper spacing between description and price
     * 
     * The resulting ItemStack is ready for display in the shop interface
     * and contains all necessary information for players to make informed
     * purchasing decisions.
     * 
     * @return A formatted ItemStack ready for display in the shop interface,
     *         or the original item if metadata cannot be modified
     */
    public ItemStack toItemStack() {
        // Create a new ItemStack with the same material type
        ItemStack clone = new ItemStack(item.getType());
        ItemMeta meta = clone.getItemMeta();
        
        if (meta != null) {
            // Set the custom display name
            meta.setDisplayName(name);
            
            // Build the complete lore including description and pricing
            List<String> fullLore = new ArrayList<>(lore);
            fullLore.add(""); // Add spacing between description and price
            fullLore.add(ChatColor.GREEN + "Price : " + price + " Paw Coins");
            
            meta.setLore(fullLore);
            clone.setItemMeta(meta);
        }
        
        return clone;
    }
}
