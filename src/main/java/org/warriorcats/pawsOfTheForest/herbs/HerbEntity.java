package org.warriorcats.pawsOfTheForest.herbs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "herbs")
@Getter
@Setter
public class HerbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Herbs herb;

    @Column(nullable = false)
    private int quantity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "collected_at", nullable = false)
    private Date collectedAt;
}