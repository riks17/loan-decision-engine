package com.rikhav.loanengine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rikhav.loanengine.domain.ApplicationStatus;
import com.rikhav.loanengine.domain.RejectionReason;
import com.rikhav.loanengine.domain.RiskBand;

import java.util.List;
import java.util.UUID;

public record LoanDecisionResponse(
        UUID applicationId,
        ApplicationStatus status,
        RiskBand riskBand,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        OfferResponse offer,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<RejectionReason> rejectionReasons
) {
}