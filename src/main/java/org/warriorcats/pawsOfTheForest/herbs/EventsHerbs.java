package org.warriorcats.pawsOfTheForest.herbs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.illnesses.IllnessEntity;
import org.warriorcats.pawsOfTheForest.illnesses.Illnesses;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.BiomesUtils;
import org.warriorcats.pawsOfTheForest.utils.BlocksUtils;
import org.warriorcats.pawsOfTheForest.utils.BlocksUtils;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class EventsHerbs implements LoadingListener {

    private final Map<UUID, Long> lastHerbSearch = new ConcurrentHashMap<>();
    private static final long HERB_SEARCH_COOLDOWN_MS = 30000;

    @Override
    public void load() {
        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            long now = System.currentTimeMillis();
            lastHerbSearch.entrySet().removeIf(entry -> now - entry.getValue() > HERB_SEARCH_COOLDOWN_MS);
        }, 0L, 20 * 60);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block blockBelow = event.getTo().clone().subtract(0, 1, 0).getBlock();
        UUID playerId = player.getUniqueId();

        if (!BlocksUtils.isGrass(blockBelow)) {
            return;
        }

        if (lastHerbSearch.containsKey(playerId) && 
            System.currentTimeMillis() - lastHerbSearch.get(playerId) < HERB_SEARCH_COOLDOWN_MS) {
            return;
        }

        // Try to find herbs
        for (Herbs herb : Herbs.values()) {
            if (Math.random() < herb.getFindChance()) {
                int quantity = herb == Herbs.COBWEBS ? 1 + (int)(Math.random() * 3) : 1;
                ItemStack herbItem = herb.createCustomItem(quantity);
                
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(herbItem);
                } else {
                    player.getWorld().dropItem(player.getLocation(), herbItem);
                }
                
                player.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Herbs.HERB_FOUND + " " + quantity + " x " + herb + "!");
                lastHerbSearch.put(playerId, System.currentTimeMillis());
                return;
            }
        }

        // Special Lunarfang
        if (BiomesUtils.isFullMoon(player.getWorld()) && BiomesUtils.isHighMountain(blockBelow.getLocation())) {
            if (Math.random() < Herbs.LUNARFANG.getFindChance()) {
                ItemStack lunarfang = Herbs.LUNARFANG.createCustomItem(1);
                
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(lunarfang);
                } else {
                    player.getWorld().dropItem(player.getLocation(), lunarfang);
                }
                
                player.sendMessage(MessagesConf.Herbs.COLOR_FEEDBACK + MessagesConf.Herbs.LUNARFANG_FOUND);
                lastHerbSearch.put(playerId, System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        UUID playerId = player.getUniqueId();

        // Check if it's a plant/herb block that can yield herbs
        if (!BlocksUtils.isGrass(block)) {
            return;
        }

        // Apply cooldown like foraging
        if (lastHerbSearch.containsKey(playerId) && 
            System.currentTimeMillis() - lastHerbSearch.get(playerId) < HERB_SEARCH_COOLDOWN_MS) {
            return;
        }

        // Try to find herbs with lower chance than foraging
        for (Herbs herb : Herbs.values()) {
            // Reduce find chance by half for breaking blocks
            if (Math.random() < (herb.getFindChance() * 0.5)) {
                int quantity = herb == Herbs.COBWEBS ? 1 + (int)(Math.random() * 2) : 1;
                ItemStack herbItem = herb.createCustomItem(quantity);
                
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(herbItem);
                } else {
                    player.getWorld().dropItem(player.getLocation(), herbItem);
                }
                
                player.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Herbs.HERB_FOUND + " " + quantity + " x " + herb + "!");
                lastHerbSearch.put(playerId, System.currentTimeMillis());
                return;
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        // Only process right clicks
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && 
            event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        // Check if player is holding a herb
        Optional<Herbs> herb = Herbs.getHerbFromItem(item);
        if (herb.isEmpty()) return;

        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());

        // Try to treat illnesses with this herb
        for (Illnesses illness : herb.get().getTreatedIllnesses()) {
            if (entity.hasIllness(illness)) {
                treatIllness(player, herb.get(), illness);
                return;
            }
        }

        player.sendMessage(MessagesConf.Herbs.COLOR_FEEDBACK + MessagesConf.Herbs.HERB_CANNOT_TREAT + " " + herb.get());
    }

    private void treatIllness(Player player, Herbs herb, Illnesses illness) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        
        // Special case: wounds need both marigold/cobwebs
        if (illness == Illnesses.WOUNDS && herb == Herbs.MARIGOLD) {
            if (!hasHerb(player, Herbs.COBWEBS)) {
                player.sendMessage(MessagesConf.Herbs.COLOR_FEEDBACK + MessagesConf.Herbs.NEED_COBWEBS + " " + Herbs.COBWEBS);
                return;
            }
            consumeHerb(player, Herbs.COBWEBS);
        }

        // Consume the herb
        consumeHerb(player, herb);

        // Remove illness effects
        for (PotionEffectType effect : illness.getPotionEffects().keySet()) {
            player.removePotionEffect(effect);
        }

        // Remove illness from database
        HibernateUtils.withTransaction((transaction, session) -> {
            IllnessEntity illnessEntity = entity.getIllness(illness);
            entity.getIllnesses().remove(illnessEntity);
            session.remove(illnessEntity);
            return entity;
        });

        player.sendMessage(MessagesConf.Herbs.COLOR_FEEDBACK + MessagesConf.Herbs.ILLNESS_CURED + " " + illness + " with " + herb + "!");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
    }

    private boolean hasHerb(Player player, Herbs herb) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Herbs.getHerbFromItem(item).isPresent() && 
                Herbs.getHerbFromItem(item).get() == herb && item.getAmount() > 0) {
                return true;
            }
        }
        return false;
    }

    private void consumeHerb(Player player, Herbs herb) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Herbs.getHerbFromItem(item).isPresent() && 
                Herbs.getHerbFromItem(item).get() == herb && item.getAmount() > 0) {
                item.setAmount(item.getAmount() - 1);
                break;
            }
        }
    }
}