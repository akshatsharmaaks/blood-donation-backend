package com.blooddonation.service;

import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationHistoryService {

    private final DonationHistoryRepository historyRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponse<DonationHistory> recordDonation(DonationRecordRequest dto) {
        User user = getCurrentUser();
        DonorProfile donor = donorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found"));

        DonationHistory history = DonationHistory.builder()
                .donor(donor)
                .bloodGroup(donor.getBloodGroup())
                .unitsDonated(dto.getUnitsDonated())
                .donationDate(dto.getDonationDate())
                .notes(dto.getNotes())
                .build();

        historyRepository.save(history);

        // Update donor's last donation date and total count
        donor.setLastDonationDate(dto.getDonationDate());
        donor.setTotalDonations(donor.getTotalDonations() + 1);
        donorProfileRepository.save(donor);

        return ApiResponse.success("Donation recorded", history);
    }

    public ApiResponse<List<DonationHistory>> getMyHistory() {
        User user = getCurrentUser();
        DonorProfile donor = donorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found"));
        List<DonationHistory> history =
                historyRepository.findByDonorIdOrderByDonationDateDesc(donor.getId());
        return ApiResponse.success("History fetched", history);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Data
    public static class DonationRecordRequest {
        private Integer unitsDonated;
        private LocalDate donationDate;
        private String notes;
    }
}