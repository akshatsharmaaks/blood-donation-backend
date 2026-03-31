package com.blooddonation.algorithm;

import com.blooddonation.enums.BloodGroup;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BloodCompatibilityMatrix {

    // Map: recipient blood group → list of compatible donor blood groups
    private static final Map<BloodGroup, List<BloodGroup>> COMPATIBILITY_MAP =
            new EnumMap<>(BloodGroup.class);

    static {
        COMPATIBILITY_MAP.put(BloodGroup.A_POSITIVE,
                List.of(BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE,
                        BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE));

        COMPATIBILITY_MAP.put(BloodGroup.A_NEGATIVE,
                List.of(BloodGroup.A_NEGATIVE, BloodGroup.O_NEGATIVE));

        COMPATIBILITY_MAP.put(BloodGroup.B_POSITIVE,
                List.of(BloodGroup.B_POSITIVE, BloodGroup.B_NEGATIVE,
                        BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE));

        COMPATIBILITY_MAP.put(BloodGroup.B_NEGATIVE,
                List.of(BloodGroup.B_NEGATIVE, BloodGroup.O_NEGATIVE));

        COMPATIBILITY_MAP.put(BloodGroup.AB_POSITIVE,
                List.of(BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE,
                        BloodGroup.B_POSITIVE, BloodGroup.B_NEGATIVE,
                        BloodGroup.AB_POSITIVE, BloodGroup.AB_NEGATIVE,
                        BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE));

        COMPATIBILITY_MAP.put(BloodGroup.AB_NEGATIVE,
                List.of(BloodGroup.A_NEGATIVE, BloodGroup.B_NEGATIVE,
                        BloodGroup.AB_NEGATIVE, BloodGroup.O_NEGATIVE));

        COMPATIBILITY_MAP.put(BloodGroup.O_POSITIVE,
                List.of(BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE));

        COMPATIBILITY_MAP.put(BloodGroup.O_NEGATIVE,
                List.of(BloodGroup.O_NEGATIVE));
    }

    public List<BloodGroup> getCompatibleDonorGroups(BloodGroup recipientGroup) {
        return COMPATIBILITY_MAP.getOrDefault(recipientGroup, List.of());
    }

    public boolean isCompatible(BloodGroup donorGroup, BloodGroup recipientGroup) {
        return getCompatibleDonorGroups(recipientGroup).contains(donorGroup);
    }
}