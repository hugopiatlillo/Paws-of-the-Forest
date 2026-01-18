package org.warriorcats.pawsOfTheForest.skills.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.skills.menus.MenuSkillTree;

import java.util.List;

/**
 * Command handler for opening the main skill tree interface.
 * 
 * <p>This command opens the skill tree menu where players can view and purchase
 * skills from various branches including universal skills (Hunting, Navigation, 
 * Resilience, Herbalist), background skills (Kittypet, Loner, Rogue, City Cat),
 * and clan-specific skills.</p>
 * 
 * <p><b>Usage:</b> /skills</p>
 * <p><b>Permission:</b> warriorcats.skills</p>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
public class CommandOpenSkills extends AbstractCommand {

    /**
     * Executes the skills command to open the main skill tree interface.
     * 
     * <p>Opens the skill tree menu showing all available branches and the player's
     * current skill points. Players can navigate to specific branches to view and
     * purchase individual skills.</p>
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
                "warriorcats.skills", "/skills")) {
            return true;
        }

        MenuSkillTree.open((Player) sender);

        return true;
    }

    /**
     * Provides tab completion suggestions for the skills command.
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
