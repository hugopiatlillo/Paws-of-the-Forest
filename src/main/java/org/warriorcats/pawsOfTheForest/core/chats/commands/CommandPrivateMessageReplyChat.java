package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;

import java.util.*;

/**
 * Command handler for private message reply functionality.
 * 
 * This command allows players to quickly reply to the most recent private message
 * they received or sent. It uses the conversation tracking map from
 * CommandPrivateMessageChat to determine the appropriate recipient.
 * 
 * The reply system prioritizes responding to the most recent incoming message
 * over outgoing messages, making conversations more intuitive. If no conversation
 * history exists, an error message is displayed.
 * 
 * Usage: /reply <message>
 * Permission: warriorcats.chat.reply
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandPrivateMessageReplyChat extends AbstractCommand {

    /**
     * Executes the reply command to respond to the most recent private message.
     * 
     * This method implements a two-tier reply system:
     * 1. First, it looks for incoming messages (where the sender is the recipient)
     * 2. If no incoming messages exist, it replies to the last player messaged
     * 
     * The conversation tracking map is updated to maintain the reply chain.
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
                "warriorcats.chat.reply", "/reply <message>")) {
            return true;
        }

        Player player = null;

        // First priority: find the most recent player who sent a message TO this sender
        UUID uuid = CommandPrivateMessageChat.PRIVATE_MESSAGES_MAP.values().stream()
                .filter(pair -> pair.getKey().equals(((Player) sender).getUniqueId()))
                .min((pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue())) // Most recent first
                .map(Pair::getKey)
                .orElse(null);

        if (uuid != null) {
            player = Bukkit.getPlayer(uuid);
        }

        // Second priority: if no incoming messages, reply to the last player this sender messaged
        if (player == null) {
            Pair<UUID, Date> lastSent = CommandPrivateMessageChat.PRIVATE_MESSAGES_MAP.get(((Player) sender).getUniqueId());
            if (lastSent != null) {
                player = Bukkit.getPlayer(lastSent.getKey());
            }
        }

        // If still no valid player found, show error
        if (player == null) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.PLAYER_NOT_FOUND);
            return true;
        }

        // Update the conversation tracking map for future replies
        CommandPrivateMessageChat.PRIVATE_MESSAGES_MAP.put(((Player) sender).getUniqueId(), Pair.of(player.getUniqueId(), new Date()));

        // Join all message arguments
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length));

        // Send the formatted reply message
        player.sendMessage(MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + "["
                + sender.getName()
                + " -> "
                + player.getName()
                + "] "
                + MessagesConf.Chats.COLOR_MESSAGE + message);

        return true;
    }

    /**
     * Provides tab completion suggestions for the reply command.
     * 
     * This method returns appropriate tab completion suggestions based on the
     * current argument position. For the first argument, it suggests "message"
     * as a placeholder to indicate where the user should type their reply.
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
