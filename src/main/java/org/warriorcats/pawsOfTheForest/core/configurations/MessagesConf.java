package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;

/**
 * Configuration class for managing all user-facing messages, colors, and text content
 * in the Paws of the Forest plugin.
 * 
 * This class extends BaseConfiguration and provides centralized access to all localized
 * messages, chat colors, and user interface text elements. It organizes messages into
 * logical sections (Chats, Shops, Preys, Clans, Skills, Illnesses) for better maintainability
 * and localization support.
 * 
 * The configuration is loaded from the "messages_config.properties" file and supports
 * both color customization and text localization. All static constants are initialized
 * with sensible default values that are automatically added to the configuration file
 * if they don't exist.
 * 
 * Usage example:
 * <pre>
 * String errorMsg = MessagesConf.GENERIC_ERROR;
 * ChatColor playerColor = MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT;
 * </pre>
 * 
 * @author Warrior Cats Plugin Team
 * @since 1.0.0
 */
public abstract class MessagesConf extends BaseConfiguration {

    /**
     * The filename of the messages configuration file.
     * This file contains all localized messages and color settings.
     */
    public static final String CONFIG_FILE_NAME = "messages_config.properties";

    /**
     * Generic error message displayed when an unknown command usage occurs.
     * Default: "Unknown usage :"
     */
    public static final String GENERIC_ERROR =
            getPropertyOrDefault("generic_error", "Unknown usage :", CONFIG_FILE_NAME);

    /**
     * Configuration section for chat-related messages, colors, and settings.
     * Contains all text and color configurations for the chat system including
     * player names, messages, channels, and common chat responses.
     */
    public static class Chats {
        /**
         * Default color for player names in chat messages.
         * Default: DARK_AQUA
         */
        public static final ChatColor COLOR_PLAYER_NAME_DEFAULT =
                getPropertyOrDefault("chats.colors.playerName", ChatColor.DARK_AQUA, CONFIG_FILE_NAME);
        /**
         * Color for regular chat messages.
         * Default: GRAY
         */
        public static final ChatColor COLOR_MESSAGE =
                getPropertyOrDefault("chats.colors.message", ChatColor.GRAY, CONFIG_FILE_NAME);
        /**
         * Color for feedback messages and confirmations.
         * Default: GREEN
         */
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("chats.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);
        /**
         * Color for standard chat channel messages.
         * Default: DARK_GREEN
         */
        public static final ChatColor COLOR_STANDARD_CHANNEL =
                getPropertyOrDefault("chats.colors.standardChannel", ChatColor.DARK_GREEN, CONFIG_FILE_NAME);
        /**
         * Color for clan-specific chat channel messages.
         * Default: DARK_PURPLE
         */
        public static final ChatColor COLOR_CLAN_CHANNEL =
                getPropertyOrDefault("chats.colors.clansChannel", ChatColor.DARK_PURPLE, CONFIG_FILE_NAME);
        /**
         * Color for roleplay chat channel messages.
         * Default: GOLD
         */
        public static final ChatColor COLOR_ROLEPLAY_CHANNEL =
                getPropertyOrDefault("chats.colors.roleplayChannel", ChatColor.GOLD, CONFIG_FILE_NAME);


        /**
         * Message displayed when a player lacks sufficient permissions for an action.
         * Default: "You don't have enough permissions to do that."
         */
        public static final String NOT_ENOUGH_PERMISSIONS =
                getPropertyOrDefault("chats.notEnoughPermissions", "You don't have enough permissions to do that.", CONFIG_FILE_NAME);

        /**
         * Message displayed when a player tries to use clan-specific features without being in a clan.
         * Default: "You are not a member of a Clan."
         */
        public static final String NOT_A_CLAN_MEMBER =
                getPropertyOrDefault("chats.notAClanMember", "You are not a member of a Clan.", CONFIG_FILE_NAME);

        /**
         * Message displayed when a player has disabled roleplay messages.
         * Default: "You have disabled roleplay messages."
         */
        public static final String NOT_SHOWING_ROLEPLAY =
                getPropertyOrDefault("chats.notShowingRoleplay", "You have disabled roleplay messages.", CONFIG_FILE_NAME);

        /**
         * Message displayed when a specified player cannot be found or is offline.
         * Default: "Player specified is offline or doesn't exist."
         */
        public static final String PLAYER_NOT_FOUND =
                getPropertyOrDefault("chats.playerNotFound", "Player specified is offline or doesn't exist.", CONFIG_FILE_NAME);

        /**
         * Message displayed when chat is toggled on or off.
         * Default: "Chat toggled :"
         */
        public static final String CHAT_TOGGLED =
                getPropertyOrDefault("chats.chatToggled", "Chat toggled :", CONFIG_FILE_NAME);
    }

