package com.blooddonation.dto.response;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.DonorRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DonorRequestResponse {
    private Long id;
    private Long donorProfileId;
    private String donorName;
    private String donorPhone;
    private BloodGroup bloodGroup;
    private Integer unitsOffered;
    private DonorRequestStatus status;
    private LocalDate preferredDate;
    private String city;
    private String state;
    private String message;
    private Long receiverRequestId;     // null if open offer
    private LocalDateTime createdAt;
}