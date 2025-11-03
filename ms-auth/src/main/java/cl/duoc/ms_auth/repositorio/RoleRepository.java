package cl.duoc.ms_auth.repositorio;

import cl.duoc.ms_auth.entidades.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // 'ADMIN', 'LAB_TECH'
    boolean existsByName(String name);
}
