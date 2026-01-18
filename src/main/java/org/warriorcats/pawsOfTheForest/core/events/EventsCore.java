package org.warriorcats.pawsOfTheForest.core.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.chats.commands.CommandToggleChat;
import org.warriorcats.pawsOfTheForest.utils.*;
import org.warriorcats.pawsOfTheForest.vitals.HUD;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.bukkit.potion.PotionEffectType.*;

/**
 * Core event listener for the Paws of the Forest plugin that handles fundamental game mechanics.
 * This class manages player lifecycle events, combat tracking, chat system integration,
 * custom event dispatching, and player state management.
 * 
 * <p>Key responsibilities include:</p>
 * <ul>
 *   <li>Player join/quit handling with database synchronization</li>
 *   <li>Combat state tracking and timeout management</li>
 *   <li>Custom event dispatching (fear, freeze, combat events)</li>
 *   <li>Chat system integration and redirection</li>
 *   <li>Player caching for performance optimization</li>
 * </ul>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 */
public class EventsCore implements Listener {

    /**
     * The delay in seconds before a player is considered out of combat.
     * After this time elapses without taking or dealing damage, the PlayerOutCombatEvent is fired.
     */
    public static final int FIGHTING_PLAYERS_SCAN_DELAY_S = 10;

    /**
     * Thread-safe set containing all players currently engaged in combat.
     * Players are added when they take or deal damage and removed after the combat timeout.
     */
    public static final Set<Player> PLAYERS_FIGHTING = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    /**
     * Thread-safe set containing players who are in the process of leaving the server.
     * Used to prevent race conditions during player quit events.
     */
    public static final Set<Player> PLAYERS_LEAVING = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    /**
     * Cache mapping player UUIDs to their corresponding PlayerEntity objects.
     * This cache improves performance by avoiding frequent database queries.
     */
    public static final Map<UUID, PlayerEntity> PLAYERS_CACHE = new ConcurrentHashMap<>();

    /**
     * Set of potion effects that are considered "fear effects".
     * When a player receives any of these effects, a PlayerFearEvent is triggered.
     */
    public static final Set<PotionEffectType> FEAR_EFFECTS = Set.of(
            BLINDNESS,
            NAUSEA,
            SLOWNESS
    );

    /**
     * Handles player join events by initializing or loading player data from the database.
     * 
     * <p>This method performs the following actions:</p>
     * <ul>
     *   <li>Creates a new PlayerEntity if the player is joining for the first time</li>
     *   <li>Initializes default settings and skill branches for new players</li>
     *   <li>Applies existing skill effects for returning players (Iron Hide, Hard Knock Life)</li>
     *   <li>Caches the player entity for performance</li>
     *   <li>Opens the HUD interface</li>
     *   <li>Sets the resource pack</li>
     *   <li>Configures default chat channel</li>
     * </ul>
     * 
     * @param event The PlayerJoinEvent containing the joining player
     */
    @EventHandler
    public void on(PlayerJoinEvent event) {
        // Saving player data at joining if it does not exist
        HibernateUtils.withSession(session -> {
            PlayerEntity existing = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());

            if (existing == null) {
                session.beginTransaction();
                existing = new PlayerEntity();
                existing.setUuid(event.getPlayer().getUniqueId());
                existing.setName(event.getPlayer().getName());
                existing.setBirthDate(Instant.now());
                existing.setSettings(new SettingsEntity());
                // Initialize all skill branches for new players
                for (SkillBranches branche : SkillBranches.values()) {
                    SkillBranchEntity brancheEntity = new SkillBranchEntity();
                    brancheEntity.setBranch(branche);
                    existing.getSkillBranches().add(brancheEntity);
                }
                session.persist(existing);
                session.getTransaction().commit();
            } else {
                // Apply existing skill effects for returning players
                if (existing.hasAbility(Skills.IRON_HIDE)) {
                    SkillsUtils.updateIronHideArmor(event.getPlayer(), existing.getAbilityTier(Skills.IRON_HIDE));
                }
                if (existing.hasAbility(Skills.HARD_KNOCK_LIFE)) {
                    SkillsUtils.updateHardKnockLifeArmor(event.getPlayer());
                }
            }
            PLAYERS_CACHE.put(event.getPlayer().getUniqueId(), existing);
        });

        // Synchronizing inventory
        PlayersUtils.synchronizeInventory(event.getPlayer());

        // Toggling HUD
        HUD.open(event.getPlayer());

