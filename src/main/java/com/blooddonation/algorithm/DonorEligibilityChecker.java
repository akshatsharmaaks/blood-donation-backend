package com.blooddonation.algorithm;

import com.blooddonation.model.DonorProfile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DonorEligibilityChecker {

    private static final int MIN_DAYS_BETWEEN_DONATIONS = 90;
    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 65;
    private static final double MIN_WEIGHT_KG = 50.0;

    public boolean isEligible(DonorProfile donor) {
        if (!donor.getIsAvailable()) return false;
        if (donor.getHasMedicalCondition()) return false;
        if (donor.getAge() < MIN_AGE || donor.getAge() > MAX_AGE) return false;
        if (donor.getWeightKg() < MIN_WEIGHT_KG) return false;
        if (donor.getLastDonationDate() != null) {
            long daysSinceLast = ChronoUnit.DAYS.between(
                    donor.getLastDonationDate(), LocalDate.now());
            if (daysSinceLast < MIN_DAYS_BETWEEN_DONATIONS) return false;
        }
        return true;
    }

    public long daysUntilEligible(DonorProfile donor) {
        if (donor.getLastDonationDate() == null) return 0;
        long daysSinceLast = ChronoUnit.DAYS.between(
                donor.getLastDonationDate(), LocalDate.now());
        long remaining = MIN_DAYS_BETWEEN_DONATIONS - daysSinceLast;
        return Math.max(0, remaining);
    }
}