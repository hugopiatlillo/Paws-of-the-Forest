package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;
import org.bukkit.Material;
import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of all available skills in the Warrior Cats skill system.
 * 
 * Skills are organized into different branches and can be either:
 * - Active skills: Require manual activation and have only 1 tier
 * - Passive skills: Provide automatic benefits and can have multiple tiers
 * 
 * Each skill belongs to a specific {@link SkillBranches} and has:
 * - A display name for UI purposes
 * - An active/passive flag
 * - Maximum number of tiers
 * - An associated Material icon for display
 * 
 * Skills are divided into several categories:
 * - General skills (Hunting, Navigation, Resilience, Herbalist)
 * - Background skills (Kittypet, Loner, Rogue, City Cat)
 * - Clan-specific skills (Breeze, Echo, Creek, Shade Clan)
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
@Getter
public enum Skills {
    // === HUNTING SKILLS ===
    /** Active skill: Reveals nearby prey animals */
    PREY_SENSE("Prey Sense", true, SkillBranches.HUNTING, 1, Material.GHAST_TEAR),
    /** Active skill: Points toward the nearest prey */
    HUNTERS_COMPASS("Hunter's Compass", true, SkillBranches.HUNTING, 1, Material.COMPASS),
    /** Active skill: Special hunting attack with knockdown effect */
    LOW_SWEEP("Low Sweep", true, SkillBranches.HUNTING, 1, Material.RABBIT_FOOT),
    /** Passive skill: Reduces footstep noise while hunting */
    SILENT_PAW("Silent Paw", false, SkillBranches.HUNTING, 3, Material.LEATHER),
    /** Passive skill: Tracks wounded prey more effectively */
    BLOOD_HUNTER("Blood Hunter", false, SkillBranches.HUNTING, 4, Material.REDSTONE),
    /** Passive skill: Increases food gained from successful hunts */
    EFFICIENT_KILL("Efficient Kill", false, SkillBranches.HUNTING, 3, Material.COOKED_BEEF),

    // === NAVIGATION SKILLS ===
    /** Active skill: Shows current coordinates and biome */
    LOCATION_AWARENESS("Location Awareness", true, SkillBranches.NAVIGATION, 1, Material.COMPASS),
    /** Active skill: Temporarily increases movement speed */
    PATHFINDING_BOOST("Pathfinding Boost", true, SkillBranches.NAVIGATION, 1, Material.FEATHER),
    /** Passive skill: Remembers and marks visited locations */
    TRAIL_MEMORY("Trail Memory", false, SkillBranches.NAVIGATION, 3, Material.FILLED_MAP),
    /** Passive skill: Reduces energy consumption while traveling */
    ENDURANCE_TRAVELER("Endurance Traveler", false, SkillBranches.NAVIGATION, 4, Material.COOKED_PORKCHOP),
    /** Passive skill: Improves climbing and reduces fall damage */
    CLIMBERS_GRACE("Climber's Grace", false, SkillBranches.NAVIGATION, 2, Material.LADDER),

    // === RESILIENCE SKILLS ===
    /** Active skill: Prevents death and provides temporary invulnerability */
    HOLD_ON("Hold On!", true, SkillBranches.RESILIENCE, 1, Material.TOTEM_OF_UNDYING),
    /** Active skill: Quickly recovers from downed state */
    ON_YOUR_PAWS("On Your Paws!", true, SkillBranches.RESILIENCE, 1, Material.GOLDEN_APPLE),
    /** Passive skill: Reduces incoming damage */
    IRON_HIDE("Iron Hide", false, SkillBranches.RESILIENCE, 3, Material.IRON_CHESTPLATE),
    /** Passive skill: Increases resistance to illnesses */
    IMMUNE_SYSTEM("Immune System", false, SkillBranches.RESILIENCE, 3, Material.SPIDER_EYE),
    /** Passive skill: Provides cold weather protection */
    THICK_COAT("Thick Coat", false, SkillBranches.RESILIENCE, 2, Material.SNOWBALL),
    /** Passive skill: Increases food effectiveness and satisfaction */
    HEARTY_APPETITE("Hearty Appetite", false, SkillBranches.RESILIENCE, 3, Material.COOKED_MUTTON),
    /** Passive skill: Increases inventory carrying capacity */
    BEAST_OF_BURDEN("Beast of Burden", false, SkillBranches.RESILIENCE, 2, Material.CHEST),

