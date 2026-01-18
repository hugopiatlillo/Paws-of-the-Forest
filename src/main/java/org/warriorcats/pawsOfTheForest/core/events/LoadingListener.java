package org.warriorcats.pawsOfTheForest.core.events;

import org.bukkit.event.Listener;

/**
 * Interface for event listeners that require initialization during plugin loading.
 * 
 * <p>This interface extends the standard Bukkit {@link Listener} interface and adds
 * a load method that should be called during the plugin's initialization phase.
 * Implementing classes can use this method to perform setup operations, register
 * additional listeners, or initialize resources that are needed before the listener
 * starts handling events.</p>
 * 
 * <p>Typical use cases include:</p>
 * <ul>
 *   <li>Registering additional event handlers dynamically</li>
 *   <li>Loading configuration data required for event processing</li>
 *   <li>Initializing data structures or caches</li>
 *   <li>Setting up scheduled tasks or background processes</li>
 * </ul>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 */
public interface LoadingListener extends Listener {
    
    /**
     * Called during plugin initialization to perform any required setup operations.
     * 
     * <p>This method is invoked by the plugin loader before the listener begins
     * handling events. Implementations should use this method to initialize any
     * resources, register additional listeners, or perform configuration that
     * is necessary for proper event handling.</p>
     * 
     * <p>If initialization fails, implementations should throw appropriate
     * exceptions to indicate the failure to the plugin loader.</p>
     * 
     * @throws Exception if initialization fails for any reason
     */
    void load();
}
