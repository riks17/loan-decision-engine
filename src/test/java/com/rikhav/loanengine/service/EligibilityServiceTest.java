package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.RejectionReason;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EligibilityServiceTest {

    private final EligibilityService service = new EligibilityService();

    private static final BigDecimal INCOME = new BigDecimal("100000");

    @Test
    void acceptsApplicantWhoMeetsAllRules() {
        assertTrue(service.checkApplicantRules(720, 30, 36).isEmpty());
    }

    @Test
    void rejectsCreditScoreBelow600() {
        assertEquals(List.of(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM),
                service.checkApplicantRules(599, 30, 36));
    }

    @Test
    void acceptsCreditScoreOfExactly600() {
        assertTrue(service.checkApplicantRules(600, 30, 36).isEmpty());
    }

    @Test
    void rejectsWhenAgePlusTenureExceedsSixtyFiveYears() {
        assertEquals(List.of(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED),
                service.checkApplicantRules(720, 60, 72));
    }

    @Test
    void acceptsAgePlusTenureOfExactlySixtyFiveYears() {
        assertTrue(service.checkApplicantRules(720, 60, 60).isEmpty());
    }

    @Test
    void rejectsAgePlusTenureOneMonthOverTheLimit() {
        assertEquals(List.of(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED),
                service.checkApplicantRules(720, 60, 61));
    }

    @Test
    void collectsEveryApplicantRuleThatFails() {
        assertEquals(
                List.of(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM, RejectionReason.AGE_TENURE_LIMIT_EXCEEDED),
                service.checkApplicantRules(580, 60, 72));
    }

    @Test
    void acceptsEmiOfExactlyFiftyPercentOfIncome() {
        assertEquals(Optional.empty(),
                service.checkAffordability(new BigDecimal("50000.00"), INCOME));
    }

    @Test
    void rejectsEmiJustAboveFiftyPercentOfIncome() {
        assertEquals(Optional.of(RejectionReason.EMI_EXCEEDS_50_PERCENT),
                service.checkAffordability(new BigDecimal("50000.01"), INCOME));
    }

    @Test
    void reportsFiftyPercentReasonForEmiOfExactlySixtyPercent() {
        assertEquals(Optional.of(RejectionReason.EMI_EXCEEDS_50_PERCENT),
                service.checkAffordability(new BigDecimal("60000.00"), INCOME));
    }

    @Test
    void reportsSixtyPercentReasonForEmiAboveSixtyPercent() {
        assertEquals(Optional.of(RejectionReason.EMI_EXCEEDS_60_PERCENT),
                service.checkAffordability(new BigDecimal("60000.01"), INCOME));
    }

    @Test
    void acceptsSampleRequestEmiAgainstSampleIncome() {
        assertEquals(Optional.empty(),
                service.checkAffordability(new BigDecimal("16967.64"), new BigDecimal("75000")));
    }
}