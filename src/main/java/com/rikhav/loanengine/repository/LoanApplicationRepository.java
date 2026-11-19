package com.rikhav.loanengine.repository;

import com.rikhav.loanengine.domain.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
}