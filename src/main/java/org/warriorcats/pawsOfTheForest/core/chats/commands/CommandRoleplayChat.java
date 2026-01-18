package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.List;

/**
 * Command handler for global roleplay chat functionality.
 * 
 * This command allows players to send roleplay messages to all online players
 * who have enabled roleplay messages in their settings. Unlike local roleplay
 * chat, this channel has no distance restrictions and broadcasts server-wide.
 * 
 * The roleplay filter ensures that only players who want to see roleplay
 * content will receive these messages, helping to maintain immersion for
 * roleplay participants while not disrupting other players' experience.
 * 
 * Usage: /roleplay <message>
 * Permission: warriorcats.chat.roleplay
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandRoleplayChat extends AbstractCommand {

    /**
     * Executes the roleplay chat command to broadcast a roleplay message.
     * 
     * This method validates permissions and arguments, then sends the message
     * to all online players who have roleplay messages enabled in their settings.
     * Players who have disabled roleplay content will not receive the message.
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
                "warriorcats.chat.roleplay", "/roleplay <message>")) {
            return true;
        }

        // Broadcast to all players with roleplay enabled
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerEntity playerEntity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
            
            // Skip players who have disabled roleplay messages
            if (!playerEntity.getSettings().isShowRoleplay()) {
                continue;
            }

            // Send formatted roleplay message
            player.sendMessage(formatWithClanPrefixIfPresent(MessagesConf.Chats.COLOR_ROLEPLAY_CHANNEL + "[RP]",
                    MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName() + ": " +
                    MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)), (Player) sender));
        }

        return true;
    }

    /**
     * Provides tab completion suggestions for the roleplay chat command.
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
