package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.ApplicationStatus;
import com.rikhav.loanengine.domain.EmploymentType;
import com.rikhav.loanengine.domain.LoanApplication;
import com.rikhav.loanengine.domain.LoanPurpose;
import com.rikhav.loanengine.domain.RejectionReason;
import com.rikhav.loanengine.domain.RiskBand;
import com.rikhav.loanengine.dto.ApplicantRequest;
import com.rikhav.loanengine.dto.LoanApplicationRequest;
import com.rikhav.loanengine.dto.LoanDecisionResponse;
import com.rikhav.loanengine.dto.LoanRequest;
import com.rikhav.loanengine.repository.LoanApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoanApplicationServiceTest {

    private LoanApplicationRepository repository;
    private LoanApplicationService service;

    @BeforeEach
    void setUp() {
        repository = mock(LoanApplicationRepository.class);
        when(repository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service = new LoanApplicationService(
                new RiskClassifier(),
                new InterestRateCalculator(),
                new EmiCalculator(),
                new EligibilityService(),
                repository);
    }

    private LoanApplicationRequest request(int age, String income, EmploymentType employment,
                                           int score, String amount, int tenure) {
        return new LoanApplicationRequest(
                new ApplicantRequest("Rahul", age, new BigDecimal(income), employment, score),
                new LoanRequest(new BigDecimal(amount), tenure, LoanPurpose.PERSONAL));
    }

    @Test
    void approvesBriefSampleApplicationWithSingleOffer() {
        LoanDecisionResponse response = service.evaluate(
                request(30, "75000", EmploymentType.SALARIED, 720, "500000", 36));

        assertEquals(ApplicationStatus.APPROVED, response.status());
        assertEquals(RiskBand.MEDIUM, response.riskBand());
        assertEquals(new BigDecimal("13.5"), response.offer().interestRate());
        assertEquals(36, response.offer().tenureMonths());
        assertNull(response.rejectionReasons());
    }

    @Test
    void rejectsLowCreditScoreWithNullRiskBandAndNoOffer() {
        LoanDecisionResponse response = service.evaluate(
                request(30, "75000", EmploymentType.SALARIED, 580, "500000", 36));

        assertEquals(ApplicationStatus.REJECTED, response.status());
        assertNull(response.riskBand());
        assertNull(response.offer());
        assertEquals(List.of(RejectionReason.CREDIT_SCORE_BELOW_MINIMUM), response.rejectionReasons());
    }

    @Test
    void collectsAgeAndEmiReasonsTogether() {
        // Age 60 with 72 months breaks the age limit, and 50,00,000 on 75,000 income breaks the EMI limit.
        LoanDecisionResponse response = service.evaluate(
                request(60, "75000", EmploymentType.SALARIED, 720, "5000000", 72));

        assertEquals(ApplicationStatus.REJECTED, response.status());
        assertEquals(
                List.of(RejectionReason.AGE_TENURE_LIMIT_EXCEEDED, RejectionReason.EMI_EXCEEDS_60_PERCENT),
                response.rejectionReasons());
    }

    @Test
    void storesApprovedDecisions() {
        service.evaluate(request(30, "75000", EmploymentType.SALARIED, 720, "500000", 36));

        verify(repository).save(any(LoanApplication.class));
    }

    @Test
    void storesRejectedDecisions() {
        service.evaluate(request(30, "75000", EmploymentType.SALARIED, 580, "500000", 36));

        verify(repository).save(any(LoanApplication.class));
    }
}