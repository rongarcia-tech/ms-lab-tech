package cl.duoc.ms_lab.repositorio;

import cl.duoc.ms_lab.entidades.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    Optional<Laboratory> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
