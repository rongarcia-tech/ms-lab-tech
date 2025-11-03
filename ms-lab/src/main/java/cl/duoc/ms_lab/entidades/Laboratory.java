package cl.duoc.ms_lab.entidades;

import cl.duoc.ms_lab.converter.StringListJsonConverter;
import cl.duoc.ms_lab.converter.UuidRaw16Converter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "LABORATORIES",
        indexes = {
                @Index(name = "IDX_LABS_ACTIVE", columnList = "ACTIVE"),
                @Index(name = "IDX_LABS_CODE",   columnList = "CODE")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_LABS_CODE",  columnNames = "CODE"),
                @UniqueConstraint(name = "UQ_LABS_NAME",  columnNames = "NAME"),
                @UniqueConstraint(name = "UQ_LABS_EXTID", columnNames = "EXTERNAL_ID")
        }
)
public class Laboratory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // RAW(16) con SYS_GUID por default en la tabla. Dejo insertable/updatable false si confías 100% en BD.
    @Column(name = "EXTERNAL_ID", columnDefinition = "RAW(16)", nullable = false, updatable = false, insertable = false)
    @Convert(converter = UuidRaw16Converter.class)
    private UUID externalId;

    @Column(name = "CODE", length = 50)
    private String code;

    @Column(name = "NAME", length = 150, nullable = false)
    private String name;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "PHONE", length = 30)
    private String phone;

    @Column(name = "ACTIVE", length = 1, nullable = false)
    private String active = "Y"; // 'Y'/'N'

    @Lob
    @Column(name = "SUPPORTED_TESTS", columnDefinition = "CLOB CHECK (SUPPORTED_TESTS IS JSON)")
    @Convert(converter = StringListJsonConverter.class)
    private List<String> supportedTests;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Laboratory() {
    }

    public Laboratory(Long id, UUID externalId, String code, String name, String address, String phone, String active, List<String> supportedTests, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.active = active;
        this.supportedTests = supportedTests;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public List<String> getSupportedTests() {
        return supportedTests;
    }

    public void setSupportedTests(List<String> supportedTests) {
        this.supportedTests = supportedTests;
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

