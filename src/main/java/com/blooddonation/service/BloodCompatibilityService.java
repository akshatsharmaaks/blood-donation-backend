package com.blooddonation.service;

import com.blooddonation.algorithm.BloodCompatibilityMatrix;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.enums.BloodGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodCompatibilityService {

    private final BloodCompatibilityMatrix compatibilityMatrix;

    public ApiResponse<List<BloodGroup>> getCompatibleDonors(BloodGroup recipientGroup) {
        List<BloodGroup> compatible =
                compatibilityMatrix.getCompatibleDonorGroups(recipientGroup);
        return ApiResponse.success("Compatible donor groups", compatible);
    }

    public ApiResponse<Boolean> checkCompatibility(BloodGroup donorGroup,
                                                   BloodGroup recipientGroup) {
        boolean result = compatibilityMatrix.isCompatible(donorGroup, recipientGroup);
        String msg = result ? "Compatible" : "Not compatible";
        return ApiResponse.success(msg, result);
    }
}