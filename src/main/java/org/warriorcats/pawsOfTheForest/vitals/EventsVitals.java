package org.warriorcats.pawsOfTheForest.vitals;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event handler class responsible for managing player vitals in the Paws of the Forest plugin.
 * <p>
 * This class handles the dynamic adjustment of player vitals (thirst, energy, hygiene, and social)
 * based on various player actions and environmental factors. It implements a system similar to
 * Minecraft's hunger mechanics but extends it to include multiple vital statistics.
 * </p>
 * <p>
 * The vitals system operates on multiple timers and responds to player events such as:
 * - Movement (walking, sprinting, swimming)
 * - Combat (attacking and being attacked)
 * - Environmental interactions (drinking, mining, socializing)
 * - Passive regeneration and consumption
 * </p>
 * 
 * @author Warriors Cats Team
 * @version 1.0
 * @since 1.0
 */
public class EventsVitals implements LoadingListener {

    /** Minimum Minecraft food level required for natural health regeneration */
    public static final int BASE_MINECRAFT_FOOD_LEVEL_FOR_REGEN = 18;
    
    /** Number of movement units before vitals consumption occurs */
    public static final int BASE_MINECRAFT_FOOT_STEP_BEFORE_CONSUMING = 4;
    
    /** Activity frequency timer interval in ticks (80 ticks = 4 seconds) */
    public static final long BASE_MINECRAFT_ACTIVITY_FREQUENCY_TICKS = 80L;

    /** Base regeneration value for energy when player has sufficient food */
    public static final double BASE_REGEN_VALUE = 0.05;
    
    /** Social vital regeneration value for chat and interaction activities */
    public static final double SOCIAL_REGEN_VALUE = 0.1;
    
    /** Thirst regeneration value when consuming drinkable items */
    public static final double DRINK_REGEN_VALUE = 0.3;

    /** Base consumption value for passive vital drain and basic movement */
    public static final double BASE_CONSUME_VALUE = 0.005;
    
    /** Vitals consumption value when sprinting */
    public static final double SPRINT_CONSUME_VALUE = 0.1;
    
    /** Energy consumption value when swimming */
    public static final double SWIM_CONSUME_VALUE = 0.01;
    
    /** Vitals consumption value when jumping */
    public static final double JUMP_CONSUME_VALUE = 0.02;
    
    /** Vitals consumption value when mining blocks */
    public static final double MINE_CONSUME_VALUE = 0.005;
    
    /** Vitals consumption value when attacking entities */
    public static final double ATTACK_CONSUME_VALUE = 0.1;
    
    /** Vitals consumption value when being attacked */
    public static final double ATTACKED_CONSUME_VALUE = 0.1;

    /** Tracks accumulated movement distance for each player to determine when to consume vitals */
    private final Map<UUID, Double> distances = new ConcurrentHashMap<>();
    
    /** Tracks last social activity timestamp for each player to manage social vital decay */
    private final Map<UUID, Long> lastSocialActivities = new ConcurrentHashMap<>();

    /**
     * Activity frequency flag that toggles periodically to control vitals consumption timing.
     * <p>
     * Algorithm explanation:
     * When players walk, sprint, or swim, we count the meters traveled. Once a fixed threshold
     * is reached, vitals are decreased only if this frequency flag is true. This mimics
     * Minecraft's internal hunger consumption algorithm.
     * </p>
     */
    private boolean activityFrequency = false;

