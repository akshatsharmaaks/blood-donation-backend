package com.blooddonation.service;

import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.enums.RequestStatus;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.model.BloodInventory;
import com.blooddonation.model.Hospital;
import com.blooddonation.model.ReceiverRequest;
import com.blooddonation.model.User;
import com.blooddonation.repository.BloodInventoryRepository;
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.ReceiverRequestRepository;
import com.blooddonation.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final ReceiverRequestRepository receiverRequestRepository;
    private final BloodInventoryRepository inventoryRepository;

    @Transactional
    public ApiResponse<Hospital> registerHospital(HospitalRequest dto) {
        User user = getCurrentUser();

        if (hospitalRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("Hospital profile already exists");
        }
        if (hospitalRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new IllegalArgumentException("License number already registered");
        }

        Hospital hospital = Hospital.builder()
                .user(user)
                .hospitalName(dto.getHospitalName())
                .licenseNumber(dto.getLicenseNumber())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .latitude(dto.getLatitude() != null ? dto.getLatitude() : 0.0)
                .longitude(dto.getLongitude() != null ? dto.getLongitude() : 0.0)
                .contactPerson(dto.getContactPerson())
                .emergencyPhone(dto.getEmergencyPhone())
                .isVerified(false)
                .build();

        hospitalRepository.save(hospital);
        return ApiResponse.success("Hospital registered, pending verification", hospital);
    }

    public ApiResponse<Hospital> getMyHospital() {
        User user = getCurrentUser();
        Hospital hospital = hospitalRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital profile not found"));
        return ApiResponse.success("Hospital fetched", hospital);
    }

    public ApiResponse<List<Hospital>> getAllVerifiedHospitals() {
        return ApiResponse.success("Verified hospitals",
                hospitalRepository.findByIsVerifiedTrue());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


    public ApiResponse<List<ReceiverRequest>> getPendingRequests() {
        // Fetch all pending requests for the hospital to see
        var requests = receiverRequestRepository.findByStatusOrderByCreatedAtDesc(RequestStatus.PENDING);
        return ApiResponse.success("Pending requests fetched", requests);
    }

    private Hospital getMyHospitalEntity() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();

        com.blooddonation.model.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.blooddonation.exception.ResourceNotFoundException("User not found"));

        return hospitalRepository.findByUserId(user.getId())
                .orElseThrow(() -> new com.blooddonation.exception.ResourceNotFoundException("Hospital profile not found"));
    }

    @Transactional
    public ApiResponse<Void> fulfillReceiverRequest(Long requestId) {
        ReceiverRequest request = receiverRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        Hospital hospital = getMyHospitalEntity(); // Helper to get current hospital

        // Check if hospital has enough blood units in inventory
        BloodInventory inventory = inventoryRepository
                .findByHospitalIdAndBloodGroup(hospital.getId(), request.getBloodGroup())
                .orElseThrow(() -> new IllegalArgumentException("No inventory found for this blood group"));

        if (inventory.getUnitsAvailable() < request.getUnitsRequired()) {
            throw new IllegalArgumentException("Insufficient inventory to fulfill this request");
        }

        // Deduct units and update status
        inventory.setUnitsAvailable(inventory.getUnitsAvailable() - request.getUnitsRequired());
        request.setStatus(RequestStatus.FULFILLED);

        inventoryRepository.save(inventory);
        receiverRequestRepository.save(request);

        return ApiResponse.success("Request fulfilled successfully", null);
    }

    // Inner DTO — or move to dto/request/HospitalRequest.java
    @Data
    public static class HospitalRequest {
        private String hospitalName;
        private String licenseNumber;
        private String address;
        private String city;
        private String state;
        private Double latitude;
        private Double longitude;
        private String contactPerson;
        private String emergencyPhone;
    }
}