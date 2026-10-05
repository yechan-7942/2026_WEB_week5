package com.webservice.week05.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String count,
        @PositiveOrZero int price,
        @PositiveOrZero int day
) {
}
