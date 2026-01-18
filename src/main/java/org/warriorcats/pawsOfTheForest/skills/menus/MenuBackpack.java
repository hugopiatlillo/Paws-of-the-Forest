package org.warriorcats.pawsOfTheForest.skills.menus;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsPassives;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;

/**
 * Menu utility class for creating and managing Beast of Burden backpack inventories.
 * 
 * <p>This class provides functionality to open expandable backpack inventories for players
 * who have unlocked the Beast of Burden skill. The backpack size scales with skill tier,
 * providing 9 additional inventory slots per tier level.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Dynamic inventory size based on skill tier</li>
 *   <li>Persistent storage of backpack contents</li>
 *   <li>Automatic loading of previously stored items</li>
 *   <li>Audio feedback when opening</li>
 * </ul>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
public abstract class MenuBackpack {

    /** The title displayed for backpack inventories */
    public static final String TITLE = Skills.BEAST_OF_BURDEN.getDisplayName();

    /**
     * Opens a backpack inventory for the specified player.
     * 
     * <p>Creates a custom inventory sized according to the player's Beast of Burden tier.
     * Each tier provides 9 additional slots. Previously stored items are automatically
     * loaded from the player's saved backpack data.</p>
     * 
     * <p>Inventory sizing:</p>
     * <ul>
     *   <li>Tier 1: 9 slots</li>
     *   <li>Tier 2: 18 slots</li>
     *   <li>Tier 3: 27 slots</li>
     *   <li>etc.</li>
     * </ul>
     *
     * @param player the player to open the backpack for
     * @param tier the Beast of Burden skill tier determining inventory size
     */
    public static void open(Player player, int tier) {
        Inventory menu = Bukkit.createInventory(player, tier * EventsSkillsPassives.BEAST_OF_BURDEN_TIER_VALUE, TITLE);

        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        if (entity.getBackpackData() != null) {
            ItemStack[] items = ItemsUtils.deserializeItemStackArray(entity.getBackpackData());
            int counter = 0;
            for (ItemStack item : items) {
                menu.setItem(counter, item);
                counter++;
            }
        }

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.2f);
    }
}
