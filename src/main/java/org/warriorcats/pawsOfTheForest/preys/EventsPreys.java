package org.warriorcats.pawsOfTheForest.preys;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsPassives;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Handles all prey-related events and behaviors in the Warrior Cats game system.
 * This class manages:
 * - Automatic prey spawning around players
 * - Prey fleeing behavior when players approach
 * - Reward distribution when preys are killed
 * - Marking of dangerous prey items
 * 
 * The system includes sophisticated hunting mechanics where preys flee from players
 * unless the player has stealth abilities, and provides experience points and coins
 * as rewards for successful hunts.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0
 * @see Prey
 * @see LoadingListener
 */
public class EventsPreys implements LoadingListener {

    /** The probability that common prey will spawn during each spawn cycle */
    public static final float COMMON_SPAWN_CHANCE = 0.15f;
    
    /** The base probability that higher-tier prey will spawn during each spawn cycle */
    public static final float HIGHER_SPAWN_CHANCE = 0.05f;

    /** The radius in blocks within which preys will detect and flee from players */
    public static final int DEFAULT_FLEE_RADIUS = 6;
    
    /** The delay in seconds between prey spawn checks for each player */
    public static final int DEFAULT_SPAWN_SCAN_DELAY_S = 10;

    /** 
     * Map tracking currently fleeing preys to prevent duplicate flee tasks.
     * Key: Prey entity UUID, Value: BukkitTask managing the flee duration
     */
    private static final Map<UUID, BukkitTask> FLEEING_PREYS = new HashMap<>();

    /**
     * Initializes the prey spawning system when the plugin loads.
     * Creates a repeating task that periodically spawns preys around online players
     * based on spawn chances and player skills.
     * 
     * The spawning algorithm:
     * 1. For each online player, generates a random location within 20 blocks
     * 2. Determines spawn chance based on player's Blood Hunter skill
     * 3. Selects appropriate prey type (higher-tier vs common)
     * 4. Validates location suitability for aquatic/terrestrial preys
     * 5. Attempts to spawn the selected prey using custom or vanilla methods
     */
    @Override
    public void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Function to generate random spawn location around player
                Function<Player, Location> getLocation = (player) -> {
                    Location loc = player.getLocation().clone().add(
                            (Math.random() - 0.5) * 20, // Random X offset within ±10 blocks
                            0,
                            (Math.random() - 0.5) * 20  // Random Z offset within ±10 blocks
                    );
                    // Set Y to highest block + 1 for surface spawning
                    loc.setY(player.getWorld().getHighestBlockYAt(loc) + 1);
                    return loc;
                };
                
                // Function to check if location is suitable for prey type
                BiFunction<Location, Prey, Boolean> isSuitable = (location, prey) -> {
                    if (location.getBlock().isLiquid()) return prey.isAquatic();
                    if (location.clone().subtract(0, 1, 0).getBlock().isLiquid()) return prey.isAquatic();
                    return !prey.isAquatic();
                };
                
                // Process spawning for each online player
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerEntity playerEntity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
                    List<Prey> preys = new ArrayList<>();
                    
                    // Calculate higher-tier spawn chance with Blood Hunter skill bonus
                    double higherSpawnChance = !playerEntity.hasAbility(Skills.BLOOD_HUNTER) ?
                            HIGHER_SPAWN_CHANCE :
                            HIGHER_SPAWN_CHANCE * (1 + playerEntity.getAbilityTier(Skills.BLOOD_HUNTER) * EventsSkillsPassives.BLOOD_HUNTER_TIER_PERCENTAGE);
                    
                    // Determine prey tier to spawn
                    if (Math.random() < higherSpawnChance) {
                        preys = Prey.getAllHighers();
                    } else if (Math.random() < COMMON_SPAWN_CHANCE) {
                        preys = Prey.getAllCommons();
                    }
                    
