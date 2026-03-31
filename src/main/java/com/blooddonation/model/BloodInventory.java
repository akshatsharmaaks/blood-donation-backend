package com.blooddonation.model;

import com.blooddonation.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hospital_id", "blood_group"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Column(nullable = false)
    @Builder.Default
    private Integer unitsAvailable = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer minimumThreshold = 5;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;
}