package org.warriorcats.pawsOfTheForest.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event that is triggered when a player exits combat.
 * 
 * <p>This event is fired when a player has been in combat but has not dealt or
 * received damage for the configured timeout period. This marks the end of their
 * combat state. Other plugins or systems can listen to this event to implement
 * post-combat mechanics such as:</p>
 * 
 * <ul>
 *   <li>Re-enabling teleportation or restricted commands</li>
 *   <li>Removing combat-specific buffs or debuffs</li>
 *   <li>Logging combat session completion for statistics</li>
 *   <li>Restoring normal UI elements or HUD state</li>
 *   <li>Stopping combat music or sound effects</li>
 *   <li>Re-enabling logout without penalties</li>
 *   <li>Applying post-combat healing or restoration effects</li>
 * </ul>
 * 
 * <p><strong>Trigger Conditions:</strong></p>
 * <p>This event is automatically triggered by {@link EventsCore} when:</p>
 * <ul>
 *   <li>A player has been marked as in combat (via {@link PlayerInCombatEvent})</li>
 *   <li>{@value EventsCore#FIGHTING_PLAYERS_SCAN_DELAY_S} seconds have elapsed since
 *       the player last dealt or received damage</li>
 *   <li>The player is removed from the active combat tracking set</li>
 * </ul>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 * @see PlayerInCombatEvent
 * @see EventsCore#FIGHTING_PLAYERS_SCAN_DELAY_S
 * @see EventsCore#PLAYERS_FIGHTING
 */
@Getter
@RequiredArgsConstructor
public class PlayerOutCombatEvent extends Event {
    
    /**
     * Static handler list required for Bukkit event system.
     */
    private static final HandlerList HANDLERS = new HandlerList();
    
    /**
     * The player who has exited combat.
     */
    private final Player player;

    /**
     * Gets the handler list for this event.
     * Required by Bukkit's event system for proper event handling.
     * 
     * @return The handler list for this event
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the static handler list for this event.
     * Required by Bukkit's event system for proper event registration.
     * 
     * @return The static handler list for this event
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
