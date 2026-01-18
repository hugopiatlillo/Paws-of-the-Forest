package org.warriorcats.pawsOfTheForest.clans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Command handler for clan management operations.
 * 
 * This command allows administrators to:
 * - Add players to specific clans
 * - Remove players from their current clans
 * - Manage clan membership with proper validation
 * 
 * Command syntax: /clans <clan> <add|remove> <player>
 * 
 * The command performs database transactions to update player entities
 * and maintains the player cache for performance.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
public class CommandClans extends AbstractCommand {

    /**
     * Executes the clan management command.
     * 
     * @param sender The command sender (must have warriorcats.clans permission)
     * @param command The command instance
     * @param label The command label used
     * @param args Command arguments: [clan, action, player]
     * @return true if command was handled
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String usage = "/clans <clan> <add|remove> <player>";
        
        // Validate permissions and argument count
        if (!checkForPermissionsAndArgs(sender, args, 3,
                "warriorcats.clans", usage)) {
            return true;
        }

        // Parse and validate clan argument
        Clans clan;
        try {
            clan = Clans.from(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Clans.CLAN_NOT_FOUND);
            return true;
        }

        // Perform database transaction for clan membership change
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            // Find the target player
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + MessagesConf.Chats.PLAYER_NOT_FOUND);
                return true;
            }
            
            // Get player entity from database
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            var transaction = session.beginTransaction();
            
            // Process the clan action
            switch (args[1].toLowerCase()) {
                case "add":
                    // Add player to the specified clan
                    playerEntity.setClan(clan);
                    player.sendMessage(MessagesConf.Clans.COLOR_FEEDBACK + MessagesConf.Clans.CLAN_ADDED + " " + clan);
                    break;
                    
                case "remove":
                    // Validate player is in the specified clan before removal
                    if (playerEntity.getClan() != clan) {
                        sender.sendMessage(ChatColor.RED + MessagesConf.Clans.PLAYER_NOT_BELONG_TO_CLAN);
                        return true;
                    }
                    // Remove player from clan
                    playerEntity.setClan(null);
                    player.sendMessage(MessagesConf.Clans.COLOR_FEEDBACK + MessagesConf.Clans.CLAN_REMOVED + " " + clan);
                    break;
                    
                default:
                    sender.sendMessage(ChatColor.RED + MessagesConf.GENERIC_ERROR + " " + usage);
                    return true;
            }
            
            // Update cache and commit transaction
            EventsCore.PLAYERS_CACHE.put(player.getUniqueId(), playerEntity);
            transaction.commit();
        }


        return true;
    }

    /**
     * Provides tab completion for the clans command.
     * 
     * @param sender The command sender
     * @param command The command instance
     * @param alias The alias used
     * @param args Current arguments
     * @return List of completion options
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                // First argument: clan names
                return List.of(Arrays.stream(Clans.values()).map(Clans::toString).toArray(String[]::new));
            case 2:
                // Second argument: action (add/remove)
                return List.of("add", "remove");
            case 3:
                // Third argument: player name placeholder
                return List.of("player");
            default:
                return null;
        }
    }
}
