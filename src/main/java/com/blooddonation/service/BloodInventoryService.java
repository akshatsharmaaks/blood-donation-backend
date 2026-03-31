package com.blooddonation.service;

import com.blooddonation.dto.request.InventoryUpdateRequest;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodInventoryService {

    private final BloodInventoryRepository inventoryRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;

    public ApiResponse<List<BloodInventory>> getMyInventory() {
        Hospital hospital = getCurrentHospital();
        return ApiResponse.success("Inventory fetched",
                inventoryRepository.findByHospital(hospital));
    }

    @Transactional
    public ApiResponse<BloodInventory> updateInventory(InventoryUpdateRequest dto) {
        Hospital hospital = getCurrentHospital();

        BloodInventory inventory = inventoryRepository
                .findByHospitalAndBloodGroup(hospital, dto.getBloodGroup())
                .orElse(BloodInventory.builder()
                        .hospital(hospital)
                        .bloodGroup(dto.getBloodGroup())
                        .build());

        inventory.setUnitsAvailable(dto.getUnitsAvailable());
        if (dto.getMinimumThreshold() != null) {
            inventory.setMinimumThreshold(dto.getMinimumThreshold());
        }

        inventoryRepository.save(inventory);
        return ApiResponse.success("Inventory updated", inventory);
    }

    public ApiResponse<List<BloodInventory>> getAvailableByBloodGroup(
            com.blooddonation.enums.BloodGroup bloodGroup) {
        return ApiResponse.success("Available inventory",
                inventoryRepository.findAvailableByBloodGroup(bloodGroup));
    }

    private Hospital getCurrentHospital() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return hospitalRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
    }
}