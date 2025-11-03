package cl.duoc.ms_lab.controladores;

import cl.duoc.ms_lab.dtos.request.LabCreateRequest;
import cl.duoc.ms_lab.dtos.request.LabUpdateRequest;
import cl.duoc.ms_lab.dtos.response.LabResponse;
import cl.duoc.ms_lab.servicios.LaboratoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Laboratories")
@RestController
@RequestMapping("/labs")
public class LabsController {

    private final LaboratoryService service;

    public LabsController(LaboratoryService service) {
        this.service = service;
    }

    @Operation(summary = "Create laboratory (ADMIN)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabResponse create(@Valid @RequestBody LabCreateRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Update laboratory (ADMIN)")
    @PutMapping("/{id}")
    public LabResponse update(@PathVariable Long id, @Valid @RequestBody LabUpdateRequest req) {
        return service.update(id, req);
    }

    @Operation(summary = "Get laboratory by id (AUTH)")
    @GetMapping("/{id}")
    public LabResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "List laboratories (AUTH)")
    @GetMapping
    public Page<LabResponse> list(
            @RequestParam(required = false) Boolean active,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return service.list(active, pageable);
    }

    // Opcional: activar/desactivar de forma explícita (ADMIN)
    @Operation(summary = "Activate laboratory (ADMIN)")
    @PostMapping("/{id}/activate")
    public LabResponse activate(@PathVariable Long id) {
        return service.activate(id);
    }

    @Operation(summary = "Deactivate laboratory (ADMIN)")
    @PostMapping("/{id}/deactivate")
    public LabResponse deactivate(@PathVariable Long id) {
        return service.deactivate(id);
    }
}
