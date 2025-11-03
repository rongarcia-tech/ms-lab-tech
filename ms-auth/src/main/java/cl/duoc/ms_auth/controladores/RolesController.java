package cl.duoc.ms_auth.controladores;

import cl.duoc.ms_auth.dtos.RoleResponse;
import cl.duoc.ms_auth.servicios.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los roles de usuario.
 * Proporciona endpoints para listar los roles disponibles en el sistema.
 */
@RestController
@RequestMapping("/roles")
public class RolesController {

    private final RoleService roleService;

    /**
     * Constructor para inyectar el servicio de roles.
     *
     * @param roleService El servicio que maneja la lógica de negocio para los roles.
     */
    public RolesController(RoleService roleService){ this.roleService = roleService; }

    /**
     * Endpoint para obtener una lista de todos los roles disponibles.
     *
     * @return Un {@link ResponseEntity} que contiene una lista de {@link RoleResponse}.
     */
    @GetMapping
    public ResponseEntity<List<RoleResponse>> list() {
        return ResponseEntity.ok(roleService.list());
    }
}
