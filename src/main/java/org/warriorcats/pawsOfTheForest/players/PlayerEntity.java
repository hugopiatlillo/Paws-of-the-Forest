package org.warriorcats.pawsOfTheForest.players;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.clans.Clans;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.illnesses.IllnessEntity;
import org.warriorcats.pawsOfTheForest.illnesses.Illnesses;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillEntity;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Entity representing a player in the Warrior Cats roleplay system.
 * 
 * This entity stores all persistent player data including:
 * - Basic player information (UUID, name, biography)
 * - Character vitals (thirst, energy, hygiene, social needs)
 * - Clan membership and progression data
 * - Skill trees and abilities
 * - Illness tracking
 * - Inventory data (backpack serialization)
 * 
 * The entity uses Hibernate annotations for ORM mapping and Lombok for
 * automatic getter/setter generation.
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "players")
public class PlayerEntity {

    /** Player's unique identifier from Minecraft */
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    /** Player's current display name */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /** Player's character biography for roleplay */
    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    /** When the character was born (for age calculation) */
    @Column(name = "birth_date", nullable = false)
    private Instant birthDate;

    /** Experience points available for skill upgrades */
    @Column(name = "xp_perks", nullable = false)
    private double xpPerks;

    /** Thirst level (0.0 to 1.0) */
    @Column(name = "thirst", nullable = false)
    private double thirst = 1;

    /** Energy level (0.0 to 1.0) */
    @Column(name = "energy", nullable = false)
    private double energy = 1;

    /** Hygiene level (0.0 to 1.0) */
    @Column(name = "hygiene", nullable = false)
    private double hygiene = 1;

    /** Social need level (0.0 to 1.0) */
    @Column(name = "social", nullable = false)
    private double social = 1;

    /** In-game currency amount */
    @Column(name = "coins", nullable = false)
    private long coins;

    /** Player's current clan membership (can be null for rogues/loners) */
    @Enumerated(EnumType.STRING)
    @Column(name = "clan")
    private Clans clan;

    /** Player's configuration settings */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_uuid", nullable = false)
    private SettingsEntity settings;

    /** Player's skill tree progress across all branches */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SkillBranchEntity> skillBranches = new ArrayList<>();

    /** Active illnesses affecting the player */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<IllnessEntity> illnesses = new ArrayList<>();

    /** Serialized inventory data for the backpack system */
    @Lob
    private byte[] backpackData;

    /**
     * Sets the thirst level, automatically clamping between 0.0 and 1.0.
     * 
     * @param thirst The new thirst level
     */
    public void setThirst(double thirst) {
        this.thirst = Math.max(0, Math.min(thirst, 1.0));
    }

    /**
     * Sets the energy level, automatically clamping between 0.0 and 1.0.
     * 
     * @param energy The new energy level
     */
    public void setEnergy(double energy) {
        this.energy = Math.max(0, Math.min(energy, 1.0));
    }

    /**
     * Sets the hygiene level, automatically clamping between 0.0 and 1.0.
     * 
     * @param hygiene The new hygiene level
     */
    public void setHygiene(double hygiene) {
        this.hygiene = Math.max(0, Math.min(hygiene, 1.0));
    }

    /**
     * Sets the social need level, automatically clamping between 0.0 and 1.0.
     * 
     * @param social The new social level
     */
    public void setSocial(double social) {
        this.social = Math.max(0, Math.min(social, 1.0));
    }

    /**
     * Calculates the character's age in Minecraft days since birth.
     * 
     * @return Age in days
     */
    public long getAgeInMinecraftDays() {
        return Duration.between(birthDate, Instant.now()).toDays();
    }

    /**
     * Checks if the player has a specific illness.
     * 
     * @param illness The illness to check for
     * @return true if the player has the illness
     */
    public boolean hasIllness(Illnesses illness) {
        return illnesses.stream().anyMatch(illnessEntity -> illnessEntity.getIllness() == illness);
    }

    /**
     * Gets the illness entity for a specific illness type.
     * 
     * @param illness The illness type to retrieve
     * @return The illness entity
     * @throws IllegalArgumentException if the player doesn't have the illness
     */
    public IllnessEntity getIllness(Illnesses illness) {
        return illnesses.stream()
                .filter(illnessEntity -> illnessEntity.getIllness() == illness)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not find illness: " + illness + " for player: " + name));
    }

    /**
     * Checks if the player has unlocked a specific skill.
     * 
     * @param skill The skill to check
     * @return true if the player has the skill
     */
    public boolean hasAbility(Skills skill) {
        return getAbilityInternal(skill).isPresent();
    }

    /**
     * Gets the current tier level of a skill based on the player's progress.
     * 
     * @param skill The skill to check
     * @return The current tier (0 if not unlocked)
     */
    public int getAbilityTier(Skills skill) {
        return skill.getCurrentTier(getAbilityPerk(skill));
    }

    /**
     * Gets the current progress/experience points in a specific skill.
     * 
     * @param skill The skill to check
     * @return The progress amount (0.0 if not unlocked)
     */
    public double getAbilityPerk(Skills skill) {
        return getAbilityInternal(skill).map(SkillEntity::getProgress).orElse(0d);
    }

    /**
     * Gets the skill entity for a specific skill.
     * 
     * @param skill The skill to retrieve
     * @return The skill entity or null if not unlocked
     */
    public SkillEntity getAbility(Skills skill) {
        return getAbilityInternal(skill).orElse(null);
    }

    /**
     * Gets the skill branch entity that contains the specified skill.
     * 
     * @param skill The skill whose branch to find
     * @return The skill branch entity
     * @throws IllegalArgumentException if the branch is not found
     */
    public SkillBranchEntity getAbilityBranch(Skills skill) {
        for (SkillBranchEntity branche : skillBranches) {
            if (branche.getBranch() == skill.getBranch()) {
                return branche;
            }
        }
        throw new IllegalArgumentException("Could not find ability branch for skill : " + skill);
    }

    /**
     * Internal helper method to find a skill entity across all skill branches.
     * 
     * @param skill The skill to search for
     * @return Optional containing the skill entity if found
     */
    private Optional<SkillEntity> getAbilityInternal(Skills skill) {
        Optional<SkillEntity> skillEntity = Optional.empty();
        for (SkillBranchEntity branche : skillBranches) {
            skillEntity = branche.getSkills().stream()
                .filter(other -> other.getSkill() == skill)
                .findFirst();
            if (skillEntity.isPresent()) {
                return skillEntity;
            }
        }
        return skillEntity;
    }
}
