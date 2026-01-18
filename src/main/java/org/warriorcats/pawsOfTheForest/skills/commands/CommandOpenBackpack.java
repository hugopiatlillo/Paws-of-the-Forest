package org.warriorcats.pawsOfTheForest.skills.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.skills.menus.MenuBackpack;

import java.util.List;

/**
 * Command handler for opening the Beast of Burden skill backpack inventory.
 * 
 * <p>This command allows players with the Beast of Burden skill to access their
 * expanded inventory storage. The backpack size depends on the skill tier, providing
 * 9 additional slots per tier level.</p>
 * 
 * <p><b>Usage:</b> /backpack</p>
 * <p><b>Permission:</b> warriorcats.backpack</p>
 * <p><b>Requirements:</b> Beast of Burden skill unlocked</p>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
public class CommandOpenBackpack extends AbstractCommand {

    /**
     * Executes the backpack command to open the player's extended inventory.
     * 
     * <p>Validates that the player has the Beast of Burden skill before opening
     * the backpack interface. The backpack size is determined by the skill tier.</p>
     *
     * @param sender the command sender (must be a player)
     * @param command the command instance
     * @param label the command label used
     * @param args command arguments (none expected)
     * @return true if command was handled
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 0,
                "warriorcats.backpack", "/backpack")) {
            return true;
        }

        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(((Player) sender).getUniqueId());
        if (!entity.hasAbility(Skills.BEAST_OF_BURDEN)) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_BEAST_OF_BURDEN_NOT_UNLOCKED);
            return true;
        }

        int tier = entity.getAbilityTier(Skills.BEAST_OF_BURDEN);

        MenuBackpack.open((Player) sender, tier);

        return true;
    }

    /**
     * Provides tab completion suggestions for the backpack command.
     * No tab completion is needed as this command takes no arguments.
     *
     * @param sender the command sender
     * @param command the command instance
     * @param alias the command alias used
     * @param args command arguments
     * @return null (no tab completion)
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
