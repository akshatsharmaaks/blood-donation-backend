package com.blooddonation.service;

import com.blooddonation.algorithm.*;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.DonorResponse;
import com.blooddonation.enums.BloodGroup;
import com.blooddonation.model.DonorProfile;
import com.blooddonation.repository.DonorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonorRecommendationService {

    private final DonorProfileRepository donorProfileRepository;
    private final BloodCompatibilityMatrix compatibilityMatrix;
    private final DonorRankingAlgorithm rankingAlgorithm;

    public ApiResponse<List<DonorResponse>> recommend(BloodGroup requiredGroup,
                                                      String city,
                                                      double lat,
                                                      double lon,
                                                      int limit) {
        List<BloodGroup> compatible =
                compatibilityMatrix.getCompatibleDonorGroups(requiredGroup);

        List<DonorProfile> donors = (city != null && !city.isBlank())
                ? donorProfileRepository.findEligibleDonors(compatible, city)
                : donorProfileRepository.findEligibleDonorsByBloodGroups(compatible);

        List<DonorResponse> ranked = rankingAlgorithm
                .rankDonors(donors, requiredGroup, lat, lon)
                .stream()
                .limit(limit)
                .toList();

        return ApiResponse.success("Top " + ranked.size() + " recommended donors", ranked);
    }
}