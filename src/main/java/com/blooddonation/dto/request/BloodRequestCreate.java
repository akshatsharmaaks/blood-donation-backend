package com.blooddonation.dto.request;

import com.blooddonation.enums.BloodGroup;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BloodRequestCreate {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Units required")
    @Min(value = 1, message = "At least 1 unit required")
    private Integer unitsRequired;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    private String hospital;

    @NotBlank(message = "City is required")
    private String city;

    private String state;
    private Double latitude;
    private Double longitude;
    private String notes;

    @NotBlank(message = "Urgency level is required")
    @Pattern(regexp = "LOW|MEDIUM|HIGH|CRITICAL",
            message = "Urgency must be LOW, MEDIUM, HIGH, or CRITICAL")
    private String urgencyLevel;
}