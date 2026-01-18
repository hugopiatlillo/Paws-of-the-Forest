package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.warriorcats.pawsOfTheForest.preys.Prey;

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration class for managing prey entities and their properties in the hunting system.
 * 
 * This class extends BaseConfiguration and handles the loading and management of prey
 * configurations from the "preys_config.yaml" file. It processes YAML configuration data
 * to create Prey objects with various attributes such as experience rewards, coin values,
 * behavior characteristics, and environmental preferences.
 * 
 * The configuration supports different prey types with customizable properties:
 * - Experience points (XP) earned when hunting the prey
 * - Coin rewards for successful hunts
 * - Flee duration for escape mechanics
 * - Environmental preferences (higher altitude, aquatic, etc.)
 * - Quality indicators (bad/good prey classification)
 * 
 * Example configuration structure:
 * <pre>
 * prey:
 *   rabbit:
 *     xp: 15.0
 *     coins: 3
 *     flee_duration_seconds: 2.5
 *     higher: false
 *     aquatic: false
 *     bad: false
 * </pre>
 * 
 * @author Warrior Cats Plugin Team
 * @since 1.0.0
 */
public abstract class PreysConf extends BaseConfiguration {

    /**
     * The filename of the prey configuration file.
     * This YAML file contains all prey definitions and their properties.
     */
    public static final String CONFIG_FILE_NAME = "preys_config.yaml";

    /**
     * Loads the prey configuration from the specified YAML file and populates the prey registry.
     * 
     * This method extends the base loading functionality to specifically handle prey data.
     * After loading the YAML source, it processes the "prey" configuration section to create
     * Prey objects with the following properties:
     * 
     * - name: The prey identifier (converted to uppercase)
     * - xp: Experience points rewarded for hunting this prey
     * - coins: Coin reward for successful hunts
     * - flee_duration_seconds: How long the prey attempts to flee (in seconds)
     * - higher: Whether this prey prefers higher altitude areas
     * - aquatic: Whether this prey is found in or near water
     * - bad: Whether this prey is considered low-quality or diseased
     * 
     * All created Prey objects are automatically added to the static PREYS collection
     * for runtime access by the hunting system.
     * 
     * @param configFileName The name of the configuration file to load
     * @throws IllegalStateException if the "prey" configuration section is missing
     * @throws NumberFormatException if numeric values cannot be parsed
     */
    @Override
    public void load(String configFileName) {
        super.load(configFileName);
        ConfigurationSection preysSource = yamlSource.getConfigurationSection("prey");
        // Clear existing preys to avoid duplicates on reload
        Preys.PREYS.clear();
        
        for (var entry : preysSource.getKeys(false)) {
            Prey prey = new Prey(
                    entry.toUpperCase(), // Normalize prey names to uppercase
                    preysSource.getDouble(entry + ".xp"),
                    preysSource.getLong(entry + ".coins"),
                    (float) preysSource.getDouble(entry + ".flee_duration_seconds"),
                    preysSource.getBoolean(entry + ".higher"),
                    preysSource.getBoolean(entry + ".aquatic"),
                    preysSource.getBoolean(entry + ".bad")
            );
            Preys.PREYS.add(prey);
        }
    }

    /**
     * Static container class for accessing prey data at runtime.
     * 
     * This class provides a centralized registry of all loaded prey configurations.
     * The PREYS set is populated during configuration loading and contains all
     * available prey types that can be spawned and hunted in the game.
     * 
     * Usage examples:
     * <pre>
     * // Find a specific prey by name
     * Optional&lt;Prey&gt; rabbit = Preys.PREYS.stream()
     *     .filter(prey -&gt; prey.getName().equals("RABBIT"))
     *     .findFirst();
     * 
     * // Get all aquatic prey
     * List&lt;Prey&gt; aquaticPrey = Preys.PREYS.stream()
     *     .filter(Prey::isAquatic)
     *     .collect(Collectors.toList());
     * </pre>
     */
    public static class Preys {
        /**
         * A set containing all loaded prey configurations.
         * This collection is populated when the configuration is loaded and provides
         * runtime access to prey data for the hunting system.
         * 
         * The set uses HashSet for efficient lookup operations and prevents duplicate
         * prey entries based on the Prey class's equality implementation.
         */
        public static final Set<Prey> PREYS = new HashSet<>();
    }
}
