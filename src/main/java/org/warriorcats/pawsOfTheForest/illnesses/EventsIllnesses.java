package org.warriorcats.pawsOfTheForest.illnesses;

import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.clans.Clans;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event listener and manager for the illness system in the Warrior Cats plugin.
 * 
 * This class handles all aspects of illness mechanics including:
 * - Illness contraction through various environmental and combat triggers
 * - Illness progression and worsening over time
 * - Application and management of illness-related potion effects
 * - Special illness behaviors (such as rabies aggression)
 * - Environmental tracking for condition-based illnesses
 * 
 * The system uses probability-based infection rates and environmental factors
 * to create realistic illness scenarios. Different clans may have resistances
 * to certain environmental conditions (e.g., Creek clan vs frostbite, Breeze clan vs heatstroke).
 * 
 * Illness progression is handled through a scheduled task that runs every 5 seconds,
 * checking all online players for worsened conditions and applying appropriate effects.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 * @see Illnesses
 * @see IllnessEntity
 * @see LoadingListener
 */
public class EventsIllnesses implements LoadingListener {

    /** Base probability rate for random illness infections (0.2% chance) */
    public static final double BASE_INFECTION_RATE = 0.002;
    
    /** Infection rate when near infected players (25% chance) */
    public static final double NEARBY_BASE_INFECTION_RATE = 0.25;
    
    /** Infection rate for punctual events like combat or consumption (5% chance) */
    public static final double PUNCTUAL_INFECTION_RATE = 0.05;
    
    /** Maximum distance in blocks for proximity-based infections */ 
    public static final int BASE_INFECTION_DISTANCE = 5;

    /** Probability rate for rabies aggressive behavior episodes (10% chance) */
    public static final double RABIES_AGGRESSION_RATE = 0.1;
    
    /** Health threshold below which broken bones can occur (2 hearts) */
    public static final int BROKEN_BONES_HEALTH_RATE = 2;
    
    /** Damage threshold above which seizures can be triggered (3 hearts) */
    public static final int SEIZURES_HEALTH_RATE = 6;
    
    /** Damage threshold above which arthritis can be triggered (1.5 hearts) */
    public static final int ARTHRITIS_HEALTH_RATE = 3;

    /** Age in Minecraft days after which players become susceptible to arthritis (180 days) */
    public static final int ARTHRITIS_DAYS_RATE = 180;

    /** 
     * Tracks players with worsened non-fatal illnesses to prevent duplicate effect application.
     * Fatal illnesses that cause death when worsened are not stored here.
     */
    private final Map<UUID, Set<Illnesses>> worsened = new ConcurrentHashMap<>();

    /** Tracks time spent in tall grass for external parasite infection calculations */
    private final Map<UUID, Long> timeInTallGrass = new ConcurrentHashMap<>();
    
    /** Tracks time spent in cold biomes for frostbite infection calculations */
    private final Map<UUID, Long> timeInSnow = new ConcurrentHashMap<>();
    
    /** Tracks time spent in direct sunlight for heatstroke infection calculations */
    private final Map<UUID, Long> timeInSun = new ConcurrentHashMap<>();

