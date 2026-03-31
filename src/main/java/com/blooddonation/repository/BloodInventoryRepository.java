package com.blooddonation.repository;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.model.BloodInventory;
import com.blooddonation.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {
    List<BloodInventory> findByHospital(Hospital hospital);
    Optional<BloodInventory> findByHospitalAndBloodGroup(Hospital hospital, BloodGroup bloodGroup);

    // Add this method
    Optional<BloodInventory> findByHospitalIdAndBloodGroup(Long hospitalId, BloodGroup bloodGroup);

    // Useful for showing the full inventory
    List<BloodInventory> findByHospitalId(Long hospitalId);

    @Query("""
        SELECT i FROM BloodInventory i
        WHERE i.bloodGroup = :bloodGroup
        AND i.unitsAvailable > 0
        AND i.hospital.isVerified = true
    """)
    List<BloodInventory> findAvailableByBloodGroup(BloodGroup bloodGroup);
}