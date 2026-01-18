package org.warriorcats.pawsOfTheForest.core.chats;

import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;

/**
 * Enumeration of all available chat channels in the roleplay system.
 * 
 * The chat system provides different channels for various types of communication:
 * - GLOBAL: Server-wide chat visible to all players
 * - LOCAL: Location-based chat with limited radius (50 blocks)
 * - CLAN: Private chat for clan members only
 * - ROLEPLAY: Global roleplay chat for in-character communication
 * - LOCALROLEPLAY: Local roleplay chat for nearby in-character communication
 * 
 * Players can toggle between channels to switch their default chat mode.
 * The LOCAL_CHANNEL_RADIUS constant defines the distance limit for local chats.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
public enum ChatChannels {
    /** Server-wide chat visible to all players */
    GLOBAL, 
    /** Location-based chat with limited radius */
    LOCAL, 
    /** Private chat for clan members only */
    CLAN, 
    /** Global roleplay chat for in-character communication */
    ROLEPLAY, 
    /** Local roleplay chat for nearby in-character communication */
    LOCALROLEPLAY;

    /** The default chat channel when players first join */
    public static final ChatChannels DEFAULT_TOGGLED = GLOBAL;
    /** The radius in blocks for local chat channels */
    public static final int LOCAL_CHANNEL_RADIUS = 50;

    /**
     * Parses a chat channel from its string representation.
     * 
     * @param channelStr The string representation of the channel
     * @return The corresponding ChatChannels enum value
     * @throws IllegalArgumentException if the string doesn't match any channel
     */
    public static ChatChannels from(String channelStr) {
        return EnumsUtils.from(channelStr, ChatChannels.class);
    }

    /**
     * Returns the display name of the chat channel.
     * Special handling for LOCALROLEPLAY to show as "LOCAL ROLEPLAY".
     * 
     * @return The formatted channel name
     */
    @Override
    public String toString() {
        if (this == LOCALROLEPLAY) {
            return "LOCAL ROLEPLAY";
        }
        return super.toString();
    }

    /**
     * Checks if a chat channel is used for roleplay communication.
     * 
     * @param channel The channel to check
     * @return true if the channel is ROLEPLAY or LOCALROLEPLAY
     */
    public static boolean isRoleplay(ChatChannels channel) {
        return channel == ROLEPLAY || channel == LOCALROLEPLAY;
    }
}
