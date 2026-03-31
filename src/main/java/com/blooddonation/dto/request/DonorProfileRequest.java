package com.blooddonation.dto.request;

import com.blooddonation.enums.BloodGroup;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DonorProfileRequest {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Donor must be at least 18 years old")
    @Max(value = 65, message = "Donor must be at most 65 years old")
    private Integer age;

    @NotNull(message = "Weight is required")
    @Min(value = 50, message = "Minimum weight is 50 kg")
    private Double weightKg;

    private String city;
    private String state;
    private String country;

    private Double latitude;
    private Double longitude;

    private LocalDate lastDonationDate;
    private Boolean isAvailable;
    private Boolean hasMedicalCondition;
    private String medicalNotes;
}