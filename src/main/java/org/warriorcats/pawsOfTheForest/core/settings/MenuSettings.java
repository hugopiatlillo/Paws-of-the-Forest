package org.warriorcats.pawsOfTheForest.core.settings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.Arrays;

/**
 * Utility class for creating and managing the settings menu interface.
 * 
 * <p>This abstract class provides static methods for generating the settings menu
 * inventory that players can interact with to configure their preferences. It handles
 * the visual representation of settings options and provides logic for cycling through
 * available chat channels based on player permissions and clan membership.</p>
 * 
 * <p>The settings menu includes:</p>
 * <ul>
 *   <li><strong>Roleplay Toggle:</strong> Enables/disables access to roleplay chat channels</li>
 *   <li><strong>Chat Channel Dropdown:</strong> Allows cycling through available chat channels</li>
 * </ul>
 * 
 * <p>Menu items are positioned at specific inventory slots and use color-coded
 * materials and display names to clearly communicate their current state to players.</p>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 * @see EventsSettings
 * @see SettingsEntity
 * @see ChatChannels
 */
public abstract class MenuSettings {

    /**
     * The title displayed at the top of the settings menu inventory.
     */
    public static final String TITLE = "Chat Settings";

    /**
     * Inventory slot index for the roleplay chat toggle button.
     * This toggle enables or disables access to roleplay chat channels.
     */
    public static final int INDEX_RP_TOGGLE = 10;
    
    /**
     * Inventory slot index for the chat channel dropdown selector.
     * This dropdown allows cycling through available chat channels.
     */
    public static final int INDEX_CHAT_DROPDOWN = 12;

    /**
     * Creates a settings menu inventory customized for the specified player.
     * 
     * <p>This method generates a new inventory with the appropriate items positioned
     * in their designated slots. The items reflect the player's current settings state:</p>
     * <ul>
     *   <li>Roleplay toggle shows current RP enablement status</li>
     *   <li>Chat dropdown shows currently selected chat channel</li>
     * </ul>
     * 
     * @param player The player for whom to create the settings menu
     * @return A new Inventory containing the settings menu items
     */
    public static Inventory create(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        inv.setItem(INDEX_RP_TOGGLE, createToggleItem("RP Chat", isRpEnabled(player)));
        inv.setItem(INDEX_CHAT_DROPDOWN, createChatDropdown("Toggled Chat", getToggledChat(player)));

        return inv;
    }

    /**
     * Checks whether roleplay chat channels are enabled for the specified player.
     * 
     * @param player The player to check roleplay settings for
     * @return true if roleplay chat is enabled, false otherwise
     */
    public static boolean isRpEnabled(Player player) {
        return fetchSettings(player).isShowRoleplay();
    }

    /**
     * Gets the currently selected chat channel for the specified player.
     * 
     * @param player The player to get the chat channel for
     * @return The currently toggled ChatChannel for the player
     */
    public static ChatChannels getToggledChat(Player player) {
        return fetchSettings(player).getToggledChat();
    }

    /**
     * Determines the next available chat channel in the cycling sequence for the specified player.
     * 
     * <p>This method applies filtering logic based on player permissions and settings:</p>
     * <ul>
     *   <li>If roleplay is disabled, roleplay channels are excluded from the cycle</li>
     *   <li>If the player has no clan, the clan channel is excluded from the cycle</li>
     *   <li>Returns the next channel in the filtered sequence, wrapping to the beginning if needed</li>
     * </ul>
     * 
     * @param player The player for whom to get the next chat channel
     * @param current The player's currently selected chat channel
     * @return The next available ChatChannel in the cycle
     */
    public static ChatChannels getNextChat(Player player, ChatChannels current) {
        ChatChannels[] values;
        SettingsEntity settings = fetchSettings(player);
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        
        // Filter available channels based on player permissions and settings
        values = Arrays.stream(ChatChannels.values()).filter(channel -> {
            boolean filtered = true;
            
            // Exclude roleplay channels if RP is disabled
            if (!settings.isShowRoleplay()) {
                filtered = filtered && !ChatChannels.isRoleplay(channel);
            }
            
            // Exclude clan channel if player has no clan
            if (entity.getClan() == null) {
                filtered = filtered && channel != ChatChannels.CLAN;
            }
            
            return filtered;
        }).toArray(ChatChannels[]::new);
        
        // Calculate next channel index with wraparound
        int currentIndex = Arrays.asList(values).indexOf(current);
        int nextIndex = (currentIndex + 1) % values.length;
        return values[nextIndex];
    }

    /**
     * Creates a toggle item stack with appropriate visual representation.
     * 
     * <p>Toggle items use different materials and colors to indicate their state:</p>
     * <ul>
     *   <li>Enabled: Lime dye with green "ON" text</li>
     *   <li>Disabled: Gray dye with red "OFF" text</li>
     * </ul>
     * 
     * @param name The display name for the toggle item
     * @param enabled Whether the toggle is currently enabled
     * @return An ItemStack representing the toggle in its current state
     */
    private static ItemStack createToggleItem(String name, boolean enabled) {
        Material mat = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " + (enabled ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a dropdown item stack showing the currently selected chat channel.
     * 
     * <p>The dropdown item uses a book material and displays the selected channel
     * name in aqua color to clearly indicate the current selection.</p>
     * 
     * @param name The display name for the dropdown item
     * @param selected The currently selected chat channel
     * @return An ItemStack representing the dropdown with the selected channel
     */
    private static ItemStack createChatDropdown(String name, ChatChannels selected) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " + ChatColor.AQUA + selected.toString());
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Retrieves the settings entity for the specified player from the cache.
     * 
     * <p>This method provides quick access to player settings without requiring
     * a database query by utilizing the player cache maintained by EventsCore.</p>
     * 
     * @param player The player whose settings to retrieve
     * @return The SettingsEntity associated with the player
     * @see EventsCore#PLAYERS_CACHE
     */
    private static SettingsEntity fetchSettings(Player player) {
        return EventsCore.PLAYERS_CACHE.get(player.getUniqueId()).getSettings();
    }
}

