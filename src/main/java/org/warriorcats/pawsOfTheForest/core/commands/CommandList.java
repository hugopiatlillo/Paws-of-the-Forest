package org.warriorcats.pawsOfTheForest.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;

import java.util.List;

/**
 * Command handler for listing all online players on the server.
 * 
 * This command provides a simple way for players to see who else is currently
 * online. It displays all player names with consistent formatting using the
 * configured color scheme.
 * 
 * Command syntax: /list
 * Permission required: warriorcats.list
 * 
 * The command does not require any arguments and simply iterates through all
 * online players to display their names to the command sender.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
public class CommandList extends AbstractCommand {

    /**
     * Executes the list command to show all online players.
     * 
     * @param sender The command sender
     * @param command The command instance
     * @param label The command label used
     * @param args Command arguments (none required)
     * @return true if command was handled
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Validate permissions (no arguments required)
        if (!checkForPermissionsAndArgs(sender, args, 0,
                "warriorcats.list", "/list")) {
            return true;
        }

        // Display all online players with consistent formatting
        for (Player player : Bukkit.getOnlinePlayers()) {
            sender.sendMessage(MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + player.getName());
        }

        return true;
    }

    /**
     * Provides tab completion for the list command.
     * Since this command takes no arguments, no completion is provided.
     * 
     * @param sender The command sender
     * @param command The command instance
     * @param alias The alias used
     * @param args Current arguments
     * @return null (no completion options)
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