                    // Attempt to spawn selected prey if suitable location found
                    if (!preys.isEmpty()) {
                        Prey toSpawn = preys.get(new Random().nextInt(preys.size()));
                        Location locationToSpawn = getLocation.apply(player);
                        if (isSuitable.apply(locationToSpawn, toSpawn)) {
                            try {
                                MobsUtils.spawn(locationToSpawn, toSpawn.entityType().toLowerCase(), Math.random());
                            } catch (IllegalArgumentException ignored) {
                                // Allow vanilla spawning to work as fallback without error handling
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0, 20 * DEFAULT_SPAWN_SCAN_DELAY_S);
    }

    /**
     * Handles prey fleeing behavior when players move nearby.
     * This event is triggered whenever a player moves and checks for nearby prey entities
     * that should flee from the player's presence.
     * 
     * The fleeing mechanism:
     * 1. Detects prey entities within the flee radius
     * 2. Checks if player is stealthed (some skills allow stealth hunting)
     * 3. Calculates flee direction away from the player
     * 4. Applies velocity and rotation to make prey flee realistically
     * 5. Triggers running animation for custom models
     * 6. Starts a timer to limit flee duration per prey configuration
     * 
     * @param event The PlayerMoveEvent triggered when a player moves
     */
    @EventHandler
    public void on(PlayerMoveEvent event) {
        // Check all entities within flee radius
        for (Entity nearby : event.getPlayer().getNearbyEntities(DEFAULT_FLEE_RADIUS, DEFAULT_FLEE_RADIUS, DEFAULT_FLEE_RADIUS)) {
            if (nearby instanceof LivingEntity nearbyLiving && Prey.isPrey(nearbyLiving) &&
                    (!MobsUtils.isStealthFrom(event.getPlayer(), nearbyLiving) || FLEEING_PREYS.containsKey(nearbyLiving.getUniqueId()))) {
                
                // Calculate flee direction (away from player)
                Vector fleeVector = nearbyLiving.getLocation().toVector()
                        .subtract(event.getPlayer().getLocation().toVector()).normalize().multiply(0.35);

                // Apply flee velocity
                nearbyLiving.setVelocity(fleeVector);

                // Orient prey to face flee direction
                Location loc = nearbyLiving.getLocation();
                Vector dir = fleeVector.clone().normalize();
                float yaw = (float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
                loc.setYaw(yaw);

                nearbyLiving.teleport(loc);

                // Trigger running animation for custom models
                ModeledEntity modeled = ModelEngine.getModeledEntity(nearbyLiving);
                if (modeled != null) {
                    modeled.getModels().values().forEach(model -> {
                        model.getAnimationHandler().playAnimation("run", 0, 0, 0, false);
                    });
                }
                
                // Start flee duration timer if not already fleeing
                if (!FLEEING_PREYS.containsKey(nearbyLiving.getUniqueId())) {
                    FLEEING_PREYS.put(nearbyLiving.getUniqueId(), new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            // Clean up if entity is no longer valid
                            if (!nearbyLiving.isValid()) {
                                FLEEING_PREYS.remove(nearbyLiving.getUniqueId());
                                this.cancel();
                                return;
                            }
                            
                            // Check if flee duration has elapsed
                            ticks += 20;
                            if (ticks >= Prey.fromEntity(nearbyLiving).get().fleeDurationSeconds() * 20) {
                                FLEEING_PREYS.remove(nearbyLiving.getUniqueId());
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(PawsOfTheForest.getInstance(), 0, 20));
                }
            }
        }
    }

    /**
     * Handles the death of prey entities, managing reward distribution and item marking.
     * This event processes two main aspects of prey hunting:
     * 1. Reward distribution (experience points and coins) to the player who killed the prey
     * 2. Marking of dangerous prey items that could cause poisoning when consumed
     * 
     * The reward system updates both the vanilla Minecraft experience and the custom
     * player progression system with perks and coins.
     * 
     * @param event The EntityDeathEvent triggered when any entity dies
     */
    @EventHandler
    public void on(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Optional<Prey> existingPrey = Prey.fromEntity(event.getEntity());

        // Process rewards when a player successfully kills a prey
        if (existingPrey.isPresent() && killer != null) {
            Prey prey = existingPrey.get();
            
            // Update player's progression data in database transaction
            HibernateUtils.withTransaction(((transaction, session) -> {
                PlayerEntity player = session.get(PlayerEntity.class, killer.getUniqueId());
                event.setDroppedExp((int) prey.xp());  // Set vanilla Minecraft experience
                player.setXpPerks(player.getXpPerks() + prey.xp());  // Add to custom perk experience
                player.setCoins(player.getCoins() + prey.coins());    // Add coin reward
                return player;
            }));
            
            // Send feedback messages to the player
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.SKILL_POINTS_EARNED + event.getDroppedExp());
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.COINS_EARNED + prey.coins());
        }

        // Mark items from dangerous preys to prevent accidental poisoning
        if (existingPrey.isPresent() && Prey.getAllBadsToEat().contains(existingPrey.get())) {
            for (ItemStack drop : event.getDrops()) {
                if (ItemsUtils.isRawPrey(drop)) {
                    ItemsUtils.markAsBadPrey(drop);  // Add warning metadata to item
                }
            }
        }
    }
}
