package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.RiskBand;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RiskClassifier {

    private static final int MINIMUM_ELIGIBLE_SCORE = 600;
    private static final int MEDIUM_RISK_FLOOR = 650;
    private static final int LOW_RISK_FLOOR = 750;

    public Optional<RiskBand> classify(int creditScore) {
        if (creditScore >= LOW_RISK_FLOOR) {
            return Optional.of(RiskBand.LOW);
        }
        if (creditScore >= MEDIUM_RISK_FLOOR) {
            return Optional.of(RiskBand.MEDIUM);
        }
        if (creditScore >= MINIMUM_ELIGIBLE_SCORE) {
            return Optional.of(RiskBand.HIGH);
        }
        return Optional.empty();
    }
}