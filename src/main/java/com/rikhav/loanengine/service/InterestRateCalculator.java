package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.EmploymentType;
import com.rikhav.loanengine.domain.RiskBand;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InterestRateCalculator {

    private static final BigDecimal BASE_RATE = new BigDecimal("12.0");
    private static final BigDecimal SELF_EMPLOYED_PREMIUM = new BigDecimal("1.0");
    private static final BigDecimal LARGE_LOAN_PREMIUM = new BigDecimal("0.5");
    private static final BigDecimal LARGE_LOAN_THRESHOLD = new BigDecimal("1000000");
    private static final BigDecimal NO_PREMIUM = new BigDecimal("0.0");

    public BigDecimal calculate(RiskBand riskBand, EmploymentType employmentType, BigDecimal loanAmount) {
        return BASE_RATE
                .add(riskPremium(riskBand))
                .add(employmentPremium(employmentType))
                .add(loanSizePremium(loanAmount));
    }

    private BigDecimal riskPremium(RiskBand riskBand) {
        return switch (riskBand) {
            case LOW -> NO_PREMIUM;
            case MEDIUM -> new BigDecimal("1.5");
            case HIGH -> new BigDecimal("3.0");
        };
    }

    private BigDecimal employmentPremium(EmploymentType employmentType) {
        return switch (employmentType) {
            case SALARIED -> NO_PREMIUM;
            case SELF_EMPLOYED -> SELF_EMPLOYED_PREMIUM;
        };
    }

    private BigDecimal loanSizePremium(BigDecimal loanAmount) {
        return loanAmount.compareTo(LARGE_LOAN_THRESHOLD) > 0 ? LARGE_LOAN_PREMIUM : NO_PREMIUM;
    }
}