package com.blooddonation.algorithm;

import com.blooddonation.dto.response.DonorResponse;
import com.blooddonation.enums.BloodGroup;
import com.blooddonation.model.DonorProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DonorRankingAlgorithm {

    private final BloodCompatibilityMatrix compatibilityMatrix;
    private final DonorEligibilityChecker eligibilityChecker;

    // Scoring weights (must sum to 1.0)
    private static final double WEIGHT_COMPATIBILITY = 0.40;
    private static final double WEIGHT_DISTANCE      = 0.30;
    private static final double WEIGHT_RECENCY       = 0.20;
    private static final double WEIGHT_EXPERIENCE    = 0.10;

    public List<DonorResponse> rankDonors(List<DonorProfile> donors,
                                          BloodGroup requiredGroup,
                                          double requestLat,
                                          double requestLon) {
        return donors.stream()
                .filter(eligibilityChecker::isEligible)
                .map(donor -> {
                    double score = calculateScore(donor, requiredGroup, requestLat, requestLon);
                    double distance = haversineDistance(
                            requestLat, requestLon,
                            donor.getLatitude(), donor.getLongitude());
                    return buildResponse(donor, score, distance);
                })
                .sorted(Comparator.comparingDouble(DonorResponse::getRankingScore).reversed())
                .collect(Collectors.toList());
    }

    private double calculateScore(DonorProfile donor, BloodGroup requiredGroup,
                                  double reqLat, double reqLon) {
        double compatScore = donor.getBloodGroup().equals(requiredGroup) ? 1.0 : 0.7;
        double distKm = haversineDistance(reqLat, reqLon,
                donor.getLatitude(), donor.getLongitude());
        double distScore = 1.0 / (1.0 + distKm / 10.0);
        double recencyScore = calculateRecencyScore(donor.getLastDonationDate());
        double expScore = Math.min(donor.getTotalDonations() / 10.0, 1.0);

        return (compatScore * WEIGHT_COMPATIBILITY)
                + (distScore   * WEIGHT_DISTANCE)
                + (recencyScore * WEIGHT_RECENCY)
                + (expScore    * WEIGHT_EXPERIENCE);
    }

    private double calculateRecencyScore(LocalDate lastDonation) {
        if (lastDonation == null) return 1.0; // Never donated → highest recency priority
        long days = ChronoUnit.DAYS.between(lastDonation, LocalDate.now());
        return Math.min(days / 365.0, 1.0);
    }

    // Haversine formula — straight-line distance in km between two lat/lon points
    public double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private DonorResponse buildResponse(DonorProfile donor, double score, double distance) {
        return DonorResponse.builder()
                .id(donor.getId())
                .userId(donor.getUser().getId())
                .fullName(donor.getUser().getFullName())
                .email(donor.getUser().getEmail())
                .phone(donor.getUser().getPhone())
                .bloodGroup(donor.getBloodGroup())
                .age(donor.getAge())
                .city(donor.getCity())
                .state(donor.getState())
                .isAvailable(donor.getIsAvailable())
                .lastDonationDate(donor.getLastDonationDate())
                .totalDonations(donor.getTotalDonations())
                .distanceKm(Math.round(distance * 10.0) / 10.0)
                .rankingScore(Math.round(score * 1000.0) / 1000.0)
                .build();
    }
}