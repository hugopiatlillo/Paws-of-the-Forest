package org.warriorcats.pawsOfTheForest.utils;

import com.ticxo.modelengine.api.model.ModeledEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.preys.Prey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for mob-related operations and entity management.
 * 
 * <p>This class provides comprehensive functionality for working with living
 * entities, including MythicMobs integration, stealth detection, loot generation,
 * disease mechanics, and entity behavior checks. It serves as a bridge between
 * Bukkit entities and custom game mechanics.</p>
 * 
 * <p>Key features include:</p>
 * <ul>
 *   <li>MythicMobs spawning and management</li>
 *   <li>Stealth and line-of-sight detection</li>
 *   <li>Loot generation for various mob types</li>
 *   <li>Disease and infection mechanics (rabies)</li>
 *   <li>Entity poisoning and immunity checks</li>
 *   <li>Predator classification</li>
 * </ul>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class MobsUtils {

    /**
     * NamespacedKey for storing rabies infection data in entity metadata.
     */
    private static final NamespacedKey RABIES_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "rabies");

    /**
     * Gets all living entities from all loaded worlds.
     * 
     * @return a list of all living entities currently loaded
     */
    public static List<LivingEntity> getAllEntities() {
        List<LivingEntity> entities = new ArrayList<>();
        // Iterate through all loaded worlds
        for (World world : Bukkit.getWorlds()) {
            // Check each entity in the world
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity livingEntity) {
                    entities.add(livingEntity);
                }
            }
        }
        return entities;
    }

    /**
     * Extracts the model name from a ModelEngine entity.
     * 
     * @param modeledEntity the modeled entity to get the name from
     * @return the model name of the entity
     */
    public static String getModelName(ModeledEntity modeledEntity) {
         // Get the first model name from the entity's model map
         return modeledEntity.getModels().entrySet().iterator().next().getKey();
    }

    /**
     * Spawns a MythicMob at the specified location with a given level.
     * 
     * @param location the location to spawn the mob at
     * @param modelName the name of the MythicMob model to spawn
     * @param level the level of the mob to spawn
     * @return the spawned ActiveMob instance
     * @throws IllegalArgumentException if the model name is not found
     */
    public static ActiveMob spawn(Location location, String modelName, double level) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(modelName).orElse(null);

        if (mob == null) {
            throw new IllegalArgumentException("Could not find MythicMob from model name : " + modelName);
        }

        return mob.spawn(BukkitAdapter.adapt(location), level);
    }

    /**
     * Generates a random food item with a random quantity within the specified range.
     * 
     * @param minAmount the minimum number of items to generate
     * @param maxAmount the maximum number of items to generate
     * @return a random food ItemStack with random quantity
     */
    public static ItemStack getRandomDropFood(int minAmount, int maxAmount) {
        final Random random = new Random();
        List<Material> foods = ItemsUtils.getAllFoods();
        Material food = foods.get(random.nextInt(foods.size()));
        int qty = minAmount + random.nextInt(maxAmount - minAmount + 1);
        return new ItemStack(food, qty);
    }

    /**
     * Checks if a player is in stealth mode relative to an entity.
     * 
     * <p>A player is considered stealthed if they are sneaking, invisible,
     * or the entity doesn't have line of sight to them.</p>
     * 
     * @param player the player to check stealth for
     * @param entity the entity to check stealth against
     * @return true if the player is stealthed from the entity, false otherwise
     */
    public static boolean isStealthFrom(Player player, LivingEntity entity) {
        return player.isSneaking() || player.isInvisible() || !entity.hasLineOfSight(player);
    }

    /**
     * Generates random loot from stealing activities.
     * 
     * <p>Some items like gold and iron nuggets have higher quantities.
     * Other items typically drop in single quantities.</p>
     * 
     * @return a random loot ItemStack from stealing
     */
    public static ItemStack getRandomLootFromStealing() {
        final Random random = new Random();

        Material loot = ItemsUtils.COMMON_LOOTS.get(random.nextInt(ItemsUtils.COMMON_LOOTS.size()));

        int amount = switch (loot) {
            case GOLD_NUGGET, IRON_NUGGET -> 1 + random.nextInt(3);
            default -> 1;
        };

        return new ItemStack(loot, amount);
    }

    /**
     * Checks if a prey entity is a rat (mouse).
     * 
     * @param prey the prey entity to check
     * @return true if the prey is a rat, false otherwise
     */
    public static boolean isRat(Prey prey) {
        return prey.entityType().equalsIgnoreCase("mouse");
    }

    /**
     * Generates random loot from rat hunting.
     * 
     * @return a random ItemStack from rat loot with 1-2 items
     * @see ItemsUtils#RAT_LOOTS
     */
    public static ItemStack getRandomLootFromRat() {
        final Random random = new Random();

        Material loot = ItemsUtils.RAT_LOOTS.get(random.nextInt(ItemsUtils.RAT_LOOTS.size()));

        int amount = 1 + random.nextInt(2);

        return new ItemStack(loot, amount);
    }

    /**
     * Checks if a living entity can be affected by poison.
     * 
     * <p>Undead entities, certain hostile mobs, and some passive creatures
     * are immune to poison effects. This method categorizes entities based
     * on their immunity to toxic effects.</p>
     * 
     * @param entity the entity to check poison susceptibility for
     * @return true if the entity can be poisoned, false if immune
     */
    public static boolean canBePoisoned(LivingEntity entity) {
        // Players can always be poisoned
        if (entity instanceof Player) return true;

        // Check entity type for poison immunity
        switch (entity.getType()) {
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case ZOMBIE_HORSE:
            case ZOMBIFIED_PIGLIN:
            case DROWNED:
            case HUSK:
            case STRAY:
            case SKELETON:
            case SKELETON_HORSE:
            case WITHER_SKELETON:
            case WITHER:
            case PHANTOM:
            case VEX:
            case ZOGLIN:
            case WARDEN:
            case PIGLIN_BRUTE:
                return false; // Undead and certain hostile mobs are immune

            case SHEEP:
            case COW:
            case PIG:
            case CHICKEN:
            case HORSE:
            case DONKEY:
            case MULE:
            case LLAMA:
            case RABBIT:
            case WOLF:
            case CAT:
            case OCELOT:
            case PARROT:
            case VILLAGER:
            case IRON_GOLEM:
            case SNOW_GOLEM:
            case FOX:
            case PANDA:
            case TURTLE:
            case FROG:
            case AXOLOTL:
                return false; // Passive creatures are immune to most toxins

            default:
                return true; // All other entities can be poisoned
        }
    }

    /**
     * Marks an entity as infected with rabies using persistent metadata.
     * 
     * @param entity the entity to mark as rabies-infected
     */
    public static void markInfectedByRabies(LivingEntity entity) {
        entity.getPersistentDataContainer().set(
                RABIES_KEY,
                PersistentDataType.BYTE,
                (byte) 1
        );
    }

    /**
     * Checks if an entity is infected with rabies.
     * 
     * @param entity the entity to check for rabies infection
     * @return true if the entity has rabies, false otherwise
     */
    public static boolean isInfectedWithRabies(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(RABIES_KEY, PersistentDataType.BYTE);
    }

    /**
     * Checks if an entity can be infected with rabies.
     * 
     * <p>Only certain animal types (wolves, bats, and cats) can contract rabies.</p>
     * 
     * @param entity the entity to check rabies susceptibility for
     * @return true if the entity can contract rabies, false otherwise
     */
    public static boolean canBeInfectedByRabies(LivingEntity entity) {
        return entity instanceof Wolf || entity instanceof Bat || entity instanceof Cat;
    }

    /**
     * Checks if an entity is classified as a predator.
     * 
     * <p>Predators include wolves, foxes, ocelots, and cats.</p>
     * 
     * @param entity the entity to check
     * @return true if the entity is a predator, false otherwise
     */
    public static boolean isPredator(LivingEntity entity) {
        return switch (entity.getType()) {
            case WOLF, FOX, OCELOT, CAT -> true;
            default -> false;
        };
    }
}
