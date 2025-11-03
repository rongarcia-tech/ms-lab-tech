package cl.duoc.ms_lab.servicios.impl;

import cl.duoc.ms_lab.dtos.request.AssignOrderRequest;
import cl.duoc.ms_lab.dtos.request.OrderCreateRequest;
import cl.duoc.ms_lab.dtos.response.OrderResponse;
import cl.duoc.ms_lab.entidades.Laboratory;
import cl.duoc.ms_lab.entidades.Order;
import cl.duoc.ms_lab.entidades.OrderStatus;
import cl.duoc.ms_lab.exceptions.BadRequestException;
import cl.duoc.ms_lab.exceptions.ForbiddenException;
import cl.duoc.ms_lab.exceptions.NotFoundException;
import cl.duoc.ms_lab.mappers.LabOrderMapper;
import cl.duoc.ms_lab.repositorio.LaboratoryRepository;
import cl.duoc.ms_lab.repositorio.OrderRepository;
import cl.duoc.ms_lab.security.SecurityUtils;
import cl.duoc.ms_lab.servicios.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final LaboratoryRepository labRepo;

    public OrderServiceImpl(OrderRepository orderRepo, LaboratoryRepository labRepo) {
        this.orderRepo = orderRepo;
        this.labRepo = labRepo;
    }

    @Override
    public OrderResponse create(OrderCreateRequest req) {
        Order o = new Order();
        o.setPatientId(req.patientId());
        o.setRequestedTest(req.requestedTest());
        o.setStatus(OrderStatus.CREATED);

        if (req.labCode() != null && !req.labCode().isBlank()) {
            Laboratory lab = labRepo.findByCode(req.labCode())
                    .orElseThrow(() -> new NotFoundException("Lab not found code=" + req.labCode()));
            o.setLaboratory(lab);
            o.setStatus(OrderStatus.ASSIGNED);
            o.setAssignedAt(LocalDateTime.now());
        }

        o = orderRepo.save(o);
        return LabOrderMapper.toResponse(o);
    }

    @Override
    public OrderResponse assign(Long orderId, AssignOrderRequest req) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found id=" + orderId));
        System.out.println("PASO1");
        // Solo ADMIN llega aquí por regla del SecurityConfig,
        // pero de todos modos podríamos validar con SecurityUtils.hasRole("ADMIN") si quieres.

        Laboratory lab = labRepo.findByCode(req.labCode())
                .orElseThrow(() -> new NotFoundException("Lab not found code=" + req.labCode()));
        System.out.println("PASO2");
        if (o.getStatus() == OrderStatus.FINISHED) {
            throw new BadRequestException("Cannot assign a FINISHED order");
        }

        o.setLaboratory(lab);
        o.setStatus(OrderStatus.ASSIGNED);
        o.setAssignedAt(LocalDateTime.now());

        o = orderRepo.save(o);
        return LabOrderMapper.toResponse(o);
    }

    @Override
    public OrderResponse advanceToInProgress(Long orderId) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found id=" + orderId));

        if (o.getStatus() != OrderStatus.ASSIGNED) {
            throw new BadRequestException("Only ASSIGNED orders can move to IN_PROGRESS");
        }
        o.setStatus(OrderStatus.IN_PROGRESS);
        o = orderRepo.save(o);
        return LabOrderMapper.toResponse(o);
    }

    @Override
    public OrderResponse finish(Long orderId) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found id=" + orderId));

        if (o.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new BadRequestException("Only IN_PROGRESS orders can move to FINISHED");
        }
        o.setStatus(OrderStatus.FINISHED);
        o = orderRepo.save(o);
        return LabOrderMapper.toResponse(o);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order o = orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found id=" + id));

        // Si es LAB_TECH, debe pertenecer a su labCode
        if (!SecurityUtils.hasRole("ADMIN")) {
            var ju = SecurityUtils.currentUserOrNull();
            String labCodeFromToken = ju != null ? ju.labCode() : null;
            String orderLabCode = (o.getLaboratory() != null) ? o.getLaboratory().getCode() : null;

            if (labCodeFromToken == null || orderLabCode == null || !labCodeFromToken.equals(orderLabCode)) {
                throw new ForbiddenException("Order does not belong to your lab");
            }
        }

        return LabOrderMapper.toResponse(o);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> list(OrderStatus status, String labCode, String patientId, Pageable pageable) {
        // ADMIN: puede ver todo (aplica filtros si vienen)
        if (SecurityUtils.hasRole("ADMIN")) {
            if (patientId != null && !patientId.isBlank()) {
                return orderRepo.findByPatientId(patientId, pageable).map(LabOrderMapper::toResponse);
            }
            if (status != null && labCode != null && !labCode.isBlank()) {
                Long labId = labRepo.findByCode(labCode)
                        .map(Laboratory::getId)
                        .orElse(-1L);
                return orderRepo.findByStatusAndLaboratory_Id(status, labId, pageable).map(LabOrderMapper::toResponse);
            }
            if (status != null) {
                return orderRepo.findByStatus(status, pageable).map(LabOrderMapper::toResponse);
            }
            if (labCode != null && !labCode.isBlank()) {
                Long labId = labRepo.findByCode(labCode)
                        .map(Laboratory::getId)
                        .orElse(-1L);
                return orderRepo.findByLaboratory_Id(labId, pageable).map(LabOrderMapper::toResponse);
            }
            return orderRepo.findAll(pageable).map(LabOrderMapper::toResponse);
        }

        // LAB_TECH: ignora labCode del request; fuerza el del token
        var ju = SecurityUtils.currentUserOrNull();
        String labCodeFromToken = ju != null ? ju.labCode() : null;
        if (labCodeFromToken == null || labCodeFromToken.isBlank())
            throw new ForbiddenException("Missing labCode in token");

        Long labId = labRepo.findByCode(labCodeFromToken)
                .map(Laboratory::getId)
                .orElse(-1L);

        if (patientId != null && !patientId.isBlank()) {
            // regla: solo de su lab; si quisieras combinar lab + patient, crea query compuesta
            return orderRepo.findByLaboratory_Id(labId, pageable).map(LabOrderMapper::toResponse);
        }
        if (status != null) {
            return orderRepo.findByStatusAndLaboratory_Id(status, labId, pageable).map(LabOrderMapper::toResponse);
        }
        return orderRepo.findByLaboratory_Id(labId, pageable).map(LabOrderMapper::toResponse);
    }
}

