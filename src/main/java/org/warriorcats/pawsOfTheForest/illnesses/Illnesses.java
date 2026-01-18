package org.warriorcats.pawsOfTheForest.illnesses;

import lombok.Getter;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

import java.util.Map;

/**
 * Enumeration of all illnesses that can affect players in the Warrior Cats system.
 * 
 * Each illness has specific properties including:
 * - Whether it can be fatal if left untreated
 * - Time until the illness worsens (in Minecraft days)
 * - Potion effects applied to the affected player
 * 
 * Illnesses range from minor conditions like external parasites to life-threatening
 * diseases like rabies. Some illnesses are progressive and will worsen over time
 * if not treated with appropriate herbs.
 * 
 * The potion effects simulate the symptoms and impact of each illness on gameplay,
 * affecting movement, vision, strength, or causing additional status effects.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
@Getter
public enum Illnesses {
    /** Respiratory illness affecting breathing and stamina (non-fatal, worsens after 5 days) */
    UPPER_RESPIRATORY_INFECTION(false, 5, Map.of(
            PotionEffectType.SLOWNESS, 0,
            PotionEffectType.WEAKNESS, 0,
            PotionEffectType.HUNGER, 0,
            PotionEffectType.NAUSEA, 0
    )),

    /** Deadly viral infection causing erratic behavior (fatal, worsens after 2 days) */
    RABIES(true, 2, Map.of(
            PotionEffectType.NAUSEA, 2,
            PotionEffectType.BLINDNESS, 0,
            PotionEffectType.JUMP_BOOST, 1
    )),

    /** Parasitic worms causing malnutrition (fatal, worsens after 7 days) */
    INTERNAL_PARASITES(true, 7, Map.of(
            PotionEffectType.HUNGER, 1,
            PotionEffectType.SLOWNESS, 0
    )),

    /** Fleas and ticks causing discomfort (non-fatal, no progression) */
    EXTERNAL_PARASITES(false, 0, Map.of(
            PotionEffectType.SLOWNESS, 0,
            PotionEffectType.HUNGER, 0,
            PotionEffectType.NAUSEA, 0
    )),

    /** Cold-induced tissue damage (fatal, worsens after 2 days) */
    FROSTBITE(true, 2, Map.of(
            PotionEffectType.SLOWNESS, 1,
            PotionEffectType.WITHER, 0,
            PotionEffectType.BLINDNESS, 0
    )),

    /** Heat-induced exhaustion and dehydration (fatal, worsens after 2 days) */
    HEATSTROKE(true, 2, Map.of(
            PotionEffectType.NAUSEA, 1,
            PotionEffectType.BLINDNESS, 0,
            PotionEffectType.WEAKNESS, 0
    )),

    /** Open wounds from combat or accidents (non-fatal, progresses to infection after 1 day) */
    WOUNDS(false, 1, Map.of(
            PotionEffectType.WEAKNESS, 0
    )),

    /** Bacterial infection from untreated wounds (fatal, worsens after 3 days) */
    INFECTED_WOUNDS(true, 3, Map.of(
            PotionEffectType.WITHER, 0,
            PotionEffectType.WEAKNESS, 1
    )),

    /** Fractured bones causing mobility issues (non-fatal, no progression) */
    BROKEN_BONES(false, 0, Map.of(
            PotionEffectType.SLOWNESS, 3,
            PotionEffectType.NAUSEA, 0
    )),

    /** Toxic substance ingestion (fatal, worsens after 1 day) */
    POISONING(true, 1, Map.of(
            PotionEffectType.POISON, 1,
            PotionEffectType.NAUSEA, 0,
            PotionEffectType.HUNGER, 0,
            PotionEffectType.WITHER, 0
    )),

    /** Neurological condition causing spasms (non-fatal, no progression) */
    SEIZURES(false, 0, Map.of(
            PotionEffectType.NAUSEA, 0,
            PotionEffectType.BLINDNESS, 0,
            PotionEffectType.LEVITATION, 0
    )),

    /** Joint inflammation causing chronic pain (non-fatal, no progression) */
    ARTHRITIS(false, 0, Map.of(
            PotionEffectType.SLOWNESS, 0
    ));

    /** Whether this illness can cause death if left untreated */
    private final boolean fatal;
    /** Number of Minecraft days before the illness worsens (0 = no progression) */
    private final int minecraftDaysBeforeWorsened;
    /** Map of potion effects and their amplifier levels applied by this illness */
    private final Map<PotionEffectType, Integer> potionEffects;

    /**
     * Constructor for illness enum values.
     * 
     * @param fatal Whether this illness can be fatal
     * @param minecraftDaysBeforeWorsened Days until illness progression (0 for no progression)
     * @param potionEffects Map of potion effects and their amplifier levels
     */
    Illnesses(boolean fatal, int minecraftDaysBeforeWorsened, Map<PotionEffectType, Integer> potionEffects) {
        this.fatal = fatal;
        this.minecraftDaysBeforeWorsened = minecraftDaysBeforeWorsened;
        this.potionEffects = potionEffects;
    }

    /**
     * Returns the display name of the illness with proper formatting.
     * 
     * @return The formatted illness name (e.g., "Upper Respiratory Infection")
     */
    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    /**
     * Parses an illness from its string representation.
     * 
     * @param illnessStr The string representation of the illness
     * @return The corresponding Illnesses enum value
     * @throws IllegalArgumentException if the string doesn't match any illness
     */
    public static Illnesses from(String illnessStr) {
        return EnumsUtils.from(illnessStr, Illnesses.class);
    }
}
