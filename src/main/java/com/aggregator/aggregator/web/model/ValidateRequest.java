package com.aggregator.aggregator.web.model;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ValidateRequest(@NotBlank String accountNumber, List<String> providers) {
}
