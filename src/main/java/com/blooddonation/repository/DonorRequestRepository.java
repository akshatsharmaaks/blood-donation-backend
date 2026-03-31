package com.blooddonation.repository;

import com.blooddonation.enums.DonorRequestStatus;
import com.blooddonation.model.DonorRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorRequestRepository extends JpaRepository<DonorRequest, Long> {

    // All offers made by a specific donor
    List<DonorRequest> findByDonorProfileIdOrderByCreatedAtDesc(Long donorProfileId);

    // All offers made against a specific receiver request
    List<DonorRequest> findByReceiverRequestIdOrderByCreatedAtDesc(Long receiverRequestId);

    // All open (general) offers not tied to any receiver request
    List<DonorRequest> findByReceiverRequestIsNullAndStatus(DonorRequestStatus status);

    // Check if donor already offered for this specific receiver request
    boolean existsByDonorProfileIdAndReceiverRequestId(Long donorProfileId,
                                                       Long receiverRequestId);
}