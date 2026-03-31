package com.blooddonation.model;

import com.blooddonation.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Double weightKg;

    private String city;
    private String state;
    private String country;

    @Column(nullable = false)
    @Builder.Default
    private Double latitude = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double longitude = 0.0;

    private LocalDate lastDonationDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean hasMedicalCondition = false;

    private String medicalNotes;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalDonations = 0;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}