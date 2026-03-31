package com.blooddonation.enums;

public enum DonorRequestStatus {
    PENDING,       // Donor offered, waiting for receiver/admin to confirm
    ACCEPTED,      // Receiver accepted the donor's offer
    REJECTED,      // Receiver rejected the offer
    COMPLETED,     // Donation actually happened
    WITHDRAWN      // Donor withdrew the offer
}