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
 * Command handler for private messaging functionality.
 * 
 * This command allows players to send private messages directly to other players.
 * It maintains a conversation history map that tracks the most recent private
 * message exchanges between players, which is used by the reply command system.
 * 
 * The private message system stores sender-recipient pairs with timestamps to
 * enable proper reply functionality and conversation threading.
 * 
 * Usage: /message <player> <message>
 * Permission: warriorcats.chat.message
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandPrivateMessageChat extends AbstractCommand {

    /**
     * Static map that tracks private message conversations.
     * Maps sender UUID to a pair containing recipient UUID and timestamp.
     * This is used by the reply system to determine conversation context.
     */
    public static final Map<UUID, Pair<UUID, Date>> PRIVATE_MESSAGES_MAP = new HashMap<>();

    /**
     * Executes the private message command to send a direct message to another player.
     * 
     * This method validates permissions and arguments, finds the target player,
     * and sends them a formatted private message. It also updates the conversation
     * tracking map to enable proper reply functionality.
     * 
     * @param sender the command sender (must be a player)
     * @param command the command that was executed
     * @param label the alias of the command that was used
     * @param args the arguments passed to the command (target player and message)
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Validate permissions and argument count (need at least player name and message)
        if (!checkForPermissionsAndArgs(sender, args, 2,
                "warriorcats.chat.message", "/message <player> <message>")) {
            return true;
        }

        // Find the target player by name
        Player player = Bukkit.getPlayer(args[0]);

        // Check if the target player exists and is online
        if (player == null) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.PLAYER_NOT_FOUND);
            return true; // Return true to indicate command was handled
        }

        // Join all message arguments (excluding the player name)
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        // Send the formatted private message to the target player
        player.sendMessage(MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + "["
                + sender.getName()
                + " -> "
                + player.getName()
                + "] "
                + MessagesConf.Chats.COLOR_MESSAGE + message);

        // Update the conversation tracking map for reply functionality
        PRIVATE_MESSAGES_MAP.put(((Player) sender).getUniqueId(), Pair.of(player.getUniqueId(), new Date()));

        return true;
    }

    /**
     * Provides tab completion suggestions for the private message command.
     * 
     * This method returns appropriate tab completion suggestions based on the
     * current argument position. For the first argument, it suggests "player"
     * and for the second argument, it suggests "message" as placeholders.
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
                return List.of("player");
            case 2:
                return List.of("message");
            default:
                return null;
        }
    }
}
