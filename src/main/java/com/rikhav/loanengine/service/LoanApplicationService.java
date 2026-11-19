package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.ApplicationStatus;
import com.rikhav.loanengine.domain.LoanApplication;
import com.rikhav.loanengine.domain.RejectionReason;
import com.rikhav.loanengine.domain.RiskBand;
import com.rikhav.loanengine.dto.ApplicantRequest;
import com.rikhav.loanengine.dto.LoanApplicationRequest;
import com.rikhav.loanengine.dto.LoanDecisionResponse;
import com.rikhav.loanengine.dto.LoanRequest;
import com.rikhav.loanengine.dto.OfferResponse;
import com.rikhav.loanengine.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoanApplicationService {

    private final RiskClassifier riskClassifier;
    private final InterestRateCalculator interestRateCalculator;
    private final EmiCalculator emiCalculator;
    private final EligibilityService eligibilityService;
    private final LoanApplicationRepository repository;

    public LoanApplicationService(RiskClassifier riskClassifier,
                                  InterestRateCalculator interestRateCalculator,
                                  EmiCalculator emiCalculator,
                                  EligibilityService eligibilityService,
                                  LoanApplicationRepository repository) {
        this.riskClassifier = riskClassifier;
        this.interestRateCalculator = interestRateCalculator;
        this.emiCalculator = emiCalculator;
        this.eligibilityService = eligibilityService;
        this.repository = repository;
    }

    @Transactional
    public LoanDecisionResponse evaluate(LoanApplicationRequest request) {
        ApplicantRequest applicant = request.applicant();
        LoanRequest loan = request.loan();

        LoanApplication application = new LoanApplication(
                applicant.name(), applicant.age(), applicant.monthlyIncome(),
                applicant.employmentType(), applicant.creditScore(),
                loan.amount(), loan.tenureMonths(), loan.purpose());

        Optional<RiskBand> riskBand = riskClassifier.classify(applicant.creditScore());
        List<RejectionReason> reasons = new ArrayList<>(
                eligibilityService.checkApplicantRules(
                        applicant.creditScore(), applicant.age(), loan.tenureMonths()));

        BigDecimal interestRate = null;
        BigDecimal emi = null;

        if (riskBand.isPresent()) {
            interestRate = interestRateCalculator.calculate(
                    riskBand.get(), applicant.employmentType(), loan.amount());
            emi = emiCalculator.calculateEmi(loan.amount(), interestRate, loan.tenureMonths());
            eligibilityService.checkAffordability(emi, applicant.monthlyIncome())
                    .ifPresent(reasons::add);
        }

        if (!reasons.isEmpty()) {
            application.reject(reasons);
            LoanApplication saved = repository.save(application);
            return new LoanDecisionResponse(
                    saved.getId(), ApplicationStatus.REJECTED, null, null, reasons);
        }

        RiskBand band = riskBand.orElseThrow();
        BigDecimal totalPayable = emiCalculator.calculateTotalPayable(emi, loan.tenureMonths());
        application.approve(band, interestRate, emi, totalPayable);
        LoanApplication saved = repository.save(application);

        OfferResponse offer = new OfferResponse(interestRate, loan.tenureMonths(), emi, totalPayable);
        return new LoanDecisionResponse(
                saved.getId(), ApplicationStatus.APPROVED, band, offer, null);
    }
}