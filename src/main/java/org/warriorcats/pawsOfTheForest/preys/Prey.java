package org.warriorcats.pawsOfTheForest.preys;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
import org.bukkit.entity.LivingEntity;
import org.warriorcats.pawsOfTheForest.core.configurations.PreysConf;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.List;
import java.util.Optional;

/**
 * Represents a prey entity in the Warrior Cats game system.
 * This record encapsulates all the properties and behaviors of huntable creatures,
 * including their rewards, difficulty, and environmental preferences.
 * 
 * Preys are categorized into different types (common/higher tier, aquatic/terrestrial, safe/bad to eat)
 * and provide experience points and coins when successfully hunted by players.
 * 
 * @param entityType The Bukkit entity type name or custom model name (uppercase)
 * @param xp The experience points awarded when this prey is killed
 * @param coins The coin reward given to the player for hunting this prey
 * @param fleeDurationSeconds How many seconds this prey will flee when detecting a player
 * @param isHigher Whether this is a higher-tier prey (rare, more valuable)
 * @param isAquatic Whether this prey can only spawn in or near water
 * @param isBad Whether consuming this prey is harmful (causes poisoning)
 * 
 * @author Warrior Cats Development Team
 * @since 1.0
 */
public record Prey(String entityType, double xp, long coins, float fleeDurationSeconds, boolean isHigher, boolean isAquatic, boolean isBad) {

    /**
     * Attempts to create a Prey instance from a given LivingEntity.
     * This method checks if the entity is a registered prey type by comparing
     * its entity type (or custom model name) against the configured prey list.
     * 
     * The method first checks for custom ModelEngine models, then falls back
     * to standard Bukkit entity types.
     * 
     * @param entity The LivingEntity to check and convert to a Prey
     * @return An Optional containing the Prey if the entity is a registered prey type,
     *         or Optional.empty() if the entity is not a huntable prey
     * @throws NullPointerException if entity is null
     */
    public static Optional<Prey> fromEntity(LivingEntity entity) {
        String entityType = entity.getType().name().toUpperCase();

        // Check if entity has a custom ModelEngine model
        ModeledEntity modeledEntity = ModelEngine.getModeledEntity(entity);
        if (modeledEntity != null) {
            entityType = MobsUtils.getModelName(modeledEntity).toUpperCase();
        }

        // Search through configured preys for a match
        Optional<Prey> existingPrey = Optional.empty();
        for (Prey prey : PreysConf.Preys.PREYS) {
            if (prey.entityType().equals(entityType)) {
                existingPrey = Optional.of(prey);
                break;
            }
        }

        return existingPrey;
    }

    /**
     * Checks if a given LivingEntity is a huntable prey.
     * This is a convenience method that uses {@link #fromEntity(LivingEntity)}
     * to determine if the entity is registered as a prey type.
     * 
     * @param entity The LivingEntity to check
     * @return true if the entity is a registered prey type, false otherwise
     * @throws NullPointerException if entity is null
     */
    public static boolean isPrey(LivingEntity entity) {
        return fromEntity(entity).isPresent();
    }

    /**
     * Retrieves all currently loaded LivingEntities that are registered as prey types.
     * This method scans all entities in the world and filters them to include
     * only those that match configured prey types.
     * 
     * @return A list of all LivingEntities that are huntable preys
     * @see MobsUtils#getAllEntities()
     */
    public static List<LivingEntity> getAllEntities() {
        return MobsUtils.getAllEntities().stream()
                .filter(Prey::isPrey)
                .toList();
    }

    /**
     * Retrieves all common (non-higher tier) prey types from the configuration.
     * Common preys are the standard huntable creatures that spawn more frequently
     * and typically provide lower rewards than higher-tier preys.
     * 
     * @return A list of all common prey types
     * @see #getAllHighers()
     */
    public static List<Prey> getAllCommons() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> !prey.isHigher)
                .toList();
    }

    /**
     * Retrieves all higher-tier prey types from the configuration.
     * Higher-tier preys are rare, valuable creatures that spawn less frequently
     * but provide greater rewards when successfully hunted.
     * 
     * @return A list of all higher-tier prey types
     * @see #getAllCommons()
     */
    public static List<Prey> getAllHighers() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> prey.isHigher)
                .toList();
    }

    /**
     * Retrieves all prey types that are harmful to consume.
     * Bad preys are creatures that, when eaten, can cause negative effects
     * such as poisoning or other debuffs to the player.
     * 
     * @return A list of all prey types that are dangerous to eat
     * @see #getAllGoodsToEat()
     */
    public static List<Prey> getAllBadsToEat() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> prey.isBad)
                .toList();
    }

    /**
     * Retrieves all prey types that are safe to consume.
     * Good preys are creatures that can be safely eaten without causing
     * any negative effects to the player.
     * 
     * @return A list of all prey types that are safe to eat
     * @see #getAllBadsToEat()
     */
    public static List<Prey> getAllGoodsToEat() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> !prey.isBad)
                .toList();
    }
}
