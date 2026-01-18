package org.warriorcats.pawsOfTheForest;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.warriorcats.pawsOfTheForest.clans.CommandClans;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.chats.commands.*;
import org.warriorcats.pawsOfTheForest.core.commands.CommandList;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.herbs.EventsHerbs;
import org.warriorcats.pawsOfTheForest.illnesses.EventsIllnesses;
import org.warriorcats.pawsOfTheForest.shops.CommandOpenShop;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.PreysConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.settings.EventsSettings;
import org.warriorcats.pawsOfTheForest.preys.EventsPreys;
import org.warriorcats.pawsOfTheForest.shops.EventsShop;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsActives;
import org.warriorcats.pawsOfTheForest.skills.commands.CommandOpenBackpack;
import org.warriorcats.pawsOfTheForest.skills.commands.CommandOpenSkills;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsPassives;
import org.warriorcats.pawsOfTheForest.skills.menus.EventsSkillsMenu;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.HttpServerUtils;
import org.warriorcats.pawsOfTheForest.vitals.EventsVitals;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main plugin class for Paws of the Forest - a Minecraft plugin that provides
 * a comprehensive Warrior Cats roleplay experience.
 * 
 * This plugin includes features such as:
 * - Clan system with different cat clans
 * - Skill trees with various abilities and specializations
 * - Chat system with roleplay channels
 * - Player vitals and health management
 * - Shop system for trading
 * - Herb collection and illness system
 * - Custom resource pack integration
 * 
 * @author Warrior Cats Development Team
 * @version 1.0.0-dev
 * @since 1.0.0
 */
public final class PawsOfTheForest extends JavaPlugin {

    /** Singleton instance of the plugin for global access */
    private static PawsOfTheForest INSTANCE;

    /**
     * Constructor that initializes the singleton instance.
     * Called automatically by the Bukkit plugin loader.
     */
    public PawsOfTheForest() {
        INSTANCE = this;
    }

    /**
     * Gets the singleton instance of the plugin.
     * 
     * @return The plugin instance
     */
    public static PawsOfTheForest getInstance() {
        return INSTANCE;
    }

    /**
     * Called when the plugin is enabled. Initializes all core systems including:
     * - Database connection
     * - Configuration loading
     * - Command registration
     * - Event listener registration
     * - Resource pack preparation and HTTP server
     */
    @Override
    public void onEnable() {
        // Initialize database connection
        HibernateUtils.getSessionFactory();
        getLogger().info("Hibernate/MySQL connected.");

        // Load configuration files
        MessagesConf.getInstance().load(MessagesConf.CONFIG_FILE_NAME);
        ShopsConf.getInstance().load(ShopsConf.CONFIG_FILE_NAME);
        PreysConf.getInstance().load(PreysConf.CONFIG_FILE_NAME);

        // Register all chat and utility commands
        registerCommand("global", new CommandGlobalChat());
        registerCommand("local", new CommandLocalChat());
        registerCommand("clan", new CommandClanChat());
        registerCommand("roleplay", new CommandRoleplayChat());
        registerCommand("localroleplay", new CommandLocalRoleplayChat());
        registerCommand("message", new CommandPrivateMessageChat());
        registerCommand("reply", new CommandPrivateMessageReplyChat());
        registerCommand("toggle", new CommandToggleChat());
        registerCommand("list", new CommandList());
        registerCommand("shop", new CommandOpenShop());
        registerCommand("clans", new CommandClans());
        registerCommand("skills", new CommandOpenSkills());
        registerCommand("backpack", new CommandOpenBackpack());

        // Register all event listeners for game mechanics
        registerEvent(new EventsCore());
        registerEvent(new EventsSettings());
        registerEvent(new EventsShop());
        registerEvent(new EventsPreys());
        registerEvent(new EventsSkillsPassives());
        registerEvent(new EventsSkillsMenu());
        registerEvent(new EventsSkillsActives());
        registerEvent(new EventsVitals());
        registerEvent(new EventsIllnesses());
        registerEvent(new EventsHerbs());

        // Prepare and serve the custom resource pack
        prepareHttpServerForResourcesPack();
    }

    /**
     * Called when the plugin is disabled. Properly shuts down database connections
     * and cleans up resources.
     */
    @Override
    public void onDisable() {
        HibernateUtils.shutdown();
    }

    /**
     * Registers a command with both executor and tab completer if the instance implements them.
     * 
     * @param name The command name as defined in plugin.yml
     * @param instance The command instance that may implement CommandExecutor and/or TabCompleter
     */
    private void registerCommand(String name, Object instance) {
        if (instance instanceof CommandExecutor executor) {
            INSTANCE.getCommand(name).setExecutor(executor);
        }
        if (instance instanceof TabCompleter completer) {
            INSTANCE.getCommand(name).setTabCompleter(completer);
        }
    }

    /**
     * Registers an event listener with the plugin manager.
     * If the listener implements LoadingListener, calls its load method for initialization.
     * 
     * @param instance The event listener to register
     */
    private void registerEvent(Listener instance) {
        INSTANCE.getServer().getPluginManager().registerEvents(instance, INSTANCE);
        if (instance instanceof LoadingListener loadingListener) {
            loadingListener.load();
        }
    }

    /**
     * Prepares and serves the custom resource pack by merging the base pack with ModelEngine assets.
     * This method:
     * 1. Extracts the base resource pack
     * 2. Merges it with ModelEngine's generated assets
     * 3. Creates a final merged resource pack
     * 4. Starts an HTTP server to serve the pack to players
     */
    private void prepareHttpServerForResourcesPack() {
        Path pluginData = FileUtils.PLUGIN_DATA_FOLDER.toPath();
        Path modelEngineFolder = Paths.get("plugins", "ModelEngine", "resource pack");
        Path tmpUnzipFolder = pluginData.resolve("tmp_base_pack");
        Path baseZip = pluginData.resolve("base_pack.zip");
        Path mergedPackZip = pluginData.resolve(FileUtils.RESOURCES_PACK_PATH);

        // Extract base resource pack to temporary folder
        FileUtils.unzipFolder(baseZip, tmpUnzipFolder);
        
        // Merge ModelEngine assets into the base pack
        FileUtils.copyFolder(modelEngineFolder, tmpUnzipFolder);

        // Create the final merged resource pack
        FileUtils.zipFolder(tmpUnzipFolder, mergedPackZip);

        // Clean up temporary folder
        FileUtils.deleteFolder(tmpUnzipFolder);

        // Start HTTP server to serve the resource pack to players
        HttpServerUtils.start(
                HttpServerUtils.RESOURCES_PACK_PORT,
                mergedPackZip,
                "/" + FileUtils.RESOURCES_PACK_PATH
        );
    }
}
