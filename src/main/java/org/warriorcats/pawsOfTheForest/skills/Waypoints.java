package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;
import org.bukkit.block.Biome;
import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

/**
 * Enumeration representing navigational waypoints for the Location Awareness and Trail Memory skills.
 * 
 * <p>Waypoints are automatically discovered when players enter specific biomes and can be used
 * for navigation and teleportation. Each waypoint type corresponds to a category of biomes
 * that serve similar purposes in Warrior Cat society.</p>
 * 
 * <p>The waypoint system supports:</p>
 * <ul>
 *   <li>Automatic discovery when entering qualifying biomes</li>
 *   <li>Compass navigation via Location Awareness skill</li>
 *   <li>Teleportation via Trail Memory skill</li>
 *   <li>Persistent storage across server restarts</li>
 * </ul>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public enum Waypoints {
    /** 
     * Camp waypoint - discovered in open, habitable areas suitable for clan camps.
     * Includes plains, meadows, beaches, and other open spaces.
     */
    CAMP(Set.of(
            Biome.PLAINS,
            Biome.SUNFLOWER_PLAINS,
            Biome.MEADOW,
            Biome.CHERRY_GROVE,
            Biome.BEACH,
            Biome.SNOWY_PLAINS
    )),

    /**
     * Den waypoint - discovered in forested areas suitable for individual dens.
     * Includes all forest types and wooded biomes.
     */
    DEN(Set.of(
            Biome.FOREST,
            Biome.BIRCH_FOREST,
            Biome.OLD_GROWTH_BIRCH_FOREST,
            Biome.DARK_FOREST,
            Biome.TAIGA,
            Biome.SNOWY_TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA,
            Biome.WINDSWEPT_FOREST
    )),

    /**
     * Herb Patch waypoint - discovered in areas rich with medicinal plants.
     * Includes flower forests, jungles, swamps, and rivers.
     */
    HERB_PATCH(Set.of(
            Biome.FLOWER_FOREST,
            Biome.JUNGLE,
            Biome.BAMBOO_JUNGLE,
            Biome.SPARSE_JUNGLE,
            Biome.SWAMP,
            Biome.MANGROVE_SWAMP,
            Biome.RIVER,
            Biome.FROZEN_RIVER
    )),

    /**
     * Hunting Grounds waypoint - discovered in areas suitable for hunting prey.
     * Includes savannas, hills, badlands, and other open hunting territories.
     */
    HUNTING_GROUNDS(Set.of(
            Biome.SAVANNA,
            Biome.WINDSWEPT_SAVANNA,
            Biome.WINDSWEPT_HILLS,
            Biome.WINDSWEPT_GRAVELLY_HILLS,
            Biome.BADLANDS,
            Biome.WOODED_BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.DESERT,
            Biome.STONY_PEAKS,
            Biome.GROVE,
            Biome.SNOWY_SLOPES
    ));

    /** Set of biomes that trigger discovery of this waypoint type */
    private Set<Biome> biomes;

    /**
     * Constructor for waypoint types.
     *
     * @param biomes set of biomes that trigger discovery of this waypoint
     */
    Waypoints(Set<Biome> biomes) {
        this.biomes = biomes;
    }

    /**
     * Returns a human-readable string representation of the waypoint.
     * Converts enum names to title case with spaces.
     *
     * @return formatted waypoint name
     */
    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    /**
     * Finds the waypoint type associated with a specific biome.
     *
     * @param biome the biome to check
     * @return Optional containing the associated waypoint, or empty if none found
     */
    public static Optional<Waypoints> getFromBiome(Biome biome) {
        for (Waypoints value : values()) {
            if (value.getBiomes().contains(biome)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets a waypoint by its index position for UI cycling.
     *
     * @param index the index (0-3)
     * @return the waypoint at the given index, or null if invalid
     */
    @Nullable public static Waypoints getFromIndex(int index) {
        return switch (index) {
            case 0 -> CAMP;
            case 1 -> DEN;
            case 2 -> HERB_PATCH;
            case 3 -> HUNTING_GROUNDS;
            default -> null;
        };
    }

    /**
     * Gets the index position of a waypoint for UI cycling.
     *
     * @param waypoint the waypoint to get the index for
     * @return the index (0-3) of the waypoint
     */
    public static int getIndex(Waypoints waypoint) {
        return switch (waypoint) {
            case CAMP -> 0;
            case DEN -> 1;
            case HERB_PATCH -> 2;
            case HUNTING_GROUNDS -> 3;
        };
    }

    /**
     * Parses a waypoint from a string representation.
     *
     * @param waypointStr the string to parse
     * @return the corresponding Waypoints enum value
     * @throws IllegalArgumentException if the string doesn't match any waypoint
     */
    public static Waypoints from(String waypointStr) {
        return EnumsUtils.from(waypointStr, Waypoints.class);
    }
}
