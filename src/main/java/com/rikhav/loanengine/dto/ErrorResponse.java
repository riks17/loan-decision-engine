package com.rikhav.loanengine.dto;

import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        List<String> messages
) {
}