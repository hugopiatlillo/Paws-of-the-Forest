package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.List;

/**
 * Command handler for local roleplay chat functionality.
 * 
 * This command combines the location-based messaging of local chat with roleplay
 * filtering. Only players within the local chat radius who have roleplay messages
 * enabled in their settings will receive messages sent through this channel.
 * 
 * This is perfect for immersive roleplay scenarios where players want to ensure
 * their roleplay messages only reach nearby players who are interested in
 * participating in roleplay activities.
 * 
 * Usage: /localroleplay <message>
 * Permission: warriorcats.chat.localroleplay
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandLocalRoleplayChat extends AbstractCommand {

    /**
     * Executes the local roleplay chat command to send a roleplay message to nearby players.
     * 
     * This method validates permissions and arguments, then finds all players within
     * the local chat radius who have roleplay messages enabled in their settings.
     * The message is only sent to players who meet both criteria: proximity and
     * roleplay preference.
     * 
     * @param sender the command sender (must be a player)
     * @param command the command that was executed
     * @param label the alias of the command that was used
     * @param args the arguments passed to the command (message content)
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Validate permissions and argument count
        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.localroleplay", "/localroleplay <message>")) {
            return true;
        }

        // Get sender's current location for radius calculation
        Location senderLocation = ((Player) sender).getLocation();
        
        // Check each online player for proximity and roleplay preference
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerEntity playerEntity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
            
            // Skip players who have disabled roleplay messages
            if (!playerEntity.getSettings().isShowRoleplay()) {
                continue;
            }

            // Calculate distance on each axis within the local radius
            boolean x = Math.abs(player.getLocation().getX() - senderLocation.getX()) < ChatChannels.LOCAL_CHANNEL_RADIUS;
            boolean y = Math.abs(player.getLocation().getY() - senderLocation.getY()) < ChatChannels.LOCAL_CHANNEL_RADIUS;
            boolean z = Math.abs(player.getLocation().getZ() - senderLocation.getZ()) < ChatChannels.LOCAL_CHANNEL_RADIUS;

            // Send message only if player is within radius on all axes
            if (x && y && z) {
                player.sendMessage(formatWithClanPrefixIfPresent(MessagesConf.Chats.COLOR_ROLEPLAY_CHANNEL + "[Local RP]",
                        MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName() + ": " +
                        MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)), (Player) sender));
            }
        }

        return true;
    }

    /**
     * Provides tab completion suggestions for the local roleplay chat command.
     * 
     * This method returns appropriate tab completion suggestions based on the
     * current argument position. For the first argument, it suggests "message"
     * as a placeholder to indicate where the user should type their roleplay message.
     * 
     * @param sender the command sender requesting tab completion
     * @param command the command being tab completed
     * @param alias the alias of the command that was used
     * @param args the arguments typed so far
     * @return a list of tab completion suggestions, or null if no suggestions
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of("message");
            default:
                return null;
        }
    }
}
