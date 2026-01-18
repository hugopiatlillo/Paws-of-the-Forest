package org.warriorcats.pawsOfTheForest.skills;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.PlayerInCombatEvent;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.PlayersUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event handler for active skills in the Warrior Cats skill system.
 * 
 * <p>This class manages all active skills that players can trigger through right-click interactions,
 * including combat abilities, hunting skills, navigation tools, and support skills. It also handles
 * the downed state system for player revival mechanics and manages skill item persistence.</p>
 * 
 * <p>Active skills include:</p>
 * <ul>
 *   <li>Prey Sense - Highlights nearby prey with glowing effect</li>
 *   <li>Hunter's Compass - Points compass to nearest prey</li>
 *   <li>Low Sweep - Applies slowness to nearby enemies after a charge delay</li>
 *   <li>Pathfinding Boost - Provides speed and jump boost for travel</li>
 *   <li>On Your Paws - Revives downed clan members</li>
 *   <li>Location Awareness - Navigation tool for stored waypoints</li>
 *   <li>Trail Memory - Teleportation to stored waypoint locations</li>
 * </ul>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
public class EventsSkillsActives implements Listener {

    /** The radius in blocks for the Prey Sense skill to detect prey entities */
    public static final int PREY_SENSE_RADIUS = 25;
    /** Duration in ticks that prey entities glow after Prey Sense activation */
    public static final int PREY_SENSE_DURATION_TICKS = 5 * 20;
    /** Range in blocks for the Low Sweep skill to affect targets */
    public static final int LOW_SWEEP_RANGE = 4;
    /** Charge delay in ticks before Low Sweep activates (1 second) */
    public static final int LOW_SWEEP_CHARGE_DELAY_TICKS = 20; // 1s
    /** Duration in ticks for Low Sweep slowness effect (2.5 seconds) */
    public static final int LOW_SWEEP_SLOWNESS_DURATION_TICKS = 50; // 2.5s
    /** Duration in ticks for Pathfinding Boost effects (10 seconds) */
    public static final int PATHFINDING_BOOST_DURATION_TICKS = 10 * 20;
    /** Radius in blocks for Hold On skill to activate around dying players */
    public static final double HOLD_ON_RADIUS = 50;
    /** Duration in ticks for Hold On downed state (2 minutes) */
    public static final long HOLD_ON_DURATION_TICKS = 2 * 60 * 20; // 2 minutes
    /** Duration in ticks for On Your Paws revival process (8 seconds) */
    public static final long ON_YOUR_PAWS_DURATION_TICKS = 8 * 20; // 8s

    /** Cooldown in seconds for Hold On skill (10 minutes) */
    public static final long HOLD_ON_COOLDOWN_S = 600; // 2 minutes
    /** Cooldown in seconds for Prey Sense skill */
    public static final long PREY_SENSE_COOLDOWN_S = 20;
    /** Cooldown in seconds for Hunter's Compass skill */
    public static final long HUNTERS_COMPASS_COOLDOWN_S = 60;
    /** Cooldown in seconds for Low Sweep skill */
    public static final long LOW_SWEEP_COOLDOWN_S = 15;
    /** Cooldown in seconds for Pathfinding Boost skill */
    public static final long PATHFINDING_BOOST_COOLDOWN_S = 20;
    /** Cooldown in seconds for On Your Paws skill */
    public static final long ON_YOUR_PAWS_COOLDOWN_S = 60;
    /** Cooldown in seconds for Location Awareness skill */
    public static final long LOCATION_AWARENESS_COOLDOWN_S = 60;
    /** Base cooldown in seconds for Trail Memory skill */
    public static final long TRAIL_MEMORY_BASE_COOLDOWN_S = 80;
    /** Reduction in cooldown per tier for Trail Memory skill */
    public static final long TRAIL_MEMORY_TIER_VALUE_S = 20;

    /** 
     * Thread-safe map storing waypoint locations for each player.
     * Maps player UUID to their discovered waypoints with biome and location data.
     */
    public static final Map<UUID, Map<Waypoints, Pair<Biome, Location>>> STORED_WAYPOINTS =
            new ConcurrentHashMap<>();

    /** Thread-safe map tracking pending revival tasks for On Your Paws skill */
    private final Map<UUID, BukkitTask> pendingRevives = new ConcurrentHashMap<>();

    // Handling persistent items (actives skills and noteblock) management

