package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

/**
 * Event listener for handling shop-related interactions and transactions.
 * 
 * This class manages all shop inventory interactions, including item purchases,
 * transaction processing, and shop interface management. It handles the core
 * business logic for the shop system, including balance validation, item
 * delivery, and database updates through Hibernate sessions.
 * 
 * The class listens for inventory click events specifically within the shop
 * interface and processes purchase transactions with proper error handling
 * and user feedback.
 * 
 * @author WarriorCats Plugin Team
 * @version 1.0
 * @since 1.0
 */
public class EventsShop implements Listener {

    /**
     * Handles inventory click events within the shop interface.
     * 
     * This method processes all inventory interactions when a player clicks on items
     * in the shop menu. It manages the complete purchase workflow including:
     * - Validating the click is within the shop interface
     * - Retrieving the selected shop item and its price
     * - Checking the player's coin balance
     * - Processing the transaction (deducting coins, adding item to inventory)
     * - Updating the player cache and database
     * - Providing appropriate feedback messages
     * 
     * The method uses Hibernate sessions for database operations and ensures
     * transaction integrity with proper commit/rollback handling.
     * 
     * @param event The inventory click event triggered by player interaction
     */
    @EventHandler
    public void on(InventoryClickEvent event) {
        // Ensure the clicker is a player
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        // Check if the click occurred in the shop interface
        if (event.getView().getTitle().equals(MenuShop.TITLE)) {
            event.setCancelled(true); // Prevent item movement in shop interface

            int slot = event.getRawSlot();
            ShopItem item = ShopsConf.Shops.SHOP_ITEMS.get(slot);
            if (item == null) return; // No item configured for this slot

            // Process the purchase transaction within a Hibernate session
            HibernateUtils.withSession(session -> {
                PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());

                long balance = entity.getCoins();

                // Check if player has sufficient funds for the purchase
                if (balance >= item.price()) {
                    var transaction = session.beginTransaction();
                    
                    // Deduct the item price from player's balance
                    entity.setCoins(balance - item.price());
                    
                    // Add the purchased item to player's inventory
                    player.getInventory().addItem(item.toItemStack());
                    
                    // Send purchase confirmation message
                    player.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.MADE_BUY + " " + item.price() + " Paw Coins.");
                    
                    // Update the player cache with new balance
                    EventsCore.PLAYERS_CACHE.put(player.getUniqueId(), entity);
                    
                    // Commit the transaction to persist changes
                    transaction.commit();
                } else {
                    // Inform player of insufficient funds
                    player.sendMessage(ChatColor.RED + MessagesConf.Preys.NOT_ENOUGH_COINS);
                }
            });
        }
    }
}
