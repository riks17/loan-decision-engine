package com.rikhav.loanengine.dto;

import com.rikhav.loanengine.domain.LoanPurpose;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanRequest(

        @NotNull(message = "Loan amount is required")
        @DecimalMin(value = "10000", message = "Loan amount must be between 10,000 and 50,00,000")
        @DecimalMax(value = "5000000", message = "Loan amount must be between 10,000 and 50,00,000")
        BigDecimal amount,

        @NotNull(message = "Tenure is required")
        @Min(value = 6, message = "Tenure must be between 6 and 360 months")
        @Max(value = 360, message = "Tenure must be between 6 and 360 months")
        Integer tenureMonths,

        @NotNull(message = "Loan purpose is required (PERSONAL, HOME or AUTO)")
        LoanPurpose purpose
) {
}