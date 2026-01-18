package org.warriorcats.pawsOfTheForest.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

/**
 * Abstract base class for all plugin commands that provides common functionality.
 * 
 * This class implements both CommandExecutor and TabCompleter interfaces and provides
 * utility methods for permission checking, argument validation, and chat formatting.
 * 
 * All plugin commands should extend this class to inherit common command handling
 * patterns and maintain consistency across the command system.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    /**
     * Validates that the command sender has the required permission and provided enough arguments.
     * 
     * This method performs two checks:
     * 1. Verifies the sender has the required permission
     * 2. Ensures the minimum number of arguments was provided
     * 
     * If either check fails, an appropriate error message is sent to the sender.
     * 
     * @param sender The command sender to check
     * @param args The command arguments provided
     * @param argsLength The minimum number of arguments required
     * @param permission The permission node required to execute the command
     * @param usage The usage string to display if arguments are insufficient
     * @return true if both checks pass, false otherwise
     */
    protected boolean checkForPermissionsAndArgs(CommandSender sender, String[] args, int argsLength, String permission, String usage) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_ENOUGH_PERMISSIONS);
            return false;
        }

        if (args.length < argsLength) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usage);
            return false;
        }

        return true;
    }

    /**
     * Formats a chat message with the sender's clan prefix if they belong to a clan.
     * 
     * If the player is a member of a clan, the message will be prefixed with
     * the clan's color code and name in brackets. If not in a clan, only the
     * provided prefix is used.
     * 
     * @param prefix The base prefix for the message
     * @param message The main message content
     * @param sender The player sending the message
     * @return The formatted message with clan prefix if applicable
     */
    protected String formatWithClanPrefixIfPresent(String prefix, String message, Player sender) {
        PlayerEntity playerEntity = EventsCore.PLAYERS_CACHE.get(sender.getUniqueId());
        if (playerEntity.getClan() != null) {
            return playerEntity.getClan().getColorCode() + "[" + playerEntity.getClan().toString() + "]" + " " + prefix + " " + message;
        }
        return prefix + " " + message;
    }
}
