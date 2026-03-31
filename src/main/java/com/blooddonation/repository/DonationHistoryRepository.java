package com.blooddonation.repository;

import com.blooddonation.model.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Long> {
    List<DonationHistory> findByDonorIdOrderByDonationDateDesc(Long donorId);
    long countByDonorId(Long donorId);
}