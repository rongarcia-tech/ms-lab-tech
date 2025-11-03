package cl.duoc.ms_lab.entidades;

import cl.duoc.ms_lab.converter.UuidRaw16Converter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ORDERS",
        indexes = {
                @Index(name = "IDX_ORDERS_LAB_ID",  columnList = "LAB_ID"),
                @Index(name = "IDX_ORDERS_STATUS",  columnList = "STATUS"),
                @Index(name = "IDX_ORDERS_PATIENT", columnList = "PATIENT_ID")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_ORDERS_EXTID", columnNames = "EXTERNAL_ID")
        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // idem: lo deja Oracle (SYS_GUID); JPA solo lo lee
    @Column(name = "EXTERNAL_ID", columnDefinition = "RAW(16)", nullable = false, updatable = false, insertable = false)
    @Convert(converter = UuidRaw16Converter.class)
    private UUID externalId;

    @Column(name = "PATIENT_ID", length = 100, nullable = false)
    private String patientId;

    @Column(name = "REQUESTED_TEST", length = 100, nullable = false)
    private String requestedTest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAB_ID", foreignKey = @ForeignKey(name = "FK_ORDERS_LAB"))
    private Laboratory laboratory; // puede ser null hasta asignación

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "ASSIGNED_AT")
    private LocalDateTime assignedAt;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Order() {
    }

    public Order(Long id, UUID externalId, String patientId, String requestedTest, Laboratory laboratory, OrderStatus status, LocalDateTime assignedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.patientId = patientId;
        this.requestedTest = requestedTest;
        this.laboratory = laboratory;
        this.status = status;
        this.assignedAt = assignedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRequestedTest() {
        return requestedTest;
    }

    public void setRequestedTest(String requestedTest) {
        this.requestedTest = requestedTest;
    }

    public Laboratory getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
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
}
