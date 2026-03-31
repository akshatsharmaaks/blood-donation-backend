package com.blooddonation.dto.response;

import com.blooddonation.enums.BloodGroup;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DonorResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private BloodGroup bloodGroup;
    private Integer age;
    private String city;
    private String state;
    private Boolean isAvailable;
    private LocalDate lastDonationDate;
    private Integer totalDonations;
    private Double distanceKm;       // populated during ranked search
    private Double rankingScore;     // populated during ranked search
}