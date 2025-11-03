package cl.duoc.ms_lab.mappers;

import cl.duoc.ms_lab.dtos.request.LabCreateRequest;
import cl.duoc.ms_lab.dtos.request.LabUpdateRequest;
import cl.duoc.ms_lab.dtos.response.LabResponse;
import cl.duoc.ms_lab.dtos.response.OrderResponse;
import cl.duoc.ms_lab.entidades.Laboratory;
import cl.duoc.ms_lab.entidades.Order;

import java.util.Optional;
import java.util.UUID;

public class LabOrderMapper {

    // --- Laboratory ---

    public static Laboratory toEntity(LabCreateRequest r) {
        var e = new Laboratory();
        e.setCode(r.code());
        e.setName(r.name());
        e.setAddress(r.address());
        e.setPhone(r.phone());
        e.setSupportedTests(r.supportedTests());
        e.setActive("Y");
        return e;
    }

    public static void applyUpdate(Laboratory e, LabUpdateRequest r) {
        if (r.code() != null) e.setCode(r.code());
        if (r.name() != null) e.setName(r.name());
        if (r.address() != null) e.setAddress(r.address());
        if (r.phone() != null) e.setPhone(r.phone());
        if (r.supportedTests() != null) e.setSupportedTests(r.supportedTests());
        if (r.active() != null) e.setActive(r.active() ? "Y" : "N");
    }

    public static LabResponse toResponse(Laboratory e) {
        return new LabResponse(
                e.getId(),
                uuidToString(e.getExternalId()),
                e.getCode(),
                e.getName(),
                e.getAddress(),
                e.getPhone(),
                "Y".equalsIgnoreCase(e.getActive()),
                e.getSupportedTests(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    // --- Order ---

    public static OrderResponse toResponse(Order o) {
        var lab = Optional.ofNullable(o.getLaboratory())
                .map(l -> new OrderResponse.LabMini(l.getId(), l.getCode(), l.getName()))
                .orElse(null);

        return new OrderResponse(
                o.getId(),
                uuidToString(o.getExternalId()),
                o.getPatientId(),
                o.getRequestedTest(),
                o.getStatus(),
                lab,
                o.getAssignedAt(),
                o.getCreatedAt(),
                o.getUpdatedAt()
        );
    }

    private static String uuidToString(UUID u) {
        return u != null ? u.toString() : null;
    }
}