    /**
     * Handles player respawn events to synchronize skill items in inventory.
     * Ensures that active skill items are properly restored after respawn.
     *
     * @param event the player respawn event
     */
    @EventHandler
    public void on(PlayerRespawnEvent event) {
        PlayersUtils.synchronizeInventory(event.getPlayer());
    }

    /**
     * Prevents players from dropping active skill items or note blocks.
     * Active skill items are persistent and should not be droppable.
     *
     * @param event the player item drop event
     */
    @EventHandler
    public void on(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();
        if (ItemsUtils.isActiveSkill(event.getPlayer(), dropped) || dropped.getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles player death events for Hold On skill activation and item management.
     * Removes active skill items from drops and attempts to activate Hold On if conditions are met.
     * 
     * <p>Hold On requires:</p>
     * <ul>
     *   <li>Player not already downed</li>
     *   <li>Hold On not on cooldown</li>
     *   <li>Clan member with Hold On skill within range</li>
     * </ul>
     *
     * @param event the player death event
     */
    @EventHandler
    public void on(PlayerDeathEvent event) {
        event.getDrops().removeIf(item -> ItemsUtils.isActiveSkill(event.getPlayer(), item));

        // Handling Resilience branch (downed state)

        Player dying = event.getEntity();

        if (PlayersUtils.isDowned(dying)) return;
        if (PlayersUtils.hasHoldOnOnCooldown(dying)) return;

        Optional<Player> protector = dying.getWorld().getPlayers().stream()
                .filter(p -> !p.equals(dying))
                .filter(p -> p.getLocation().distanceSquared(dying.getLocation()) <= HOLD_ON_RADIUS * HOLD_ON_RADIUS)
                .filter(p -> {
                    PlayerEntity pe = EventsCore.PLAYERS_CACHE.get(p.getUniqueId());
                    PlayerEntity dyingEntity = EventsCore.PLAYERS_CACHE.get(dying.getUniqueId());
                    return pe.hasAbility(Skills.HOLD_ON) && pe.getClan() == dyingEntity.getClan();
                })
                .findAny();

        if (protector.isEmpty()) return;

        Player helper = protector.get();

        event.setCancelled(true);
        dying.setHealth(1);
        dying.setFireTicks(0);
        dying.setFoodLevel(1);
        dying.setVelocity(dying.getVelocity().multiply(0));

        PlayersUtils.setDowned(dying, true);
        PlayersUtils.markHoldOnUsed(dying);

        helper.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_HOLD_ON + " " + dying.getName());
        dying.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_HOLD_ON);

        handleDownedState(dying, HOLD_ON_DURATION_TICKS);
    }

