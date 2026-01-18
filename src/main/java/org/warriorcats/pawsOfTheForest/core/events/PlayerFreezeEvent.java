package org.warriorcats.pawsOfTheForest.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event that is triggered when a player enters a freezing environment.
 * 
 * <p>This event is fired when a player moves into environmental conditions that
 * cause freezing, such as entering powder snow blocks. Other plugins or systems
 * can listen to this event to implement freeze-related mechanics such as:</p>
 * 
 * <ul>
 *   <li>Temperature-based damage over time</li>
 *   <li>Movement speed reduction or temporary immobilization</li>
 *   <li>Visual effects like ice particles or frost overlay</li>
 *   <li>Skill-based resistance or immunity to freezing</li>
 *   <li>Equipment durability loss from extreme cold</li>
 *   <li>Special abilities that activate in cold environments</li>
 * </ul>
 * 
 * <p><strong>Trigger Conditions:</strong></p>
 * <p>This event is automatically triggered by {@link EventsCore} when a player's
 * location block is detected as {@code Material.POWDER_SNOW} during movement.</p>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 * @see EventsCore#on(org.bukkit.event.player.PlayerMoveEvent)
 */
@Getter
@RequiredArgsConstructor
public class PlayerFreezeEvent extends Event {
    
    /**
     * Static handler list required for Bukkit event system.
     */
    private static final HandlerList HANDLERS = new HandlerList();
    
    /**
     * The player who is experiencing the freezing conditions.
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
