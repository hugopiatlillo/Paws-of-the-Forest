package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Command handler for chat channel toggling functionality.
 * 
 * This command allows players to set their default chat channel, which determines
 * where their messages are sent when they type without using a specific chat command.
 * The toggle system supports all available chat channels: global, local, clan,
 * roleplay, and localroleplay.
 * 
 * The command validates that players meet the requirements for specific channels
 * (e.g., clan membership for clan chat, roleplay settings for roleplay channels)
 * before allowing the toggle.
 * 
 * Usage: /toggle <global|local|clan|roleplay|localroleplay>
 * Permission: warriorcats.chat.toggle
 * 
 * @author WarriorCats Plugin Team
 * @since 1.0
 */
public class CommandToggleChat extends AbstractCommand {

    /**
     * Retrieves the currently toggled chat channel for a player.
     * 
     * @param player the player whose toggled chat channel to retrieve
     * @return the currently active chat channel for the player
     */
    public static ChatChannels getToggledChat(Player player) {
        return EventsCore.PLAYERS_CACHE.get(player.getUniqueId()).getSettings().getToggledChat();
    }

    /**
     * Sets the toggled chat channel for a player and persists the change to the database.
     * 
     * This method updates both the cached player entity and the database record
     * to ensure the setting persists across server restarts.
     * 
     * @param player the player whose chat channel to update
     * @param chatToggled the new chat channel to set as default
     */
    public static void setToggledChat(Player player, ChatChannels chatToggled) {
        HibernateUtils.withTransaction(((transaction, session) -> {
            PlayerEntity senderEntity = session.get(PlayerEntity.class, player.getUniqueId());
            senderEntity.getSettings().setToggledChat(chatToggled);
            return senderEntity;
        }));
    }

    /**
     * Executes the toggle chat command to change a player's default chat channel.
     * 
     * This method validates permissions and arguments, then checks if the player
     * meets the requirements for the requested chat channel. If all validations
     * pass, the player's default chat channel is updated and a confirmation
     * message is sent.
     * 
     * @param sender the command sender (must be a player)
     * @param command the command that was executed
     * @param label the alias of the command that was used
     * @param args the arguments passed to the command (chat channel name)
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Validate permissions and argument count
        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.toggle", "/toggle <global|local|clan|roleplay|localroleplay>")) {
            return true;
        }

        // Parse the requested chat channel
        ChatChannels chatToggled;
        try {
            chatToggled = ChatChannels.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            // Invalid chat channel provided
            sender.sendMessage(ChatColor.RED + "Invalid chat channel. Available options: global, local, clan, roleplay, localroleplay");
            return true;
        }

        // Validate clan membership for clan chat
        if (chatToggled == ChatChannels.CLAN) {
            PlayerEntity senderEntity = EventsCore.PLAYERS_CACHE.get(((Player) sender).getUniqueId());
            if (senderEntity.getClan() == null) {
                sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_A_CLAN_MEMBER);
                return true;
            }
        }

        // Validate roleplay settings for roleplay channels
        if (ChatChannels.isRoleplay(chatToggled)) {
            PlayerEntity senderEntity = EventsCore.PLAYERS_CACHE.get(((Player) sender).getUniqueId());
            if (!senderEntity.getSettings().isShowRoleplay()) {
                sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_SHOWING_ROLEPLAY);
                return true;
            }
        }

        // Update the player's toggled chat channel
        setToggledChat((Player) sender, chatToggled);

        // Send confirmation message
        sender.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Chats.CHAT_TOGGLED + " " + chatToggled.name().toLowerCase());

        return true;
    }

    /**
     * Provides tab completion suggestions for the toggle chat command.
     * 
     * This method returns all available chat channel names in lowercase format
     * for the first argument, making it easy for players to see and select
     * their desired chat channel.
     * 
     * @param sender the command sender requesting tab completion
     * @param command the command being tab completed
     * @param alias the alias of the command that was used
     * @param args the arguments typed so far
     * @return a list of available chat channel names, or null if no suggestions
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                // Return all available chat channel names in lowercase
                return List.of(Arrays.stream(ChatChannels.values()).map(e -> e.name().toLowerCase()).toArray(String[]::new));
            default:
                return null;
        }
    }
}
