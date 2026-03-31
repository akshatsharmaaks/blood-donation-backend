package com.blooddonation.repository;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.model.DonorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {

    Optional<DonorProfile> findByUserId(Long userId);

    List<DonorProfile> findByBloodGroupAndIsAvailableTrue(BloodGroup bloodGroup);

    List<DonorProfile> findByCityIgnoreCaseAndIsAvailableTrue(String city);

    @Query("""
        SELECT d FROM DonorProfile d
        WHERE d.isAvailable = true
        AND d.bloodGroup IN :bloodGroups
        AND d.city = :city
        ORDER BY d.lastDonationDate ASC NULLS FIRST
    """)
    List<DonorProfile> findEligibleDonors(
            @Param("bloodGroups") List<BloodGroup> bloodGroups,
            @Param("city") String city);

    @Query("""
        SELECT d FROM DonorProfile d
        WHERE d.isAvailable = true
        AND d.bloodGroup IN :bloodGroups
        ORDER BY d.lastDonationDate ASC NULLS FIRST
    """)
    List<DonorProfile> findEligibleDonorsByBloodGroups(
            @Param("bloodGroups") List<BloodGroup> bloodGroups);
}