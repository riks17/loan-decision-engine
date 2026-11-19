package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.EmploymentType;
import com.rikhav.loanengine.domain.RiskBand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestRateCalculatorTest {

    private final InterestRateCalculator calculator = new InterestRateCalculator();

    private static final BigDecimal SMALL_LOAN = new BigDecimal("500000");

    @Test
    void chargesBaseRateForLowRiskSalariedSmallLoan() {
        assertEquals(new BigDecimal("12.0"),
                calculator.calculate(RiskBand.LOW, EmploymentType.SALARIED, SMALL_LOAN));
    }

    @Test
    void addsOnePointFivePercentForMediumRisk() {
        assertEquals(new BigDecimal("13.5"),
                calculator.calculate(RiskBand.MEDIUM, EmploymentType.SALARIED, SMALL_LOAN));
    }

    @Test
    void addsThreePercentForHighRisk() {
        assertEquals(new BigDecimal("15.0"),
                calculator.calculate(RiskBand.HIGH, EmploymentType.SALARIED, SMALL_LOAN));
    }

    @Test
    void addsOnePercentForSelfEmployed() {
        assertEquals(new BigDecimal("13.0"),
                calculator.calculate(RiskBand.LOW, EmploymentType.SELF_EMPLOYED, SMALL_LOAN));
    }

    @Test
    void addsHalfPercentForLoanAboveTenLakh() {
        assertEquals(new BigDecimal("12.5"),
                calculator.calculate(RiskBand.LOW, EmploymentType.SALARIED, new BigDecimal("1000001")));
    }

    @Test
    void addsNoLoanSizePremiumAtExactlyTenLakh() {
        assertEquals(new BigDecimal("12.0"),
                calculator.calculate(RiskBand.LOW, EmploymentType.SALARIED, new BigDecimal("1000000")));
    }

    @Test
    void combinesAllPremiums() {
        // 12.0 base + 3.0 high risk + 1.0 self-employed + 0.5 large loan
        assertEquals(new BigDecimal("16.5"),
                calculator.calculate(RiskBand.HIGH, EmploymentType.SELF_EMPLOYED, new BigDecimal("2000000")));
    }
}