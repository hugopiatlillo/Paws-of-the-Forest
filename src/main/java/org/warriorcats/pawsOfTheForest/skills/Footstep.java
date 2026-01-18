package org.warriorcats.pawsOfTheForest.skills;

import org.bukkit.Location;

/**
 * Record representing a player's footstep for tracking purposes.
 * 
 * <p>Used by the Tracker passive skill to create visual trails of player movement.
 * Footsteps are automatically cleaned up after 5 seconds to prevent memory buildup.</p>
 * 
 * @param location the world location where the footstep occurred
 * @param timestamp the system time in milliseconds when the footstep was created
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
public record Footstep(Location location, long timestamp) {
}
