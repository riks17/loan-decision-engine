package com.rikhav.loanengine.controller;

import com.rikhav.loanengine.dto.LoanApplicationRequest;
import com.rikhav.loanengine.dto.LoanDecisionResponse;
import com.rikhav.loanengine.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping("/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDecisionResponse createApplication(@Valid @RequestBody LoanApplicationRequest request) {
        return loanApplicationService.evaluate(request);
    }
}