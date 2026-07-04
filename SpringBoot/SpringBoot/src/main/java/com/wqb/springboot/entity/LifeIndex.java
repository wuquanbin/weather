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

import java.time.LocalDate;

@Entity
@Table(name = "life_index")
public class LifeIndex extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false)
    private LocalDate indexDate;

    @Column(nullable = false, length = 32)
    private String indexType;

    @Column(nullable = false, length = 32)
    private String level;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(length = 255)
    private String advice;

    public LifeIndex() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }
    public LocalDate getIndexDate() { return indexDate; }
    public void setIndexDate(LocalDate indexDate) { this.indexDate = indexDate; }
    public String getIndexType() { return indexType; }
    public void setIndexType(String indexType) { this.indexType = indexType; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
}
