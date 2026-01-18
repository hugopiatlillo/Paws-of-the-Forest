package org.warriorcats.pawsOfTheForest.core.settings;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;

import java.util.UUID;

/**
 * JPA entity representing player-specific settings stored in the database.
 * 
 * <p>This entity maintains persistent configuration preferences for individual players,
 * including chat-related settings and other customizable options. Each player has
 * exactly one SettingsEntity associated with their unique identifier.</p>
 * 
 * <p>The entity is automatically managed by Hibernate and includes default values
 * for all settings to ensure new players have a consistent initial experience.</p>
 * 
 * <p>Current settings include:</p>
 * <ul>
 *   <li><strong>Roleplay Visibility:</strong> Controls whether roleplay chat channels
 *       are available to the player</li>
 *   <li><strong>Chat Channel Selection:</strong> The player's currently active chat channel
 *       for message routing</li>
 * </ul>
 * 
 * @author Paws of the Forest Development Team
 * @version 1.0
 * @since 1.0
 * @see ChatChannels
 */
@Data
@Entity
@Table(name = "player_settings")
public class SettingsEntity {

    /**
     * The unique identifier for this settings record, corresponding to the player's UUID.
     * 
     * <p>This serves as both the primary key for the database table and the link
     * to the specific player these settings belong to. A random UUID is generated
     * by default, but this should be overridden with the actual player UUID when
     * the entity is created.</p>
     */
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid = UUID.randomUUID();

    /**
     * Whether roleplay chat channels should be visible and accessible to this player.
     * 
     * <p>When set to {@code false}, roleplay-specific chat channels will be hidden
     * from the player's chat options and they will be unable to participate in
     * roleplay conversations. Defaults to {@code true} to encourage roleplay participation.</p>
     * 
     * <p>If this setting is disabled while the player's active chat channel is a
     * roleplay channel, their chat selection will automatically revert to the default channel.</p>
     */
    @Column(name = "show_roleplay")
    private boolean showRoleplay = true;

    /**
     * The chat channel currently selected by the player for sending messages.
     * 
     * <p>This determines where the player's chat messages will be routed when they type
     * in chat. The available options may be filtered based on other settings (such as
     * roleplay visibility) and player permissions (such as clan membership).</p>
     * 
     * <p>Defaults to the system-defined default chat channel to ensure consistent
     * behavior for new players.</p>
     * 
     * @see ChatChannels#DEFAULT_TOGGLED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "toggled_chat")
    private ChatChannels toggledChat = ChatChannels.DEFAULT_TOGGLED;
}
