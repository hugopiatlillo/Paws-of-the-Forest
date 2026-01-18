package org.warriorcats.pawsOfTheForest.skills;

import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

/**
 * Enumeration representing the different skill tree branches in the Warrior Cats system.
 * 
 * <p>Skill branches are organized into four main categories:</p>
 * <ul>
 *   <li><b>Universal Branches:</b> Available to all players (Hunting, Navigation, Resilience, Herbalist)</li>
 *   <li><b>Background Branches:</b> Based on character background (Kittypet, Loner, Rogue, City Cat)</li>
 *   <li><b>Clan Branches:</b> Exclusive to specific clans (Breeze, Echo, Creek, Shade)</li>
 * </ul>
 * 
 * <p>Each branch contains multiple skills with different unlock costs and tier systems.</p>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
public enum SkillBranches {
    /** Universal hunting skills including prey detection and combat abilities */
    HUNTING,
    /** Universal navigation skills for movement and exploration */
    NAVIGATION,  
    /** Universal resilience skills for survival and defense */
    RESILIENCE,
    /** Universal herbalist skills for healing and herb knowledge */
    HERBALIST,
    /** Background branch for house cats with comfort-based skills */
    KITTYPET,
    /** Background branch for independent cats with survival skills */
    LONER,
    /** Background branch for aggressive cats with combat skills */
    ROGUE,
    /** Background branch for urban cats with city-specific skills */
    CITY_CAT,
    /** Clan-specific branch for Breeze Clan with wind and speed skills */
    BREEZE_CLAN,
    /** Clan-specific branch for Echo Clan with forest and stealth skills */
    ECHO_CLAN,
    /** Clan-specific branch for Creek Clan with water-based skills */
    CREEK_CLAN,
    /** Clan-specific branch for Shade Clan with darkness and poison skills */
    SHADE_CLAN;

    /**
     * Returns a human-readable string representation of the skill branch.
     * Converts enum names to title case with spaces.
     *
     * @return formatted skill branch name
     */
    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    /** XP cost required to unlock active skills */
    public static final double UNLOCK_SKILL = 8;
    /** XP cost per tier for passive skills */
    public static final double UNLOCK_SKILL_TIER = 2;

    /**
     * Parses a skill branch from a string representation.
     *
     * @param branchStr the string to parse
     * @return the corresponding SkillBranches enum value
     * @throws IllegalArgumentException if the string doesn't match any branch
     */
    public static SkillBranches from(String branchStr) {
        return EnumsUtils.from(branchStr, SkillBranches.class);
    }
}
