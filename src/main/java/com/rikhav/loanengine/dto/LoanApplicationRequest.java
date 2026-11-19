package com.rikhav.loanengine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LoanApplicationRequest(

        @NotNull(message = "Applicant details are required")
        @Valid
        ApplicantRequest applicant,

        @NotNull(message = "Loan details are required")
        @Valid
        LoanRequest loan
) {
}