    // === HERBALIST SKILLS ===
    /** Active skill: Identifies herbs and their properties */
    HERB_KNOWLEDGE("Herb Knowledge", true, SkillBranches.HERBALIST, 1, Material.FERN),
    /** Active skill: Creates healing remedies from herbs */
    BREW_REMEDY("Brew Remedy", true, SkillBranches.HERBALIST, 1, Material.BREWING_STAND),
    /** Passive skill: Increases herb gathering speed and yield */
    QUICK_GATHERER("Quick Gatherer", false, SkillBranches.HERBALIST, 3, Material.SHEARS),
    /** Passive skill: Provides detailed information about plant properties */
    BOTANICAL_LORE("Botanical Lore", false, SkillBranches.HERBALIST, 3, Material.WRITABLE_BOOK),
    /** Passive skill: Prevents herb contamination and improves hygiene */
    CLEAN_PAWS("Clean Paws", false, SkillBranches.HERBALIST, 2, Material.HONEYCOMB),

    // === KITTYPET BACKGROUND SKILLS ===
    /** Passive skill: Always well-nourished, slower hunger decay */
    WELL_FED("Well-Fed", false, SkillBranches.KITTYPET, 1, Material.COOKED_SALMON),
    /** Passive skill: Better hygiene maintenance and comfort */
    PAMPERED("Pampered", false, SkillBranches.KITTYPET, 1, Material.MILK_BUCKET),
    /** Passive skill: Protected from certain harsh realities */
    SHELTERED_MIND("Sheltered Mind", false, SkillBranches.KITTYPET, 1, Material.BOOK),

    // === LONER BACKGROUND SKILLS ===
    /** Passive skill: Enhanced tracking abilities */
    TRACKER("Tracker", false, SkillBranches.LONER, 1, Material.COMPASS),
    /** Passive skill: Improved resource gathering and survival */
    CRAFTY("Crafty", false, SkillBranches.LONER, 1, Material.FERN),
    /** Passive skill: Can interact with normally hostile groups */
    FLEXIBLE_MORALS("Flexible Morals", false, SkillBranches.LONER, 1, Material.EMERALD),

    // === ROGUE BACKGROUND SKILLS ===
    /** Passive skill: Bonus damage from stealth attacks */
    AMBUSHER("Ambusher", false, SkillBranches.ROGUE, 1, Material.IRON_SWORD),
    /** Passive skill: Can find food in unusual places */
    SCAVENGE("Scavenge", false, SkillBranches.ROGUE, 1, Material.ROTTEN_FLESH),
    /** Passive skill: Increased resilience from tough upbringing */
    HARD_KNOCK_LIFE("Hard Knock Life", false, SkillBranches.ROGUE, 1, Material.LEATHER_CHESTPLATE),

    // === CITY CAT BACKGROUND SKILLS ===
    /** Passive skill: Better navigation in urban environments */
    URBAN_NAVIGATION("Urban Navigation", false, SkillBranches.CITY_CAT, 1, Material.STONE),
    /** Passive skill: Specialized hunting for small urban prey */
    RAT_CATCHER("Rat Catcher", false, SkillBranches.CITY_CAT, 1, Material.RABBIT),
    /** Passive skill: Resistance to urban diseases and toxins */
    DISEASE_RESISTANCE("Disease Resistance", false, SkillBranches.CITY_CAT, 1, Material.SPIDER_EYE),

    // === BREEZE CLAN SKILLS ===
    /** Passive skill: Increased speed in open terrain */
    SPEED_OF_THE_MOOR("Speed of the Moor", false, SkillBranches.BREEZE_CLAN, 1, Material.SUGAR),
    /** Passive skill: Even lighter footsteps and improved stealth */
    LIGHTSTEP("Lightstep", false, SkillBranches.BREEZE_CLAN, 1, Material.FEATHER),
    /** Passive skill: Wind-based combat techniques */
    SHARP_WIND("Sharp Wind", false, SkillBranches.BREEZE_CLAN, 1, Material.PAPER),

