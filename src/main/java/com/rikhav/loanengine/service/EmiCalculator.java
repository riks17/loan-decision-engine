package com.rikhav.loanengine.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class EmiCalculator {

    private static final MathContext PRECISION = MathContext.DECIMAL128;
    private static final BigDecimal MONTHS_TIMES_PERCENT = BigDecimal.valueOf(1200);
    private static final int MONEY_SCALE = 2;

    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRatePercent, int tenureMonths) {
        if (principal == null || principal.signum() <= 0) {
            throw new IllegalArgumentException("Principal must be greater than zero");
        }
        if (annualRatePercent == null || annualRatePercent.signum() < 0) {
            throw new IllegalArgumentException("Interest rate must not be negative");
        }
        if (tenureMonths <= 0) {
            throw new IllegalArgumentException("Tenure must be greater than zero");
        }

        if (annualRatePercent.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), MONEY_SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal monthlyRate = annualRatePercent.divide(MONTHS_TIMES_PERCENT, PRECISION);
        BigDecimal growthFactor = BigDecimal.ONE.add(monthlyRate).pow(tenureMonths, PRECISION);

        BigDecimal numerator = principal.multiply(monthlyRate).multiply(growthFactor);
        BigDecimal denominator = growthFactor.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalPayable(BigDecimal emi, int tenureMonths) {
        return emi.multiply(BigDecimal.valueOf(tenureMonths))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}