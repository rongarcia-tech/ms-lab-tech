package cl.duoc.ms_lab.servicios;

import cl.duoc.ms_lab.dtos.request.AssignOrderRequest;
import cl.duoc.ms_lab.dtos.request.OrderCreateRequest;
import cl.duoc.ms_lab.dtos.response.OrderResponse;
import cl.duoc.ms_lab.entidades.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse create(OrderCreateRequest req);
    OrderResponse assign(Long orderId, AssignOrderRequest req);
    OrderResponse advanceToInProgress(Long orderId);
    OrderResponse finish(Long orderId);
    OrderResponse getById(Long id);
    Page<OrderResponse> list(OrderStatus status, String labCode, String patientId, Pageable pageable);
}
