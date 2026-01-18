package org.warriorcats.pawsOfTheForest.skills.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity representing a skill branch in the database.
 * 
 * <p>This entity stores information about a player's progress within a specific skill branch,
 * including all individual skills they have unlocked or progressed in that branch.
 * Each skill branch contains multiple skills with their own progress values.</p>
 * 
 * <p>The entity uses eager loading for skills to ensure all skill data is available
 * when the branch is loaded, which is important for skill calculations and UI display.</p>
 * 
 * @author PawsOfTheForest Development Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Entity
@Table(name = "skill_branches")
public class SkillBranchEntity {

    /** Unique identifier for this skill branch record */
    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    /** The type of skill branch this entity represents */
    @Column(name = "branch")
    @Enumerated(EnumType.STRING)
    private SkillBranches branch;

    /** 
     * List of all skills within this branch that the player has made progress on.
     * Uses eager loading to ensure skill data is always available.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SkillEntity> skills = new ArrayList<>();
}
