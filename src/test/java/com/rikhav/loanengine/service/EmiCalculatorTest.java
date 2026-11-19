package com.rikhav.loanengine.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmiCalculatorTest {

    private final EmiCalculator calculator = new EmiCalculator();

    @Test
    void calculatesEmiForKnownLoan() {
        // 1,00,000 at 12% for 12 months
        BigDecimal emi = calculator.calculateEmi(new BigDecimal("100000"), new BigDecimal("12"), 12);

        assertEquals(new BigDecimal("8884.88"), emi);
    }

    @Test
    void calculatesEmiForThirtyYearTenure() {
        // 1,00,000 at 12% for 360 months
        BigDecimal emi = calculator.calculateEmi(new BigDecimal("100000"), new BigDecimal("12"), 360);

        assertEquals(new BigDecimal("1028.61"), emi);
    }

    @Test
    void calculatesEmiForBriefSampleRequest() {
        // 5,00,000 at 13.5% for 36 months: the PDF's sample request.
        // The PDF shows 16234.23 but the formula gives about 16967.6, so check a range.
        BigDecimal emi = calculator.calculateEmi(new BigDecimal("500000"), new BigDecimal("13.5"), 36);

        assertTrue(emi.compareTo(new BigDecimal("16967.00")) > 0);
        assertTrue(emi.compareTo(new BigDecimal("16968.00")) < 0);
    }

    @Test
    void roundsResultToTwoDecimalPlaces() {
        BigDecimal emi = calculator.calculateEmi(new BigDecimal("500000"), new BigDecimal("13.5"), 36);

        assertEquals(2, emi.scale());
    }

    @Test
    void splitsPrincipalEvenlyWhenRateIsZero() {
        BigDecimal emi = calculator.calculateEmi(new BigDecimal("120000"), BigDecimal.ZERO, 12);

        assertEquals(new BigDecimal("10000.00"), emi);
    }

    @Test
    void calculatesTotalPayableAsEmiTimesTenure() {
        BigDecimal total = calculator.calculateTotalPayable(new BigDecimal("8884.88"), 12);

        assertEquals(new BigDecimal("106618.56"), total);
    }

    @Test
    void rejectsZeroPrincipal() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateEmi(BigDecimal.ZERO, new BigDecimal("12"), 12));
    }

    @Test
    void rejectsNegativeRate() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateEmi(new BigDecimal("100000"), new BigDecimal("-1"), 12));
    }

    @Test
    void rejectsZeroTenure() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateEmi(new BigDecimal("100000"), new BigDecimal("12"), 0));
    }
}