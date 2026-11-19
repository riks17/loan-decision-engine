package com.rikhav.loanengine.service;

import com.rikhav.loanengine.domain.RiskBand;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskClassifierTest {

    private final RiskClassifier classifier = new RiskClassifier();

    @Test
    void classifiesScoreOf750AsLow() {
        assertEquals(Optional.of(RiskBand.LOW), classifier.classify(750));
    }

    @Test
    void classifiesTopScoreAsLow() {
        assertEquals(Optional.of(RiskBand.LOW), classifier.classify(900));
    }

    @Test
    void classifiesScoreOf749AsMedium() {
        assertEquals(Optional.of(RiskBand.MEDIUM), classifier.classify(749));
    }

    @Test
    void classifiesScoreOf650AsMedium() {
        assertEquals(Optional.of(RiskBand.MEDIUM), classifier.classify(650));
    }

    @Test
    void classifiesScoreOf649AsHigh() {
        assertEquals(Optional.of(RiskBand.HIGH), classifier.classify(649));
    }

    @Test
    void classifiesScoreOf600AsHigh() {
        assertEquals(Optional.of(RiskBand.HIGH), classifier.classify(600));
    }

    @Test
    void returnsNoBandBelow600() {
        assertEquals(Optional.empty(), classifier.classify(599));
    }

    @Test
    void returnsNoBandForLowestPossibleScore() {
        assertEquals(Optional.empty(), classifier.classify(300));
    }
}