package cl.duoc.ms_lab.servicios;

import cl.duoc.ms_lab.dtos.request.LabCreateRequest;
import cl.duoc.ms_lab.dtos.request.LabUpdateRequest;
import cl.duoc.ms_lab.dtos.response.LabResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LaboratoryService {
    LabResponse create(LabCreateRequest req);
    LabResponse update(Long id, LabUpdateRequest req);
    LabResponse getById(Long id);
    Page<LabResponse> list(Boolean active, Pageable pageable);
    LabResponse activate(Long id);
    LabResponse deactivate(Long id);
}