    /**
     * Configuration section for shop-related messages, colors, and currency display.
     * Contains all text and color configurations for the shop system including
     * coin display, currency information, and shop interaction messages.
     */
    public static class Shops {
        /**
         * Color for displaying coin amounts in shops.
         * Default: DARK_GREEN
         */
        public static final ChatColor COLOR_COINS =
                getPropertyOrDefault("shops.colors.coins", ChatColor.DARK_GREEN, CONFIG_FILE_NAME);

        /**
         * Color for coin-related text labels in shops.
         * Default: GOLD
         */
        public static final ChatColor COLOR_COINS_TEXT =
                getPropertyOrDefault("shops.colors.coinsText", ChatColor.GOLD, CONFIG_FILE_NAME);

        /**
         * Color for coin-related item lore text in shops.
         * Default: GRAY
         */
        public static final ChatColor COLOR_COINS_LORE =
                getPropertyOrDefault("shops.colors.coinsLore", ChatColor.GRAY, CONFIG_FILE_NAME);

        /**
         * Label text for displaying coins in the shop interface.
         * Default: "Coins :"
         */
        public static final String COINS =
                getPropertyOrDefault("shops.coins", "Coins :", CONFIG_FILE_NAME);

        /**
         * First line of lore text for coin-related items.
         * Default: "Your coins"
         */
        public static final String COINS_LORE_1 =
                getPropertyOrDefault("shops.coinsLore1", "Your coins", CONFIG_FILE_NAME);

        /**
         * Second line of lore text for coin-related items, explaining how to earn coins.
         * Default: "Earn it by killing preys !"
         */
        public static final String COINS_LORE_2 =
                getPropertyOrDefault("shops.coinsLore2", "Earn it by killing preys !", CONFIG_FILE_NAME);
    }

    /**
     * Configuration section for prey hunting messages and feedback.
     * Contains all text and color configurations for the prey hunting system including
     * skill points, experience, coins earned, and purchase confirmations.
     */
    public static class Preys {
        /**
         * Color for prey hunting feedback messages.
         * Default: GREEN
         */
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("preys.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        /**
         * Message prefix for displaying earned skill points from hunting.
         * Default: "Skill points : +"
         */
        public static final String SKILL_POINTS_EARNED =
                getPropertyOrDefault("preys.skillPointsEarned", "Skill points : +", CONFIG_FILE_NAME);

        /**
         * Message for displaying the player's total experience points.
         * Default: "Your total xp is :"
         */
        public static final String XP_LEFT =
                getPropertyOrDefault("preys.xpLeft", "Your total xp is :", CONFIG_FILE_NAME);

        /**
         * Message prefix for displaying earned coins from hunting.
         * Default: "Paw coins : +"
         */
        public static final String COINS_EARNED =
                getPropertyOrDefault("preys.coinsEarned", "Paw coins : +", CONFIG_FILE_NAME);

        /**
         * Message for displaying the player's total coin balance.
         * Default: "Your total Paw coins is :"
         */
        public static final String COINS_LEFT =
                getPropertyOrDefault("preys.coinsLeft", "Your total Paw coins is :", CONFIG_FILE_NAME);

        /**
         * Error message displayed when a player lacks sufficient coins for a purchase.
         * Default: "You have not enough coins to buy this !"
         */
        public static final String NOT_ENOUGH_COINS =
                getPropertyOrDefault("preys.notEnoughCoins", "You have not enough coins to buy this !", CONFIG_FILE_NAME);

        /**
         * Confirmation message displayed when a player successfully purchases an item.
         * Default: "You have bought a shop item for :"
         */
        public static final String MADE_BUY =
                getPropertyOrDefault("preys.madeBuy", "You have bought a shop item for :", CONFIG_FILE_NAME);
    }

