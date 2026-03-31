package com.blooddonation.model;


import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "receiver_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiverRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Column(nullable = false)
    private Integer unitsRequired;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private String patientName;
    private String hospital;
    private String city;
    private String state;

    @Column(nullable = false)
    @Builder.Default
    private Double latitude = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Double longitude = 0.0;

    @Column(length = 1000)
    private String notes;

    private String urgencyLevel;   // LOW, MEDIUM, HIGH, CRITICAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_donor_id")
    private DonorProfile matchedDonor;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}