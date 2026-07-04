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
@Table(name = "travel_place")
public class TravelPlace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 32)
    private String category;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 64)
    private String location;

    @Column(nullable = false)
    private Boolean indoor;

    @Column(nullable = false, length = 128)
    private String weatherTags;

    @Column(nullable = false, length = 128)
    private String sceneTags;

    @Column(nullable = false)
    private Integer recommendLevel;

    @Column(nullable = false, length = 255)
    private String highlight;

    public TravelPlace() {
    }

    public TravelPlace(
            District district,
            String name,
            String category,
            String address,
            String location,
            Boolean indoor,
            String weatherTags,
            String sceneTags,
            Integer recommendLevel,
            String highlight
    ) {
        this.district = district;
        this.name = name;
        this.category = category;
        this.address = address;
        this.location = location;
        this.indoor = indoor;
        this.weatherTags = weatherTags;
        this.sceneTags = sceneTags;
        this.recommendLevel = recommendLevel;
        this.highlight = highlight;
    }

    public Long getId() {
        return id;
    }

    public District getDistrict() {
        return district;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public Boolean getIndoor() {
        return indoor;
    }

    public String getWeatherTags() {
        return weatherTags;
    }

    public String getSceneTags() {
        return sceneTags;
    }

    public Integer getRecommendLevel() {
        return recommendLevel;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setDistrict(District district) { this.district = district; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setAddress(String address) { this.address = address; }
    public void setLocation(String location) { this.location = location; }
    public void setIndoor(Boolean indoor) { this.indoor = indoor; }
    public void setWeatherTags(String weatherTags) { this.weatherTags = weatherTags; }
    public void setSceneTags(String sceneTags) { this.sceneTags = sceneTags; }
    public void setRecommendLevel(Integer recommendLevel) { this.recommendLevel = recommendLevel; }
    public void setHighlight(String highlight) { this.highlight = highlight; }
}
