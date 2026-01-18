package org.warriorcats.pawsOfTheForest.skills.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.warriorcats.pawsOfTheForest.clans.Clans;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu utility class for creating and managing the main skill tree selection interface.
 * 
 * <p>This class provides the main skill tree menu where players can view all available
 * skill branches and navigate to specific branch interfaces. The menu displays universal
 * branches available to all players, background-specific branches, and clan-exclusive
 * branches based on the player's clan membership.</p>
 * 
 * <p>Menu layout includes:</p>
 * <ul>
 *   <li><b>Row 1:</b> Universal branches (Hunting, Navigation, Resilience, Herbalist)</li>
 *   <li><b>Row 2:</b> Background branches (Kittypet, Loner, Rogue, City Cat)</li>
 *   <li><b>Row 3:</b> Clan-specific branches (Breeze, Echo, Creek, Shade)</li>
 *   <li><b>Bottom:</b> Close button and skill points display</li>
 * </ul>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
public abstract class MenuSkillTree {
    /** The title displayed for the main skill tree menu */
    public static final String TITLE = "Skill Trees";

    /** Slot index for the close/back button */
    public static final int INDEX_BACK = 36;
    /** Slot index for the skill points display item */
    public static final int INDEX_SKILLS_POINTS = 44;

    // Universal skill branch slot indices
    /** Slot index for the Hunting skill branch */
    public static final int INDEX_HUNTING = 1;
    /** Slot index for the Navigation skill branch */
    public static final int INDEX_NAVIGATION = 3;
    /** Slot index for the Resilience skill branch */
    public static final int INDEX_RESILIENCE = 5;
    /** Slot index for the Herbalist skill branch */
    public static final int INDEX_HERBALIST = 7;

    // Background skill branch slot indices
    /** Slot index for the Kittypet skill branch */
    public static final int INDEX_KITTYPET = 10;
    /** Slot index for the Loner skill branch */
    public static final int INDEX_LONER = 12;
    /** Slot index for the Rogue skill branch */
    public static final int INDEX_ROGUE = 14;
    /** Slot index for the City Cat skill branch */
    public static final int INDEX_CITY_CAT = 16;

    // Clan-specific skill branch slot indices
    /** Slot index for the Breeze Clan skill branch */
    public static final int INDEX_BREEZE_CLAN = 19;
    /** Slot index for the Echo Clan skill branch */
    public static final int INDEX_ECHO_CLAN = 21;
    /** Slot index for the Creek Clan skill branch */
    public static final int INDEX_CREEK_CLAN = 23;
    /** Slot index for the Shade Clan skill branch */
    public static final int INDEX_SHADE_CLAN = 25;

    // Menu text constants
    /** Text displayed for clickable menu items */
    public static final String TEXT_CLICK_TO_OPEN = MessagesConf.Skills.COLOR_DESCRIPTION + "Click to open";
    /** Text displayed for the close button */
    public static final String TEXT_CLOSE = MessagesConf.Skills.COLOR_DESCRIPTION + "Close";
    /** Text displayed for disabled/unavailable items */
    public static final String TEXT_DISABLED = MessagesConf.Skills.COLOR_DESCRIPTION + "Unavailable";
    /** Text displayed for the skill points indicator */
    public static final String TEXT_SKILL_POINTS = MessagesConf.Skills.COLOR_DESCRIPTION + "Skill Points";

    /**
     * Opens the main skill tree menu for the specified player.
     * 
     * <p>Creates and displays a 45-slot inventory showing all skill branches
     * available to the player. Clan-specific branches are only enabled if the
     * player belongs to the corresponding clan.</p>
     *
     * @param player the player to open the menu for
     */
    public static void open(Player player) {
        Inventory menu = Bukkit.createInventory(null, 45, TITLE);

        menu.setItem(INDEX_HUNTING, createMenuItem(Material.RABBIT, SkillBranches.HUNTING.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.HUNTING_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_NAVIGATION, createMenuItem(Material.COMPASS, SkillBranches.NAVIGATION.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.NAVIGATION_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_RESILIENCE, createMenuItem(Material.SHIELD, SkillBranches.RESILIENCE.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.RESILIENCE_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_HERBALIST, createMenuItem(Material.FERN, SkillBranches.HERBALIST.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.HERBALIST_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_KITTYPET, createMenuItem(Material.MILK_BUCKET, SkillBranches.KITTYPET.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.KITTYPET_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_LONER, createMenuItem(Material.LEATHER, SkillBranches.LONER.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.LONER_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_ROGUE, createMenuItem(Material.IRON_SWORD, SkillBranches.ROGUE.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.ROGUE_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_CITY_CAT, createMenuItem(Material.STONE_BRICKS, SkillBranches.CITY_CAT.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.CITY_CAT_DESCRIPTION
        ), true, true));

        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());

        menu.setItem(INDEX_BREEZE_CLAN, createMenuItem(Material.SUGAR, SkillBranches.BREEZE_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.BREEZE_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.BREEZE));

        menu.setItem(INDEX_ECHO_CLAN, createMenuItem(Material.OAK_LEAVES, SkillBranches.ECHO_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.ECHO_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.ECHO));

        menu.setItem(INDEX_CREEK_CLAN, createMenuItem(Material.KELP, SkillBranches.CREEK_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.CREEK_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.CREEK));

        menu.setItem(INDEX_SHADE_CLAN, createMenuItem(Material.ENDER_PEARL, SkillBranches.SHADE_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.SHADE_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.SHADE));

        menu.setItem(INDEX_BACK, createMenuItem(Material.BARRIER, TEXT_CLOSE, List.of(MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.MENU_EXIT), false, true));
        menu.setItem(INDEX_SKILLS_POINTS, createSkillPointsItemStack(player));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.2f);
    }

    /**
     * Gets the current skill points available to the player.
     *
     * @param player the player to check
     * @return the number of skill points available
     */
    public static double getSkillPoints(Player player) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        return entity.getXpPerks();
    }

    /**
     * Creates an item stack displaying the player's current skill points.
     *
     * @param player the player to create the item for
     * @return ItemStack showing skill points information
     */
    public static ItemStack createSkillPointsItemStack(Player player) {
        return createMenuItem(Material.NETHER_STAR, TEXT_SKILL_POINTS, List.of(MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.MENU_SKILL_POINTS + " " + getSkillPoints(player)), false, true);
    }

    private static ItemStack createMenuItem(Material material, String name, List<String> lore, boolean additionalLore, boolean enabled) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName((enabled ? "" : ChatColor.GRAY) + name);
        meta.setLore(new ArrayList<>(lore) {{
            if (additionalLore) {
                add("");
                add(enabled ? TEXT_CLICK_TO_OPEN : TEXT_DISABLED);
            }
        }});
        item.setItemMeta(meta);
        return item;
    }
}
