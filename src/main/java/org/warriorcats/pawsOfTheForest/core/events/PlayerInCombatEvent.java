package org.warriorcats.pawsOfTheForest.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event that is triggered when a player enters combat.
 * 
 * <p>This event is fired when a player either deals damage to another entity or
 * receives damage from another entity, marking the beginning of their combat state.
 * Other plugins or systems can listen to this event to implement combat-related
 * mechanics such as:</p>
 * 
 * <ul>
 *   <li>Preventing teleportation or certain commands during combat</li>
 *   <li>Applying combat-specific buffs or debuffs</li>
 *   <li>Logging combat events for administrative purposes</li>
 *   <li>Activating combat-specific UI elements or HUD changes</li>
 *   <li>Triggering combat music or sound effects</li>
 *   <li>Preventing logout or applying logout penalties</li>
 * </ul>
 * 
 * <p><strong>Trigger Conditions:</strong></p>
 * <p>This event is automatically triggered by {@link EventsCore} when:</p>
 * <ul>
 *   <li>A player deals damage to another entity (as the damager)</li>
 *   <li>A player receives damage from another entity (as the victim)</li>
 *   <li>The player is not already marked as being in combat</li>
 * </ul>
 * 
 * <p>Once triggered, the player will remain in combat for {@value EventsCore#FIGHTING_PLAYERS_SCAN_DELAY_S}
 * seconds, after which a {@link PlayerOutCombatEvent} will be fired.</p>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 * @see PlayerOutCombatEvent
 * @see EventsCore#FIGHTING_PLAYERS_SCAN_DELAY_S
 * @see EventsCore#PLAYERS_FIGHTING
 */
@Getter
@RequiredArgsConstructor
public class PlayerInCombatEvent extends Event {
    
    /**
     * Static handler list required for Bukkit event system.
     */
    private static final HandlerList HANDLERS = new HandlerList();
    
    /**
     * The player who has entered combat.
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
