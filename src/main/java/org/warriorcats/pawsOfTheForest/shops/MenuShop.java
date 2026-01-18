package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.List;

/**
 * Utility class for creating and managing the shop menu interface.
 * 
 * This abstract class provides static methods for generating the shop inventory
 * interface that players interact with. It handles the creation of the shop GUI,
 * populating it with available items from the shop configuration, and displaying
 * the player's current coin balance.
 * 
 * The shop interface uses a 27-slot inventory (3 rows) with shop items placed
 * according to their configuration order, and a special coin display item in
 * the bottom-right corner (slot 26) showing the player's current balance.
 * 
 * @author WarriorCats Plugin Team
 * @version 1.0
 * @since 1.0
 */
public abstract class MenuShop {

    /** The title displayed in the shop inventory interface */
    public static final String TITLE = "Paw Shop";

    /**
     * Opens the shop interface for the specified player.
     * 
     * This method creates a new 27-slot inventory with the shop title,
     * populates it with all configured shop items in their designated slots,
     * adds a coin balance display item in the bottom-right corner,
     * and opens the inventory for the player to interact with.
     * 
     * Each shop item is placed in a slot corresponding to its index in the
     * shop configuration list, allowing for consistent item positioning.
     * 
     * @param player The player for whom to open the shop interface
     */
    public static void open(Player player) {
        // Create a 3-row inventory with the shop title
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Populate the inventory with all configured shop items
        for (var shopItem : ShopsConf.Shops.SHOP_ITEMS) {
            inv.setItem(ShopsConf.Shops.SHOP_ITEMS.indexOf(shopItem), shopItem.toItemStack());
        }

        // Add the coin balance display item in the bottom-right corner (slot 26)
        inv.setItem(26, createCoinsItem(player));

        // Open the shop interface for the player
        player.openInventory(inv);
    }

    /**
     * Creates a visual item stack displaying the player's current coin balance.
     * 
     * This method generates a sunflower item that serves as a balance indicator
     * in the shop interface. The item displays the player's current Paw Coins
     * balance in both the display name and includes descriptive lore text
     * explaining the coin system.
     * 
     * The coin information is retrieved from the player cache for performance,
     * and the display formatting uses colors and messages from the shop
     * configuration to maintain consistency with the plugin's theming.
     * 
     * @param player The player whose coin balance should be displayed
     * @return An ItemStack representing the player's coin balance display
     */
    private static ItemStack createCoinsItem(Player player) {
        // Retrieve the player's data from the cache
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        
        // Create a sunflower item to represent the coin balance
        ItemStack coinItem = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = coinItem.getItemMeta();
        
        // Set the display name showing the current coin balance
        meta.setDisplayName(MessagesConf.Shops.COLOR_COINS_TEXT + MessagesConf.Shops.COINS + " " +
                MessagesConf.Shops.COLOR_COINS + entity.getCoins());
        
        // Add descriptive lore explaining the coin system
        meta.setLore(List.of(MessagesConf.Shops.COLOR_COINS_LORE + MessagesConf.Shops.COINS_LORE_1,
                MessagesConf.Shops.COLOR_COINS_LORE + MessagesConf.Shops.COINS_LORE_2));
        
        coinItem.setItemMeta(meta);
        return coinItem;
    }
}
