package com.blooddonation.repository;

import com.blooddonation.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByUserId(Long userId);
    List<Hospital> findByIsVerifiedTrue();
    Boolean existsByLicenseNumber(String licenseNumber);
}