    /**
     * Initializes the vitals system by setting up periodic tasks for vital management.
     * <p>
     * This method creates three main scheduled tasks:
     * 1. Activity frequency toggle (every 80 ticks / 4 seconds)
     * 2. Social vital decay check (every 60 seconds)
     * 3. Natural energy regeneration (every 3 seconds)
     * </p>
     */
    @Override
    public void load() {
        // Toggle activity frequency to control vitals consumption timing
        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            activityFrequency = !activityFrequency;
        }, 0L, BASE_MINECRAFT_ACTIVITY_FREQUENCY_TICKS);

        // Check for social inactivity and apply decay every minute
        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            long now = System.currentTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                long last = lastSocialActivities.getOrDefault(uuid, now);
                // Apply social and hygiene decay if no social activity for 60 seconds
                if (now - last >= 60000) {
                    decreaseVitals(player, 0, 0, BASE_CONSUME_VALUE, BASE_CONSUME_VALUE);
                }
            }
        }, 0L, 60L * 20L);

        // Natural energy regeneration when player has sufficient food
        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                // Only regenerate energy if player has high food level (similar to health regen)
                if (player.getFoodLevel() > BASE_MINECRAFT_FOOD_LEVEL_FOR_REGEN) {
                    increaseVitals(player, 0, BASE_REGEN_VALUE, 0, 0);
                }
            }
        }, 0L, 60L);
    }

    /**
     * Handles player respawn by fully restoring all vitals.
     * 
     * @param event the player respawn event
     */
    @EventHandler
    public void on(PlayerRespawnEvent event) {
        // Restore all vitals to maximum on respawn
        increaseVitals(event.getPlayer(), 1, 1, 1, 1);
    }

    /**
     * Handles player movement to calculate vitals consumption based on activity type.
     * <p>
     * This method tracks movement distance and applies different consumption rates for:
     * - Sprinting: High thirst and energy consumption
     * - Swimming: Moderate energy consumption
     * - Walking: Base consumption for thirst and energy
     * </p>
     * <p>
     * Additionally provides thirst and hygiene regeneration when in water or rain.
     * </p>
     * 
     * @param event the player movement event
     */
    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        double distance = event.getFrom().distanceSquared(event.getTo());
        double total = distances.getOrDefault(player.getUniqueId(), 0.0);
        total += distance;

        // Apply vitals consumption based on movement type when thresholds are met
        if (total >= BASE_MINECRAFT_FOOT_STEP_BEFORE_CONSUMING && activityFrequency) {
            if (player.isSprinting()) {
                // Sprinting consumes more thirst and energy
                decreaseVitals(player, SPRINT_CONSUME_VALUE, SPRINT_CONSUME_VALUE, 0, 0);
            } else if (player.isInWater()) {
                // Swimming only consumes energy
                decreaseVitals(player, 0, SWIM_CONSUME_VALUE, 0, 0);
            } else {
                // Normal walking consumes base amounts of thirst and energy
                decreaseVitals(player, BASE_CONSUME_VALUE, BASE_CONSUME_VALUE, 0, 0);
            }
            activityFrequency = false;
        }

        // Reset distance counter when player stops moving
        if (distance == 0) {
            total = 0;
        }

        // Regenerate thirst and hygiene when in water, rain, or bubble columns
        if (player.isInWaterOrRainOrBubbleColumn()) {
            increaseVitals(player, 1, 0, 1, 0);
        }

        distances.put(player.getUniqueId(), total);
    }

    /**
     * Handles player jumping by consuming thirst and energy.
     * 
     * @param event the player jump event
     */
    @EventHandler
    public void on(PlayerJumpEvent event) {
        decreaseVitals(event.getPlayer(), JUMP_CONSUME_VALUE, JUMP_CONSUME_VALUE, 0, 0);
    }

    /**
     * Handles block breaking by consuming thirst, energy, and hygiene.
     * Mining is considered a strenuous activity that affects multiple vitals.
     * 
     * @param event the block break event
     */
    @EventHandler
    public void on(BlockBreakEvent event) {
        decreaseVitals(event.getPlayer(), MINE_CONSUME_VALUE, MINE_CONSUME_VALUE, MINE_CONSUME_VALUE, 0);
    }

    /**
     * Handles entity damage caused by a player (attacking).
     * Attacking consumes thirst, energy, and hygiene due to physical exertion.
     * 
     * @param event the entity damage by entity event
     */
    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        decreaseVitals(player, ATTACK_CONSUME_VALUE, ATTACK_CONSUME_VALUE, ATTACK_CONSUME_VALUE, 0);
    }

    /**
     * Handles player taking damage (being attacked).
     * Taking damage decreases thirst, energy, and hygiene due to stress and injury.
     * 
     * @param event the entity damage event
     */
    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        decreaseVitals(player, ATTACKED_CONSUME_VALUE, ATTACKED_CONSUME_VALUE, ATTACKED_CONSUME_VALUE, 0);
    }

    /**
     * Handles player chat messages as social activity.
     * Chatting increases social vital and updates the last social activity timestamp.
     * 
     * @param event the async player chat event
     */
    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        lastSocialActivities.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        increaseVitals(event.getPlayer(), 0, 0, 0, SOCIAL_REGEN_VALUE);
    }

    /**
     * Handles player interactions as social activity.
     * Interactions increase social vital and update the last social activity timestamp.
     * 
     * @param event the player interact event
     */
    @EventHandler
    public void on(PlayerInteractEvent event) {
        lastSocialActivities.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        increaseVitals(event.getPlayer(), 0, 0, 0, SOCIAL_REGEN_VALUE);
    }

    /**
     * Handles player item consumption, specifically drinkable items.
     * Consuming drinkable items restores thirst.
     * 
     * @param event the player item consume event
     */
    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (ItemsUtils.isDrinkable(event.getItem())) {
            increaseVitals(event.getPlayer(), DRINK_REGEN_VALUE, 0, 0, 0);
        }
    }

    /**
     * Decreases player vitals by the specified amounts.
     * Values are clamped to prevent going below 0.0.
     * 
     * @param player the player whose vitals to decrease
     * @param thirst the amount to decrease thirst by
     * @param energy the amount to decrease energy by
     * @param hygiene the amount to decrease hygiene by
     * @param social the amount to decrease social by
     */
    private void decreaseVitals(Player player, double thirst, double energy, double hygiene, double social) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        HibernateUtils.withTransaction(((transaction, session) -> {
            entity.setThirst(entity.getThirst() - thirst);
            entity.setEnergy(entity.getEnergy() - energy);
            entity.setHygiene(entity.getHygiene() - hygiene);
            entity.setSocial(entity.getSocial() - social);
            return entity;
        }));
        HUD.updateInterface(player);
    }

    /**
     * Increases player vitals by the specified amounts.
     * Values are clamped to prevent exceeding 1.0 (100%).
     * 
     * @param player the player whose vitals to increase
     * @param thirst the amount to increase thirst by
     * @param energy the amount to increase energy by
     * @param hygiene the amount to increase hygiene by
     * @param social the amount to increase social by
     */
    private void increaseVitals(Player player, double thirst, double energy, double hygiene, double social) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        HibernateUtils.withTransaction(((transaction, session) -> {
            entity.setThirst(entity.getThirst() + thirst);
            entity.setEnergy(entity.getEnergy() + energy);
            entity.setHygiene(entity.getHygiene() + hygiene);
            entity.setSocial(entity.getSocial() + social);
            return entity;
        }));
        HUD.updateInterface(player);
    }
}
