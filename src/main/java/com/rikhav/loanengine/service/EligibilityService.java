package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.RejectionReason;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EligibilityService {


    private static final int MONTHS_PER_YEAR = 12;
    private static final int MAXIMUM_AGE_PLUS_TENURE_MONTHS = 65 * MONTHS_PER_YEAR;
    private static final BigDecimal EMI_REJECTION_RATIO = new BigDecimal("0.60");
    private static final BigDecimal EMI_OFFER_RATIO = new BigDecimal("0.50");

    public List<RejectionReason> checkApplicantRules(int creditScore, int age, int tenureMonths) {
        List<RejectionReason> reasons = new ArrayList<>();

        if (creditScore < LendingPolicy.MINIMUM_CREDIT_SCORE) {
            reasons.add(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM);
        }
        if (age * MONTHS_PER_YEAR + tenureMonths > MAXIMUM_AGE_PLUS_TENURE_MONTHS) {
            reasons.add(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED);
        }
        return reasons;
    }

    public Optional<RejectionReason> checkAffordability(BigDecimal emi, BigDecimal monthlyIncome) {
        if (emi.compareTo(monthlyIncome.multiply(EMI_REJECTION_RATIO)) > 0) {
            return Optional.of(RejectionReason.EMI_EXCEEDS_60_PERCENT);
        }
        if (emi.compareTo(monthlyIncome.multiply(EMI_OFFER_RATIO)) > 0) {
            return Optional.of(RejectionReason.EMI_EXCEEDS_50_PERCENT);
        }
        return Optional.empty();
    }
}