package com.rikhav.loanengine.dto;

import java.math.BigDecimal;

public record OfferResponse(
        BigDecimal interestRate,
        Integer tenureMonths,
        BigDecimal emi,
        BigDecimal totalPayable
) {
}