    /**
     * Configuration section for clan-related messages and notifications.
     * Contains all text and color configurations for the clan system including
     * membership management, clan operations, and clan-specific feedback.
     */
    public static class Clans {
        /**
         * Color for clan-related feedback messages.
         * Default: GREEN
         */
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("clans.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        /**
         * Error message when trying to perform clan operations on a non-member.
         * Default: "This player is not in this Clan !"
         */
        public static final String PLAYER_NOT_BELONG_TO_CLAN =
                getPropertyOrDefault("clans.playerNotBelongToClan", "This player is not in this Clan !", CONFIG_FILE_NAME);

        /**
         * Error message when a specified clan cannot be found.
         * Default: "Clan specified doesn't exist."
         */
        public static final String CLAN_NOT_FOUND =
                getPropertyOrDefault("clans.clanNotFound", "Clan specified doesn't exist.", CONFIG_FILE_NAME);

        /**
         * Confirmation message when a player is successfully added to a clan.
         * Default: "You've been added to Clan :"
         */
        public static final String CLAN_ADDED =
                getPropertyOrDefault("clans.clanAdded", "You've been added to Clan :", CONFIG_FILE_NAME);

        /**
         * Notification message when a player is removed from a clan.
         * Default: "You've been removed from Clan :"
         */
        public static final String CLAN_REMOVED =
                getPropertyOrDefault("clans.clanRemoved", "You've been removed from Clan :", CONFIG_FILE_NAME);
    }

