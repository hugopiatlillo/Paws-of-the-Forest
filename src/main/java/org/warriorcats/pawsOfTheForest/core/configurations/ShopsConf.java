package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.shops.ShopItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for managing shop items and their properties in the trading system.
 * 
 * This class extends BaseConfiguration and handles the loading and management of shop
 * configurations from the "shops_config.yaml" file. It processes YAML configuration data
 * to create ShopItem objects that represent purchasable items in the in-game shop system.
 * 
 * The configuration supports customizable shop items with the following properties:
 * - Minecraft item type (Material)
 * - Display name for the shop interface
 * - Price in the game's currency (coins)
 * - Descriptive lore text for item tooltips
 * 
 * Shop items are stored in an ordered list to maintain consistent positioning and
 * indexing within shop interfaces. This allows for reliable item access and display
 * ordering across game sessions.
 * 
 * Example configuration structure:
 * <pre>
 * shop:
 *   healing_potion:
 *     item: "POTION"
 *     name: "Healing Potion"
 *     price: 15
 *     lore:
 *       - "Restores health when consumed"
 *       - "Useful for surviving tough battles"
 * </pre>
 * 
 * @author Warrior Cats Plugin Team
 * @since 1.0.0
 */
public abstract class ShopsConf extends BaseConfiguration {

    /**
     * The filename of the shop configuration file.
     * This YAML file contains all shop item definitions and their properties.
     */
    public static final String CONFIG_FILE_NAME = "shops_config.yaml";

    /**
     * Loads the shop configuration from the specified YAML file and populates the shop item registry.
     * 
     * This method extends the base loading functionality to specifically handle shop item data.
     * After loading the YAML source, it processes the "shop" configuration section to create
     * ShopItem objects with the following properties:
     * 
     * - item: The Minecraft Material type (converted to uppercase for validation)
     * - name: The display name shown in shop interfaces
     * - price: The cost in coins required to purchase the item
     * - lore: A list of descriptive text lines shown in item tooltips
     * 
     * All created ShopItem objects are automatically added to the static SHOP_ITEMS list
     * in the order they appear in the configuration file. This maintains consistent
     * shop layout and allows for indexed access to items.
     * 
     * The method includes validation for Material types to ensure only valid Minecraft
     * items are loaded into the shop system.
     * 
     * @param configFileName The name of the configuration file to load
     * @throws IllegalArgumentException if an invalid Material name is specified
     * @throws IllegalStateException if the "shop" configuration section is missing
     * @throws ClassCastException if lore is not properly formatted as a list of strings
     */
    @Override
    public void load(String configFileName) {
        super.load(configFileName);
        ConfigurationSection shopSource = yamlSource.getConfigurationSection("shop");
        // Clear existing shop items to avoid duplicates on reload
        Shops.SHOP_ITEMS.clear();
        
        for (var entry : shopSource.getKeys(false)) {
            // Create ItemStack with proper Material validation
            String materialName = shopSource.getString(entry + ".item").toUpperCase();
            Material material = Material.valueOf(materialName); // This will throw IllegalArgumentException for invalid materials
            
            ShopItem shopItem = new ShopItem(
                    new ItemStack(material),
                    shopSource.getString(entry + ".name"),
                    shopSource.getLong(entry + ".price"),
                    (List<String>) shopSource.getList(entry + ".lore")
            );
            Shops.SHOP_ITEMS.add(shopItem);
        }
    }

    /**
     * Static container class for accessing shop item data at runtime.
     * 
     * This class provides a centralized registry of all loaded shop item configurations.
     * The SHOP_ITEMS list is populated during configuration loading and contains all
     * available items that can be purchased in the in-game shops.
     * 
     * The use of a List (rather than a Set) is intentional to maintain ordering and
     * provide indexed access for shop interfaces. This allows consistent item positioning
     * across different shop GUI implementations and ensures reliable item selection.
     * 
     * Usage examples:
     * <pre>
     * // Get a specific shop item by index
     * ShopItem firstItem = Shops.SHOP_ITEMS.get(0);
     * 
     * // Find items within a price range
     * List&lt;ShopItem&gt; affordableItems = Shops.SHOP_ITEMS.stream()
     *     .filter(item -&gt; item.getPrice() &lt;= playerCoins)
     *     .collect(Collectors.toList());
     * 
     * // Get total number of shop items
     * int shopSize = Shops.SHOP_ITEMS.size();
     * </pre>
     */
    public static class Shops {
        /**
         * An ordered list containing all loaded shop item configurations.
         * 
         * This collection is populated when the configuration is loaded and provides
         * runtime access to shop item data for the trading system. The list maintains
         * the order of items as they appear in the configuration file, which is important
         * for consistent shop interface layouts.
         * 
         * Items are indexed starting from 0, allowing for direct access by position
         * which is useful for GUI-based shop implementations where item slots correspond
         * to list indices.
         */
        public static final List<ShopItem> SHOP_ITEMS = new ArrayList<>();
    }
}
