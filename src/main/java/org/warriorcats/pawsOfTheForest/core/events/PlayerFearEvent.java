package org.warriorcats.pawsOfTheForest.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event that is triggered when a player experiences fear-inducing effects.
 * 
 * <p>This event is fired when a player receives specific potion effects that are
 * classified as "fear effects" (blindness, nausea, slowness). Other plugins or
 * systems can listen to this event to implement fear-related mechanics such as:</p>
 * 
 * <ul>
 *   <li>Visual or audio cues to enhance the fear experience</li>
 *   <li>Skill-based resistance or immunity to fear</li>
 *   <li>Statistical tracking of fear events</li>
 *   <li>Special abilities that trigger on fear</li>
 * </ul>
 * 
 * <p><strong>Trigger Conditions:</strong></p>
 * <p>This event is automatically triggered by {@link EventsCore} when a player
 * receives any of the following potion effects:</p>
 * <ul>
 *   <li>Blindness</li>
 *   <li>Nausea</li>
 *   <li>Slowness</li>
 * </ul>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 * @see EventsCore#FEAR_EFFECTS
 */
@Getter
@RequiredArgsConstructor
public class PlayerFearEvent extends Event {
    
    /**
     * Static handler list required for Bukkit event system.
     */
    private static final HandlerList HANDLERS = new HandlerList();
    
    /**
     * The player who is experiencing the fear effect.
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
