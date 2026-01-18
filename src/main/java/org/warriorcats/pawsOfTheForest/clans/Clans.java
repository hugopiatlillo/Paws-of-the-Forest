package org.warriorcats.pawsOfTheForest.clans;

import lombok.Getter;
import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

/**
 * Enumeration of all available clans in the Warrior Cats roleplay system.
 * 
 * Each clan has:
 * - A unique color code for chat and UI display
 * - Distinct territorial characteristics and abilities
 * - Specialized skill trees available only to clan members
 * 
 * The four clans are:
 * - BREEZE: Fast and agile cats of the open moors (light yellow)
 * - ECHO: Strong forest dwellers with thick pelts (purple)
 * - CREEK: Skilled swimmers who live near water (teal)
 * - SHADE: Mysterious night hunters with toxic abilities (dark red)
 * 
 * Players can join clans to access clan-specific skills and participate
 * in clan-based roleplay and territorial conflicts.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
@Getter
public enum Clans {
    /** Swift cats of the open moors - masters of speed and wind */
    BREEZE("#FFFACD"), 
    /** Strong forest cats - resilient and powerful */
    ECHO("#800080"), 
    /** Water-loving cats - excellent swimmers and fishers */
    CREEK("#008080"), 
    /** Mysterious shadow cats - masters of stealth and toxins */
    SHADE("#B3002C");

    /** The hex color code used for clan identification in chat and UI */
    private final String color;

    /**
     * Constructor for clan enum values.
     * 
     * @param color The hex color code representing this clan
     */
    Clans(String color) {
        this.color = color;
    }

    /**
     * Gets the Bukkit ChatColor representation of this clan's color.
     * 
     * @return The ChatColor string for use in chat messages
     */
    public String getColorCode() {
        return net.md_5.bungee.api.ChatColor.of(this.color).toString();
    }

    /**
     * Parses a clan from its string representation.
     * 
     * @param clanStr The string representation of the clan
     * @return The corresponding Clans enum value
     * @throws IllegalArgumentException if the string doesn't match any clan
     */
    public static Clans from(String clanStr) {
        return EnumsUtils.from(clanStr, Clans.class);
    }

    /**
     * Returns the display name of the clan with proper capitalization.
     * Format: "BreezeClan", "EchoClan", etc.
     * 
     * @return The formatted clan name
     */
    @Override
    public String toString() {
        return StringsUtils.capitalize(name().toLowerCase()) + StringsUtils.capitalize("clan");
    }
}