    /**
     * Initializes the illness system by starting a scheduled task that manages illness progression.
     * 
     * The task runs every 5 seconds (100 ticks) and performs the following operations:
     * 1. Checks all online players for worsened illnesses
     * 2. Handles fatal illness progression (instant death)
     * 3. Applies worsened effects for non-fatal illnesses
     * 4. Manages age-related arthritis susceptibility
     * 
     * Fatal illnesses cause immediate death when worsened, while non-fatal illnesses
     * receive enhanced potion effects. The system prevents duplicate effect application
     * by tracking worsened illness states.
     */
    @Override
    public void load() {
        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
                
                // Check all current illnesses for progression
                for (Illnesses illness : Illnesses.values()) {
                    if (entity.hasIllness(illness)) {
                        IllnessEntity illnessEntity = entity.getIllness(illness);
                        if (illnessEntity.isWorsened()) {
                            if (illnessEntity.getIllness().isFatal()) {
                                // Fatal illnesses cause immediate death when worsened
                                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);
                                player.setHealth(0);
                                player.sendMessage(MessagesConf.Illnesses.COLOR_FEEDBACK + MessagesConf.Illnesses.ILLNESS_WORSENED_DEATH + " " + illness);
                            } else {
                                // Special case: WOUNDS progress to INFECTED_WOUNDS
                                if (illness == Illnesses.WOUNDS && !entity.hasIllness(Illnesses.INFECTED_WOUNDS)) {
                                    applyIllness(player, Illnesses.INFECTED_WOUNDS);
                                    player.sendMessage(ChatColor.DARK_RED + MessagesConf.Illnesses.WOUNDS_INFECTED);
                                    continue;
                                }
                                
                                // Skip if already processed worsened state for this illness
                                if (worsened.containsKey(player.getUniqueId()) &&
                                        worsened.get(player.getUniqueId()).stream()
                                                .anyMatch(worsened -> worsened == illness)) {
                                    continue;
                                }
                                // Apply worsened effects for non-fatal illnesses
                                worsened.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
                                addPotionEffects(player, illness, illnessEntity.getAmplifier());
                                worsened.get(player.getUniqueId()).add(illness);
                                player.sendMessage(MessagesConf.Illnesses.COLOR_FEEDBACK + MessagesConf.Illnesses.ILLNESS_WORSENED + " " + illness);
                            }
                        }
                    }
                }
                // Age-related arthritis development for elderly players
                if (entity.getAgeInMinecraftDays() > ARTHRITIS_DAYS_RATE && Math.random() < BASE_INFECTION_RATE) {
                    applyIllness(player, Illnesses.ARTHRITIS);
                }
            }
        }, 0L, 20 * 5);
    }

    /**
     * Handles player join events by reapplying all existing illness effects.
     * 
     * When a player reconnects to the server, their potion effects are lost.
     * This handler ensures that all persistent illness effects are restored
     * with the appropriate amplifier levels based on illness progression.
     * 
     * @param event The player join event
     */
    @EventHandler
    public void on(PlayerJoinEvent event) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(event.getPlayer().getUniqueId());
        // Restore all illness effects for the reconnecting player
        for (IllnessEntity illnessEntity : entity.getIllnesses()) {
            addPotionEffects(event.getPlayer(), illnessEntity.getIllness(), illnessEntity.getAmplifier());
        }
    }

    /**
     * Handles player movement events and manages environment-based illness infections.
     * 
     * This method processes multiple illness types based on environmental conditions:
     * - Upper respiratory infections during storms or near infected players
     * - External parasites from prolonged exposure to tall grass
     * - Frostbite from extended time in cold biomes (except Creek clan)
     * - Heatstroke from direct sunlight exposure (except Breeze clan)
     * - Rabies aggression behavior for infected players
     * 
     * Environmental tracking uses time-based thresholds to prevent instant infections
     * and create realistic exposure scenarios.
     * 
     * @param event The player movement event
     */
    @EventHandler
    public void on(PlayerMoveEvent event) {
        // UPPER_RESPIRATORY_INFECTION - triggered by storms or proximity to infected players
        if (event.getPlayer().getWorld().hasStorm() && Math.random() < BASE_INFECTION_RATE ||
            isNearFromPlayerSick(event.getPlayer(), Illnesses.UPPER_RESPIRATORY_INFECTION) && Math.random() < NEARBY_BASE_INFECTION_RATE) {
            applyIllness(event.getPlayer(), Illnesses.UPPER_RESPIRATORY_INFECTION);
        }

        // EXTERNAL_PARASITES - acquired from prolonged exposure to tall grass
        Block blockBelow = event.getTo().clone().subtract(0, 1, 0).getBlock();
        UUID uuid = event.getPlayer().getUniqueId();

        if (blockBelow.getType().toString().contains("TALL_GRASS")) {
            // Start tracking time in tall grass
            timeInTallGrass.putIfAbsent(uuid, System.currentTimeMillis());

            long elapsed = System.currentTimeMillis() - timeInTallGrass.get(uuid);
            // Higher chance after 2 minutes, lower chance after 30 seconds
            if ((elapsed > 120_000 && Math.random() < BASE_INFECTION_RATE) ||
                    (elapsed > 30_000 && Math.random() < BASE_INFECTION_RATE / 2)) {
                applyIllness(event.getPlayer(), Illnesses.EXTERNAL_PARASITES);
                timeInTallGrass.remove(uuid);
            }
        } else {
            // Reset timer when leaving tall grass
            timeInTallGrass.remove(uuid);
        }

        // FROSTBITE - cold biome exposure with clan resistance
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(event.getPlayer().getUniqueId());
        if (BiomesUtils.isCold(blockBelow) && entity.getClan() != Clans.CREEK) {
            // Creek clan has natural resistance to cold
            timeInSnow.putIfAbsent(uuid, System.currentTimeMillis());

            long duration = System.currentTimeMillis() - timeInSnow.get(uuid);
            // Requires 1 minute in cold + low air (hypothermia condition)
            if (duration > 60_000 && event.getPlayer().getRemainingAir() < 300) {
                applyIllness(event.getPlayer(), Illnesses.FROSTBITE);
                timeInSnow.remove(uuid);
            }
        } else {
            // Reset timer when leaving cold biomes or for Creek clan
            timeInSnow.remove(uuid);
        }

        // HEATSTROKE - direct sunlight exposure with clan resistance
        Location loc = event.getPlayer().getLocation();
        if (event.getPlayer().getWorld().getTime() > 0 && event.getPlayer().getWorld().getTime() < 12300
                && event.getPlayer().getWorld().getHighestBlockYAt(loc) <= loc.getBlockY() + 1
                && !event.getPlayer().getEyeLocation().getBlock().getType().toString().contains("WATER")
                && entity.getClan() != Clans.BREEZE) {
            // Breeze clan has natural resistance to heat
            // Conditions: daytime, no cover above, not in water
            timeInSun.putIfAbsent(uuid, System.currentTimeMillis());

            long duration = System.currentTimeMillis() - timeInSun.get(uuid);
            // Requires 1.5 minutes of direct sun exposure
            if (duration > 90_000) {
                applyIllness(event.getPlayer(), Illnesses.HEATSTROKE);
                timeInSun.remove(uuid);
            }
        } else {
            // Reset timer when conditions no longer met
            timeInSun.remove(uuid);
        }

        // RABIES aggression behavior - infected players show hostile actions
        if (entity.hasIllness(Illnesses.RABIES) && Math.random() < RABIES_AGGRESSION_RATE) {
            // Make player look at nearest player aggressively
            PlayersUtils.getNearestPlayer(event.getPlayer()).ifPresent(nearest ->
                    event.getPlayer().lookAt(nearest, LookAnchor.EYES, LookAnchor.EYES));

            // Play threatening growl sound
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WOLF_GROWL, 1f, 0.8f);

            // Force aggressive chat message
            event.getPlayer().chat(ChatColor.DARK_RED + MessagesConf.Illnesses.GROWL_RABIES);
        }
    }

    /**
     * Handles creature spawn events to randomly infect spawning mobs with rabies.
     * 
     * When eligible creatures spawn, they have a chance to be infected with rabies,
     * making them potential transmission vectors for players. Only certain mob types
     * can carry rabies as determined by {@link MobsUtils#canBeInfectedByRabies(LivingEntity)}.
     * 
     * @param event The creature spawn event
     */
    @EventHandler
    public void on(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        // Randomly infect eligible creatures with rabies at spawn
        if (MobsUtils.canBeInfectedByRabies(entity) && Math.random() < PUNCTUAL_INFECTION_RATE) {
            MobsUtils.markInfectedByRabies(entity);
        }
    }

    /**
     * Handles entity damage events for combat-related illness transmissions.
     * 
     * This handler processes illness transmission through combat interactions:
     * - Rabies transmission from infected mobs to players (with secondary seizure risk)
     * - Infected wounds from attacks by players or predators
     * - Broken bones from severe player-inflicted damage
     * 
     * The system uses different probability rates based on the type of interaction
     * and the severity of the damage dealt.
     * 
     * @param event The entity damage by entity event
     */
    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!(event.getDamager() instanceof LivingEntity entity)) {
            return;
        }

        // RABIES - transmitted from infected mobs with high probability
        if (MobsUtils.isInfectedWithRabies(entity) && Math.random() < NEARBY_BASE_INFECTION_RATE) {
            applyIllness(player, Illnesses.RABIES);
            // Secondary complication: seizures from rabies infection
            if (Math.random() < PUNCTUAL_INFECTION_RATE) {
                applyIllness(player, Illnesses.SEIZURES);
            }
        }

        // WOUNDS - from any melee attacks
        if (Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(player, Illnesses.WOUNDS);
        }

        // INFECTED_WOUNDS - from attacks by players or predatory mobs (higher chance)
        if ((entity instanceof Player || MobsUtils.isPredator(entity)) && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(player, Illnesses.INFECTED_WOUNDS);
        }

        // BROKEN_BONES - from severe damage that brings health below threshold
        if (entity instanceof Player && player.getHealth() - event.getFinalDamage() <= BROKEN_BONES_HEALTH_RATE && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(player, Illnesses.BROKEN_BONES);
        }
    }

    /**
     * Handles item consumption events for food and herb-related illness transmissions.
     * 
     * This handler manages illness risks from consuming various items:
     * - Internal parasites from contaminated drinks or raw prey
     * - Poisoning from spoiled prey, toxic items, or toxic herbs
     * - Seizures from consuming cursed herbs
     * 
     * The system encourages careful consideration of food safety and herb knowledge
     * in the Warrior Cats survival experience.
     * 
     * @param event The player item consume event
     */
    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        // INTERNAL_PARASITES - from contaminated water or raw prey
        if ((ItemsUtils.isDrinkable(event.getItem()) || ItemsUtils.isRawPrey(event.getItem()))
                && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(event.getPlayer(), Illnesses.INTERNAL_PARASITES);
        }

        // POISONING - from spoiled prey, toxic items, or dangerous herbs
        if ((ItemsUtils.isBadPrey(event.getItem()) || ItemsUtils.isToxicItem(event.getItem())
                || ItemsUtils.isToxicHerb(event.getItem())) && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(event.getPlayer(), Illnesses.POISONING);
        }

        // SEIZURES - from consuming cursed or improperly prepared herbs
        if (ItemsUtils.isCursedHerb(event.getItem()) && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(event.getPlayer(), Illnesses.SEIZURES);
        }
    }

    /**
     * Handles bed usage events for external parasite transmission.
     * 
     * When players use beds that may be infested with fleas or other parasites,
     * there's a chance of contracting external parasites. The infection check
     * is delayed by 5 seconds to simulate the time needed for parasites to
     * transfer from the bedding to the player.
     * 
     * This mechanic encourages maintaining clean sleeping areas and adds
     * realism to communal living spaces in clan territories.
     * 
     * @param event The player bed enter event
     */
    @EventHandler
    public void on(PlayerBedEnterEvent event) {
        // Delayed check for parasite transmission from infested bedding
        Bukkit.getScheduler().runTaskLater(PawsOfTheForest.getInstance(), () -> {
            if (Math.random() < PUNCTUAL_INFECTION_RATE) {
                applyIllness(event.getPlayer(), Illnesses.EXTERNAL_PARASITES);
            }
        }, 20 * 5);
    }

    /**
     * Handles general entity damage events for trauma-related illness development.
     * 
     * This handler processes illnesses that can result from various forms of damage:
     * - Broken bones from fall damage when health drops below threshold
     * - Seizures from fall damage or severe trauma (high damage amounts)
     * - Arthritis from repeated physical trauma
     * 
     * The system simulates realistic medical consequences of physical trauma,
     * encouraging players to avoid dangerous situations and seek proper treatment.
     * 
     * @param event The entity damage event
     */
    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // BROKEN_BONES - from fall damage when critically injured
        if (event.getDamageSource().getDamageType() == DamageType.FALL
                && player.getHealth() - event.getFinalDamage() <= BROKEN_BONES_HEALTH_RATE
                && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(player, Illnesses.BROKEN_BONES);
        }

        // SEIZURES - from severe trauma (falls or high damage)
        if ((event.getDamageSource().getDamageType() == DamageType.FALL
                || event.getFinalDamage() >= SEIZURES_HEALTH_RATE) && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(player, Illnesses.SEIZURES);
        }

        // ARTHRITIS - from repeated physical trauma
        if (event.getFinalDamage() >= ARTHRITIS_HEALTH_RATE && Math.random() < PUNCTUAL_INFECTION_RATE) {
            applyIllness(player, Illnesses.ARTHRITIS);
        }
    }

    /**
     * Applies a new illness to a player if they don't already have it.
     * 
     * This method handles the complete process of illness application:
     * 1. Checks if the player already has the illness (prevents duplicates)
     * 2. Creates a new IllnessEntity with current timestamp
     * 3. Persists the illness to the database via Hibernate transaction
     * 4. Sends notification message to the player
     * 5. Applies initial potion effects with base amplifier (0)
     * 
     * @param player The player to infect with the illness
     * @param illness The type of illness to apply
     */
    private void applyIllness(Player player, Illnesses illness) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        // Prevent duplicate illness infections
        if (entity.hasIllness(illness)) {
            return;
        }
        
        // Create and persist new illness entity
        HibernateUtils.withTransaction(((transaction, session) -> {
            IllnessEntity illnessEntity = new IllnessEntity();
            illnessEntity.setIllness(illness);
            illnessEntity.setGotAt(Date.from(Instant.now()));
            entity.getIllnesses().add(illnessEntity);
            return entity;
        }));
        
        // Notify player and apply initial effects
        player.sendMessage(MessagesConf.Illnesses.COLOR_FEEDBACK + MessagesConf.Illnesses.GOT_SICK + " " + illness);
        addPotionEffects(player, illness, 0);
    }

    /**
     * Applies illness-specific potion effects to a player.
     * 
     * This method first removes any existing effects from the same illness to prevent
     * stacking, then applies fresh effects with the appropriate amplifier level.
     * 
     * Amplifier logic:
     * - If amplifier > 0 (worsened illness): Uses the worsened amplifier level
     * - If amplifier = 0 (normal illness): Uses the effect's default amplifier
     * 
     * Effects are applied with infinite duration (Integer.MAX_VALUE) as they persist
     * until the illness is cured or the player logs out.
     * 
     * @param player The player to apply effects to
     * @param illness The illness whose effects should be applied
     * @param amplifier The amplifier level (0 for normal, >0 for worsened)
     */
    private void addPotionEffects(Player player, Illnesses illness, int amplifier) {
        // Remove existing effects to prevent stacking
        removePotionEffects(player, illness);
        
        // Apply each effect defined by the illness
        for (Map.Entry<PotionEffectType, Integer> effect : illness.getPotionEffects().entrySet()) {
            player.addPotionEffect(new PotionEffect(effect.getKey(), Integer.MAX_VALUE,
                    amplifier > 0 ? amplifier : effect.getValue()));
        }
    }

    /**
     * Removes all potion effects associated with a specific illness from a player.
     * 
     * This method is used to clean up existing effects before applying fresh ones,
     * preventing effect stacking and ensuring accurate symptom representation.
     * It's also used when curing illnesses to remove all associated symptoms.
     * 
     * @param player The player to remove effects from
     * @param illness The illness whose effects should be removed
     */
    private void removePotionEffects(Player player, Illnesses illness) {
        // Remove each effect type associated with this illness
        for (PotionEffectType effect : illness.getPotionEffects().keySet()) {
            player.removePotionEffect(effect);
        }
    }

    /**
     * Checks if there are any players nearby who have a specific illness.
     * 
     * This method is used for proximity-based illness transmission, such as
     * respiratory infections that can spread between players in close contact.
     * The method uses squared distance calculation for performance optimization
     * when checking against all online players.
     * 
     * Distance calculation:
     * - Uses distanceSquared() for better performance (avoids sqrt calculation)
     * - BASE_INFECTION_DISTANCE defines the maximum transmission range
     * - Only checks players in the same world as the target player
     * 
     * @param player The player to check for nearby infected players
     * @param illness The specific illness to check for in nearby players
     * @return true if any nearby player has the specified illness, false otherwise
     */
    private boolean isNearFromPlayerSick(Player player, Illnesses illness) {
        // Check all online players for proximity and illness status
        for (Player other : Bukkit.getOnlinePlayers()) {
            PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(other.getUniqueId());
            // Use squared distance for performance, check illness status
            if (player.getLocation().distanceSquared(other.getLocation()) < BASE_INFECTION_DISTANCE &&
                entity.hasIllness(illness)) {
                return true;
            }
        }
        return false;
    }
}
