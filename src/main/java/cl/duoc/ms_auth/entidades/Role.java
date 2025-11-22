package cl.duoc.ms_auth.entidades;


import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un rol de usuario en el sistema.
 * Define los permisos y el nivel de acceso que un usuario puede tener.
 */
@Entity
@Table(name = "ROLES",
        uniqueConstraints = @UniqueConstraint(name="UQ_ROLES_NAME", columnNames = "NAME"))
public class Role {
    /**
     * El identificador único para el rol.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    /**
     * El nombre del rol (por ejemplo, 'ADMIN', 'LAB_TECH'). Debe ser único.
     */
    @Column(name = "NAME", length = 50, nullable = false)
    private String name; // 'ADMIN' | 'LAB_TECH'

    /**
     * Una descripción de lo que implica el rol.
     */
    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    /**
     * La fecha y hora en que se creó el rol. Gestionado por la base de datos.
     */
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * La fecha y hora de la última actualización del rol. Gestionado por la base de datos.
     */
    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor con todos los campos para la entidad Role.
     *
     * @param id El ID del rol.
     * @param name El nombre del rol.
     * @param description La descripción del rol.
     * @param createdAt La fecha de creación.
     * @param updatedAt La fecha de última actualización.
     */
    public Role(Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Role() {
    }

    /**
     * Obtiene el ID del rol.
     * @return el ID del rol.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el ID del rol.
     * @param id el nuevo ID del rol.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del rol.
     * @return el nombre del rol.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del rol.
     * @param name el nuevo nombre del rol.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la descripción del rol.
     * @return la descripción del rol.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción del rol.
     * @param description la nueva descripción del rol.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtiene la fecha de creación del rol.
     * @return la fecha de creación.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha de creación del rol.
     * @param createdAt la nueva fecha de creación.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obtiene la fecha de la última actualización del rol.
     * @return la fecha de la última actualización.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Establece la fecha de la última actualización del rol.
     * @param updatedAt la nueva fecha de última actualización.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
