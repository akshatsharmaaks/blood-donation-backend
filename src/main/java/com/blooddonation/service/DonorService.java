package com.blooddonation.service;

import com.blooddonation.algorithm.*;
import com.blooddonation.dto.request.DonorProfileRequest;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.DonorResponse;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.model.DonorProfile;
import com.blooddonation.model.User;
import com.blooddonation.repository.*;
import com.blooddonation.enums.BloodGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonorService {

    private final DonorProfileRepository donorProfileRepository;
    private final UserRepository userRepository;
    private final BloodCompatibilityMatrix compatibilityMatrix;
    private final DonorRankingAlgorithm rankingAlgorithm;
    private final DonorEligibilityChecker eligibilityChecker;

    @Transactional
    @CacheEvict(value = "donors", allEntries = true)
    public ApiResponse<DonorResponse> createProfile(DonorProfileRequest request) {
        User user = getCurrentUser();

        if (donorProfileRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("Donor profile already exists");
        }

        DonorProfile profile = DonorProfile.builder()
                .user(user)
                .bloodGroup(request.getBloodGroup())
                .age(request.getAge())
                .weightKg(request.getWeightKg())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .latitude(request.getLatitude() != null ? request.getLatitude() : 0.0)
                .longitude(request.getLongitude() != null ? request.getLongitude() : 0.0)
                .lastDonationDate(request.getLastDonationDate())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .hasMedicalCondition(request.getHasMedicalCondition() != null
                        ? request.getHasMedicalCondition() : false)
                .medicalNotes(request.getMedicalNotes())
                .totalDonations(0)
                .build();

        donorProfileRepository.save(profile);
        return ApiResponse.success("Donor profile created", buildResponse(profile));
    }

    @Transactional
    @CacheEvict(value = "donors", allEntries = true)
    public ApiResponse<DonorResponse> updateProfile(DonorProfileRequest request) {
        User user = getCurrentUser();
        DonorProfile profile = donorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found"));

        profile.setBloodGroup(request.getBloodGroup());
        profile.setAge(request.getAge());
        profile.setWeightKg(request.getWeightKg());
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setCountry(request.getCountry());
        if (request.getLatitude() != null) profile.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) profile.setLongitude(request.getLongitude());
        if (request.getLastDonationDate() != null)
            profile.setLastDonationDate(request.getLastDonationDate());
        if (request.getIsAvailable() != null) profile.setIsAvailable(request.getIsAvailable());
        if (request.getHasMedicalCondition() != null)
            profile.setHasMedicalCondition(request.getHasMedicalCondition());
        profile.setMedicalNotes(request.getMedicalNotes());

        donorProfileRepository.save(profile);
        return ApiResponse.success("Profile updated", buildResponse(profile));
    }

    public ApiResponse<DonorResponse> getMyProfile() {
        User user = getCurrentUser();
        DonorProfile profile = donorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found"));
        return ApiResponse.success("Profile fetched", buildResponse(profile));
    }

    @Cacheable(value = "donors", key = "#bloodGroup + '_' + #city")
    public ApiResponse<List<DonorResponse>> searchDonors(BloodGroup bloodGroup,
                                                         String city,
                                                         Double lat,
                                                         Double lon) {
        List<BloodGroup> compatibleGroups =
                compatibilityMatrix.getCompatibleDonorGroups(bloodGroup);

        List<DonorProfile> donors = (city != null && !city.isBlank())
                ? donorProfileRepository.findEligibleDonors(compatibleGroups, city)
                : donorProfileRepository.findEligibleDonorsByBloodGroups(compatibleGroups);

        double searchLat = lat != null ? lat : 0.0;
        double searchLon = lon != null ? lon : 0.0;

        List<DonorResponse> ranked = rankingAlgorithm.rankDonors(
                donors, bloodGroup, searchLat, searchLon);

        return ApiResponse.success("Donors found: " + ranked.size(), ranked);
    }

    public ApiResponse<Long> getDaysUntilEligible() {
        User user = getCurrentUser();
        DonorProfile profile = donorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found"));
        long days = eligibilityChecker.daysUntilEligible(profile);
        return ApiResponse.success("Days until eligible", days);
    }

    private DonorResponse buildResponse(DonorProfile profile) {
        return DonorResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .email(profile.getUser().getEmail())
                .phone(profile.getUser().getPhone())
                .bloodGroup(profile.getBloodGroup())
                .age(profile.getAge())
                .city(profile.getCity())
                .state(profile.getState())
                .isAvailable(profile.getIsAvailable())
                .lastDonationDate(profile.getLastDonationDate())
                .totalDonations(profile.getTotalDonations())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}