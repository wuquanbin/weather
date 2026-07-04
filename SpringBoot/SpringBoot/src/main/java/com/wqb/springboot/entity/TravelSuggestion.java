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
@Table(name = "travel_suggestion")
public class TravelSuggestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false, length = 32)
    private String scenarioCode;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 255)
    private String summary;

    @Column(nullable = false, length = 500)
    private String recommendation;

    @Column(nullable = false, length = 32)
    private String priorityTag;

    @Column(nullable = false, length = 32)
    private String iconKey;

    @Column(nullable = false)
    private Integer priority;

    public TravelSuggestion() {
    }

    public TravelSuggestion(
            District district,
            String scenarioCode,
            String title,
            String summary,
            String recommendation,
            String priorityTag,
            String iconKey,
            Integer priority
    ) {
        this.district = district;
        this.scenarioCode = scenarioCode;
        this.title = title;
        this.summary = summary;
        this.recommendation = recommendation;
        this.priorityTag = priorityTag;
        this.iconKey = iconKey;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public District getDistrict() {
        return district;
    }

    public String getScenarioCode() {
        return scenarioCode;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getPriorityTag() {
        return priorityTag;
    }

    public String getIconKey() {
        return iconKey;
    }

    public Integer getPriority() {
        return priority;
    }
}
