package org.warriorcats.pawsOfTheForest.skills.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.util.UUID;

/**
 * JPA entity representing an individual skill and its progress in the database.
 * 
 * <p>This entity stores the player's progress for a specific skill within a skill branch.
 * Progress is stored as a double value to accommodate both binary unlocked states
 * (for active skills) and tiered progression (for passive skills).</p>
 * 
 * <p>Progress interpretation:</p>
 * <ul>
 *   <li><b>Active Skills:</b> Progress > 0 means unlocked, 0 means locked</li>
 *   <li><b>Passive Skills:</b> Progress / 2 = current tier level</li>
 * </ul>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Entity
@Table(name = "skills")
public class SkillEntity {

    /** Unique identifier for this skill record */
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    /** The specific skill this entity represents */
    @Enumerated(EnumType.STRING)
    @Column(name = "skill", nullable = false)
    private Skills skill;

    /** 
     * Progress value for this skill.
     * For active skills: > 0 = unlocked, 0 = locked
     * For passive skills: progress / 2 = tier level
     */
    @Column(name = "progress", nullable = false)
    private double progress;
}
