package cl.duoc.ms_lab.dtos.response;

import cl.duoc.ms_lab.entidades.OrderStatus;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String externalId,
        String patientId,
        String requestedTest,
        OrderStatus status,
        LabMini lab,
        LocalDateTime assignedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record LabMini(Long id, String code, String name) {}
}
