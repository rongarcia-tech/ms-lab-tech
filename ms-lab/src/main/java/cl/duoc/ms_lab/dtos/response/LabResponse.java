package cl.duoc.ms_lab.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

public record LabResponse(
        Long id,
        String externalId, // UUID as string (lo mapeamos)
        String code,
        String name,
        String address,
        String phone,
        boolean active,
        List<String> supportedTests,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

