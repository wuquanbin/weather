package com.wqb.springboot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "warning_notice")
public class WarningNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @Column(nullable = false, length = 32)
    private String warningType;

    @Column(nullable = false, length = 16)
    private String severity;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(length = 255)
    private String impactArea;

    @Column(columnDefinition = "TEXT")
    private String defenseGuidance;

    public WarningNotice() {
    }

    public WarningNotice(
            District district,
            String warningType,
            String severity,
            String title,
            String content,
            LocalDateTime issuedAt,
            LocalDateTime expiresAt,
            String status
    ) {
        this.district = district;
        this.warningType = warningType;
        this.severity = severity;
        this.title = title;
        this.content = content;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public WarningNotice(
            District district,
            String warningType,
            String severity,
            String title,
            String content,
            LocalDateTime issuedAt,
            LocalDateTime expiresAt,
            String status,
            String impactArea,
            String defenseGuidance
    ) {
        this.district = district;
        this.warningType = warningType;
        this.severity = severity;
        this.title = title;
        this.content = content;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.impactArea = impactArea;
        this.defenseGuidance = defenseGuidance;
    }

    public Long getId() { return id; }
    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }
    public String getWarningType() { return warningType; }
    public void setWarningType(String warningType) { this.warningType = warningType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImpactArea() { return impactArea; }
    public void setImpactArea(String impactArea) { this.impactArea = impactArea; }
    public String getDefenseGuidance() { return defenseGuidance; }
    public void setDefenseGuidance(String defenseGuidance) { this.defenseGuidance = defenseGuidance; }
}
