package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;

import java.util.List;

/**
 * Command handler for global chat functionality.
 * 
 * This command allows players to send messages to all online players on the server,
 * regardless of their location, clan affiliation, or other restrictions. Messages
 * are broadcast server-wide and formatted with the sender's name and clan prefix
 * if applicable.
 * 
 * The global chat serves as the primary communication channel for server-wide
 * announcements, general conversation, and cross-clan communication.
 * 
 * Usage: /global <message>
 * Permission: warriorcats.chat.global
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandGlobalChat extends AbstractCommand {

    /**
     * Executes the global chat command to broadcast a message to all online players.
     * 
     * This method validates that the sender has the required permissions and has
     * provided a message. Once validated, the message is formatted with the sender's
     * name (and clan prefix if applicable) and broadcast to every player currently
     * online on the server.
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
                "warriorcats.chat.global", "/global <message>")) {
            return true;
        }

        // Broadcast message to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(formatWithClanPrefixIfPresent(MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName(),
                     ": " + MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)), (Player) sender));
        }

        return true;
    }

    /**
     * Provides tab completion suggestions for the global chat command.
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
