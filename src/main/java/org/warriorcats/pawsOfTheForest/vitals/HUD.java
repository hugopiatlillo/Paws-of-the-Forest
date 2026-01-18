package org.warriorcats.pawsOfTheForest.vitals;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Heads-Up Display (HUD) manager for displaying player vitals in the Paws of the Forest plugin.
 * <p>
 * This class provides a visual interface for players to monitor their vital statistics including
 * thirst, energy, hygiene, and social levels. The HUD uses Minecraft's boss bar system to display
 * vitals as custom icons with progress indicators, and the action bar for social statistics.
 * </p>
 * <p>
 * The display includes:
 * - Boss bars with custom Unicode icons for thirst, energy, and hygiene
 * - Action bar display for social statistics with percentage
 * - Real-time updates when vitals change
 * - Automatic cleanup of old boss bars when players disconnect
 * </p>
 * 
 * @author Warriors Cats Team
 * @version 1.0
 * @since 1.0
 */
public abstract class HUD {

    /** Unicode character for full thirst icon in resource pack */
    public static final char THIRST_FULL_ICON = '\uE000';
    
    /** Unicode character for empty thirst icon in resource pack */
    public static final char THIRST_EMPTY_ICON = '\uE001';
    
    /** Unicode character for full energy icon in resource pack */
    public static final char ENERGY_FULL_ICON = '\uE002';
    
    /** Unicode character for empty energy icon in resource pack */
    public static final char ENERGY_EMPTY_ICON = '\uE003';
    
    /** Unicode character for full hygiene icon in resource pack */
    public static final char HYGIENE_FULL_ICON = '\uE004';
    
    /** Unicode character for empty hygiene icon in resource pack */
    public static final char HYGIENE_EMPTY_ICON = '\uE005';
    
    /** Unicode character for social icon in resource pack */
    public static final char SOCIAL_ICON = '\uE006';

    /** Maps player UUIDs to their main HUD boss bar keys */
    private static final Map<UUID, NamespacedKey> PROGRESS_BARS = new ConcurrentHashMap<>();
    
    /** Maps player UUIDs to their dummy boss bar keys used for vertical spacing */
    private static final Map<UUID, NamespacedKey> DUMMY_BARS = new ConcurrentHashMap<>();

    /** Maps player UUIDs to their social action bar update tasks */
    private static final Map<UUID, BukkitTask> SOCIAL_ACTION_BAR_TASKS = new ConcurrentHashMap<>();

    /**
     * Opens the HUD interface for a player by clearing any existing boss bars and initializing the vitals display.
     * <p>
     * This method should be called when a player joins the server or when the HUD needs to be reset.
     * It ensures a clean slate by removing the player from all existing boss bars before setting up new ones.
     * </p>
     * 
     * @param player the player to open the HUD interface for
     */
    public static void open(Player player) {
        // Remove player from all existing boss bars to prevent conflicts
        for (@NotNull Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            BossBar b = it.next();
            b.removePlayer(player);
        }
        updateInterface(player);
    }

    /**
     * Updates the HUD interface for a player with their current vital statistics.
     * <p>
     * This method refreshes the display to show current values for:
     * - Thirst, energy, and hygiene as boss bars with progress indicators
     * - Social statistics as action bar text with percentage
     * </p>
     * <p>
     * The method handles boss bar creation, updates, and task management for real-time display.
     * </p>
     * 
     * @param player the player whose HUD interface to update
     */
    public static void updateInterface(Player player) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());

        // Create the main HUD display text with all vital bars
        String barText = createPartialBar(entity.getThirst(), THIRST_FULL_ICON, THIRST_EMPTY_ICON) + "  " +
                createPartialBar(entity.getEnergy(), ENERGY_FULL_ICON, ENERGY_EMPTY_ICON) + "  " +
                createPartialBar(entity.getHygiene(), HYGIENE_FULL_ICON, HYGIENE_EMPTY_ICON);

        UUID uuid = player.getUniqueId();

        // Create or update dummy bar for vertical spacing in the display
        NamespacedKey dummyKey = DUMMY_BARS.computeIfAbsent(uuid, id -> NamespacedKey.minecraft("hud_dummy_" + id));
        BossBar dummy = Bukkit.getBossBar(dummyKey);
        if (dummy == null) {
            dummy = Bukkit.createBossBar(dummyKey, " ", BarColor.WHITE, BarStyle.SEGMENTED_20);
            dummy.addPlayer(player);
        } else {
            if (!dummy.getPlayers().contains(player)) {
                dummy.addPlayer(player);
            }
        }

        // Create or update the main HUD boss bar with vitals display
        NamespacedKey key = PROGRESS_BARS.computeIfAbsent(uuid, id -> NamespacedKey.minecraft("hud_" + id));
        BossBar bar = Bukkit.getBossBar(key);
        if (bar == null) {
            bar = Bukkit.createBossBar(key, barText, BarColor.WHITE, BarStyle.SEGMENTED_20);
            bar.addPlayer(player);
        } else {
            bar.setTitle(barText);
            if (!bar.getPlayers().contains(player)) {
                bar.addPlayer(player);
            }
        }

        // Setup social vital display in action bar with percentage
        String socialText = SOCIAL_ICON + "  " + (int) (entity.getSocial() * 100) + "%";

        // Cancel any existing social action bar task to prevent duplicates
        BukkitTask previous = SOCIAL_ACTION_BAR_TASKS.remove(uuid);
        if (previous != null && !previous.isCancelled()) {
            previous.cancel();
        }
        
        // Start new task to continuously update social display every second
        SOCIAL_ACTION_BAR_TASKS.put(uuid, new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                player.sendActionBar(Component.text(socialText));
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0L, 20L));
    }

    /**
     * Creates a visual progress bar string using custom icons to represent a vital's current level.
     * <p>
     * The bar consists of 10 segments total, where each segment shows either a full or empty icon
     * based on the vital's current percentage. The value is clamped between 0.0 and 1.0.
     * </p>
     * 
     * @param value the vital value as a decimal between 0.0 and 1.0
     * @param fullChar the Unicode character to use for filled segments
     * @param emptyChar the Unicode character to use for empty segments
     * @return a string representing the progress bar with appropriate filled and empty segments
     */
    private static String createPartialBar(double value, char fullChar, char emptyChar) {
        int total = 10; // Total number of segments in the progress bar
        
        // Clamp value between 0.0 and 1.0 to prevent invalid displays
        value = Math.max(0.0, Math.min(1.0, value));
        
        // Calculate filled and empty segments
        int filled = (int) Math.round(value * total);
        int empty = total - filled;
        
        // Build the progress bar string with repeated characters
        return String.valueOf(fullChar).repeat(filled) + String.valueOf(emptyChar).repeat(empty);
    }
}