package org.warriorcats.pawsOfTheForest.herbs;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.illnesses.Illnesses;
import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

import java.util.List;

import java.util.Optional;
import java.util.Set;

/**
 * Enumeration of all herbs available in the Warrior Cats medicine system.
 * 
 * Each herb has specific properties including:
 * - The material representation in Minecraft
 * - A find chance when foraging (probability of discovery)
 * - Set of illnesses it can treat
 * - Descriptive text explaining its medical uses
 * 
 * Herbs are used by players with herbalist skills to treat various illnesses
 * and conditions. Some herbs like Lunarfang are extremely rare and treat
 * serious conditions like rabies.
 * 
 * The find chance determines how likely a player is to discover this herb
 * when foraging in appropriate biomes and conditions.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
@Getter
public enum Herbs {
    /** Common herb for respiratory infections (15% find chance) */
    CATMINT(Material.FERN, 0.15, Set.of(Illnesses.UPPER_RESPIRATORY_INFECTION), "Primary treatment for greencough"),
    
    /** Versatile herb for multiple conditions (12% find chance) */
    CHAMOMILE(Material.DANDELION, 0.12, Set.of(Illnesses.UPPER_RESPIRATORY_INFECTION, Illnesses.HEATSTROKE, Illnesses.POISONING), "Soothes nausea and inflammation"),
    
    /** Purgative herb for parasites and poisoning (8% find chance) */
    YARROW(Material.WHEAT, 0.08, Set.of(Illnesses.INTERNAL_PARASITES, Illnesses.POISONING), "Induces vomiting to purge parasites"),
    
    /** Multi-purpose herb for energy and joint issues (10% find chance) */
    GOLDENROD(Material.SUNFLOWER, 0.10, Set.of(Illnesses.EXTERNAL_PARASITES, Illnesses.INTERNAL_PARASITES, Illnesses.HEATSTROKE, Illnesses.SEIZURES, Illnesses.ARTHRITIS), "Helps with energy loss and joint flexibility"),
    
    /** Antiseptic herb for wound care (9% find chance) */
    MARIGOLD(Material.ORANGE_TULIP, 0.09, Set.of(Illnesses.WOUNDS), "Cleanses wounds and prevents infection"),
    
    /** Antibiotic herb for existing infections (7% find chance) */
    BURDOCK_ROOT(Material.POTATO, 0.07, Set.of(Illnesses.INFECTED_WOUNDS), "Fights existing infections"),
    
    /** Healing herb for tissue and bone repair (6% find chance) */
    COMFREY(Material.KELP, 0.06, Set.of(Illnesses.FROSTBITE, Illnesses.BROKEN_BONES), "Restores tissue and repairs bones"),
    
    /** Pain relief herb with sedative properties (5% find chance) */
    POPPY_SEEDS(Material.POPPY, 0.05, Set.of(Illnesses.BROKEN_BONES, Illnesses.SEIZURES, Illnesses.ARTHRITIS), "Pain management and sedative"),
    
    /** Ultra-rare herb that only appears during full moon (1% find chance) */
    LUNARFANG(Material.GLOWSTONE_DUST, 0.01, Set.of(Illnesses.RABIES), "Glowing herb found only at full moon, cures rabies"),
    
    /** Crafted item, not found naturally (0% find chance) */
    MOUSEBILE(Material.SLIME_BALL, 0.0, Set.of(Illnesses.EXTERNAL_PARASITES), "Crafted from mouse + toxic herb"),
    
    /** Common material for wound dressing (20% find chance) */
    COBWEBS(Material.COBWEB, 0.20, Set.of(Illnesses.WOUNDS), "Used to stop bleeding from wounds");

    /** The Minecraft material used to represent this herb */
    private final Material material;
    /** Probability of finding this herb when foraging (0.0 to 1.0) */
    private final double findChance;
    /** Set of illnesses this herb can treat */
    private final Set<Illnesses> treatedIllnesses;
    /** Description of the herb's medical properties */
    private final String description;

    /**
     * Constructor for herb enum values.
     * 
     * @param material The Minecraft material representing this herb
     * @param findChance The probability of finding this herb (0.0 to 1.0)
     * @param treatedIllnesses Set of illnesses this herb can treat
     * @param description Medical description of the herb's properties
     */
    Herbs(Material material, double findChance, Set<Illnesses> treatedIllnesses, String description) {
        this.material = material;
        this.findChance = findChance;
        this.treatedIllnesses = treatedIllnesses;
        this.description = description;
    }

    /**
     * Returns the display name of the herb with proper capitalization and spacing.
     * 
     * @return The formatted display name
     */
    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    /**
     * Parses a herb from its string representation.
     * 
     * @param herbStr The string representation of the herb
     * @return The corresponding Herbs enum value
     * @throws IllegalArgumentException if the string doesn't match any herb
     */
    public static Herbs from(String herbStr) {
        return EnumsUtils.from(herbStr, Herbs.class);
    }

    /**
     * Gets the herb enum value from a custom ItemStack.
     *
     * @param item The ItemStack to check
     * @return The corresponding Herbs enum value, or empty if not found
     */
    public static Optional<Herbs> getHerbFromItem(ItemStack item) {
        for (Herbs herb : values()) {
            if (ItemsUtils.isSameItem(herb.createCustomItem(1), item)) {
                return Optional.of(herb);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if this herb can treat a specific illness.
     * 
     * @param illness The illness to check treatment for
     * @return true if this herb can treat the given illness
     */
    public boolean canTreat(Illnesses illness) {
        return treatedIllnesses.contains(illness);
    }

    /**
     * Creates a custom ItemStack for this herb with proper name, lore, and metadata.
     * 
     * @param quantity The number of herbs to create
     * @return A custom ItemStack representing this herb
     */
    public ItemStack createCustomItem(int quantity) {
        ItemStack item = new ItemStack(material, quantity);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Set custom name with green color
            meta.setDisplayName(ChatColor.GREEN + toString());
            
            // Set lore with description and treated illnesses
            meta.setLore(List.of(
                ChatColor.GRAY + description,
                ChatColor.DARK_GRAY + "Find chance: " + (int)(findChance * 100) + "%",
                ChatColor.YELLOW + "Treats: " + getTreatedIllnessesString()
            ));
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    /**
     * Gets a formatted string of all illnesses this herb can treat.
     * 
     * @return A comma-separated string of treatable illnesses
     */
    private String getTreatedIllnessesString() {
        if (treatedIllnesses.isEmpty()) {
            return "None";
        }
        return treatedIllnesses.stream()
                .map(Illnesses::toString)
                .reduce((a, b) -> a + ", " + b)
                .orElse("None");
    }
}