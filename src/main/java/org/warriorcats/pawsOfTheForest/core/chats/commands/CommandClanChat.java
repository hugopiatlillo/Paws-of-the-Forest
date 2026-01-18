package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.List;

/**
 * Command handler for clan chat functionality.
 * 
 * This command allows players who are members of a clan to send messages exclusively
 * to other members of the same clan. Messages are broadcast to all online players
 * who belong to the sender's clan and are formatted with clan-specific styling.
 * 
 * The command validates that the sender is a member of a clan before allowing
 * message transmission and applies appropriate permission checks.
 * 
 * Usage: /clan <message>
 * Permission: warriorcats.chat.clan
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandClanChat extends AbstractCommand {

    /**
     * Executes the clan chat command to send a message to all clan members.
     * 
     * This method first validates that the sender has the required permissions and
     * has provided a message. It then checks if the sender belongs to a clan.
     * If all validations pass, the message is formatted and sent to all online
     * players who are members of the same clan as the sender.
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
                "warriorcats.chat.clan", "/clan <message>")) {
            return true;
        }

        // Get the sender's player entity from cache
        PlayerEntity senderEntity = EventsCore.PLAYERS_CACHE.get(((Player) sender).getUniqueId());

        // Check if the sender is a member of any clan
        if (senderEntity.getClan() == null) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_A_CLAN_MEMBER);
            return true;
        }

        // Broadcast message to all clan members
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
            // Skip players who are not in the same clan
            if (senderEntity.getClan() != entity.getClan()) {
                continue;
            }
            // Format and send the clan message
            player.sendMessage(formatWithClanPrefixIfPresent(MessagesConf.Chats.COLOR_CLAN_CHANNEL + "[Clan] ",
                    MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName() + ": " +
                    MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)), (Player) sender));
        }

        return true;
    }

    /**
     * Provides tab completion suggestions for the clan chat command.
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
