package cl.duoc.ms_lab.servicios.impl;

import cl.duoc.ms_lab.dtos.request.LabCreateRequest;
import cl.duoc.ms_lab.dtos.request.LabUpdateRequest;
import cl.duoc.ms_lab.dtos.response.LabResponse;
import cl.duoc.ms_lab.entidades.Laboratory;
import cl.duoc.ms_lab.exceptions.ConflictException;
import cl.duoc.ms_lab.exceptions.NotFoundException;
import cl.duoc.ms_lab.mappers.LabOrderMapper;
import cl.duoc.ms_lab.repositorio.LaboratoryRepository;
import cl.duoc.ms_lab.servicios.LaboratoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LaboratoryServiceImpl implements LaboratoryService {

    private final LaboratoryRepository repo;

    public LaboratoryServiceImpl(LaboratoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public LabResponse create(LabCreateRequest req) {
        if (repo.existsByCode(req.code()))
            throw new ConflictException("Laboratory code already exists: " + req.code());
        // validar name unique (opcional, ya hay unique constraint)
        Laboratory e = LabOrderMapper.toEntity(req);
        e = repo.save(e);
        return LabOrderMapper.toResponse(e);
    }

    @Override
    public LabResponse update(Long id, LabUpdateRequest req) {
        Laboratory e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Laboratory not found id=" + id));

        // Unicidad CODE si cambia
        if (req.code() != null && !req.code().equals(e.getCode()) && repo.existsByCode(req.code())) {
            throw new ConflictException("Laboratory code already exists: " + req.code());
        }

        LabOrderMapper.applyUpdate(e, req);
        e = repo.save(e);
        return LabOrderMapper.toResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public LabResponse getById(Long id) {
        return repo.findById(id)
                .map(LabOrderMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Laboratory not found id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabResponse> list(Boolean active, Pageable pageable) {
        // Para mantener simple: si quieres filtrar por ACTIVE, crea un método en repo.
        Page<Laboratory> page = repo.findAll(pageable);
        return page.map(LabOrderMapper::toResponse);
    }

    @Override
    public LabResponse activate(Long id) {
        Laboratory e = repo.findById(id).orElseThrow(() -> new NotFoundException("Laboratory not found id=" + id));
        e.setActive("Y");
        return LabOrderMapper.toResponse(repo.save(e));
    }

    @Override
    public LabResponse deactivate(Long id) {
        Laboratory e = repo.findById(id).orElseThrow(() -> new NotFoundException("Laboratory not found id=" + id));
        e.setActive("N");
        return LabOrderMapper.toResponse(repo.save(e));
    }
}
