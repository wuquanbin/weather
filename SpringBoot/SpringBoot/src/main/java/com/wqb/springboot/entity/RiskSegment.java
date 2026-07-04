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

@Entity
@Table(name = "risk_segment")
public class RiskSegment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 64)
    private String location;

    @Column(nullable = false, length = 32)
    private String riskType;

    @Column(nullable = false, length = 128)
    private String triggerWeatherTags;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 255)
    private String advice;

    @Column(nullable = false)
    private Integer priority;

    public RiskSegment() {
    }

    public RiskSegment(
            District district,
            String name,
            String location,
            String riskType,
            String triggerWeatherTags,
            String description,
            String advice,
            Integer priority
    ) {
        this.district = district;
        this.name = name;
        this.location = location;
        this.riskType = riskType;
        this.triggerWeatherTags = triggerWeatherTags;
        this.description = description;
        this.advice = advice;
        this.priority = priority;
    }

    public Long getId() { return id; }
    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getRiskType() { return riskType; }
    public void setRiskType(String riskType) { this.riskType = riskType; }
    public String getTriggerWeatherTags() { return triggerWeatherTags; }
    public void setTriggerWeatherTags(String triggerWeatherTags) { this.triggerWeatherTags = triggerWeatherTags; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}
