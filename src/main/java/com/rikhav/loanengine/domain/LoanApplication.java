package com.rikhav.loanengine.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String applicantName;
    private int age;

    @Column(precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private int creditScore;

    @Column(precision = 15, scale = 2)
    private BigDecimal loanAmount;

    private int tenureMonths;

    @Enumerated(EnumType.STRING)
    private LoanPurpose purpose;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    private RiskBand riskBand;

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(precision = 15, scale = 2)
    private BigDecimal emi;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalPayable;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "application_rejection_reasons",
            joinColumns = @JoinColumn(name = "application_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    private List<RejectionReason> rejectionReasons = new ArrayList<>();

    private Instant createdAt;

    protected LoanApplication() {
        // required by JPA
    }

    public LoanApplication(String applicantName, int age, BigDecimal monthlyIncome,
                           EmploymentType employmentType, int creditScore,
                           BigDecimal loanAmount, int tenureMonths, LoanPurpose purpose) {
        this.applicantName = applicantName;
        this.age = age;
        this.monthlyIncome = monthlyIncome;
        this.employmentType = employmentType;
        this.creditScore = creditScore;
        this.loanAmount = loanAmount;
        this.tenureMonths = tenureMonths;
        this.purpose = purpose;
        this.createdAt = Instant.now();
    }

    public void approve(RiskBand riskBand, BigDecimal interestRate, BigDecimal emi, BigDecimal totalPayable) {
        this.status = ApplicationStatus.APPROVED;
        this.riskBand = riskBand;
        this.interestRate = interestRate;
        this.emi = emi;
        this.totalPayable = totalPayable;
    }

    public void reject(List<RejectionReason> reasons) {
        this.status = ApplicationStatus.REJECTED;
        this.rejectionReasons = new ArrayList<>(reasons);
    }

    public UUID getId() {
        return id;
    }
}