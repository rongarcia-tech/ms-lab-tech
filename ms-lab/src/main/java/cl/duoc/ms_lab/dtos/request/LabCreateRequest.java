package cl.duoc.ms_lab.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record LabCreateRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Z0-9_]{3,50}$")
        String code,

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 255)
        String address,

        @Size(max = 30)
        String phone,

        List<String> supportedTests
) {}

