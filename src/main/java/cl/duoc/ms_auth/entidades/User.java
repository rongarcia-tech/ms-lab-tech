package cl.duoc.ms_auth.entidades;


import cl.duoc.ms_auth.converter.UuidRaw16Converter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entidad JPA que representa a un usuario en el sistema.
 * Contiene la información de autenticación y los datos personales del usuario.
 */
@Entity
@Table(name = "USERS",
        uniqueConstraints = {
                @UniqueConstraint(name="UQ_USERS_USERNAME", columnNames = "USERNAME"),
                @UniqueConstraint(name="UQ_USERS_EMAIL",    columnNames = "EMAIL"),
                @UniqueConstraint(name="UQ_USERS_EXTID",    columnNames = "EXTERNAL_ID")
        })
public class User {
    /**
     * El identificador único interno para el usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    /**
     * El identificador único externo (UUID) para el usuario, almacenado como RAW(16).
     */
    @Convert(converter = UuidRaw16Converter.class)
    @Column(name = "EXTERNAL_ID", columnDefinition = "RAW(16)", nullable = false)
    private UUID externalId;

    /**
     * El nombre de usuario, debe ser único.
     */
    @Column(name = "USERNAME", length = 150, nullable = false)
    private String username;

    /**
     * La dirección de correo electrónico del usuario, debe ser única.
     */
    @Column(name = "EMAIL", length = 200, nullable = false)
    private String email;

    /**
     * El hash de la contraseña del usuario.
     */
    @Column(name = "PASSWORD_HASH", length = 255, nullable = false)
    private String passwordHash;

    /**
     * El código del laboratorio asociado al usuario. Requerido solo para ciertos roles.
     */
    // LAB_CODE requerido solo para LAB_TECH (validación a nivel de servicio)
    @Column(name = "LAB_CODE", length = 50)
    private String labCode;

    /**
     * El estado del usuario ('Y' para activo, 'N' para inactivo).
     */
    // DB guarda 'Y'/'N'. Mantengo exacto al esquema para evitar mapeos raros.
    @Column(name = "ACTIVE", length = 1, nullable = false)
    private String active = "Y";

    /**
     * La fecha y hora en que se creó el usuario. Gestionado por la base de datos.
     */
    // Timestamps gestionados por la BD (DEFAULT + TRIGGER).
    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * La fecha y hora de la última actualización del usuario. Gestionado por la base de datos.
     */
    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    /**
     * El conjunto de roles asignados al usuario.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Método de ciclo de vida de JPA que se ejecuta antes de que la entidad sea persistida.
     * Asigna un UUID si no existe y establece el estado activo por defecto.
     */
    @PrePersist
    public void prePersist() {
        if (externalId == null) externalId = UUID.randomUUID(); // espejo lógico de SYS_GUID()
        if (active == null) active = "Y";
    }

    /**
     * Constructor con todos los campos para la entidad User.
     */
    public User(Long id, UUID externalId, String username, String email, String passwordHash, String labCode, String active, LocalDateTime createdAt, LocalDateTime updatedAt, Set<Role> roles) {
        this.id = id;
        this.externalId = externalId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.labCode = labCode;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
    }

    /**
     * Constructor por defecto requerido por JPA.
     */
    public User() {
    }

    //<editor-fold desc="Getters y Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public void setExternalId(UUID externalId) {
        this.externalId = externalId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
