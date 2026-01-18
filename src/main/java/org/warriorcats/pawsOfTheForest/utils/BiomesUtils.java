package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;

import java.util.Set;

/**
 * Utility class for biome-related operations and environmental checks.
 * 
 * <p>This class provides a collection of methods and constants for identifying
 * different types of biomes, checking environmental conditions, and determining
 * damage types related to environmental effects. It includes categorized sets
 * of biomes (water, forest, plains, cold, hot) and methods for spatial and
 * temporal environmental analysis.</p>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class BiomesUtils {

    /**
     * Set of all water-based biomes including oceans and rivers.
     * Used for determining if an entity is in an aquatic environment.
     */
    public static final Set<Biome> WATER_BIOMES = Set.of(
            Biome.OCEAN,
            Biome.DEEP_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,
            Biome.RIVER,
            Biome.FROZEN_RIVER
    );

    /**
     * Set of all forest-based biomes including various types of forests and taigas.
     * Used for determining if an entity is in a forested environment.
     */
    public static final Set<Biome> FOREST_BIOMES = Set.of(
            Biome.FOREST,
            Biome.BIRCH_FOREST,
            Biome.OLD_GROWTH_BIRCH_FOREST,
            Biome.DARK_FOREST,
            Biome.FLOWER_FOREST,
            Biome.TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA,
            Biome.SNOWY_TAIGA,
            Biome.WINDSWEPT_FOREST,
            Biome.GROVE,
            Biome.WOODED_BADLANDS
    );

    /**
     * Set of all plains-based biomes including meadows and savannas.
     * Used for determining if an entity is in an open grassland environment.
     */
    public static final Set<Biome> PLAINS_BIOMES = Set.of(
            Biome.PLAINS,
            Biome.SUNFLOWER_PLAINS,
            Biome.SAVANNA,
            Biome.SAVANNA_PLATEAU,
            Biome.WINDSWEPT_SAVANNA,
            Biome.MEADOW
    );

    /**
     * Set of all cold biomes including frozen areas and snowy regions.
     * Used for determining if an entity is in a cold environment that may affect gameplay.
     */
    public static final Set<Biome> COLD_BIOMES = Set.of(
            Biome.FROZEN_OCEAN,
            Biome.FROZEN_RIVER,
            Biome.SNOWY_PLAINS,
            Biome.SNOWY_BEACH,
            Biome.SNOWY_TAIGA,
            Biome.ICE_SPIKES,
            Biome.GROVE,
            Biome.SNOWY_SLOPES,
            Biome.FROZEN_PEAKS,
            Biome.JAGGED_PEAKS,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA
    );

    /**
     * Set of all hot biomes including deserts, badlands, jungles, and nether biomes.
     * Used for determining if an entity is in a hot environment that may affect gameplay.
     */
    public static final Set<Biome> HOT_BIOMES = Set.of(
            Biome.DESERT,
            Biome.BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS,
            Biome.SAVANNA,
            Biome.SAVANNA_PLATEAU,
            Biome.WINDSWEPT_SAVANNA,
            Biome.JUNGLE,
            Biome.SPARSE_JUNGLE,
            Biome.BAMBOO_JUNGLE,
            Biome.WARM_OCEAN,
            Biome.NETHER_WASTES,
            Biome.SOUL_SAND_VALLEY,
            Biome.CRIMSON_FOREST,
            Biome.WARPED_FOREST,
            Biome.BASALT_DELTAS
    );

    /**
     * Set of all damage types related to fire and heat.
     * Used for determining if damage is fire-based for gameplay mechanics.
     */
    public static final Set<DamageType> FIRE_DAMAGE_TYPES = Set.of(
            DamageType.IN_FIRE,
            DamageType.ON_FIRE,
            DamageType.CAMPFIRE,
            DamageType.LAVA,
            DamageType.HOT_FLOOR,
            DamageType.FIREBALL,
            DamageType.UNATTRIBUTED_FIREBALL
    );

    /**
     * Set of all damage types related to freezing.
     * Used for determining if damage is freeze-based for gameplay mechanics.
     */
    public static final Set<DamageType> FREEZE_DAMAGE_TYPES = Set.of(
            DamageType.FREEZE
    );

    /**
     * Checks if a location has open space above it with sufficient sky light.
     * 
     * <p>A location is considered open space if there are no solid blocks
     * in the 10 blocks directly above it and the sky light level is at least 14.</p>
     * 
     * @param loc the location to check
     * @return true if the location has open space above it, false otherwise
     */
    public static boolean isOpenSpace(Location loc) {
        // Check if any of the 10 blocks above are solid
        for (int y = 1; y <= 10; y++) {
            if (!loc.clone().add(0, y, 0).getBlock().isEmpty()) {
                return false;
            }
        }
        // Ensure sufficient sky light (minimum 14 out of 15)
        return loc.getBlock().getLightFromSky() >= 14;
    }

    /**
     * Checks if the given biome is a water-based biome.
     * 
     * @param biome the biome to check
     * @return true if the biome is water-based, false otherwise
     * @see #WATER_BIOMES
     */
    public static boolean isWater(Biome biome) {
        return WATER_BIOMES.contains(biome);
    }

    /**
     * Checks if the given biome is a forest-based biome.
     * 
     * @param biome the biome to check
     * @return true if the biome is forest-based, false otherwise
     * @see #FOREST_BIOMES
     */
    public static boolean isForest(Biome biome) {
        return FOREST_BIOMES.contains(biome);
    }

    /**
     * Checks if the given biome is a plains-based biome.
     * 
     * @param biome the biome to check
     * @return true if the biome is plains-based, false otherwise
     * @see #PLAINS_BIOMES
     */
    public static boolean isPlain(Biome biome) {
        return PLAINS_BIOMES.contains(biome);
    }

    /**
     * Checks if the given biome is a cold biome.
     * 
     * @param biome the biome to check
     * @return true if the biome is cold, false otherwise
     * @see #COLD_BIOMES
     */
    public static boolean isCold(Biome biome) {
        return COLD_BIOMES.contains(biome);
    }

    /**
     * Checks if the given block is cold-related (ice, snow, etc.).
     * 
     * <p>This method checks for specific cold block types like snow, ice variants,
     * and any block type containing "ICE" in its name.</p>
     * 
     * @param block the block to check
     * @return true if the block is cold-related, false otherwise
     */
    public static boolean isCold(Block block) {
        return switch (block.getType()) {
            case SNOW, SNOW_BLOCK, ICE, PACKED_ICE, BLUE_ICE, FROSTED_ICE -> true;
            default -> block.getType().name().contains("ICE");
        };
    }

    /**
     * Checks if the given biome is a hot biome.
     * 
     * @param biome the biome to check
     * @return true if the biome is hot, false otherwise
     * @see #HOT_BIOMES
     */
    public static boolean isHot(Biome biome) {
        return HOT_BIOMES.contains(biome);
    }

    /**
     * Checks if it is nighttime in the given world.
     * 
     * <p>Night is defined as game time between 13000 and 23000 ticks.</p>
     * 
     * @param world the world to check
     * @return true if it is nighttime, false otherwise
     */
    public static boolean isNight(World world) {
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }

    /**
     * Checks if a location is dark (has low light levels).
     * 
     * <p>A location is considered dark if its light level is below 8.</p>
     * 
     * @param location the location to check
     * @return true if the location is dark, false otherwise
     */
    public static boolean isDark(Location location) {
        return location.getBlock().getLightLevel() < 8;
    }

    /**
     * Checks if the given damage type is fire-related.
     * 
     * @param damageType the damage type to check
     * @return true if the damage is fire-related, false otherwise
     * @see #FIRE_DAMAGE_TYPES
     */
    public static boolean isDamageFromFire(DamageType damageType) {
        return FIRE_DAMAGE_TYPES.contains(damageType);
    }

    /**
     * Checks if the given damage type is freeze-related.
     * 
     * @param damageType the damage type to check
     * @return true if the damage is freeze-related, false otherwise
     * @see #FREEZE_DAMAGE_TYPES
     */
    public static boolean isDamageFromFreeze(DamageType damageType) {
        return FREEZE_DAMAGE_TYPES.contains(damageType);
    }

    /**
     * Checks if it's currently a full moon in the given world.
     * 
     * <p>In Minecraft, the moon phase cycle lasts 8 days (160,000 ticks).
     * A full moon occurs every 8th day when the moon phase is 0.</p>
     * 
     * @param world the world to check the moon phase for
     * @return true if it's a full moon, false otherwise
     */
    public static boolean isFullMoon(World world) {
        // Moon cycle is 8 days (8 * 24000 ticks = 192000 ticks total cycle)
        // Full moon is when moon phase is 0
        long fullTime = world.getFullTime();
        long dayInCycle = (fullTime / 24000) % 8;
        return dayInCycle == 0;
    }

    /**
     * Checks if the given location is in a high mountain area.
     * 
     * <p>A location is considered high mountain if it meets all of the following criteria:
     * <ul>
     * <li>Y coordinate is above 120 (high altitude)</li>
     * <li>Located in a mountain-type biome</li>
     * </ul></p>
     * 
     * @param location the location to check
     * @return true if the location is in high mountains, false otherwise
     */
    public static boolean isHighMountain(Location location) {
        // Check if altitude is high enough (above Y=120)
        if (location.getY() < 120) {
            return false;
        }

        // Check if biome is mountain-related
        Biome biome = location.getBlock().getBiome();
        Set<Biome> mountainBiomes = Set.of(
                Biome.JAGGED_PEAKS,
                Biome.FROZEN_PEAKS,
                Biome.STONY_PEAKS,
                Biome.SNOWY_SLOPES,
                Biome.GROVE,
                Biome.MEADOW,
                Biome.WINDSWEPT_HILLS,
                Biome.WINDSWEPT_GRAVELLY_HILLS,
                Biome.WINDSWEPT_FOREST,
                Biome.WINDSWEPT_SAVANNA
        );

        return mountainBiomes.contains(biome);
    }
}
