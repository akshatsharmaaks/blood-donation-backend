package com.blooddonation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DonorRequestCreate {

    // Optional — if null it's a general open donation offer
    private Long receiverRequestId;

    @NotNull(message = "Units offered is required")
    @Min(value = 1, message = "Must offer at least 1 unit")
    private Integer unitsOffered;

    @NotNull(message = "Preferred date is required")
    @Future(message = "Preferred date must be in the future")
    private LocalDate preferredDate;

    private String city;
    private String state;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
}