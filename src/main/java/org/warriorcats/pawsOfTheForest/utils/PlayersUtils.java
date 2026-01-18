package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsActives;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utility class providing helper methods for player-related operations.
 * 
 * This class contains static methods for:
 * - Player state management (downed state, cooldowns)
 * - Inventory synchronization with skill system
 * - Player location and proximity calculations
 * - Waypoint and navigation helpers
 * - Movement speed modifications
 * 
 * The class uses Bukkit metadata system for temporary player state storage
 * and integrates with the plugin's skill and combat systems.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class PlayersUtils {

    /** Metadata key for tracking if a player is in downed state */
    private static final String META_DOWNED_KEY = "downed";
    /** Metadata key for tracking Hold On skill cooldown */
    private static final String META_DOWNED_CD_KEY = "hold_on_cd";
    /** Metadata key for tracking current waypoint index */
    private static final String META_WAYPOINT_INDEX = "waypoint_index";

    /**
     * Finds the nearest player to the given source player.
     * Only considers players in the same world as the source.
     * 
     * @param source The player to find the nearest neighbor for
     * @return Optional containing the nearest player, or empty if no valid players found
     */
    public static Optional<Player> getNearestPlayer(Player source) {
        Location sourceLoc = source.getLocation();
        Player nearest = null;
        double minDistanceSquared = Double.MAX_VALUE;

        // Search through all online players
        for (Player target : Bukkit.getOnlinePlayers()) {
            // Skip self and players in different worlds
            if (target.equals(source) || !target.getWorld().equals(source.getWorld())) continue;

            double distanceSquared = sourceLoc.distanceSquared(target.getLocation());
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                nearest = target;
            }
        }

        return Optional.ofNullable(nearest);
    }

    /**
     * Gets the current waypoint index for a player's navigation.
     * 
     * @param player The player to check
     * @return The waypoint index, or -1 if no waypoint is set
     */
    public static int getWaypointIndex(Player player) {
        return player.hasMetadata(META_WAYPOINT_INDEX)
                ? player.getMetadata(META_WAYPOINT_INDEX).getFirst().asInt()
                : -1;
    }

    /**
     * Sets the current waypoint index for a player's navigation.
     * 
     * @param player The player to update
     * @param index The waypoint index to set
     */
    public static void setWaypointIndex(Player player, int index) {
        player.setMetadata(META_WAYPOINT_INDEX,
                new FixedMetadataValue(PawsOfTheForest.getInstance(), index));
    }

    /**
     * Checks if a player is currently in the downed state (incapacitated but not dead).
     * 
     * @param player The player to check
     * @return true if the player is downed
     */
    public static boolean isDowned(Player player) {
        return player.hasMetadata(META_DOWNED_KEY) && player.getMetadata(META_DOWNED_KEY).getFirst().asBoolean();
    }

    /**
     * Sets or removes the downed state for a player.
     * 
     * @param player The player to update
     * @param state true to mark as downed, false to remove downed state
     */
    public static void setDowned(Player player, boolean state) {
        if (state) {
            player.setMetadata(META_DOWNED_KEY, new FixedMetadataValue(PawsOfTheForest.getInstance(), true));
        } else {
            player.removeMetadata(META_DOWNED_KEY, PawsOfTheForest.getInstance());
        }
    }

    /**
     * Gets the remaining cooldown time for the Hold On skill in seconds.
     * 
     * @param player The player to check
     * @return Remaining cooldown in seconds, or 0 if no cooldown
     */
    public static long getDownedCooldown(Player player) {
        if (!player.hasMetadata(META_DOWNED_CD_KEY)) return 0;

        long remaining = player.getMetadata(META_DOWNED_CD_KEY).getFirst().asLong() - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    /**
     * Checks if the Hold On skill is currently on cooldown for a player.
     * 
     * @param player The player to check
     * @return true if Hold On is on cooldown
     */
    public static boolean hasHoldOnOnCooldown(Player player) {
        return player.hasMetadata(META_DOWNED_CD_KEY) &&
                player.getMetadata(META_DOWNED_CD_KEY).getFirst().asLong() > System.currentTimeMillis();
    }

    /**
     * Marks the Hold On skill as used and starts its cooldown timer.
     * 
     * @param player The player who used the skill
     */
    public static void markHoldOnUsed(Player player) {
        long until = System.currentTimeMillis() + (EventsSkillsActives.HOLD_ON_COOLDOWN_S * 1000);
        player.setMetadata(META_DOWNED_CD_KEY, new FixedMetadataValue(PawsOfTheForest.getInstance(), until));
    }

    public static void increaseMovementSpeed(Player player, Supplier<Boolean> condition, double factor, float defaultSpeed) {
        if (condition.get()) {
            player.setWalkSpeed((float) (defaultSpeed * (1 + factor)));
        } else {
            player.setWalkSpeed(defaultSpeed);
        }
    }

    public static boolean hasActiveSkillInInventory(Player player, Skills skill) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (ItemsUtils.isActiveSkill(player, item)) {
                ItemStack activeSkill = ItemsUtils.getActiveSkill(player, skill);
                if (ItemsUtils.isSameItem(activeSkill, item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasNoteBlockInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (!ItemsUtils.isEmpty(item) && item.getType() == Material.NOTE_BLOCK) {
                return true;
            }
        }
        return false;
    }

    /**
     * Synchronizes a player's inventory with their current active skills.
     * This is a convenience method that fetches the player entity from cache.
     * 
     * @param player The player whose inventory should be synchronized
     */
    public static void synchronizeInventory(Player player) {
        synchronizeInventory(player, EventsCore.PLAYERS_CACHE.get(player.getUniqueId()));
    }

    /**
     * Synchronizes a player's inventory to ensure they have all their active skills available.
     * 
     * This method performs a comprehensive inventory synchronization:
     * 1. Identifies which active skills the player should have
     * 2. Clears existing skill items and note blocks from inventory
     * 3. Calculates space needed for new items
     * 4. Drops non-essential items if space is insufficient
     * 5. Adds the required skill items and note block
     * 
     * The note block is used for certain game mechanics and is always provided.
     * 
     * @param player The player whose inventory should be synchronized
     * @param entity The player's database entity containing skill information
     */
    public static void synchronizeInventory(Player player, PlayerEntity entity) {
        // Get list of active skills this player has unlocked
        List<Skills> skills = Skills.getActiveSkills().stream()
                .filter(entity::hasAbility)
                .toList();

        // Phase 1: Clear existing skill items and note blocks from inventory
        // This prevents duplicates and ensures clean slate
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getContents()[i];
            if (!ItemsUtils.isEmpty(itemStack) &&
                    (ItemsUtils.isActiveSkill(player, itemStack) || itemStack.getType() == Material.NOTE_BLOCK)) {
                player.getInventory().clear(i);
            }
        }

        // Phase 2: Prepare list of items that need to be added
        List<ItemStack> itemsToAdd = new ArrayList<>();
        
        // Add skill items that aren't already present
        for (Skills skill : skills) {
            if (!hasActiveSkillInInventory(player, skill)) {
                itemsToAdd.add(ItemsUtils.getActiveSkill(player, skill));
            }
        }
        
        // Always ensure player has a note block for game mechanics
        boolean needNoteBlock = !hasNoteBlockInInventory(player);
        if (needNoteBlock) {
            itemsToAdd.add(new ItemStack(Material.NOTE_BLOCK));
        }

        int neededSlots = itemsToAdd.size();

        // Phase 3: Calculate available inventory space
        ItemStack[] contents = player.getInventory().getContents();
        int freeSlots = (int) Arrays.stream(contents)
                .filter(ItemsUtils::isEmpty)
                .count();

        // Phase 4: Make room if necessary by dropping non-essential items
        if (freeSlots < neededSlots) {
            int toDrop = neededSlots - freeSlots;

            // Drop items until we have enough space, but preserve skill items and note blocks
            for (int i = 0; i < contents.length && toDrop > 0; i++) {
                ItemStack item = contents[i];
                
                // Skip empty slots, skill items, and note blocks
                if (ItemsUtils.isEmpty(item)) continue;
                if (ItemsUtils.isActiveSkill(player, item)) continue;
                if (item.getType() == Material.NOTE_BLOCK) continue;

                // Drop this item to make space
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                player.getInventory().clear(i);
                toDrop--;
            }
        }

        // Phase 5: Add all required items to inventory
        for (ItemStack itemStack : itemsToAdd) {
            player.getInventory().addItem(itemStack);
        }

        // Force inventory update to ensure client sees changes
        player.updateInventory();
    }
}
