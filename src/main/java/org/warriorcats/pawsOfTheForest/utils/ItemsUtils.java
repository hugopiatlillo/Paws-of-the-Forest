package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.skills.menus.MenuSkillTreePath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Utility class for item-related operations and game mechanics.
 * 
 * <p>This comprehensive utility class provides functionality for working with
 * Minecraft items, including item categorization, metadata handling, skill
 * system integration, and loot generation. It contains constants for various
 * item categories and methods for item validation, serialization, and
 * game-specific checks.</p>
 * 
 * <p>Key features include:</p>
 * <ul>
 *   <li>Item categorization (urban blocks, trash, prey, herbs, etc.)</li>
 *   <li>Active skill item management and cooldown handling</li>
 *   <li>Item serialization and deserialization</li>
 *   <li>Loot generation for various game activities</li>
 *   <li>Toxicity and food safety checks</li>
 *   <li>Persistent data container integration</li>
 * </ul>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ItemsUtils {

    /**
     * NamespacedKey for storing primary cooldown data in item metadata.
     */
    public static final NamespacedKey META_COOLDOWN_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "cooldown");
    /**
     * NamespacedKey for storing secondary cooldown data in item metadata.
     */
    public static final NamespacedKey META_COOLDOWN_SECONDARY_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "cooldown_secondary");
    /**
     * NamespacedKey for marking items as bad prey in item metadata.
     */
    public static final NamespacedKey META_BAD_PREY_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "bad_prey");

    /**
     * Set of materials considered urban/artificial blocks.
     * Used for determining if a location is in an urban environment.
     */
    public static final Set<Material> URBAN_BLOCKS = Set.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.STONE_BRICKS,
            Material.MOSSY_STONE_BRICKS,
            Material.CRACKED_STONE_BRICKS,
            Material.CHISELED_STONE_BRICKS,
            Material.ANDESITE,
            Material.POLISHED_ANDESITE,
            Material.DIORITE,
            Material.POLISHED_DIORITE,
            Material.GRANITE,
            Material.POLISHED_GRANITE,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE,
            Material.POLISHED_DEEPSLATE,
            Material.DEEPSLATE_BRICKS,
            Material.DEEPSLATE_TILES,
            Material.GRAVEL,
            Material.SMOOTH_STONE,

            Material.WHITE_CONCRETE,
            Material.ORANGE_CONCRETE,
            Material.MAGENTA_CONCRETE,
            Material.LIGHT_BLUE_CONCRETE,
            Material.YELLOW_CONCRETE,
            Material.LIME_CONCRETE,
            Material.PINK_CONCRETE,
            Material.GRAY_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE,
            Material.CYAN_CONCRETE,
            Material.PURPLE_CONCRETE,
            Material.BLUE_CONCRETE,
            Material.BROWN_CONCRETE,
            Material.GREEN_CONCRETE,
            Material.RED_CONCRETE,
            Material.BLACK_CONCRETE,

            Material.WHITE_CONCRETE_POWDER,
            Material.ORANGE_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.YELLOW_CONCRETE_POWDER,
            Material.LIME_CONCRETE_POWDER,
            Material.PINK_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.CYAN_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER,
            Material.BROWN_CONCRETE_POWDER,
            Material.GREEN_CONCRETE_POWDER,
            Material.RED_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER
    );

    /**
     * Set of materials considered trash blocks where items can be scavenged.
     */
    public static final Set<Material> TRASH_BLOCKS = Set.of(
            Material.COARSE_DIRT,
            Material.PODZOL,
            Material.GRAVEL,
            Material.MUD,
            Material.COMPOSTER
    );

    /**
     * List of common loot materials that can be found through scavenging.
     */
    public static final List<Material> COMMON_LOOTS = List.of(
            Material.IRON_NUGGET,
            Material.GOLD_NUGGET,
            Material.COPPER_INGOT,
            Material.COAL,
            Material.FLINT,
            Material.BONE,

            Material.STRING,
            Material.LEATHER,
            Material.FEATHER,
            Material.GUNPOWDER,

            Material.BREAD,
            Material.APPLE,
            Material.CARROT,
            Material.POTATO,

            Material.EMERALD,
            Material.LAPIS_LAZULI,
            Material.REDSTONE,
            Material.QUARTZ
    );

    /**
     * List of materials that can be obtained from rat hunting.
     */
    public static final List<Material> RAT_LOOTS = List.of(
            Material.ROTTEN_FLESH,
            Material.BONE,
            Material.LEATHER,
            Material.STRING
    );

    /**
     * List of materials that can be obtained from fishing activities.
     */
    public static final List<Material> FISH_LOOTS = List.of(
            Material.COD,
            Material.SALMON,
            Material.PUFFERFISH,
            Material.TROPICAL_FISH,
            Material.KELP,
            Material.SEAGRASS
    );

    /**
     * Checks if an item is raw prey that can be consumed by cats.
     * 
     * @param item the item to check
     * @return true if the item is raw prey, false otherwise
     */
    public static boolean isRawPrey(ItemStack item) {
        return switch (item.getType()) {
            case COD, SALMON, TROPICAL_FISH, PUFFERFISH, RABBIT, CHICKEN, MUTTON, PORKCHOP, BEEF -> true;
            default -> false;
        };
    }

    /**
     * Checks if an item is bad prey that should not be consumed.
     * 
     * <p>Bad prey includes rotten flesh, raw prey, or items specifically
     * marked as bad prey using persistent data.</p>
     * 
     * @param item the item to check
     * @return true if the item is bad prey, false otherwise
     */
    public static boolean isBadPrey(ItemStack item) {
        return item.getType() == Material.ROTTEN_FLESH || isRawPrey(item) || isMarkedBadPrey(item);
    }

    /**
     * Checks if an item is a cursed herb with negative effects.
     * 
     * @param item the item to check
     * @return true if the item is a cursed herb, false otherwise
     */
    public static boolean isCursedHerb(ItemStack item) {
        return switch (item.getType()) {
            case WITHER_ROSE, DEAD_BUSH, CHORUS_FLOWER,
                 WARPED_ROOTS, CRIMSON_ROOTS,
                 SPORE_BLOSSOM, TWISTING_VINES,
                 NETHER_SPROUTS, NETHER_WART,
                 WEEPING_VINES, RED_MUSHROOM_BLOCK,
                 BROWN_MUSHROOM_BLOCK, MANGROVE_ROOTS -> true;
            default -> false;
        };
    }

    /**
     * Checks if an item is a toxic herb that can cause harm.
     * 
     * @param item the item to check
     * @return true if the item is a toxic herb, false otherwise
     */
    public static boolean isToxicHerb(ItemStack item) {
        return switch (item.getType()) {
            case DEAD_BUSH, WITHER_ROSE, FERN, LARGE_FERN,
                 WARPED_ROOTS, CRIMSON_ROOTS, NETHER_SPROUTS,
                 SPORE_BLOSSOM, AZALEA_LEAVES, SMALL_DRIPLEAF -> true;
            default -> false;
        };
    }

    /**
     * Checks if an item is toxic and harmful to consume.
     * 
     * @param item the item to check
     * @return true if the item is toxic, false otherwise
     */
    public static boolean isToxicItem(ItemStack item) {
        return switch (item.getType()) {
            case POISONOUS_POTATO,
                 RED_MUSHROOM, BROWN_MUSHROOM,
                 PUFFERFISH,
                 SPIDER_EYE, FERMENTED_SPIDER_EYE,
                 SUSPICIOUS_STEW,
                 DRAGON_BREATH -> true;
            default -> false;
        };
    }

    /**
     * Checks if an item is specifically marked as bad prey using metadata.
     * 
     * @param item the item to check
     * @return true if the item is marked as bad prey, false otherwise
     */
    public static boolean isMarkedBadPrey(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(META_BAD_PREY_KEY, PersistentDataType.BYTE);
    }

    /**
     * Marks an item as bad prey using persistent metadata.
     * 
     * @param item the item to mark as bad prey
     */
    public static void markAsBadPrey(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(META_BAD_PREY_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
    }

    /**
     * Checks if an item can be consumed as a drink.
     * 
     * @param item the item to check
     * @return true if the item is drinkable, false otherwise
     */
    public static boolean isDrinkable(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        Material type = item.getType();

        return switch (type) {
            case POTION, MILK_BUCKET, MUSHROOM_STEW, RABBIT_STEW, BEETROOT_SOUP -> true;
            default -> false;
        };
    }

    /**
     * Checks if an item represents a specific active skill for a player.
     * 
     * @param player the player to check
     * @param item the item to check
     * @param skill the specific skill to check for
     * @return true if the item is the specified active skill, false otherwise
     */
    public static boolean isActiveSkill(Player player, ItemStack item, Skills skill) {
        return MenuSkillTreePath
                .generateActiveSkillsFor(EventsCore.PLAYERS_CACHE.get(player.getUniqueId()), skill.getBranch())
                .values().stream()
                .anyMatch(activeSkill -> isSameItem(activeSkill, item));
    }

    /**
     * Checks if an item represents any active skill for a player.
     * 
     * @param player the player to check
     * @param item the item to check
     * @return true if the item is any active skill, false otherwise
     */
    public static boolean isActiveSkill(Player player, ItemStack item) {
        for (Skills skill : Skills.getActiveSkills()) {
            if (isActiveSkill(player, item, skill)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the active skill item for a specific skill and player.
     * 
     * @param player the player to get the skill for
     * @param skill the skill to get the item for
     * @return the ItemStack representing the active skill
     * @throws IllegalArgumentException if the skill is not found for the player
     */
    public static ItemStack getActiveSkill(Player player, Skills skill) {
        for (SkillBranches branch : SkillBranches.values()) {
            Optional<ItemStack> activeSkillOpt = MenuSkillTreePath
                    .generateActiveSkillsFor(EventsCore.PLAYERS_CACHE.get(player.getUniqueId()), branch)
                    .entrySet().stream()
                    .filter(e -> MenuSkillTreePath.getSkillByIndex(e.getKey(), branch) == skill)
                    .map(Map.Entry::getValue)
                    .findAny();
            if (activeSkillOpt.isPresent()) {
                return activeSkillOpt.get();
            }
        }
        throw new IllegalArgumentException("Could not find active skill for player and skill : " + player.getName() + ", " + skill);
    }

    /**
     * Checks if a skill item is off cooldown and ready to use.
     * 
     * @param player the player using the skill
     * @param item the skill item to check
     * @return true if the skill is off cooldown, false if still cooling down
     */
    public static boolean checkForCooldown(Player player, ItemStack item) {
        return checkForCooldown(player, item, META_COOLDOWN_KEY);
    }

    /**
     * Checks if a skill item is off cooldown using a specific metadata key.
     * 
     * @param player the player using the skill
     * @param item the skill item to check
     * @param key the metadata key to check for cooldown data
     * @return true if the skill is off cooldown, false if still cooling down
     */
    public static boolean checkForCooldown(Player player, ItemStack item, NamespacedKey key) {
        return getCooldown(player, item, key) == 0;
    }

    /**
     * Gets the remaining cooldown time in seconds for a skill item.
     * 
     * @param player the player using the skill
     * @param item the skill item to check
     * @return the remaining cooldown time in seconds, or 0 if ready to use
     */
    public static long getCooldown(Player player, ItemStack item) {
        return getCooldown(player, item, META_COOLDOWN_KEY);
    }

    /**
     * Gets the remaining cooldown time using a specific metadata key.
     * 
     * @param player the player using the skill
     * @param item the skill item to check
     * @param key the metadata key to check for cooldown data
     * @return the remaining cooldown time in seconds, or 0 if ready to use
     * @throws IllegalArgumentException if the item is not an active skill
     */
    public static long getCooldown(Player player, ItemStack item, NamespacedKey key) {
        if (!isActiveSkill(player, item)) {
            throw new IllegalArgumentException("Item is not an active skill");
        }
        ItemMeta meta = item.getItemMeta();
        long nextTime = meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.LONG, 0L);
        long now = System.currentTimeMillis();
        long remainingMillis = nextTime - now;
        // Convert milliseconds to seconds, ensuring non-negative result
        return Math.max(0, remainingMillis / 1000);
    }

    /**
     * Sets a cooldown on a skill item.
     * 
     * @param player the player using the skill
     * @param item the skill item to set cooldown on
     * @param cooldown the cooldown duration in seconds
     */
    public static void setCooldown(Player player, ItemStack item, long cooldown) {
        setCooldown(player, item, cooldown, META_COOLDOWN_KEY);
    }

    /**
     * Sets a cooldown on a skill item using a specific metadata key.
     * 
     * @param player the player using the skill
     * @param item the skill item to set cooldown on
     * @param cooldown the cooldown duration in seconds
     * @param key the metadata key to store the cooldown data
     * @throws IllegalArgumentException if the item is not an active skill
     */
    public static void setCooldown(Player player, ItemStack item, long cooldown, NamespacedKey key) {
        if (!isActiveSkill(player, item)) {
            throw new IllegalArgumentException("Item is not an active skill");
        }
        ItemMeta meta = item.getItemMeta();
        // Calculate when the cooldown will expire (current time + cooldown in milliseconds)
        long nextAvailableTime = System.currentTimeMillis() + (cooldown * 1000);
        meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, nextAvailableTime);
        item.setItemMeta(meta);
    }

    /**
     * Checks if an ItemStack is null or empty.
     * 
     * @param item the item to check
     * @return true if the item is null or empty, false otherwise
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.isEmpty();
    }

    /**
     * Checks if a material is considered an urban/artificial block.
     * 
     * @param material the material to check
     * @return true if the material is urban, false otherwise
     * @see #URBAN_BLOCKS
     */
    public static boolean isUrbanBlock(Material material) {
        return URBAN_BLOCKS.contains(material);
    }

    /**
     * Checks if a material is considered a trash block for scavenging.
     * 
     * @param material the material to check
     * @return true if the material is a trash block, false otherwise
     * @see #TRASH_BLOCKS
     */
    public static boolean isTrashBlock(Material material) {
        return TRASH_BLOCKS.contains(material);
    }

    /**
     * Gets a list of all edible materials in Minecraft.
     * 
     * @return a list of all food materials
     */
    public static List<Material> getAllFoods() {
        return Arrays.stream(Material.values())
                .filter(Material::isEdible)
                .collect(Collectors.toList());
    }

    /**
     * Generates a random loot item from trash scavenging.
     * 
     * @return a random ItemStack from the common loot table
     * @see #COMMON_LOOTS
     */
    public static ItemStack getRandomLootFromTrash() {
        final Random random = new Random();
        // Select random loot from common loot table
        Material loot = ItemsUtils.COMMON_LOOTS.get(random.nextInt(ItemsUtils.COMMON_LOOTS.size()));

        return new ItemStack(loot, 1);
    }

    /**
     * Generates a random loot item from fishing activities.
     * 
     * @return a random ItemStack from the fish loot table with random quantity (1-3)
     * @see #FISH_LOOTS
     */
    public static ItemStack getRandomLootFromFish() {
        final Random random = new Random();
        // Select random fish loot
        Material loot = FISH_LOOTS.get(random.nextInt(FISH_LOOTS.size()));
        // Return 1-3 items randomly
        return new ItemStack(loot, 1 + random.nextInt(3));
    }

    /**
     * Serializes an array of ItemStacks to a byte array for storage.
     * 
     * <p>This method uses Bukkit's serialization system to convert ItemStack
     * arrays into byte arrays that can be stored in databases or files.</p>
     * 
     * @param items the ItemStack array to serialize
     * @return the serialized byte array, or empty array if serialization fails
     */
    public static byte[] serializeItemStackArray(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not serialize item stacks array", e);
        }
        return new byte[0];
    }

    /**
     * Deserializes a byte array back into an ItemStack array.
     * 
     * <p>This method reverses the serialization process, converting stored
     * byte data back into usable ItemStack arrays.</p>
     * 
     * @param data the byte array to deserialize
     * @return the deserialized ItemStack array, or empty array if deserialization fails
     */
    public static ItemStack[] deserializeItemStackArray(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not deserialize item stacks array", e);
        }
        return new ItemStack[0];
    }

    /**
     * Checks if two ItemStacks are the same item, ignoring NBT data.
     * 
     * <p>This method compares items based on their type, display name, and lore,
     * but ignores persistent data and other NBT tags. This is useful for
     * comparing skill items or checking item similarity without metadata.</p>
     * 
     * @param a the first ItemStack to compare
     * @param b the second ItemStack to compare
     * @return true if the items are the same (ignoring NBT), false otherwise
     */
    public static boolean isSameItem(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.getType() != b.getType()) return false;

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        if (metaA == null || metaB == null) return false;

        if (!Objects.equals(metaA.getDisplayName(), metaB.getDisplayName())) return false;

        List<String> loreA = metaA.getLore();
        List<String> loreB = metaB.getLore();
        if (loreA == null && loreB == null) return true;
        if (loreA == null || loreB == null) return false;

        return loreA.equals(loreB);
    }
}
