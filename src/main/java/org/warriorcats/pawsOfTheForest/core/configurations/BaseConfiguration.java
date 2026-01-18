package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;

import java.util.Properties;

/**
 * Base configuration class that provides core functionality for loading and managing
 * configuration files in both Properties and YAML formats.
 * 
 * This class implements the Singleton pattern and serves as the foundation for all
 * configuration management in the Paws of the Forest plugin. It automatically detects
 * file format based on file extension and provides unified access methods for both
 * Properties (.properties) and YAML (.yml/.yaml) configuration files.
 * 
 * The class handles:
 * - Automatic file format detection and loading
 * - Default value management with automatic file updates
 * - Type-safe property retrieval with ChatColor support
 * - Singleton instance management
 * 
 * @author Warrior Cats Plugin Team
 * @since 1.0.0
 */
public class BaseConfiguration {

    /**
     * Singleton instance of the BaseConfiguration.
     * Ensures only one configuration manager exists per application lifecycle.
     */
    private static BaseConfiguration INSTANCE = null;

    /**
     * Properties object for managing .properties configuration files.
     * Used when the configuration file has a .properties extension.
     */
    protected Properties propertiesSource = new Properties();

    /**
     * YAML configuration object for managing .yml/.yaml configuration files.
     * Used when the configuration file has a .yml or .yaml extension.
     */
    protected YamlConfiguration yamlSource = new YamlConfiguration();

    /**
     * Protected constructor to enforce singleton pattern.
     * Automatically sets this instance as the global singleton instance.
     */
    protected BaseConfiguration() {
        INSTANCE = this;
    }

    /**
     * Gets the singleton instance of BaseConfiguration.
     * Creates a new instance if none exists.
     * 
     * @return The singleton BaseConfiguration instance
     */
    public static BaseConfiguration getInstance() {
        if (INSTANCE == null) {
            new BaseConfiguration();
        }
        return INSTANCE;
    }

    /**
     * Loads a configuration file, automatically detecting the format based on file extension.
     * Supports both Properties (.properties) and YAML (.yml/.yaml) formats.
     * 
     * @param configFilePath The path to the configuration file to load
     */
    public void load(String configFilePath) {
        if (FileUtils.isYaml(configFilePath)) {
            loadYamlSource(configFilePath);
        } else {
            loadPropertiesSource(configFilePath);
        }
    }

    /**
     * Loads a Properties configuration file into the propertiesSource.
     * 
     * @param configFilePath The path to the .properties file to load
     */
    protected void loadPropertiesSource(String configFilePath) {
        propertiesSource = FileUtils.load(configFilePath, propertiesSource);
    }

    /**
     * Loads a YAML configuration file into the yamlSource.
     * 
     * @param configFilePath The path to the .yml/.yaml file to load
     */
    protected void loadYamlSource(String configFilePath) {
        yamlSource = FileUtils.load(configFilePath, yamlSource);
    }

    /**
     * Checks if a configuration key exists, and if not, adds it with the default value.
     * Automatically saves the configuration file when a default value is added.
     * 
     * This method ensures that all required configuration keys exist in the file,
     * preventing runtime errors and providing sensible defaults for new installations.
     * 
     * @param key The configuration key to check
     * @param defaultValue The default value to set if the key doesn't exist
     * @param configFilePath The path to the configuration file
     * @return true if the key already existed, false if it was added with default value
     */
    protected static boolean checkForDefaultKey(String key, String defaultValue, String configFilePath) {
        if (FileUtils.isYaml(configFilePath)) {
            if (!getInstance().yamlSource.contains(key)) {
                getInstance().yamlSource.set(key, defaultValue);
                FileUtils.store(configFilePath, getInstance().yamlSource);
                return false;
            }
        } else {
            if (!getInstance().propertiesSource.containsKey(key)) {
                getInstance().propertiesSource.setProperty(key, defaultValue);
                FileUtils.store(configFilePath, getInstance().propertiesSource);
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves a string property value from the configuration, or returns the default value
     * if the key doesn't exist. Automatically adds missing keys to the configuration file.
     * 
     * @param key The configuration key to retrieve
     * @param defaultValue The default value to return and store if key doesn't exist
     * @param configFilePath The path to the configuration file
     * @return The property value from the configuration, or the default value
     */
    protected static String getPropertyOrDefault(String key, String defaultValue, String configFilePath) {
        if (!checkForDefaultKey(key, defaultValue, configFilePath)) {
            return defaultValue;
        }
        return !FileUtils.isYaml(configFilePath) ? getInstance().propertiesSource.getProperty(key) : (String) getInstance().yamlSource.get(key);
    }

    /**
     * Retrieves a ChatColor property value from the configuration, or returns the default value
     * if the key doesn't exist. Automatically adds missing keys to the configuration file.
     * 
     * The method handles case-insensitive ChatColor name matching and validates that the
     * stored value corresponds to a valid ChatColor enum value.
     * 
     * @param key The configuration key to retrieve
     * @param defaultValue The default ChatColor to return and store if key doesn't exist
     * @param configFilePath The path to the configuration file
     * @return The ChatColor value from the configuration, or the default value
     * @throws IllegalArgumentException if the stored value is not a valid ChatColor name
     */
    protected static ChatColor getPropertyOrDefault(String key, ChatColor defaultValue, String configFilePath) {
        String value = defaultValue.name();
        if (!checkForDefaultKey(key, value, configFilePath)) {
            return defaultValue;
        }
        return ChatColor.valueOf(!FileUtils.isYaml(configFilePath) ?
                getInstance().propertiesSource.getProperty(key).toUpperCase() :
                ((String) getInstance().yamlSource.get(key)).toUpperCase());
    }
}
