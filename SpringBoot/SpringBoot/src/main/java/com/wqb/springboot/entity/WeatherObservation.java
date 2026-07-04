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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_observation")
public class WeatherObservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false)
    private LocalDateTime observationTime;

    @Column(nullable = false, length = 32)
    private String weatherType;

    @Column(nullable = false)
    private BigDecimal temperature;

    @Column(nullable = false)
    private BigDecimal apparentTemperature;

    @Column(nullable = false)
    private Integer humidity;

    @Column(nullable = false, length = 32)
    private String windDirection;

    @Column(nullable = false, length = 32)
    private String windScale;

    @Column(nullable = false, length = 32)
    private String airQuality;

    @Column(nullable = false)
    private Integer precipitationProbability;

    @Column(nullable = false, length = 32)
    private String comfortLevel;

    @Column(nullable = false, length = 32)
    private String uvLevel;

    @Column(nullable = false, length = 32)
    private String travelIndex;

    @Column()
    private BigDecimal pressure;

    @Column()
    private BigDecimal visibility;

    public WeatherObservation() {
    }

    public WeatherObservation(
            District district,
            LocalDateTime observationTime,
            String weatherType,
            BigDecimal temperature,
            BigDecimal apparentTemperature,
            Integer humidity,
            String windDirection,
            String windScale,
            String airQuality,
            Integer precipitationProbability,
            String comfortLevel,
            String uvLevel,
            String travelIndex
    ) {
        this.district = district;
        this.observationTime = observationTime;
        this.weatherType = weatherType;
        this.temperature = temperature;
        this.apparentTemperature = apparentTemperature;
        this.humidity = humidity;
        this.windDirection = windDirection;
        this.windScale = windScale;
        this.airQuality = airQuality;
        this.precipitationProbability = precipitationProbability;
        this.comfortLevel = comfortLevel;
        this.uvLevel = uvLevel;
        this.travelIndex = travelIndex;
    }

    public Long getId() { return id; }
    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }
    public LocalDateTime getObservationTime() { return observationTime; }
    public void setObservationTime(LocalDateTime observationTime) { this.observationTime = observationTime; }
    public String getWeatherType() { return weatherType; }
    public void setWeatherType(String weatherType) { this.weatherType = weatherType; }
    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    public BigDecimal getApparentTemperature() { return apparentTemperature; }
    public void setApparentTemperature(BigDecimal apparentTemperature) { this.apparentTemperature = apparentTemperature; }
    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }
    public String getWindScale() { return windScale; }
    public void setWindScale(String windScale) { this.windScale = windScale; }
    public String getAirQuality() { return airQuality; }
    public void setAirQuality(String airQuality) { this.airQuality = airQuality; }
    public Integer getPrecipitationProbability() { return precipitationProbability; }
    public void setPrecipitationProbability(Integer precipitationProbability) { this.precipitationProbability = precipitationProbability; }
    public String getComfortLevel() { return comfortLevel; }
    public void setComfortLevel(String comfortLevel) { this.comfortLevel = comfortLevel; }
    public String getUvLevel() { return uvLevel; }
    public void setUvLevel(String uvLevel) { this.uvLevel = uvLevel; }
    public String getTravelIndex() { return travelIndex; }
    public void setTravelIndex(String travelIndex) { this.travelIndex = travelIndex; }
    public BigDecimal getPressure() { return pressure; }
    public void setPressure(BigDecimal pressure) { this.pressure = pressure; }
    public BigDecimal getVisibility() { return visibility; }
    public void setVisibility(BigDecimal visibility) { this.visibility = visibility; }
}