    /**
     * Handles player join events to restore downed state if applicable.
     * If a player was downed when they logged off, restores the downed state with remaining time.
     *
     * @param event the player join event
     */
    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PlayersUtils.isDowned(player)) {
            handleDownedState(player, PlayersUtils.getDownedCooldown(player));
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (PlayersUtils.isDowned(player)) {
            player.setSneaking(true);
            player.setWalkSpeed(0.05f);
            if (event.getFrom().getY() < event.getTo().getY()) {
                event.setTo(event.getFrom());
            }
        } else if (player.getWalkSpeed() < 0.1) {
            player.setWalkSpeed(0.2f);
        }

        if (EventsCore.PLAYERS_CACHE.get(player.getUniqueId()).hasAbility(Skills.LOCATION_AWARENESS)) {
            Biome currentBiome = player.getWorld().getBiome(player.getLocation());
            Map<Waypoints, Pair<Biome, Location>> stored = STORED_WAYPOINTS.get(player.getUniqueId());
            Optional<Waypoints> processing = Waypoints.getFromBiome(currentBiome);
            if (processing.isEmpty()) {
                return;
            }
            if (stored == null) {
                stored = new HashMap<>();
            }
            Pair<Biome, Location> storedLocation = stored.get(processing.get());
            if (storedLocation == null || storedLocation.getKey() != currentBiome) {
                storedLocation = Pair.of(currentBiome, player.getLocation());
                stored.put(processing.get(), storedLocation);
                STORED_WAYPOINTS.put(player.getUniqueId(), stored);
                player.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_LOCATION_AWARENESS_VISITED);
            }
        }
    }

    @EventHandler
    public void on(PlayerInCombatEvent event) {
        Player player = event.getPlayer();
        if (pendingRevives.containsKey(player.getUniqueId())) {
            pendingRevives.get(player.getUniqueId()).cancel();
            pendingRevives.remove(player.getUniqueId());
        }
    }

    // Handling active skills

    /**
     * Handles player interaction events for active skill usage.
     * Processes right-click interactions with active skill items and left-click for Trail Memory.
     * 
     * <p>Supported active skills:</p>
     * <ul>
     *   <li>Prey Sense - Right click to highlight nearby prey</li>
     *   <li>Hunter's Compass - Right click to point compass to nearest prey</li>
     *   <li>Low Sweep - Right click to charge and slow nearby enemies</li>
     *   <li>Pathfinding Boost - Right click for speed and jump boost</li>
     *   <li>On Your Paws - Right click to revive downed clan members</li>
     *   <li>Location Awareness - Right click to cycle through waypoints</li>
     *   <li>Trail Memory - Left click Location Awareness item to teleport</li>
     * </ul>
     *
     * @param event the player interact event
     */
    @EventHandler
    public void on(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (ItemsUtils.isEmpty(item) || !ItemsUtils.isActiveSkill(event.getPlayer(), item)) return;
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            for (Skills active : Skills.getActiveSkills()) {
                ItemStack activeSkillTemplate = ItemsUtils.getActiveSkill(event.getPlayer(), active);
                if (ItemsUtils.isSameItem(activeSkillTemplate, item)) {
                    switch (active) {
                        case PREY_SENSE -> handlePreySense(event);
                        case HUNTERS_COMPASS -> handleHuntersCompass(event);
                        case LOW_SWEEP -> handleLowSweep(event);
                        case PATHFINDING_BOOST -> handlePathfindingBoost(event);
                        case ON_YOUR_PAWS -> handleOnYourPaws(event);
                        case LOCATION_AWARENESS -> handleLocationAwareness(event);
                    }
                }
            }
            event.setCancelled(true);
        }
        if (event.getAction().toString().contains("LEFT_CLICK") &&
                ItemsUtils.isSameItem(ItemsUtils.getActiveSkill(event.getPlayer(), Skills.LOCATION_AWARENESS), item) &&
                EventsCore.PLAYERS_CACHE.get(event.getPlayer().getUniqueId()).hasAbility(Skills.TRAIL_MEMORY)) {
            handleTrailMemory(event);
            event.setCancelled(true);
        }
    }

    /**
     * Handles the Prey Sense active skill activation.
     * Applies glowing effect to all prey entities within the detection radius.
     *
     * @param event the player interact event that triggered this skill
     */
    private void handlePreySense(PlayerInteractEvent event) {
        withCooldown(() -> {
            Collection<LivingEntity> livingEntities = event.getPlayer().getWorld()
                    .getNearbyLivingEntities(event.getPlayer().getLocation(), PREY_SENSE_RADIUS);
            for (LivingEntity livingEntity : livingEntities) {
                if (Prey.isPrey(livingEntity)) {
                    livingEntity.addPotionEffect(
                            new PotionEffect(
                                    PotionEffectType.GLOWING,
                                    PREY_SENSE_DURATION_TICKS,
                                    0,
                                    false,
                                    false));
                }
            }
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), PREY_SENSE_COOLDOWN_S);
            event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_PREY_SENSE);
        }, event, false);
    }

    /**
     * Handles the Hunter's Compass active skill activation.
     * Sets the player's compass target to the nearest prey entity.
     *
     * @param event the player interact event that triggered this skill
     */
    private void handleHuntersCompass(PlayerInteractEvent event) {
        withCooldown(() -> {
            Optional<LivingEntity> shorter = Prey.getAllEntities().stream()
                    .min(Comparator.comparingDouble(prey ->
                            prey.getLocation().distanceSquared(event.getPlayer().getLocation())));
            shorter.ifPresent(entity -> {
                event.getPlayer().setCompassTarget(entity.getLocation());
                ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), HUNTERS_COMPASS_COOLDOWN_S);
                event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_HUNTERS_COMPASS);
            });
        }, event, false);
    }

    /**
     * Handles the Low Sweep active skill activation.
     * Charges for 1 second, then applies slowness to the nearest enemy within range.
     * Includes visual and audio effects.
     *
     * @param event the player interact event that triggered this skill
     */
    private void handleLowSweep(PlayerInteractEvent event) {
        withCooldown(() -> {
            event.getPlayer().sendMessage(ChatColor.GOLD + MessagesConf.Skills.PLAYER_MESSAGE_PREPARE_LOW_SWEEP);

            Bukkit.getScheduler().runTaskLater(
                    PawsOfTheForest.getInstance(),
                    () -> {
                        Optional<LivingEntity> shorter = event.getPlayer().getWorld()
                                .getNearbyLivingEntities(event.getPlayer().getLocation(), LOW_SWEEP_RANGE).stream()
                                .filter(entity -> !entity.getUniqueId().equals(event.getPlayer().getUniqueId()))
                                .min(Comparator.comparingDouble(entity ->
                                        entity.getLocation().distanceSquared(event.getPlayer().getLocation())));
                        if (shorter.isEmpty()) {
                            event.getPlayer().sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_LOW_SWEEP_NO_TARGET);
                            return;
                        }

                        LivingEntity target = shorter.get();
                        target.addPotionEffect(new PotionEffect(
                                PotionEffectType.SLOWNESS,
                                LOW_SWEEP_SLOWNESS_DURATION_TICKS,
                                1,
                                false,
                                true
                        ));

                        target.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 1);
                        target.getWorld().playSound(target.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
                        event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_LOW_SWEEP);
                        ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), LOW_SWEEP_COOLDOWN_S);
                    },
                    LOW_SWEEP_CHARGE_DELAY_TICKS
            );
        }, event, false);
    }

    /**
     * Handles the Pathfinding Boost active skill activation.
     * Provides speed and jump boost effects for improved movement.
     * Cannot be used while in combat.
     *
     * @param event the player interact event that triggered this skill
     */
    private void handlePathfindingBoost(PlayerInteractEvent event) {
        withCooldown(() -> {
            if (EventsCore.PLAYERS_FIGHTING.contains(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_IN_COMBAT);
                return;
            }
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PATHFINDING_BOOST_DURATION_TICKS, 0, false, false));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, PATHFINDING_BOOST_DURATION_TICKS, 0, false, false));
            event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_PATHFINDING_BOOST);
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), PATHFINDING_BOOST_COOLDOWN_S);
        }, event, false);
    }

    /**
     * Handles the On Your Paws active skill activation.
     * Revives a downed clan member after an 8-second channeling period.
     * 
     * <p>Requirements:</p>
     * <ul>
     *   <li>User not in combat</li>
     *   <li>Target is a downed player</li>
     *   <li>Target is in the same clan as the user</li>
     * </ul>
     *
     * @param event the player interact event that triggered this skill
     */
    private void handleOnYourPaws(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (EventsCore.PLAYERS_FIGHTING.contains(player)) {
            player.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_IN_COMBAT);
            return;
        }

        Entity targetEntity = player.getTargetEntity(25);
        if (!(targetEntity instanceof Player target)) {
            return;
        }

        if (!PlayersUtils.isDowned(target)) {
            return;
        }

        if (EventsCore.PLAYERS_CACHE.get(player.getUniqueId()).getClan() !=
                EventsCore.PLAYERS_CACHE.get(target.getUniqueId()).getClan()) {
            player.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_ON_YOUR_PAWS_NOT_IN_CLAN);
            return;
        }

        withCooldown(() -> {
            player.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_ON_YOUR_PAWS + " " + target.getName());
            BukkitTask task = Bukkit.getScheduler().runTaskLater(PawsOfTheForest.getInstance(), () -> {
                pendingRevives.remove(player.getUniqueId());
                if (!player.isOnline() || !target.isOnline()) return;
                PlayersUtils.setDowned(target, false);
                target.setHealth(4);
                target.setFoodLevel(10);
                target.setFireTicks(0);
                target.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_ON_YOUR_PAWS_REVIVED);
                ItemsUtils.setCooldown(player, event.getItem(), ON_YOUR_PAWS_COOLDOWN_S);
            }, ON_YOUR_PAWS_DURATION_TICKS);
            pendingRevives.put(player.getUniqueId(), task);
        }, event, false);
    }

    /**
     * Handles the Location Awareness active skill activation.
     * Cycles through stored waypoints and sets compass to the next available waypoint.
     *
     * @param event the player interact event that triggered this skill
     */
    private void handleLocationAwareness(PlayerInteractEvent event) {
        Map<Waypoints, Pair<Biome, Location>> stored = STORED_WAYPOINTS.get(event.getPlayer().getUniqueId());
        if (stored == null) {
            event.getPlayer().sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_LOCATION_AWARENESS_NO_WAYPOINT);
            return;
        }
        withCooldown(() -> {
            int currentIndex = PlayersUtils.getWaypointIndex(event.getPlayer());
            currentIndex++;
            Waypoints nextWaypoint = Waypoints.getFromIndex(currentIndex);
            while (!stored.containsKey(nextWaypoint)) {
                currentIndex++;
                nextWaypoint = Waypoints.getFromIndex(currentIndex);
                if (stored.containsKey(nextWaypoint)) {
                    break;
                } else if (currentIndex >= Waypoints.values().length - 1) {
                    currentIndex = 0;
                    nextWaypoint = Waypoints.getFromIndex(currentIndex);
                }
            }

            PlayersUtils.setWaypointIndex(event.getPlayer(), currentIndex);

            Pair<Biome, Location> location = stored.get(nextWaypoint);

            event.getPlayer().setCompassTarget(location.getValue());
            event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_LOCATION_AWARENESS + " " + nextWaypoint);
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), LOCATION_AWARENESS_COOLDOWN_S);
        }, event, false);
    }

    /**
     * Handles the Trail Memory active skill activation.
     * Teleports the player to their currently selected waypoint location.
     * Cooldown is reduced based on skill tier.
     *
     * @param event the player interact event that triggered this skill
     */
    private void handleTrailMemory(PlayerInteractEvent event) {
        Map<Waypoints, Pair<Biome, Location>> stored = STORED_WAYPOINTS.get(event.getPlayer().getUniqueId());
        if (stored == null) {
            event.getPlayer().sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_LOCATION_AWARENESS_NO_WAYPOINT);
            return;
        }
        int currentIndex = PlayersUtils.getWaypointIndex(event.getPlayer());
        if (currentIndex < 0) {
            event.getPlayer().sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_TRAIL_MEMORY_NO_WAYPOINT);
            return;
        }
        withCooldown(() -> {
            Waypoints waypoint = Waypoints.getFromIndex(currentIndex);
            Pair<Biome, Location> location = stored.get(waypoint);
            event.getPlayer().teleport(location.getValue());
            event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_TRAIL_MEMORY + " " + waypoint);

            int tier = EventsCore.PLAYERS_CACHE.get(event.getPlayer().getUniqueId()).getAbilityTier(Skills.TRAIL_MEMORY);
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), TRAIL_MEMORY_BASE_COOLDOWN_S - TRAIL_MEMORY_TIER_VALUE_S * tier, ItemsUtils.META_COOLDOWN_SECONDARY_KEY);
        }, event, true);
    }

    /**
     * Manages the downed state for a player.
     * After the specified delay, if the player is still downed, they die permanently.
     *
     * @param dying the player in the downed state
     * @param delay the time in ticks before permanent death
     */
    private void handleDownedState(Player dying, long delay) {
        Bukkit.getScheduler().runTaskLater(PawsOfTheForest.getInstance(), () -> {
            if (!dying.isOnline()) return;
            if (PlayersUtils.isDowned(dying)) {
                dying.setHealth(0);
                PlayersUtils.setDowned(dying, false);
                dying.sendMessage(ChatColor.DARK_RED + MessagesConf.Skills.PLAYER_MESSAGE_HOLD_ON_SUCCUMBED);
            }
        }, delay);
    }

    /**
     * Executes a skill action if the cooldown has expired, otherwise shows cooldown message.
     *
     * @param runnable the skill action to execute
     * @param event the player interact event
     * @param secondary whether to use the secondary cooldown key for multi-function items
     */
    private void withCooldown(Runnable runnable, PlayerInteractEvent event, boolean secondary) {
        if (ItemsUtils.checkForCooldown(event.getPlayer(), event.getItem(),
                secondary ? ItemsUtils.META_COOLDOWN_SECONDARY_KEY : ItemsUtils.META_COOLDOWN_KEY)) {
            runnable.run();
        } else {
            event.getPlayer().sendMessage(ChatColor.RED +
                    MessagesConf.Skills.PLAYER_MESSAGE_COOLDOWN + " " +
                    ItemsUtils.getCooldown(event.getPlayer(), event.getItem()) + "s");
        }
    }
}
