package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;

import java.util.List;

/**
 * Command handler for opening the shop interface.
 * 
 * This class handles the "/shop" command that allows players to access
 * the in-game shop where they can purchase various items using Paw Coins.
 * The command requires the "warriorcats.shop" permission to execute.
 * 
 * @author WarriorCats Plugin Team
 * @version 1.0
 * @since 1.0
 */
public class CommandOpenShop extends AbstractCommand {

    /**
     * Executes the shop command to open the shop interface for a player.
     * 
     * This method validates that the command sender has the required permissions
     * and is a player, then opens the shop menu interface. The command requires
     * no additional arguments beyond the base command.
     * 
     * @param sender The command sender (must be a player)
     * @param command The command object that was executed
     * @param label The alias of the command that was used
     * @param args Command arguments (none expected for this command)
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Validate permissions and argument count (expects 0 arguments)
        if (!checkForPermissionsAndArgs(sender, args, 0,
                "warriorcats.shop", "/shop")) {
            return true;
        }

        // Open the shop menu for the player
        MenuShop.open((Player) sender);

        return true;
    }

    /**
     * Provides tab completion suggestions for the shop command.
     * 
     * Since the shop command does not accept any arguments, this method
     * returns null to indicate no tab completion suggestions are available.
     * 
     * @param sender The command sender requesting tab completion
     * @param command The command being tab completed
     * @param alias The alias of the command used
     * @param args The current arguments being typed
     * @return null as no tab completion is needed for this command
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null; // No tab completion needed for shop command
    }
}
