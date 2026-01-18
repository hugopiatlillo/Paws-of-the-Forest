package org.warriorcats.pawsOfTheForest.core.settings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

/**
 * Event listener for handling player interactions with the settings system.
 * 
 * <p>This class manages all user interactions with the settings menu, including
 * inventory clicks for configuration options and item interactions that open
 * the settings interface. It handles both chat-related settings and the
 * settings menu activation mechanism.</p>
 * 
 * <p>Key responsibilities include:</p>
 * <ul>
 *   <li>Processing settings menu inventory clicks</li>
 *   <li>Updating player chat preferences in the database</li>
 *   <li>Handling roleplay chat toggle functionality</li>
 *   <li>Managing chat channel selection and cycling</li>
 *   <li>Opening settings menu via note block item interaction</li>
 * </ul>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 * @see MenuSettings
 * @see SettingsEntity
 */
public class EventsSettings implements Listener {

    /**
     * Handles inventory clicks within the settings menu to process player configuration changes.
     * 
     * <p>This method processes clicks on various settings options within the settings inventory:</p>
     * <ul>
     *   <li><strong>Roleplay Toggle:</strong> Enables/disables roleplay chat channels and resets
     *       chat selection to default if RP is disabled while on an RP channel</li>
     *   <li><strong>Chat Dropdown:</strong> Cycles through available chat channels based on
     *       player's current settings and clan membership status</li>
     * </ul>
     * 
     * <p>All database operations are performed asynchronously to prevent server lag,
     * with UI updates scheduled back to the main thread for thread safety.</p>
     * 
     * @param event The InventoryClickEvent containing click information and player
     */
    @EventHandler
    public void on(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(MenuSettings.TITLE)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        // Handle roleplay chat toggle
        if (slot == MenuSettings.INDEX_RP_TOGGLE) {
            Bukkit.getScheduler().runTaskAsynchronously(PawsOfTheForest.getInstance(), () -> {
                HibernateUtils.withTransaction(((transaction, session) -> {
                    PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                    boolean current = entity.getSettings().isShowRoleplay();
                    entity.getSettings().setShowRoleplay(!current);
                    if (!entity.getSettings().isShowRoleplay() && ChatChannels.isRoleplay(entity.getSettings().getToggledChat())) {
                        // Resetting the chat toggled if user disabled RP, and it was RP channel
                        entity.getSettings().setToggledChat(ChatChannels.DEFAULT_TOGGLED);
                    }
                    return entity;
                }));
                // Refresh the settings menu on the main thread
                Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
                    player.openInventory(MenuSettings.create(player));
                });
            });
        }

        // Handle chat channel dropdown selection
        if (slot == MenuSettings.INDEX_CHAT_DROPDOWN) {
            Bukkit.getScheduler().runTaskAsynchronously(PawsOfTheForest.getInstance(), () -> {
                HibernateUtils.withTransaction(((transaction, session) -> {
                    PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                    ChatChannels current = entity.getSettings().getToggledChat();
                    ChatChannels next = MenuSettings.getNextChat(player, current);
                    entity.getSettings().setToggledChat(next);
                    return entity;
                }));
                // Refresh the settings menu on the main thread
                Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
                    player.openInventory(MenuSettings.create(player));
                });
            });
        }
    }

    /**
     * Handles player interactions with items to trigger settings menu opening.
     * 
     * <p>This method specifically listens for right-click interactions with note block items,
     * which serve as the trigger mechanism for opening the settings menu. When a player
     * right-clicks with a note block item, the settings menu inventory is opened and the
     * interaction event is cancelled to prevent the default note block behavior.</p>
     * 
     * @param event The PlayerInteractEvent containing interaction details
     */
    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Only process note block items
        if (item == null || item.getType() != Material.NOTE_BLOCK) return;
        
        // Open settings menu on right-click
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            player.openInventory(MenuSettings.create(player));
            event.setCancelled(true);
        }
    }
}
