package cl.duoc.ms_lab.controladores;

import cl.duoc.ms_lab.config.PageResponse;
import cl.duoc.ms_lab.dtos.request.AssignOrderRequest;
import cl.duoc.ms_lab.dtos.request.OrderCreateRequest;
import cl.duoc.ms_lab.dtos.response.OrderResponse;
import cl.duoc.ms_lab.entidades.OrderStatus;
import cl.duoc.ms_lab.servicios.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders")
@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService service;

    public OrdersController(OrderService service) {
        this.service = service;
    }

    @Operation(summary = "Create order (ADMIN)")
    @PostMapping
    public OrderResponse create(@RequestBody OrderCreateRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Assign order to lab (ADMIN)")
    @PostMapping("/{id}/assign")
    public OrderResponse assign(@PathVariable Long id, @RequestBody AssignOrderRequest req) {
        return service.assign(id, req);
    }

    @Operation(summary = "Start order (ADMIN) → IN_PROGRESS")
    @PostMapping("/{id}/start")
    public OrderResponse start(@PathVariable Long id) {
        return service.advanceToInProgress(id);
    }

    @Operation(summary = "Finish order (ADMIN) → FINISHED")
    @PostMapping("/{id}/finish")
    public OrderResponse finish(@PathVariable Long id) {
        return service.finish(id);
    }

    @Operation(summary = "Get order by id (AUTH; LAB_TECH solo de su lab)")
    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "List orders (AUTH; LAB_TECH solo de su lab)")
    @GetMapping
    public PageResponse<OrderResponse> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String labCode,     // ignorado para LAB_TECH en service
            @RequestParam(required = false) String patientId,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return PageResponse.from(service.list(status, labCode, patientId, pageable));
    }
}

