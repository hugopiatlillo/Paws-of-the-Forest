package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Utility class for skill-related operations and attribute management.
 * 
 * <p>This class provides functionality for managing player skills that affect
 * Minecraft attributes. It handles attribute modifier application and removal
 * for various skill effects, particularly armor bonuses from specific skills.</p>
 * 
 * <p>The class manages persistent attribute modifiers using unique UUIDs and
 * names to ensure proper tracking and cleanup of skill-based bonuses.</p>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SkillsUtils {

    /** Name identifier for the Iron Hide skill armor bonus modifier. */
    private static final String NAME_IRON_HIDE_BONUS = "IRON_HIDE_BONUS";
    /** Unique UUID for the Iron Hide skill armor bonus modifier. */
    private static final UUID UUID_IRON_HIDE_BONUS = UUID.fromString("00000000-0000-0000-0000-000000000001");

    /** Name identifier for the Hard Knock Life skill armor bonus modifier. */
    private static final String NAME_HARD_KNOCK_LIFE_BONUS = "HARD_KNOCK_LIFE_BONUS";
    /** Unique UUID for the Hard Knock Life skill armor bonus modifier. */
    private static final UUID UUID_HARD_KNOCK_LIFE_BONUS = UUID.fromString("00000000-0000-0000-0000-000000000002");

    /**
     * Updates the Iron Hide skill armor bonus for a player.
     * 
     * <p>This method removes any existing Iron Hide armor modifiers and applies
     * a new one based on the skill tier. If the tier is 0 or negative, no bonus
     * is applied.</p>
     * 
     * @param player the player to update the armor bonus for
     * @param tier the tier level of the Iron Hide skill (0 removes the bonus)
     */
    public static void updateIronHideArmor(Player player, int tier) {
        var attr = player.getAttribute(Attribute.GENERIC_ARMOR);

        // Remove any existing Iron Hide modifiers
        removeModifiers(UUID_IRON_HIDE_BONUS, NAME_IRON_HIDE_BONUS, attr);

        // Apply new armor bonus if tier is positive
        if (tier > 0) {
            AttributeModifier mod = new AttributeModifier(
                    UUID_IRON_HIDE_BONUS,
                    NAME_IRON_HIDE_BONUS,
                    tier,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            attr.addModifier(mod);
        }
    }

    /**
     * Updates the Hard Knock Life skill armor bonus for a player.
     * 
     * <p>This method removes any existing Hard Knock Life armor modifiers and
     * applies a fixed +1 armor bonus.</p>
     * 
     * @param player the player to update the armor bonus for
     */
    public static void updateHardKnockLifeArmor(Player player) {
        var attr = player.getAttribute(Attribute.GENERIC_ARMOR);

        // Remove any existing Hard Knock Life modifiers
        removeModifiers(UUID_HARD_KNOCK_LIFE_BONUS, NAME_HARD_KNOCK_LIFE_BONUS, attr);

        AttributeModifier mod = new AttributeModifier(
                UUID_HARD_KNOCK_LIFE_BONUS,
                NAME_HARD_KNOCK_LIFE_BONUS,
                1,
                AttributeModifier.Operation.ADD_NUMBER
        );
        attr.addModifier(mod);
    }

    /**
     * Removes attribute modifiers matching the given UUID or name.
     * 
     * <p>This method ensures clean removal of skill-based modifiers by checking
     * both UUID and name identifiers to handle any potential duplicates.</p>
     * 
     * @param uuid the UUID of the modifier to remove
     * @param name the name of the modifier to remove
     * @param attr the attribute instance to remove modifiers from
     */
    private static void removeModifiers(UUID uuid, String name, AttributeInstance attr) {
        // Find all modifiers matching the UUID or name
        var toRemove = attr.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(uuid)
                        || mod.getName().equals(name))
                .toList();
        // Remove all matching modifiers
        toRemove.forEach(attr::removeModifier);
    }
}