    // === ECHO CLAN SKILLS ===
    /** Passive skill: Enhanced protection from environmental damage */
    THICK_PELT("Thick Pelt", false, SkillBranches.ECHO_CLAN, 1, Material.LEATHER),
    /** Passive skill: Better camouflage in forest environments */
    FOREST_COVER("Forest Cover", false, SkillBranches.ECHO_CLAN, 1, Material.OAK_LEAVES),
    /** Passive skill: Attacks have chance to stun opponents */
    STUNNING_BLOW("Stunning Blow", false, SkillBranches.ECHO_CLAN, 1, Material.STONE_AXE),

    // === CREEK CLAN SKILLS ===
    /** Passive skill: Enhanced swimming speed and water breathing */
    STRONG_SWIMMER("Strong Swimmer", false, SkillBranches.CREEK_CLAN, 1, Material.KELP),
    /** Passive skill: Better balance and stability near water */
    AQUA_BALANCE("Aqua Balance", false, SkillBranches.CREEK_CLAN, 1, Material.FISHING_ROD),
    /** Passive skill: Water provides healing and protection */
    WATERS_RESILIENCE("Water's Resilience", false, SkillBranches.CREEK_CLAN, 1, Material.TURTLE_HELMET),

    // === SHADE CLAN SKILLS ===
    /** Passive skill: Enhanced abilities during nighttime */
    NIGHTSTALKER("Nightstalker", false, SkillBranches.SHADE_CLAN, 1, Material.ENDER_PEARL),
    /** Passive skill: Attacks can poison enemies */
    TOXIC_CLAWS("Toxic Claws", false, SkillBranches.SHADE_CLAN, 1, Material.POISONOUS_POTATO),
    /** Passive skill: Kills don't alert nearby enemies */
    SILENT_KILL("Silent Kill", false, SkillBranches.SHADE_CLAN, 1, Material.IRON_SWORD);

    /** The display name shown to players */
    private final String displayName;
    /** Whether this skill requires manual activation */
    private final boolean isActive;
    /** Which skill branch this skill belongs to */
    private final SkillBranches branch;
    /** Maximum number of tiers this skill can have */
    private final int maxTiers;
    /** The Material icon used for UI display */
    private final Material icon;

    /**
     * Constructor for skill enum values.
     * 
     * @param displayName The human-readable name for the skill
     * @param isActive Whether the skill requires manual activation
     * @param branch The skill branch this skill belongs to
     * @param maxTiers Maximum number of tiers (must be 1 for active skills)
     * @param icon The Material icon for UI display
     * @throws IllegalArgumentException if an active skill has more than 1 tier
     */
    Skills(String displayName, boolean isActive, SkillBranches branch, int maxTiers, Material icon) {
        if (isActive && maxTiers != 1) {
            throw new IllegalArgumentException("An active skill must have only 1 max tier");
        }
        this.displayName = displayName;
        this.isActive = isActive;
        this.branch = branch;
        this.maxTiers = maxTiers;
        this.icon = icon;
    }

    /**
     * Calculates the current tier of this skill based on experience points.
     * Active skills always return their max tier (1).
     * Passive skills calculate tier based on XP thresholds.
     * 
     * @param xp The current experience points in this skill
     * @return The current tier level
     */
    public int getCurrentTier(double xp) {
        if (isActive) {
            return maxTiers;
        }
        return (int) Math.round(xp / SkillBranches.UNLOCK_SKILL_TIER);
    }

    /**
     * Returns the display name of the skill.
     * 
     * @return The display name
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Parses a skill from its string representation.
     * 
     * @param skillsStr The string representation of the skill
     * @return The corresponding Skills enum value
     * @throws IllegalArgumentException if the string doesn't match any skill
     */
    public static Skills from(String skillsStr) {
        return EnumsUtils.from(skillsStr, Skills.class);
    }

    /**
     * Gets all skills that are active (require manual activation).
     * 
     * @return A set containing all active skills
     */
    public static Set<Skills> getActiveSkills() {
        return Arrays.stream(values()).filter(Skills::isActive).collect(Collectors.toSet());
    }
}