        // Toggling resources pack
        event.getPlayer().setResourcePack("http://localhost:" + HttpServerUtils.RESOURCES_PACK_PORT + "/" + FileUtils.RESOURCES_PACK_PATH);

        // Toggling default chat
        CommandToggleChat.setToggledChat(event.getPlayer(), ChatChannels.DEFAULT_TOGGLED);
    }

    /**
     * Handles chat events by redirecting messages to the player's currently toggled chat channel.
     * 
     * <p>This method cancels the original chat event and redirects the message to the appropriate
     * chat channel based on the player's current chat settings. The message is processed
     * synchronously to ensure proper command execution.</p>
     * 
     * @param event The AsyncChatEvent containing the chat message and player
     */
    @EventHandler
    public void on(AsyncChatEvent event) {

        event.setCancelled(true);

        ChatChannels chatToggled = CommandToggleChat.getToggledChat(event.getPlayer());
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
            Bukkit.dispatchCommand(event.getPlayer(), chatToggled.name().toLowerCase() + " " + message);
        });
    }

    /**
     * Handles player movement events to detect specific environmental triggers.
     * 
     * <p>Currently monitors for players entering powder snow blocks, which triggers
     * a PlayerFreezeEvent for freeze-related game mechanics.</p>
     * 
     * @param event The PlayerMoveEvent containing movement information
     */
    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Block block = player.getLocation().getBlock();
        if (block.getType() == Material.POWDER_SNOW) {
            Bukkit.getPluginManager().callEvent(new PlayerFreezeEvent(player));
        }
    }


    /**
     * Handles entity damage events to track combat state and trigger combat-related events.
     * 
     * <p>This method manages the combat system by:</p>
     * <ul>
     *   <li>Adding players to the fighting set when they deal or receive damage</li>
     *   <li>Triggering PlayerInCombatEvent when combat begins</li>
     *   <li>Scheduling PlayerOutCombatEvent after the combat timeout period</li>
     *   <li>Preventing duplicate combat events for already fighting players</li>
     * </ul>
     * 
     * @param event The EntityDamageByEntityEvent containing damage information
     */
    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        // Define combat logic: trigger in-combat event and schedule out-of-combat event
        Consumer<Player> consumer = player -> {
            Bukkit.getPluginManager().callEvent(new PlayerInCombatEvent(player));
            new BukkitRunnable() {
                @Override
                public void run() {
                    PLAYERS_FIGHTING.remove(player);
                    Bukkit.getPluginManager().callEvent(new PlayerOutCombatEvent(player));
                }
            }.runTaskLater(PawsOfTheForest.getInstance(), 20 * FIGHTING_PLAYERS_SCAN_DELAY_S);
        };

        // Handle damager (attacker) combat state
        if (event.getDamager() instanceof Player damager && !PLAYERS_FIGHTING.contains(damager)) {
            PLAYERS_FIGHTING.add(damager);
            consumer.accept(damager);
        }

        // Handle victim combat state
        if (event.getEntity() instanceof Player victim && !PLAYERS_FIGHTING.contains(victim)) {
            PLAYERS_FIGHTING.add(victim);
            consumer.accept(victim);
        }
    }

    /**
     * Handles player quit events by cleaning up player data and managing logout state.
     * 
     * <p>This method performs cleanup operations:</p>
     * <ul>
     *   <li>Removes the player from the entity cache</li>
     *   <li>Adds the player to the leaving set to prevent race conditions</li>
     *   <li>Schedules removal from the leaving set after a delay</li>
     * </ul>
     * 
     * @param event The PlayerQuitEvent containing the leaving player
     */
    @EventHandler
    public void on(PlayerQuitEvent event) {
        PLAYERS_CACHE.remove(event.getPlayer().getUniqueId());
        PLAYERS_LEAVING.add(event.getPlayer());

        // Schedule cleanup of leaving player status after 5 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                PLAYERS_LEAVING.remove(event.getPlayer());
            }
        }.runTaskLater(PawsOfTheForest.getInstance(), 5 * 20);
    }

    /**
     * Handles potion effect events to trigger fear-related game mechanics.
     * 
     * <p>This method monitors for specific potion effects that are classified as "fear effects"
     * (blindness, nausea, slowness) and triggers a PlayerFearEvent when these effects are applied
     * to players.</p>
     * 
     * @param event The EntityPotionEffectEvent containing potion effect information
     */
    @EventHandler
    public void on(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getNewEffect() == null) return;

        if (FEAR_EFFECTS.contains(event.getNewEffect().getType())) {
            Bukkit.getPluginManager().callEvent(new PlayerFearEvent(player));
        }
    }
}
