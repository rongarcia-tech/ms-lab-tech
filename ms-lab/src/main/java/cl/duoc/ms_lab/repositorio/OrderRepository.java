package cl.duoc.ms_lab.repositorio;

import cl.duoc.ms_lab.entidades.Order;
import cl.duoc.ms_lab.entidades.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByLaboratory_Id(Long labId, Pageable p);
    Page<Order> findByStatusAndLaboratory_Id(OrderStatus status, Long labId, Pageable p);
    Page<Order> findByStatus(OrderStatus status, Pageable p);
    Page<Order> findByPatientId(String patientId, Pageable p);
}
