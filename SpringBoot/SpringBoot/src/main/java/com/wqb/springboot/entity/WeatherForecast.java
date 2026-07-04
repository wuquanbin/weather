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
import java.time.LocalDate;

@Entity
@Table(name = "weather_forecast")
public class WeatherForecast extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false)
    private LocalDate forecastDate;

    @Column(nullable = false, length = 16)
    private String weekLabel;

    @Column(nullable = false, length = 32)
    private String weatherType;

    @Column(nullable = false)
    private BigDecimal lowTemperature;

    @Column(nullable = false)
    private BigDecimal highTemperature;

    @Column(nullable = false)
    private Integer precipitationProbability;

    @Column(nullable = false, length = 32)
    private String windDirection;

    @Column(nullable = false, length = 32)
    private String windScale;

    @Column(nullable = false, length = 255)
    private String travelAdvice;

    public WeatherForecast() {
    }

    public WeatherForecast(
            District district,
            LocalDate forecastDate,
            String weekLabel,
            String weatherType,
            BigDecimal lowTemperature,
            BigDecimal highTemperature,
            Integer precipitationProbability,
            String windDirection,
            String windScale,
            String travelAdvice
    ) {
        this.district = district;
        this.forecastDate = forecastDate;
        this.weekLabel = weekLabel;
        this.weatherType = weatherType;
        this.lowTemperature = lowTemperature;
        this.highTemperature = highTemperature;
        this.precipitationProbability = precipitationProbability;
        this.windDirection = windDirection;
        this.windScale = windScale;
        this.travelAdvice = travelAdvice;
    }

    public Long getId() {
        return id;
    }

    public District getDistrict() {
        return district;
    }

    public LocalDate getForecastDate() {
        return forecastDate;
    }

    public String getWeekLabel() {
        return weekLabel;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public BigDecimal getLowTemperature() {
        return lowTemperature;
    }

    public BigDecimal getHighTemperature() {
        return highTemperature;
    }

    public Integer getPrecipitationProbability() {
        return precipitationProbability;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindScale() {
        return windScale;
    }

    public String getTravelAdvice() {
        return travelAdvice;
    }
}
