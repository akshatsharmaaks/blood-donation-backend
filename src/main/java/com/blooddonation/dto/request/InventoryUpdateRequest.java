package com.blooddonation.dto.request;

import com.blooddonation.enums.BloodGroup;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InventoryUpdateRequest {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Units is required")
    @Min(value = 0, message = "Units cannot be negative")
    private Integer unitsAvailable;

    private Integer minimumThreshold;
}