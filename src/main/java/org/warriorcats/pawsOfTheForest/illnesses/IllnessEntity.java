package org.warriorcats.pawsOfTheForest.illnesses;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

/**
 * JPA Entity representing an illness instance affecting a player in the Warrior Cats system.
 * 
 * This entity tracks individual occurrences of illnesses, storing when the illness was contracted
 * and providing methods to determine the severity and progression state of the illness.
 * Each illness instance is uniquely identified and linked to the specific illness type through
 * the {@link Illnesses} enum.
 * 
 * The entity supports illness progression mechanics where certain illnesses can worsen over time
 * if left untreated, affecting the severity of symptoms (amplifier level) applied to the player.
 * 
 * Database mapping:
 * - Table: "illnesses"
 * - Primary key: UUID (auto-generated)
 * - Foreign key relationship with player entities through collections
 * 
 * @author Warrior Cats Development Team
 * @since 1.0.0
 * @see Illnesses
 * @see org.warriorcats.pawsOfTheForest.players.PlayerEntity
 */
@Data
@Entity
@Table(name = "illnesses")
public class IllnessEntity {

    /**
     * Unique identifier for this illness instance.
     * Automatically generated when the entity is created.
     */
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    /**
     * The type of illness affecting the player.
     * Stored as a string representation of the enum value in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "illness", nullable = false)
    private Illnesses illness;

    /**
     * Timestamp indicating when the player contracted this illness.
     * Used to calculate illness progression and determine if the condition has worsened.
     */
    @Column(name = "got_at", nullable = false)
    private Date gotAt;

    /**
     * Calculates the amplifier level for potion effects based on illness progression.
     * 
     * The amplifier determines the severity of symptoms applied to the player:
     * - 0: Normal illness symptoms (base level)
     * - 2: Worsened illness symptoms (increased severity)
     * 
     * Progression is determined by comparing the time elapsed since contraction
     * against the illness's natural progression timeline. Illnesses that don't
     * progress over time (minecraftDaysBeforeWorsened = 0) always return amplifier 0.
     * 
     * Time calculation:
     * - 1 Minecraft day = 20 minutes real time = 1,200,000 milliseconds
     * - Threshold = days * 20 * 60 * 1000 milliseconds
     * 
     * @return 0 for normal symptoms, 2 for worsened symptoms
     */
    public int getAmplifier() {
        // Non-progressive illnesses maintain base amplifier
        if (illness.getMinecraftDaysBeforeWorsened() == 0) {
            return 0;
        }
        
        // Calculate time elapsed since contraction
        long elapsed = new Date().getTime() - gotAt.getTime();
        // Convert Minecraft days to milliseconds (1 MC day = 20 real minutes)
        long threshold = (long) illness.getMinecraftDaysBeforeWorsened() * 20 * 60 * 1000;
        
        // Return worsened amplifier if threshold exceeded
        return elapsed >= threshold ? 2 : 0;
    }

    /**
     * Determines if the illness has progressed to a worsened state.
     * 
     * A worsened illness applies more severe symptoms to the player and may
     * become fatal if the illness type supports fatality. This method provides
     * a convenient boolean check for illness progression status.
     * 
     * @return true if the illness has worsened (amplifier > 0), false otherwise
     */
    public boolean isWorsened() {
        return getAmplifier() > 0;
    }
}
