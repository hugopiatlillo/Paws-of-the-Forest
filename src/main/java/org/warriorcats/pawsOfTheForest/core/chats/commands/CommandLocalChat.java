package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;

import java.util.List;

/**
 * Command handler for local chat functionality.
 * 
 * This command allows players to send messages to other players within a limited
 * radius around their current location. Only players within the local chat radius
 * (defined by ChatChannels.LOCAL_CHANNEL_RADIUS) on all three axes (X, Y, Z) will
 * receive the message.
 * 
 * This chat channel is ideal for roleplay scenarios, area-specific conversations,
 * or when players want to communicate without broadcasting to the entire server.
 * 
 * Usage: /local <message>
 * Permission: warriorcats.chat.local
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandLocalChat extends AbstractCommand {

    /**
     * Executes the local chat command to send a message to nearby players.
     * 
     * This method validates permissions and arguments, then calculates which players
     * are within the local chat radius of the sender. The radius check is performed
     * on all three axes (X, Y, Z) to ensure players are truly nearby. Only players
     * within the defined radius will receive the formatted message.
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
                "warriorcats.chat.local", "/local <message>")) {
            return true;
        }

        // Get sender's current location for radius calculation
        Location senderLocation = ((Player) sender).getLocation();
        
        // Check each online player's proximity to the sender
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Calculate distance on each axis within the local radius
            boolean x = Math.abs(player.getLocation().getX() - senderLocation.getX()) < ChatChannels.LOCAL_CHANNEL_RADIUS;
            boolean y = Math.abs(player.getLocation().getY() - senderLocation.getY()) < ChatChannels.LOCAL_CHANNEL_RADIUS;
            boolean z = Math.abs(player.getLocation().getZ() - senderLocation.getZ()) < ChatChannels.LOCAL_CHANNEL_RADIUS;

            // Send message only if player is within radius on all axes
            if (x && y && z) {
                player.sendMessage(formatWithClanPrefixIfPresent(MessagesConf.Chats.COLOR_STANDARD_CHANNEL + "[Local]",
                        MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName() + ": " +
                        MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)), (Player) sender));
            }
        }

        return true;
    }

    /**
     * Provides tab completion suggestions for the local chat command.
     * 
     * This method returns appropriate tab completion suggestions based on the
     * current argument position. For the first argument, it suggests "message"
     * as a placeholder to indicate where the user should type their message.
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
