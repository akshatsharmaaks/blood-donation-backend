package com.blooddonation.model;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.DonorRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donor_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The donor making the offer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_profile_id", nullable = false)
    private DonorProfile donorProfile;

    // Optional — donor can respond to a specific receiver request
    // or make a general open offer (null = open offer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_request_id")
    private ReceiverRequest receiverRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BloodGroup bloodGroup;

    @Column(nullable = false)
    private Integer unitsOffered;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DonorRequestStatus status = DonorRequestStatus.PENDING;

    // Preferred donation date
    private LocalDate preferredDate;

    private String city;
    private String state;

    @Column(length = 500)
    private String message;     // Optional note from donor

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}