    /**
     * Configuration section for skill system messages, descriptions, and feedback.
     * Contains all text and color configurations for the skill system including
     * skill descriptions, upgrade messages, cooldown notifications, and ability effects.
     * 
     * This section is organized into several subsections:
     * - Color settings for skill-related UI elements
     * - Skill tree descriptions for different character backgrounds
     * - Menu and interface text
     * - Player feedback messages for skill usage
     * - Individual skill descriptions and effects
     */
    public static class Skills {
        /**
         * Color for skill-related feedback messages.
         * Default: GREEN
         */
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("skills.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        /**
         * Color for skill descriptions and explanatory text.
         * Default: WHITE
         */
        public static final ChatColor COLOR_DESCRIPTION =
                getPropertyOrDefault("skills.colors.description", ChatColor.WHITE, CONFIG_FILE_NAME);

        /**
         * Description for the Hunting skill tree.
         * Default: "Track your prey, unlock primal instincts."
         */
        public static final String HUNTING_DESCRIPTION =
                getPropertyOrDefault("skills.hunting.description", "Track your prey, unlock primal instincts.", CONFIG_FILE_NAME);

        /**
         * Description for the Navigation skill tree.
         * Default: "Master movement and memory of paths."
         */
        public static final String NAVIGATION_DESCRIPTION =
                getPropertyOrDefault("skills.navigation.description", "Master movement and memory of paths.", CONFIG_FILE_NAME);

        /**
         * Description for the Resilience skill tree.
         * Default: "Survive harder hits, help your clanmates."
         */
        public static final String RESILIENCE_DESCRIPTION =
                getPropertyOrDefault("skills.resilience.description", "Survive harder hits, help your clanmates.", CONFIG_FILE_NAME);

        /**
         * Description for the Herbalist skill tree.
         * Default: "Use herbs to heal, resist illness, and brew."
         */
        public static final String HERBALIST_DESCRIPTION =
                getPropertyOrDefault("skills.herbalist.description", "Use herbs to heal, resist illness, and brew.", CONFIG_FILE_NAME);

        /**
         * Description for the Kittypet character background and associated skills.
         * Default: "A cat raised in the warmth and comfort of Twoleg dens. Well-fed, pampered, and protected, but distant from the wild ways of the forest."
         */
        public static final String KITTYPET_DESCRIPTION =
                getPropertyOrDefault("skills.kittypet.description", "A cat raised in the warmth and comfort of Twoleg dens. Well-fed, pampered, and protected, but distant from the wild ways of the forest.", CONFIG_FILE_NAME);

        /**
         * Description for the Loner character background and associated skills.
         * Default: "A solitary wanderer who shuns Clans and Twolegs alike. Living by their own rules, trusting no one but themselves."
         */
        public static final String LONER_DESCRIPTION =
                getPropertyOrDefault("skills.loner.description", "A solitary wanderer who shuns Clans and Twolegs alike. Living by their own rules, trusting no one but themselves.", CONFIG_FILE_NAME);

        /**
         * Description for the Rogue character background and associated skills.
         * Default: "A fierce outcast, untamed and unpredictable. Rogues survive by tooth and claw, often causing trouble near Clan borders."
         */
        public static final String ROGUE_DESCRIPTION =
                getPropertyOrDefault("skills.rogue.description", "A fierce outcast, untamed and unpredictable. Rogues survive by tooth and claw, often causing trouble near Clan borders.", CONFIG_FILE_NAME);

        /**
         * Description for the City Cat character background and associated skills.
         * Default: "A streetwise feline, navigating alleyways and rooftops. Cunning and adaptable, they thrive in the bustling chaos of the Twolegplace."
         */
        public static final String CITY_CAT_DESCRIPTION =
                getPropertyOrDefault("skills.cityCat.description", "A streetwise feline, navigating alleyways and rooftops. Cunning and adaptable, they thrive in the bustling chaos of the Twolegplace.", CONFIG_FILE_NAME);

        /**
         * Description for BreezeClan-specific skills and abilities.
         * Default: "Skills from the fast BreezeClan."
         */
        public static final String BREEZE_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.breezeClan.description", "Skills from the fast BreezeClan.", CONFIG_FILE_NAME);

        /**
         * Description for EchoClan-specific skills and abilities.
         * Default: "Skills from the stealthy EchoClan."
         */
        public static final String ECHO_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.echoClan.description", "Skills from the stealthy EchoClan.", CONFIG_FILE_NAME);

        /**
         * Description for CreekClan-specific skills and abilities.
         * Default: "Skills from the water-wise CreekClan."
         */
        public static final String CREEK_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.creekClan.description", "Skills from the water-wise CreekClan.", CONFIG_FILE_NAME);

        /**
         * Description for ShadeClan-specific skills and abilities.
         * Default: "Skills from the elusive ShadeClan."
         */
        public static final String SHADE_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.shadeClan.description", "Skills from the elusive ShadeClan.", CONFIG_FILE_NAME);

        /**
         * Menu option text for exiting the skills menu.
         * Default: "Exit this menu."
         */
        public static final String MENU_EXIT =
                getPropertyOrDefault("skills.menu.exit", "Exit this menu.", CONFIG_FILE_NAME);

        /**
         * Menu display text showing the player's current skill points.
         * Default: "You have skill points :"
         */
        public static final String MENU_SKILL_POINTS =
                getPropertyOrDefault("skills.menu.skillPoints", "You have skill points :", CONFIG_FILE_NAME);

        /**
         * Message displayed when a skill is still on cooldown.
         * Default: "This skill is still in cooldown for :"
         */
        public static final String PLAYER_MESSAGE_COOLDOWN =
                getPropertyOrDefault("skills.playerMessages.cooldown", "This skill is still in cooldown for :", CONFIG_FILE_NAME);

        /**
         * Error message when a player lacks sufficient skill points to unlock a skill.
         * Default: "You have not enough points to buy this !"
         */
        public static final String PLAYER_MESSAGE_NOT_ENOUGH_POINTS =
                getPropertyOrDefault("skills.playerMessages.notEnoughPoints", "You have not enough points to buy this !", CONFIG_FILE_NAME);

        /**
         * Message displayed when trying to unlock a skill that's already unlocked or at max level.
         * Default: "You have already unlocked this ! (or you have reached max level)"
         */
        public static final String PLAYER_MESSAGE_ALREADY_UNLOCKED =
                getPropertyOrDefault("skills.playerMessages.alreadyUnlocked", "You have already unlocked this ! (or you have reached max level)", CONFIG_FILE_NAME);

        /**
         * Error message when trying to use inventory expansion without the Beast of Burden skill.
         * Default: "You don't have the Beast of Burden skill."
         */
        public static final String PLAYER_MESSAGE_BEAST_OF_BURDEN_NOT_UNLOCKED =
                getPropertyOrDefault("skills.playerMessages.beastOfBurdenNotUnlocked", "You don't have the Beast of Burden skill.", CONFIG_FILE_NAME);

        /**
         * Success message when successfully stealing from an NPC.
         * Default: "You discreetly stole from NPC !"
         */
        public static final String PLAYER_MESSAGE_STOLE_FROM_NPC =
                getPropertyOrDefault("skills.playerMessages.stoleFromNPC", "You discreetly stole from NPC !", CONFIG_FILE_NAME);

        /**
         * Success message when finding loot while scavenging trash.
         * Default: "You scavenged some trash and found a loot !"
         */
        public static final String PLAYER_MESSAGE_FOUND_TRASH_LOOT =
                getPropertyOrDefault("skills.playerMessages.foundTrashLoot", "You scavenged some trash and found a loot !", CONFIG_FILE_NAME);

        /**
         * Success message when catching a rat using the Rat Catcher skill.
         * Default: "You have caught a rat !"
         */
        public static final String PLAYER_MESSAGE_CAUGHT_RAT =
                getPropertyOrDefault("skills.playerMessages.caughtRat", "You have caught a rat !", CONFIG_FILE_NAME);

        /*
         * ===================================================================================
         * SKILL EFFECT MESSAGES
         * ===================================================================================
         * The following constants define player feedback messages for various skill effects,
         * combat abilities, and status changes. Each message follows the pattern:
         * 
         * - PLAYER_MESSAGE_[EFFECT]: Message shown to the player experiencing the effect
         * - PLAYER_MESSAGE_APPLIED_[EFFECT]: Message shown when applying effect to others
         * 
         * Categories include:
         * - Combat Effects: bleeding, staggered, poisoned
         * - Movement Skills: pathfinding boost, location awareness, trail memory
         * - Utility Skills: aqua balance, prey sense, hunters compass
         * - Clan Skills: hold on, on your paws (revival system)
         * 
         * All messages include thematic skill names (e.g., "Sharp Wind!", "Aqua Balance!")
         * followed by the mechanical effect description.
         * ===================================================================================
         */

        public static final String PLAYER_MESSAGE_BLEEDING =
                getPropertyOrDefault("skills.playerMessages.bleeding", "Sharp Wind ! You are bleeding !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_BLEEDING =
                getPropertyOrDefault("skills.playerMessages.appliedBleeding", "Sharp Wind ! You have applied bleeding !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_STAGGERED =
                getPropertyOrDefault("skills.playerMessages.staggered", "Stunning Blow ! You are staggered !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_STAGGERED =
                getPropertyOrDefault("skills.playerMessages.appliedStaggered", "Stunning Blow ! You have applied stagger !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_POISONED =
                getPropertyOrDefault("skills.playerMessages.poisoned", "Toxic Claws ! You are poisoned !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_POISONED =
                getPropertyOrDefault("skills.playerMessages.appliedPoisoned", "Toxic Claws ! You have applied poison !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_AQUA_BALANCE =
                getPropertyOrDefault("skills.playerMessages.appliedAquaBalance", "Aqua Balance ! You have caught some fresh fish !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_PREY_SENSE =
                getPropertyOrDefault("skills.playerMessages.appliedPreySense", "Prey Sense ! You attune your senses to the wild... Scanning for nearby prey.", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_HUNTERS_COMPASS =
                getPropertyOrDefault("skills.playerMessages.appliedHuntersCompass", "Hunter's Compass ! You grip the earth beneath your paws... The hunt is on.", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_PREPARE_LOW_SWEEP =
                getPropertyOrDefault("skills.playerMessages.prepareLowSweep", "You focus your weight... Preparing to sweep !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_LOW_SWEEP_NO_TARGET =
                getPropertyOrDefault("skills.playerMessages.lowSweepNoTarget", "No target nearby.", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_LOW_SWEEP =
                getPropertyOrDefault("skills.playerMessages.appliedLowSweep", "Low Sweep ! You sweep the legs of the target !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_IN_COMBAT =
                getPropertyOrDefault("skills.playerMessages.inCombat", "You cannot use this while fighting !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_PATHFINDING_BOOST =
                getPropertyOrDefault("skills.playerMessages.appliedPathfindingBoost", "Pathfinding Boost ! You can now run faster and jump higher !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_HOLD_ON =
                getPropertyOrDefault("skills.playerMessages.appliedHoldOn", "Hold On ! Triggered to protect :", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_HOLD_ON =
                getPropertyOrDefault("skills.playerMessages.holdOn", "You're downed instead of dying !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_HOLD_ON_SUCCUMBED =
                getPropertyOrDefault("skills.playerMessages.holdOnSuccumbed", "You succumbed...", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_ON_YOUR_PAWS =
                getPropertyOrDefault("skills.playerMessages.onYourPaws", "On Your Paws ! You are reviving :", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_ON_YOUR_PAWS_NOT_IN_CLAN =
                getPropertyOrDefault("skills.playerMessages.onYourPawsNotInClan", "This player is not in your clan.", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_ON_YOUR_PAWS_REVIVED =
                getPropertyOrDefault("skills.playerMessages.onYourPawsRevived", "On Your Paws ! You have been revived !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_LOCATION_AWARENESS_VISITED =
                getPropertyOrDefault("skills.playerMessages.locationAwarenessVisited", "Location Awareness ! You have located and stored a waypoint !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_LOCATION_AWARENESS =
                getPropertyOrDefault("skills.playerMessages.locationAwareness", "Location Awareness ! Switched to waypoint :", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_LOCATION_AWARENESS_NO_WAYPOINT =
                getPropertyOrDefault("skills.playerMessages.locationAwarenessNoWaypoint", "No stored waypoint.", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_TRAIL_MEMORY_NO_WAYPOINT =
                getPropertyOrDefault("skills.playerMessages.trailMemoryNoWaypoint", "No active waypoint to teleport to.", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_TRAIL_MEMORY =
                getPropertyOrDefault("skills.playerMessages.trailMemory", "Trail Memory ! You have been teleported to :", CONFIG_FILE_NAME);

        /*
         * ===================================================================================
         * INDIVIDUAL SKILL DESCRIPTIONS
         * ===================================================================================
         * The following constants provide concise descriptions for individual skills that
         * appear in tooltips, menus, and help text. Each description explains:
         * 
         * - The mechanical effect of the skill
         * - Duration, range, or magnitude when applicable
         * - Special conditions or requirements
         * 
         * Descriptions are organized by skill category:
         * - Hunting Skills: prey detection, tracking, stealth
         * - Navigation Skills: movement bonuses, waypoints, teleportation
         * - Resilience Skills: defensive abilities, revival, protection
         * - Herbalist Skills: herb usage, crafting, resistance
         * - Background Skills: clan-specific and role-specific abilities
         * ===================================================================================
         */

        public static final String PREY_SENSE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.preySense", "Reveal nearby prey (5s glowing, 25 blocks)", CONFIG_FILE_NAME);

        public static final String HUNTERS_COMPASS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.huntersCompass", "Points to closest huntable target (updates every 60s)", CONFIG_FILE_NAME);

        public static final String LOW_SWEEP_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.lowSweep", "Applies Slowness II to target (2.5s)", CONFIG_FILE_NAME);

        public static final String SILENT_PAW_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.silentPaw", "Reduces movement sound radius", CONFIG_FILE_NAME);

        public static final String BLOOD_HUNTER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.bloodHunter", "Higher chance for quality prey", CONFIG_FILE_NAME);

        public static final String EFFICIENT_KILL_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.efficientKill", "More XP/food on stealth kills", CONFIG_FILE_NAME);

        public static final String LOCATION_AWARENESS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.locationAwareness", "Cycle compass between known waypoints", CONFIG_FILE_NAME);

        public static final String PATHFINDING_BOOST_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.pathfindingBoost", "Grants Speed I and Jump I outside combat", CONFIG_FILE_NAME);

        public static final String TRAIL_MEMORY_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.trailMemory", "Recall landmarks instantly", CONFIG_FILE_NAME);

        public static final String ENDURANCE_TRAVELER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.enduranceTraveler", "Reduce hunger loss out of combat", CONFIG_FILE_NAME);

        public static final String CLIMBERS_GRACE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.climbersGrace", "Jump higher passively", CONFIG_FILE_NAME);

        public static final String HOLD_ON_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.holdOn", "Avoids death and enters downed state", CONFIG_FILE_NAME);

        public static final String ON_YOUR_PAWS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.onYourPaws", "Revive downed ally after 8s", CONFIG_FILE_NAME);

        public static final String IRON_HIDE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.ironHide", "+1 armor per tier", CONFIG_FILE_NAME);

        public static final String IMMUNE_SYSTEM_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.immuneSystem", "10% illness resistance per tier", CONFIG_FILE_NAME);

        public static final String THICK_COAT_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.thickCoat", "Cold resistance, weak to fire", CONFIG_FILE_NAME);

        public static final String HEARTY_APPETITE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.heartyAppetite", "Increases food saturation restoration per tier", CONFIG_FILE_NAME);

        public static final String BEAST_OF_BURDEN_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.beastOfBurden", "Adds inventory capacity per tier", CONFIG_FILE_NAME);

        public static final String HERB_KNOWLEDGE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.herbKnowledge", "Highlights herbs within 15 blocks", CONFIG_FILE_NAME);

        public static final String BREW_REMEDY_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.brewRemedy", "Brew cures using collected herbs", CONFIG_FILE_NAME);

        public static final String QUICK_GATHERER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.quickGatherer", "Collect herbs faster", CONFIG_FILE_NAME);

        public static final String BOTANICAL_LORE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.botanicalLore", "Unlock new recipes or uses", CONFIG_FILE_NAME);

        public static final String CLEAN_PAWS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.cleanPaws", "Reduce self-infection risk", CONFIG_FILE_NAME);

        public static final String WELL_FED_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.wellFed", "Heals faster when full.", CONFIG_FILE_NAME);

        public static final String PAMPERED_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.pampered", "Less likely to fall ill.", CONFIG_FILE_NAME);

        public static final String SHELTERED_MIND_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.shelteredMind", "Immune to fear effects.", CONFIG_FILE_NAME);

        public static final String TRACKER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.tracker", "Detect recent footsteps.", CONFIG_FILE_NAME);

        public static final String CRAFTY_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.crafty", "Use herbs more efficiently.", CONFIG_FILE_NAME);

        public static final String FLEXIBLE_MORALS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.flexibleMorals", "Can trade/steal from NPCs.", CONFIG_FILE_NAME);

        public static final String AMBUSHER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.ambusher", "+Sneak attack damage.", CONFIG_FILE_NAME);

        public static final String SCAVENGE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.scavenge", "Loot items from trash piles.", CONFIG_FILE_NAME);

        public static final String HARD_KNOCK_LIFE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.hardKnockLife", "+1 natural armor.", CONFIG_FILE_NAME);

        public static final String URBAN_NAVIGATION_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.urbanNavigation", "Speed boost on concrete/stone.", CONFIG_FILE_NAME);

        public static final String RAT_CATCHER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.ratCatcher", "Track and catch rats.", CONFIG_FILE_NAME);

        public static final String DISEASE_RESISTANCE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.diseaseResistance", "Reduced illness severity.", CONFIG_FILE_NAME);

        public static final String SPEED_OF_THE_MOOR_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.speedOfTheMoor", "+15% plains movement speed.", CONFIG_FILE_NAME);

        public static final String LIGHTSTEP_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.lightstep", "Reduced fall damage.", CONFIG_FILE_NAME);

        public static final String SHARP_WIND_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.sharpWind", "10% chance to bleed in open spaces.", CONFIG_FILE_NAME);

        public static final String THICK_PELT_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.thickPelt", "Reduces melee damage.", CONFIG_FILE_NAME);

        public static final String FOREST_COVER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.forestCover", "Camouflage in wooded biomes.", CONFIG_FILE_NAME);

        public static final String STUNNING_BLOW_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.stunningBlow", "Bonus stagger chance from elevated attacks.", CONFIG_FILE_NAME);

        public static final String STRONG_SWIMMER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.strongSwimmer", "Faster water movement.", CONFIG_FILE_NAME);

        public static final String AQUA_BALANCE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.aquaBalance", "Can fish for food.", CONFIG_FILE_NAME);

        public static final String WATERS_RESILIENCE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.watersResilience", "Hunger decays slower in wet zones.", CONFIG_FILE_NAME);

        public static final String NIGHTSTALKER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.nightstalker", "No night blindness.", CONFIG_FILE_NAME);

        public static final String TOXIC_CLAWS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.toxicClaws", "Poison on low-light hits.", CONFIG_FILE_NAME);

        public static final String SILENT_KILL_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.silentKill", "Bonus damage on sneak attacks.", CONFIG_FILE_NAME);

    }

    /**
     * Configuration section for illness and disease-related messages.
     * Contains all text and color configurations for the illness system including
     * infection notifications, disease progression messages, and illness effects.
     */
    public static class Illnesses {
        /**
         * Color for illness-related feedback messages and notifications.
         * Default: DARK_RED
         */
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("illnesses.colors.feedback", ChatColor.DARK_RED, CONFIG_FILE_NAME);

        /**
         * Message displayed when a player becomes ill.
         * Default: "You got sick !"
         */
        public static final String GOT_SICK =
                getPropertyOrDefault("illnesses.gotSick", "You got sick !", CONFIG_FILE_NAME);

        /**
         * Message displayed when a player's illness becomes more severe.
         * Default: "Your illness has worsened !"
         */
        public static final String ILLNESS_WORSENED =
                getPropertyOrDefault("illnesses.illnessWorsened", "Your illness has worsened !", CONFIG_FILE_NAME);

        /**
         * Message displayed when illness progression results in player death.
         * Default: "Your illness has worsened and you are dead !"
         */
        public static final String ILLNESS_WORSENED_DEATH =
                getPropertyOrDefault("illnesses.illnessWorsenedDeath", "Your illness has worsened and you are dead !", CONFIG_FILE_NAME);

        /**
         * Atmospheric text displayed for rabies illness effects.
         * Default: "* raspy growl *"
         */
        public static final String GROWL_RABIES =
                getPropertyOrDefault("illnesses.growlRabies", "* raspy growl *", CONFIG_FILE_NAME);

        /**
         * Message displayed when wounds progress to become infected.
         * Default: "Your wounds have become infected!"
         */
        public static final String WOUNDS_INFECTED =
                getPropertyOrDefault("illnesses.woundsInfected", "Your wounds have become infected!", CONFIG_FILE_NAME);

    }

    /**
     * Configuration section for herb-related messages and feedback.
     * Contains all text and color configurations for the herb system including
     * foraging notifications, treatment messages, and herb discovery feedback.
     */
    public static class Herbs {
        /**
         * Color for herb-related feedback messages and notifications.
         * Default: GREEN
         */
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("herbs.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        /**
         * Message displayed when finding herbs while foraging.
         * Default: "You found:"
         */
        public static final String HERB_FOUND =
                getPropertyOrDefault("herbs.herbFound", "You found:", CONFIG_FILE_NAME);

        /**
         * Message displayed when finding rare Lunarfang.
         * Default: "You found rare Lunarfang!"
         */
        public static final String LUNARFANG_FOUND =
                getPropertyOrDefault("herbs.lunarfangFound", "You found rare Lunarfang!", CONFIG_FILE_NAME);

        /**
         * Message displayed when herb cannot treat player's illnesses.
         * Default: "You don't have any illness that can treat:"
         */
        public static final String HERB_CANNOT_TREAT =
                getPropertyOrDefault("herbs.herbCannotTreat", "You don't have any illness that can treat:", CONFIG_FILE_NAME);

        /**
         * Message displayed when cobwebs are needed for wound treatment.
         * Default: "You need to treat wounds properly :"
         */
        public static final String NEED_COBWEBS =
                getPropertyOrDefault("herbs.needCobwebs", "You need to treat wounds properly :", CONFIG_FILE_NAME);

        /**
         * Message displayed when successfully curing an illness.
         * Default: "You cured:"
         */
        public static final String ILLNESS_CURED =
                getPropertyOrDefault("herbs.illnessCured", "You cured:", CONFIG_FILE_NAME);
    }
}
