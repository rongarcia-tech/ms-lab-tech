package cl.duoc.ms_lab.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record OrderCreateRequest(
        @NotBlank String patientId,
        @NotBlank String requestedTest,
        String labCode // opcional; si viene, nace ASSIGNED
) {}
