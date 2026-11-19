package com.rikhav.loanengine.controller;

import com.rikhav.loanengine.domain.ApplicationStatus;
import com.rikhav.loanengine.dto.LoanApplicationRequest;
import com.rikhav.loanengine.dto.LoanDecisionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class LoanApplicationController {

    @PostMapping("/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDecisionResponse createApplication(@Valid @RequestBody LoanApplicationRequest request) {
        // Temporary stub for test
        return new LoanDecisionResponse(
                UUID.randomUUID(),
                ApplicationStatus.REJECTED,
                null,
                null,
                List.of());
    }
}