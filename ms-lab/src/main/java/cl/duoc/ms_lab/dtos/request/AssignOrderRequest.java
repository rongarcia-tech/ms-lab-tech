package cl.duoc.ms_lab.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record AssignOrderRequest(
        @NotBlank String labCode
) {}

